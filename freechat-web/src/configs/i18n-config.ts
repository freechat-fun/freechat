const location = document
  .querySelector('meta[name="_location"]')
  ?.getAttribute('content');

// location 'CN' ஆக இருந்தால் 'zh' (index 1), இல்லையெனில் 'en' (index 0)
const defaultLocaleIndex = location === 'CN' ? 1 : 0;

const locales: Readonly<{ [key: string]: string }> = {
  en: 'English',
  zh: '中文',
  ta: 'தமிழ்', // தமிழ் சேர்க்கப்பட்டது
};

const i18nConfig = {
  locales: Object.keys(locales),
  defaultLocale: Object.keys(locales)[defaultLocaleIndex],
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
