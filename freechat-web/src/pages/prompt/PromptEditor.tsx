/* eslint-disable @typescript-eslint/no-explicit-any */
import { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate, useParams } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext, useUserInfoContext } from "../../contexts";
import { Box, Button, ButtonGroup, Card, Chip, ChipDelete, Divider, FormControl, FormHelperText, IconButton, Input, List, ListDivider, ListItem, ListItemDecorator, Option, Radio, RadioGroup, Select, Stack, Switch, Table, Textarea, Theme, Tooltip, Typography, listItemDecoratorClasses, optionClasses, switchClasses } from "@mui/joy";
import { AddCircleRounded, ArrowBackRounded, CancelOutlined, CheckCircleOutlineRounded, CheckRounded, EditRounded, HelpOutlineRounded, InfoOutlined, IosShareRounded, PlayCircleOutlineRounded, RemoveCircleOutlineRounded, SaveAltRounded } from "@mui/icons-material";
import { CommonBox, ConfirmModal, LinePlaceholder, TinyInput } from "../../components";
import { AiModelInfoDTO, ChatMessageDTO, ChatPromptContentDTO, LlmResultDTO, PromptAiParamDTO, PromptDetailsDTO, PromptTemplateDTO, PromptUpdateDTO } from "freechat-sdk";
import { formatDate, getDateLabel } from "../../libs/date_utils";
import { PromptRunner } from "../../components/prompt";
import { locales } from "../../configs/i18n-config";
import { extractVariables, generateExample } from "../../libs/template_utils";
import { providers } from "../../configs/model-providers-config";

interface MessageRound {
  user: ChatMessageDTO;
  assistant: ChatMessageDTO;
}

export default function PromptEditor() {
  const navigator = useNavigate();
  const { id } = useParams();
  const { t, i18n } = useTranslation(['prompt', 'button']);
  const { promptApi, aiServiceApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useUserInfoContext();

  const [play, setPlay] = useState(false);
  const [defaultParameters, setDefaultParameters] = useState<{ [key: string]: any }>()
  const [defaultVariables, setDefaultVariables] = useState<{ [key: string]: any }>()
  const [defaultOutputText, setDefaultOutputText] = useState<string>();
  const [origRecord, setOrigRecord] = useState(new PromptDetailsDTO());
  const [editRecord, setEditRecord] = useState(new PromptDetailsDTO());
  const [originName, setOriginName] = useState<string>();
  const [editRecordName, setEditRecordName] = useState<string>();
  const [editRecordNameError, setEditRecordNameError] = useState(false);

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
  const [lang, setLang] = useState<string>();
  const [tags, setTags] = useState<string[]>([]);
  const [tag, setTag] = useState<string>();
  const [models, setModels] = useState<AiModelInfoDTO[]>([]);
  const [modelId, setModelId] = useState<string>();
  const [modelInfos, setModelInfos] = useState<AiModelInfoDTO[]>([]);

  const [rounds, setRounds] = useState<MessageRound[]>([]);
  const [editRound, setEditRound] = useState(false);
  const [editUserName, setEditUserName] = useState<string | undefined>('user');
  const [editAssistantName, setEditAssistantName] = useState<string | undefined>('assistant')
  const [editUserContent, setEditUserContent] = useState<string>();
  const [editAssistantContent, setEditAssistantContent] = useState<string>();
  const [saved, setSaved] = useState(false);

  const FORMATS = [
    {
      label: 'Mustache',
      value: 'mustache',
    },{
      label: 'F-String',
      value: 'f_string',
    }
  ]

  const contentStyle = {
    whiteSpace: 'pre-wrap',
    overflowWrap: 'break-word',
    flex: 1,
  };

  const roundItemStyle = (theme: Theme) => ({
    width: '100%',
    mr: 'auto',
    justifyContent: 'flex-end',
    border: `1px solid ${theme.palette.primary.outlinedBorder}`,
    backgroundColor: theme.palette.background.body,
    padding: theme.spacing(1, 2),
    borderRadius: '12px',
  });

  useEffect(() => {
    if (id) {
      promptApi?.getPromptDetails(id)
        .then(resp => {
          setOrigRecord(resp);
          setEditRecord(resp);
        })
        .catch(handleError);
    }

    aiServiceApi?.listAiModelInfo1()
      .then(setModelInfos)
      .catch(handleError);
  }, [handleError, id, username, promptApi, aiServiceApi]);

  useEffect(() => {
    if (origRecord) {
      if (origRecord.username !== username) {
        return;
      }
      
      setOriginName(origRecord.name);

      setDescription(origRecord.description);
      setStringTemplate(origRecord.template);
      setInputs(extractVariables(origRecord));
      setStringTemplate(origRecord.template);
      setSystem(origRecord.chatTemplate?.system);
      setUserName(origRecord.chatTemplate?.messageToSend?.name ?? 'user');
      setUserMessage(origRecord.chatTemplate?.messageToSend?.content ||
        (origRecord.format === 'f_string' ? '{input}' : '{{input}}'));
      setMessages(origRecord.chatTemplate?.messages ?? []);
      setExample(origRecord.example);

      setVisibility(origRecord.visibility ?? 'private');
      setFormat(origRecord.format ?? 'mustache');
      setLang(origRecord.lang ? origRecord.lang.split('_')[0] : 'en');
      setTags(origRecord.tags ?? []);
      setModels(origRecord.aiModels ?? []);
    }
  }, [origRecord, username]);

  useEffect(() => {
    setSaved(() => false);
  }, [editRecord]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      if (!newRecord.chatTemplate) {
        newRecord.chatTemplate = new ChatPromptContentDTO();
      }
      newRecord.chatTemplate.system = system;
      setInputs(extractVariables(newRecord));
      return newRecord;
    });
  }, [system]);

  useEffect(() => {
    setRounds(messagesToRounds(messages));
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      if (!newRecord.chatTemplate) {
        newRecord.chatTemplate = new ChatPromptContentDTO();
      }
      newRecord.chatTemplate.messages = messages;
      setInputs(extractVariables(newRecord));
      return newRecord;
    });
  }, [messages]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      if (!newRecord.chatTemplate) {
        newRecord.chatTemplate = new ChatPromptContentDTO();
      }
      if (!newRecord.chatTemplate.messageToSend) {
        newRecord.chatTemplate.messageToSend = new ChatMessageDTO();
        newRecord.chatTemplate.messageToSend.role = 'user';
      }
      newRecord.chatTemplate.messageToSend.content = userMessage;
      setInputs(extractVariables(newRecord));
      return newRecord;
    });
  }, [userMessage]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.template = stringTemplate;
      setInputs(extractVariables(newRecord));
      return newRecord;
    });
  }, [stringTemplate]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.example = example;
      setInputs(extractVariables(newRecord));
      return newRecord;
    });
  }, [example]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.inputs = JSON.stringify(inputs);
      return newRecord;
    });
  }, [inputs]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.visibility = visibility;
      return newRecord;
    });
  }, [visibility]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.format = format;
      setUserMessage(prevUserMessage => {
        if (format === 'mustache' && prevUserMessage === '{input}') {
          return '{{input}}';
        } else if (format === 'f_string' && prevUserMessage === '{{input}}') {
          return '{input}';
        } else {
          return prevUserMessage;
        }
      });
      setInputs(extractVariables(newRecord));
      return newRecord;
    });
  }, [format]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.lang = lang;
      return newRecord;
    });
  }, [lang]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.tags = tags;
      return newRecord;
    });
  }, [tags]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.aiModels = models;
      return newRecord;
    });
  }, [models]);

  function handlePlaySuccess(request: PromptAiParamDTO | undefined, response: LlmResultDTO | undefined): void {
    request?.params && Object.keys(request?.params).length > 0 ?
      setDefaultParameters(request?.params) : setDefaultParameters(undefined);

    if (request?.promptRef?.variables && Object.keys(request?.promptRef?.variables).length > 0) {
      setDefaultVariables(request?.promptRef?.variables); 
    } else if (request?.promptTemplate?.variables && Object.keys(request?.promptTemplate?.variables).length > 0) {
      setDefaultVariables(request?.promptTemplate?.variables);
    } else {
      setDefaultVariables(undefined);
    }

    setDefaultOutputText(response?.message?.content ?? response?.text);
  }

  function handleNameChange(): void {
    if (editRecordNameError) {
      return;
    }

    if (editRecordName && editRecordName !== editRecord?.name) {
      if (editRecordName === originName) {
        const newRecord = { ...editRecord };
        newRecord.name = editRecordName;
        setEditRecord(newRecord);
        setEditRecordName(undefined);
      } else {
        promptApi?.existsName(editRecordName)
          .then(resp => {
            if (!resp) {
              const newRecord = { ...editRecord };
              newRecord.name = editRecordName;
              setEditRecord(newRecord);
              setEditRecordName(undefined);
            } else {
              setEditRecordNameError(true);
            }
          })
          .catch(handleError);
      }
    } else {
      setEditRecordNameError(false);
      setEditRecordName(undefined);
    }
  }

  function handleRoundRemoved(round: MessageRound) {
    const newMessages = messages.filter(message => (message !== round.assistant) && (message !== round.user));
    setMessages(newMessages);
  }

  function handleRoundEdit(): void {
    if (rounds.length > 0) {
      const round = rounds[rounds.length - 1];
      setEditUserName((round.user.name || round.user.role || 'user').toUpperCase());
      setEditAssistantName((round.assistant.name || round.assistant.role || 'assistant').toUpperCase());
    }
    setEditRound(true);
  }

  function handleRoundCommit(): void {
    const currentUserMessage = new ChatMessageDTO();
    currentUserMessage.role = 'user',
    currentUserMessage.name = editUserName;
    currentUserMessage.content = editUserContent;
    currentUserMessage.gmtCreate = new Date();

    const currentAssistantMessage = new ChatMessageDTO();
    currentAssistantMessage.role = 'assistant',
    currentAssistantMessage.name = editAssistantName;
    currentAssistantMessage.content = editAssistantContent;
    currentAssistantMessage.gmtCreate = new Date();

    setMessages([...messages, currentUserMessage, currentAssistantMessage]);
    setEditRound(false);
  }

  function handleInputChange(k: string, v: string): void {
    const currentInputs = {...inputs ?? {}};
    currentInputs[k] = v;
    setInputs(currentInputs);
  }

  function handleTagDelete(tagDeleted: string): void {
    tags && setTags(tags.filter(tag => tagDeleted !== tag));
  }

  function handleModelDelete(modelIdDeleted: string): void {
    models && setModels(models.filter(model => modelIdDeleted !== model.modelId));
  }

  function handleTagSubmit(event: React.FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    tags && tag && !tags.includes(tag) && setTags([...tags, tag]);
    setTag(undefined);
  }

  function handleModelSubmit(): void {
    const modelInfo = modelInfos.find(modelInfo => modelInfo.modelId === modelId);
    modelInfo && models && !models.find(model => modelId === model.modelId) && setModels([...models, modelInfo]);
    setModelId(undefined);
  }

  function handleExampleGenerate(request: PromptAiParamDTO | undefined, response: LlmResultDTO | undefined): void {
    if (!request || !response) {
      return;
    }
    const req = {...request};
    const onRendered = ((renderedRequest: PromptAiParamDTO) => {
      const newExample = generateExample(renderedRequest, response);
      if (newExample) {
        setExample(newExample);
      }
    });
    
    if (req.prompt) {
      onRendered(req);
    } else if (req.promptTemplate) {
      promptApi?.applyPromptTemplate(req.promptTemplate)
        .then(resp => {
          if (!resp) {
            return;
          }
          if (req.promptTemplate?.template) {
            req.promptTemplate.template = resp;
          } else if (req.promptTemplate?.chatTemplate) {
            req.promptTemplate.chatTemplate = JSON.parse(resp) as ChatPromptContentDTO;
          }

          onRendered(req);
        })
        .catch(handleError);
    } else if (req.promptRef) {
      promptApi?.applyPromptRef(req.promptRef)
        .then(resp => {
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
            promptTemplate.chatTemplate = JSON.parse(resp) as ChatPromptContentDTO;
          } catch(error) {
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
    if (!editRecord.promptId) {
      return;
    }
    const request = recordToUpdateRequest(editRecord);
    promptApi?.updatePrompt(editRecord.promptId, request)
      .then(setSaved)
      .catch(handleError);
  }

  function handleRecordPublish(): void {
    if (!editRecord.promptId) {
      return;
    }
    const onSaved = (id: string, visibility: string) => {
      promptApi?.publishPrompt(id, visibility)
        .then(resp => {
          if (!resp) {
            return;
          }
          navigator(`/w/console/prompt/${resp}`);
        })
        .catch(handleError);
    };

    if (!saved) {
      const request = recordToUpdateRequest(editRecord);
        promptApi?.updatePrompt(editRecord.promptId, request)
          .then(resp => {
            setSaved(resp);
            if (resp) {
              onSaved(editRecord.promptId as string, editRecord.visibility ?? 'public');
            }
          })
          .catch(handleError);
    } else {
      onSaved(editRecord.promptId as string, editRecord.visibility ?? 'public');
    }
  }

  function filterModels(provider?: string): (AiModelInfoDTO)[] {
    return editRecord && modelInfos ? modelInfos.filter(modelInfo => {
      if (!modelInfo || (provider && modelInfo.provider !== provider)) {
        return false;
      }
      switch(editRecord.type) {
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
    }) : [];
  }

  function messagesToRounds(messages: ChatMessageDTO[] | undefined): MessageRound[] {
    const rounds: MessageRound[] = [];
  
    if (!messages) {
      return rounds;
    }
  
    for (let i = 0; i < messages.length; i = i + 2) {
      if ((i + 1) < messages.length) {
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
        }
  
        rounds.push(round);
      }
    }
  
    return rounds;
  }

  function recordToUpdateRequest(record: PromptDetailsDTO): PromptUpdateDTO {
    const request = new PromptUpdateDTO();
    request.aiModels = record.aiModels?.map(modelInfo => modelInfo.modelId as string);
    request.chatTemplate = record.chatTemplate;
    request.description = record.description;
    request.draft = record.draft;
    request.example = record.example;
    request.ext = record.ext;
    request.format = record.format;
    request.inputs = record.inputs;
    request.lang = record.lang;
    request.name = record.name ?? `untitiled-${formatDate(new Date())}`;
    request.tags = record.tags;
    request.template = record.template;
    if (origRecord.visibility !== 'hidden') {
      request.visibility = record.visibility;
    }

    return request;
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
        <CommonBox sx={{
          alignItems: 'center',
          flex: 1,
        }}>
          <Typography level="h3">{editRecord?.name}</Typography>
          <IconButton
            disabled={!!editRecordName}
            size="sm"
            onClick={() => setEditRecordName(editRecord?.name || 'untitled-1')}
          >
            <EditRounded fontSize="small" />
          </IconButton>
        </CommonBox>
        <Typography level="body-sm">
          {t('Updated on')} {getDateLabel(editRecord?.gmtModified || new Date(0), i18n.language, true)}
        </Typography>

        <ButtonGroup
          size="sm"
          variant="soft"
          color="primary"
          sx={{
          borderRadius: '16px',
        }}>
          <Button
            disabled={saved}
            startDecorator={saved ? <CheckRounded /> : <SaveAltRounded />}
            onClick={handleRecordSave}
          >
            {t('button:Save')}
          </Button>
          <Button
            startDecorator={<IosShareRounded />}
            onClick={handleRecordPublish}
          >
            {t('button:Publish')}
          </Button>
          {play ? (
            <Button
            startDecorator={<ArrowBackRounded />}
            onClick={() => setPlay(false)}
          >
            {t('button:Back')}
          </Button>
          ) : (
            <Button
              startDecorator={<PlayCircleOutlineRounded />}
              onClick={() => setPlay(true)}
            >
              {t('Try it', { ns: 'button' })}
            </Button>
          )}
        </ButtonGroup>
      </Box>
      <Divider />
      <CommonBox sx={{ alignItems: 'flex-start' }}>
        <CommonBox sx={{ flex: 1, alignItems: 'flex-start' }}>
          <Stack spacing={3} sx={{
            minWidth: { sm: '16rem' },
            mt: 2,
            flex: 1,
          }}>
            <Stack spacing={1} sx={{
              minWidth: { sm: '12rem' },
            }}>
              <CommonBox>
                <Typography level="title-lg" color="primary">
                  {t('Description')}
                </Typography>
                <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" placement="right" title={t('Supports Markdown format')}>
                  <HelpOutlineRounded fontSize="small" />
                </Tooltip>
              </CommonBox>
              <Textarea
                name="info-description"
                minRows={3}
                value={description}
                onChange={(event) => setDescription(event.target.value)}
              />
            </Stack>
            <LinePlaceholder />

            {origRecord?.type === 'chat' && origRecord?.chatTemplate ? (
              <Fragment>
                <Stack spacing={1} sx={{
                  minWidth: { sm: '12rem' },
                }}>
                  <Chip variant="soft" color="primary">SYSTEM</Chip>
                  <Textarea
                    name="system"
                    minRows={3}
                    sx={contentStyle}
                    value={system}
                    onChange={(event) => setSystem(event.target.value)}
                  />
                </Stack>
                <Stack spacing={1} sx={{
                  minWidth: { sm: '12rem' },
                }}>
                  <CommonBox>
                    <Chip variant="soft" color="primary">MESSAGES</Chip>
                    <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" placement="right" title={t('These messages will always be used as the starting message in the chat history')}>
                      <HelpOutlineRounded fontSize="small" />
                    </Tooltip>
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
                            <Box sx={{
                              flex: 1,
                              display: 'grid',
                              gridTemplateColumns: 'auto 1fr',
                              gap: 1,
                            }}>
                              <Chip variant="soft" color="success">{(round.user.name || round.user.role || 'user').toUpperCase()}</Chip>
                              <Typography level="body-md" sx={contentStyle}>
                                {round.user.content}
                              </Typography>
                              <Chip variant="soft" color="warning">{(round.assistant.name || round.assistant.role || 'assistant').toUpperCase()}</Chip>
                              <Typography level="body-md" sx={contentStyle}>
                                {round.assistant.content}
                              </Typography>
                            </Box>
                            <IconButton
                              sx={{
                                mr: 4,
                              }}
                              onClick={() => handleRoundRemoved(round)}
                            >
                              <RemoveCircleOutlineRounded />
                            </IconButton>
                          </CommonBox>
                        )
                      })}
                    </Fragment>
                  )}
                  {editRound && (
                    <CommonBox sx={roundItemStyle}>
                      <Box sx={{
                        flex: 1,
                        display: 'grid',
                        gridTemplateColumns: 'auto 1fr',
                        gap: 1,
                      }}>
                        {rounds.length > 0 ? (
                          <Chip variant="soft" color="success">{editUserName}</Chip>
                        ) : (
                          <TinyInput
                            name="editUserName"
                            value={editUserName}
                            onChange={(event) => setEditUserName(event.target.value)}
                          />
                        )}
                        <Textarea
                          name="editUserMessage"
                          sx={contentStyle}
                          value={editUserContent}
                          onChange={(event) => setEditUserContent(event.target.value)}
                        />
                        {rounds.length > 0 ? (
                          <Chip variant="soft" color="warning">{editAssistantName}</Chip>
                        ) : (
                          <TinyInput
                            name="editAssistantName"
                            value={editAssistantName}
                            onChange={(event) => setEditAssistantName(event.target.value)}
                          />
                        )}
                        <Textarea
                          name="editAssistantMessage"
                          sx={contentStyle}
                          value={editAssistantContent}
                          onChange={(event) => setEditAssistantContent(event.target.value)}
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
                <Stack spacing={1} sx={{
                  minWidth: { sm: '12rem' },
                }}>
                  <Chip variant="soft" color="success">{userName?.toUpperCase() ?? 'USER'}</Chip>
                  <Textarea
                    name="user"
                    sx={contentStyle}
                    value={userMessage}
                    onChange={(event) => setUserMessage(event.target.value)}
                  />
                </Stack>

              </Fragment>
            ) : (
              <Stack spacing={1} sx={{
                minWidth: { sm: '12rem' },
              }}>
                <Typography level="title-lg" color="primary">
                  {t('Template')}
                </Typography>
                <Textarea
                  name="string-template"
                  minRows={3}
                  sx={contentStyle}
                  value={stringTemplate}
                  onChange={(event) => setStringTemplate(event.target.value)}
                />
              </Stack>
            )}

            <LinePlaceholder />
            <CommonBox>
              <Typography level="title-lg" color="primary">
                {t('Inputs')}
              </Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" placement="right" title={t('If an input\'s value is not specified at runtime, the default value set here will be used')}>
                <HelpOutlineRounded fontSize="small" />
              </Tooltip>
            </CommonBox>
            {inputs && Object.keys(inputs).length > 0 && (
              <Table sx={{ my: 1 }}>
                <thead>
                  <tr>
                    <th style={{ width: '30%', maxWidth: '50%', overflowWrap: 'break-word' }}>{t('Placeholder')}</th>
                    <th>{t('Default value')}</th>
                  </tr>
                </thead>
                <tbody>
                  {Object.entries(inputs).map(([k, v]) => (
                    <tr key={`input-${k}`}>
                      <td>{k}</td>
                      <td>
                      <Textarea
                        name={`input-value-${k}`}
                        value={v}
                        onChange={(event) => handleInputChange(k, event.target.value)}
                      />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            )}

            <Stack spacing={1} sx={{
              minWidth: { sm: '12rem' },
            }}>
              <CommonBox>
                <Typography level="title-lg" color="primary">
                  {t('Example')}
                </Typography>
                <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" placement="right" title={t('Supports Markdown format')}>
                  <HelpOutlineRounded fontSize="small" />
                </Tooltip>
              </CommonBox>
              <Textarea
                name="example"
                minRows={3}
                sx={contentStyle}
                value={example}
                onChange={(event) => setExample(event.target.value)}
              />
            </Stack>
          </Stack>

          {/* Meta Settings */}
          <Card sx={{
            width: '16rem',
            m: 2,
            p: 2,
            boxShadow: 'sm',
          }}>
            <CommonBox sx={{gap: 2}}>
              <Typography level="title-sm" textColor="neutral">
                {t('Public')}
              </Typography>
              <Switch
                disabled={visibility==='hidden'}
                checked={visibility==='public'}
                sx={{
                  [`&.${switchClasses.checked}`]: {
                    '--Switch-trackBackground': '#4CA176',
                    '&:hover': {
                      '--Switch-trackBackground': '#5CB186',
                    },
                  },
                }}
                onChange={(event) => event.target.checked ? setVisibility('public') : setVisibility('private')}
              />
            </CommonBox>
            <LinePlaceholder spacing={2} />

            <CommonBox sx={{gap: 2}}>
              <Typography level="title-sm" textColor="neutral">
                {t('Format')}
              </Typography>
              <RadioGroup
                orientation="horizontal"
                name="format"
                size="sm"
                value={format}
                onChange={(event) => setFormat(event.target.value)}
                sx={{
                  p: 0.5,
                  borderRadius: '12px',
                  bgcolor: 'neutral.softBg',
                  '--RadioGroup-gap': '4px',
                  '--Radio-actionRadius': '8px',
                }}
              >
                {FORMATS.map((item) => (
                  <Radio
                    key={`format-${item.value}`}
                    color="neutral"
                    size="sm"
                    value={item.value}
                    disableIcon
                    label={(<Typography noWrap level="body-xs">{item.label}</Typography>)}
                    variant="plain"
                    sx={{
                      p: 0.5,
                      alignItems: 'center',
                    }}
                    slotProps={{
                      action: ({ checked }) => ({
                        sx: (theme) => ({
                          ...(checked && {
                            backgroundColor: theme.palette.primary.softHoverBg,
                            boxShadow: 'sm',
                            '&:hover': {
                              bgcolor: theme.palette.primary.softActiveBg,
                            },
                          }),
                        }),
                      }),
                    }}
                  />
                ))}
              </RadioGroup>
            </CommonBox>
            <LinePlaceholder spacing={2} />

            <CommonBox sx={{gap: 2}}>
              <Typography level="title-sm" textColor="neutral">
                {t('Language')}
              </Typography>
              <Select
                size="sm"
                variant="outlined"
                value={lang}
                onChange={(_event, value) => value && setLang(value)}
              >
                {Object.keys(locales).map((locale) => (
                  <Option key={`locale-${locale}`} value={locale}>
                    {locales[locale]}
                  </Option>
                ))}
              </Select>
              <LinePlaceholder spacing={2} />
            </CommonBox>
            <LinePlaceholder spacing={2} />

            <CommonBox sx={{gap: 2}}>
              <Typography level="title-sm" textColor="neutral">
                {t('Tags')}
              </Typography>
              {(!tags || tags.length < 5) && (tag === undefined) && (
                <IconButton
                  size="sm"
                  color="primary"
                  onClick={() => setTag('')}
                >
                  <AddCircleRounded />
                </IconButton>
              )}
            </CommonBox>
            <CommonBox>
              {tags.length > 0 && (
                <Fragment>
                  {tags.map((tag, index) => (
                    <Chip
                      variant="outlined"
                      color="success"
                      key={`tag-${tag}-${index}`}
                      endDecorator={<ChipDelete onDelete={() => handleTagDelete(tag)} />}
                    >
                      {tag}
                    </Chip>
                  ))}
                </Fragment>
            )}
              {(tag !== undefined) && (
                <form onSubmit={handleTagSubmit}>
                  <TinyInput
                    type="text"
                    value={tag}
                    onChange={(event => setTag(event.target.value))}
                  />
                </form>
              )}
            </CommonBox>
            <LinePlaceholder spacing={2} />

            <CommonBox sx={{gap: 2}}>
              <Typography level="title-sm" textColor="neutral">
                {t('Models')}
              </Typography>
              {modelId === undefined && (
                <IconButton
                  size="sm"
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
                    variant="outlined"
                    color="warning"
                    key={`model-${model.modelId}-${index}`}
                    endDecorator={<ChipDelete onDelete={() => model.modelId && handleModelDelete(model.modelId)} />}
                  >
                    {model.name}
                  </Chip>
                ))}
              </CommonBox>
            )}
            {(modelId !== undefined) && (
              <Fragment>
                <CommonBox>
                  <Select
                    placeholder={<Typography textColor="gray">{t('Choose a model')}</Typography>}
                    size="sm"
                    variant="outlined"
                    value={modelId}
                    sx={{
                      flex: 1,
                    }}
                    slotProps={{
                      listbox: {
                        component: 'div',
                        sx: {
                          overflow: 'auto',
                          '--List-padding': '0px',
                          '--ListItem-radius': '0px',
                        },
                      },
                    }}
                    onChange={(_event, value) => value && setModelId(value)}
                  >
                    {providers.map((provider, index) => (
                      <Fragment key={`select-${provider.provider}`}>
                        {index !== 0 && <ListDivider role="none" />}
                        <List
                          aria-labelledby={`select-group-${provider.provider}`}
                          sx={{ '--ListItemDecorator-size': '28px' }}
                        >
                          <ListItem id={`select-group-${provider.provider}`} sticky>
                            <Typography level="body-xs">
                              {provider.label}
                            </Typography>
                          </ListItem>
                          {filterModels(provider.provider).map((modelInfo) => (
                            <Option
                              key={`option-${modelInfo.modelId}`}
                              value={modelInfo.modelId}
                              label={
                                <Fragment>
                                  <Chip
                                    color="warning"
                                    size="sm"
                                    sx={{ borderRadius: 'xs', mr: 1 }}
                                  >
                                    {provider.label}
                                  </Chip>{' '}
                                  {modelInfo.name}
                                </Fragment>
                              }
                              sx={{
                                [`&.${optionClasses.selected} .${listItemDecoratorClasses.root}`]:
                                  {
                                    opacity: 1,
                                  },
                              }}
                            >
                              <ListItemDecorator sx={{ opacity: 0 }}>
                                <CheckRounded />
                              </ListItemDecorator>
                              {modelInfo.name}
                            </Option>
                          ))}
                        </List>
                      </Fragment>
                    ))}
                  </Select>
                </CommonBox>
                <CommonBox sx={{ justifyContent: 'flex-end' }}>
                  <IconButton
                      size="sm"
                      onClick={handleModelSubmit}
                    >
                      <CheckCircleOutlineRounded fontSize="small" />
                    </IconButton>
                    <IconButton
                      size="sm"
                      onClick={() => setModelId(undefined)}
                    >
                      <CancelOutlined fontSize="small" />
                    </IconButton>
                </CommonBox>
              </Fragment>
            )}
          </Card>
        </CommonBox>
        
        {play && 
          <PromptRunner
            minWidth="16rem"
            maxWidth="40%"
            apiPath="/api/v1/prompt/send/stream"
            record={editRecord}
            defaultVariables={defaultVariables}
            defaultParameters={defaultParameters}
            defaultOutputText={defaultOutputText}
            onPlaySuccess={handlePlaySuccess}
            onExampleSave={handleExampleGenerate}
          />
        }
      </CommonBox>
      <ConfirmModal
        open={editRecordName !== undefined}
        onClose={() => setEditRecordName(undefined)}
        dialog={{
          title: t('Please enter a new name'),
        }}
        button={{
          text: t('button:Save'),
          startDecorator: <SaveAltRounded />
        }}
        onConfirm={handleNameChange}
      >
        <FormControl error={editRecordNameError}>
          <Input
            name="RecordName"
            value={editRecordName}
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
    </>
  );
}