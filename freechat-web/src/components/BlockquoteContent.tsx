import { Box, Theme } from '@mui/material';
import { PropsWithChildren } from 'react';

const BlockquoteContent: React.FC<PropsWithChildren> = ({ children }) => {
  return (
    <Box
      component="blockquote"
      sx={(theme: Theme) => ({
        borderLeft: 4,
        borderColor: theme.palette.divider,
        paddingLeft: 2,
        marginLeft: 0,
        color: theme.palette.text.secondary,
      })}
    >
      {children}
    </Box>
  );
};

export default BlockquoteContent;
