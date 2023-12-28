import { Input, styled } from "@mui/joy";

const TinyInput = styled(Input)(({ theme }) => ({
  minWidth: theme.spacing(8.5),
  maxWidth: theme.spacing(15),
  width: 'auto',
  margin: theme.spacing(0.5),
  padding: theme.spacing(0),
  '& input': {
    padding: `${theme.spacing(0.5)} ${theme.spacing(1)}`,
    fontSize: '0.875rem',
    minHeight: '1.1876em',
  },
}));

export default TinyInput;