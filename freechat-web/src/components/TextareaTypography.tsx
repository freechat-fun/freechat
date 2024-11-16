import { Typography, TypographyProps, styled } from '@mui/joy';

const TextareaTypography = styled(Typography)<TypographyProps>(({ theme }) => ({
  border: `1px solid ${theme.palette.primary.outlinedBorder}`,
  backgroundColor: theme.palette.background.body,
  padding: theme.spacing(1, 2),
  borderRadius: '6px',
  whiteSpace: 'pre-wrap',
  color: theme.palette.text.primary,
}));

export default TextareaTypography;
