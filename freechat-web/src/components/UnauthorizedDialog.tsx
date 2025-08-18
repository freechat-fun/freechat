import { useTranslation } from 'react-i18next';
import {
  Link as MuiLink,
  Dialog,
  DialogContent,
  Typography,
  Box,
} from '@mui/material';
import { useMetaInfoContext } from '../contexts';

export default function UnauthorizedDialog() {
  const { t } = useTranslation('account');
  const { isAuthorized } = useMetaInfoContext();
  const { pathname } = window.location;

  const normalizePathname = pathname.replace(/\/+$/, '');
  const publicPaths = [null, '', '/w', '/w/login', '/w/docs', '/w/privacy-policy'];

  return (
    <Dialog
      open={!isAuthorized() && !publicPaths.includes(normalizePathname)}
      maxWidth="sm"
    >
      <DialogContent>
        <Box
          sx={{
            display: 'flex',
            justifyContent: 'center',
            whiteSpace: 'pre-wrap',
            gap: 1,
          }}
        >
          <Typography>
            {t('You are not signed in yet.')} {t('Please')}
          </Typography>
          <MuiLink href="/w/login" color="primary">
            {t('sign in')}
          </MuiLink>
        </Box>
      </DialogContent>
    </Dialog>
  );
}
