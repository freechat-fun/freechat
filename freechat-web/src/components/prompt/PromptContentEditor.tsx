import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { Box, Card, Chip, ChipDelete, Divider, IconButton, Radio, RadioGroup, Option, Select, Stack, Switch, Table, Textarea, Theme, Tooltip, Typography, switchClasses, ListDivider, List, ListItem, optionClasses, listItemDecoratorClasses, ListItemDecorator } from "@mui/joy";
import { ChatMessageDTO, PromptDetailsDTO, ChatPromptContentDTO, AiModelInfoDTO } from "freechat-sdk";
import { extractJson, extractVariables } from "../../libs/template_utils";
import { Fragment, useEffect, useState } from "react";
import { CommonBox, LinePlaceholder, TinyInput } from "..";
import { AddCircleRounded, CancelOutlined, CheckCircleOutlineRounded, CheckRounded, HelpOutlineRounded, RemoveCircleOutlineRounded } from "@mui/icons-material";
import { locales } from "../../configs/i18n-config";
import { providers } from "../../configs/model-providers-config";

interface MessageRound {
  user: ChatMessageDTO;
  assistant: ChatMessageDTO;
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

export default function PromptContentEditor(props: {
  record: PromptDetailsDTO | undefined,
  onUpdate?: (record: PromptDetailsDTO) => void,
}) {
  const { record, onUpdate } = props;
  const { t } = useTranslation(['prompt']);
  const { aiServiceApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [description, setDescription] = useState(record?.description);
  const [inputs, setInputs] = useState(extractJson(record?.inputs));
  const [stringTemplate, setStringTemplate] = useState(record?.template);
  const [system, setSystem] = useState(record?.chatTemplate?.system);
  const [userName, setUserName] = useState(record?.chatTemplate?.messageToSend?.name ?? 'user');
  const [userMessage, setUserMessage] = useState(record?.chatTemplate?.messageToSend?.content ||
    (record?.format === 'f_string' ? '{input}' : '{{input}}'));
  const [messages, setMessages] = useState(record?.chatTemplate?.messages ?? []);
  const [example, setExample] = useState(record?.example);

  const [visibility, setVisibility] = useState(record?.visibility ?? 'private');
  const [format, setFormat] = useState(record?.format ?? 'mustache');
  const [lang, setLang] = useState(record?.lang ? record?.lang.split('_')[0] : 'en');
  const [tags, setTags] = useState(record?.tags ?? []);
  const [tag, setTag] = useState<string>();
  const [models, setModels] = useState(record?.aiModels ?? []);
  const [modelId, setModelId] = useState<string>();
  const [modelInfos, setModelInfos] = useState<AiModelInfoDTO[]>([]);

  const [rounds, setRounds] = useState(messagesToRounds(record?.chatTemplate?.messages));
  const [editRound, setEditRound] = useState(false);
  const [editUserName, setEditUserName] = useState<string | undefined>('user');
  const [editAssistantName, setEditAssistantName] = useState<string | undefined>('assistant')
  const [editUserContent, setEditUserContent] = useState<string>();
  const [editAssistantContent, setEditAssistantContent] = useState<string>();

  const [editRecord, setEditRecord] = useState(record ?? new PromptDetailsDTO());

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
    setDescription(record?.description);
    setStringTemplate(record?.template);
    setInputs(extractJson(record?.inputs));
    setStringTemplate(record?.template);
    setSystem(record?.chatTemplate?.system);
    setUserName(record?.chatTemplate?.messageToSend?.name ?? 'user');
    setUserMessage(record?.chatTemplate?.messageToSend?.content ||
      (record?.format === 'f_string' ? '{input}' : '{{input}}'));
    setMessages(record?.chatTemplate?.messages ?? []);
    setExample(record?.example);

    setVisibility(record?.visibility ?? 'private');
    setFormat(record?.format ?? 'mustache');
    setLang(record?.lang ? record?.lang.split('_')[0] : 'en');
    setTags(record?.tags ?? []);
    setModels(record?.aiModels ?? []);

    setEditRecord(record ?? new PromptDetailsDTO());

    aiServiceApi?.listAiModelInfo1()
      .then(setModelInfos)
      .catch(handleError);
  }, [record, aiServiceApi, handleError]);


  useEffect(() => {
    onUpdate?.(editRecord);
  }, [editRecord, onUpdate]);

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
    setTags(tags.filter(tag => tagDeleted !== tag));
  }

  function handleModelDelete(modelIdDeleted: string): void {
    setModels(models.filter(model => modelIdDeleted !== model.modelId));
  }

  function handleTagSubmit(event: React.FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    tag && !tags.includes(tag) && setTags([...tags, tag]);
    setTag(undefined);
  }

  function handleModelSubmit(): void {
    const modelInfo = modelInfos.find(modelInfo => modelInfo.modelId === modelId);
    modelInfo && models && !models.find(model => modelId === model.modelId) && setModels([...models, modelInfo]);
    setModelId(undefined);
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

  return (
    <Box sx={{
      display: 'flex',
      flexDirection: { xs: 'column', sm: 'row' },
      flex: 1,
      gap: { xs: 1, sm: 2 },
    }}>
      <Stack spacing={3} sx={{
        minWidth: { sm: '16rem' },
        mt: 2,
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

        {record?.type === 'chat' && record?.chatTemplate ? (
          <Fragment>
            <Stack spacing={1} sx={{
              minWidth: { sm: '12rem' },
            }}>
              <Chip variant="soft" color="primary">SYSTEM</Chip>
              <Textarea
                name="system"
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
              <Chip variant="soft" color="success">{userName.toUpperCase()}</Chip>
              <Textarea
                name="user"
                sx={contentStyle}
                value={userMessage}
                onChange={(event) => setUserMessage(event.target.value)}
              />
            </Stack>

          </Fragment>
        ) : (
          <Card sx={{
            minWidth: { sm: '12rem' },
            p: 2,
            boxShadow: 'sm',
          }}>
            <Typography level="title-lg" color="primary">
              {t('Template')}
            </Typography>
            <Divider />
            <Textarea
              name="string-template"
              sx={contentStyle}
              value={stringTemplate}
              onChange={(event) => setStringTemplate(event.target.value)}
            />
          </Card>
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
        minWidth: '16rem',
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
    </Box>
  );
}