import { forwardRef, useEffect, useMemo, useState } from 'react';
import {
  Typography,
  TypographyProps,
  useColorScheme,
  useTheme,
} from '@mui/material';
import { escapeRegExp } from '../libs/js_utils';

export type HighlightedTypographyProps = TypographyProps & {
  highlight?: string;
};

const HighlightedTypography = forwardRef<
  HTMLDivElement,
  HighlightedTypographyProps
>((props, ref) => {
  const { highlight, children, ...others } = props;
  const theme = useTheme();
  const { mode } = useColorScheme();
  const [bgColor, setBgColor] = useState<string>(
    mode === 'light' ? theme.palette.warning.light : theme.palette.warning.dark
  );

  useEffect(() => {
    setBgColor(
      mode === 'light'
        ? theme.palette.warning.light
        : theme.palette.warning.dark
    );
  }, [mode, theme.palette.warning.dark, theme.palette.warning.light]);

  const parts = useMemo<string[]>(() => {
    if (!highlight?.trim() || typeof children !== 'string') {
      return [];
    }

    const escapedHighlight = escapeRegExp(highlight);
    const regex = new RegExp(`(${escapedHighlight})`, 'gi');
    return children.split(regex);
  }, [children, highlight]);

  if (!highlight?.trim() || parts.length === 0) {
    return (
      <Typography ref={ref} {...others}>
        {children}
      </Typography>
    );
  }

  return (
    <Typography ref={ref} {...others}>
      {parts.map((part, index) =>
        part.toLowerCase() === highlight.toLowerCase() ? (
          <span
            key={`${part}-${index}`}
            style={{
              background: bgColor,
            }}
          >
            {part}
          </span>
        ) : (
          <span key={`${part}-${index}`}>{part}</span>
        )
      )}
    </Typography>
  );
});

export default HighlightedTypography;
