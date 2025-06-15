import { Box } from '@mui/material';
import { styled } from '@mui/material/styles';

const GridBox = styled(Box)(({ theme }) => ({
  width: '100%',
  display: 'grid',
  gridTemplateColumns: 'auto 1fr',
  alignItems: 'center',
  gap: theme.spacing(2),
}));

export default GridBox;
