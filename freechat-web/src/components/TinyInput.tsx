import { Input, styled } from '@mui/joy';

const TinyInput = styled(Input)(({ theme }) => ({
  minWidth: theme.spacing(8.5),
  maxWidth: theme.spacing(15),
  margin: theme.spacing(0),
  padding: theme.spacing(0),
  alignSelf: 'start',
  '& input': {
    padding: `${theme.spacing(0.5)} ${theme.spacing(1)}`,
    fontSize: '0.875rem',
    minHeight: '1rem',
  },
}));

export default TinyInput;
