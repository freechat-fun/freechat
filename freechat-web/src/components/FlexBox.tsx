import { Box, styled } from '@mui/material';

const FlexBox = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'flex-start',
  alignItems: 'center',
  flexWrap: 'wrap',
  gap: theme.spacing(2),
}));

export default FlexBox;
