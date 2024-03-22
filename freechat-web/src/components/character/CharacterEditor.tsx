import { Fragment, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext, useMetaInfoContext } from "../../contexts";
import { CharacterBackendDTO, CharacterBackendDetailsDTO, CharacterDetailsDTO, CharacterUpdateDTO, ChatCreateDTO, PromptRefDTO, PromptTaskDTO } from "freechat-sdk";
import { formatDate, getDateLabel } from "../../libs/date_utils";
import { locales } from "../../configs/i18n-config";
import { CommonBox, CommonContainer, ConfirmModal, ContentTextarea, ImagePicker, LabelTypography, LinePlaceholder, TinyInput } from "../../components";
import { AspectRatio, Avatar, Box, Button, ButtonGroup, Card, Chip, ChipDelete, Divider, FormControl, FormHelperText, IconButton, Input, Option, Radio, RadioGroup, Select, Stack, Switch, Tooltip, Typography, switchClasses } from "@mui/joy";
import { AddCircleRounded, CheckRounded, EditRounded, InfoOutlined, IosShareRounded, SaveAltRounded, TransitEnterexitRounded } from "@mui/icons-material";
import { CharacterAlbum, CharacterBackendSettings, CharacterBackends, CharacterGuide } from "../../components/character";
import { HelpIcon } from "../../components/icon";
import { createPromptForCharacter } from "../../libs/chat_utils";
import { getCompressedImage } from "../../libs/ui_utils";

type CharacterEditorProps = {
  id: number;
}

export default function CharacterEditor ({
  id,
}: CharacterEditorProps) {
  const navigate = useNavigate();
  const { t, i18n } = useTranslation(['character', 'account', 'button']);
  const { accountApi, promptApi, promptTaskApi, characterApi, chatApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useMetaInfoContext();

  const [origRecord, setOrigRecord] = useState(new CharacterDetailsDTO());
  const [editRecordName, setEditRecordName] = useState<string | null>(null);
  const [editRecordNameError, setEditRecordNameError] = useState(false);

  const [recordName, setRecordName] = useState<string>('');
  const [nickname, setNickname] = useState<string>();
  const [description, setDescription] = useState<string>();
  const [avatar, setAvatar] = useState<string>();
  const [picture, setPicture] = useState<string>();
  const [gender, setGender] = useState('other');
  const [lang, setLang] = useState('English');
  const [profile, setProfile] = useState<string>();
  const [greeting, setGreeting] = useState<string>();
  const [chatStyle, setChatStyle] = useState<string>();
  const [chatExample, setChatExample] = useState<string>();

  const [visibility, setVisibility] = useState<string>();
  const [tags, setTags] = useState<string[]>([]);
  const [tag, setTag] = useState<string | null>(null);

  const [editBackend, setEditBackend] = useState<CharacterBackendDetailsDTO>();
  const [backends, setBackends] = useState<Array<CharacterBackendDetailsDTO>>([]);

  const [editEnabled, setEditEnabled] = useState(false);
  const [saved, setSaved] = useState(false);

  const originName = useRef('');

  const getEditRecord = useCallback(() => {
    const newRecord = new CharacterDetailsDTO();
    newRecord.characterId = id;
    newRecord.name = recordName ?? '';
    newRecord.nickname = nickname;
    newRecord.description = description;
    newRecord.avatar = avatar;
    newRecord.picture = picture;
    newRecord.gender = gender;
    newRecord.lang = lang;
    newRecord.profile = profile;
    newRecord.greeting = greeting;
    newRecord.chatStyle = chatStyle;
    newRecord.chatExample = chatExample;
    newRecord.visibility = visibility;
    newRecord.tags = [...tags];

    return newRecord;
  }, [avatar, chatExample, chatStyle, description, gender, greeting, id, lang, nickname, picture, profile, recordName, tags, visibility]);
  
  useEffect(() => {
    if (id) {
      characterApi?.getCharacterDetails(id)
        .then(resp => {
          setOrigRecord(resp);
        })
        .catch(handleError);
    }
  }, [handleError, id, characterApi]);

  useEffect(() => {
    if (origRecord) {
      if (origRecord.username !== username) {
        return;
      }
      let draft = {};
      if (origRecord.draft) {
        try {
          draft = (JSON.parse(origRecord.draft) as CharacterDetailsDTO) ?? {};
        } catch (error) {
          console.log(`[WARNING] Invalid draft content: ${origRecord.draft}`);
        }
      }

      const draftRecord = {...origRecord, ...draft};
      
      originName.current = draftRecord.name ?? '';

      setRecordName(draftRecord.name ?? '');
      setNickname(draftRecord.nickname);
      setDescription(draftRecord.description);
      setAvatar(draftRecord.avatar);
      setPicture(draftRecord.picture);
      setGender(draftRecord.gender ?? 'other');
      setLang(draftRecord.lang ?? 'English');
      setProfile(draftRecord.profile);
      setGreeting(draftRecord.greeting);
      setChatStyle(draftRecord.chatStyle);
      setChatExample(draftRecord.chatExample);
      setVisibility(draftRecord.visibility ?? 'private');
      setTags(draftRecord.tags ?? []);
      setEditEnabled(true);
    }
  }, [origRecord, username]);

  useEffect(() => {
    if (editEnabled) {
      setSaved(false);
    }
  }, [editEnabled, nickname, description, avatar, picture, gender, lang, greeting, chatStyle, chatExample, visibility, tags]);

  function handleNameChange(): void {
    if (editRecordNameError) {
      return;
    }

    if (editRecordName && editRecordName !== recordName) {
      if (editRecordName === originName.current) {
        setRecordName(editRecordName);
        setEditRecordName(null);
      } else {
        characterApi?.existsCharacterName(editRecordName)
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

  function handleTagDelete(tagDeleted: string): void {
    tags && setTags(tags.filter(tag => tagDeleted !== tag));
  }

  function handleTagSubmit(event: React.FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    tags && tag && !tags.includes(tag) && setTags([...tags, tag]);
    setTag(null);
  }

  function handleAvatarSelect(file: Blob, name: string) {
    if (!id) {
      return;
    }
    getCompressedImage(file, 1024 * 1024)
      .then(imageInfo => {
        const request = new File([imageInfo.blob], name);
        characterApi?.uploadCharacterAvatar(id as number, request)
          .then(url => setAvatar(url))
          .catch(handleError);
      })
      .catch(handleError);
  }
    

  function handleRecordSave(): void {
    if (!id) {
      return;
    }
    const draftRecord = getEditRecord();
    draftRecord.characterId = undefined;
    draftRecord.draft = undefined;

    const request = new CharacterUpdateDTO();
    request.draft = JSON.stringify(draftRecord);

    characterApi?.updateCharacter(id, request)
      .then(setSaved)
      .catch(handleError);
  }

  function handleRecordPublish(): void {
    if (!id) {
      return;
    }
    const onUpdated = (currentId: number, visibility: string, nickname: string) => {
      characterApi?.publishCharacter1(currentId, visibility)
        .then(characterId => {
          chatApi?.getDefaultChatId(characterId)
            .then(resp => {
              navigate(`/w/chat/${resp}/debug`);
            })
            .catch(() => {
              accountApi?.getUserDetails()
                .then(userDetails => {
                  const request = new ChatCreateDTO();
                  request.userNickname = userDetails.nickname ?? userDetails.username;
                  request.userProfile = userDetails.profile;
                  request.characterNickname = nickname;
                  request.characterId = characterId as number;

                  chatApi.startChat(request)
                    .then(chatId => {
                      navigate(`/w/chat/${chatId}/debug`);
                    })
                    .catch(handleError);
                })
                .catch(handleError);
            });
        })
        .catch(handleError);
    };

    const editRecord = getEditRecord();
    const request = recordToUpdateRequest(editRecord);
    characterApi?.updateCharacter(id, request)
      .then(resp => {
        setSaved(resp);
        if (resp) {
          onUpdated(
            editRecord.characterId as number,
            editRecord.visibility === 'private' ? 'private' : 'public',
            editRecord.nickname ?? editRecord.name ?? '',
          );
        }
      })
      .catch(handleError);
  }

  function recordToUpdateRequest(record: CharacterDetailsDTO): CharacterUpdateDTO {
    const request = new CharacterUpdateDTO();
    request.nickname = record.nickname;
    request.description = record.description;
    request.avatar = record.avatar;
    request.picture = record.picture;
    request.gender = record.gender;
    request.lang = record.lang;
    request.profile = record.profile;
    request.greeting = record.greeting;
    request.chatStyle = record.chatStyle;
    request.chatExample = record.chatExample;
    request.name = record.name ?? `untitiled-${formatDate(new Date())}`;
    request.draft = '';
    request.tags = record.tags;
    request.visibility = record.visibility === 'private' ? 'private' : 'public';

    return request;
  }

  function handleBackendEdit(backend: CharacterBackendDetailsDTO, backends: CharacterBackendDetailsDTO[]) {
    if (backends.length === 0) {
      backend.isDefault = true;
    }
    setEditBackend(backend);
    setBackends(backends);
  }

  function handleBackendUpdated(
    backend: CharacterBackendDetailsDTO,
    promptTask?: PromptTaskDTO,
    redirectToChatPrompt?: boolean,
  ) {
    const request = new CharacterBackendDTO();
    request.chatPromptTaskId = backend.chatPromptTaskId;
    request.forwardToUser = backend.forwardToUser;
    request.greetingPromptTaskId = backend.greetingPromptTaskId;
    request.isDefault = backend.isDefault;
    request.messageWindowSize = backend.messageWindowSize;
    request.moderationApiKeyName = backend.moderationApiKeyName;
    request.moderationModelId = backend.moderationModelId;
    request.moderationParams = backend.moderationParams;

    const updateBackend = (backendId: string | undefined, req: CharacterBackendDTO) => {
      if (backendId) {
        characterApi?.updateCharacterBackend(backendId, req)
          .then(() => {
            if (redirectToChatPrompt && req.chatPromptTaskId) {
              navigate(`/w/prompt/task/edit/${req.chatPromptTaskId}`);
            } else {
              setBackends((prevBackends) => {
                const others = prevBackends.filter(prevBackend => prevBackend.backendId !== backendId);
                return [...others, {...backend, backendId: backendId, chatPromptTaskId: req.chatPromptTaskId}];
              });
              setEditBackend(undefined);
            }
          })
          .catch(handleError);
        } else if (id) {
          characterApi?.addCharacterBackend(id, req)
            .then((bId) => {
              if (redirectToChatPrompt && req.chatPromptTaskId) {
                navigate(`/w/prompt/task/edit/${req.chatPromptTaskId}`);
              } else {
                setBackends((prevBackends) => {
                  return [...prevBackends, {...backend, backendId: bId, chatPromptTaskId: req.chatPromptTaskId}];
                });
                setEditBackend(undefined);
              }
            })
        }
      }

    if (backend.chatPromptTaskId) {
      if (promptTask) {
        promptTaskApi?.updatePromptTask(backend.chatPromptTaskId, promptTask)
          .then(() => updateBackend(backend.backendId, request))
          .catch(handleError);
      } else {
        updateBackend(backend.backendId, request);
      }
    } else {
      const promptReq = createPromptForCharacter(recordName, lang);
      promptApi?.newPromptName(recordName ?? 'untitled')
        .then(name => {
          promptReq.name = name;
          promptApi?.createPrompt(promptReq)
            .then(promptId => {
              const promptRef = new PromptRefDTO();
              promptRef.promptId = promptId;

              const promptTaskReq = promptTask ?? new PromptTaskDTO();
              promptTaskReq.promptRef = promptRef;

              promptTaskApi?.createPromptTask(promptTaskReq)
                .then(promptTaskId => {
                  request.chatPromptTaskId = promptTaskId;
                  updateBackend(backend.backendId, request);
                })
                .catch(handleError);
            })
            .catch(handleError);
        })
        .catch(handleError);
    }
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
            disabled={saved || visibility==='hidden'}
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
        </ButtonGroup>
      </Box>

      <Divider />

      <CommonContainer sx={{ alignItems: 'flex-start' }}>
        <Stack spacing={3} sx={{
          minWidth: { sm: '16rem' },
          my: 2,
          flex: 1,
        }}>
          <CommonBox sx={{gap: 2}}>
            <Typography level="title-lg" color="primary">
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
          </CommonBox>
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

          <CommonBox>
            <Typography level="title-lg" color="primary">
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
            {(tag !== null) && (
              <form onSubmit={handleTagSubmit}>
                <TinyInput
                  autoFocus
                  type="text"
                  value={tag}
                  onChange={(event => setTag(event.target.value))}
                  endDecorator={<TransitEnterexitRounded fontSize="small" />}
                />
              </form>
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
          </CommonBox>

          <Divider>{t('Character\'s information, significantly influences chat feedback')}</Divider>

          <Card sx={{
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'stretch',
            mx: 'auto',
            gap: 2,
          }}>
            <Stack
              direction="row"
              spacing={3}
              sx={{ display: 'flex', my: 1 }}
            >
              <Box alignItems="center" sx={{
                flex: 1,
                display: 'grid',
                gridTemplateColumns: 'auto 1fr',
                gap: 1,
              }}>
                <LabelTypography>
                  {t('account:Nickname')}
                </LabelTypography>
                <Input
                  disabled={!editEnabled}
                  name="nickname"
                  value={nickname || ''}
                  onChange={(event) => setNickname(event.target.value || undefined)}
                />
                <LabelTypography>
                  {t('account:Gender')}
                </LabelTypography>
                <RadioGroup
                  name="genderGroup"
                  orientation="horizontal"
                  value={gender}
                  onChange={(event) => setGender(event.target.value)}
                  sx={{ my: 1 }}
                >
                  <Radio value="male" label={t('account:Male')} disabled={!editEnabled} />
                  <Radio value="female" label={t('account:Female')} disabled={!editEnabled} />
                  <Radio value="other" label={t('account:Other')} disabled={!editEnabled} />
                </RadioGroup>

                <LabelTypography>
                  {t('Language')}
                </LabelTypography>
                <Select
                  size="sm"
                  variant="outlined"
                  value={lang}
                  onChange={(_event, value) => value && setLang(value)}
                  sx={{mr: 'auto'}}
                >
                  {Object.keys(locales).map((locale) => (
                    <Option key={`locale-${locale}`} value={locale}>
                      {locales[locale]}
                    </Option>
                  ))}
                </Select>
              </Box>
                
              <Stack direction="column" spacing={1} sx={{ minWidth: 120 }}>
                <AspectRatio
                  ratio="1"
                  maxHeight={200}
                  sx={{ flex: 1, borderRadius: '50%' }}
                >
                  <Avatar variant="soft" src={avatar} />
                </AspectRatio>
                <ImagePicker
                  key="avatar-picker"
                  onImageSelect={handleAvatarSelect}
                  previewProps={{
                    maxWidth: '200px',
                    maxHeight: '200px',
                    borderRadius: '50%',
                  }}
                  disabled={!editEnabled}
                  size="sm"
                  variant="outlined"
                  color="neutral"
                  sx={{
                    bgcolor: 'background.body',
                    position: 'absolute',
                    zIndex: 2,
                    borderRadius: '50%',
                    right: 20,
                    top: 110,
                    boxShadow: 'sm',
                  }}
                />
              </Stack>
            </Stack>

            <LabelTypography>
              {t('Profile')}
            </LabelTypography>
            <ContentTextarea
              name="info-profile"
              minRows={3}
              value={profile}
              onChange={(event) => setProfile(event.target.value)}
            />

            <LabelTypography>
              {t('Chat Style')}
            </LabelTypography>
            <ContentTextarea
              name="info-chat-style"
              minRows={1}
              value={chatStyle || ''}
              onChange={(event) => setChatStyle(event.target.value || undefined)}
            />

            <LabelTypography>
              {t('Chat Example')}
            </LabelTypography>
            <ContentTextarea
              name="info-chat-example"
              minRows={3}
              value={chatExample || ''}
              onChange={(event) => setChatExample(event.target.value || undefined)}
            />

            <LabelTypography>
              {t('Greeting')}
            </LabelTypography>
            <ContentTextarea
              name="info-greeting"
              minRows={1}
              value={greeting || ''}
              onChange={(event) => setGreeting(event.target.value || undefined)}
            />
          </Card>

          <CharacterBackends
            characterId={id}
            defaultBackends={backends}
            editMode={true}
            onEdit={handleBackendEdit}
          />
          <LinePlaceholder />

          <LabelTypography>
            {t('Album')}
          </LabelTypography>
          <CharacterAlbum
            characterId={id}
            picture={picture}
            setPicture={setPicture}
          />
          <LinePlaceholder />
        </Stack>

        {editBackend ? (
          <CharacterBackendSettings
            backend={{...editBackend}}
            onSave={handleBackendUpdated}
            onCancel={() => setEditBackend(undefined)}
            sx={{
              width: { xs: '100%', sm: '21rem' }
          }}/>
        ) : (
          <CharacterGuide sx={{
            display: { xs: 'none', md: 'inherit' },
            width: '21rem',
          }}/>
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
    </>
  );
}