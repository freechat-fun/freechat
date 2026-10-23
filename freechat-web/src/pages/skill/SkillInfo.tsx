import { useEffect, useEffectEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import {
  AddRounded,
  ArrowBackRounded,
  DeleteOutlineRounded,
  EditRounded,
  RefreshRounded,
  SaveRounded,
} from '@mui/icons-material';
import {
  Alert,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { SkillUpdateDTO } from 'freechat-sdk';
import type { SkillDetailsDTO } from 'freechat-sdk';
import { RouterBlocker } from '../../components';
import { SkillFiles } from '../../components/skill';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';
import { isSkillFileName, normalizeSkillError } from '../../libs/skill_utils';

export default function SkillInfo() {
  const { id } = useParams();
  const { username } = useMetaInfoContext();
  return username && id ? (
    <SkillDetails key={`${username}:${id}`} id={id} />
  ) : null;
}

function SkillDetails({ id }: { id: string }) {
  const navigate = useNavigate();
  const { t } = useTranslation('skill');
  const { skillApi } = useFreeChatApiContext();
  const { username } = useMetaInfoContext();
  const { handleError } = useErrorMessageBusContext();
  const [record, setRecord] = useState<SkillDetailsDTO>();
  const [files, setFiles] = useState<NonNullable<SkillDetailsDTO['files']>>({});
  const [selected, setSelected] = useState('SKILL.md');
  const [editing, setEditing] = useState(false);
  const [removeSelected, setRemoveSelected] = useState(false);
  const [busy, setBusy] = useState(false);
  const [loading, setLoading] = useState(true);
  const [failed, setFailed] = useState(false);
  const [reload, setReload] = useState(0);
  const [deleting, setDeleting] = useState(false);
  const [adding, setAdding] = useState(false);
  const [fileName, setFileName] = useState('');
  const [validationError, setValidationError] = useState<string>();
  const reportLoadError = useEffectEvent((reason: unknown) =>
    handleError(normalizeSkillError(reason, t))
  );

  useEffect(() => {
    if (!username || !skillApi || !id) return;
    let active = true;
    skillApi
      .getSkill(id)
      .then((response) => {
        if (!active) return;
        setRecord(response);
        setFiles({ ...response.files });
        setSelected('SKILL.md');
      })
      .catch((reason: unknown) => {
        if (active) {
          setFailed(true);
          reportLoadError(reason);
        }
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [id, skillApi, username, reload]);

  function reloadSkill() {
    setLoading(true);
    setFailed(false);
    setReload((value) => value + 1);
  }

  async function save() {
    if (!skillApi || !record?.revision || !id || busy) return;
    setBusy(true);
    setValidationError(undefined);
    const request = new SkillUpdateDTO();
    request.expectedRevision = record.revision;
    request.files = { ...files };
    if (removeSelected) delete request.files[selected];
    try {
      const response = await skillApi.updateSkill(id, request);
      setRecord(response);
      setFiles({ ...response.files });
      if (!Object.prototype.hasOwnProperty.call(response.files ?? {}, selected))
        setSelected('SKILL.md');
      setEditing(false);
      setRemoveSelected(false);
    } catch (reason) {
      const error = normalizeSkillError(reason, t);
      setValidationError(error.message);
      if (error.code === 401) handleError(error);
    } finally {
      setBusy(false);
    }
  }

  async function deleteSkill() {
    if (!skillApi || !record?.revision || !id || busy) return;
    setBusy(true);
    try {
      await skillApi.deleteSkill(id, record.revision);
      navigate('/w/skills');
    } catch (reason) {
      handleError(normalizeSkillError(reason, t));
    } finally {
      setBusy(false);
    }
  }

  function cancel() {
    setValidationError(undefined);
    setFiles({ ...record?.files });
    if (!Object.prototype.hasOwnProperty.call(record?.files ?? {}, selected))
      setSelected('SKILL.md');
    setRemoveSelected(false);
    setEditing(false);
  }

  function addFile() {
    if (!validNewFile) return;
    setFiles((previous) => ({ ...previous, [fileName]: '' }));
    setSelected(fileName);
    setAdding(false);
    setEditing(true);
  }

  const validNewFile =
    isSkillFileName(fileName) &&
    !Object.prototype.hasOwnProperty.call(files, fileName);
  if (!username) return null;
  if (loading)
    return <CircularProgress aria-label={t('Loading skill')} sx={{ m: 3 }} />;
  if (failed || !record)
    return (
      <Alert
        severity="error"
        sx={{ my: 2 }}
        action={<Button onClick={reloadSkill}>{t('Retry')}</Button>}
      >
        {t('Could not load this skill.')}
      </Alert>
    );

  return (
    <Stack spacing={2} sx={{ py: 2 }}>
      <Stack
        direction="row"
        useFlexGap
        sx={{ flexWrap: 'wrap', gap: 1, alignItems: 'center' }}
      >
        <Button
          startIcon={<ArrowBackRounded />}
          onClick={() => navigate('/w/skills')}
        >
          {t('My skills')}
        </Button>
        <Typography variant="h4" sx={{ flex: 1, overflowWrap: 'anywhere' }}>
          {record.name}
        </Typography>
        {editing ? (
          <>
            <Button onClick={cancel} disabled={busy}>
              {t('Cancel')}
            </Button>
            <Button
              variant="contained"
              startIcon={<SaveRounded />}
              disabled={busy}
              onClick={() => void save()}
            >
              {t('Validate and save')}
            </Button>
          </>
        ) : (
          <>
            <Button
              startIcon={<RefreshRounded />}
              disabled={busy}
              onClick={reloadSkill}
            >
              {t('Reload')}
            </Button>
            <Button
              startIcon={<AddRounded />}
              disabled={busy}
              onClick={() => {
                setFileName('');
                setAdding(true);
              }}
            >
              {t('Add reference file')}
            </Button>
            <Button
              variant="contained"
              startIcon={<EditRounded />}
              disabled={busy}
              onClick={() => setEditing(true)}
            >
              {t('Edit file')}
            </Button>
            <Button
              color="error"
              startIcon={<DeleteOutlineRounded />}
              disabled={busy}
              onClick={() => setDeleting(true)}
            >
              {t('Delete skill')}
            </Button>
          </>
        )}
      </Stack>
      <Stack direction="row" useFlexGap sx={{ flexWrap: 'wrap', gap: 1 }}>
        <Chip label={t('Private')} size="small" variant="outlined" />
        {Array.from(
          new Set(['user', 'prompt-only', ...(record.tags ?? [])])
        ).map((tag) => (
          <Chip
            key={tag}
            label={tag === 'user' || tag === 'prompt-only' ? t(tag) : tag}
            size="small"
            color="success"
            variant="outlined"
          />
        ))}
      </Stack>
      <Typography sx={{ whiteSpace: 'pre-wrap', overflowWrap: 'anywhere' }}>
        {record.description}
      </Typography>
      <Typography variant="body2" sx={{ overflowWrap: 'anywhere' }}>
        {t('Revision')}: {record.revision}
      </Typography>
      <Typography variant="body2" sx={{ overflowWrap: 'anywhere' }}>
        {t('Digest')}: {record.digest}
      </Typography>
      <Alert severity="warning">
        {t(
          'Automated review does not guarantee safety. Read every file. Never include secrets in skills used by shared characters.'
        )}
      </Alert>
      {validationError && <Alert severity="error">{validationError}</Alert>}
      {editing && (
        <Alert severity="info">
          {t(
            'Editing locks file selection, even before changes. Saving validates the entire file bundle; failures keep your draft open.'
          )}
        </Alert>
      )}
      {editing && selected !== 'SKILL.md' && (
        <Button
          color="error"
          disabled={busy}
          onClick={() => setRemoveSelected((value) => !value)}
          sx={{ alignSelf: 'flex-start' }}
        >
          {removeSelected
            ? t('Keep this file')
            : t('Mark this file for removal')}
        </Button>
      )}
      {removeSelected && (
        <Alert severity="warning">
          {t(
            'This file will be removed only after a successful save. Cancel keeps the original bundle.'
          )}
        </Alert>
      )}
      <Divider />
      <SkillFiles
        files={files}
        selected={selected}
        onSelect={(name) => {
          if (!editing) setSelected(name);
        }}
        editing={editing}
        disabled={busy || removeSelected}
        onChange={(content) =>
          setFiles((previous) => ({ ...previous, [selected]: content }))
        }
      />
      <RouterBlocker
        when={editing}
        title={t('Leave skill editor?')}
        message={t('You are editing a skill. Leave and discard this draft?')}
      />
      <Dialog
        open={adding}
        onClose={() => setAdding(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle>{t('Add reference file')}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            fullWidth
            label={t('File name')}
            value={fileName}
            onChange={(event) => setFileName(event.target.value)}
            error={!!fileName && !validNewFile}
            helperText={t(
              'Use a unique relative .md or .txt path, such as references/guide.md. The file is not saved until you validate and save.'
            )}
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setAdding(false)}>{t('Cancel')}</Button>
          <Button disabled={!validNewFile} onClick={addFile}>
            {t('Add and edit')}
          </Button>
        </DialogActions>
      </Dialog>
      <Dialog open={deleting} onClose={() => !busy && setDeleting(false)}>
        <DialogTitle>{t('Delete this private skill?')}</DialogTitle>
        <DialogContent>
          <Typography>
            {t(
              'Characters using this skill may no longer be able to read it. This cannot be undone.'
            )}
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button disabled={busy} onClick={() => setDeleting(false)}>
            {t('Cancel')}
          </Button>
          <Button
            color="error"
            disabled={busy}
            onClick={() => void deleteSkill()}
          >
            {t('Delete skill')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
