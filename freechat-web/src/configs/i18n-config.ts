const locales: Readonly<{ [key: string]: string }> = {
  'en': 'English',
  'zh': '中文',
};

const i18nConfig = {
  locales: Object.keys(locales),
  defaultLocale: Object.keys(locales)[0],
  namespaces: ['translation', 'button', 'sign-in', 'account', 'sidebar', 'prompt', 'character'],
  defaultNamespace: 'translation',
} as const;

function getLocaleLabel(key: string): string | undefined {
  if (key in locales) {
    return locales[key];
  }
  return undefined;
}

export { locales, i18nConfig, getLocaleLabel };