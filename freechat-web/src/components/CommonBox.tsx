import { Box, styled } from '@mui/material';

const CommonBox = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'flex-start',
  alignItems: 'center',
  flexWrap: 'wrap',
  gap: theme.spacing(2),
}));

export default CommonBox;
