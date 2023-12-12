import { IconButton, Snackbar, Stack, Typography } from "@mui/joy";
import { useErrorMessageBusContext } from "../context";
import { useTranslation } from 'react-i18next';
import { Clear, ErrorOutline } from "@mui/icons-material";

export default function ErrorMessageSnackbar() {
  const { t } = useTranslation();
  const { messages, removeMessage } = useErrorMessageBusContext();
  const message = messages && messages.length > 0 ? messages[0] : undefined;

  return (
    <>
      {messages &&
        <Snackbar
          open={!!message}
          onClose={() => message && removeMessage(message)}
          color="danger"
          startDecorator={<ErrorOutline />}
          endDecorator={
            <IconButton onClick={() => message && removeMessage(message)}>
              <Clear />
            </IconButton>
        }>
          {message &&
            <Stack>
              <Typography level="title-md">
                {`${t('Exception')}: ${message.code}`}
              </Typography>
              <Typography>
                {message.message}
              </Typography>
            </Stack>
          }
        </Snackbar>
      }
    </>
  );
}