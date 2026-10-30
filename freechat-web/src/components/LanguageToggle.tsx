import { useState, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { i18nConfig, locales } from '../configs/i18n-config';
import { CheckRounded, TranslateRounded } from '@mui/icons-material';
import {
  ListItemButton,
  ListItemButtonProps,
  ListItemIcon,
  Menu,
  MenuItem,
} from '@mui/material';

export default function LanguageToggle(props: ListItemButtonProps) {
  const [mounted, setMounted] = useState(false);
  const [openMenu, setOpenMenu] = useState(false);
  const anchorRef = useRef<HTMLDivElement>(null);
  const { i18n } = useTranslation();
  const language =
    i18n.resolvedLanguage || i18n.language || i18nConfig.defaultLocale;

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return <ListItemButton dense id="toggle-language" disabled {...props} />;
  }
  return (
    <>
      <ListItemButton
        dense
        id="toggle-language"
        ref={anchorRef}
        onClick={() => setOpenMenu(true)}
        {...props}
      >
        <TranslateRounded />
      </ListItemButton>
      <Menu
        id="language-menu"
        anchorEl={anchorRef.current}
        open={openMenu}
        onClose={() => setOpenMenu(false)}
        // sidebars sit at zIndex 9999, above the MUI modal layer
        sx={{ zIndex: 10000 }}
      >
        {Object.keys(locales).map((locale) => (
          <MenuItem
            key={`locale-${locale}`}
            selected={locale === language}
            onClick={() => {
              i18n.changeLanguage(locale);
              setOpenMenu(false);
            }}
          >
            <ListItemIcon>
              <CheckRounded
                fontSize="small"
                sx={{ opacity: locale === language ? 1 : 0 }}
              />
            </ListItemIcon>
            {locales[locale]}
          </MenuItem>
        ))}
      </Menu>
    </>
  );
}
