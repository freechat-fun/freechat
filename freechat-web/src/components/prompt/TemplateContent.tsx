import { Fragment } from 'react';
import { Box, Chip, Divider, Stack, Typography } from '@mui/material';
import { PromptDetailsDTO, ChatMessageDTO } from 'freechat-sdk';
import { getMessageText } from '../../libs/template_utils';

export default function TemplateContent(props: {
  record: PromptDetailsDTO | undefined;
}) {
  const { record } = props;
  const contentStyle = {
    whiteSpace: 'pre-wrap',
    overflowWrap: 'break-word',
  };

  if (record?.type === 'chat' && record?.chatTemplate) {
    const defaultInput =
      record?.format === 'f_string' ? '{input}' : '{{{input}}}';
    const template = record?.chatTemplate;
    const system = template?.system;
    const userName = template?.messageToSend?.name || 'user';
    const userMessage = getMessageText(template?.messageToSend) || defaultInput;
    const messages: ChatMessageDTO[] = template?.messages ?? [];

    return (
      <Fragment>
        <Stack sx={{ p: 2, border: 'none', boxShadow: 'none' }}>
          <Chip
            label="SYSTEM"
            color="info"
            variant="filled"
            size="small"
            sx={{ mt: 1, mb: 2 }}
          />
          <Typography variant="body1" sx={contentStyle}>
            {system}
          </Typography>
        </Stack>
        {messages.length > 0 && (
          <Fragment>
            <Divider sx={{ mx: 2 }} />
            <Box
              sx={{
                display: 'grid',
                gridTemplateColumns: 'auto 1fr',
                p: 2,
              }}
            >
              {messages?.map((message, index) => {
                const isAssistant = message.role === 'assistant';
                const name = (
                  message.name ||
                  message.role ||
                  'user'
                ).toUpperCase();
                const content = getMessageText(message) || '';

                return (
                  <Fragment key={`message-${index}`}>
                    <Box sx={{ px: 2, pb: 2 }}>
                      <Chip
                        label={name}
                        color={isAssistant ? 'warning' : 'success'}
                        variant="outlined"
                        size="small"
                      />
                    </Box>
                    <Box sx={{ pb: 1 }}>
                      <Typography variant="body1" sx={contentStyle}>
                        {content}
                      </Typography>
                    </Box>
                  </Fragment>
                );
              })}
            </Box>
          </Fragment>
        )}
        <Divider sx={{ mx: 2 }} />
        <Stack sx={{ p: 2, border: 'none', boxShadow: 'none' }}>
          <Chip
            label={userName?.toUpperCase() ?? 'USER'}
            color="success"
            variant="filled"
            size="small"
            sx={{ mt: 1, mb: 2 }}
          />
          <Typography variant="body1" sx={contentStyle}>
            {userMessage}
          </Typography>
        </Stack>
      </Fragment>
    );
  } else {
    return (
      <Typography variant="body1" sx={contentStyle}>
        {record?.template || ' '}
      </Typography>
    );
  }
}
