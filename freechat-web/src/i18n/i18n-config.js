const locales = {
  'en': 'English',
  'zh': '中文',
}

const i18nConfig = {
  locales: Object.keys(locales),
  defaultLocale: Object.keys(locales)[0],
  namespaces: ['translation', 'button'],
  defaultNamespace: 'translation',
};

export { locales, i18nConfig };