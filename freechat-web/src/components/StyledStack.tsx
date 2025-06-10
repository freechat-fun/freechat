import { Stack, styled } from '@mui/material';

const StyledStack = styled(Stack)(({ theme }) => ({
  margin: theme.spacing(2),
  padding: theme.spacing(2),
  boxShadow: theme.shadows[1],
  borderRadius: '6px',
  border: `1px solid ${theme.palette.divider}`,
  transition: 'transform 0.4s, box-shadow 0.4s',
  gap: theme.spacing(2),
  [theme.breakpoints.down('sm')]: {
    marginLeft: theme.spacing(0),
    marginRight: theme.spacing(0),
  },
  '&:hover, &:focus-within': {
    boxShadow: theme.shadows[3],
    transform: 'translateY(-2px)',
  },
}));

export default StyledStack;
