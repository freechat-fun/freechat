import { forwardRef } from 'react';
import { TextField, TextFieldProps } from '@mui/material';

const ContentTextarea = forwardRef<HTMLInputElement, TextFieldProps>(
  (props, ref) => {
    return (
      <TextField
        ref={ref}
        multiline
        slotProps={{
          input: {
            sx: {
              whiteSpace: 'pre-wrap',
              overflowWrap: 'break-word',
            },
          },
        }}
        sx={{
          flex: 1,
        }}
        {...props}
      />
    );
  }
);

ContentTextarea.displayName = 'ContentTextarea';

export default ContentTextarea;
