import { Typography, styled } from "@mui/joy";

const TextareaTypography = styled(Typography)(({ theme }) => ({
  border: `1px solid ${theme.palette.primary.outlinedBorder}`,
  backgroundColor: theme.palette.background.body,
  padding: theme.spacing(1, 2),
  borderRadius: '6px',
  minHeight: '96px',
  color: theme.palette.text.primary,
}));

export default TextareaTypography;
