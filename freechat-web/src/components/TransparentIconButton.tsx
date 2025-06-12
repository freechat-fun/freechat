// TransparentIconButton.tsx
import React from 'react';
import { IconButton, IconButtonProps } from '@mui/material';
import { styled } from '@mui/material/styles';

const StyledTransparentIconButton = styled(IconButton)(() => ({
  backgroundColor: 'transparent',
  '&:hover': {
    backgroundColor: 'transparent',
  },
  '&:active': {
    backgroundColor: 'transparent',
  },
  '&:focus': {
    backgroundColor: 'transparent',
  },
  '&.Mui-focusVisible': {
    backgroundColor: 'transparent',
    outline: 'none',
  },
}));

const TransparentIconButton = React.forwardRef<
  HTMLButtonElement,
  IconButtonProps
>((props, ref) => {
  const disableRippleProp =
    props.disableRipple !== undefined ? props.disableRipple : true;

  return (
    <StyledTransparentIconButton
      {...props}
      ref={ref}
      disableRipple={disableRippleProp}
    >
      {props.children}
    </StyledTransparentIconButton>
  );
});

TransparentIconButton.displayName = 'TransparentIconButton';

export default TransparentIconButton;
