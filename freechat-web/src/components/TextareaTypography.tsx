import { Typography, styled } from "@mui/joy";
import { TypographyProps } from '@mui/joy/Typography';

const TextareaTypography = styled(Typography)<TypographyProps>(({ theme }) => ({
  border: `1px solid ${theme.palette.primary.outlinedBorder}`,
  backgroundColor: theme.palette.background.body,
  padding: theme.spacing(1, 2),
  borderRadius: '6px',
  minHeight: '96px',
  color: theme.palette.text.primary,
}));

export default TextareaTypography;
