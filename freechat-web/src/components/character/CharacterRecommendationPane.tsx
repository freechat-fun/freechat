import { useCallback, useEffect, useMemo, useState } from 'react';
import { Box, BoxProps } from '@mui/material';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  CharacterQueryDTO,
  CharacterQueryWhere,
  CharacterSummaryDTO,
} from 'freechat-sdk';
import { CharacterRecommendationList, CharacterRecommendationPoster } from '.';

type CharacterRecommendationPaneProps = BoxProps & {
  lang?: string;
};

export default function CharacterRecommendationPane({
  lang = 'en',
  sx,
  ...others
}: CharacterRecommendationPaneProps) {
  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [records, setRecords] = useState<CharacterSummaryDTO[]>([]);
  const [selectedId, setSelectedId] = useState<number | undefined>();

  const pageSize = 6;

  const recommendationQuery = useCallback(() => {
    const newQuery = new CharacterQueryDTO();
    newQuery.where = new CharacterQueryWhere();
    newQuery.where.visibility = 'public';
    newQuery.where.highPriority = true;
    newQuery.where.lang = lang;
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['modifyTime'];

    return newQuery;
  }, [lang]);

  const selectedRecord = useMemo(() => {
    return records.find((record) => record.characterId === selectedId);
  }, [records, selectedId]);

  useEffect(() => {
    setSelectedId((prevId) => {
      if (records.length === 0) {
        return 0;
      }
      if (records.find((r) => r.characterId === prevId)) {
        return prevId;
      }
      return records[0].characterId;
    });
  }, [records]);

  useEffect(() => {
    characterApi
      ?.searchPublicCharacterSummary(recommendationQuery())
      .then(setRecords)
      .catch(handleError);
  }, [characterApi, handleError, recommendationQuery]);

  return (
    <Box
      sx={{
        flex: 1,
        mx: 'auto',
        borderRadius: 2,
        p: 2,
        display: records.length > 0 ? 'grid' : 'none',
        gridTemplateColumns: '1fr 4fr',
        gridTemplateRows: 'minmax(auto, 1fr)',
        alignItems: 'stretch',
        ...sx,
      }}
      {...others}
    >
      <CharacterRecommendationList
        records={records}
        selectedId={selectedId ?? 0}
        setSelectedId={setSelectedId}
        sx={{
          minHeight: `calc(80px * ${records.length})`,
        }}
      />

      <CharacterRecommendationPoster
        record={selectedRecord}
        maxDescriptionLines={2 * records.length - 2}
        sx={{
          gridTemplateRows: `calc(64px * ${records.length})`,
        }}
      />
    </Box>
  );
}
