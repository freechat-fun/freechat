import { forwardRef, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useErrorMessageBusContext, useFreeChatApiContext } from '../contexts';
import {
  TextField,
  Link,
  Stack,
  StackProps,
  InputAdornment,
} from '@mui/material';
import { HotTagDTO } from 'freechat-sdk';
import { SearchRounded } from '@mui/icons-material';
import { useDebounce } from '../libs/ui_utils';
import { LinePlaceholder } from '.';

type HotTagsProp = StackProps & {
  infoType: string;
  count?: number;
  onTagClick?: (tag: string) => void;
};

const HotTags = forwardRef<HTMLDivElement, HotTagsProp>((props, ref) => {
  const { infoType, count = 10, onTagClick = () => {}, ...others } = props;
  const { t } = useTranslation();
  const { interactiveStatisticsApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [hotTags, setHotTags] = useState<HotTagDTO[]>([]);
  const [keyWord, setKeyWord] = useState('');
  const debouncedSearchTerm = useDebounce<string>(keyWord, 500);

  useEffect(() => {
    if (infoType) {
      interactiveStatisticsApi
        ?.listHotTags(infoType, count, debouncedSearchTerm)
        .then(setHotTags)
        .catch(handleError);
    }
  }, [
    interactiveStatisticsApi,
    infoType,
    count,
    debouncedSearchTerm,
    handleError,
  ]);

  function handleInputChange(event: React.ChangeEvent<HTMLInputElement>): void {
    if (event.target.value !== keyWord) {
      setKeyWord(event.target.value);
    }
  }

  return (
    <Stack spacing={2} ref={ref} {...others}>
      {hotTags.map((tag, index) => (
        <Link
          key={`tag-${tag.content}=${index}`}
          href="#"
          onClick={(event) => {
            event.preventDefault();
            if (tag.content) {
              onTagClick?.(tag.content);
            }
          }}
          sx={{
            textDecoration: 'none',
            '&:hover, &:focus-within': {
              textDecoration: 'underline',
            },
          }}
        >
          {`${tag.content} (${tag.count})`}
        </Link>
      ))}
      <LinePlaceholder spacing={6} />
      <TextField
        name="keyWord"
        type="text"
        value={keyWord}
        onChange={handleInputChange}
        placeholder={t('Search tags')}
        slotProps={{
          input: {
            startAdornment: (
              <InputAdornment position="start">
                <SearchRounded
                  fontSize="small"
                  sx={{ color: 'text.secondary' }}
                />
              </InputAdornment>
            ),
          },
        }}
        fullWidth
        size="small"
      />
    </Stack>
  );
});

export default HotTags;
