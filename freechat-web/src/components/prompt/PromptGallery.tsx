import { forwardRef, useCallback, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  useErrorMessageBusContext,
  useFrameScrollContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import {
  CommonBox,
  HighlightedTypography,
  HotTags,
  InfoSearchbar,
  LinePlaceholder,
  SummaryTypography,
} from '../../components';
import {
  InteractiveStatsDTO,
  PromptQueryDTO,
  PromptQueryWhere,
  PromptSummaryDTO,
  PromptSummaryStatsDTO,
  UserBasicInfoDTO,
} from 'freechat-sdk';
import {
  Avatar,
  Box,
  Card,
  Chip,
  CircularProgress,
  Divider,
  Link,
  Stack,
  Tooltip,
  Typography,
} from '@mui/joy';
import { SxProps } from '@mui/joy/styles/types';
import { ShareRounded, VisibilityRounded } from '@mui/icons-material';
import { getDateLabel } from '../../libs/date_utils';

type RecordCardProps = {
  record: PromptSummaryStatsDTO;
  keyWord?: string;
  sx?: SxProps;
  onClick?: () => void;
};

const RecordCard = forwardRef<HTMLDivElement, RecordCardProps>((props, ref) => {
  const { record, keyWord, sx, onClick } = props;
  const { i18n } = useTranslation();
  const { accountApi } = useFreeChatApiContext();

  const [user, setUser] = useState<UserBasicInfoDTO>();
  const [userName, setUserName] = useState(record?.username ?? '');
  const [tags, setTags] = useState(record?.tags ?? []);

  useEffect(() => {
    if (record.username) {
      accountApi?.getUserBasic(record.username).then((resp) => {
        setUser(resp);
        setUserName(resp?.nickname ?? resp?.username ?? '');
      });
    }
  }, [accountApi, record]);

  useEffect(() => {
    setTags(record?.tags ?? []);
  }, [record]);

  return (
    <Card
      ref={ref}
      sx={{
        ...sx,
        transition: 'transform 0.4s, box-shadow 0.4s',
        boxShadow: 'sm',
        '&:hover': {
          boxShadow: 'lg',
          transform: 'translateY(-2px)',
        },
      }}
    >
      <Box
        sx={{
          ...sx,
          display: 'flex',
          justifyContent: 'flex-start',
          alignItems: 'center',
          gap: 2,
        }}
      >
        <Link
          overlay
          onClick={(event) => {
            event.preventDefault();
            onClick?.();
          }}
        >
          <HighlightedTypography
            highlight={keyWord}
            level="title-lg"
            sx={{
              whiteSpace: 'nowrap',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              maxWidth: '20rem',
            }}
          >
            {record.name}
          </HighlightedTypography>
        </Link>
        <Chip color="success" variant="soft">
          v{record.version}
        </Chip>
        <Chip
          color={record.type === 'string' ? 'warning' : 'success'}
          variant="outlined"
        >
          {record.type}
        </Chip>
        <CommonBox sx={{ ml: 'auto' }}>
          <Tooltip sx={{ maxWidth: '20rem' }} size="sm" title={userName}>
            <Avatar alt={userName} src={user?.picture} size="md" />
          </Tooltip>
        </CommonBox>
      </Box>
      <Divider />

      <SummaryTypography highlight={keyWord} sx={{ ...sx }}>
        {record.description}
      </SummaryTypography>
      <Divider />

      {tags.length > 0 && (
        <CommonBox>
          {tags.map((tag, index) => (
            <Chip
              variant="outlined"
              color="success"
              key={`tag-${record.promptId}-${tag}-${index}`}
            >
              {tag}
            </Chip>
          ))}
        </CommonBox>
      )}

      {tags.length > 0 && <Divider />}

      <Box
        sx={{
          ...sx,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <Typography level="body-sm" textColor="gray">
          {getDateLabel(record.gmtModified || new Date(0), i18n.language)}
        </Typography>
        <CommonBox>
          <Chip
            size="sm"
            variant="plain"
            startDecorator={<VisibilityRounded />}
          >
            {record.viewCount}
          </Chip>
          <Chip size="sm" variant="plain" startDecorator={<ShareRounded />}>
            {record.referCount}
          </Chip>
        </CommonBox>
      </Box>
    </Card>
  );
});

export default function PromptGallery() {
  const navigate = useNavigate();
  const { setFrameScrollHandler } = useFrameScrollContext();
  const { isAuthorized } = useMetaInfoContext();
  const { promptApi, interactiveStatisticsApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 6;

  const [records, setRecords] = useState<PromptSummaryStatsDTO[]>([]);
  const [query, setQuery] = useState<PromptQueryDTO>();
  const [, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [finish, setFinish] = useState(false);
  const [scrollRemainingHeight, setScrollRemainingHeight] = useState(200);

  const keyWord = useRef<string>(undefined);

  const defaultQuery = useCallback(() => {
    const newQuery = new PromptQueryDTO();
    newQuery.where = new PromptQueryWhere();
    newQuery.where.visibility = 'public';
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['viewCount', 'referCount', 'modifyTime'];

    return newQuery;
  }, []);

  useEffect(() => {
    const handleScroll = (frameRef: HTMLDivElement) => {
      const { scrollTop, scrollHeight, clientHeight } = frameRef;
      setScrollRemainingHeight(scrollHeight - scrollTop - clientHeight);
    };

    setFrameScrollHandler?.(() => handleScroll);

    return () => {
      setFrameScrollHandler?.(undefined);
    };
  }, [setFrameScrollHandler]);

  useEffect(() => {
    const threshold = 200;
    if (
      !loading &&
      !finish &&
      scrollRemainingHeight < threshold &&
      scrollRemainingHeight >= 0
    ) {
      if (finish) {
        return;
      }
      setPage((prevPage) => {
        const newPage = prevPage + 1;
        setQuery((prevQuery) => {
          if (!prevQuery) {
            prevQuery = defaultQuery();
          }
          const newQuery = new PromptQueryDTO();
          newQuery.where = prevQuery.where;
          if (!newQuery.where) {
            newQuery.where = new PromptQueryWhere();
            newQuery.where.visibility = 'public';
          }
          newQuery.pageSize = prevQuery.pageSize || pageSize;
          newQuery.orderBy = prevQuery.orderBy;
          newQuery.pageNum = newPage;
          return newQuery;
        });
        return newPage;
      });
    }
  }, [loading, finish, scrollRemainingHeight, defaultQuery]);

  useEffect(() => {
    setLoading(true);
    const currentQuery = query || defaultQuery();
    promptApi
      ?.searchPublicPromptSummary(currentQuery)
      .then((resp) => {
        if (resp.length == 0) {
          setFinish(true);
          return;
        }
        setRecords((prevRecords) => {
          const delta = resp.filter(
            (r0) => !prevRecords.map((pr) => pr.promptId).includes(r0.promptId)
          );
          return prevRecords.concat(delta);
        });
        resp.forEach((r) => {
          if (r.promptUid) {
            interactiveStatisticsApi
              ?.getStatistics('prompt', r.promptUid)
              .then((stats) => promptInfoWithStats(r, stats))
              .then((recordWithStats) => {
                setRecords((prevRecords) => {
                  const newRecords = prevRecords.filter(
                    (r1) => r1.promptId !== recordWithStats.promptId
                  );
                  newRecords.push(recordWithStats);
                  return newRecords;
                });
              })
              .catch(handleError);
          }
        });
      })
      .catch(handleError)
      .finally(() => setLoading(false));
  }, [defaultQuery, handleError, interactiveStatisticsApi, promptApi, query]);

  function promptInfoWithStats(
    record: PromptSummaryDTO,
    stats: InteractiveStatsDTO | undefined | null
  ): PromptSummaryStatsDTO {
    const s = {
      ...{
        viewCount: 0,
        referCount: 0,
        recommendCount: 0,
        scoreCount: 0,
        score: 0,
      },
      ...stats,
    };
    const recordWithStats: PromptSummaryStatsDTO = { ...s, ...record };
    return recordWithStats;
  }

  function handleSearch(
    text: string | undefined,
    modelIds: string[] | undefined
  ): void {
    const where = new PromptQueryWhere();
    where.text = text;
    where.aiModels = modelIds;
    where.visibility = 'public';

    const newQuery = new PromptQueryDTO();
    newQuery.where = where;
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['viewCount', 'referCount', 'modifyTime'];

    keyWord.current = text;
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

  function handleView(record: PromptSummaryStatsDTO): void {
    if (!isAuthorized()) {
      navigate('/w/login');
      return;
    } else if (!record.promptUid) {
      return;
    }

    interactiveStatisticsApi
      ?.increaseStatistic('prompt', record.promptUid, 'view_count')
      .finally(() => navigate(`/w/prompt/${record.promptId}`));
  }

  function comparator(
    a: PromptSummaryStatsDTO,
    b: PromptSummaryStatsDTO
  ): number {
    if ((a.viewCount ?? 0) > (b.viewCount ?? 0)) return -1;
    if ((a.viewCount ?? 0) < (b.viewCount ?? 0)) return 1;

    if ((a.referCount ?? 0) > (b.referCount ?? 0)) return -1;
    if ((a.referCount ?? 0) < (b.referCount ?? 0)) return 1;

    const defaultDate = new Date();
    if ((a.gmtModified ?? defaultDate) > (b.gmtModified ?? defaultDate))
      return -1;
    if ((a.gmtModified ?? defaultDate) < (b.gmtModified ?? defaultDate))
      return 1;

    return 0;
  }

  return (
    <>
      <LinePlaceholder />
      <Box
        sx={{
          display: 'flex',
          alignItems: 'flex-start',
          gap: 3,
        }}
      >
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
              <RecordCard
                key={`record-card-${record.promptId || index}`}
                keyWord={keyWord.current}
                record={record}
                onClick={() => handleView(record)}
              />
            ))}

            <Box
              sx={{
                display: loading ? 'flex' : 'none',
                justifyContent: 'center',
                mt: 2,
                mb: {
                  xs: 'calc(10px + var(--Footer-height))',
                  sm: 2,
                },
              }}
            >
              <CircularProgress />
            </Box>
          </Box>
        </Stack>
        <Divider
          orientation="vertical"
          sx={{
            display: { xs: 'none', sm: 'block' },
          }}
        />
        <HotTags
          infoType="prompt"
          count={30}
          onTagClick={handleTagClick}
          sx={{
            display: { xs: 'none', sm: 'flex' },
            width: '16rem',
          }}
        />
      </Box>
    </>
  );
}
