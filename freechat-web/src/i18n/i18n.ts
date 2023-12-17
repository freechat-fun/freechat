import i18n from 'i18next';
import { initReactI18next } from "react-i18next/initReactI18next";
import resourcesToBackend from 'i18next-resources-to-backend';
import LanguageDetector from "i18next-browser-languagedetector";
import { i18nConfig } from './i18n-config';

i18n
  .use(initReactI18next)
  .use(LanguageDetector)
  .use(resourcesToBackend((language: string, namespace: string) => import(`./locales/${language}/${namespace}.json`)))
  .init({
    fallbackLng: i18nConfig.defaultLocale,
    supportedLngs: i18nConfig.locales,
    defaultNS: i18nConfig.defaultNamespace,
    fallbackNS: i18nConfig.defaultNamespace,
    ns: i18nConfig.namespaces,
    nonExplicitSupportedLngs: true,
    interpolation: {
      escapeValue: false, // not needed for react as it escapes by default
    },

    debug: false,
  });
