import { useTranslation } from 'react-i18next';
import { Link, Modal, ModalDialog, Typography } from '@mui/joy';
import { useMetaInfoContext } from '../contexts';

export default function UnauthorizedDialog() {
  const { t } = useTranslation('account');
  const { isAuthorized } = useMetaInfoContext();
  const { pathname } = window.location;

  const normalizePathname = pathname.replace(/\/+$/, '');
  const publicPaths = [null, '', '/w', '/w/login', '/w/docs'];

  return (
    <Modal open={!isAuthorized() && !publicPaths.includes(normalizePathname)}>
      <ModalDialog layout="center">
        <Typography
          sx={{
            display: 'flex',
            justifyContent: 'center',
            whiteSpace: 'pre-wrap',
          }}
        >
          {t('You are not signed in yet.')} {t('Please')}
          <Link href="/w/login">{t('sign in')}</Link>
        </Typography>
      </ModalDialog>
    </Modal>
  );
}
