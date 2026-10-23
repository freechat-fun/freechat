import { useTranslation } from 'react-i18next';
import {
  Box,
  List,
  ListItemButton,
  ListItemText,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import type { SkillDetailsDTO } from 'freechat-sdk';

type SkillFilesProps = {
  files: NonNullable<SkillDetailsDTO['files']>;
  selected: string;
  onSelect: (name: string) => void;
  editing?: boolean;
  disabled?: boolean;
  onChange?: (content: string) => void;
};

export default function SkillFiles({
  files,
  selected,
  onSelect,
  editing = false,
  disabled = false,
  onChange,
}: SkillFilesProps) {
  const { t } = useTranslation('skill');
  return (
    <Stack
      direction={{ xs: 'column', md: 'row' }}
      spacing={3}
      sx={{ alignItems: 'flex-start', minWidth: 0 }}
    >
      <Box sx={{ flex: 1, minWidth: 0, width: '100%' }}>
        <Typography
          variant="subtitle1"
          sx={{ overflowWrap: 'anywhere', mb: 1 }}
        >
          {selected}
        </Typography>
        {editing ? (
          <TextField
            fullWidth
            multiline
            minRows={16}
            label={t('File content')}
            value={files[selected] ?? ''}
            disabled={disabled}
            onChange={(event) => onChange?.(event.target.value)}
            slotProps={{ input: { sx: { fontFamily: 'monospace' } } }}
          />
        ) : (
          <Box
            component="pre"
            sx={{
              m: 0,
              p: 2,
              bgcolor: 'background.paper',
              borderRadius: 1,
              whiteSpace: 'pre-wrap',
              overflowWrap: 'anywhere',
              fontFamily: 'monospace',
              fontSize: '0.875rem',
              minHeight: 240,
            }}
          >
            {files[selected] ?? ''}
          </Box>
        )}
      </Box>
      <Box sx={{ width: { xs: '100%', md: 260 }, flexShrink: 0 }}>
        <Typography variant="h6">{t('Files')}</Typography>
        {editing && (
          <Typography variant="body2" color="text.secondary">
            {t('Save or cancel before switching files.')}
          </Typography>
        )}
        <List aria-label={t('Files')}>
          {Object.keys(files)
            .sort((a, b) =>
              a === 'SKILL.md' ? -1 : b === 'SKILL.md' ? 1 : a.localeCompare(b)
            )
            .map((name) => (
              <ListItemButton
                key={name}
                selected={selected === name}
                disabled={editing || disabled}
                onClick={() => onSelect(name)}
                sx={{ borderRadius: 1 }}
              >
                <ListItemText
                  primary={name}
                  slotProps={{ primary: { sx: { overflowWrap: 'anywhere' } } }}
                />
              </ListItemButton>
            ))}
        </List>
      </Box>
    </Stack>
  );
}
