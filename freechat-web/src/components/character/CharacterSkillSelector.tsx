import { useEffect, useEffectEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Alert,
  Box,
  Button,
  Checkbox,
  CircularProgress,
  FormControlLabel,
  Stack,
  Typography,
} from '@mui/material';
import type { SkillSummaryDTO } from 'freechat-sdk';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import { normalizeSkillError } from '../../libs/skill_utils';

type CharacterSkillSelectorProps = {
  enabled: string[];
  disabled: boolean;
  onChange: (ids: string[]) => void;
};

export default function CharacterSkillSelector({
  enabled,
  disabled,
  onChange,
}: CharacterSkillSelectorProps) {
  const { t } = useTranslation('skill');
  const { skillApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useMetaInfoContext();
  const [skills, setSkills] = useState<SkillSummaryDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [failed, setFailed] = useState(false);
  const [reload, setReload] = useState(0);
  const report = useEffectEvent((reason: unknown) =>
    handleError(normalizeSkillError(reason, t))
  );

  useEffect(() => {
    if (!skillApi || !username) return;
    let active = true;
    skillApi
      .listSkills()
      .then((response) => {
        if (active) setSkills(response);
      })
      .catch((reason: unknown) => {
        if (active) {
          setFailed(true);
          report(reason);
        }
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [skillApi, username, reload]);

  const available = new Map(
    skills
      .filter((skill) => skill.skillId)
      .map((skill) => [skill.skillId as string, skill])
  );
  const ids = Array.from(new Set([...enabled, ...available.keys()]));
  return (
    <Stack spacing={2}>
      <Typography variant="h6" color="primary">
        {t('Character skills')}
      </Typography>
      <Typography variant="body2">
        {t(
          'Select your private skills for this character. No selection means no user skills. Saved selections are retained if the inventory cannot be loaded.'
        )}
      </Typography>
      <Alert severity="warning">
        {t(
          'While user skills are enabled, only approved in-process roleplay, memory and skill-reading tools are available. Network, image, file-system and shell tools are unavailable. Skills cannot grant tools.'
        )}
      </Alert>
      <Typography variant="body2" color="text.secondary">
        {t(
          'Never include secrets in skills used by shared characters. The server validates skill access and tool permissions.'
        )}
      </Typography>
      {loading && (
        <CircularProgress size={24} aria-label={t('Loading skills')} />
      )}
      {failed && (
        <Alert
          severity="error"
          action={
            <Button
              onClick={() => {
                setLoading(true);
                setFailed(false);
                setReload((value) => value + 1);
              }}
            >
              {t('Retry')}
            </Button>
          }
        >
          {t(
            'Could not load your skills. Saved selections have not been changed.'
          )}
        </Alert>
      )}
      {!loading && !failed && ids.length === 0 && (
        <Typography variant="body2">
          {t('No skills found. Install a skill from My skills first.')}
        </Typography>
      )}
      <Box
        role="group"
        aria-label={t('Character skills')}
        sx={{ display: 'flex', flexDirection: 'column' }}
      >
        {ids.map((id) => (
          <FormControlLabel
            key={id}
            control={
              <Checkbox
                checked={enabled.includes(id)}
                disabled={disabled || loading || failed}
                onChange={(event) =>
                  onChange(
                    event.target.checked
                      ? [...enabled, id]
                      : enabled.filter((value) => value !== id)
                  )
                }
              />
            }
            label={
              <Box>
                <Typography sx={{ overflowWrap: 'anywhere' }}>
                  {available.get(id)?.name ?? t('Saved skill: {{id}}', { id })}
                </Typography>
                {!loading && !failed && !available.has(id) && (
                  <Typography variant="body2" color="warning.main">
                    {t(
                      'This saved skill is unavailable. It remains selected until you remove it.'
                    )}
                  </Typography>
                )}
              </Box>
            }
          />
        ))}
      </Box>
    </Stack>
  );
}
