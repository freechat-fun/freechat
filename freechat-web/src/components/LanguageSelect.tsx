import { useTranslation } from 'react-i18next';
import { Option, Select } from '@mui/joy';
import { LanguageRounded } from '@mui/icons-material';
import { locales, i18nConfig } from '../configs/i18n-config.ts';

export default function LanguageSelect() {
  const { i18n } = useTranslation();

  const handleChange = (
    _event: React.SyntheticEvent | null,
    value: string | null
  ) => {
    if (value) {
      i18n.changeLanguage(value);
    }
  };

  const language = i18n.language || i18nConfig.defaultLocale;

  return (
    <Select
      variant="plain"
      onChange={handleChange}
      value={language}
      startDecorator={<LanguageRounded />}
    >
      {Object.keys(locales).map((locale) => (
        <Option key={locale} value={locale}>
          {locales[locale]}
        </Option>
      ))}
    </Select>
  );
}
