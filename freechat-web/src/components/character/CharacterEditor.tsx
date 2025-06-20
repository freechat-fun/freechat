import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import {
  CharacterBackendDTO,
  CharacterBackendDetailsDTO,
  CharacterDetailsDTO,
  CharacterUpdateDTO,
  ChatCreateDTO,
  PromptRefDTO,
  PromptTaskDTO,
} from 'freechat-sdk';
import { formatDate, getDateLabel } from '../../libs/date_utils';
import { locales } from '../../configs/i18n-config';
import {
  FlexBox,
  DynamicFlexBox,
  ConfirmModal,
  ContentTextarea,
  ImagePicker,
  LabelTypography,
  LinePlaceholder,
  OptionTooltip,
  RouterBlocker,
  StyledStack,
  TinyInput,
} from '../../components';
import {
  Avatar,
  Box,
  Button,
  ButtonGroup,
  Chip,
  Divider,
  FormControl,
  FormControlLabel,
  FormHelperText,
  IconButton,
  MenuItem,
  Radio,
  RadioGroup,
  Select,
  Stack,
  Switch,
  TextField,
  Typography,
} from '@mui/material';
import {
  AddCircleRounded,
  CheckRounded,
  EditRounded,
  ImportExportRounded,
  InfoOutlined,
  IosShareRounded,
  SaveAltRounded,
  TransitEnterexitRounded,
} from '@mui/icons-material';
import {
  CharacterAlbumPane,
  CharacterBackendSettings,
  CharacterBackendsPane,
  CharacterDocumentsPane,
  CharacterGuide,
} from '../../components/character';
import { HelpIcon } from '../../components/icon';
import { createPromptForCharacter } from '../../libs/chat_utils';
import { getCompressedImage } from '../../libs/ui_utils';
import { objectsEqual } from '../../libs/js_utils';
import { exportCharacter } from '../../libs/character_utils';
import { InputAdornment } from '@mui/material';

type CharacterEditorProps = {
  id: number;
};

export default function CharacterEditor({ id }: CharacterEditorProps) {
  const navigate = useNavigate();
  const { t, i18n } = useTranslation(['character', 'account', 'button']);
  const { accountApi, promptApi, promptTaskApi, characterApi, chatApi } =
    useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useMetaInfoContext();

  const [origRecord, setOrigRecord] = useState(new CharacterDetailsDTO());
  const [editRecordName, setEditRecordName] = useState<string | null>(null);
  const [editRecordNameError, setEditRecordNameError] = useState(false);

  const [recordUid, setRecordUid] = useState<string>();
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
  const [defaultScene, setDefaultScene] = useState<string>();

  const [visibility, setVisibility] = useState<string>();
  const [tags, setTags] = useState<string[]>([]);
  const [tag, setTag] = useState<string>();

  const [editBackend, setEditBackend] = useState<CharacterBackendDetailsDTO>();
  const [backends, setBackends] = useState<Array<CharacterBackendDetailsDTO>>(
    []
  );

  const [editEnabled, setEditEnabled] = useState(false);

  const originName = useRef('');
  const backendRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (id) {
      characterApi
        ?.getCharacterDetails(id)
        .then(setOrigRecord)
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
        } catch {
          console.log(`[WARNING] Invalid draft content: ${origRecord.draft}`);
        }
      }

      const draftRecord = { ...origRecord, ...draft };

      originName.current = draftRecord.name ?? '';

      setRecordUid(draftRecord.characterUid);
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
      setDefaultScene(draftRecord.defaultScene);
      setVisibility(draftRecord.visibility ?? 'private');
      setTags(draftRecord.tags ?? []);
      setEditEnabled(true);
    }
  }, [origRecord, username]);

  useEffect(() => {
    if (editBackend && backendRef.current) {
      backendRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [editBackend]);

  function handleNameChange(): void {
    if (editRecordNameError) {
      return;
    }

    if (editRecordName && editRecordName !== recordName) {
      if (editRecordName === originName.current) {
        setRecordName(editRecordName);
        setEditRecordName(null);
      } else {
        characterApi
          ?.existsCharacterName(editRecordName)
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

  function handleTagDelete(tagDeleted: string): void {
    if (tags) {
      setTags(tags.filter((tag) => tagDeleted !== tag));
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

  function handleAvatarSelect(file: Blob, name: string) {
    if (!id) {
      return;
    }
    getCompressedImage(file, 100 * 1024)
      .then((imageInfo) => {
        const request = new File([imageInfo.blob], name);
        if (recordUid) {
          characterApi
            ?.uploadCharacterAvatar(recordUid, request)
            .then((url) => setAvatar(url))
            .catch(handleError);
        }
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

    characterApi
      ?.updateCharacter(id, request)
      .then((resp) => {
        if (!resp) {
          return;
        }
        characterApi
          ?.getCharacterDetails(id)
          .then(setOrigRecord)
          .catch(handleError);
      })
      .catch(handleError);
  }

  function handleRecordPublish(): void {
    if (!id) {
      return;
    }
    const onUpdated = (
      currentId: number,
      visibility: string,
      nickname: string,
      about: string | undefined
    ) => {
      characterApi
        ?.publishCharacter(currentId, visibility)
        .then(() => {
          chatApi
            ?.getDefaultChatId(recordUid as string)
            .then((resp) => {
              navigate(`/w/chat/${resp}/debug`);
            })
            .catch(() => {
              accountApi
                ?.getUserDetails()
                .then((userDetails) => {
                  const request = new ChatCreateDTO();
                  request.userNickname =
                    userDetails.nickname ?? userDetails.username;
                  request.userProfile = userDetails.profile;
                  request.characterNickname = nickname;
                  request.characterUid = recordUid as string;
                  request.about = about;

                  chatApi
                    .startChat(request)
                    .then((chatId) => {
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
    characterApi
      ?.updateCharacter(id, request)
      .then((resp) => {
        if (resp) {
          characterApi
            ?.getCharacterDetails(id)
            .then((newRecord) => {
              setOrigRecord(newRecord);
              onUpdated(
                editRecord.characterId as number,
                editRecord.visibility === 'private' ? 'private' : 'public',
                editRecord.nickname ?? editRecord.name ?? '',
                editRecord.defaultScene
              );
            })
            .catch(handleError);
        }
      })
      .catch(handleError);
  }

  function handleRecordExport(): void {
    if (!id) {
      return;
    }
    setEditEnabled(false);
    exportCharacter(id)
      .catch(handleError)
      .finally(() => setEditEnabled(true));
  }

  function recordToUpdateRequest(
    record: CharacterDetailsDTO
  ): CharacterUpdateDTO {
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
    request.defaultScene = record.defaultScene;
    request.name = record.name ?? `untitiled-${formatDate(new Date())}`;
    request.draft = '';
    request.tags = record.tags;
    request.visibility = record.visibility === 'private' ? 'private' : 'public';

    return request;
  }

  function handleBackendEdit(
    backend: CharacterBackendDetailsDTO,
    backends: CharacterBackendDetailsDTO[]
  ) {
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
    redirectHash?: string
  ) {
    const request = new CharacterBackendDTO();
    request.chatPromptTaskId = backend.chatPromptTaskId;
    request.greetingPromptTaskId = backend.greetingPromptTaskId;
    request.isDefault = backend.isDefault;
    request.messageWindowSize = backend.messageWindowSize;
    request.longTermMemoryWindowSize = backend.longTermMemoryWindowSize;
    request.proactiveChatWaitingTime = backend.proactiveChatWaitingTime;
    request.moderationApiKeyName = backend.moderationApiKeyName;
    request.moderationModelId = backend.moderationModelId;
    request.moderationParams = backend.moderationParams;
    request.quotaType = backend.quotaType;
    request.initQuota = backend.initQuota;
    request.enableAlbumTool = backend.enableAlbumTool;
    request.enableTts = backend.enableTts;
    request.ttsSpeakerIdx = backend.ttsSpeakerIdx;
    request.ttsSpeakerWav = backend.ttsSpeakerWav;
    request.ttsSpeakerType = backend.ttsSpeakerType;

    const updateBackend = (
      backendId: string | undefined,
      req: CharacterBackendDTO
    ) => {
      if (backendId) {
        characterApi
          ?.updateCharacterBackend(backendId, req)
          .then(() => {
            if (redirectToChatPrompt && req.chatPromptTaskId) {
              saveAndNavigate(req.chatPromptTaskId, redirectHash);
            } else {
              setBackends((prevBackends) => {
                const others = prevBackends.filter(
                  (prevBackend) => prevBackend.backendId !== backendId
                );
                return [
                  ...others,
                  {
                    ...backend,
                    backendId: backendId,
                    chatPromptTaskId: req.chatPromptTaskId,
                  },
                ];
              });
              setEditBackend(undefined);
            }
          })
          .catch(handleError);
      } else if (recordUid) {
        characterApi?.addCharacterBackend(recordUid, req).then((bId) => {
          if (redirectToChatPrompt && req.chatPromptTaskId) {
            saveAndNavigate(req.chatPromptTaskId, redirectHash);
          } else {
            setBackends((prevBackends) => {
              return [
                ...prevBackends,
                {
                  ...backend,
                  backendId: bId,
                  chatPromptTaskId: req.chatPromptTaskId,
                },
              ];
            });
            setEditBackend(undefined);
          }
        });
      }
    };

    if (backend.chatPromptTaskId) {
      if (promptTask) {
        promptTaskApi
          ?.updatePromptTask(backend.chatPromptTaskId, promptTask)
          .then(() => updateBackend(backend.backendId, request))
          .catch(handleError);
      } else {
        updateBackend(backend.backendId, request);
      }
    } else {
      const promptReq = createPromptForCharacter(recordName, lang);
      promptApi
        ?.newPromptName(recordName ?? 'untitled')
        .then((name) => {
          promptReq.name = name;
          promptApi
            ?.createPrompt(promptReq)
            .then((promptId) => {
              const promptRef = new PromptRefDTO();
              promptRef.promptId = promptId;

              const promptTaskReq = promptTask ?? new PromptTaskDTO();
              promptTaskReq.promptRef = promptRef;

              promptTaskApi
                ?.createPromptTask(promptTaskReq)
                .then((promptTaskId) => {
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

  function getEditRecord(): CharacterDetailsDTO {
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
    newRecord.defaultScene = defaultScene;
    newRecord.visibility = visibility;
    newRecord.tags = [...tags];
    newRecord.characterUid = origRecord.characterUid;

    return newRecord;
  }

  function isSaved(): boolean {
    if (origRecord.username !== username) {
      return false;
    }

    let draft = {};
    if (origRecord.draft) {
      try {
        draft = (JSON.parse(origRecord.draft) as CharacterDetailsDTO) ?? {};
      } catch {
        console.log(`[WARNING] Invalid draft content: ${origRecord.draft}`);
      }
    }

    const draftRecord = { ...origRecord, ...draft };

    return (
      objectsEqual(draftRecord.name ?? '', recordName) &&
      objectsEqual(draftRecord.nickname, nickname) &&
      objectsEqual(draftRecord.description, description) &&
      objectsEqual(draftRecord.avatar, avatar) &&
      objectsEqual(draftRecord.picture, picture) &&
      objectsEqual(draftRecord.gender ?? 'other', gender) &&
      objectsEqual(draftRecord.lang ?? 'English', lang) &&
      objectsEqual(draftRecord.profile, profile) &&
      objectsEqual(draftRecord.greeting, greeting) &&
      objectsEqual(draftRecord.chatStyle, chatStyle) &&
      objectsEqual(draftRecord.chatExample, chatExample) &&
      objectsEqual(draftRecord.defaultScene, defaultScene) &&
      objectsEqual(draftRecord.visibility ?? 'private', visibility) &&
      objectsEqual(draftRecord.tags ?? [], tags)
    );
  }

  function saveAndNavigate(promptTaskId: string, hash?: string): void {
    const draftRecord = getEditRecord();
    draftRecord.characterId = undefined;
    draftRecord.draft = undefined;

    const request = new CharacterUpdateDTO();
    request.draft = JSON.stringify(draftRecord);

    characterApi
      ?.updateCharacter(id, request)
      .finally(() =>
        navigate(`/w/prompt/task/edit/${promptTaskId}${hash ?? ''}`)
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
        <DynamicFlexBox
          sx={{
            alignItems: 'center',
            flex: 1,
          }}
        >
          <Typography variant="h4">{recordName}</Typography>
          <IconButton
            disabled={!!editRecordName}
            size="small"
            onClick={() => setEditRecordName(recordName || 'untitled')}
          >
            <EditRounded fontSize="small" />
          </IconButton>
        </DynamicFlexBox>
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
            disabled={isSaved() || visibility === 'hidden' || !editEnabled}
            startIcon={isSaved() ? <CheckRounded /> : <SaveAltRounded />}
            onClick={handleRecordSave}
            sx={{
              borderRadius: '16px',
            }}
          >
            {t('button:Save')}
          </Button>
          <Button
            disabled={!editEnabled}
            startIcon={<IosShareRounded />}
            onClick={handleRecordPublish}
            sx={{
              borderRadius: '16px',
            }}
          >
            {t('button:Publish')}
          </Button>
          <Button
            disabled={!editEnabled}
            startIcon={<ImportExportRounded />}
            onClick={handleRecordExport}
            sx={{
              borderRadius: '16px',
            }}
          >
            {t('button:Export')}
          </Button>
        </ButtonGroup>
      </Box>

      <Divider />

      <DynamicFlexBox sx={{ alignItems: 'flex-start' }}>
        <Stack
          spacing={3}
          sx={{
            minWidth: { sm: '16rem' },
            my: 2,
            flex: 1,
          }}
        >
          <FlexBox sx={{ gap: 2 }}>
            <Typography variant="h6" color="primary">
              {t('Public')}
            </Typography>
            <Switch
              checked={visibility === 'public' || visibility === 'hidden'}
              color="success"
              onChange={(event) =>
                event.target.checked
                  ? setVisibility('public')
                  : setVisibility('private')
              }
            />
          </FlexBox>
          <Stack
            spacing={1}
            sx={{
              minWidth: { sm: '12rem' },
            }}
          >
            <FlexBox>
              <Typography variant="h6" color="primary">
                {t('Description')}
              </Typography>
              <OptionTooltip
                placement="right"
                title={t('Supports markdown format')}
              >
                <HelpIcon />
              </OptionTooltip>
            </FlexBox>
            <ContentTextarea
              name="info-description"
              minRows={3}
              value={description}
              onChange={(event) => setDescription(event.target.value)}
            />
          </Stack>

          <LinePlaceholder />

          <FlexBox>
            <Typography variant="h6" color="primary">
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
                  autoFocus
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
          </FlexBox>
          <FlexBox>
            {tags.length > 0 &&
              tags.map((tag, index) => (
                <Chip
                  variant="outlined"
                  color="success"
                  key={`tag-${tag}-${index}`}
                  onDelete={() => handleTagDelete(tag)}
                  label={tag}
                />
              ))}
          </FlexBox>

          <Divider>
            {t(
              "Character's information, significantly influences chat feedback"
            )}
          </Divider>

          <StyledStack
            spacing={2}
            sx={{ '&:hover, &:focus-within': { transform: 'none' } }}
          >
            <Stack direction="row" spacing={3} sx={{ display: 'flex', my: 1 }}>
              <Box
                sx={{
                  flex: 1,
                  display: 'grid',
                  gridTemplateColumns: 'auto 1fr',
                  gap: 1,
                  alignItems: 'center',
                }}
              >
                <LabelTypography>{t('account:Nickname')}</LabelTypography>
                <TextField
                  disabled={!editEnabled}
                  name="nickname"
                  value={nickname || ''}
                  onChange={(event) =>
                    setNickname(event.target.value || undefined)
                  }
                  size="small"
                />
                <LabelTypography>{t('account:Gender')}</LabelTypography>
                <RadioGroup
                  name="genderGroup"
                  row
                  value={gender}
                  onChange={(event) => setGender(event.target.value)}
                  sx={{ my: 1 }}
                >
                  <FormControlLabel
                    value="male"
                    control={<Radio disabled={!editEnabled} />}
                    label={t('account:Male')}
                  />
                  <FormControlLabel
                    value="female"
                    control={<Radio disabled={!editEnabled} />}
                    label={t('account:Female')}
                  />
                  <FormControlLabel
                    value="other"
                    control={<Radio disabled={!editEnabled} />}
                    label={t('account:Other')}
                  />
                </RadioGroup>

                <LabelTypography>{t('Language')}</LabelTypography>
                <FormControl sx={{ flex: 1 }} size="small">
                  <Select
                    size="small"
                    value={lang}
                    onChange={(event) => setLang(event.target.value)}
                    sx={{ mr: 'auto' }}
                  >
                    {Object.keys(locales).map((locale) => (
                      <MenuItem key={`locale-${locale}`} value={locale}>
                        {locales[locale]}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Box>

              <Stack
                direction="column"
                spacing={1}
                sx={{ minWidth: 120, position: 'relative' }}
              >
                <Box
                  sx={{
                    position: 'relative',
                    width: '100%',
                    paddingBottom: '100%',
                    borderRadius: '50%',
                    overflow: 'hidden',
                  }}
                >
                  <Avatar
                    src={avatar}
                    sx={{
                      position: 'absolute',
                      top: 0,
                      left: 0,
                      width: '100%',
                      height: '100%',
                    }}
                  />
                </Box>
                <ImagePicker
                  key="avatar-picker"
                  onImageSelect={handleAvatarSelect}
                  previewProps={{
                    maxWidth: '200px',
                    maxHeight: '200px',
                    borderRadius: '50%',
                  }}
                  disabled={!editEnabled}
                  sx={{
                    backgroundColor: 'background.paper',
                    position: 'absolute',
                    zIndex: 2,
                    borderRadius: '50%',
                    right: 5,
                    top: 85,
                    boxShadow: 1,
                    '&:hover, &:focus-within': {
                      backgroundColor: 'background.paper',
                    },
                  }}
                />
              </Stack>
            </Stack>

            <LabelTypography>{t('Profile')}</LabelTypography>
            <ContentTextarea
              name="info-profile"
              minRows={3}
              value={profile}
              onChange={(event) => setProfile(event.target.value)}
            />

            <LabelTypography>{t('Chat Style')}</LabelTypography>
            <ContentTextarea
              name="info-chat-style"
              minRows={1}
              value={chatStyle || ''}
              onChange={(event) =>
                setChatStyle(event.target.value || undefined)
              }
            />

            <LabelTypography>{t('Chat Example')}</LabelTypography>
            <ContentTextarea
              name="info-chat-example"
              minRows={3}
              value={chatExample || ''}
              onChange={(event) =>
                setChatExample(event.target.value || undefined)
              }
            />

            <FlexBox>
              <LabelTypography>{t('Default Scene')}</LabelTypography>
              <OptionTooltip
                placement="right"
                title={t(
                  'The default scene will be set as the default conversation background information when creating a new chat.'
                )}
              >
                <HelpIcon />
              </OptionTooltip>
            </FlexBox>
            <ContentTextarea
              name="info-default-scene"
              minRows={1}
              value={defaultScene || ''}
              onChange={(event) =>
                setDefaultScene(event.target.value || undefined)
              }
            />

            <LabelTypography>{t('Greeting')}</LabelTypography>
            <ContentTextarea
              name="info-greeting"
              minRows={1}
              value={greeting || ''}
              onChange={(event) => setGreeting(event.target.value || undefined)}
            />
          </StyledStack>
          <LinePlaceholder />

          <StyledStack
            sx={{ '&:hover, &:focus-within': { transform: 'none' } }}
          >
            <CharacterDocumentsPane characterUid={recordUid} editMode={true} />
          </StyledStack>
          <LinePlaceholder />

          <StyledStack
            sx={{ '&:hover, &:focus-within': { transform: 'none' } }}
          >
            <CharacterBackendsPane
              characterUid={recordUid}
              defaultBackends={backends}
              editMode={true}
              onEdit={handleBackendEdit}
            />
          </StyledStack>
          <LinePlaceholder />

          <FlexBox>
            <LabelTypography sx={{ mr: 0 }}>{t('Album')}</LabelTypography>
            <OptionTooltip
              placement="right"
              title={t(
                'Maximum of 10 pictures are allowed, one of which can be selected as the chat background'
              )}
            >
              <HelpIcon />
            </OptionTooltip>
          </FlexBox>
          <CharacterAlbumPane
            characterUid={recordUid}
            picture={picture}
            setPicture={setPicture}
          />
          <LinePlaceholder />
        </Stack>

        {editBackend ? (
          <CharacterBackendSettings
            ref={backendRef}
            backend={{ ...editBackend }}
            onSave={handleBackendUpdated}
            onCancel={() => setEditBackend(undefined)}
            sx={{
              width: { xs: '100%', sm: '24rem' },
            }}
          />
        ) : (
          <CharacterGuide
            sx={{
              display: { xs: 'none', md: 'inherit' },
              width: '24rem',
            }}
          />
        )}
      </DynamicFlexBox>

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
          <TextField
            name="RecordName"
            value={editRecordName ?? ''}
            onChange={(event) => {
              setEditRecordName(event.target.value);
              setEditRecordNameError(false);
            }}
            size="small"
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
