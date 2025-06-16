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
  ConfirmModal,
  InfoCardCover,
  InfoSearchbar,
  LinePlaceholder,
  StyledStack,
  SummaryTypography,
} from '../../components';
import {
  ChatMessageDTO,
  ChatPromptContentDTO,
  PromptCreateDTO,
  PromptQueryDTO,
  PromptQueryWhere,
  PromptSummaryDTO,
} from 'freechat-sdk';
import {
  Box,
  Button,
  Chip,
  FormControl,
  FormHelperText,
  IconButton,
  TextField,
  Typography,
  Radio,
  RadioGroup,
  FormControlLabel,
  DialogContentText,
} from '@mui/material';
import { SxProps, Theme } from '@mui/material/styles';
import {
  AddCircleRounded,
  DeleteForeverRounded,
  InfoOutlined,
  KeyboardArrowLeftRounded,
  KeyboardArrowRightRounded,
  SaveAltRounded,
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
import { setMessageText } from '../../libs/template_utils';

type RecordCardProps = {
  record: PromptSummaryDTO;
  onView: () => void;
  onEdit: () => void;
  onDelete: () => void;
  sx?: SxProps<Theme>;
};

const RecordCard = forwardRef<HTMLDivElement, RecordCardProps>((props, ref) => {
  const { record, onView, onEdit, onDelete, sx } = props;
  const { t, i18n } = useTranslation();

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
          sx={{ m: 0 }}
        />
        <Chip
          label={record.type}
          color={record.type === 'string' ? 'warning' : 'success'}
          variant="outlined"
          size="small"
        />
      </Box>
      <SummaryTypography>{record.description}</SummaryTypography>
      <LinePlaceholder spacing={2} />
      <InfoCardCover
        onView={() => onView()}
        onEdit={() => onEdit()}
        onDelete={() => onDelete()}
      />
    </StyledStack>
  );
});

RecordCard.displayName = 'Prompts.RecordCard';

export default function Prompts() {
  const navigate = useNavigate();
  const { t } = useTranslation(['prompt']);
  const { promptApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 6;

  const [records, setRecords] = useState<PromptSummaryDTO[]>([]);
  const [query, setQuery] = useState<PromptQueryDTO>(defaultQuery(pageSize));
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [recordDeleted, setRecordDeleted] = useState<PromptSummaryDTO>();

  const [editRecordName, setEditRecordName] = useState<string>();
  const [editRecordNameError, setEditRecordNameError] = useState(false);
  const [editRecordType, setEditRecordType] = useState('chat');

  const [showCards, setShowCards] = useState(false);
  const [showCardsFinish, setShowCardsFinish] = useState(false);

  const cardRefs = useRef(Array(pageSize).fill(createRef()));

  const doSearch = useCallback(() => {
    promptApi
      ?.searchPromptSummary(query)
      .then((resp) => {
        setRecords(resp);
        if (page === 0) {
          promptApi?.countPrompts(query).then(setTotal).catch(handleError);
        }
      })
      .catch(handleError);
  }, [handleError, page, promptApi, query]);

  useEffect(() => {
    doSearch();
    return initTransitionSequence(setShowCards, setShowCardsFinish, pageSize);
  }, [doSearch]);

  function defaultQuery(limit: number): PromptQueryDTO {
    const newQuery = new PromptQueryDTO();
    newQuery.where = new PromptQueryWhere();
    newQuery.pageSize = limit;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['modifyTime'];

    return newQuery;
  }

  function handleSearch(
    text: string | undefined,
    modelIds: string[] | undefined
  ): void {
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
    if (record?.name) {
      promptApi
        ?.deletePromptByName(record.name)
        .then(() => doSearch())
        .catch(handleError);
    }

    setRecordDeleted(undefined);
  }

  function handleTryDelete(record: PromptSummaryDTO): void {
    setRecordDeleted(record);
  }

  function handleView(record: PromptSummaryDTO): void {
    navigate(`/w/prompt/${record.promptId}`);
  }

  function handleEdit(record: PromptSummaryDTO): void {
    navigate(`/w/prompt/edit/${record.promptId}`);
  }

  function handleNameChange(): void {
    if (editRecordNameError || !editRecordName) {
      return;
    }

    promptApi
      ?.existsPromptName(editRecordName)
      .then((resp) => {
        if (!resp) {
          const request = new PromptCreateDTO();
          if (editRecordType === 'chat') {
            request.chatTemplate = new ChatPromptContentDTO();
            request.chatTemplate.system = '';
            request.chatTemplate.messageToSend = new ChatMessageDTO();
            request.chatTemplate.messageToSend.role = 'user';
            setMessageText(request.chatTemplate.messageToSend, '{{{input}}}');
            request.inputs = JSON.stringify({ input: '' });
          } else if (editRecordType === 'string') {
            request.template = '';
          }
          request.format = 'mustache';
          request.lang = i18nConfig.defaultLocale;
          request.name = editRecordName;
          request.visibility = 'public';

          promptApi
            ?.createPrompt(request)
            .then((resp) => {
              if (resp) {
                navigate(`/w/prompt/edit/${resp}`);
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
        <Button
          startIcon={<AddCircleRounded />}
          variant="contained"
          onClick={() => setEditRecordName('untitled')}
          sx={{ borderRadius: '20px' }}
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
                onView={() => handleView(record)}
                onEdit={() => handleEdit(record)}
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
          variant="outlined"
          sx={{ mr: 1.5 }}
          label={labelDisplayedRows(
            records.length === 0 ? 0 : page * pageSize + 1,
            getLabelDisplayedRowsTo(),
            total
          )}
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
          title: t('Do you really want to delete this prompt?'),
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
                if (event.target) {
                  setEditRecordName(event.target.value);
                  setEditRecordNameError(false);
                }
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
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'flex-start',
              alignItems: 'center',
              gap: 1,
            }}
          >
            <Chip label={t('Type')} variant="outlined" size="small" />
            <RadioGroup
              row
              name="format"
              value={editRecordType}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                if (event.target) {
                  setEditRecordType(event.target.value);
                }
              }}
            >
              {['chat', 'string'].map((type) => (
                <FormControlLabel
                  key={`prompt-type-${type}`}
                  value={type}
                  control={<Radio size="small" />}
                  label={
                    <Typography variant="body2" noWrap>
                      {type}
                    </Typography>
                  }
                />
              ))}
            </RadioGroup>
          </Box>
        </Box>
      </ConfirmModal>
    </>
  );
}
