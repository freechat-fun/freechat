const location = document
  .querySelector('meta[name="_location"]')
  ?.getAttribute('content');

const locales: Readonly<{ [key: string]: string }> = {
  en: 'English',
  zh: '中文',
  ta: 'தமிழ்',
};

const i18nConfig = {
  locales: Object.keys(locales),
  defaultLocale: location === 'CN' ? 'zh' : 'en',
  namespaces: [
    'translation',
    'button',
    'sign-in',
    'account',
    'sidebar',
    'prompt',
    'skill',
    'connector',
    'character',
    'chat',
  ],
  defaultNamespace: 'translation',
} as const;

function getLocaleLabel(key: string): string | undefined {
  if (key in locales) {
    return locales[key];
  }
  return undefined;
}

export { locales, i18nConfig, getLocaleLabel };
