import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { i18nConfig, locales } from "../configs/i18n-config";
import { IconButton, IconButtonProps } from "@mui/joy";
import { LanguageRounded } from "@mui/icons-material";
import { EnIcon, ZhIcon } from "./icon";

export default function LanguageToggle(props: IconButtonProps) {
  const [mounted, setMounted] = useState(false);

  const { i18n } = useTranslation();

  const language = i18n.language || i18nConfig.defaultLocale;

  const Icon = language === 'en' ? ZhIcon : (language == 'zh' ? EnIcon : LanguageRounded);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <IconButton
        size="sm"
        variant="plain"
        color="neutral"
        {...props}
        disabled
      />
    );
  }
  return (
    <IconButton
      id="toggle-language"
      size="sm"
      variant="plain"
      color="neutral"
      {...props}
      onClick={() => {
        const languages = Object.keys(locales);
        const index = languages.findIndex(l => l === language);
        const nextIndex = (index + 1) % languages.length;
        i18n.changeLanguage(languages[nextIndex]);
      }}
    >
      <Icon />
    </IconButton>
  );
}