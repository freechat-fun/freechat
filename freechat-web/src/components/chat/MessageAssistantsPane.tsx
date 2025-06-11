import { Fragment, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  CharacterQueryDTO,
  CharacterQueryWhere,
  CharacterSummaryDTO,
} from 'freechat-sdk';
import { Box, Divider, Stack, styled } from '@mui/material';
import { MessageAssistant } from '.';
import { LinePlaceholder } from '..';

const AssistantsBox = styled(Box)(({ theme }) => ({
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fill, minmax(100px, 1fr))',
  gap: theme.spacing(3),
  width: '220px',
  [theme.breakpoints.up('sm')]: {
    width: '420px',
  },
  [theme.breakpoints.up('md')]: {
    width: '720px',
  },
}));

type MessageAssistantsPaneProps = {
  assistantUid?: string;
  setAssistant?: (assistant?: CharacterSummaryDTO | null) => void;
};

export default function MessageAssistantsPane({
  assistantUid,
  setAssistant,
}: MessageAssistantsPaneProps) {
  const { t } = useTranslation('character');
  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [officialRecords, setOfficialRecords] = useState<CharacterSummaryDTO[]>(
    []
  );
  const [personalRecords, setPersonalRecords] = useState<CharacterSummaryDTO[]>(
    []
  );

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
    newQuery.where.highPriority = true;
    newQuery.pageSize = maxAssistantCount;
    newQuery.pageNum = 0;
    newQuery.orderBy = ['modifyTime'];

    return newQuery;
  }

  useEffect(() => {
    characterApi
      ?.searchPublicCharacterSummary(officialQuery())
      .then(setOfficialRecords)
      .catch(handleError);

    characterApi
      ?.searchCharacterSummary(personalQuery())
      .then(setPersonalRecords)
      .catch(handleError);
  }, [characterApi, handleError]);

  function handleSelect(character?: CharacterSummaryDTO) {
    if (!character) {
      return;
    }

    if (assistantUid === character.characterUid) {
      setAssistant?.(null);
    } else {
      setAssistant?.(character);
    }
  }

  return (
    <Stack spacing={2}>
      {officialRecords.length > 0 && (
        <Fragment>
          <Divider sx={{ my: 2 }}>
            <Box component="span" sx={{ px: 2 }}>
              {t('Recommended Characters')}
            </Box>
          </Divider>
          <AssistantsBox>
            {officialRecords.map((summary, index) => (
              <MessageAssistant
                key={`message-assistant-official-${summary.characterUid ?? index}`}
                url={summary.avatar}
                checked={assistantUid === summary.characterUid}
                onSelect={() => handleSelect(summary)}
              />
            ))}
          </AssistantsBox>
        </Fragment>
      )}
      {personalRecords.length > 0 && (
        <Fragment>
          <LinePlaceholder spacing={6} />
          <Divider sx={{ my: 2 }}>
            <Box component="span" sx={{ px: 2 }}>
              {t('Your Characters')}
            </Box>
          </Divider>
          <AssistantsBox>
            {personalRecords.map((summary, index) => (
              <MessageAssistant
                key={`message-assistant-personal-${summary.characterUid ?? index}`}
                url={summary.avatar}
                checked={assistantUid === summary.characterUid}
                onSelect={() => handleSelect(summary)}
              />
            ))}
          </AssistantsBox>
        </Fragment>
      )}
    </Stack>
  );
}
