import { Box, styled } from "@mui/joy";

const CommonBox = styled(Box)(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'flex-start',
  flexWrap: 'wrap',
  gap: theme.spacing(1),
}));

export default CommonBox;