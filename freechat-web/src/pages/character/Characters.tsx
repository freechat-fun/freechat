import { createRef, forwardRef, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { CommonBox, ConfirmModal, InfoCardCover, InfoSearchbar, LinePlaceholder, SummaryTypography } from "../../components";
import { CharacterCreateDTO, CharacterQueryDTO, CharacterQueryWhere, CharacterSummaryDTO, ChatCreateDTO } from "freechat-sdk";
import { Avatar, Box, Button, Card, Chip, FormControl, FormHelperText, IconButton, Input, Typography } from "@mui/joy";
import { SxProps } from "@mui/joy/styles/types";
import { AddCircleRounded, DeleteForeverRounded, InfoOutlined, KeyboardArrowLeftRounded, KeyboardArrowRightRounded, SaveAltRounded } from "@mui/icons-material";
import { Transition } from 'react-transition-group';
import { getDateLabel } from '../../libs/date_utils';
import { defaultTransitionInterval, defaultTransitionSetting, initTransitionSequence, transitionStyles } from "../../libs/transition_utils";
import { i18nConfig } from "../../configs/i18n-config";
import { ChatIcon } from "../../components/icon";

type RecordCardProps = {
  record: CharacterSummaryDTO,
  onView: (record: CharacterSummaryDTO) => void,
  onEdit: (record: CharacterSummaryDTO) => void,
  onDelete: (record: CharacterSummaryDTO) => void,
  sx?: SxProps,
}

const RecordCard = forwardRef<HTMLDivElement, RecordCardProps>((props, ref) => {
  const { record, onView, onEdit, onDelete, sx } = props;
  const { t, i18n } = useTranslation();

  const characterName = record.nickname ?? record.name;

  return (
    <Card ref={ref} sx={{
      ...sx,
      transition: 'transform 0.4s, box-shadow 0.4s',
      boxShadow: 'sm',
      '&:hover': {
        boxShadow: 'lg',
        transform: 'translateY(-1px)',
      },
    }}>
      <CommonBox>
        <Avatar alt={characterName} src={record.avatar}>{characterName}</Avatar>
        <Typography
          level="title-lg"
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
      </CommonBox>
      <Box sx={{
        ...sx,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
      }}>
        <Typography level="body-sm" textColor="gray">
          {getDateLabel(record.gmtModified || new Date(0), i18n.language)}
        </Typography>
        <Typography level="body-sm" textColor={record.visibility === 'public' ? 'success.500' : 'warning.500'}>
          {record.visibility === 'public' ? t('public') : t('private')}
        </Typography>
      </Box>
      <Box sx={{
        ...sx,
        display: 'flex',
        justifyContent: 'start',
        alignItems: 'center',
        gap: 2,
      }}>
        <Chip color="success" variant="soft">v{record.version}</Chip>
      </Box>
      <SummaryTypography sx={{...sx}}>
        {record.description}
      </SummaryTypography>
      <LinePlaceholder spacing={2} />
      <InfoCardCover
        icons={{view: ChatIcon}}
        onView={() => onView(record)}
        onEdit={() => onEdit(record)}
        onDelete={() => onDelete(record)}
      />
    </Card>
  )
});

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

  const cardRefs = useRef(Array(pageSize).fill(createRef()));

  const doSearch = useCallback(() => {
    characterApi?.searchCharacterSummary(query)
      .then(resp => {
        setRecords(resp);
        if (page === 0) {
          characterApi?.countCharacters(query)
            .then(setTotal)
            .catch(handleError);
        }
      })
      .catch(handleError);
  }, [characterApi, handleError, page, query]);

  useEffect(() => {
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
    record?.name && characterApi?.deleteCharacterByName(record.name)
      .then(() =>doSearch())
      .catch(handleError);

    setRecordDeleted(undefined);
  }

  function handleTryDelete(record: CharacterSummaryDTO): void {
    setRecordDeleted(record);
  }

  function handleView(record: CharacterSummaryDTO): void {
    record.characterId && chatApi?.getDefaultChatId(record.characterId)
      .then(resp => {
        navigate(`/w/chat/${resp}/debug`);
      })
      .catch(() => {
        accountApi?.getUserDetails()
          .then(userDetails => {
            characterApi?.getDefaultCharacterBackend(record.characterId as string)
              .then(backend => {
                if (backend.backendId) {
                  const request = new ChatCreateDTO();
                  request.userNickname = userDetails.nickname ?? userDetails.username;
                  request.userProfile = userDetails.profile;
                  request.characterNickname = record.nickname ?? record.name;
                  request.backendId = backend.backendId;

                  chatApi.startChat(request)
                    .then(chatId => {
                      navigate(`/w/chat/${chatId}/debug`);
                    })
                    .catch(handleError);
                }
              })
              .catch(handleError);
          })
          .catch(handleError);
      });
  }

  function handleEdit(record: CharacterSummaryDTO): void {
    navigate(`/w/character/edit/${record.characterId}`);
  }

  function handleNameChange(): void {
    if (editRecordNameError || !editRecordName) {
      return;
    }

    characterApi?.existsCharacterName(editRecordName)
      .then(resp => {
        if (!resp) {
          const request = new CharacterCreateDTO();
          request.lang = i18nConfig.defaultLocale;
          request.name = editRecordName;
          request.visibility = 'public';

          characterApi?.createCharacter(request)
            .then(resp => {
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

  function getLabelDisplayedRowsTo(): number {
    const currentCount = (page + 1) * pageSize;
    return total === 0 && records.length > 0 ? currentCount : Math.min(total, currentCount);
  }

  function labelDisplayedRows(from: number, to: number, count: number): string {
    return `${from}-${to} of ${to <= count ? count : `more than ${to}`}`;
  }

  return (
    <>
      <LinePlaceholder />
      <Box sx={{
        display: 'flex',
        flexDirection: { xs: 'column', lg: 'row' },
        justifyContent: 'space-between',
        alignItems: { xs: 'start', lg: 'center' },
      }}>
        <InfoSearchbar enableModelSelect={false} onSearch={handleSearch} />
        <Button
          startDecorator={<AddCircleRounded />}
          sx={{ borderRadius: '20px' }}
          onClick={() => setEditRecordName('untitled')}
        >
          {t('Create new')}
        </Button>
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
            key={record.characterId || `transition-${index}`}
            nodeRef={cardRefs.current[index]}
          >
            {(state) => (
              <RecordCard
                key={record.characterId}
                ref={cardRefs.current[index]}
                record={record}
                onView={handleView}
                onEdit={handleEdit}
                onDelete={handleTryDelete}
                sx={{
                  transition: defaultTransitionSetting,
                  ...transitionStyles[state],
                }}
              />
            )}
          </Transition>
          
        ))}
      </Box>
      <Box sx={{
        opacity: showCardsFinish ? 1 : 0,
        display: 'flex',
        justifyContent: 'end',
        alignItems: 'center',
        gap: 1,
        mt: 2,
      }}>
        <Chip variant="outlined" sx={{ mr: 1.5 }}>
          {labelDisplayedRows(
            records.length === 0 ? 0 : page * pageSize + 1,
            getLabelDisplayedRowsTo(),
            total,
          )}
        </Chip>
        <IconButton
          size="sm"
          color="neutral"
          variant="outlined"
          disabled={page === 0}
          onClick={() => handleChangePage(page - 1)}
          sx={{ bgcolor: 'background.surface' }}
        >
          <KeyboardArrowLeftRounded />
        </IconButton>
        <IconButton
          size="sm"
          color="neutral"
          variant="outlined"
          disabled={
            records.length !== -1
              ? (1 + page) * pageSize >= total
              : false
          }
          onClick={() => handleChangePage(page + 1)}
          sx={{ bgcolor: 'background.surface' }}
        >
          <KeyboardArrowRightRounded />
        </IconButton>
      </Box>

      <ConfirmModal
        open={!!recordDeleted}
        onClose={() => setRecordDeleted(undefined)}
        obj={recordDeleted}
        dialog={{
          color: 'danger',
          title: t('Do you really want to delete this character?'),
        }}
        button={{
          color: 'danger',
          text: t('button:Delete'),
          startDecorator: <DeleteForeverRounded />
        }}
        onConfirm={handleDelete}
      >
        <Typography>{recordDeleted?.name}</Typography>
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
          startDecorator: <SaveAltRounded />
        }}
        onConfirm={handleNameChange}
      >
        <Box sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'flex-start',
          alignItems: 'flex-start',
          gap: 0.5,
        }}>
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
        </Box>
      </ConfirmModal>
    </>
  )
}