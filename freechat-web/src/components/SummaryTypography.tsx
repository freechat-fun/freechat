import { Typography, TypographyProps, styled } from "@mui/joy";

const SummaryTypography = styled(Typography)<TypographyProps>(() => ({
  display: '-webkit-box',
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  WebkitBoxOrient: 'vertical',
  WebkitLineClamp: 3,
  maxHeight: '4.5rem',
  minHeight: '4.5rem',
  lineHeight: '1.5rem',
  whiteSpace: 'pre-wrap',
}));

export default SummaryTypography;
