import { createRef, forwardRef, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { ConfirmModal, InfoCardCover, InfoSearchbar, LinePlaceholder } from "../../components";
import { ChatMessageDTO, ChatPromptContentDTO, PromptCreateDTO, PromptQueryDTO, PromptQueryWhere, PromptSummaryDTO } from "freechat-sdk";
import { Box, Button, Card, Chip, FormControl, FormHelperText, IconButton, Input, Radio, RadioGroup, Typography } from "@mui/joy";
import { SxProps } from "@mui/joy/styles/types";
import { AddCircleRounded, DeleteForeverRounded, InfoOutlined, KeyboardArrowLeftRounded, KeyboardArrowRightRounded, SaveAltRounded } from "@mui/icons-material";
import { Transition } from 'react-transition-group';
import { getDateLabel } from '../../libs/date_utils';
import { defaultTransitionInterval, defaultTransitionSetting, initTransitionSequence, transitionStyles } from "../../libs/transition_utils";
import { i18nConfig } from "../../configs/i18n-config";

interface RecordCardProps {
  record: PromptSummaryDTO,
  onView: (record: PromptSummaryDTO) => void,
  onEdit: (record: PromptSummaryDTO) => void,
  onDelete: (record: PromptSummaryDTO) => void,
  sx?: SxProps,
}

const RecordCard = forwardRef<HTMLDivElement, RecordCardProps>((props, ref) => {
  const { record, onView, onEdit, onDelete, sx } = props;
  const { t, i18n } = useTranslation();

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
        <Chip color={record.type === 'string' ? 'warning' : 'success'} variant="outlined">{record.type}</Chip>
      </Box>
      <Typography
        sx={{
          ...sx,
          display: '-webkit-box',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          WebkitBoxOrient: 'vertical',
          WebkitLineClamp: 3,
          maxHeight: '4.5rem',
          minHeight: '4.5rem',
          lineHeight: '1.5rem',
        }}
      >
        {record.description}
      </Typography>
      <LinePlaceholder spacing={2} />
      <InfoCardCover
        onView={() => onView(record)}
        onEdit={() => onEdit(record)}
        onDelete={() => onDelete(record)}
      />
    </Card>
  )
});

export default function Prompts() {
  const navigate = useNavigate();
  const { t } = useTranslation(['prompt']);
  const { promptApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 6;

  const [records, setRecords] = useState<PromptSummaryDTO[]>([]);
  const [query, setQuery] = useState<PromptQueryDTO>(defaultQuery());
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [recordDeleted, setRecordDeleted] = useState<PromptSummaryDTO>();

  const [editRecordName, setEditRecordName] = useState<string>();
  const [editRecordNameError, setEditRecordNameError] = useState(false);
  const [editRecordType, setEditRecordType] = useState('chat');

  const [showCards, setShowCards] = useState(false);
  const [showCardsFinish, setShowCardsFinish] = useState(false);
  const cardRefs = useRef(Array(pageSize).fill(createRef()));

  useEffect(() => {
    doSearch(query);
    return initTransitionSequence(setShowCards, setShowCardsFinish, pageSize);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [promptApi, query]);

  function defaultQuery(): PromptQueryDTO {
    const newQuery = new PromptQueryDTO();
    newQuery.where = new PromptQueryWhere();
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['modifyTime'];

    return newQuery;
  }

  function doSearch(query: PromptQueryDTO): void {
    promptApi?.searchPromptSummary(query)
      .then(resp => {
        setRecords(resp);
        if (page === 0) {
          promptApi?.countPrompts(query)
            .then(setTotal)
            .catch(handleError);
        }
      })
      .catch(handleError);
  }

  function handleSearch(text: string | undefined, modelIds: string[]): void {
    const where = new PromptQueryWhere();
    where.text = text;
    where.aiModels = modelIds;

    const newQuery = new PromptQueryDTO();
    newQuery.where = where;
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['modifyTime'];

    setPage(0);
    setQuery(newQuery);
  }

  function handleChangePage(newPage: number): void {
    const newQuery = new PromptQueryDTO();
    newQuery.where = query?.where;
    newQuery.pageSize = query?.pageSize || pageSize;
    newQuery.orderBy = query?.orderBy;
    newQuery.pageNum = newPage;

    setPage(newPage);
    setQuery(newQuery);
  }

  function handleDelete(record: PromptSummaryDTO | undefined): void {
    record?.name && promptApi?.deletePromptByName(record.name)
      .then(() =>doSearch(query))
      .catch(handleError);

    setRecordDeleted(undefined);
  }

  function handleTryDelete(record: PromptSummaryDTO): void {
    setRecordDeleted(record);
  }

  function handleView(record: PromptSummaryDTO): void {
    navigate(`/w/console/prompt/${record.promptId}`)
  }

  function handleEdit(record: PromptSummaryDTO): void {
    navigate(`/w/console/prompt/edit/${record.promptId}`)
  }

  function handleNameChange(): void {
    if (editRecordNameError || !editRecordName) {
      return;
    }
      
    promptApi?.existsName(editRecordName)
      .then(resp => {
        if (!resp) {
          const request = new PromptCreateDTO();
          if (editRecordType === 'chat') {
            request.chatTemplate = new ChatPromptContentDTO();
            request.chatTemplate.system = '';
            request.chatTemplate.messageToSend = new ChatMessageDTO();
            request.chatTemplate.messageToSend.role = 'user';
            request.chatTemplate.messageToSend.content = '{{input}}';
            request.inputs = JSON.stringify({'input': ''});
          } else if (editRecordType === 'string') {
            request.template = '';
          }
          request.format = 'mustache';
          request.lang = i18nConfig.defaultLocale;
          request.name = editRecordName;
          request.visibility = 'public';

          promptApi?.createPrompt(request)
            .then(resp => {
              if (resp) {
                navigate(`/w/console/prompt/edit/${resp}`);
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

  return(
  <>
    <LinePlaceholder />
    <Box sx={{
      display: 'flex',
      flexDirection: { xs: 'column', lg: 'row' },
      justifyContent: 'space-between',
      alignItems: { xs: 'start', lg: 'center' },
    }}>
      <InfoSearchbar onSearch={handleSearch} />
      <Button
        startDecorator={<AddCircleRounded />}
        sx={{ borderRadius: '20px' }}
        onClick={() => setEditRecordName('untitled-1')}
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
          key={record.promptId || `transition-${index}`}
          nodeRef={cardRefs.current[index]}
        >
          {(state) => (
            <RecordCard
              key={record.promptId}
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
        title: t('Do you really want to delete this prompt?'),
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
          <Box sx={{
            display: 'flex',
            justifyContent: 'flex-start',
            alignItems: 'center',
            gap: 1,
          }}>
            <Chip variant="outlined" size="md">{t('Type')}</Chip>
            <RadioGroup
              orientation="horizontal"
              name="format"
              size="sm"
              value={editRecordType}
              onChange={(event) => setEditRecordType(event.target.value)}
              sx={{
                p: 0.5,
                '--RadioGroup-gap': '4px',
                '--Radio-actionRadius': '4px',
              }}
            >
              {['chat', 'string'].map((type) => (
                <Radio
                  key={`prompt-type-${type}`}
                  color="neutral"
                  size="sm"
                  value={type}
                  label={(<Typography noWrap level="body-sm">{type}</Typography>)}
                  variant="outlined"
                  sx={{
                    p: 0.5,
                    alignItems: 'center',
                  }}
                />
              ))}
            </RadioGroup>
          </Box>
        </Box>
      </ConfirmModal>
  </>
  );
}