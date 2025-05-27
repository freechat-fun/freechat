import { forwardRef } from 'react';
import { Typography, TypographyProps } from '@mui/material';

const LabelTypography = forwardRef<HTMLDivElement, TypographyProps>(
  (props, ref) => {
    const { sx, children, ...others } = props;

    return (
      <Typography
        ref={ref}
        variant="subtitle1"
        color="textSecondary"
        sx={{
          mr: 2,
          ...sx,
        }}
        {...others}
      >
        {children}
      </Typography>
    );
  }
);

export default LabelTypography;
