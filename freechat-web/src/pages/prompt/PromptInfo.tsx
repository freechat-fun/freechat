import { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { useParams } from "react-router-dom";
import { Box, Button, Card, Chip, Divider, Stack, Tooltip, Typography } from "@mui/joy";
import { ChatBubbleOutlineRounded, PlayCircleOutlineRounded, PublicOffRounded, PublicRounded, Title } from "@mui/icons-material";
import { LinePlaceholder } from "../../components";
import { PromptDetailsDTO, ChatMessageDTO, AiModelInfoDTO } from "freechat-sdk";
import { getDateLabel } from "../../libs/date_utils";
import { getLocaleLabel } from "../../configs/i18n-config";

function TemplateContent(props: { record: PromptDetailsDTO | undefined }) {
  const { record } = props;
  const contentStyle = {
    whiteSpace: 'pre-wrap',
    overflowWrap: 'break-word',
  };

  if (record?.type === 'chat' && record?.chatTemplate) {
    const defaultInput = record?.format === 'f_string' ? '{input}' : '{{input}}';
    const template = record?.chatTemplate;
    const system = template?.system;
    const userName = template?.messagesToSend?.name || 'User';
    const userMessage = template?.messagesToSend?.content || defaultInput;
    const messages: ChatMessageDTO[] = template?.messages?.length ? template?.messages : [];

    messages.forEach(m => console.log(m));
    
    return (
      <Fragment>
        <Card sx={{ p: 2, border: 'none' }}>
          <Chip variant="soft" color="primary">System</Chip>
          <Typography level="body-md" sx={contentStyle}>
            {system}
          </Typography>
        </Card>
        {messages.length > 0 && (
          <Fragment>
            <Divider sx={{ mx: 2 }} />
            <Box sx={{
              display: 'grid',
              gridTemplateColumns: 'auto 1fr',
            }}>
            {
              messages?.map((message, index) => {
                const isAssistant = message.role === 'assistant';
                const name = message.name || message.role || 'user';
                const content = message.content || '';

                return (
                  <Fragment key={`message-${index}`}>
                    <Box sx={{ px: 2, pb: 1 }}>
                      <Chip variant="soft" color={isAssistant ? 'warning' : 'success'}>{name}</Chip>
                    </Box>
                    <Box sx={{ pb: 1 }}>
                      <Typography level="body-md" sx={contentStyle}>
                        {content}
                      </Typography>
                    </Box>
                  </Fragment>
                )}
              )
            }
            </Box>
          </Fragment>
          )
        }
        <Divider sx={{ mx: 2 }} />
        <Card sx={{ p: 2, border: 'none' }}>
          <Chip variant="soft" color="success">{userName}</Chip>
          <Typography level="body-md" sx={contentStyle}>
            {userMessage}
          </Typography>
        </Card>
      </Fragment>
    );
  } else {
    return (
      <Typography level="body-md"  sx={contentStyle}>
        {record?.template}
      </Typography>
    );
  }
}

function ContentPanel(props: { record: PromptDetailsDTO | undefined }) {
  const { record } = props;
  const { t } = useTranslation(['prompt']);

  return (
    <Stack spacing={3} sx={{
      minWidth: { sm: '16rem' },
      mt: 2,
      flex: 1,
    }}>
      <Card sx={{
        minWidth: { sm: '12rem' },
        p: 2,
        boxShadow: 'sm',
      }}>
        <Typography level="title-lg" color="primary">
          {t('Description')}
        </Typography>
        <Divider />
        <Typography level="body-md">
          {record?.description}
        </Typography>
      </Card>
      <Card sx={{
        minWidth: { sm: '12rem' },
        p: 2,
        boxShadow: 'sm',
      }}>
        <Typography level="title-lg" color="primary">
          {t('Template')}
        </Typography>
        <Divider />
        <TemplateContent record={record} />
      </Card>
      { record?.example && (
        <Card sx={{
          minWidth: { sm: '12rem' },
          p: 2,
        }}>
          <Typography level="title-lg" color="primary">
            {t('Example')}
          </Typography>
          <Divider />
          <Typography level="body-md">
            {record?.example}
          </Typography>
        </Card>
      )}
    </Stack>
  );
}

function InfoPanel(props: { record: PromptDetailsDTO | undefined }) {
  const { record } = props;
  const { t } = useTranslation(['prompt']);
  const format = record?.format;
  const lang = record?.lang;
  const tags: string[] = record?.tags || [];
  const models: AiModelInfoDTO[] = record?.aiModels || [];

  return (
    <Card sx={{
      minWidth: { sm: '12rem' },
      maxWidth: { sm: '16rem' },
      mt: 2,
      p: 2,
      boxShadow: 'sm',
    }}>
      {format && (
        <Fragment>
          <Typography level="title-sm" textColor="neutral">
            {t('Format')}
          </Typography>
          <Typography level="body-sm">
            {format === 'f_string' ? 'F-String: ' : 'Mustache: '}&nbsp;<b>{format === 'f_string' ? '{placeholder}' : '{{placeholder}}'}</b>
          </Typography>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {lang && (
        <Fragment>
          <Typography level="title-sm" textColor="neutral">
            {t('Language')}
          </Typography>
          <Typography level="body-sm">
            {getLocaleLabel(lang.split('_')[0]) || lang}
          </Typography>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {tags.length > 0 && (
        <Fragment>
          <Typography level="title-sm" textColor="neutral">
            {t('Tags')}
          </Typography>
          <Box sx={{
            display: 'flex',
            justifyContent: 'flex-start',
            alignItems: 'center',
            flexWrap: 'wrap',
            gap: 1,
          }}>
            {tags.map((tag, index) => (
              <Chip variant="outlined" color="success" key={`tag-${tag}-${index}`}>{tag}</Chip>
            ))}
          </Box>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {models.length > 0 && (
        <Fragment>
          <Typography level="title-sm" textColor="neutral">
            {t('Models')}
          </Typography>
          <Box sx={{
            display: 'flex',
            justifyContent: 'flex-start',
            alignItems: 'center',
            flexWrap: 'wrap',
            gap: 1,
          }}>
            {models.map((model, index) => (
              <Chip variant="outlined" color="warning" key={`model-${model.modelId}-${index}`}>{model.name}</Chip>
            ))}
          </Box>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}
    </Card>
  );
}

export default function PromptInfo() {
  const { id } = useParams();
  const { t, i18n } = useTranslation(['prompt', 'button']);
  const { promptApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [record, setRecord] = useState<PromptDetailsDTO>();

  useEffect(() => {
    getRecord();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [promptApi]);

  function getRecord() {
    id && promptApi?.getPromptDetails(id)
      .then(resp => setRecord(resp))
      .catch(handleError);
  }

  return (
    <>
      <LinePlaceholder />
      <Box sx={{
        display: 'flex',
        flexDirection: { xs: 'column', sm: 'row' },
        alignItems: { xs: 'flex-start', sm: 'flex-end' },
        gap: { xs: 1, sm: 2 },
        justifyContent: 'flex-end',
      }}>
        <Box sx={{
          justifySelf: 'flex-start',
          display: 'flex',
          alignItems: 'flex-end',
          flex: 1,
          gap: 2,
        }}>
          <Typography level="h3">{record?.name}</Typography>
          <Chip color="success" variant="soft">v{record?.version}</Chip>
          <Tooltip title={record?.type === 'chat' ? 'chat template' : 'string template'} size="sm">
            {record?.type === 'chat' ?
              <ChatBubbleOutlineRounded color="info" fontSize="small" /> :
              <Title color="info" fontSize="small" />
            }
          </Tooltip>
          <Tooltip title={record?.visibility} size="sm">
            {record?.visibility === 'public' ?
              <PublicRounded color="info" fontSize="small" /> :
              <PublicOffRounded color="info" />}
          </Tooltip>
        </Box>
        <Typography level="body-sm">
          {t('Updated on')} {getDateLabel(record?.gmtModified || new Date(0), i18n.language, true)}
        </Typography>
        <Button startDecorator={<PlayCircleOutlineRounded />}>
          {t('Try it')}
        </Button>
      </Box>
      <Divider />
      <Box sx={{
        display: 'flex',
        flexDirection: { xs: 'column', sm: 'row' },
        gap: { xs: 1, sm: 2 },
      }}>
        <ContentPanel record={record} />
        <InfoPanel record={record} />
      </Box>
    </>
  );
}