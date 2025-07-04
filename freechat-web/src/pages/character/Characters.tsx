import {
  createRef,
  forwardRef,
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  FlexBox,
  ConfirmModal,
  InfoCardCover,
  InfoSearchbar,
  LinePlaceholder,
  StyledStack,
  SummaryTypography,
} from '../../components';
import {
  CharacterCreateDTO,
  CharacterQueryDTO,
  CharacterQueryWhere,
  CharacterSummaryDTO,
  ChatCreateDTO,
} from 'freechat-sdk';
import {
  Avatar,
  Box,
  Button,
  ButtonGroup,
  Chip,
  DialogContentText,
  FormControl,
  FormHelperText,
  IconButton,
  TextField,
  Typography,
} from '@mui/material';
import { SxProps, Theme } from '@mui/material/styles';
import {
  AddCircleRounded,
  DeleteForeverRounded,
  ImportExportRounded,
  InfoOutlined,
  KeyboardArrowLeftRounded,
  KeyboardArrowRightRounded,
  SaveAltRounded,
  SmsRounded,
} from '@mui/icons-material';
import { Transition } from 'react-transition-group';
import { getDateLabel } from '../../libs/date_utils';
import {
  defaultTransitionInterval,
  defaultTransitionSetting,
  initTransitionSequence,
  transitionStyles,
} from '../../libs/ui_utils';
import { i18nConfig } from '../../configs/i18n-config';
import { exportCharacter } from '../../libs/character_utils';

let idCounter = 0;

type RecordCardProps = {
  record: CharacterSummaryDTO;
  onView: () => void;
  onEdit: () => void;
  onDownload: () => void;
  onDelete: () => void;
  sx?: SxProps<Theme>;
};

const RecordCard = forwardRef<HTMLDivElement, RecordCardProps>((props, ref) => {
  const { record, onView, onEdit, onDownload, onDelete, sx } = props;
  const { t, i18n } = useTranslation();

  const characterName = record.nickname ?? record.name;

  return (
    <StyledStack
      ref={ref}
      sx={{
        ...sx,
        position: 'relative',
        width: '100%',
        overflow: 'hidden',
        gap: 1,
        boxShadow: 0,
        backgroundColor: 'background.paper',
        '&:hover, &:focus-within': {
          boxShadow: 0,
        },
        m: 0,
      }}
    >
      <FlexBox>
        <Avatar alt={characterName} src={record.avatar}>
          {characterName}
        </Avatar>
        <Typography
          variant="h6"
          sx={{
            ...sx,
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            maxWidth: '20rem',
          }}
        >
          {record.name}
        </Typography>
      </FlexBox>
      <Box
        sx={{
          ...sx,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <Typography variant="body2" color="text.secondary">
          {getDateLabel(record.gmtModified || new Date(0), i18n.language)}
        </Typography>
        <Typography
          variant="body2"
          color={
            record.visibility === 'public' ? 'success.main' : 'warning.main'
          }
        >
          {record.visibility === 'public' ? t('public') : t('private')}
        </Typography>
      </Box>
      <Box
        sx={{
          ...sx,
          display: 'flex',
          justifyContent: 'start',
          alignItems: 'center',
          gap: 2,
        }}
      >
        <Chip
          label={`v${record.version}`}
          color="success"
          variant="outlined"
          size="small"
        />
      </Box>
      <SummaryTypography>{record.description}</SummaryTypography>
      <LinePlaceholder spacing={2} />
      <InfoCardCover
        icons={{ view: SmsRounded }}
        onView={() => onView()}
        onEdit={() => onEdit()}
        onDownload={() => onDownload()}
        onDelete={() => onDelete()}
      />
    </StyledStack>
  );
});

RecordCard.displayName = 'Characters.RecordCard';

export default function Characters() {
  const navigate = useNavigate();
  const { t } = useTranslation(['character']);
  const { accountApi, characterApi, chatApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 6;

  const [records, setRecords] = useState<CharacterSummaryDTO[]>([]);
  const [query, setQuery] = useState<CharacterQueryDTO>(defaultQuery(pageSize));
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [recordDeleted, setRecordDeleted] = useState<CharacterSummaryDTO>();

  const [editRecordName, setEditRecordName] = useState<string>();
  const [editRecordNameError, setEditRecordNameError] = useState(false);

  const [showCards, setShowCards] = useState(false);
  const [showCardsFinish, setShowCardsFinish] = useState(false);

  const [characterUploading, setCharacterUploading] = useState(false);

  const fileInputId = useRef(
    `character-configuration-input-${idCounter}`
  ).current;
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const cardRefs = useRef(Array(pageSize).fill(createRef()));

  const doSearch = useCallback(() => {
    characterApi
      ?.searchCharacterSummary(query)
      .then((resp) => {
        setRecords(resp);
        if (page === 0) {
          characterApi
            ?.countCharacters(query)
            .then(setTotal)
            .catch(handleError);
        }
      })
      .catch(handleError);
  }, [characterApi, handleError, page, query]);

  useEffect(() => {
    idCounter++;
    doSearch();
    return initTransitionSequence(setShowCards, setShowCardsFinish, pageSize);
  }, [doSearch]);

  function defaultQuery(limit: number): CharacterQueryDTO {
    const newQuery = new CharacterQueryDTO();
    newQuery.where = new CharacterQueryWhere();
    newQuery.pageSize = limit;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['modifyTime'];

    return newQuery;
  }

  function handleSearch(text: string | undefined): void {
    const where = new CharacterQueryWhere();
    where.text = text;

    const newQuery = new CharacterQueryDTO();
    newQuery.where = where;
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['modifyTime'];

    setPage(0);
    setQuery(newQuery);
  }

  function handleChangePage(newPage: number): void {
    const newQuery = new CharacterQueryDTO();
    newQuery.where = query?.where;
    newQuery.pageSize = query?.pageSize || pageSize;
    newQuery.orderBy = query?.orderBy;
    newQuery.pageNum = newPage;

    setPage(newPage);
    setQuery(newQuery);
  }

  function handleDelete(record: CharacterSummaryDTO | undefined): void {
    if (record?.name) {
      characterApi
        ?.deleteCharacterByName(record.name)
        .then(() => doSearch())
        .catch(handleError);
    }
    setRecordDeleted(undefined);
  }

  function handleTryDelete(record: CharacterSummaryDTO): void {
    setRecordDeleted(record);
  }

  function handleView(record: CharacterSummaryDTO): void {
    if (record.characterUid) {
      chatApi
        ?.getDefaultChatId(record.characterUid)
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
              request.characterNickname = record.nickname ?? record.name;
              request.characterUid = record.characterUid as string;
              request.about = record.defaultScene;

              chatApi
                .startChat(request)
                .then((chatId) => {
                  navigate(`/w/chat/${chatId}/debug`);
                })
                .catch(handleError);
            })
            .catch(handleError);
        });
    }
  }

  function handleEdit(record: CharacterSummaryDTO): void {
    if (record.characterId) {
      navigate(`/w/character/edit/${record.characterId}`);
    }
  }

  function handleDownload(record: CharacterSummaryDTO): void {
    if (record.characterId) {
      exportCharacter(record.characterId).catch(handleError);
    }
  }

  function handleNameChange(): void {
    if (editRecordNameError || !editRecordName) {
      return;
    }

    characterApi
      ?.existsCharacterName(editRecordName)
      .then((resp) => {
        if (!resp) {
          const request = new CharacterCreateDTO();
          request.lang = i18nConfig.defaultLocale;
          request.name = editRecordName;
          request.visibility = 'public';

          characterApi
            ?.createCharacter(request)
            .then((resp) => {
              if (resp) {
                navigate(`/w/character/edit/${resp}`);
              }
            })
            .catch(handleError);

          setEditRecordName(undefined);
        } else {
          setEditRecordNameError(true);
        }
      })
      .catch(handleError);
  }

  function handleConfFileChange(
    event: React.ChangeEvent<HTMLInputElement>
  ): void {
    const file = event.target.files && event.target.files[0];
    if (file) {
      setCharacterUploading(true);
      characterApi
        ?.importCharacter(file)
        .then(() => doSearch())
        .catch(handleError)
        .finally(() => setCharacterUploading(false));
    }
  }

  function handleConfFileModify(): void {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  }

  function getLabelDisplayedRowsTo(): number {
    const currentCount = (page + 1) * pageSize;
    return total === 0 && records.length > 0
      ? currentCount
      : Math.min(total, currentCount);
  }

  function labelDisplayedRows(from: number, to: number, count: number): string {
    return `${from}-${to} of ${to <= count ? count : `more than ${to}`}`;
  }

  return (
    <>
      <LinePlaceholder />
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'row',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <InfoSearchbar
          onSearch={handleSearch}
          sx={{ width: { sm: '420px' } }}
        />
        <TextField
          disabled={characterUploading}
          type="file"
          id={fileInputId}
          onChange={handleConfFileChange}
          sx={{ display: 'none' }}
          slotProps={{
            htmlInput: {
              ref: fileInputRef,
              accept: '*/tar.gz',
            },
          }}
        />
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
            disabled={characterUploading}
            startIcon={<AddCircleRounded />}
            onClick={() => setEditRecordName('untitled')}
            sx={{
              borderRadius: '16px',
            }}
          >
            {t('button:Create')}
          </Button>
          <label htmlFor={fileInputId}>
            <Button
              disabled={characterUploading}
              startIcon={<ImportExportRounded />}
              onClick={handleConfFileModify}
              sx={{
                borderRadius: '16px',
              }}
            >
              {t('button:Import')}
            </Button>
          </label>
        </ButtonGroup>
      </Box>
      <LinePlaceholder />
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
          gap: 3,
        }}
      >
        {records.map((record, index) => (
          <Transition
            in={showCards}
            timeout={index * defaultTransitionInterval}
            unmountOnExit
            key={`transition-${index}`}
            nodeRef={cardRefs.current[index]}
          >
            {(state) => (
              <RecordCard
                key={record.characterId}
                ref={cardRefs.current[index]}
                record={record}
                onView={() => handleView(record)}
                onEdit={() => handleEdit(record)}
                onDownload={() => handleDownload(record)}
                onDelete={() => handleTryDelete(record)}
                sx={{
                  transition: defaultTransitionSetting,
                  ...transitionStyles[state],
                }}
              />
            )}
          </Transition>
        ))}
      </Box>
      <Box
        sx={{
          opacity: showCardsFinish ? 1 : 0,
          display: 'flex',
          justifyContent: 'end',
          alignItems: 'center',
          gap: 1,
          mt: 2,
        }}
      >
        <Chip
          label={labelDisplayedRows(
            records.length === 0 ? 0 : page * pageSize + 1,
            getLabelDisplayedRowsTo(),
            total
          )}
          variant="outlined"
          sx={{ mr: 1.5 }}
        />
        <IconButton
          size="small"
          color="default"
          disabled={page === 0}
          onClick={() => handleChangePage(page - 1)}
          sx={{
            bgcolor: 'background.paper',
            border: '1px solid',
            borderColor: 'divider',
          }}
        >
          <KeyboardArrowLeftRounded />
        </IconButton>
        <IconButton
          size="small"
          color="default"
          disabled={
            records.length !== -1 ? (1 + page) * pageSize >= total : false
          }
          onClick={() => handleChangePage(page + 1)}
          sx={{
            bgcolor: 'background.paper',
            border: '1px solid',
            borderColor: 'divider',
          }}
        >
          <KeyboardArrowRightRounded />
        </IconButton>
      </Box>

      <ConfirmModal
        open={!!recordDeleted}
        onClose={() => setRecordDeleted(undefined)}
        obj={recordDeleted}
        dialog={{
          color: 'error',
          title: t('Do you really want to delete this character?'),
        }}
        button={{
          color: 'error',
          text: t('button:Delete'),
          startIcon: <DeleteForeverRounded />,
        }}
        onConfirm={handleDelete}
      >
        <DialogContentText>{recordDeleted?.name}</DialogContentText>
      </ConfirmModal>

      <ConfirmModal
        open={editRecordName !== undefined}
        onClose={() => {
          setEditRecordNameError(false);
          setEditRecordName(undefined);
        }}
        dialog={{
          title: t('Please enter a new name'),
        }}
        button={{
          text: t('button:Create'),
          startIcon: <SaveAltRounded />,
        }}
        onConfirm={handleNameChange}
      >
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'flex-start',
            alignItems: 'flex-start',
            gap: 0.5,
          }}
        >
          <FormControl error={editRecordNameError}>
            <TextField
              name="RecordName"
              value={editRecordName}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
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
        </Box>
      </ConfirmModal>
    </>
  );
}
