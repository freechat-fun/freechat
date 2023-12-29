/* eslint-disable @typescript-eslint/no-explicit-any */
import Mustache from 'mustache';
import { PromptDetailsDTO } from 'freechat-sdk';

export function extractMustacheTemplateVariableNames(templateContent: string): Set<string> {
  const variables = new Set<string>();
  const context = {
    get(name: string): string {
      variables.add(name);
      return '';
    }
  };

  Mustache.render(templateContent, context);
  return variables;
}

export function extractFStringTemplateVariableNames(templateContent: string): Set<string> {
  const VAR_PATTERN: RegExp = /{(.*?)}(?!})/g;
  const variables = new Set<string>();
  let match: RegExpExecArray | null;

  while ((match = VAR_PATTERN.exec(templateContent)) !== null) {
      variables.add(match[1].trim());
  }
  
  return variables;
}

export function extractVariables(record: PromptDetailsDTO | undefined): Map<string,  any> | undefined {
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
  return record?.type === 'chat' && record?.chatTemplate ?
    `${record?.chatTemplate?.system ?? ''}\n
      ${record?.chatTemplate?.messagesToSend?.content ?? (record?.format === 'f_string' ? '{input}' : '{{input}}')}` :
    record?.template;
}

export function initVariables(names: Set<string>, defaultInputs: Map<string,  any> | undefined): Map<string,  any> {
  const resultMap = new Map<string, string | undefined>();
  names.forEach(name => resultMap.set(name, defaultInputs?.get(name)));
  return resultMap;
}

export function extractJson(jsonString: string): Map<string,  any> {
  const jsonObject = JSON.parse(jsonString);
  const resultMap = new Map<string, string | undefined>();

  for (const [key, value] of Object.entries(jsonObject)) {
    if (value === null) {
      resultMap.set(key, undefined);
    } else if (typeof value === 'string') {
      resultMap.set(key, value);
    } else {
      resultMap.set(key, JSON.stringify(value));
    }
  }
  return resultMap;
}

export function mapToTypedObject<T>(map: Map<string, any> | undefined): T | undefined {
  if (typeof map === 'undefined') {
    return undefined;
  }
  const result: { [key: string] : any } = {};
  map.forEach((value, key) => {
    result[key] = value;
  });
  return result as T;
}
