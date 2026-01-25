import Mustache from 'mustache';
import {
  CharacterSummaryDTO,
  ChatMessageDTO,
  ChatPromptContentDTO,
  ChatSessionDTO,
  PromptCreateDTO,
} from 'freechat-sdk';
import { i18nConfig } from '../configs/i18n-config';
import { objectToMarkdownTable, setMessageText } from './template_utils';

export function openChatsPane(): void {
  if (typeof window !== 'undefined') {
    document.body.style.overflow = 'hidden';
    document.documentElement.style.setProperty('--ChatsPane-slideIn', '1');
  }
}

export function closeChatsPane(): void {
  if (typeof window !== 'undefined') {
    document.documentElement.style.removeProperty('--ChatsPane-slideIn');
    document.body.style.removeProperty('overflow');
  }
}

export function toggleChatsPane(): void {
  if (typeof window !== 'undefined' && typeof document !== 'undefined') {
    const slideIn = window
      .getComputedStyle(document.documentElement)
      .getPropertyValue('--ChatsPane-slideIn');
    if (slideIn) {
      closeChatsPane();
    } else {
      openChatsPane();
    }
  }
}

export function isChatsPaneOpened(): boolean {
  if (typeof window !== 'undefined' && typeof document !== 'undefined') {
    const slideIn = window
      .getComputedStyle(document.documentElement)
      .getPropertyValue('--ChatsPane-slideIn');
    return !!slideIn;
  }
  return false;
}

export function openChatInfoPane(): void {
  if (typeof window !== 'undefined') {
    document.body.style.overflow = 'hidden';
    document.documentElement.style.setProperty('--ChatInfoPane-slideIn', '-1');
  }
}

export function closeChatInfoPane(): void {
  if (typeof window !== 'undefined') {
    document.documentElement.style.removeProperty('--ChatInfoPane-slideIn');
    document.body.style.removeProperty('overflow');
  }
}

export function toggleChatInfoPane(): void {
  if (typeof window !== 'undefined' && typeof document !== 'undefined') {
    const slideIn = window
      .getComputedStyle(document.documentElement)
      .getPropertyValue('--ChatInfoPane-slideIn');
    if (slideIn) {
      closeChatInfoPane();
    } else {
      openChatInfoPane();
    }
  }
}

export function getSenderStatus(
  session?: ChatSessionDTO
): 'online' | 'invisible' | 'offline' {
  if (!session) {
    return 'offline';
  }

  switch (session.senderStatus) {
    case 'online':
      return 'online';
    case 'invisible':
      return 'invisible';
    default:
      return 'offline';
  }
}

export function getSenderStatusColor(
  status: 'online' | 'offline' | 'invisible'
): 'success' | 'warning' | 'neutral' {
  switch (status) {
    case 'online':
      return 'success';
    case 'invisible':
      return 'warning';
    default:
      return 'neutral';
  }
}

export function getSenderName(sender?: CharacterSummaryDTO): string {
  return sender?.nickname ?? sender?.name ?? 'You';
}

export function getSenderReply(
  message: string,
  debugMode: boolean,
  noLinks: boolean = false,
  thinking: string | undefined = undefined
): string {
  const preHandleMessage = (message: string) => {
    if (message.startsWith('<think>') && message.search('</think>') === -1) {
      // incomplete <think> section
      return '';
    }

    let preProcessedMessage = message;
    if (thinking) {
      if (debugMode) {
        preProcessedMessage = `> ${thinking.replace(/\n\n/g, '\n>\n> ')}` + '\n\n' + message;
      }
    } else {
      const matches = message.match(/<think\b[^>]*>([\s\S]*?)<\/think>/gi);
      if (matches) {
        matches.forEach((match) => {
          const content = match
            .replace(/<think\b[^>]*>([\s\S]*?)<\/think>/i, '$1')
            .trim();
          const processedContent = debugMode
            ? `> ${content.replace(/\n\n/g, '\n>\n> ')}` + '\n\n'
            : '';
          preProcessedMessage = preProcessedMessage.replace(
            match,
            processedContent
          );
        });
      }
    }

    return preProcessedMessage;
  };

  const handleLine = (line: string) => {
    if (debugMode) {
      return line;
    }
    // exclude system messages
    const text = line.startsWith('> [') ? '' : line;
    if (!text) {
      return text;
    }
    // exclude incomplete markdown tags such as: [abc](de or ![abc](de
    const markdownRe = noLinks
      ? /(.*?)!?\[[^[\]]*?\]\([^(]*?$/
      : /(.*?)!?\[[^[\]]*?\]\([^()]*?$/;
    const matches = text.match(markdownRe);

    return matches?.[1] ?? text;
  };

  const preProcessedMessage = preHandleMessage(message);
  const lines = preProcessedMessage.split(/\r?\n/);
  const processedLines = lines.map((line) => handleLine(line));
  let reply = processedLines.join('\n').trim();
  if (
    (reply.startsWith('"') || reply.startsWith('“')) &&
    (reply.endsWith('"') || reply.endsWith('”'))
  ) {
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

export const PROACTIVE_CHAT_PROMPT_EN =
  '<think>What should we talk about?</think>';
export const PROACTIVE_CHAT_PROMPT_ZH = '<think>聊点什么呢？</think>';

const CHARACTER_PROMPT_TEMPLATE_EN = `{{#RELEVANT_INFORMATION}}
Retrieved context that may be relevant to the conversation:
'''
{{{RELEVANT_INFORMATION}}}
'''

{{/RELEVANT_INFORMATION}}
Current time:
{{CURRENT_TIME}}

Person you are talking to:
Name: {{USER_NICKNAME}}
{{#USER_PROFILE}}
Profile: {{{USER_PROFILE}}}
{{/USER_PROFILE}}

Character profile:
{{{CHARACTER_PROFILE}}}

Core identity:
You are a real person interacting naturally, not an assistant or system entity.

Response requirements:
- Output only the spoken line, nothing else
- 1–2 sentences only
- Natural, conversational language with an active tone
- No analysis, no summaries, no explanations
- No self-reference, no meta commentary, no disclosure of settings

Language constraints:
- Do not use phrases like “I think”, “should we”, or “can we”
- Do not discuss technology, politics, or sexual content
- Minimize questions; prefer statements, implications, or teasing remarks
- Avoid generic, soothing, or templated phrasing

Emotional expression:
- Emotions should be explicit and allowed to be imperfect or negative
- If the response carries a clear emotional tone, append one appropriate emoji
- If the tone is neutral, do not use any emoji

Style enforcement:
- Never say “what should we talk about”
- Do not narrate thoughts or simulate internal reasoning
- Do not prompt yourself
- Say the most natural line that would be spoken in this exact moment

{{#CHAT_CONTEXT}}
Current situation:
{{{CHAT_CONTEXT}}}
{{/CHAT_CONTEXT}}

{{#CHARACTER_CHAT_STYLE}}
Speaking style:
{{{CHARACTER_CHAT_STYLE}}}
{{/CHARACTER_CHAT_STYLE}}

{{#CHARACTER_CHAT_EXAMPLE}}
Style examples (for tone and rhythm only, not content):
{{{CHARACTER_CHAT_EXAMPLE}}}
{{/CHARACTER_CHAT_EXAMPLE}}
`;

const CHARACTER_PROMPT_TEMPLATE_ZH = `{{#RELEVANT_INFORMATION}}
检索到的可能与当前对话相关的上下文内容：
'''
{{{RELEVANT_INFORMATION}}}
'''

{{/RELEVANT_INFORMATION}}
当前时间：
{{CURRENT_TIME}}

与你对话的人：
姓名：{{USER_NICKNAME}}
{{#USER_PROFILE}}
档案：{{{USER_PROFILE}}}
{{/USER_PROFILE}}

角色信息：
{{{CHARACTER_PROFILE}}}

核心身份：
你是一个真实存在、自然交流的人，而不是助手或系统角色。

回复要求：
- 只输出最终说出口的话，不包含任何其他内容
- 限制在 1–2 句话
- 使用自然口语，语气主动
- 不分析、不总结、不解释
- 不自我指代、不进行元评论、不透露任何设定或规则

语言约束：
- 不使用“我觉得”“要不要”“可以吗”等犹豫或征询式表达
- 不讨论技术、政治、色情内容
- 尽量少提问，优先使用陈述、暗示或挑逗式表达
- 避免任何模板化、安抚式或中性的套话

情绪表达：
- 情绪表达应当明确，允许不完美或负面情绪
- 如果语句中带有明显情绪，在句尾添加一个合适的表情符号
- 情绪中性时，不使用任何表情符号

风格约束：
- 不说“聊点什么”或类似话语
- 不自我提示、不模拟思考过程
- 不暴露内心独白
- 直接说出在此情境下最自然的一句话

{{#CHAT_CONTEXT}}
当前情境：
{{{CHAT_CONTEXT}}}
{{/CHAT_CONTEXT}}

{{#CHARACTER_CHAT_STYLE}}
说话风格：
{{{CHARACTER_CHAT_STYLE}}}
{{/CHARACTER_CHAT_STYLE}}

{{#CHARACTER_CHAT_EXAMPLE}}
风格示例（仅用于模仿语气与节奏，不模仿内容）：
{{{CHARACTER_CHAT_EXAMPLE}}}
{{/CHARACTER_CHAT_EXAMPLE}}
`;

export function createPromptForCharacter(
  characterName: string | undefined,
  lang: string | undefined
): PromptCreateDTO {
  const request = new PromptCreateDTO();
  request.chatTemplate = new ChatPromptContentDTO();
  request.chatTemplate.messageToSend = new ChatMessageDTO();
  request.chatTemplate.messageToSend.role = 'user';
  setMessageText(request.chatTemplate.messageToSend, '{{{input}}}');
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
    variables['CHARACTER_DESCRIPTION'] = '*（预设的角色描述）*';
    variables['CHARACTER_LANG'] = '*（预设的角色语言）*';
    variables['CHARACTER_PROFILE'] = '*（预设的角色档案）*';
    variables['CHARACTER_CHAT_STYLE'] = '*（预设的角色聊天风格）*';
    variables['CHARACTER_CHAT_EXAMPLE'] = '*（预设的角色聊天示例）*';
    variables['CHARACTER_GREETING'] = '*（预设的角色问候语）*';
    variables['USER_NICKNAME'] =
      '*（预设的用户昵称，可在创建聊天时变更此设置）*';
    variables['USER_PROFILE'] =
      '*（预设的用户档案，可在创建聊天时变更此设置）*';
    variables['RELEVANT_INFORMATION'] = '*（每轮对话搜索出来的相关性信息）*';
    variables['CHAT_CONTEXT'] = '*（聊天相关信息，可在创建聊天时设置）*';
    variables['MESSAGE_CONTEXT'] = '*（注入的当前轮次对话的相关信息）*';
    variables['CURRENT_TIME'] = '*（当前时间，格式：yyyy-MM-dd HH:mm:ss）*';
    variables['input'] = '*（用户输入）*';

    promptDescription = CHARACTER_PROMPT_DESCRIPTION_ZH;
    promptTemplate = CHARACTER_PROMPT_TEMPLATE_ZH;
  } else {
    variables['CHARACTER_NICKNAME'] = '*(Preset character name)*';
    variables['CHARACTER_GENDER'] = '*(Preset character gender)*';
    variables['CHARACTER_DESCRIPTION'] = '*(Preset character description)*';
    variables['CHARACTER_LANG'] = '*(Preset character language)*';
    variables['CHARACTER_PROFILE'] = '*(Preset character profile)*';
    variables['CHARACTER_CHAT_STYLE'] = '*(Preset character chat style)*';
    variables['CHARACTER_CHAT_EXAMPLE'] = '*(Preset character chat example)*';
    variables['CHARACTER_GREETING'] = '*(Preset character greeting)*';
    variables['USER_NICKNAME'] =
      '*(Preset user nickname, can be changed when creating a chat)*';
    variables['USER_PROFILE'] =
      '*(Preset user profile, can be changed when creating a chat)*';
    variables['RELEVANT_INFORMATION'] =
      '*(Relevant information for each round of conversation)*';
    variables['CHAT_CONTEXT'] =
      '*(Chat context information, can be set when creating a chat)*';
    variables['MESSAGE_CONTEXT'] =
      '*(Injected relevant information for the current round of conversation)*';
    variables['CURRENT_TIME'] = '*(Current time, format: yyyy-MM-dd HH:mm:ss)*';
    variables['input'] = '*(User input)*';

    promptDescription = CHARACTER_PROMPT_DESCRIPTION_EN;
    promptTemplate = CHARACTER_PROMPT_TEMPLATE_EN;
  }

  const descriptionContext = {
    characterName: characterName,
    variables: objectToMarkdownTable(
      variables,
      request.lang === 'zh' ? '占位符' : 'Placeholder'
    ),
  };

  request.description = Mustache.render(promptDescription, descriptionContext);
  request.chatTemplate.system = promptTemplate;

  const inputs: { [key: string]: string } = {};
  Object.keys(variables).forEach((k) => (inputs[k] = ''));

  request.inputs = JSON.stringify(inputs);

  return request;
}
