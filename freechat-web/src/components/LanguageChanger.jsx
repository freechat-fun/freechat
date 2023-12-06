import { useTranslation } from 'react-i18next';
import { Option, Select } from '@mui/joy';
import { Language } from '@mui/icons-material';
import { locales } from '../i18n/i18n-config';

export default function LanguageChanger() {
  const { i18n } = useTranslation();

  function handleChange (_e, value) {
    const newLocale = value;
    i18n.changeLanguage(newLocale);
  }

  return (
    <Select
      variant='plain'
      onChange={handleChange}
      value={i18n.language}
      startDecorator={<Language />}
    >
      {Object.keys(locales).map(locale =>
         (<Option key={locale} value={locale}>{locales[locale]}</Option>))}
    </Select>
  );
}