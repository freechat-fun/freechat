/* eslint-disable @typescript-eslint/no-explicit-any */
import { Fragment, useCallback, useEffect, useRef, useState } from 'react';
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
  Card,
  Chip,
  Divider,
  FormControl,
  FormHelperText,
  IconButton,
  InputAdornment,
  InputLabel,
  ListSubheader,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  Switch,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  styled,
} from '@mui/material';
import {
  AddCircleRounded,
  ArrowBackRounded,
  CancelOutlined,
  CheckCircleOutlineRounded,
  CheckRounded,
  EditRounded,
  InfoOutlined,
  IosShareRounded,
  PlayCircleOutlineRounded,
  RemoveCircleOutlineRounded,
  SaveAltRounded,
  TransitEnterexitRounded,
} from '@mui/icons-material';
import {
  CommonBox,
  CommonContainer,
  CommonGridBox,
  ConfirmModal,
  ContentTextarea,
  LinePlaceholder,
  OptionTooltip,
  RouterBlocker,
  TinyInput,
} from '../../components';
import {
  AiModelInfoDTO,
  ChatMessageDTO,
  ChatPromptContentDTO,
  LlmResultDTO,
  PromptAiParamDTO,
  PromptDetailsDTO,
  PromptTemplateDTO,
  PromptUpdateDTO,
} from 'freechat-sdk';
import { formatDate, getDateLabel } from '../../libs/date_utils';
import { PromptRunner } from '../../components/prompt';
import { locales } from '../../configs/i18n-config';
import {
  extractVariables,
  generateExample,
  getMessageText,
  setMessageText,
} from '../../libs/template_utils';
import { providers } from '../../configs/model-providers-config';
import { HelpIcon } from '../../components/icon';
import { objectsEqual } from '../../libs/js_utils';
import { Theme } from '@mui/material';

type MessageRound = {
  user: ChatMessageDTO;
  assistant: ChatMessageDTO;
};

type PromptEditorProps = {
  id: number | undefined;
  parameters?: { [key: string]: any };
  variables?: { [key: string]: any };
};

const TypographyContent = styled(Typography)(() => ({
  whiteSpace: 'pre-wrap',
  overflowWrap: 'break-word',
  flex: 1,
}));

export default function PromptEditor({
  id,
  parameters,
  variables,
}: PromptEditorProps) {
  const navigator = useNavigate();
  const { t, i18n } = useTranslation(['prompt', 'button']);
  const { promptApi, aiServiceApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useMetaInfoContext();

  const [play, setPlay] = useState(false);
  const [defaultParameters, setDefaultParameters] = useState(
    parameters ? { ...parameters } : undefined
  );
  const [defaultVariables, setDefaultVariables] = useState(
    variables ? { ...variables } : undefined
  );
  const [defaultOutputText, setDefaultOutputText] = useState<string>();
  const [origRecord, setOrigRecord] = useState(new PromptDetailsDTO());
  const [editRecordName, setEditRecordName] = useState<string | null>(null);
  const [editRecordNameError, setEditRecordNameError] = useState(false);

  const [recordName, setRecordName] = useState<string>();
  const [description, setDescription] = useState<string>();
  const [inputs, setInputs] = useState<{ [key: string]: any }>();
  const [stringTemplate, setStringTemplate] = useState<string>();
  const [system, setSystem] = useState<string>();
  const [userName, setUserName] = useState<string>();
  const [userMessage, setUserMessage] = useState<string>();
  const [messages, setMessages] = useState<ChatMessageDTO[]>([]);
  const [example, setExample] = useState<string>();

  const [visibility, setVisibility] = useState<string>();
  const [format, setFormat] = useState<string>('mustache');
  const [lang, setLang] = useState<string>('en');
  const [tags, setTags] = useState<string[]>([]);
  const [tag, setTag] = useState<string>();
  const [models, setModels] = useState<AiModelInfoDTO[]>([]);
  const [modelId, setModelId] = useState<string>();
  const [modelInfos, setModelInfos] = useState<AiModelInfoDTO[]>([]);

  const [rounds, setRounds] = useState<MessageRound[]>([]);
  const [editRound, setEditRound] = useState(false);
  const [editUserName, setEditUserName] = useState<string | undefined>('user');
  const [editAssistantName, setEditAssistantName] = useState<
    string | undefined
  >('assistant');
  const [editUserContent, setEditUserContent] = useState<string>();
  const [editAssistantContent, setEditAssistantContent] = useState<string>();

  const originName = useRef<string>(undefined);
  const systemRef = useRef<HTMLDivElement>(null);
  const messageRef = useRef<HTMLDivElement>(null);

  const FORMATS = [
    {
      label: 'Mustache',
      value: 'mustache',
    },
    {
      label: 'F-String',
      value: 'f_string',
    },
  ];

  const roundItemStyle = (theme: Theme) => ({
    width: '100%',
    mr: 'auto',
    justifyContent: 'flex-end',
    border: `1px solid ${theme.palette.divider}`,
    backgroundColor: theme.palette.background.default,
    padding: theme.spacing(1, 2),
    borderRadius: '12px',
  });

  const getEditRecord = useCallback(
    (inputsJson: string | undefined) => {
      const newRecord = new PromptDetailsDTO();
      newRecord.promptId = id;
      newRecord.name = recordName;
      newRecord.description = description;
      newRecord.type = origRecord.type;
      newRecord.chatTemplate = new ChatPromptContentDTO();
      newRecord.chatTemplate.system = system;
      newRecord.chatTemplate.messages = [...messages];
      newRecord.chatTemplate.messageToSend = new ChatMessageDTO();
      newRecord.chatTemplate.messageToSend.role = 'user';
      setMessageText(newRecord.chatTemplate.messageToSend, userMessage);
      newRecord.template = stringTemplate;
      newRecord.example = example;
      newRecord.inputs = inputsJson;
      newRecord.visibility = visibility;
      newRecord.format = format;
      newRecord.lang = lang;
      newRecord.tags = [...tags];
      newRecord.aiModels = [...models];
      newRecord.promptUid = origRecord.promptUid;

      return newRecord;
    },
    [
      description,
      example,
      format,
      id,
      lang,
      messages,
      models,
      origRecord.promptUid,
      origRecord.type,
      recordName,
      stringTemplate,
      system,
      tags,
      userMessage,
      visibility,
    ]
  );

  useEffect(() => {
    if (id) {
      promptApi?.getPromptDetails(id).then(setOrigRecord).catch(handleError);
    }

    aiServiceApi?.listAiModelInfo().then(setModelInfos).catch(handleError);
  }, [handleError, id, promptApi, aiServiceApi]);

  useEffect(() => {
    const { hash } = window.location;
    const delay = 200;
    let handler: number | undefined;

    if (hash === '#system' && systemRef.current) {
      handler = setTimeout(
        () => systemRef.current?.scrollIntoView({ behavior: 'smooth' }),
        delay
      );
    } else if (hash === '#messages' && messageRef.current) {
      handler = setTimeout(
        () => messageRef.current?.scrollIntoView({ behavior: 'smooth' }),
        delay
      );
    }

    return () => {
      if (handler) {
        clearTimeout(handler);
      }
    };
  }, [origRecord]);

  useEffect(() => {
    setDefaultParameters(parameters ? { ...parameters } : undefined);
  }, [parameters]);

  useEffect(() => {
    setDefaultVariables(variables ? { ...variables } : undefined);
  }, [variables]);

  useEffect(() => {
    if (origRecord) {
      if (origRecord.username !== username) {
        return;
      }
      let draft: PromptDetailsDTO = {};
      if (origRecord.draft) {
        try {
          draft = (JSON.parse(origRecord.draft) as PromptDetailsDTO) ?? {};
        } catch {
          console.warn(`[WARNING] Invalid draft content: ${origRecord.draft}`);
        }
      }

      const draftRecord = { ...origRecord, ...draft };

      setRecordName(draftRecord.name);
      setDescription(draftRecord.description);
      setStringTemplate(draftRecord.template);
      setSystem(draftRecord.chatTemplate?.system);
      setUserName(draftRecord.chatTemplate?.messageToSend?.name ?? 'user');
      setUserMessage(
        draftRecord.chatTemplate?.messageToSend?.contents?.[0]?.content ||
          (draftRecord.format === 'f_string' ? '{input}' : '{{{input}}}')
      );
      setMessages(draftRecord.chatTemplate?.messages ?? []);
      setExample(draftRecord.example);

      setVisibility(draftRecord.visibility ?? 'private');
      setFormat(draftRecord.format ?? 'mustache');
      setLang(draftRecord.lang ? draftRecord.lang.split('_')[0] : 'en');
      setTags(draftRecord.tags ?? []);
      setModels(draftRecord.aiModels ?? []);
      if (draftRecord.ext) {
        try {
          const persistentParameters = JSON.parse(draftRecord.ext) as {
            [key: string]: any;
          };
          setDefaultParameters(persistentParameters);
        } catch {
          // ignore
        }
      }
      setInputs(extractVariables(draftRecord));

      originName.current = draftRecord.name;
    }
  }, [origRecord, username]);

  useEffect(() => {
    if (originName.current) {
      setInputs((prevInputs) =>
        extractVariables(getEditRecord(JSON.stringify(prevInputs)))
      );
    }
  }, [getEditRecord, userMessage, stringTemplate]);

  useEffect(() => {
    if (originName.current) {
      setRounds(messagesToRounds(messages));
      setInputs((prevInputs) =>
        extractVariables(getEditRecord(JSON.stringify(prevInputs)))
      );
    }
  }, [getEditRecord, messages]);

  useEffect(() => {
    if (originName.current) {
      setDefaultVariables((prevVariables) => {
        const filteredInputs: { [key: string]: any } = {};
        Object.keys(prevVariables ?? {}).forEach((k) => {
          if (k in (inputs ?? {}) && prevVariables?.k) {
            filteredInputs[k] = prevVariables.k;
          }
        });
        return { ...inputs, ...filteredInputs };
      });
    }
  }, [inputs]);

  useEffect(() => {
    if (originName.current) {
      setUserMessage((prevUserMessage) => {
        if (format === 'mustache' && prevUserMessage === '{input}') {
          return '{{{input}}}';
        } else if (format === 'f_string' && prevUserMessage === '{{{input}}}') {
          return '{input}';
        } else {
          return prevUserMessage;
        }
      });
    }
  }, [format]);

  function handlePlaySuccess(
    request: PromptAiParamDTO | undefined,
    response: LlmResultDTO | undefined
  ): void {
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

    setDefaultOutputText(getMessageText(response?.message) ?? response?.text);
  }

  function handlePlayFailure(
    request: PromptAiParamDTO | undefined,
    error: any
  ): void {
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
  }

  function handleNameChange(): void {
    if (editRecordNameError) {
      return;
    }

    if (editRecordName && editRecordName !== recordName) {
      if (editRecordName === originName.current) {
        setRecordName(editRecordName);
        setEditRecordName(null);
      } else {
        promptApi
          ?.existsPromptName(editRecordName)
          .then((resp) => {
            if (!resp) {
              setRecordName(editRecordName);
              setEditRecordName(null);
            } else {
              setEditRecordNameError(true);
            }
          })
          .catch(handleError);
      }
    } else {
      setEditRecordNameError(false);
      setEditRecordName(null);
    }
  }

  function handleRoundRemoved(round: MessageRound) {
    const newMessages = messages.filter(
      (message) => message !== round.assistant && message !== round.user
    );
    setMessages(newMessages);
  }

  function handleRoundEdit(): void {
    if (rounds.length > 0) {
      const round = rounds[rounds.length - 1];
      setEditUserName(
        (round.user.name || round.user.role || 'user').toUpperCase()
      );
      setEditAssistantName(
        (
          round.assistant.name ||
          round.assistant.role ||
          'assistant'
        ).toUpperCase()
      );
    }
    setEditRound(true);
  }

  function handleRoundCommit(): void {
    const currentUserMessage = new ChatMessageDTO();
    currentUserMessage.role = 'user';
    currentUserMessage.name = editUserName;
    setMessageText(currentUserMessage, editUserContent);

    const currentAssistantMessage = new ChatMessageDTO();
    currentAssistantMessage.role = 'assistant';
    currentAssistantMessage.name = editAssistantName;
    setMessageText(currentAssistantMessage, editAssistantContent);

    setMessages([...messages, currentUserMessage, currentAssistantMessage]);
    setEditRound(false);
  }

  function handleInputsChange(k: string, v: string): void {
    const currentInputs = { ...(inputs ?? {}) };
    currentInputs[k] = v;
    setInputs(currentInputs);
  }

  function handleTagDelete(tagDeleted: string): void {
    if (tags) {
      setTags(tags.filter((tag) => tagDeleted !== tag));
    }
  }

  function handleModelDelete(modelIdDeleted: string): void {
    if (models) {
      setModels(models.filter((model) => modelIdDeleted !== model.modelId));
    }
  }

  function handleTagSubmit(
    event: React.FormEvent<HTMLFormElement> | undefined
  ): void {
    if (event) {
      event.preventDefault();
    }
    if (tags && tag && !tags.includes(tag)) {
      setTags([...tags, tag]);
    }
    setTag(undefined);
  }

  function handleModelSubmit(): void {
    const modelInfo = modelInfos.find(
      (modelInfo) => modelInfo.modelId === modelId
    );
    if (
      modelInfo &&
      models &&
      !models.find((model) => modelId === model.modelId)
    ) {
      setModels([...models, modelInfo]);
    }
    setModelId(undefined);
  }

  function handleExampleGenerate(
    request: PromptAiParamDTO | undefined,
    response: LlmResultDTO | undefined
  ): void {
    if (!request || !response) {
      return;
    }
    const req = { ...request };
    const onRendered = (renderedRequest: PromptAiParamDTO) => {
      const newExample = generateExample(renderedRequest, response);
      if (newExample) {
        setExample(newExample);
      }
    };

    if (req.prompt) {
      onRendered(req);
    } else if (req.promptTemplate) {
      promptApi
        ?.applyPromptTemplate(req.promptTemplate)
        .then((resp) => {
          if (!resp) {
            return;
          }
          if (req.promptTemplate?.template) {
            req.promptTemplate.template = resp;
          } else if (req.promptTemplate?.chatTemplate) {
            req.promptTemplate.chatTemplate = JSON.parse(
              resp
            ) as ChatPromptContentDTO;
          }

          onRendered(req);
        })
        .catch(handleError);
    } else if (req.promptRef) {
      promptApi
        ?.applyPromptRef(req.promptRef)
        .then((resp) => {
          if (!resp) {
            return;
          }
          const promptTemplate = new PromptTemplateDTO();
          if (req.promptRef?.variables?.containsKey('{input}')) {
            promptTemplate.format = 'f_string';
          } else {
            promptTemplate.format = 'mustache';
          }
          try {
            promptTemplate.chatTemplate = JSON.parse(
              resp
            ) as ChatPromptContentDTO;
          } catch {
            promptTemplate.template = resp;
          }
          promptTemplate.variables = req.promptRef?.variables;

          req.promptTemplate = promptTemplate;
          req.promptRef = undefined;

          onRendered(req);
        })
        .catch(handleError);
    }
  }

  function handleRecordSave(): void {
    if (!id) {
      return;
    }
    const draftRecord = getEditRecord(JSON.stringify(inputs));
    draftRecord.promptId = undefined;
    draftRecord.draft = undefined;

    const request = new PromptUpdateDTO();
    request.draft = JSON.stringify(draftRecord);

    promptApi
      ?.updatePrompt(id, request)
      .then((resp) => {
        if (!resp) {
          return;
        }
        promptApi?.getPromptDetails(id).then(setOrigRecord).catch(handleError);
      })
      .catch(handleError);
  }

  function handleRecordPublish(): void {
    if (!id) {
      return;
    }
    const onUpdated = (currentId: number, visibility: string) => {
      promptApi
        ?.publishPrompt(currentId, visibility)
        .then((resp) => {
          if (!resp) {
            return;
          }
          navigator(`/w/prompt/${resp}`);
        })
        .catch(handleError);
    };

    const editRecord = getEditRecord(JSON.stringify(inputs));

    const request = recordToUpdateRequest(editRecord);
    promptApi
      ?.updatePrompt(id, request)
      .then((resp) => {
        if (resp) {
          promptApi
            ?.getPromptDetails(id)
            .then((newRecord) => {
              setOrigRecord(newRecord);
              onUpdated(
                id,
                editRecord.visibility === 'private' ? 'private' : 'public'
              );
            })
            .catch(handleError);
        }
      })
      .catch(handleError);
  }

  function filterModels(provider?: string): AiModelInfoDTO[] {
    return modelInfos
      ? modelInfos.filter((modelInfo) => {
          if (!modelInfo || (provider && modelInfo.provider !== provider)) {
            return false;
          }
          switch (origRecord.type) {
            case 'chat': {
              return modelInfo.type === 'text2chat';
            }
            case 'string': {
              return modelInfo.type === 'text2text';
            }
            default: {
              return false;
            }
          }
        })
      : [];
  }

  function messagesToRounds(
    messages: ChatMessageDTO[] | undefined
  ): MessageRound[] {
    const rounds: MessageRound[] = [];

    if (!messages) {
      return rounds;
    }

    for (let i = 0; i < messages.length; i = i + 2) {
      if (i + 1 < messages.length) {
        let assistant: ChatMessageDTO;
        let user: ChatMessageDTO;
        if (messages[i].role === 'user') {
          user = messages[i];
          assistant = messages[i + 1];
        } else {
          assistant = messages[i];
          user = messages[i + 1];
        }

        const round: MessageRound = {
          assistant: assistant,
          user: user,
        };

        rounds.push(round);
      }
    }

    return rounds;
  }

  function recordToUpdateRequest(record: PromptDetailsDTO): PromptUpdateDTO {
    const request = new PromptUpdateDTO();
    request.aiModels = record.aiModels?.map(
      (modelInfo) => modelInfo.modelId as string
    );
    request.chatTemplate = { ...record.chatTemplate };
    request.description = record.description;
    request.example = record.example;
    request.ext = record.ext;
    request.format = record.format;
    request.inputs = record.inputs;
    request.lang = record.lang;
    request.name = record.name ?? `untitiled-${formatDate(new Date())}`;
    request.tags = record.tags;
    request.template = record.template;
    request.draft = '';
    request.ext = JSON.stringify(defaultParameters ?? {});
    request.visibility = record.visibility === 'private' ? 'private' : 'public';

    return request;
  }

  function isSaved(): boolean {
    if (origRecord.username !== username) {
      return false;
    }
    let draft: PromptDetailsDTO = {};
    if (origRecord.draft) {
      try {
        draft = (JSON.parse(origRecord.draft) as PromptDetailsDTO) ?? {};
      } catch {
        console.warn(`[WARNING] Invalid draft content: ${origRecord.draft}`);
      }
    }

    const draftRecord = { ...origRecord, ...draft };

    return (
      objectsEqual(draftRecord.name, recordName) &&
      objectsEqual(draftRecord.description, description) &&
      objectsEqual(draftRecord.template, stringTemplate) &&
      objectsEqual(draftRecord.chatTemplate?.system, system) &&
      objectsEqual(
        draftRecord.chatTemplate?.messageToSend?.name ?? 'user',
        userName
      ) &&
      objectsEqual(
        draftRecord.chatTemplate?.messageToSend?.contents?.[0]?.content ||
          (draftRecord.format === 'f_string' ? '{input}' : '{{{input}}}'),
        userMessage
      ) &&
      objectsEqual(draftRecord.chatTemplate?.messages ?? [], messages) &&
      objectsEqual(draftRecord.example, example) &&
      objectsEqual(draftRecord.visibility ?? 'private', visibility) &&
      objectsEqual(draftRecord.format ?? 'mustache', format) &&
      objectsEqual(
        draftRecord.lang ? draftRecord.lang.split('_')[0] : 'en',
        lang
      ) &&
      objectsEqual(draftRecord.tags ?? [], tags) &&
      objectsEqual(draftRecord.aiModels ?? [], models)
    );
  }

  return (
    <>
      <LinePlaceholder />
      <Box
        sx={{
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          alignItems: { xs: 'flex-start', sm: 'flex-end' },
          gap: { xs: 1, sm: 2 },
          justifyContent: 'flex-end',
        }}
      >
        <CommonContainer
          sx={{
            alignItems: 'center',
            flex: 1,
          }}
        >
          <Typography variant="h4">{recordName}</Typography>
          <IconButton
            size="small"
            disabled={!!editRecordName}
            onClick={() => setEditRecordName(recordName || 'untitled')}
          >
            <EditRounded fontSize="small" />
          </IconButton>
        </CommonContainer>
        <Typography variant="body2">
          {t('Updated on')}{' '}
          {getDateLabel(
            origRecord?.gmtModified || new Date(0),
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
            disabled={isSaved() || visibility === 'hidden'}
            startIcon={isSaved() ? <CheckRounded /> : <SaveAltRounded />}
            onClick={handleRecordSave}
            sx={{
              borderRadius: '16px',
            }}
          >
            {t('button:Save')}
          </Button>
          <Button startIcon={<IosShareRounded />} onClick={handleRecordPublish}>
            {t('button:Publish')}
          </Button>
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
      </Box>

      <Divider />

      <CommonContainer sx={{ alignItems: 'flex-start' }}>
        <CommonContainer sx={{ flex: 1, alignItems: 'flex-start' }}>
          <Stack
            spacing={3}
            sx={{
              minWidth: { sm: '16rem' },
              mt: 2,
              flex: 1,
            }}
          >
            <Stack
              spacing={1}
              sx={{
                minWidth: { sm: '12rem' },
              }}
            >
              <CommonBox>
                <Typography variant="h6" color="primary">
                  {t('Description')}
                </Typography>
                <OptionTooltip
                  placement="right"
                  title={t('Supports markdown format')}
                >
                  <HelpIcon />
                </OptionTooltip>
              </CommonBox>
              <ContentTextarea
                name="info-description"
                minRows={3}
                value={description}
                onChange={(event) => setDescription(event.target.value)}
              />
            </Stack>
            <LinePlaceholder />

            {origRecord?.type === 'chat' && origRecord?.chatTemplate ? (
              <Fragment>
                <Stack
                  spacing={1}
                  sx={{
                    minWidth: { sm: '12rem' },
                  }}
                >
                  <Chip
                    label="SYSTEM"
                    ref={systemRef}
                    color="info"
                    variant="filled"
                    size="small"
                    sx={{ mt: 1, mb: 2, alignSelf: 'flex-start' }}
                  />
                  <ContentTextarea
                    name="system"
                    minRows={3}
                    value={system}
                    onChange={(event) => setSystem(event.target.value)}
                  />
                </Stack>
                <Stack
                  spacing={1}
                  sx={{
                    minWidth: { sm: '12rem' },
                  }}
                >
                  <CommonBox ref={messageRef}>
                    <Chip label="MESSAGES" variant="filled" color="warning" />
                    <OptionTooltip
                      placement="right"
                      title={t(
                        'These messages will always be used as the starting message in the chat history'
                      )}
                    >
                      <HelpIcon />
                    </OptionTooltip>
                    <IconButton
                      disabled={editRound}
                      color="primary"
                      onClick={() => handleRoundEdit()}
                    >
                      <AddCircleRounded />
                    </IconButton>
                  </CommonBox>
                  {rounds.length > 0 && (
                    <Fragment>
                      <Divider sx={{ mx: 2 }} />
                      {rounds?.map((round, index) => {
                        return (
                          <CommonBox key={`round-${index}`} sx={roundItemStyle}>
                            <CommonGridBox
                              sx={{
                                flex: 1,
                                gap: 1,
                              }}
                            >
                              <Chip
                                size="small"
                                variant="outlined"
                                color="success"
                                label={(
                                  round.user.name ||
                                  round.user.role ||
                                  'user'
                                ).toUpperCase()}
                              />
                              <TypographyContent>
                                {getMessageText(round.user)}
                              </TypographyContent>
                              <Chip
                                size="small"
                                variant="outlined"
                                color="warning"
                                label={(
                                  round.assistant.name ||
                                  round.assistant.role ||
                                  'assistant'
                                ).toUpperCase()}
                              />
                              <TypographyContent>
                                {getMessageText(round.assistant)}
                              </TypographyContent>
                            </CommonGridBox>
                            <IconButton
                              sx={{
                                mr: 4,
                              }}
                              onClick={() => handleRoundRemoved(round)}
                            >
                              <RemoveCircleOutlineRounded />
                            </IconButton>
                          </CommonBox>
                        );
                      })}
                    </Fragment>
                  )}
                  {editRound && (
                    <CommonBox sx={roundItemStyle}>
                      <Box
                        sx={{
                          flex: 1,
                          display: 'grid',
                          gridTemplateColumns: 'auto 1fr',
                          gap: 1,
                        }}
                      >
                        {rounds.length > 0 ? (
                          <Chip
                            size="small"
                            variant="filled"
                            color="success"
                            label={editUserName}
                          />
                        ) : (
                          <TinyInput
                            name="editUserName"
                            value={editUserName}
                            onChange={(event) =>
                              setEditUserName(event.target.value)
                            }
                          />
                        )}
                        <ContentTextarea
                          name="editUserMessage"
                          value={editUserContent}
                          onChange={(event) =>
                            setEditUserContent(event.target.value)
                          }
                        />
                        {rounds.length > 0 ? (
                          <Chip
                            size="small"
                            variant="filled"
                            color="warning"
                            label={editAssistantName}
                          />
                        ) : (
                          <TinyInput
                            name="editAssistantName"
                            value={editAssistantName}
                            onChange={(event) =>
                              setEditAssistantName(event.target.value)
                            }
                          />
                        )}
                        <ContentTextarea
                          name="editAssistantMessage"
                          value={editAssistantContent}
                          onChange={(event) =>
                            setEditAssistantContent(event.target.value)
                          }
                        />
                      </Box>
                      <IconButton
                        sx={{
                          mx: 1,
                        }}
                        onClick={() => handleRoundCommit()}
                      >
                        <CheckCircleOutlineRounded />
                      </IconButton>
                      <IconButton
                        sx={{
                          mr: 4,
                        }}
                        onClick={() => setEditRound(false)}
                      >
                        <CancelOutlined />
                      </IconButton>
                    </CommonBox>
                  )}
                </Stack>
                <Stack
                  spacing={1}
                  sx={{
                    minWidth: { sm: '12rem' },
                  }}
                >
                  <Chip
                    label={userName?.toUpperCase() ?? 'USER'}
                    color="success"
                    variant="filled"
                    size="small"
                    sx={{ mt: 1, mb: 2, alignSelf: 'flex-start' }}
                  />
                  <ContentTextarea
                    name="user"
                    value={userMessage}
                    onChange={(event) => setUserMessage(event.target.value)}
                  />
                </Stack>
              </Fragment>
            ) : (
              <Stack
                spacing={1}
                sx={{
                  minWidth: { sm: '12rem' },
                }}
              >
                <Typography variant="h6" color="primary">
                  {t('Template')}
                </Typography>
                <ContentTextarea
                  name="string-template"
                  minRows={3}
                  value={stringTemplate}
                  onChange={(event) => setStringTemplate(event.target.value)}
                />
              </Stack>
            )}

            <LinePlaceholder />
            <CommonBox>
              <Typography variant="h6" color="primary">
                {t('Inputs')}
              </Typography>
              <OptionTooltip
                placement="right"
                title={t(
                  "If an input's value is not specified at runtime, the default value set here will be used"
                )}
              >
                <HelpIcon />
              </OptionTooltip>
            </CommonBox>
            {inputs && Object.keys(inputs).length > 0 && (
              <TableContainer>
                <Table size="small" sx={{ mx: 1 }}>
                  <TableHead>
                    <TableRow>
                      <TableCell
                        sx={{
                          width: '30%',
                          maxWidth: '50%',
                          backgroundColor: 'action.hover',
                        }}
                      >
                        {t('Placeholder')}
                      </TableCell>
                      <TableCell sx={{ backgroundColor: 'action.hover' }}>
                        {t('Default value')}
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {Object.entries(inputs).map(([k, v]) => (
                      <TableRow key={`input-${k}`}>
                        <TableCell>{k}</TableCell>
                        <TableCell>
                          <TinyInput
                            disabled={k === 'input'}
                            name={`input-value-${k}`}
                            value={v}
                            multiline
                            fullWidth
                            onChange={(event) =>
                              handleInputsChange(k, event.target.value)
                            }
                            slotProps={{
                              input: {
                                size: 'small',
                              },
                            }}
                            sx={{
                              maxWidth: undefined,
                            }}
                          />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}

            <Stack
              spacing={1}
              sx={{
                minWidth: { sm: '12rem' },
              }}
            >
              <CommonBox>
                <Typography variant="h6" color="primary">
                  {t('Example')}
                </Typography>
                <OptionTooltip
                  placement="right"
                  title={t('Supports markdown format')}
                >
                  <HelpIcon />
                </OptionTooltip>
              </CommonBox>
              <ContentTextarea
                name="example"
                minRows={3}
                value={example}
                onChange={(event) => setExample(event.target.value)}
              />
            </Stack>
            <LinePlaceholder spacing={2} />
          </Stack>

          {/* Meta Settings */}
          {!play && (
            <Card
              sx={{
                width: { xs: '100%', sm: '20rem' },
                my: 2,
                mx: { xs: 0, sm: 2 },
                p: 2,
                boxShadow: 2,
                border: 1,
                borderColor: 'divider',
              }}
            >
              <CommonGridBox>
                <Typography variant="subtitle1" color="text.secondary">
                  {t('Public')}
                </Typography>
                <Switch
                  color="success"
                  checked={visibility === 'public' || visibility === 'hidden'}
                  disableRipple
                  onChange={(event) =>
                    event.target.checked
                      ? setVisibility('public')
                      : setVisibility('private')
                  }
                />

                <Typography variant="subtitle1" color="text.secondary">
                  {t('Format')}
                </Typography>

                <ButtonGroup
                  size="small"
                  variant="contained"
                  sx={{
                    border: 0,
                    boxShadow: 0,
                    borderRadius: 2,
                  }}
                >
                  {FORMATS.map((item) => (
                    <Button
                      key={`format-${item.value}`}
                      value={item.value}
                      color="info"
                      onClick={() => setFormat(item.value)}
                      size="small"
                      sx={{
                        fontSize: 'small',
                        borderRadius: 2,
                        color:
                          format === item.value ? 'text.default' : 'GrayText',
                        backgroundColor:
                          format === item.value ? 'primary' : 'transparent',
                      }}
                    >
                      {item.label}
                    </Button>
                  ))}
                </ButtonGroup>

                <Typography variant="subtitle1" color="text.secondary">
                  {t('Language')}
                </Typography>
                <FormControl sx={{ flex: 1 }} size="small">
                  <Select
                    variant="outlined"
                    value={lang}
                    onChange={(event) =>
                      event.target.value && setLang(event.target.value)
                    }
                  >
                    {Object.keys(locales).map((locale) => (
                      <MenuItem key={`locale-${locale}`} value={locale}>
                        {locales[locale]}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </CommonGridBox>

              <CommonBox sx={{ mt: 4, mb: 2 }}>
                <Typography variant="subtitle1" color="text.secondary">
                  {t('Tags')}
                </Typography>
                {(!tags || tags.length < 5) && tag === undefined && (
                  <IconButton
                    size="small"
                    color="primary"
                    onClick={() => setTag('')}
                  >
                    <AddCircleRounded />
                  </IconButton>
                )}
                {tag !== undefined && (
                  <form onSubmit={handleTagSubmit}>
                    <TinyInput
                      type="text"
                      value={tag}
                      onChange={(event) => setTag(event.target.value)}
                      onBlur={() => handleTagSubmit(undefined)}
                      slotProps={{
                        input: {
                          size: 'small',
                          sx: { fontSize: 'small' },
                          endAdornment: (
                            <InputAdornment position="end">
                              <TransitEnterexitRounded fontSize="small" />
                            </InputAdornment>
                          ),
                        },
                      }}
                    />
                  </form>
                )}
              </CommonBox>
              <CommonBox>
                {tags.length > 0 &&
                  tags.map((tag, index) => (
                    <Chip
                      label={tag}
                      variant="outlined"
                      color="success"
                      key={`tag-${tag}-${index}`}
                      onDelete={() => handleTagDelete(tag)}
                    />
                  ))}
              </CommonBox>
              <LinePlaceholder spacing={2} />

              <CommonBox sx={{ mt: 4, mb: 1 }}>
                <Typography variant="subtitle1" color="text.secondary">
                  {t('Models')}
                </Typography>
                {modelId === undefined && (
                  <IconButton
                    size="small"
                    color="primary"
                    onClick={() => setModelId('')}
                  >
                    <AddCircleRounded />
                  </IconButton>
                )}
              </CommonBox>
              {models.length > 0 && (
                <CommonBox>
                  {models.map((model, index) => (
                    <Chip
                      label={model.name}
                      variant="outlined"
                      color="warning"
                      key={`model-${model.modelId}-${index}`}
                      onDelete={() => handleModelDelete(model.modelId ?? '')}
                    />
                  ))}
                </CommonBox>
              )}
              {modelId !== undefined && (
                <Fragment>
                  <CommonBox>
                    <FormControl size="small" sx={{ flex: 1, mt: 2 }}>
                      <InputLabel id="choose-model-label">
                        {t('Choose model')}
                      </InputLabel>
                      <Select
                        label={t('Choose model')}
                        variant="outlined"
                        value={modelId}
                        onChange={(event: SelectChangeEvent) =>
                          event.target.value && setModelId(event.target.value)
                        }
                      >
                        {providers.reduce<React.ReactNode[]>(
                          (acc, provider, index) => {
                            if (index !== 0) {
                              acc.push(
                                <Divider
                                  sx={{ my: 1 }}
                                  key={`divider-${index}`}
                                />
                              );
                            }
                            acc.push(
                              <ListSubheader key={`header-${index}`}>
                                <Typography variant="body2">
                                  {provider.label}
                                </Typography>
                              </ListSubheader>
                            );
                            filterModels(provider.provider).forEach(
                              (modelInfo) => {
                                acc.push(
                                  <MenuItem
                                    key={`option-${modelInfo.modelId}`}
                                    value={modelInfo.modelId}
                                    sx={{
                                      ml: 2,
                                    }}
                                  >
                                    {modelInfo.name}
                                  </MenuItem>
                                );
                              }
                            );
                            return acc;
                          },
                          []
                        )}
                      </Select>
                    </FormControl>
                  </CommonBox>
                  <CommonBox sx={{ justifyContent: 'flex-end' }}>
                    <IconButton onClick={handleModelSubmit}>
                      <CheckCircleOutlineRounded fontSize="small" />
                    </IconButton>
                    <IconButton onClick={() => setModelId(undefined)}>
                      <CancelOutlined fontSize="small" />
                    </IconButton>
                  </CommonBox>
                </Fragment>
              )}
            </Card>
          )}
        </CommonContainer>

        {play && (
          <PromptRunner
            minWidth="16rem"
            maxWidth="50%"
            apiPath="/api/v2/prompt/send/stream"
            record={getEditRecord(JSON.stringify(inputs))}
            defaultVariables={defaultVariables}
            defaultParameters={defaultParameters}
            defaultOutputText={defaultOutputText}
            onPlaySuccess={handlePlaySuccess}
            onPlayFailure={handlePlayFailure}
            onExampleSave={handleExampleGenerate}
          />
        )}
      </CommonContainer>
      <ConfirmModal
        open={editRecordName !== null}
        onClose={() => setEditRecordName(null)}
        dialog={{
          title: t('Please enter a new name'),
        }}
        button={{
          text: t('button:Save'),
          startIcon: <SaveAltRounded />,
        }}
        onConfirm={handleNameChange}
      >
        <FormControl error={editRecordNameError}>
          <TinyInput
            name="RecordName"
            value={editRecordName ?? ''}
            onChange={(event) => {
              setEditRecordName(event.target.value);
              setEditRecordNameError(false);
            }}
          />
          {editRecordNameError && (
            <FormHelperText>
              <InfoOutlined />
              {t('Name already exists!')}
            </FormHelperText>
          )}
        </FormControl>
      </ConfirmModal>

      <RouterBlocker
        when={!isSaved()}
        message={t(
          'You may have unsaved changes. Are you sure you want to leave?'
        )}
      />
    </>
  );
}
