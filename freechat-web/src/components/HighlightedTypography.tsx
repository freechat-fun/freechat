import { forwardRef, useMemo } from "react";
import { Typography, TypographyProps } from "@mui/joy";
import { escapeRegExp } from "../libs/js_utils";

type HighlightedTypographyProps = TypographyProps & {
  highlight?: string;
}

const HighlightedTypography = forwardRef<HTMLDivElement, HighlightedTypographyProps>((props, ref) => {
  const { highlight, children, ...others } = props;

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
      {parts.map((part, index) => (
        part.toLowerCase() === highlight.toLowerCase() ? (
          <span key={`${part}-${index}`} style={{ backgroundColor: 'yellow' }}>{part}</span>
        ) : (
          <span key={`${part}-${index}`}>{part}</span>
        )
      ))}
    </Typography>
  );
});

export default HighlightedTypography;
