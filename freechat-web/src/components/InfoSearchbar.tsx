import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import SearchIcon from '@mui/icons-material/Search';
import { Box, TextField, Button, SxProps, Theme } from '@mui/material';

export default function InfoSearchbar(props: {
  onSearch: (text: string | undefined, modelIds: string[] | undefined) => void;
  sx?: SxProps<Theme>;
}) {
  const { onSearch, sx } = props;
  const { t } = useTranslation('button');

  const [text, setText] = useState('');

  function handleInputChange(event: React.ChangeEvent<HTMLInputElement>): void {
    if (event.target.value !== text) {
      setText(event.target.value);
    }
  }

  function handleSubmit(event: React.FormEvent<HTMLFormElement>): void {
    event.preventDefault();
    const modelIds: string[] = [];
    onSearch(text, modelIds);
  }

  return (
    <form onSubmit={handleSubmit}>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'start',
          alignItems: 'center',
          py: 1,
          pr: 1,
          gap: 3,
          borderRadius: 1,
          ...sx,
        }}
      >
        <TextField
          name="text"
          type="text"
          value={text}
          size="small"
          onChange={handleInputChange}
          placeholder={t('Search title, description, content and more')}
          slotProps={{
            input: {
              startAdornment: (
                <SearchIcon sx={{ color: 'action.active', mr: 1 }} />
              ),
              sx: {
                borderRadius: '6px',
              },
            },
          }}
          sx={{
            flex: 1,
          }}
        />
        <Button size="small" type="submit" variant="contained" color="primary">
          {t('Search')}
        </Button>
      </Box>
    </form>
  );
}
