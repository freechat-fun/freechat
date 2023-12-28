import Mustache from 'mustache';

export function extractMustacheTemplateVariables(templateContent: string): Set<string> {
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

export function extractFStringTemplateVariables(templateContent: string): Set<string> {
  const VAR_PATTERN: RegExp = /(^|[^{])({)(\{*[^{}]*\})(})([^}]|$)/g;
  const variables = new Set<string>();
  let match: RegExpExecArray | null;

  while ((match = VAR_PATTERN.exec(templateContent)) !== null) {
      variables.add(match[3].trim());
  }

  return variables;
}

export function extractJson(jsonString: string): Map<string,  string | undefined> {
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
