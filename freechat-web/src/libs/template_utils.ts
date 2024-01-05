/* eslint-disable @typescript-eslint/no-explicit-any */
import Mustache from 'mustache';
import { ChatPromptContentDTO, LlmResultDTO, PromptAiParamDTO, PromptDetailsDTO } from 'freechat-sdk';

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
      templates.push(`${message.content}`));
    
    templates.push(`${chatTemplate.messageToSend?.content ?? (record?.format === 'f_string' ? '{input}' : '{{input}}')}`);
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

### Model Info
{{model}}

### Model Parameters:
{{{parameters}}}

### Input
{{{prompt}}}

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

  let markdownContent = '**[SYSTEM]**\n';

  markdownContent += `${chatTemplate.system ?? ''}\n\n`;

  if (chatTemplate.messages && chatTemplate.messages.length > 0) {
    markdownContent += '**[MESSAGES]**\n';
    chatTemplate.messages.forEach(message => {
      markdownContent += `**${message.role?.toUpperCase()}: **${message.content}\n`;
    });
    markdownContent += '\n';
  }

  markdownContent += '**[USER]**\n';
  markdownContent += `${chatTemplate.messageToSend?.content ?? (format === 'f_string' ? '{input}' : '{{input}}')}`;

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
  const output = response.message?.content ?? response.text;

  const markdownContext = {
    variables: variables,
    prompt: prompt,
    model: model,
    parameters: parameters,
    output: output,
  };

  return Mustache.render(EXAMPLE_TEMPLATE, markdownContext);
}
