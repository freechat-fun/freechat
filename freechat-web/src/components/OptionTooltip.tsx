import { forwardRef } from 'react';
import { Tooltip, TooltipProps } from '@mui/material';

const OptionTooltip = forwardRef<HTMLDivElement, TooltipProps>((props, ref) => {
  const { sx, children, ...others } = props;
  return (
    <Tooltip
      ref={ref}
      slotProps={{
        tooltip: {
          sx: {
            fontSize: 'small',
            maxWidth: '20rem',
            ...sx,
          },
        },
      }}
      {...others}
    >
      {children}
    </Tooltip>
  );
});

export default OptionTooltip;
