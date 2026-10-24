import { useEffect, useEffectEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Alert,
  Box,
  Button,
  Checkbox,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  Stack,
  Tab,
  Tabs,
  TextField,
  Typography,
} from '@mui/material';
import { SkillConfirmDTO, SkillInstallDTO } from 'freechat-sdk';
import type { SkillDetailsDTO, SkillDraftDTO } from 'freechat-sdk';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import { normalizeSkillError, readSkillFiles } from '../../libs/skill_utils';
import { SkillFiles } from '.';

type SkillInstallDialogProps = {
  onClose: () => void;
  onInstalled: (skill: SkillDetailsDTO) => void;
};

export default function SkillInstallDialog({
  onClose,
  onInstalled,
}: SkillInstallDialogProps) {
  const { t } = useTranslation('skill');
  const { skillApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const [source, setSource] = useState('paste');
  const [content, setContent] = useState('');
  const [sourceUrl, setSourceUrl] = useState('');
  const [files, setFiles] = useState<NonNullable<SkillDetailsDTO['files']>>({});
  const [draft, setDraft] = useState<SkillDraftDTO>();
  const [selected, setSelected] = useState('SKILL.md');
  const [reviewed, setReviewed] = useState(false);
  const [busy, setBusy] = useState(false);
  const [validationErrorKey, setValidationErrorKey] = useState<string>();
  const [allowedHosts, setAllowedHosts] = useState<string[]>();
  const [hostsFailed, setHostsFailed] = useState(false);
  const [hostsReload, setHostsReload] = useState(0);
  const reportHostsFailure = useEffectEvent((reason: unknown) => {
    const error = normalizeSkillError(reason, t);
    if (error.code === 401) handleError(error);
  });

  useEffect(() => {
    if (!skillApi) return;
    let active = true;
    skillApi
      .getSkillAllowedHosts()
      .then((hosts) => {
        if (active) setAllowedHosts(hosts);
      })
      .catch((reason: unknown) => {
        if (active) {
          setHostsFailed(true);
          reportHostsFailure(reason);
        }
      });
    return () => {
      active = false;
    };
  }, [skillApi, hostsReload]);

  function report(reason: unknown) {
    const error = normalizeSkillError(reason, t);
    setValidationErrorKey(error.messageKey);
    if (error.code === 401) handleError(error);
  }

  async function selectFiles(selectedFiles: File[]) {
    setBusy(true);
    setFiles({});
    try {
      setFiles(await readSkillFiles(selectedFiles));
    } catch (reason) {
      report(reason);
    } finally {
      setBusy(false);
    }
  }

  async function prepare() {
    if (!skillApi || busy) return;
    setBusy(true);
    setValidationErrorKey(undefined);
    const request = new SkillInstallDTO();
    if (source === 'url') {
      request.sourceUrl = sourceUrl.trim();
    } else {
      request.files = source === 'paste' ? { 'SKILL.md': content } : files;
    }
    try {
      const response = await skillApi.prepareSkill(request);
      setDraft(response);
      setSelected('SKILL.md');
      setReviewed(false);
    } catch (reason) {
      report(reason);
    } finally {
      setBusy(false);
    }
  }

  async function confirm() {
    if (!skillApi || !draft?.draftId || !draft.digest || !reviewed || busy)
      return;
    if (draft.expiresAt && new Date(draft.expiresAt).getTime() <= Date.now()) {
      report({ errorCode: 'SKILL_DRAFT_EXPIRED' });
      return;
    }
    setBusy(true);
    const request = new SkillConfirmDTO();
    request.digest = draft.digest;
    try {
      onInstalled(await skillApi.confirmSkill(draft.draftId, request));
    } catch (reason) {
      report(reason);
    } finally {
      setBusy(false);
    }
  }

  const ready =
    source === 'paste'
      ? !!content.trim()
      : source === 'url'
        ? !!sourceUrl.trim() && !!allowedHosts?.length
        : Object.prototype.hasOwnProperty.call(files, 'SKILL.md');

  return (
    <Dialog open fullWidth maxWidth="lg" onClose={() => !busy && onClose()}>
      <DialogTitle>
        {draft
          ? t('Review skill before installing')
          : t('Install a private skill')}
      </DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          {validationErrorKey && (
            <Alert severity="error">{t(validationErrorKey)}</Alert>
          )}
          <Alert severity="info">
            {t(
              'Installing skills requires a server-configured LLM, even for pasted text or uploaded files. The server reviews content for dangerous scripts, regardless of file name or document format.'
            )}
          </Alert>
          {hostsFailed ? (
            <Alert
              severity="error"
              action={
                <Button
                  onClick={() => {
                    setHostsFailed(false);
                    setAllowedHosts(undefined);
                    setHostsReload((value) => value + 1);
                  }}
                >
                  {t('Retry')}
                </Button>
              }
            >
              {t(
                'Could not load allowed source hosts. Retry or use pasted text or files.'
              )}
            </Alert>
          ) : allowedHosts?.length ? (
            <Stack
              direction="row"
              spacing={1}
              useFlexGap
              sx={{ alignItems: 'center', flexWrap: 'wrap' }}
            >
              <Typography variant="body2">
                {t('Allowed source hosts')}
              </Typography>
              <Stack
                component="ul"
                direction="row"
                spacing={1}
                useFlexGap
                aria-label={t('Allowed source hosts')}
                sx={{
                  m: 0,
                  p: 0,
                  listStyle: 'none',
                  flexWrap: 'wrap',
                  maxWidth: '100%',
                }}
              >
                {allowedHosts.map((host) => (
                  <Chip
                    component="li"
                    key={host}
                    label={host}
                    title={host}
                    size="small"
                    variant="outlined"
                    sx={{ maxWidth: '100%' }}
                  />
                ))}
              </Stack>
            </Stack>
          ) : (
            <Typography variant="body2" sx={{ overflowWrap: 'anywhere' }}>
              {allowedHosts === undefined
                ? t('Loading allowed source hosts...')
                : t(
                    'No source hosts are allowed. Use pasted text or files instead.'
                  )}
            </Typography>
          )}
          <Typography variant="body2" color="text.secondary">
            {t(
              'GitHub repository roots and skill directories, and ClawHub skill pages, import a complete pinned file bundle. Other allowed links must return UTF-8 text. The server reviews content for dangerous scripts.'
            )}
          </Typography>
          <Alert severity="warning">
            {t(
              'Automated review does not guarantee safety. Read every file. Never include secrets in skills used by shared characters.'
            )}
          </Alert>
          {draft ? (
            <>
              <Typography variant="h6">{draft.name}</Typography>
              <Typography
                sx={{ whiteSpace: 'pre-wrap', overflowWrap: 'anywhere' }}
              >
                {draft.description}
              </Typography>
              <Typography variant="body2">
                {t(
                  'All files shown here will be installed exactly as previewed.'
                )}
              </Typography>
              {draft.sourceUrl && (
                <Typography variant="body2" sx={{ overflowWrap: 'anywhere' }}>
                  {t('Resolved source')}: {draft.sourceUrl}
                </Typography>
              )}
              {draft.sourceRevision && (
                <Typography variant="body2" sx={{ overflowWrap: 'anywhere' }}>
                  {t('Pinned source version')}: {draft.sourceRevision}
                </Typography>
              )}
              <Typography variant="body2" sx={{ overflowWrap: 'anywhere' }}>
                {t('Digest')}: {draft.digest}
              </Typography>
              <Typography variant="body2">
                {t('Preview expires at')}: {draft.expiresAt?.toLocaleString()}
              </Typography>
              {draft.expectedRevision && (
                <Typography variant="body2">
                  {t('Replaces revision')}: {draft.expectedRevision}
                </Typography>
              )}
              <SkillFiles
                files={draft.files ?? {}}
                selected={selected}
                onSelect={setSelected}
                disabled={busy}
              />
              <FormControlLabel
                control={
                  <Checkbox
                    checked={reviewed}
                    onChange={(event) => setReviewed(event.target.checked)}
                    disabled={busy}
                  />
                }
                label={t(
                  'I reviewed all files and confirm installation of this exact digest.'
                )}
              />
            </>
          ) : (
            <>
              <Tabs
                value={source}
                onChange={(_, value: string) => setSource(value)}
                aria-label={t('Install source')}
              >
                <Tab
                  value="paste"
                  label={t('Paste SKILL.md')}
                  disabled={busy}
                />
                <Tab value="files" label={t('Select files')} disabled={busy} />
                <Tab value="url" label={t('Approved URL')} disabled={busy} />
              </Tabs>
              {source === 'paste' && (
                <TextField
                  label={t('SKILL.md content')}
                  required
                  multiline
                  minRows={12}
                  value={content}
                  onChange={(event) => setContent(event.target.value)}
                  disabled={busy}
                />
              )}
              {source === 'files' && (
                <Box>
                  <Button component="label" variant="outlined" disabled={busy}>
                    {t('Select UTF-8 files')}
                    <input
                      hidden
                      type="file"
                      multiple
                      onChange={(event) => {
                        const selectedFiles = Array.from(
                          event.target.files ?? []
                        );
                        event.target.value = '';
                        if (selectedFiles.length)
                          void selectFiles(selectedFiles);
                      }}
                    />
                  </Button>
                  <Typography variant="body2" sx={{ mt: 1 }}>
                    {t(
                      'Select SKILL.md and any UTF-8 reference files together, regardless of extension. Safe relative paths and size limits still apply; the server reviews content for dangerous scripts.'
                    )}
                  </Typography>
                  {Object.keys(files).map((name) => (
                    <Typography key={name} variant="body2">
                      {name}
                    </Typography>
                  ))}
                </Box>
              )}
              {source === 'url' && (
                <TextField
                  type="text"
                  label={t('Approved source URL')}
                  required
                  value={sourceUrl}
                  onChange={(event) => setSourceUrl(event.target.value)}
                  disabled={busy}
                  helperText={t(
                    'The LLM interprets your link, including surrounding text or a missing HTTPS prefix. The server verifies and previews all files before installation.'
                  )}
                />
              )}
            </>
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={busy}>
          {t('Cancel')}
        </Button>
        {draft ? (
          <>
            <Button
              onClick={() => {
                setDraft(undefined);
                setReviewed(false);
              }}
              disabled={busy}
            >
              {t('Back to source')}
            </Button>
            <Button
              variant="contained"
              onClick={() => void confirm()}
              disabled={busy || !reviewed || !draft.digest}
            >
              {t('Confirm installation')}
            </Button>
          </>
        ) : (
          <Button
            variant="contained"
            onClick={() => void prepare()}
            disabled={busy || !ready}
          >
            {busy ? t('Analyzing and checking...') : t('Prepare preview')}
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
