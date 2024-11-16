import { forwardRef } from 'react';
import { Tooltip, TooltipProps } from '@mui/joy';

const OptionTooltip = forwardRef<HTMLDivElement, TooltipProps>((props, ref) => {
  const { sx, children, ...others } = props;
  return (
    <Tooltip
      ref={ref}
      size="sm"
      sx={{
        maxWidth: '20rem',
        ...sx,
      }}
      {...others}
    >
      {children}
    </Tooltip>
  );
});

export default OptionTooltip;
