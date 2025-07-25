import { Box, styled } from '@mui/material';

const DynamicFlexBox = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'flex-start',
  alignItems: 'center',
  flexWrap: 'wrap',
  gap: theme.spacing(1),
  [theme.breakpoints.only('xs')]: {
    flexDirection: 'column',
    gap: theme.spacing(1),
  },
  [theme.breakpoints.up('sm')]: {
    flexDirection: 'row',
    gap: theme.spacing(2),
  },
}));

export default DynamicFlexBox;
