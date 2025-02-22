import { Fragment } from 'react';
import { Box, Card, Chip, Divider, Typography } from '@mui/joy';
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
        <Card sx={{ p: 2, border: 'none' }}>
          <Chip variant="soft" color="primary">
            SYSTEM
          </Chip>
          <Typography level="body-md" sx={contentStyle}>
            {system}
          </Typography>
        </Card>
        {messages.length > 0 && (
          <Fragment>
            <Divider sx={{ mx: 2 }} />
            <Box
              sx={{
                display: 'grid',
                gridTemplateColumns: 'auto 1fr',
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
                    <Box sx={{ px: 2, pb: 1 }}>
                      <Chip
                        variant="soft"
                        color={isAssistant ? 'warning' : 'success'}
                      >
                        {name}
                      </Chip>
                    </Box>
                    <Box sx={{ pb: 1 }}>
                      <Typography level="body-md" sx={contentStyle}>
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
        <Card sx={{ p: 2, border: 'none' }}>
          <Chip variant="soft" color="success">
            {userName.toUpperCase()}
          </Chip>
          <Typography level="body-md" sx={contentStyle}>
            {userMessage}
          </Typography>
        </Card>
      </Fragment>
    );
  } else {
    return (
      <Typography level="body-md" sx={contentStyle}>
        {record?.template || ' '}
      </Typography>
    );
  }
}
