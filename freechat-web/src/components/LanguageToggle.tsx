import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { i18nConfig, locales } from '../configs/i18n-config';
import { EnIcon, ZhIcon } from './icon';
import { ListItemButton, ListItemButtonProps } from '@mui/material';

export default function LanguageToggle(props: ListItemButtonProps) {
  const [mounted, setMounted] = useState(false);
  const { i18n } = useTranslation();
  const language = i18n.language || i18nConfig.defaultLocale;
  const Icon = language === 'zh' ? EnIcon : ZhIcon;

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return <ListItemButton dense id="toggle-language" disabled {...props} />;
  }
  return (
    <ListItemButton
      dense
      id="toggle-language"
      onClick={() => {
        const languages = Object.keys(locales);
        const index = languages.findIndex((l) => l === language);
        const nextIndex = (index + 1) % languages.length;
        i18n.changeLanguage(languages[nextIndex]);
      }}
      {...props}
    >
      <Icon />
    </ListItemButton>
  );
}
