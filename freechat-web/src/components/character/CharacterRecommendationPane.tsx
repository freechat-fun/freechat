import { useCallback, useEffect, useMemo, useState } from "react";
import { Sheet, SheetProps } from "@mui/joy";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { CharacterQueryDTO, CharacterQueryWhere, CharacterSummaryDTO } from "freechat-sdk";
import { CharacterRecommendationList, CharacterRecommendationPoster } from ".";

export default function CharacterRecommendationPane(props: SheetProps) {
  const { sx, ...others } = props;

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
    newQuery.pageSize = pageSize;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['modifyTime'];

    return newQuery;
  }, []);


  const selectedRecord = useMemo(() => {
    // console.log(JSON.stringify(records.find(record => record.characterId === selectedId)));
    return records.find(record => record.characterId === selectedId);
  }, [records, selectedId]);

  useEffect(() => {
    setSelectedId(prevId => {
      if (records.length === 0) {
        return 0;
      }
      if (records.find(r => r.characterId === prevId)) {
        return prevId;
      }
      return records[0].characterId;
    })
  }, [records]);

  useEffect(() => {
    characterApi?.searchCharacterSummary(recommendationQuery())
      .then(setRecords)
      .catch(handleError);
  }, [characterApi, handleError, recommendationQuery]);

  return (
    <Sheet
      sx={{
        flex: 1,
        mx: 'auto',
        borderRadius: 'md',
        p: 2,
        display: { xs: 'none', sm: records.length > 0 ? 'grid' : 'none' },
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
        maxDescriptionLines={2 * records.length}
        sx={{
          gridTemplateRows: `calc(64px * ${records.length})`,
        }}
      />
    </Sheet>
  )
}