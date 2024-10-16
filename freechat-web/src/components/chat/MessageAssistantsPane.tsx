import { Fragment, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { CharacterQueryDTO, CharacterQueryWhere, CharacterSummaryDTO } from "freechat-sdk";
import { Divider, Stack } from "@mui/joy";
import { MessageAssistant } from ".";

type MessageAssistantsPaneProps = {
  characterId?: number;
  setCharacterId?: (id: number | undefined) => void;
}

export default function MessageAssistantsPane({
  characterId,
  setCharacterId,
}: MessageAssistantsPaneProps) {
  const { t } = useTranslation('character');
  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [officialRecords, setOfficialRecords] = useState<CharacterSummaryDTO[]>([]);
  const [personalRecords, setPersonalRecords] = useState<CharacterSummaryDTO[]>([]);

  const maxAssistantCount = 100;

  function personalQuery(): CharacterQueryDTO {
    const newQuery = new CharacterQueryDTO();
    newQuery.where = new CharacterQueryWhere();
    newQuery.pageSize = maxAssistantCount;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['modifyTime'];

    return newQuery;
  }

  function officialQuery(): CharacterQueryDTO {
    const newQuery = new CharacterQueryDTO();
    newQuery.where = new CharacterQueryWhere();
    newQuery.where.visibility = 'public';
    newQuery.pageSize = maxAssistantCount;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['viewCount', 'referCount', 'modifyTime'];

    return newQuery;
  }

  useEffect(() => {
    characterApi?.searchPublicCharacterSummary(officialQuery())
      .then(setOfficialRecords)
      .catch(handleError);

      characterApi?.searchCharacterSummary(personalQuery())
      .then(setPersonalRecords)
      .catch(handleError);
  }, [characterApi, handleError]);

  return (
    <Stack>
      {officialRecords.length > 0 && (
        <Fragment>
          <Divider>{t('Recommended Characters')}</Divider>
          {officialRecords.map((summary, index) => (
            <MessageAssistant
              key={`message-assistant-official-${summary.characterId ?? index}`}
              url={summary.avatar}
              checked={characterId === summary.characterId}
              onSelect={() => setCharacterId?.(summary.characterId)}>
            </MessageAssistant>
          ))}
        </Fragment>
      )}
      {personalRecords.length > 0 && (
        <Fragment>
          <Divider>{t('Your Characters')}</Divider>
          {personalRecords.map((summary, index) => (
            <MessageAssistant
              key={`message-assistant-personal-${summary.characterId ?? index}`}
              url={summary.avatar}
              checked={characterId === summary.characterId}
              onSelect={() => setCharacterId?.(summary.characterId)}>
            </MessageAssistant>
          ))}
        </Fragment>
      )}
    </Stack>
  );
}