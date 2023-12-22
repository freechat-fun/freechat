import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { InfoSearchbar, LinePlaceholder } from "../../components";
import { PromptQueryDTO, PromptQueryWhere, PromptSummaryDTO } from "freechat-sdk";
import { Box, Button, Card, CardCover, Chip, Grid, IconButton, Typography } from "@mui/joy";
import { AddCircleRounded, DeleteRounded, EditRounded, KeyboardArrowLeftRounded, KeyboardArrowRightRounded, VisibilityRounded } from "@mui/icons-material";
import { formatDate } from '../../libs/date_utils';

function RecordCard(props: { record: PromptSummaryDTO }) {
  const { record } = props;
  const { t } = useTranslation();

  return (
    <Card sx={{
      transition: 'transform 0.4s, box-shadow 0.4s',
      boxShadow: 'sm',
      '&:hover': {
        boxShadow: 'lg',
        transform: 'translateY(-2px)',
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
          {formatDate(record.gmtModified || new Date(0))}
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
      <CardCover
        className="gradient-cover"
        sx={{
          '&:hover, &:focus-within': {
            opacity: 1,
          },
          opacity: 0,
          transition: '0.3s ease-in',
          background:
            'linear-gradient(180deg, transparent 67%, rgba(0,0,0,0.00345888) 68.94%, rgba(0,0,0,0.014204) 70.89%, rgba(0,0,0,0.0326639) 72.83%, rgba(0,0,0,0.0589645) 74.78%, rgba(0,0,0,0.0927099) 76.72%, rgba(0,0,0,0.132754) 78.67%, rgba(0,0,0,0.177076) 80.61%, rgba(0,0,0,0.222924) 82.56%, rgba(0,0,0,0.267246) 84.5%, rgba(0,0,0,0.30729) 86.44%, rgba(0,0,0,0.341035) 88.39%, rgba(0,0,0,0.367336) 90.33%, rgba(0,0,0,0.385796) 92.28%, rgba(0,0,0,0.396541) 94.22%, rgba(0,0,0,0.4) 96.17%)',
        }}
      >
        <div>
          <Box sx={{
            display: 'flex',
            justifyContent: 'flex-end',
            alignItems: 'flex-end',
            alignSelf: 'flex-end',
            gap: 1,
            width: '100%',
          }}>
            <IconButton
              variant="solid"
              color="neutral"
              sx={{ bgcolor: 'rgba(0 0 0 / 0)' }}
            >
              <VisibilityRounded />
            </IconButton>
            <IconButton
              variant="solid"
              color="neutral"
              sx={{ bgcolor: 'rgba(0 0 0 / 0)' }}
            >
              <EditRounded />
            </IconButton>
            <IconButton
              variant="solid"
              color="neutral"
              sx={{ bgcolor: 'rgba(0 0 0 / 0)' }}
            >
              <DeleteRounded />
            </IconButton>
          </Box>
      </div>
      </CardCover>
    </Card>
  )
}

export default function Prompts() {
  const { t } = useTranslation();
  const { promptApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 6;

  const [records, setRecords] = useState<PromptSummaryDTO[]>([]);
  const [query, setQuery] = useState<PromptQueryDTO>(defaultQuery());
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    doSearch(query);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [query]);

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
          <RecordCard record={record} />
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
  </>
  );
}