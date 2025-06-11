import { forwardRef } from 'react';
import { TextField, TextFieldProps, useTheme } from '@mui/material';

const TinyInput = forwardRef<HTMLInputElement, TextFieldProps>((props, ref) => {
  const { sx, slotProps, ...others } = props;
  const theme = useTheme();

  return (
    <TextField
      inputRef={ref}
      size="small"
      slotProps={{
        input: {
          size: 'small',
          sx: { fontSize: 'small' },
        },
        ...slotProps,
      }}
      sx={{
        minWidth: theme.spacing(10),
        maxWidth: theme.spacing(15),
        m: theme.spacing(0),
        p: theme.spacing(0),
        alignSelf: 'start',
        borderRadius: '6px',
        ...sx,
      }}
      {...others}
    />
  );
});

TinyInput.displayName = 'TinyInput';

export default TinyInput;
