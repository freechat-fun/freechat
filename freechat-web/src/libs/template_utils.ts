/* eslint-disable @typescript-eslint/no-explicit-any */
import Mustache from 'mustache';
import { PromptDetailsDTO } from 'freechat-sdk';

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
