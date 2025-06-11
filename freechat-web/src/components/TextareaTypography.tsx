import { Typography, TypographyProps } from '@mui/material';
import { styled } from '@mui/material/styles';

const TextareaTypography = styled(Typography)<TypographyProps>(({ theme }) => ({
  border: `1px solid ${theme.palette.primary.main}`,
  backgroundColor: theme.palette.background.default,
  padding: theme.spacing(1, 2),
  borderRadius: '6px',
  whiteSpace: 'pre-wrap',
  color: theme.palette.text.primary,
}));

export default TextareaTypography;
