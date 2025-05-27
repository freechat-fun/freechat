import { Typography, styled } from '@mui/material';

const HistoryTypography = styled(Typography)(({ theme }) => ({
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap',
  p: theme.spacing(1),
}));

export default HistoryTypography;
