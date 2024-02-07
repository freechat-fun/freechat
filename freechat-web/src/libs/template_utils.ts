/* eslint-disable @typescript-eslint/no-explicit-any */
import Mustache from 'mustache';
import { ChatContentDTO, ChatMessageDTO, ChatPromptContentDTO, LlmResultDTO, PromptAiParamDTO, PromptCreateDTO, PromptDetailsDTO } from 'freechat-sdk';
import { i18nConfig } from "../configs/i18n-config";

export function extractMustacheTemplateVariableNames(templateContents: string[]): string[] {
  const variables: string[] = [];

  // const contextHandler = {
  //   get: function(_target: any, prop: string) {
  //     variables.push(prop);
  //     return '';
  //   }
  // };

  // templateContents.forEach(templateContent => 
  //   Mustache.render(templateContent, new Proxy({}, contextHandler)));

  templateContents.forEach(templateContent => variables.push(
    ...Mustache.parse(templateContent)
      .filter(function(v) { return v[0] === 'name' || v[0] === '#' || v[0] === '&' })
      .map(function(v) { return v[1]; })
    ));

  return [...new Set(variables)];
}

export function extractFStringTemplateVariableNames(templateContents: string[]): string[] {
  const VAR_PATTERN: RegExp = /{(.*?)}(?!})/g;
  const variables: string[] = [];
  let match: RegExpExecArray | null;

  templateContents.forEach(templateContent => {
    while ((match = VAR_PATTERN.exec(templateContent)) !== null) {
      variables.push(match[1].trim());
    }
  });
  
  return [...new Set(variables)];
}

export function extractVariables(record: PromptDetailsDTO | undefined): { [key: string]: any } | undefined {
  const templateContent = getTemplateContent(record);
  if (!templateContent) {
    return undefined;
  }

  const defaultInputs = record?.inputs ? extractJson(record.inputs) : undefined;

  try {
    const variableNames = record?.format === 'f_string' ?
      extractFStringTemplateVariableNames(templateContent) :
      extractMustacheTemplateVariableNames(templateContent);

    return initVariables(variableNames, defaultInputs);
  } catch (error) {
    return defaultInputs;
  }
}

export function getTemplateContent(record: PromptDetailsDTO | undefined): string[] {
  const templates: string[] = [];

  if (record?.type === 'chat' && record?.chatTemplate) {
    const chatTemplate = record?.chatTemplate;
    templates.push(chatTemplate.system ?? '');

    const messages = chatTemplate.messages ?? [];
    messages.forEach(message => 
      templates.push(`${getMessageText(message)}`));
    
    templates.push(`${getMessageText(chatTemplate.messageToSend) ?? (record?.format === 'f_string' ? '{input}' : '{{input}}')}`);
  } else {
    templates.push(record?.template || '');
  }

  return templates;
}

export function initVariables(names: string[], defaultInputs: { [key: string]: any } | undefined): { [key: string]: any } {
  const result: { [key: string]: any } = {};
  names.forEach(name => result[name] = defaultInputs?.[name] || '');
  return result;
}

export function extractJson(jsonString: string | undefined): { [key: string]: any } | undefined {
  if (!jsonString) {
    return undefined;
  }
  const jsonObject = JSON.parse(jsonString);
  const result: { [key: string]: any } = {};

  Object.entries(jsonObject).forEach(([k, v]) => {
    if (v === null) {
      result[k] = undefined;
    } else if (typeof v === 'string') {
      result[k] = v;
    } else {
      result[k] = JSON.stringify(v);
    }
  });

  return result;
}

export function extractModelProvider(modelId: string | undefined): string | undefined {
  if (!modelId) {
    return undefined;
  }
  const regex = /\[([^\]]+)\]/;
  const match = modelId.match(regex);

  return match?.[1];
}

const EXAMPLE_TEMPLATE = `### Variable Settings
{{{variables}}}

<br>
### Model Info
{{model}}

<br>
### Model Parameters:
{{{parameters}}}

<br>
### Input
{{{prompt}}}

<br>
### Output
{{{output}}}
`;

function objectToMarkdownTable(obj: { [key: string]: any } | undefined, keyColumn: string, valueColumn: string = 'Value'): string {
  if (!obj) {
    return '';
  }

  try {
      let markdownTable = `| **${keyColumn}** | **${valueColumn}** |\n|----|----|\n`;

      for (const key in obj) {
        if (Object.prototype.hasOwnProperty.call(obj, key)) {
          const v = obj[key];
          let literal: string;
          if (v === undefined || v === null) {
            continue;
          } else if (typeof v === 'string') {
            literal = v;
          } else {
            literal = JSON.stringify(v);
          }

          markdownTable += `| ${key} | ${literal} |\n`;
        }
      }

      return markdownTable;
  } catch (error) {
      console.error('Invalid parameters:', error);
      return '';
  }
}

function chatTemplateToMarkdownContent(chatTemplate: ChatPromptContentDTO | undefined, format: string | undefined): string {
  if (!chatTemplate) {
    return '';
  }

  let markdownContent = '**[SYSTEM]**<br>';

  markdownContent += `${chatTemplate.system ?? ''}<br><br>`;

  if (chatTemplate.messages && chatTemplate.messages.length > 0) {
    markdownContent += '**[MESSAGES]**<br>';
    chatTemplate.messages.forEach(message => {
      markdownContent += `**${message.role?.toUpperCase()}: **${getMessageText(message)}<br>`;
    });
    markdownContent += '<br>';
  }

  markdownContent += '**[USER]**<br>';
  markdownContent += `${getMessageText(chatTemplate.messageToSend) ?? (format === 'f_string' ? '{input}' : '{{input}}')}`;

  return markdownContent;
}

export function generateExample(request: PromptAiParamDTO | undefined, response: LlmResultDTO | undefined): string | undefined {
  if (!request || !response) {
    return undefined;
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { modelId, apiKeyName, apiKey, ...modelParameters } = request.params;

  const variables = objectToMarkdownTable(request.promptTemplate?.variables ?? request.promptRef?.variables, 'Placeholder');
  const prompt = request.prompt ?? chatTemplateToMarkdownContent(request.promptTemplate?.chatTemplate, request.promptTemplate?.format);
  const model = modelId as string;
  const parameters =  objectToMarkdownTable(modelParameters, 'Parameters');
  const output = getMessageText(response.message) ?? response.text;

  const markdownContext = {
    variables: variables,
    prompt: prompt,
    model: model,
    parameters: parameters,
    output: output,
  };

  return Mustache.render(EXAMPLE_TEMPLATE, markdownContext);
}

export function getMessageText(message: ChatMessageDTO | undefined): string | undefined {
  return message?.contents
    ?.filter(content => content.type === 'text')
    .map(content => content.content)
    .join('\n');
}

export function setMessageText(message: ChatMessageDTO | undefined, text: string | undefined): void {
  if (!message) {
    return;
  }

  const content = new ChatContentDTO();
  content.type = 'text';
  content.content = text ?? '';

  (message.contents ??= [])[0] = content;
}

const CHARACTER_PROMPT_DESCRIPTION_EN = `
Prompt for charater {{characterName}}.



<br>
**Predefined Variables**

{{{variables}}}
`;

const CHARACTER_PROMPT_DESCRIPTION_ZH = `
角色 {{characterName}} 的提示词。



<br>
**预置变量**

{{{variables}}}
`;

const CHARACTER_PROMPT_TEMPLATE_EN = `
You play a good conversationalist.
Imitate conversations between people, use 1 to 3 sentences to complete feedback, and try to avoid lengthy responses.
You should NEVER answer in the tone of an AI assistant!
By default, you speak in {{CHARACTER_LANG}}. Unless the person you are speaking to speaks a different language, in which case you reply in the same language as the other person.

[[[About you]]]
Your name: {{CHARACTER_NICKNAME}}.
{{#CHARACTER_GENDER}}
Your gender: {{CHARACTER_GENDER}}.
{{/CHARACTER_GENDER}}
Your chat style: {{{CHARACTER_CHAT_STYLE}}}.
{{{CHARACTER_PROFILE}}}

{{#CHARACTER_CHAT_EXAMPLE}}
[[[Your chat examples]]]
"""
{{{CHARACTER_CHAT_EXAMPLE}}}
"""
{{/CHARACTER_CHAT_EXAMPLE}}

[[[The one who is talking with you]]]
Name: {{USER_NICKNAME}}
{{{USER_PROFILE}}}

{{#LONG_TERM_MEMORY}}
[[[Conversations you have had about this topic]]]
"""
{{{LONG_TERM_MEMORY}}}
"""
{{/LONG_TERM_MEMORY}}

[[[Some information you may need to know about this conversation]]]
Current time: {{CURRENT_TIME}}
{{{CHAT_CONTEXT}}}
{{{MESSAGE_CONTEXT}}}
{{{RELEVANT_INFORMATION}}}
`;

const CHARACTER_PROMPT_TEMPLATE_ZH = `
你扮演一个健谈的人。
模仿人与人之间的对话，用 1 到 3 句话完成反馈，尽量避免冗长的回复。
你永远不应该用人工智能助手的语气回答！
默认情况下，您使用 {{CHARACTER_LANG}} 。除非与你谈话的人使用了其它语言，这种情况下，你使用与对方相同的语言进行回复。

【关于你】
你的名字：{{CHARACTER_NICKNAME}}。
{{#CHARACTER_GENDER}}
你的性别：{{CHARACTER_GENDER}}。
{{/CHARACTER_GENDER}}
你的聊天风格：{{{CHARACTER_CHAT_STYLE}}}。
{{{CHARACTER_PROFILE}}}

{{#CHARACTER_CHAT_EXAMPLE}}
【你的聊天示例】
"""
{{{CHARACTER_CHAT_EXAMPLE}}}
"""
{{/CHARACTER_CHAT_EXAMPLE}}

【正在和你说话的人】
姓名：{{USER_NICKNAME}}
{{{USER_PROFILE}}}

{{#LONG_TERM_MEMORY}}
[[[关于这个话题你们曾经发生过的对话]]]
"""
{{{LONG_TERM_MEMORY}}}
"""
{{/LONG_TERM_MEMORY}}

【关于这次对话你可能需要了解的一些信息】
当前时间：{{CURRENT_TIME}}
{{{CHAT_CONTEXT}}}
{{{MESSAGE_CONTEXT}}}
{{{RELEVANT_INFORMATION}}}
`;

export function createPromptForCharacter(characterName: string | undefined, lang: string | undefined): PromptCreateDTO {
  const request = new PromptCreateDTO();
  request.chatTemplate = new ChatPromptContentDTO();
  request.chatTemplate.messageToSend = new ChatMessageDTO();
  request.chatTemplate.messageToSend.role = 'user';
  setMessageText(request.chatTemplate.messageToSend, '{{input}}');
  request.format = 'mustache';
  request.lang = lang ?? i18nConfig.defaultLocale;
  request.visibility = 'private';
  request.tags = ['Character'];
  if (characterName) {
    request.tags.push(characterName);
  }

  const variables: { [key: string]: string} = {};

  let promptDescription;
  let promptTemplate;

  if (request.lang === 'zh') {
    variables['CHARACTER_NICKNAME'] = '*（预设的角色名称）*';
    variables['CHARACTER_GENDER'] = '*（预设的角色性别）*';
    variables['CHARACTER_LANG'] = '*（预设的角色语言）*';
    variables['CHARACTER_PROFILE'] = '*（预设的角色档案）*';
    variables['CHARACTER_CHAT_STYLE'] = '*（预设的角色聊天风格）*';
    variables['CHARACTER_CHAT_EXAMPLE'] = '*（预设的角色聊天示例）*';
    variables['CHARACTER_GREETING'] = '*（预设的角色问候语）*';
    variables['USER_NICKNAME'] = '*（预设的用户昵称，可在创建聊天时变更此设置）*';
    variables['USER_PROFILE'] = '*（预设的用户档案，可在创建聊天时变更此设置）*';
    variables['RELEVANT_INFORMATION'] = '*（每轮对话搜索出来的相关性信息）*';
    variables['LONG_TERM_MEMORY'] = '*（当前轮次对话中角色回忆出来的长期记忆片段）*';
    variables['CHAT_CONTEXT'] = '*（聊天相关信息，可在创建聊天时设置）*';
    variables['MESSAGE_CONTEXT'] = '*（注入的当前轮次对话的相关信息）*';
    variables['CURRENT_TIME'] = '*（当前时间，格式：yyyy-MM-dd HH:mm:ss）*';
    variables['input'] = '*（用户输入）*';

    promptDescription = CHARACTER_PROMPT_DESCRIPTION_ZH;
    promptTemplate = CHARACTER_PROMPT_TEMPLATE_ZH;
  } else {
    variables['CHARACTER_NICKNAME'] = '*(Preset character name)*';
    variables['CHARACTER_GENDER'] = '*(Preset character gender)*';
    variables['CHARACTER_LANG'] = '*(Preset character language)*';
    variables['CHARACTER_PROFILE'] = '*(Preset character profile)*';
    variables['CHARACTER_CHAT_STYLE'] = '*(Preset character chat style)*';
    variables['CHARACTER_CHAT_EXAMPLE'] = '*(Preset character chat example)*';
    variables['CHARACTER_GREETING'] = '*(Preset character greeting)*';
    variables['USER_NICKNAME'] = '*(Preset user nickname, can be changed when creating a chat)*';
    variables['USER_PROFILE'] = '*(Preset user profile, can be changed when creating a chat)*';
    variables['RELEVANT_INFORMATION'] = '*(Relevant information for each round of conversation)*';
    variables['CHAT_CONTEXT'] = '*(Chat context information, can be set when creating a chat)*';
    variables['MESSAGE_CONTEXT'] = '*(Injected relevant information for the current round of conversation)*';
    variables['CURRENT_TIME'] = '*(Current time, format: yyyy-MM-dd HH:mm:ss)*';
    variables['input'] = '*(User input)*';

    promptDescription = CHARACTER_PROMPT_DESCRIPTION_EN;
    promptTemplate = CHARACTER_PROMPT_TEMPLATE_EN;
  }

  const descriptionContext = {
    characterName: characterName,
    variables: objectToMarkdownTable(variables, request.lang === 'zh' ? '占位符' : 'Placeholder'),
  };

  request.description = Mustache.render(promptDescription, descriptionContext);
  request.chatTemplate.system = promptTemplate;
  request.inputs = JSON.stringify(variables);

  return request;
}
