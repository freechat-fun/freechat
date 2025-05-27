import { Box, styled } from '@mui/material';

const OptionCard = styled(Box)(({ theme }) => ({
  border: '1px solid transparent',
  borderRadius: theme.shape.borderRadius * 2,
  boxShadow: 'none',
  '&:hover': {
    borderColor: theme.palette.primary.main,
  },
  padding: 16,
  display: 'flex',
  flexDirection: 'column',
  gap: 0,
}));

export default OptionCard;
