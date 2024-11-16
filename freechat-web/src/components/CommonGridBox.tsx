import { Box, styled } from '@mui/joy';

const CommonGridBox = styled(Box)(({ theme }) => ({
  width: '100%',
  display: 'grid',
  gridTemplateColumns: 'auto 1fr',
  alignItems: 'center',
  gap: theme.spacing(2),
}));

export default CommonGridBox;
