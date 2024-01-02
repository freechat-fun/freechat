import { useTranslation } from "react-i18next";
import { Box, Card, Chip, Divider, IconButton, Stack, Table, Textarea, Theme, Tooltip, Typography } from "@mui/joy";
import { ChatMessageDTO, PromptDetailsDTO, ChatPromptContentDTO } from "freechat-sdk";
import { extractJson, extractVariables } from "../../libs/template_utils";
import { Fragment, useEffect, useState } from "react";
import { CommonBox, LinePlaceholder, TinyInput } from "..";
import { AddCircleRounded, CancelOutlined, CheckCircleOutlineRounded, HelpOutlineRounded, RemoveCircleOutlineRounded } from "@mui/icons-material";

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
}) {
  const { record } = props;
  const { t } = useTranslation(['prompt']);

  const [description, setDescription] = useState(record?.description);
  const [inputs, setInputs] = useState(extractJson(record?.inputs));
  const [stringTemplate, setStringTemplate] = useState(record?.template);
  const [system, setSystem] = useState(record?.chatTemplate?.system);
  const [userName, setUserName] = useState(record?.chatTemplate?.messageToSend?.name || 'user');
  const [userMessage, setUserMessage] = useState(record?.chatTemplate?.messageToSend?.content ||
    (record?.format === 'f_string' ? '{input}' : '{{input}}'));
  const [messages, setMessages] = useState(record?.chatTemplate?.messages ?? []);
  const [example, setExample] = useState(record?.example);

  const [rounds, setRounds] = useState(messagesToRounds(record?.chatTemplate?.messages));
  const [editRound, setEditRound] = useState(false);
  const [editUserName, setEditUserName] = useState<string | undefined>('user');
  const [editAssistantName, setEditAssistantName] = useState<string | undefined>('assistant')
  const [editUserContent, setEditUserContent] = useState<string>();
  const [editAssistantContent, setEditAssistantContent] = useState<string>();

  const [editRecord, setEditRecord] = useState(record ?? new PromptDetailsDTO());

  const contentStyle = {
    whiteSpace: 'pre-wrap',
    overflowWrap: 'break-word',
    flex: 1,
  };

  useEffect(() => {
    setDescription(record?.description);
    setStringTemplate(record?.template);
    setInputs(extractJson(record?.inputs));
    setStringTemplate(record?.template);
    setSystem(record?.chatTemplate?.system);
    setUserName(record?.chatTemplate?.messageToSend?.name || 'user');
    setUserMessage(record?.chatTemplate?.messageToSend?.content ||
      (record?.format === 'f_string' ? '{input}' : '{{input}}'));
    setMessages(record?.chatTemplate?.messages ?? []);
    setExample(record?.example);
    setEditRecord(record ?? new PromptDetailsDTO());
  }, [record]);


  useEffect(() => {
    setInputs(extractVariables(editRecord));
  }, [editRecord]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      if (!newRecord.chatTemplate) {
        newRecord.chatTemplate = new ChatPromptContentDTO();
      }
      newRecord.chatTemplate.system = system;
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
      return newRecord;
    });
  }, [userMessage]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.template = stringTemplate;
      return newRecord;
    });
  }, [stringTemplate]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.example = example;
      return newRecord;
    });
  }, [example]);

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

  function handleRoundCommit() {
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

  const roundItemStyle = (theme: Theme) => ({
    width: '100%',
    mr: 'auto',
    justifyContent: 'flex-end',
    border: `1px solid ${theme.palette.primary.outlinedBorder}`,
    backgroundColor: theme.palette.background.body,
    padding: theme.spacing(1, 2),
    borderRadius: '12px',
  });

  return (
    <Stack spacing={3} sx={{
      minWidth: { sm: '16rem' },
      mt: 2,
      flex: 1,
      transition: 'width 0.3s',
    }}>
      <Stack spacing={1} sx={{
        minWidth: { sm: '12rem' },
      }}>
        <CommonBox>
          <Typography level="title-lg" color="primary">
            {t('Description')}
          </Typography>
          <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('Supports Markdown format')}>
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
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('These messages will always be used as the starting message in the chat history')}>
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
                <td>{v}</td>
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
          <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={t('Supports Markdown format')}>
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
  );
}