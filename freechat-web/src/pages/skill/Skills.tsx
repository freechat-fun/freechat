import { useEffect, useEffectEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { AddCircleRounded, SearchRounded } from '@mui/icons-material';
import {
  Alert,
  Box,
  Button,
  Card,
  CardActionArea,
  CardContent,
  Chip,
  CircularProgress,
  InputAdornment,
  Pagination,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import type { SkillSummaryDTO } from 'freechat-sdk';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import { SkillInstallDialog } from '../../components/skill';
import { getDateLabel } from '../../libs/date_utils';
import { normalizeSkillError } from '../../libs/skill_utils';

export default function Skills() {
  const { t, i18n } = useTranslation('skill');
  const { skillApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useMetaInfoContext();
  const navigate = useNavigate();
  const [records, setRecords] = useState<SkillSummaryDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [failed, setFailed] = useState(false);
  const [reload, setReload] = useState(0);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(1);
  const [installing, setInstalling] = useState(false);
  const report = useEffectEvent((reason: unknown) =>
    handleError(normalizeSkillError(reason, t))
  );

  useEffect(() => {
    if (!username || !skillApi) return;
    let active = true;
    skillApi
      .listSkills()
      .then((response) => {
        if (active) setRecords(response);
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

  if (!username) return null;
  const query = search.trim().toLocaleLowerCase();
  const filtered = records
    .filter((record) =>
      [
        record.name,
        record.description,
        ...(record.tags ?? []),
        'user',
        'prompt-only',
      ].some((value) => value?.toLocaleLowerCase().includes(query))
    )
    .sort((a, b) =>
      String(b.modified ?? '').localeCompare(String(a.modified ?? ''))
    );
  const pages = Math.max(1, Math.ceil(filtered.length / 6));
  const currentPage = Math.min(page, pages);

  return (
    <Stack spacing={3} sx={{ py: 2, maxWidth: 1344 }}>
      <Typography variant="h4">{t('My skills')}</Typography>
      <Typography color="text.secondary">
        {t(
          'Private prompt-only skills for your characters. Skills do not grant tool permissions.'
        )}
      </Typography>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        sx={{ justifyContent: 'space-between' }}
      >
        <TextField
          size="small"
          label={t('Search names, descriptions and tags')}
          value={search}
          onChange={(event) => {
            setSearch(event.target.value);
            setPage(1);
          }}
          slotProps={{
            input: {
              startAdornment: (
                <InputAdornment position="start">
                  <SearchRounded />
                </InputAdornment>
              ),
            },
          }}
          sx={{ width: { sm: 420 } }}
        />
        <Button
          variant="contained"
          startIcon={<AddCircleRounded />}
          onClick={() => setInstalling(true)}
          sx={{ borderRadius: '20px' }}
        >
          {t('Install skill')}
        </Button>
      </Stack>
      {loading ? (
        <CircularProgress aria-label={t('Loading skills')} />
      ) : failed ? (
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
          {t('Could not load your skills.')}
        </Alert>
      ) : (
        <>
          {filtered.length === 0 && (
            <Typography>{t('No skills found.')}</Typography>
          )}
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns:
                'repeat(auto-fill, minmax(min(100%, 320px), 1fr))',
              gap: 3,
            }}
          >
            {filtered
              .slice((currentPage - 1) * 6, currentPage * 6)
              .map((record) => (
                <Card
                  key={record.skillId}
                  variant="outlined"
                  sx={{ borderRadius: 2 }}
                >
                  <CardActionArea
                    onClick={() =>
                      navigate(
                        `/w/skill/${encodeURIComponent(record.skillId ?? '')}`
                      )
                    }
                    sx={{ height: '100%' }}
                  >
                    <CardContent>
                      <Typography variant="h6" noWrap>
                        {record.name}
                      </Typography>
                      <Stack
                        direction="row"
                        sx={{ justifyContent: 'space-between', my: 1 }}
                      >
                        <Typography variant="body2" color="text.secondary">
                          {getDateLabel(
                            record.modified || new Date(0),
                            i18n.language
                          )}
                        </Typography>
                        <Typography variant="body2" color="warning.main">
                          {t('Private')}
                        </Typography>
                      </Stack>
                      <Stack
                        direction="row"
                        useFlexGap
                        sx={{ gap: 1, flexWrap: 'wrap', my: 1 }}
                      >
                        {Array.from(
                          new Set([
                            'user',
                            'prompt-only',
                            ...(record.tags ?? []),
                          ])
                        ).map((tag) => (
                          <Chip
                            key={tag}
                            label={
                              tag === 'user' || tag === 'prompt-only'
                                ? t(tag)
                                : tag
                            }
                            size="small"
                            variant="outlined"
                            color="success"
                          />
                        ))}
                      </Stack>
                      <Typography
                        variant="body2"
                        sx={{
                          whiteSpace: 'pre-wrap',
                          overflowWrap: 'anywhere',
                          display: '-webkit-box',
                          WebkitLineClamp: 4,
                          WebkitBoxOrient: 'vertical',
                          overflow: 'hidden',
                          minHeight: 80,
                        }}
                      >
                        {record.description}
                      </Typography>
                    </CardContent>
                  </CardActionArea>
                </Card>
              ))}
          </Box>
          {pages > 1 && (
            <Pagination
              count={pages}
              page={currentPage}
              onChange={(_, value) => setPage(value)}
              getItemAriaLabel={(type, value) =>
                type === 'page'
                  ? t('Page {{page}}', { page: value })
                  : type === 'previous'
                    ? t('Previous page')
                    : t('Next page')
              }
              sx={{ alignSelf: 'flex-end' }}
            />
          )}
        </>
      )}
      {installing && (
        <SkillInstallDialog
          onClose={() => setInstalling(false)}
          onInstalled={(record) => {
            setInstalling(false);
            navigate(`/w/skill/${encodeURIComponent(record.skillId ?? '')}`);
          }}
        />
      )}
    </Stack>
  );
}
