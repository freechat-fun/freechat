/* eslint-disable @typescript-eslint/no-explicit-any */
import { Fragment, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext, useMetaInfoContext } from "../../contexts";
import { Box, Button, ButtonGroup, Card, Chip, ChipDelete, Divider, FormControl, FormHelperText, IconButton, Input, List, ListDivider, ListItem, ListItemDecorator, Option, Radio, RadioGroup, Select, Stack, Switch, Table, Textarea, Theme, Tooltip, Typography, listItemDecoratorClasses, optionClasses, switchClasses } from "@mui/joy";
import { AddCircleRounded, ArrowBackRounded, CancelOutlined, CheckCircleOutlineRounded, CheckRounded, EditRounded, InfoOutlined, IosShareRounded, PlayCircleOutlineRounded, RemoveCircleOutlineRounded, SaveAltRounded } from "@mui/icons-material";
import { CommonBox, CommonContainer, CommonGridBox, ConfirmModal, ContentTextarea, LinePlaceholder, RouterBlocker, TinyInput } from "../../components";
import { AiModelInfoDTO, ChatMessageDTO, ChatPromptContentDTO, LlmResultDTO, PromptAiParamDTO, PromptDetailsDTO, PromptTemplateDTO, PromptUpdateDTO } from "freechat-sdk";
import { formatDate, getDateLabel } from "../../libs/date_utils";
import { PromptRunner } from "../../components/prompt";
import { locales } from "../../configs/i18n-config";
import { extractVariables, generateExample, getMessageText, setMessageText } from "../../libs/template_utils";
import { providers } from "../../configs/model-providers-config";
import { HelpIcon } from "../../components/icon";
import { objectsEqual } from "../../libs/js_utils";

type MessageRound = {
  user: ChatMessageDTO;
  assistant: ChatMessageDTO;
};

type PromptEditorProps = {
  id: number | undefined;
  parameters?: { [key: string]: any };
  variables?: { [key: string]: any };
};

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
  const [defaultParameters, setDefaultParameters] = useState(parameters ? {...parameters} : undefined)
  const [defaultVariables, setDefaultVariables] = useState(variables ? {...variables} : undefined)
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

  const originName = useRef<string>();
  const systemRef = useRef<HTMLDivElement>(null);
  const messageRef = useRef<HTMLDivElement>(null);

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

const getEditRecord = useCallback((inputsJson: string | undefined) => {
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
  }, [description, example, format, id, lang, messages, models, origRecord.promptUid, origRecord.type, recordName, stringTemplate, system, tags, userMessage, visibility]);

  useEffect(() => {
    if (id) {
      promptApi?.getPromptDetails(id)
        .then(setOrigRecord)
        .catch(handleError);
    }

    aiServiceApi?.listAiModelInfo1()
      .then(setModelInfos)
      .catch(handleError);
  }, [handleError, id, promptApi, aiServiceApi]);

  useEffect(() => {
    const { hash } = window.location;
    const delay = 200;
    let handler: number | undefined;
    
    if (hash === '#system' && systemRef.current) {
      handler = setTimeout(() => systemRef.current?.scrollIntoView({ behavior: 'smooth' }), delay);
    } else if (hash === '#messages' && messageRef.current) {
      handler = setTimeout(() => messageRef.current?.scrollIntoView({ behavior: 'smooth' }), delay);
    }

    return () => {
      if (handler) {
        clearTimeout(handler);
      }
    };
  }, [origRecord]);

  useEffect(() => {
    setDefaultParameters(parameters ? {...parameters} : undefined);
  }, [parameters]);

  useEffect(() => {
    setDefaultVariables(variables ? {...variables} : undefined);
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
        } catch (error) {
          console.warn(`[WARNING] Invalid draft content: ${origRecord.draft}`);
        }
      }

      const draftRecord = {...origRecord, ...draft};

      setRecordName(draftRecord.name);
      setDescription(draftRecord.description);
      setStringTemplate(draftRecord.template);
      setSystem(draftRecord.chatTemplate?.system);
      setUserName(draftRecord.chatTemplate?.messageToSend?.name ?? 'user');
      setUserMessage(draftRecord.chatTemplate?.messageToSend?.contents?.[0]?.content ||
        (draftRecord.format === 'f_string' ? '{input}' : '{{{input}}}'));
      setMessages(draftRecord.chatTemplate?.messages ?? []);
      setExample(draftRecord.example);

      setVisibility(draftRecord.visibility ?? 'private');
      setFormat(draftRecord.format ?? 'mustache');
      setLang(draftRecord.lang ? draftRecord.lang.split('_')[0] : 'en');
      setTags(draftRecord.tags ?? []);
      setModels(draftRecord.aiModels ?? []);
      if (draftRecord.ext) {
        try {
          const persistentParameters = JSON.parse(draftRecord.ext) as { [key: string]: any };
          setDefaultParameters(persistentParameters);
        } catch (error) {
          // ignore
        }
      }
      setInputs(extractVariables(draftRecord));

      originName.current = draftRecord.name;
    }
  }, [origRecord, username]);

  useEffect(() => {
    if (originName.current) {
      setInputs((prevInputs => extractVariables(getEditRecord(JSON.stringify(prevInputs)))));
    }
  }, [getEditRecord, userMessage, stringTemplate]);

  useEffect(() => {
    if (originName.current) {
      setRounds(messagesToRounds(messages));
      setInputs((prevInputs => extractVariables(getEditRecord(JSON.stringify(prevInputs)))));
    }
  }, [getEditRecord, messages]);

  useEffect(() => {
    if (originName.current) {
      setDefaultVariables(prevVariables => {
        const filteredInputs: { [key: string]: any; } = {};
        Object.keys(prevVariables ?? {}).forEach(k => {
          if (k in (inputs ?? {}) && prevVariables?.k) {
            filteredInputs[k] = prevVariables.k;
          }
        });
        return {...inputs, ...filteredInputs};
      });
    }
  }, [inputs]);

  useEffect(() => {
    if (originName.current) {
      setUserMessage(prevUserMessage => {
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

    setDefaultOutputText(getMessageText(response?.message) ?? response?.text);
  }

  function handlePlayFailure(request: PromptAiParamDTO | undefined, error: any): void {
    request?.params && Object.keys(request?.params).length > 0 ?
      setDefaultParameters(request?.params) : setDefaultParameters(undefined);

    if (request?.promptRef?.variables && Object.keys(request?.promptRef?.variables).length > 0) {
      setDefaultVariables(request?.promptRef?.variables); 
    } else if (request?.promptTemplate?.variables && Object.keys(request?.promptTemplate?.variables).length > 0) {
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
        promptApi?.existsPromptName(editRecordName)
          .then(resp => {
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
    setMessageText(currentUserMessage, editUserContent);

    const currentAssistantMessage = new ChatMessageDTO();
    currentAssistantMessage.role = 'assistant',
    currentAssistantMessage.name = editAssistantName;
    setMessageText(currentAssistantMessage, editAssistantContent);

    setMessages([...messages, currentUserMessage, currentAssistantMessage]);
    setEditRound(false);
  }

  function handleInputsChange(k: string, v: string): void {
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
    if (!id) {
      return;
    }
    const draftRecord = getEditRecord(JSON.stringify(inputs));
    draftRecord.promptId = undefined;
    draftRecord.draft = undefined;

    const request = new PromptUpdateDTO();
    request.draft = JSON.stringify(draftRecord);

    promptApi?.updatePrompt(id, request)
      .then(resp => {
        if (!resp) {
          return;
        }
        promptApi?.getPromptDetails(id)
          .then(setOrigRecord)
          .catch(handleError);
      })
      .catch(handleError);
  }

  function handleRecordPublish(): void {
    if (!id) {
      return;
    }
    const onUpdated = (currentId: number, visibility: string) => {
      promptApi?.publishPrompt(currentId, visibility)
        .then(resp => {
          if (!resp) {
            return;
          }
          navigator(`/w/prompt/${resp}`);
        })
        .catch(handleError);
    };

    const editRecord = getEditRecord(JSON.stringify(inputs));

    const request = recordToUpdateRequest(editRecord);
    promptApi?.updatePrompt(id, request)
      .then(resp => {
        if (resp) {
          promptApi?.getPromptDetails(id)
            .then(newRecord => {
              setOrigRecord(newRecord);
              onUpdated(id, editRecord.visibility === 'private' ? 'private' : 'public');
            })
            .catch(handleError);
        }
      })
      .catch(handleError);

  }

  function filterModels(provider?: string): (AiModelInfoDTO)[] {
    return modelInfos ? modelInfos.filter(modelInfo => {
      if (!modelInfo || (provider && modelInfo.provider !== provider)) {
        return false;
      }
      switch(origRecord.type) {
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
    request.chatTemplate = {...record.chatTemplate};
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
      } catch (error) {
        console.warn(`[WARNING] Invalid draft content: ${origRecord.draft}`);
      }
    }

    const draftRecord = {...origRecord, ...draft};

    return objectsEqual(draftRecord.name, recordName) &&
      objectsEqual(draftRecord.description, description) &&
      objectsEqual(draftRecord.template, stringTemplate) &&
      objectsEqual(draftRecord.chatTemplate?.system, system) &&
      objectsEqual((draftRecord.chatTemplate?.messageToSend?.name ?? 'user'), userName) &&
      objectsEqual((draftRecord.chatTemplate?.messageToSend?.contents?.[0]?.content ||
        (draftRecord.format === 'f_string' ? '{input}' : '{{{input}}}')), userMessage) &&
      objectsEqual((draftRecord.chatTemplate?.messages ?? []), messages) &&
      objectsEqual(draftRecord.example, example) &&
      objectsEqual((draftRecord.visibility ?? 'private'), visibility) &&
      objectsEqual((draftRecord.format ?? 'mustache'), format) &&
      objectsEqual((draftRecord.lang ? draftRecord.lang.split('_')[0] : 'en'), lang) &&
      objectsEqual((draftRecord.tags ?? []), tags) &&
      objectsEqual((draftRecord.aiModels ?? []), models);
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
        <CommonContainer sx={{
          alignItems: 'center',
          flex: 1,
        }}>
          <Typography level="h3">{recordName}</Typography>
          <IconButton
            disabled={!!editRecordName}
            size="sm"
            onClick={() => setEditRecordName(recordName || 'untitled')}
          >
            <EditRounded fontSize="small" />
          </IconButton>
        </CommonContainer>
        <Typography level="body-sm">
          {t('Updated on')} {getDateLabel(origRecord?.gmtModified || new Date(0), i18n.language, true)}
        </Typography>

        <ButtonGroup
          size="sm"
          variant="soft"
          color="primary"
          sx={{
          borderRadius: '16px',
        }}>
          <Button
            disabled={isSaved() || visibility==='hidden'}
            startDecorator={isSaved() ? <CheckRounded /> : <SaveAltRounded />}
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

      <CommonContainer sx={{ alignItems: 'flex-start' }}>
        <CommonContainer sx={{ flex: 1, alignItems: 'flex-start' }}>
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
                <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" placement="right" title={t('Supports markdown format')}>
                  <HelpIcon />
                </Tooltip>
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
                <Stack spacing={1} sx={{
                  minWidth: { sm: '12rem' },
                }}>
                  <Chip ref={systemRef} variant="soft" color="primary">SYSTEM</Chip>
                  <ContentTextarea
                    name="system"
                    minRows={3}
                    value={system}
                    onChange={(event) => setSystem(event.target.value)}
                  />
                </Stack>
                <Stack spacing={1} sx={{
                  minWidth: { sm: '12rem' },
                }}>
                  <CommonBox ref={messageRef}>
                    <Chip variant="soft" color="primary">MESSAGES</Chip>
                    <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" placement="right" title={t('These messages will always be used as the starting message in the chat history')}>
                      <HelpIcon />
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
                            <CommonGridBox sx={{
                              flex: 1,
                              gap: 1,
                            }}>
                              <Chip variant="soft" color="success" sx={{ "--Chip-radius": "2px"}}>
                                {(round.user.name || round.user.role || 'user').toUpperCase()}
                              </Chip>
                              <Typography level="body-md" sx={contentStyle}>
                                {getMessageText(round.user)}
                              </Typography>
                              <Chip variant="soft" color="warning" sx={{ "--Chip-radius": "2px"}}>
                                {(round.assistant.name || round.assistant.role || 'assistant').toUpperCase()}
                              </Chip>
                              <Typography level="body-md" sx={contentStyle}>
                                {getMessageText(round.assistant)}
                              </Typography>
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
                          <Chip variant="soft" color="success" sx={{ "--Chip-radius": "2px"}}>{editUserName}</Chip>
                        ) : (
                          <TinyInput
                            name="editUserName"
                            value={editUserName}
                            onChange={(event) => setEditUserName(event.target.value)}
                          />
                        )}
                        <ContentTextarea
                          name="editUserMessage"
                          value={editUserContent}
                          onChange={(event) => setEditUserContent(event.target.value)}
                        />
                        {rounds.length > 0 ? (
                          <Chip variant="soft" color="warning" sx={{ "--Chip-radius": "2px"}}>{editAssistantName}</Chip>
                        ) : (
                          <TinyInput
                            name="editAssistantName"
                            value={editAssistantName}
                            onChange={(event) => setEditAssistantName(event.target.value)}
                          />
                        )}
                        <ContentTextarea
                          name="editAssistantMessage"
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
                  <ContentTextarea
                    name="user"
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
              <Typography level="title-lg" color="primary">
                {t('Inputs')}
              </Typography>
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" placement="right" title={t('If an input\'s value is not specified at runtime, the default value set here will be used')}>
                <HelpIcon />
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
                        disabled={k === 'input'}
                        name={`input-value-${k}`}
                        value={v}
                        onChange={(event) => handleInputsChange(k, event.target.value)}
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
                <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" placement="right" title={t('Supports markdown format')}>
                  <HelpIcon />
                </Tooltip>
              </CommonBox>
              <ContentTextarea
                name="example"
                minRows={3}
                value={example}
                onChange={(event) => setExample(event.target.value)}
              />
            </Stack>
          </Stack>

          {/* Meta Settings */}
          {!play && (
            <Card sx={{
              width: { xs: '100%', sm: '16rem' },
              my: 2,
              mx: { xs: 0, sm: 2 },
              p: 2,
              boxShadow: 'sm',
            }}>
              <CommonGridBox>
                <Typography level="title-sm" textColor="neutral">
                  {t('Public')}
                </Typography>
                <Switch
                  checked={visibility==='public' || visibility==='hidden'}
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
              </CommonGridBox>

              <CommonBox sx={{ mt: 4 }}>
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
              <CommonBox>
                {tags.length > 0 && tags.map((tag, index) => (
                  <Chip
                    variant="outlined"
                    color="success"
                    key={`tag-${tag}-${index}`}
                    endDecorator={<ChipDelete onDelete={() => handleTagDelete(tag)} />}
                  >
                    {tag}
                  </Chip>
                ))}
              </CommonBox>
              <LinePlaceholder spacing={2} />

              <CommonBox>
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
          )}
        </CommonContainer>
        
        {play && 
          <PromptRunner
            minWidth="16rem"
            maxWidth="50%"
            apiPath="/api/v1/prompt/send/stream"
            record={getEditRecord(JSON.stringify(inputs))}
            defaultVariables={defaultVariables}
            defaultParameters={defaultParameters}
            defaultOutputText={defaultOutputText}
            onPlaySuccess={handlePlaySuccess}
            onPlayFailure={handlePlayFailure}
            onExampleSave={handleExampleGenerate}
          />
        }
      </CommonContainer>
      <ConfirmModal
        open={editRecordName !== null}
        onClose={() => setEditRecordName(null)}
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
        message={t('You may have unsaved changes. Are you sure you want to leave?')}
      />
    </>
  );
}