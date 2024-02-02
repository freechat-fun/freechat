import { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate, useParams } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext, useUserInfoContext } from "../../contexts";
import { CharacterDetailsDTO, CharacterUpdateDTO } from "freechat-sdk";
import { formatDate, getDateLabel } from "../../libs/date_utils";
import { locales } from "../../configs/i18n-config";
import { CommonBox, CommonContainer, ConfirmModal, ContentTextarea, ImagePicker, LabelTypography, LinePlaceholder, TinyInput } from "../../components";
import { AspectRatio, Avatar, Box, Button, ButtonGroup, Card, Chip, ChipDelete, Divider, FormControl, FormHelperText, IconButton, Input, Option, Radio, RadioGroup, Select, Stack, Switch, Tooltip, Typography, switchClasses } from "@mui/joy";
import { AddCircleRounded, CheckRounded, EditRounded, HelpOutlineRounded, InfoOutlined, IosShareRounded, SaveAltRounded } from "@mui/icons-material";
import { CharacterGuide } from "../../components/character";

export default function CharacterEditor () {
  const navigator = useNavigate();
  const { id } = useParams();
  const { t, i18n } = useTranslation(['character', 'account', 'button']);
  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useUserInfoContext();

  const [origRecord, setOrigRecord] = useState(new CharacterDetailsDTO());
  const [editRecord, setEditRecord] = useState(new CharacterDetailsDTO());
  const [originName, setOriginName] = useState<string>();
  const [editRecordName, setEditRecordName] = useState<string>();
  const [editRecordNameError, setEditRecordNameError] = useState(false);

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
  const [tag, setTag] = useState<string>();

  const [editEnabled, setEditEnabled] = useState(false);
  const [saved, setSaved] = useState(false);
  
  useEffect(() => {
    if (id) {
      characterApi?.getCharacterDetails(id)
        .then(resp => {
          setOrigRecord(resp);
          setEditRecord(resp);
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
      
      setOriginName(draftRecord.name);

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
    setSaved(() => false);
  }, [editRecord]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.nickname = nickname;
      return newRecord;
    });
  }, [nickname]);
  
  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.description = description;
      return newRecord;
    });
  }, [description]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.avatar = avatar;
      return newRecord;
    });
  }, [avatar]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.picture = picture;
      return newRecord;
    });
  }, [picture]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.gender = gender;
      return newRecord;
    });
  }, [gender]);

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
      newRecord.lang = lang;
      return newRecord;
    });
  }, [lang]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.profile = profile;
      return newRecord;
    });
  }, [profile]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.greeting = greeting;
      return newRecord;
    });
  }, [greeting]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.chatStyle = chatStyle;
      return newRecord;
    });
  }, [chatStyle]);

  useEffect(() => {
    setEditRecord(prevRecord => {
      const newRecord = { ...prevRecord };
      newRecord.chatExample = chatExample;
      return newRecord;
    });
  }, [chatExample]);

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
      newRecord.tags = tags;
      return newRecord;
    });
  }, [tags]);

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
        characterApi?.existsCharacterName(editRecordName)
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

  function handleTagDelete(tagDeleted: string): void {
    tags && setTags(tags.filter(tag => tagDeleted !== tag));
  }

  function handleTagSubmit(event: React.FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    tags && tag && !tags.includes(tag) && setTags([...tags, tag]);
    setTag(undefined);
  }

  function handleImageSelect(file: Blob, name: string) {
    const request = new File([file], name);
    characterApi?.uploadCharacterAvatar(request)
      .then(url => setAvatar(url))
      .catch(handleError);
  }

  function handleRecordSave(): void {
    if (!editRecord.characterId) {
      return;
    }
    const draftRecord = {...editRecord};
    draftRecord.characterId = undefined;

    const request = new CharacterUpdateDTO();
    request.draft = JSON.stringify(draftRecord);

    characterApi?.updateCharacter(editRecord.characterId, request)
      .then(setSaved)
      .catch(handleError);
  }

  function handleRecordPublish(): void {
    if (!editRecord.characterId) {
      return;
    }
    const onSaved = (id: string, visibility: string) => {
      characterApi?.publishCharacter1(id, visibility)
        .then(resp => {
          if (!resp) {
            return;
          }
          navigator(`/w/character/${resp}`);
        })
        .catch(handleError);
    };

    const request = recordToUpdateRequest(editRecord);
    characterApi?.updateCharacter(editRecord.characterId, request)
      .then(resp => {
        setSaved(resp);
        if (resp) {
          onSaved(editRecord.characterId as string, editRecord.visibility === 'private' ? 'private' : 'public');
        }
      })
      .catch(handleError);
  }

  function recordToUpdateRequest(record: CharacterDetailsDTO): CharacterUpdateDTO {
    const request = new CharacterUpdateDTO();
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
          <Typography level="h3">{editRecord?.name}</Typography>
          <IconButton
            disabled={!!editRecordName}
            size="sm"
            onClick={() => setEditRecordName(editRecord?.name || 'untitled-1')}
          >
            <EditRounded fontSize="small" />
          </IconButton>
        </CommonContainer>
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
          mt: 2,
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
              <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" placement="right" title={t('Supports Markdown format')}>
                <HelpOutlineRounded fontSize="small" />
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
                  placeholder={nickname}
                  value={nickname}
                  onChange={(event) => setNickname(event.target.value)}
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
                  onImageSelect={handleImageSelect}
                  disabled={!editEnabled}
                  aria-label="upload new picture"
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
              minRows={3}
              value={chatStyle}
              onChange={(event) => setChatStyle(event.target.value)}
            />

            <LabelTypography>
              {t('Chat Example')}
            </LabelTypography>
            <ContentTextarea
              name="info-chat-example"
              minRows={3}
              value={chatExample}
              onChange={(event) => setChatExample(event.target.value)}
            />

            <LabelTypography>
              {t('Greeting')}
            </LabelTypography>
            <ContentTextarea
              name="info-greeting"
              minRows={1}
              value={greeting}
              onChange={(event) => setGreeting(event.target.value)}
            />
          </Card>

        </Stack>
        
        <CharacterGuide />

      </CommonContainer>

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