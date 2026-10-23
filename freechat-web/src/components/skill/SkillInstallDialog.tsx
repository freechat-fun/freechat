import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Alert,
  Box,
  Button,
  Checkbox,
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
  const [validationError, setValidationError] = useState<string>();

  function report(reason: unknown) {
    const error = normalizeSkillError(reason, t);
    setValidationError(error.message);
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
    setValidationError(undefined);
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
        ? !!sourceUrl.trim()
        : !!files['SKILL.md']?.trim();

  return (
    <Dialog open fullWidth maxWidth="lg" onClose={() => !busy && onClose()}>
      <DialogTitle>
        {draft
          ? t('Review skill before installing')
          : t('Install a private skill')}
      </DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          {validationError && <Alert severity="error">{validationError}</Alert>}
          <Alert severity="info">
            {t(
              'Skills contain prompt documents only, not scripts. Remote links must use operator-approved hosts. Preparing a preview does not install anything.'
            )}
          </Alert>
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
                      accept=".md,.txt"
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
                      'Select SKILL.md and any reference .md or .txt files together. Only valid UTF-8 text is accepted; archives are not supported.'
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
                  type="url"
                  label={t('Approved source URL')}
                  required
                  value={sourceUrl}
                  onChange={(event) => setSourceUrl(event.target.value)}
                  disabled={busy}
                  helperText={t(
                    'Pasting a link never installs a skill. Prepare and review it first.'
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
            {t('Prepare preview')}
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
