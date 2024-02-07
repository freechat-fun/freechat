import { createRef, forwardRef, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { CommonBox, HotTags, InfoSearchbar, LinePlaceholder, SummaryTypography } from "../../components";
import { InteractiveStatsDTO, PromptQueryDTO, PromptQueryWhere, PromptSummaryDTO, PromptSummaryStatsDTO, UserBasicInfoDTO } from "freechat-sdk";
import { Avatar, Box, Card, Chip, Divider, IconButton, Link, Stack, Tooltip, Typography } from "@mui/joy";
import { SxProps } from "@mui/joy/styles/types";
import { KeyboardArrowLeftRounded, KeyboardArrowRightRounded, ShareRounded, VisibilityRounded } from "@mui/icons-material";
import { Transition } from 'react-transition-group';
import { getDateLabel } from '../../libs/date_utils';
import { defaultTransitionInterval, defaultTransitionSetting, initTransitionSequence, transitionStyles } from "../../libs/transition_utils";

interface RecordCardProps {
  record: PromptSummaryStatsDTO,
  sx?: SxProps,
  onClick?: (record: PromptSummaryStatsDTO) => void;
}

const RecordCard = forwardRef<HTMLDivElement, RecordCardProps>((props, ref) => {
  const { record, sx , onClick} = props;
  const { i18n } = useTranslation();
  const { accountApi } = useFreeChatApiContext();

  const [user, setUser] = useState<UserBasicInfoDTO>();
  const [userName, setUserName] = useState(record?.username ?? '');
  const [tags, setTags] = useState(record?.tags ?? []);

  useEffect(() => {
    record.username && accountApi?.getUserBasic(record.username)
      .then(resp => {
        setUser(resp);
        setUserName(resp?.nickname ?? resp?.username ?? '');
      });
  }, [accountApi, record]);

  useEffect(() => {
    setTags(record?.tags ?? []);
  }, [record]);

  return (
    <Card ref={ref} sx={{
      ...sx,
      transition: 'transform 0.4s, box-shadow 0.4s',
      boxShadow: 'sm',
      '&:hover': {
        boxShadow: 'lg',
        transform: 'translateY(-2px)',
      },
    }}>
      <Box sx={{
        ...sx,
        display: 'flex',
        justifyContent: 'flex-start',
        alignItems: 'center',
        gap: 2,
      }}>
        <Link
          overlay
          onClick={(event) => {
            event.preventDefault();
            onClick?.(record);
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
        </Link>
        <Chip color="success" variant="soft">v{record.version}</Chip>
        <Chip color={record.type === 'string' ? 'warning' : 'success'} variant="outlined">{record.type}</Chip>
        <CommonBox sx={{ ml: 'auto' }}>
        <Tooltip sx= {{ maxWidth: '20rem' }} size="sm" title={userName}>
          <Avatar alt={userName} src={user?.picture} size="md" />
        </Tooltip>
          
        </CommonBox>
      </Box>
      <Divider />

      <SummaryTypography sx={{...sx}}>
        {record.description}
      </SummaryTypography>
      <Divider />

      {tags.length > 0 && (
        <CommonBox>
          {tags.map((tag, index) => (
            <Chip variant="outlined" color="success" key={`tag-${record.promptId}-${tag}-${index}`}>{tag}</Chip>
          ))}
        </CommonBox>
      )}

      {tags.length > 0 && <Divider />}

      <Box sx={{
        ...sx,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
      }}>
        <Typography level="body-sm" textColor="gray">
          {getDateLabel(record.gmtModified || new Date(0), i18n.language)}
        </Typography>
        <CommonBox>
          <Chip size="sm" variant="plain" startDecorator={<VisibilityRounded />} >
            {record.viewCount}
          </Chip>
          <Chip size="sm" variant="plain" startDecorator={<ShareRounded />} >
            {record.referCount}
          </Chip>
        </CommonBox>
      </Box>
    </Card>
  )
});

export default function PromptGallery() {
  const navigate = useNavigate();
  const { promptApi, interactiveStatisticsApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 6;

  const [records, setRecords] = useState<PromptSummaryStatsDTO[]>([]);
  const [query, setQuery] = useState<PromptQueryDTO>(defaultQuery());
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);

  const [showCards, setShowCards] = useState(false);
  const [showCardsFinish, setShowCardsFinish] = useState(false);
  const cardRefs = useRef(Array(pageSize).fill(createRef()));

  function defaultQuery(): PromptQueryDTO {
    const newQuery = new PromptQueryDTO();
    newQuery.where = new PromptQueryWhere();
    newQuery.where.visibility = 'public';
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['viewCount', 'referCount', 'modifyTime'];

    return newQuery;
  }

  useEffect(() => {
    return initTransitionSequence(setShowCards, setShowCardsFinish, pageSize);
  }, []);

  useEffect(() => {
    doSearch(query);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [promptApi, query]);

  function promptInfoWithStats(record: PromptSummaryDTO, stats: InteractiveStatsDTO | undefined | null): PromptSummaryStatsDTO {
    const s = {...{
      viewCount: 0,
      referCount: 0,
      recommendCount: 0,
      scoreCount: 0,
      score: 0,
    }, ...stats};
    const recordWithStats: PromptSummaryStatsDTO = { ...s, ...record };
    return recordWithStats;
  }

  function doSearch(query: PromptQueryDTO | undefined): void {
    query && promptApi?.searchPromptSummary(query)
      .then(resp => {
        setRecords(resp);
        resp.forEach(r => {
          r.promptId && interactiveStatisticsApi?.getStatistics('prompt', r.promptId)
            .then(stats => promptInfoWithStats(r, stats))
            .then(recordWithStats => {
              setRecords(prevRecords => {
                const newRecords = prevRecords.filter(r1 => r1.promptId !== recordWithStats.promptId);
                newRecords.push(recordWithStats);
                return newRecords;
              });
            })
            .catch(handleError);
        })
        if (page === 0) {
          promptApi?.countPrompts(query)
            .then(setTotal)
            .catch(handleError);
        }
      })
      .catch(handleError);
  }

  function handleSearch(text: string | undefined, modelIds: string[] | undefined): void {
    const where = new PromptQueryWhere();
    where.text = text;
    where.aiModels = modelIds;
    where.visibility = 'public';

    const newQuery = new PromptQueryDTO();
    newQuery.where = where;
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['viewCount', 'referCount', 'modifyTime'];

    setPage(0);
    setQuery(newQuery);
  }

  function handleTagClick(tag: string): void {
    const where = new PromptQueryWhere();
    where.tags = [tag];
    where.visibility = 'public';

    const newQuery = new PromptQueryDTO();
    newQuery.where = where;
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['viewCount', 'referCount', 'modifyTime'];

    setPage(0);
    setQuery(newQuery);
  }

  function handleChangePage(newPage: number): void {
    setPage(newPage);
    if (query) {
      const newQuery = new PromptQueryDTO();
      newQuery.where = query.where;
      newQuery.pageSize = query.pageSize || pageSize;
      newQuery.orderBy = query.orderBy;
      newQuery.pageNum = newPage;
      setQuery(newQuery);
    }
  }

  function handleView(record: PromptSummaryStatsDTO): void {
    if (!record.promptId) {
      return;
    }
    interactiveStatisticsApi?.increaseStatistic('prompt', record.promptId, 'view_count')
      .finally(() => navigate(`/w/prompt/${record.promptId}`));
  }
  
  function getLabelDisplayedRowsTo(): number {
    const currentCount = (page + 1) * pageSize;
    return total === 0 && records.length > 0 ? currentCount : Math.min(total, currentCount);
  }

  function labelDisplayedRows(from: number, to: number, count: number): string {
    return `${from}-${to} of ${to <= count ? count : `more than ${to}`}`;
  }

  function comparator(a: PromptSummaryStatsDTO, b: PromptSummaryStatsDTO): number {
    if ((a.viewCount ?? 0) > (b.viewCount ?? 0)) return -1;
    if ((a.viewCount ?? 0) < (b.viewCount ?? 0)) return 1;

    if ((a.referCount ?? 0) > (b.referCount ?? 0)) return -1;
    if ((a.referCount ?? 0) < (b.referCount ?? 0)) return 1;

    const defaultDate = new Date();
    if ((a.gmtModified ?? defaultDate) > (b.gmtModified ?? defaultDate)) return -1;
    if ((a.gmtModified ?? defaultDate) < (b.gmtModified ?? defaultDate)) return 1;

    return 0;
  }

  return(
  <>
    <LinePlaceholder />
    <Box sx={{
      display: 'flex',
      alignItems: 'flex-start',
      gap: 3,
    }}>
      <Stack sx={{ flex: 1 }}>
        <InfoSearchbar onSearch={handleSearch} />
        <LinePlaceholder />
        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, 1fr)',
            gap: 3,
          }}
        >
          {records.sort(comparator).map((record, index) => (
            <Transition
              in={showCards}
              timeout={index * defaultTransitionInterval}
              unmountOnExit
              key={`transition-${record.promptId || index}`}
              nodeRef={cardRefs.current[index]}
            >
              {(state) => (
                <RecordCard
                  key={`record-card-${record.promptId || index}`}
                  ref={cardRefs.current[index]}
                  record={record}
                  onClick={handleView}
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
      </Stack>
      <Divider orientation="vertical" sx={{
        display: {xs: 'none', sm: 'block'},
      }} />
      <HotTags
        infoType="prompt"
        count={30}
        onTagClick={handleTagClick}
        sx={{
          display: { xs: 'none', sm: 'flex' },
          width: '16rem',
      }} />
    </Box>
  </>
  );
}