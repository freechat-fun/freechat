import { forwardRef } from 'react';
import { useBlocker } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Typography } from '@mui/material';
import { ConfirmModal } from '.';
import { LaunchRounded } from '@mui/icons-material';

type RouterBlockerProps = {
  when?: boolean;
  title?: string;
  message?: string;
};

const RouterBlocker = forwardRef<HTMLDivElement, RouterBlockerProps>(
  (props, ref) => {
    const { when = false, title, message } = props;

    const { t } = useTranslation();

    const blocker = useBlocker(
      ({ currentLocation, nextLocation }) =>
        when && currentLocation.pathname !== nextLocation.pathname
    );

    return (
      <ConfirmModal
        ref={ref}
        open={blocker.state === 'blocked'}
        dialog={{
          title: title || t('Confirm to Leave'),
          color: 'error',
        }}
        button={{
          startIcon: <LaunchRounded />,
        }}
        onClose={() => blocker.reset?.()}
        onConfirm={() => blocker.proceed?.()}
      >
        <Typography>
          {message ||
            t('You have unsaved changes. Are you sure you want to leave?')}
        </Typography>
      </ConfirmModal>
    );
  }
);

export default RouterBlocker;
