import { createRef, forwardRef, useCallback, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { CommonBox, HighlightedTypography, HotTags, InfoSearchbar, LinePlaceholder, SummaryTypography } from "../../components";
import { CharacterQueryDTO, CharacterQueryWhere, CharacterSummaryDTO, CharacterSummaryStatsDTO, ChatCreateDTO, InteractiveStatsDTO } from "freechat-sdk";
import { Avatar, Box, Card, Chip, Divider, IconButton, Link, Stack, Typography, useColorScheme } from "@mui/joy";
import { SxProps } from "@mui/joy/styles/types";
import { KeyboardArrowLeftRounded, KeyboardArrowRightRounded, ShareRounded, VisibilityRounded } from "@mui/icons-material";
import { Transition } from 'react-transition-group';
import { getDateLabel } from '../../libs/date_utils';
import { processBackground, defaultTransitionInterval, defaultTransitionSetting, initTransitionSequence, transitionStyles } from "../../libs/ui_utils";

type RecordCardProps = {
  record: CharacterSummaryStatsDTO,
  keyWord?: string,
  sx?: SxProps,
  onClick?: () => void;
}

const RecordCard = forwardRef<HTMLDivElement, RecordCardProps>((props, ref) => {
  const { record, keyWord, sx , onClick} = props;
  const { i18n } = useTranslation();
  const { mode } = useColorScheme();

  const [tags, setTags] = useState(record?.tags ?? []);
  const [nickname, setNickname] = useState(record?.nickname ?? record?.name);
  const [background, setBackground] = useState('');

  useEffect(() => {
    setTags(record?.tags ?? []);
    setNickname(record?.nickname ?? record?.name);
    record.picture && processBackground(record.picture, mode, 0.3)
      .then(setBackground);
  }, [mode, record]);

  return (
    <Card ref={ref} sx={{
      ...sx,
      transition: 'transform 0.4s, box-shadow 0.4s',
      boxShadow: 'sm',
      '&:hover': {
        boxShadow: 'lg',
        transform: 'translateY(-2px)',
      },
      position: 'relative',
      overflow: 'hidden',
      backgroundImage: `url(${background})`,
      backgroundSize: 'cover',
      backgroundRepeat: 'no-repeat',
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
            onClick?.();
          }}
          sx={{
            gap: 2,
          }}
        >
          <Avatar alt={nickname} src={record.avatar} size="md" />
          <div>
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
              {nickname}
            </HighlightedTypography>
           { nickname !== record.name &&
              <HighlightedTypography highlight={keyWord} level="body-sm">{`@${record.name}`}</HighlightedTypography> }
          </div>
        </Link>
        <Chip color="success" variant="soft">v{record.version}</Chip>
        {(record.gender === 'female' || record.gender === 'male') && (
          <Chip color="warning" variant="outlined">{record.gender}</Chip>
        )}
      </Box>
      <Divider />

      <SummaryTypography highlight={keyWord} sx={{...sx}}>
        {record.description}
      </SummaryTypography>
      <Divider />

      {tags.length > 0 && (
        <CommonBox>
          {tags.map((tag, index) => (
            <Chip variant="outlined" color="success" key={`tag-${record.characterId}-${tag}-${index}`}>{tag}</Chip>
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
        <Typography level="body-sm">
          {getDateLabel(record.gmtModified || new Date(0), i18n.language)}
        </Typography>
        <CommonBox>
          <Chip size="sm" variant="plain" sx={{bgcolor: 'transparent'}} startDecorator={<VisibilityRounded />} >
            {record.viewCount}
          </Chip>
          <Chip size="sm" variant="plain" sx={{bgcolor: 'transparent'}} startDecorator={<ShareRounded />} >
            {record.referCount}
          </Chip>
        </CommonBox>
      </Box>
    </Card>
  )
});

type CharacterGalleryProps = {
  all?: boolean,
};

export default function CharacterGallery({
  all = false,
}: CharacterGalleryProps) {
  const navigate = useNavigate();
  const { accountApi, characterApi, chatApi, interactiveStatisticsApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 6;

  const [records, setRecords] = useState<CharacterSummaryStatsDTO[]>([]);
  const [query, setQuery] = useState<CharacterQueryDTO>();
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);

  const [showCards, setShowCards] = useState(false);
  const [showCardsFinish, setShowCardsFinish] = useState(false);

  const keyWord = useRef<string>();
  const cardRefs = useRef(Array(pageSize).fill(createRef()));

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
    return initTransitionSequence(setShowCards, setShowCardsFinish, pageSize);
  }, []);

  useEffect(() => {
    const currentQuery = query || defaultQuery();
    characterApi?.searchCharacterSummary(query || defaultQuery())
      .then(resp => {
        setRecords(resp);
        resp.forEach(r => {
          r.characterUid && interactiveStatisticsApi?.getStatistics('character', r.characterUid)
            .then(stats => characterInfoWithStats(r, stats))
            .then(recordWithStats => {
              setRecords(prevRecords => {
                const newRecords = prevRecords.filter(r1 => r1.characterId !== recordWithStats.characterId);
                newRecords.push(recordWithStats);
                return newRecords;
              });
            })
            .catch(handleError);
        })
        if (currentQuery.pageNum === 0) {
          characterApi.countCharacters(currentQuery)
            .then(setTotal)
            .catch(handleError);
        }
      })
      .catch(handleError);
  }, [characterApi, defaultQuery, handleError, interactiveStatisticsApi, query]);

  function characterInfoWithStats(record: CharacterSummaryDTO, stats: InteractiveStatsDTO | undefined | null): CharacterSummaryStatsDTO {
    const s = {...{
      viewCount: 0,
      referCount: 0,
      recommendCount: 0,
      scoreCount: 0,
      score: 0,
    }, ...stats};
    const recordWithStats: CharacterSummaryStatsDTO = { ...s, ...record };
    return recordWithStats;
  }

  function handleSearch(text: string | undefined): void {
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

  function handleChangePage(newPage: number): void {
    setPage(newPage);
    setQuery(prevQuery => {
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
    }) 
  }

  function handleView(record: CharacterSummaryStatsDTO): void {
    if (!record.characterUid || !record.characterId) {
      return;
    }
    interactiveStatisticsApi?.increaseStatistic('character', record.characterUid, 'view_count')
      .finally(() => {
        chatApi?.getDefaultChatId(record.characterId as number)
          .then(resp => {
            navigate(`/w/chat/${resp}`);
          })
          .catch(() => {
            accountApi?.getUserDetails()
              .then(userDetails => {
                const request = new ChatCreateDTO();
                request.userNickname = userDetails.nickname ?? userDetails.username;
                request.userProfile = userDetails.profile;
                request.characterNickname = record.nickname ?? record.name;
                request.characterId = record.characterId as number;
                request.about = record.defaultScene;

                chatApi.startChat(request)
                  .then(chatId => {
                    navigate(`/w/chat/${chatId}`);
                  })
                  .catch(handleError);
              })
              .catch(handleError);
          });
    });
  }
  
  function getLabelDisplayedRowsTo(): number {
    const currentCount = (page + 1) * pageSize;
    return total === 0 && records.length > 0 ? currentCount : Math.min(total, currentCount);
  }

  function labelDisplayedRows(from: number, to: number, count: number): string {
    return `${from}-${to} of ${to <= count ? count : `more than ${to}`}`;
  }

  function comparator(a: CharacterSummaryStatsDTO, b: CharacterSummaryStatsDTO): number {
    if ((a.priority ?? 1) > (b.priority ?? 1)) return -1;
    if ((a.priority ?? 1) < (b.priority ?? 1)) return 1;

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
        <InfoSearchbar enableModelSelect={false} onSearch={handleSearch} />
        <LinePlaceholder />
        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', sm: 'repeat(auto-fill, minmax(320px, 1fr))' },
            gap: 3,
          }}
        >
          {records.sort(comparator).map((record, index) => (
            <Transition
              in={showCards}
              timeout={index * defaultTransitionInterval}
              unmountOnExit
              key={`transition-${record.characterId || index}`}
              nodeRef={cardRefs.current[index]}
            >
              {(state) => (
                <RecordCard
                  key={`record-card-${record.characterId || index}`}
                  keyWord={keyWord.current}
                  ref={cardRefs.current[index]}
                  record={record}
                  onClick={() => handleView(record)}
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
          mr: 2,
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
        infoType="character"
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