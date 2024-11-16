import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useErrorMessageBusContext } from '../contexts';
import { IconButton, Link, Snackbar, Stack, Typography } from '@mui/joy';
import { ClearRounded, ErrorOutlineRounded } from '@mui/icons-material';

export default function ErrorMessageSnackbar() {
  const { t } = useTranslation();
  const { messages, removeMessage } = useErrorMessageBusContext();

  const [showDetails, setShowDetails] = useState(false);

  const message = messages && messages.length > 0 ? messages[0] : undefined;

  return (
    <>
      {messages && (
        <Snackbar
          open={!!message}
          onClose={() => message && removeMessage(message)}
          color="danger"
          startDecorator={<ErrorOutlineRounded />}
          endDecorator={
            <IconButton onClick={() => message && removeMessage(message)}>
              <ClearRounded />
            </IconButton>
          }
          sx={{
            zIndex: 10000,
          }}
        >
          {!showDetails ? (
            <Link
              overlay
              variant="plain"
              color="danger"
              sx={{
                textDecoration: 'none',
                '&:hover': {
                  textDecoration: 'none',
                  bgcolor: 'transparent',
                },
              }}
              onClick={(event) => {
                event.preventDefault();
                setShowDetails(true);
              }}
            >
              {t('Error!')}
            </Link>
          ) : (
            message && (
              <Stack>
                <Typography level="title-md">{`${t('Exception')}: ${message.code}`}</Typography>
                <Typography
                  sx={{
                    whiteSpace: 'pre-wrap',
                  }}
                >
                  {message.message}
                </Typography>
              </Stack>
            )
          )}
        </Snackbar>
      )}
    </>
  );
}
