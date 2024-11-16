import { Typography, styled } from '@mui/joy';

const HistoryTypography = styled(Typography)(({ theme }) => ({
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
  p: theme.spacing(1),
}));

export default HistoryTypography;
