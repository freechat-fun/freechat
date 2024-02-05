import { Card, styled } from "@mui/joy";

const OptionCard = styled(Card)(({ theme }) => ({
  borderColor: 'transparent',
  boxShadow: 'none',
  '&:hover': { borderColor: theme.palette.primary.outlinedBorder },
  p: 0,
  gap: 0,
}));

export default OptionCard;