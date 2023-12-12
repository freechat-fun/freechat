const locales: Readonly<{ [key: string]: string }> = {
  'en': 'English',
  'zh': '中文',
};

const i18nConfig = {
  locales: Object.keys(locales),
  defaultLocale: Object.keys(locales)[0],
  namespaces: ['translation', 'button', 'sign-in', 'account'],
  defaultNamespace: 'translation',
} as const;

export { locales, i18nConfig };