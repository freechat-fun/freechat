import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { ConfirmModal, InfoCardCover, InfoSearchbar, LinePlaceholder } from "../../components";
import { PromptQueryDTO, PromptQueryWhere, PromptSummaryDTO } from "freechat-sdk";
import { Box, Button, Card, Chip, Grid, IconButton, Typography } from "@mui/joy";
import { AddCircleRounded, DeleteForeverRounded, KeyboardArrowLeftRounded, KeyboardArrowRightRounded } from "@mui/icons-material";
import { getDateLabel } from '../../libs/date_utils';

function RecordCard(props: {
  record: PromptSummaryDTO,
  onView: (record: PromptSummaryDTO) => void,
  onEdit: (record: PromptSummaryDTO) => void,
  onDelete: (record: PromptSummaryDTO) => void,
}) {
  const { record, onView, onEdit, onDelete } = props;
  const { t, i18n } = useTranslation();

  return (
    <Card sx={{
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
          whiteSpace: 'nowrap',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          maxWidth: '20rem',
        }}
      >
        {record.name}
      </Typography>
      <Box sx={{
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
          display: '-webkit-box',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          WebkitBoxOrient: 'vertical',
          WebkitLineClamp: 3,
          maxWidth: '20rem',
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
}

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

  useEffect(() => {
    doSearch(query);
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
            .then(resp => setTotal(resp))
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
    record?.promptId && promptApi?.deletePrompt(record.promptId)
      .then(resp => resp && setRecords(
        records.filter(r => r.promptId !== record.promptId)))
      .catch(handleError);

    setRecordDeleted(undefined);
  }

  function handleTryDelete(record: PromptSummaryDTO): void {
    setRecordDeleted(record);
  }

  function handleView(record: PromptSummaryDTO): void {
    navigate(`/w/console/prompt/${record.promptId}`)
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
      >
        {t('Create new')}
      </Button>
    </Box>
    <LinePlaceholder />
    <Grid container spacing={3} alignItems="center" >
      {records.map(record => (
        <Grid xs={12} sm={6} lg={4} key={record.promptId}>
          <RecordCard
            record={record}
            onView={handleView}
            onEdit={() => {}}
            onDelete={handleTryDelete}
          />
        </Grid>
      ))}
    </Grid>
    <Box sx={{
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
  </>
  );
}