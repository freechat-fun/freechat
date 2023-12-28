import { Card, styled } from "@mui/joy";

const OptionCard = styled(Card)(() => ({
  borderColor: 'transparent',
  boxShadow: 'none',
  '&:hover': { borderColor: 'var(--joy-palette-primary-outlinedBorder)' },
  p: 0,
  gap: 0,
}));

export default OptionCard;