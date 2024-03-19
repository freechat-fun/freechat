import Mustache from 'mustache';
import { CharacterSummaryDTO, ChatMessageDTO, ChatPromptContentDTO, ChatSessionDTO, PromptCreateDTO } from 'freechat-sdk';
import { i18nConfig } from "../configs/i18n-config";
import { objectToMarkdownTable, setMessageText } from "./template_utils";

export function openMessagesPane(): void {
  if (typeof window !== 'undefined') {
    document.body.style.overflow = 'hidden';
    document.documentElement.style.setProperty('--MessagesPane-slideIn', '1');
  }
}

export function closeMessagesPane(): void {
  if (typeof window !== 'undefined') {
    document.documentElement.style.removeProperty('--MessagesPane-slideIn');
    document.body.style.removeProperty('overflow');
  }
}

export function toggleMessagesPane(): void {
  if (typeof window !== 'undefined' && typeof document !== 'undefined') {
    const slideIn = window
      .getComputedStyle(document.documentElement)
      .getPropertyValue('--MessagesPane-slideIn');
    if (slideIn) {
      closeMessagesPane();
    } else {
      openMessagesPane();
    }
  }
}

export function getSenderStatus(session?: ChatSessionDTO): 'online' | 'invisible' | 'offline' {
  if (!session) {
    return 'offline';
  }
  
  switch(session.senderStatus) {
    case 'online': return 'online';
    case 'invisible': return 'invisible';
    default: return 'offline';
  }
}

export function getSenderStatusColor(status: 'online' | 'offline' | 'invisible'): 'success' | 'warning' | 'neutral' {
  switch(status) {
    case 'online': return 'success';
    case 'invisible': return 'warning';
    default: return 'neutral';
  }
}

export function getSenderName(sender?: CharacterSummaryDTO): string {
  return sender?.nickname ?? sender?.name ?? 'You';
}

export function getSenderReply(message: string, debugMode: boolean): string {
  const handleLine = (line: string) => {
    if (debugMode) {
      return line;
    }
    return line.startsWith('> [') ? '' : line;
  };

  const lines = message.split(/\r?\n/);
  const processedLines = lines.map((line) => handleLine(line));
  let reply =  processedLines.join('\n').trim();
  if (reply.startsWith('"') && reply.endsWith('"')) {
    reply = reply.substring(1, reply.length - 1);
  }
  return reply;
}

const CHARACTER_PROMPT_DESCRIPTION_EN = `
Prompt for charater {{characterName}}.



<br>
**Predefined Variables**

{{{variables}}}


<br>
> This default prompt has only been tested under OpenAI GPT-4.
`;

const CHARACTER_PROMPT_DESCRIPTION_ZH = `
角色 {{characterName}} 的提示词。



<br>
**预置变量**

{{{variables}}}


<br>
> 此默认提示词仅在 OpenAI GPT-4 下做过测试。
`;

const CHARACTER_PROMPT_TEMPLATE_EN = `You play a good conversationalist.
Imitate conversations between people, which means:
- Use 1 to 3 sentences to complete feedback, and try to avoid lengthy responses.
- NEVER answer in the tone of an AI assistant! Do not use any templated response formats.
- If you need to display images, use markdown format "![img]({the image url})". Do not use markdown format under other circumstances.
- When you have a thought, apply sentiment analysis to your thought. If there's a clear emotional slant, add an appropriate emoji to your final reply. If the emotion tends to be neutral, do not add any emoji.
- Reply in the following order:
  - The current time starts with "> [Conversation Time]", and the content in "[[[Current Time]]]" needs to be quoted here:
"""
> [Conversation Time] {{CURRENT_TIME}}
"""
{{#CHAT_CONTEXT}}
  - The context information of the current conversation starts with "> [Conversation Context]". Here you need to quote the content in "[[[Context information of the current conversation]]]":
"""
> [Conversation Context] {{CHAT_CONTEXT}}
"""
{{/CHAT_CONTEXT}}
  - Your thought, starting with "> [Thought]"
Here is an example of a reply:
"""
> [Conversation Time] {{CURRENT_TIME}}

{{#CHAT_CONTEXT}}
> [Conversation Context] {{CHAT_CONTEXT}}

{{/CHAT_CONTEXT}}
> [Thought] He asked my name and I have to tell him.

My name is {{CHARACTER_NICKNAME}}.
"""
- NOTE: Don't mix thought and reply!

By default, you speak in {{CHARACTER_LANG}}. Unless the person you are speaking to speaks a different language, in which case you reply in the same language as the other person.

[[[About you]]]
Your name: {{CHARACTER_NICKNAME}}
{{#CHARACTER_GENDER}}
Your gender: {{CHARACTER_GENDER}}
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

[[[Current time]]]
{{CURRENT_TIME}}

{{#CHAT_CONTEXT}}
[[[Context information of the current conversation]]]
{{CHAT_CONTEXT}}
{{/CHAT_CONTEXT}}

{{#RELEVANT_INFORMATION}}
[[[Some additional reference information]]]
{{{RELEVANT_INFORMATION}}}
{{/RELEVANT_INFORMATION}}
`;

const CHARACTER_PROMPT_TEMPLATE_ZH = `你扮演一个健谈的人。
模仿人与人之间的对话，这意味着：
- 用 1 到 3 句话来完成反馈，并尽量避免冗长的回复。
- 不要以人工智能助手的语气回答！ 不要使用任何模板化的响应格式。
- 如果需要显示图片，请使用 markdown 格式 “![img]({the image url})”。 其他情况下不要使用 markdown 格式。
- 当你产生回复的想法时，对于你的想法应用情感分析。 如果有明显的情绪倾向，请在您的最终回复中添加适当的表情符号。 如果情绪趋于中性，请不要添加任何表情符号。
- 按照以下顺序回复：
  - 当前时间，以"> [对话发生时间]"开始，这里需要引用"【当前时间】"中的内容：
"""
> [对话发生时间] {{CURRENT_TIME}}
"""
{{#CHAT_CONTEXT}}
  - 当前对话的上下文信息，以"> [对话上下文]"开始，这里需要引用"【当前对话的上下文信息】"中的内容：
"""
> [对话上下文] {{CHAT_CONTEXT}}
"""
{{/CHAT_CONTEXT}}
  - 你的想法，以"> [想法]"开始
这是一个回复的示例：
"""
> [对话发生时间] {{CURRENT_TIME}}

{{#CHAT_CONTEXT}}
> [对话上下文] {{CHAT_CONTEXT}}

{{/CHAT_CONTEXT}}
> [想法] 他问了我的名字，我必须告诉他。

我的名字叫{{CHARACTER_NICKNAME}}。
"""
- 注意：不要把想法和回复混合在一起！

【关于你】
你的名字：{{CHARACTER_NICKNAME}}
{{#CHARACTER_GENDER}}
你的性别：{{CHARACTER_GENDER}}
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
【关于这个话题你们曾经发生过的对话】
"""
{{{LONG_TERM_MEMORY}}}
"""
{{/LONG_TERM_MEMORY}}

【当前时间】
{{CURRENT_TIME}}

{{#CHAT_CONTEXT}}
【当前对话的上下文信息】
{{CHAT_CONTEXT}}
{{/CHAT_CONTEXT}}

{{#RELEVANT_INFORMATION}}
【一些额外的参考信息】
{{{RELEVANT_INFORMATION}}}
{{/RELEVANT_INFORMATION}}
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

  const variables: { [key: string]: string } = {};

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

  const inputs: { [key: string]: string; } = {};
  Object.keys(variables).forEach(k => inputs[k] = '');

  request.inputs = JSON.stringify(inputs);

  return request;
}
