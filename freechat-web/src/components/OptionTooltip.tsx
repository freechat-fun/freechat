import { forwardRef } from 'react';
import { Tooltip, TooltipProps } from '@mui/material';

const OptionTooltip = forwardRef<HTMLDivElement, TooltipProps>((props, ref) => {
  const { children, ...others } = props;
  return (
    <Tooltip
      ref={ref}
      slotProps={{
        tooltip: {
          sx: {
            fontSize: 'small',
            maxWidth: '20rem',
          },
        },
      }}
      {...others}
    >
      {children}
    </Tooltip>
  );
});

OptionTooltip.displayName = 'OptionTooltip';

export default OptionTooltip;
