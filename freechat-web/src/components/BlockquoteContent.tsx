import { Box } from '@mui/joy';
import { PropsWithChildren } from 'react';

const BlockquoteContent: React.FC<PropsWithChildren> = ({ children }) => {
  return (
    <Box
      component="blockquote"
      sx={(theme) => ({
        borderLeft: 4,
        borderColor: theme.palette.divider,
        pl: 2,
        ml: 0,
        color: theme.palette.text.tertiary,
      })}
    >
      {children}
    </Box>
  );
};

export default BlockquoteContent;
