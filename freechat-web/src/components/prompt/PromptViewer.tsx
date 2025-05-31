/* eslint-disable @typescript-eslint/no-explicit-any */
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import {
  Box,
  Button,
  ButtonGroup,
  Chip,
  Divider,
  Typography,
} from '@mui/material';
import {
  ArrowBackRounded,
  ChatBubbleOutlineRounded,
  ContentCopyRounded,
  EditRounded,
  PlayCircleOutlineRounded,
  PublicOffRounded,
  PublicRounded,
  Title,
} from '@mui/icons-material';
import {
  CommonContainer,
  LinePlaceholder,
  OptionTooltip,
} from '../../components';
import { PromptDetailsDTO } from 'freechat-sdk';
import { getDateLabel } from '../../libs/date_utils';
import {
  PromptContent,
  PromptMeta,
  PromptRunner,
} from '../../components/prompt';
import { extractVariables, getMessageText } from '../../libs/template_utils';

type PromptViewerProps = {
  id: number | undefined;
  parameters?: { [key: string]: any };
  variables?: { [key: string]: any };
};

export default function PromptViewer({
  id,
  parameters,
  variables,
}: PromptViewerProps) {
  const navigate = useNavigate();
  const { t, i18n } = useTranslation(['prompt', 'button']);
  const { promptApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useMetaInfoContext();

  const [record, setRecord] = useState<PromptDetailsDTO>();
  const [history, setHistory] = useState<[string, number][]>([]);
  const [play, setPlay] = useState(false);
  const [defaultParameters, setDefaultParameters] = useState(
    parameters ? { ...parameters } : undefined
  );
  const [defaultVariables, setDefaultVariables] = useState(
    variables ? { ...variables } : undefined
  );
  const [defaultOutputText, setDefaultOutputText] = useState<string>();

  useEffect(() => {
    if (id) {
      promptApi
        ?.getPromptDetails(id)
        .then((resp) => {
          setRecord(resp);
          if (resp?.name) {
            promptApi
              ?.listPromptVersionsByName(resp?.name)
              .then((resp) =>
                resp.map(
                  (item) =>
                    [`v${item.version}`, item.promptId] as [string, number]
                )
              )
              .then((ids) => setHistory(ids))
              .catch(handleError);
          }
        })
        .catch(handleError);
    }
  }, [handleError, id, promptApi]);

  useEffect(() => {
    if (!record) {
      return;
    }

    setDefaultVariables(extractVariables(record));

    if (record.ext) {
      try {
        const persistentParameters = JSON.parse(record.ext) as {
          [key: string]: any;
        };
        setDefaultParameters(persistentParameters);
      } catch {
        // ignore
      }
    }
  }, [record]);

  useEffect(() => {
    setDefaultParameters(parameters ? { ...parameters } : undefined);
  }, [parameters]);

  useEffect(() => {
    setDefaultVariables(variables ? { ...variables } : undefined);
  }, [variables]);

  function handleEdit(): void {
    if (id) {
      navigate(`/w/prompt/edit/${id}`);
    }
  }

  function handleCopy(): void {
    if (id) {
      promptApi
        ?.clonePrompt(id)
        .then((resp) => {
          if (resp) {
            navigate(`/w/prompt/edit/${resp}`);
          }
        })
        .catch(handleError);
    }
  }

  return (
    <>
      <LinePlaceholder />
      <CommonContainer
        sx={{
          alignItems: { xs: 'flex-start', sm: 'flex-end' },
          justifyContent: 'flex-end',
        }}
      >
        <Box
          sx={{
            justifySelf: 'flex-start',
            display: 'flex',
            alignItems: 'center',
            flex: 1,
            gap: 2,
          }}
        >
          <Typography variant="h4">{record?.name}</Typography>
          <Chip
            label={`v${record?.version}`}
            color="success"
            variant="filled"
            size="small"
          />
          <OptionTooltip
            title={
              record?.type === 'chat' ? 'chat template' : 'string template'
            }
            sx={{ mt: 1 }}
          >
            <Box component="span">
              {record?.type === 'chat' ? (
                <ChatBubbleOutlineRounded color="info" fontSize="small" />
              ) : (
                <Title color="info" fontSize="small" />
              )}
            </Box>
          </OptionTooltip>
          <OptionTooltip title={record?.visibility} sx={{ mt: 1 }}>
            <Box component="span">
              {record?.visibility === 'public' ? (
                <PublicRounded color="info" fontSize="small" />
              ) : (
                <PublicOffRounded color="info" fontSize="small" />
              )}
            </Box>
          </OptionTooltip>
        </Box>
        <Typography variant="body2">
          {t('Updated on')}{' '}
          {getDateLabel(
            record?.gmtModified || new Date(0),
            i18n.language,
            true
          )}
        </Typography>

        <ButtonGroup
          size="small"
          variant="contained"
          sx={{
            borderRadius: '16px',
            mb: 0.5,
            mr: 2,
          }}
        >
          <Button
            startIcon={<ContentCopyRounded fontSize="small" />}
            onClick={handleCopy}
            sx={{
              borderRadius: '16px',
            }}
          >
            {t('button:Copy')}
          </Button>
          {record?.username === username && (
            <Button startIcon={<EditRounded />} onClick={handleEdit}>
              {t('button:Edit')}
            </Button>
          )}
          {play ? (
            <Button
              startIcon={<ArrowBackRounded />}
              onClick={() => setPlay(false)}
              sx={{
                borderRadius: '16px',
              }}
            >
              {t('button:Back')}
            </Button>
          ) : (
            <Button
              startIcon={<PlayCircleOutlineRounded />}
              onClick={() => setPlay(true)}
              sx={{
                borderRadius: '16px',
              }}
            >
              {t('Try it', { ns: 'button' })}
            </Button>
          )}
        </ButtonGroup>
      </CommonContainer>
      <Divider />
      <CommonContainer sx={{ flex: 1, alignItems: 'flex-start' }}>
        <PromptContent record={record} />
        {play ? (
          <PromptRunner
            minWidth="16rem"
            maxWidth="50%"
            apiPath="/api/v2/prompt/send/stream"
            record={record}
            defaultVariables={defaultVariables}
            defaultParameters={defaultParameters}
            defaultOutputText={defaultOutputText}
            onPlaySuccess={(request, response) => {
              if (request?.params && Object.keys(request?.params).length > 0) {
                setDefaultParameters(request?.params);
              } else {
                setDefaultParameters(undefined);
              }

              if (
                request?.promptRef?.variables &&
                Object.keys(request?.promptRef?.variables).length > 0
              ) {
                setDefaultVariables(request?.promptRef?.variables);
              } else if (
                request?.promptTemplate?.variables &&
                Object.keys(request?.promptTemplate?.variables).length > 0
              ) {
                setDefaultVariables(request?.promptTemplate?.variables);
              } else {
                setDefaultVariables(undefined);
              }

              setDefaultOutputText(
                getMessageText(response?.message) ?? response?.text
              );
            }}
            onPlayFailure={(request, error) => {
              if (request?.params && Object.keys(request?.params).length > 0) {
                setDefaultParameters(request?.params);
              } else {
                setDefaultParameters(undefined);
              }

              if (
                request?.promptRef?.variables &&
                Object.keys(request?.promptRef?.variables).length > 0
              ) {
                setDefaultVariables(request?.promptRef?.variables);
              } else if (
                request?.promptTemplate?.variables &&
                Object.keys(request?.promptTemplate?.variables).length > 0
              ) {
                setDefaultVariables(request?.promptTemplate?.variables);
              } else {
                setDefaultVariables(undefined);
              }

              setDefaultOutputText(error?.message ?? error?.code ?? '');
            }}
          />
        ) : (
          <PromptMeta record={record} history={history} />
        )}
      </CommonContainer>
    </>
  );
}
