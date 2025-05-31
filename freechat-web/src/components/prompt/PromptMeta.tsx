import { Fragment } from 'react';
import { useTranslation } from 'react-i18next';
import { Card, Chip, Typography } from '@mui/material';
import { CommonBox, HistoryTypography, LinePlaceholder, RouterLink } from '..';
import { PromptDetailsDTO, AiModelInfoDTO } from 'freechat-sdk';
import { getLocaleLabel } from '../../configs/i18n-config';

export default function PromptMeta(props: {
  record: PromptDetailsDTO | undefined;
  history: [string, number][];
}) {
  const { record, history } = props;
  const { t } = useTranslation(['prompt']);

  const format = record?.format;
  const lang = record?.lang;
  const tags: string[] = record?.tags ?? [];
  const models: AiModelInfoDTO[] = record?.aiModels ?? [];

  return (
    <Card
      sx={{
        width: { xs: '100%', sm: '16rem' },
        my: 2,
        mx: { xs: 0, sm: 2 },
        p: 2,
        boxShadow: 2,
        border: 1,
        borderColor: 'divider',
      }}
    >
      {format && (
        <Fragment>
          <Typography variant="subtitle2" color="text.secondary">
            {t('Format')}
          </Typography>
          <LinePlaceholder spacing={0.5} />
          <Typography variant="body2">
            {format === 'f_string' ? 'F-String: ' : 'Mustache: '}&nbsp;
            <b>{format === 'f_string' ? '{placeholder}' : '{{placeholder}}'}</b>
          </Typography>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {lang && (
        <Fragment>
          <Typography variant="subtitle2" color="text.secondary">
            {t('Language')}
          </Typography>
          <LinePlaceholder spacing={0.5} />
          <Typography variant="body2">
            {getLocaleLabel(lang.split('_')[0]) || lang}
          </Typography>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {tags.length > 0 && (
        <Fragment>
          <Typography variant="subtitle2" color="text.secondary">
            {t('Tags')}
          </Typography>
          <LinePlaceholder spacing={0.5} />
          <CommonBox>
            {tags.map((tag, index) => (
              <Chip
                label={tag}
                variant="outlined"
                color="success"
                key={`tag-${tag}-${index}`}
                size="small"
              />
            ))}
          </CommonBox>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {models.length > 0 && (
        <Fragment>
          <Typography variant="subtitle2" color="text.secondary">
            {t('Models')}
          </Typography>
          <LinePlaceholder spacing={0.5} />
          <CommonBox>
            {models.map((model, index) => (
              <Chip
                label={model.name}
                variant="outlined"
                color="warning"
                key={`model-${model.modelId}-${index}`}
                size="small"
              />
            ))}
          </CommonBox>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {history.length > 0 && (
        <Fragment>
          <Typography variant="subtitle2" color="text.secondary">
            {t('History')}
          </Typography>
          <LinePlaceholder spacing={0.5} />
          {history.map((item) => {
            const label = item[0];
            const id = item[1];
            if (id === record?.promptId) {
              return (
                <HistoryTypography
                  color="text.secondary"
                  fontSize="small"
                  key={`history-${id}`}
                >
                  {label}
                </HistoryTypography>
              );
            } else {
              return (
                <RouterLink href={`/w/prompt/${id}`} key={`history-${id}`}>
                  <HistoryTypography fontSize="small">
                    {label}
                  </HistoryTypography>
                </RouterLink>
              );
            }
          })}
        </Fragment>
      )}
    </Card>
  );
}
