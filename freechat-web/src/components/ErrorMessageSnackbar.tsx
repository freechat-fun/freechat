import { Fragment, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useErrorMessageBusContext } from '../contexts';
import {
  IconButton,
  Snackbar,
  Stack,
  Typography,
  Button,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import { ClearRounded } from '@mui/icons-material';

export default function ErrorMessageSnackbar() {
  const theme = useTheme();
  const { t } = useTranslation();
  const { messages, removeMessage } = useErrorMessageBusContext();

  const [showDetails, setShowDetails] = useState(false);

  const message = messages && messages.length > 0 ? messages[0] : undefined;
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  return (
    <>
      {messages && (
        <Snackbar
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: isMobile ? 'center' : 'right',
          }}
          open={!!message}
          onClose={() => message && removeMessage(message)}
          action={
            <IconButton
              onClick={() => {
                if (message) {
                  removeMessage(message);
                }
                setShowDetails(false);
              }}
            >
              <ClearRounded />
            </IconButton>
          }
          sx={{
            zIndex: 10000,
            mx: isMobile ? 4 : 8,
          }}
          message={
            <Fragment>
              <Button
                color="warning"
                sx={{
                  display: showDetails ? 'none' : 'flex',
                  justifyContent: 'space-around',
                  alignItems: 'center',
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  right: 0,
                  bottom: 0,
                  textDecoration: 'none',
                  '&:hover, &:focus-within': {
                    textDecoration: 'none',
                    bgcolor: 'transparent',
                  },
                }}
                onClick={(event) => {
                  event.preventDefault();
                  if (isMobile) {
                    if (message) {
                      removeMessage(message);
                    }
                  } else {
                    setShowDetails(true);
                  }
                }}
              >
                <Typography>{t(`Error!`)}</Typography>
                <Typography>{`[${message?.code}]`}</Typography>
              </Button>
              <Stack
                sx={{
                  display: showDetails && message ? 'flex' : 'none',
                }}
              >
                <Typography variant="body1">{`${t('Exception')}: ${message?.code}`}</Typography>
                <Typography
                  sx={{
                    whiteSpace: 'pre-wrap',
                    p: 1,
                  }}
                >
                  {message?.message}
                </Typography>
              </Stack>
            </Fragment>
          }
        ></Snackbar>
      )}
    </>
  );
}
