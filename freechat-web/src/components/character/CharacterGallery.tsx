import {
  forwardRef,
  SetStateAction,
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  useErrorMessageBusContext,
  useFrameScrollContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import {
  FlexBox,
  HighlightedTypography,
  HotTags,
  InfoSearchbar,
  LinePlaceholder,
  StyledStack,
  SummaryTypography,
} from '..';
import {
  CharacterQueryDTO,
  CharacterQueryWhere,
  CharacterSummaryDTO,
  CharacterSummaryStatsDTO,
  ChatCreateDTO,
  InteractiveStatsDTO,
} from 'freechat-sdk';
import {
  Avatar,
  Box,
  Chip,
  CircularProgress,
  Divider,
  Link,
  Stack,
  Typography,
  SxProps,
  Theme,
  useColorScheme,
} from '@mui/material';
import {
  Face3Outlined,
  FaceOutlined,
  ShareRounded,
  VisibilityRounded,
} from '@mui/icons-material';
import { getDateLabel } from '../../libs/date_utils';
import { processBackground } from '../../libs/ui_utils';

type RecordCardProps = {
  record: CharacterSummaryStatsDTO;
  keyWord?: string;
  sx?: SxProps<Theme>;
  onClick?: () => void;
};

const RecordCard = forwardRef<HTMLDivElement, RecordCardProps>((props, ref) => {
  const { record, keyWord, sx, onClick } = props;
  const { i18n } = useTranslation();
  const { mode } = useColorScheme();

  const [tags, setTags] = useState(record?.tags ?? []);
  const [nickname, setNickname] = useState(record?.nickname ?? record?.name);
  const [background, setBackground] = useState('');

  useEffect(() => {
    setTags(record?.tags ?? []);
    setNickname(record?.nickname ?? record?.name);
    if (record.picture) {
      processBackground(record.picture, mode, 0.4).then(setBackground);
    }
  }, [mode, record]);

  return (
    <Link
      onClick={(event) => {
        event.preventDefault();
        onClick?.();
      }}
      sx={{
        m: 2,
        p: 0,
        textDecoration: 'none',
        '&:hover, &:focus-within': {
          cursor: 'pointer',
        },
      }}
    >
      <StyledStack
        ref={ref}
        spacing={1}
        sx={{
          ...sx,
          position: 'relative',
          overflow: 'hidden',
          backgroundImage: `url(${background})`,
          backgroundSize: 'cover',
          backgroundRepeat: 'no-repeat',
          m: 0,
          gap: 0,
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
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              gap: 2,
            }}
          >
            <Avatar
              alt={nickname}
              src={record.avatar}
              sx={{ width: 40, height: 40 }}
            />
            <FlexBox sx={{ gap: 0 }}>
              <HighlightedTypography
                highlight={keyWord}
                variant="h6"
                sx={{
                  color: 'text.primary',
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  maxWidth: '20rem',
                }}
              >
                {nickname}
              </HighlightedTypography>
              {nickname !== record.name && (
                <HighlightedTypography
                  highlight={keyWord}
                  variant="caption"
                  sx={{
                    color: 'text.secondary',
                    mt: 1,
                  }}
                >{`@${record.name}`}</HighlightedTypography>
              )}
            </FlexBox>
          </Box>
          <Chip
            color="success"
            variant="outlined"
            size="small"
            label={`v${record.version}`}
          />
          {record.gender === 'male' ? (
            <FaceOutlined
              fontSize="small"
              color="info"
              sx={{ ml: 'auto', mr: 2, opacity: 0.7 }}
            />
          ) : (
            record.gender === 'female' && (
              <Face3Outlined
                fontSize="small"
                color="info"
                sx={{ ml: 'auto', mr: 2, opacity: 0.8 }}
              />
            )
          )}
        </Box>
        <Divider sx={{ mx: -2 }} />

        <SummaryTypography
          highlight={keyWord}
          sx={{ color: 'text.primary', ...sx }}
        >
          {record.description}
        </SummaryTypography>
        <Divider />

        {tags.length > 0 && (
          <FlexBox>
            {tags.map((tag, index) => (
              <Chip
                variant="filled"
                color="success"
                key={`tag-${tag}-${index}`}
                label={tag}
                size="small"
                sx={{
                  opacity: 0.9,
                }}
              />
            ))}
          </FlexBox>
        )}

        {tags.length > 0 && <Divider sx={{ mx: -2 }} />}

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
          <FlexBox>
            <Chip
              size="small"
              variant="outlined"
              icon={<VisibilityRounded />}
              label={record.viewCount}
            />
            <Chip
              size="small"
              variant="outlined"
              icon={<ShareRounded />}
              label={record.referCount}
            />
          </FlexBox>
        </Box>
      </StyledStack>
    </Link>
  );
});

RecordCard.displayName = 'CharacterGallery.RecordCard';

type CharacterGalleryProps = {
  all?: boolean;
};

export default function CharacterGallery({
  all = false,
}: CharacterGalleryProps) {
  const navigate = useNavigate();
  const { setFrameScrollHandler } = useFrameScrollContext();
  const { isAuthorized } = useMetaInfoContext();
  const { accountApi, characterApi, chatApi, interactiveStatisticsApi } =
    useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 6;

  const [records, setRecords] = useState<CharacterSummaryStatsDTO[]>([]);
  const [query, setQuery] = useState<CharacterQueryDTO>();
  const [, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [finish, setFinish] = useState(false);
  const [scrollRemainingHeight, setScrollRemainingHeight] = useState(200);

  const keyWord = useRef<string>(undefined);

  const defaultQuery = useCallback(() => {
    const newQuery = new CharacterQueryDTO();
    newQuery.where = new CharacterQueryWhere();
    newQuery.where.visibility = 'public';
    if (!all) {
      newQuery.where.highPriority = false;
    }
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['viewCount', 'referCount', 'modifyTime'];

    return newQuery;
  }, [all]);

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
          const newQuery = new CharacterQueryDTO();
          newQuery.where = prevQuery.where;
          if (!newQuery.where) {
            newQuery.where = new CharacterQueryWhere();
            newQuery.where.visibility = 'public';
            if (!all) {
              newQuery.where.highPriority = false;
            }
          }
          newQuery.pageSize = prevQuery.pageSize || pageSize;
          newQuery.orderBy = prevQuery.orderBy;
          newQuery.pageNum = newPage;
          return newQuery;
        });
        return newPage;
      });
    }
  }, [loading, finish, scrollRemainingHeight, defaultQuery, all]);

  useEffect(() => {
    if (finish) {
      return;
    }
    const currentQuery = query || defaultQuery();
    setLoading(true);
    characterApi
      ?.searchPublicCharacterSummary(currentQuery)
      .then(async (resp) => {
        if (resp.length == 0) {
          setFinish(true);
          return;
        }
        const newRecords: SetStateAction<CharacterSummaryStatsDTO[]> = [];
        await Promise.all(
          resp.map(async (r) => {
            if (r.characterUid) {
              await interactiveStatisticsApi
                ?.getStatistics('character', r.characterUid)
                .then((stats) => characterInfoWithStats(r, stats))
                .then((recordWithStats) => newRecords.push(recordWithStats))
                .catch(handleError);
            }
          })
        );
        setRecords((prevRecords) => {
          const delta = newRecords.filter(
            (r) =>
              !prevRecords.map((pr) => pr.characterId).includes(r.characterId)
          );
          if (delta && delta.length > 0) {
            return prevRecords.concat(delta);
          }
          return prevRecords;
        });
      })
      .catch(handleError)
      .finally(() => setLoading(false));
  }, [
    characterApi,
    defaultQuery,
    finish,
    handleError,
    interactiveStatisticsApi,
    query,
  ]);

  function characterInfoWithStats(
    record: CharacterSummaryDTO,
    stats: InteractiveStatsDTO | undefined | null
  ): CharacterSummaryStatsDTO {
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
    const recordWithStats: CharacterSummaryStatsDTO = { ...s, ...record };
    return recordWithStats;
  }

  function handleSearch(text: string | undefined): void {
    setRecords([]);
    setFinish(false);
    const where = new CharacterQueryWhere();
    where.text = text;
    where.visibility = 'public';
    if (!all) {
      where.highPriority = false;
    }

    const newQuery = new CharacterQueryDTO();
    newQuery.where = where;
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['viewCount', 'referCount', 'modifyTime'];

    keyWord.current = text;
    setPage(0);
    setQuery(newQuery);
  }

  function handleTagClick(tag: string): void {
    setRecords([]);
    setFinish(false);
    const where = new CharacterQueryWhere();
    where.tags = [tag];
    where.visibility = 'public';
    if (!all) {
      where.highPriority = false;
    }

    const newQuery = new CharacterQueryDTO();
    newQuery.where = where;
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['viewCount', 'referCount', 'modifyTime'];

    setPage(0);
    setQuery(newQuery);
  }

  function handleView(record: CharacterSummaryStatsDTO): void {
    if (!isAuthorized()) {
      navigate('/w/login');
      return;
    } else if (!record.characterUid || !record.characterId) {
      return;
    }
    interactiveStatisticsApi
      ?.increaseStatistic('character', record.characterUid, 'view_count')
      .finally(() => {
        chatApi
          ?.getDefaultChatId(record.characterUid as string)
          .then((resp) => {
            navigate(`/w/chat/${resp}`);
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
                    navigate(`/w/chat/${chatId}`);
                  })
                  .catch(handleError);
              })
              .catch(handleError);
          });
      });
  }

  function comparator(
    a: CharacterSummaryStatsDTO,
    b: CharacterSummaryStatsDTO
  ): number {
    if ((a.priority ?? 1) > (b.priority ?? 1)) return -1;
    if ((a.priority ?? 1) < (b.priority ?? 1)) return 1;

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
          <InfoSearchbar onSearch={handleSearch} sx={{ mx: 2 }} />
          <LinePlaceholder />
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: {
                xs: '1fr',
                sm: 'repeat(auto-fill, minmax(320px, 1fr))',
              },
              gap: 3,
            }}
          >
            {records.sort(comparator).map((record, index) => (
              <RecordCard
                key={`record-card-${record.characterId || index}`}
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
          flexItem
          sx={{
            display: { xs: 'none', sm: 'block' },
          }}
        />
        <HotTags
          infoType="character"
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
