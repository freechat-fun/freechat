const location = document.querySelector('meta[name="_location"]')?.getAttribute('content');
const defaultLocaleIndex = location === 'CN' ? 1 : 0;

const locales: Readonly<{ [key: string]: string }> = {
  'en': 'English',
  'zh': '中文',
};

const i18nConfig = {
  locales: Object.keys(locales),
  defaultLocale: Object.keys(locales)[defaultLocaleIndex],
  namespaces: ['translation', 'button', 'sign-in', 'account', 'sidebar', 'prompt', 'character', 'chat'],
  defaultNamespace: 'translation',
} as const;

function getLocaleLabel(key: string): string | undefined {
  if (key in locales) {
    return locales[key];
  }
  return undefined;
}

export { locales, i18nConfig, getLocaleLabel };