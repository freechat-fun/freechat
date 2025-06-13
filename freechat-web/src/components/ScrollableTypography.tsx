import { forwardRef } from 'react';
import { Typography, TypographyProps, useTheme } from '@mui/material';

const ScrollableTypography = forwardRef<HTMLDivElement, TypographyProps>(
  (props, ref) => {
    const { sx, children, ...others } = props;
    const theme = useTheme();

    return (
      <Typography
        ref={ref}
        variant="body1"
        component="div"
        sx={{
          maxHeight: 'calc(1.6em * 10)',
          overflowY: 'auto',
          padding: 2,
          width: '100%',
          boxSizing: 'border-box',
          whiteSpace: 'pre-wrap',
          border: 1,
          borderColor: theme.palette.divider,
          borderRadius: '2px',
          ...sx,
        }}
        {...others}
      >
        {children}
      </Typography>
    );
  }
);

ScrollableTypography.displayName = 'ScrollableTypography';

export default ScrollableTypography;
