import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate, useParams } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext, useUserInfoContext } from "../../contexts";
import { CharacterDetailsDTO, CharacterUpdateDTO } from "freechat-sdk";
import { formatDate, getDateLabel } from "../../libs/date_utils";
import { CommonBox, CommonContainer, ContentTextarea, LinePlaceholder } from "../../components";
import { Box, Button, ButtonGroup, Divider, IconButton, Stack, Textarea, Tooltip, Typography } from "@mui/joy";
import { CheckRounded, EditRounded, HelpOutlineRounded, IosShareRounded, SaveAltRounded } from "@mui/icons-material";

export default function CharacterEditor () {
  const navigator = useNavigate();
  const { id } = useParams();
  const { t, i18n } = useTranslation(['character', 'button']);
  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useUserInfoContext();

  const [origRecord, setOrigRecord] = useState(new CharacterDetailsDTO());
  const [editRecord, setEditRecord] = useState(new CharacterDetailsDTO());
  const [originName, setOriginName] = useState<string>();
  const [editRecordName, setEditRecordName] = useState<string>();
  const [editRecordNameError, setEditRecordNameError] = useState(false);

  const [description, setDescription] = useState<string>();
  const [avatar, setAvatar] = useState<string>();
  const [picture, setPicture] = useState<string>();
  const [gender, setGender] = useState('other');
  const [lang, setLang] = useState('English');
  const [profile, setProfile] = useState<string>();
  const [greeting, setGreeting] = useState<string>();
  const [chatStyle, setChatStyle] = useState<string>();
  const [chatExample, setChatExample] = useState<string>();
  const [experience, setExperience] = useState<string>();

  const [visibility, setVisibility] = useState<string>();
  const [tags, setTags] = useState<string[]>([]);
  const [tag, setTag] = useState<string>();

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

      setDescription(draftRecord.description);
      setAvatar(draftRecord.avatar);
      setPicture(draftRecord.picture);
      setGender(draftRecord.gender ?? 'other');
      setLang(draftRecord.lang ?? 'English');
      setProfile(draftRecord.profile);
      setGreeting(draftRecord.greeting);
      setChatStyle(draftRecord.chatStyle);
      setChatExample(draftRecord.chatExample);
      setExperience(draftRecord.experience);
      setVisibility(draftRecord.visibility ?? 'private');
      setTags(draftRecord.tags ?? []);
    }
  }, [origRecord, username]);

  useEffect(() => {
    setSaved(() => false);
  }, [editRecord]);
  
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
      newRecord.experience = experience;
      return newRecord;
    });
  }, [experience]);

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

    if (!saved) {
      const request = recordToUpdateRequest(editRecord);
      characterApi?.updateCharacter(editRecord.characterId, request)
          .then(resp => {
            setSaved(resp);
            if (resp) {
              onSaved(editRecord.characterId as string, editRecord.visibility === 'private' ? 'private' : 'public');
            }
          })
          .catch(handleError);
    } else {
      onSaved(editRecord.characterId as string, editRecord.visibility === 'private' ? 'private' : 'public');
    }
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
    request.experience = record.experience;
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

            <Divider>{t('Character\'s information, significantly influences chat feedback')}</Divider>

          </Stack>
          </CommonContainer>
        </CommonContainer>
      </Box>
    </>
  );
}