import { useEffect, useState } from "react";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { InfoSearchbar } from "../../components";
import { PromptQueryDTO, PromptQueryWhere, PromptSummaryDTO } from "freechat-sdk";
import { Box, Card, CardContent, Grid, IconButton, Typography } from "@mui/joy";
import { KeyboardArrowLeftRounded, KeyboardArrowRightRounded } from "@mui/icons-material";

function RecordCard(props: { record: PromptSummaryDTO }) {
  const { record } = props;

  return (
    <Card>
      <CardContent>
        {JSON.stringify(record)}
      </CardContent>
    </Card>
  )
}

export default function Prompts() {
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
    <InfoSearchbar onSearch={handleSearch} />
    <Grid container spacing={2} alignItems="center" >
      {records.map(record => (
        <Grid xs={12} sm={6} md={4} key={record.promptId}>
          <RecordCard record={record} />
        </Grid>
      ))}
    </Grid>
    <Typography textAlign="center" sx={{ minWidth: 80 }}>
      {labelDisplayedRows(
        records.length === 0 ? 0 : page * pageSize + 1,
        getLabelDisplayedRowsTo(),
        total,
      )}
    </Typography>
    <Box sx={{ display: 'flex', gap: 1 }}>
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