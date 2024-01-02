/* eslint-disable @typescript-eslint/no-explicit-any */
import Mustache from 'mustache';
import { PromptDetailsDTO } from 'freechat-sdk';

export function extractMustacheTemplateVariableNames(templateContent: string): string[] {
  const variables: string[] = [];
  const context = {
    get(name: string): string {
      variables.push(name);
      return '';
    }
  };

  Mustache.render(templateContent, context);
  return [...new Set(variables)];
}

export function extractFStringTemplateVariableNames(templateContent: string): string[] {
  const VAR_PATTERN: RegExp = /{(.*?)}(?!})/g;
  const variables: string[] = [];
  let match: RegExpExecArray | null;

  while ((match = VAR_PATTERN.exec(templateContent)) !== null) {
    variables.push(match[1].trim());
  }
  
  return [...new Set(variables)];
}

export function extractVariables(record: PromptDetailsDTO | undefined): { [key: string]: any } | undefined {
  const templateContent = getTemplateContent(record);
  if (!templateContent) {
    return undefined;
  }
  const variableNames = record?.format === 'f_string' ?
    extractFStringTemplateVariableNames(templateContent) :
    extractMustacheTemplateVariableNames(templateContent);

  const defaultInputs = record?.inputs ? extractJson(record.inputs) : undefined;

  return initVariables(variableNames, defaultInputs);
}

export function getTemplateContent(record: PromptDetailsDTO | undefined): string | undefined{
  if (record?.type === 'chat' && record?.chatTemplate) {
    const chatTemplate = record?.chatTemplate;
    let template = chatTemplate.system ?? '';

    const messages = chatTemplate.messages ?? [];
    messages.forEach(message => 
      template = `${template}\n${message.name || message.role}:${message.content}`);
    
    template = `${template}\n${chatTemplate.messageToSend?.content ?? (record?.format === 'f_string' ? '{input}' : '{{input}}')}`;

    return template;
  } else {
    return record?.template;
  }
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
