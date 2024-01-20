import { Fragment } from "react";
import { useTranslation } from "react-i18next";
import { Card, Chip, Typography } from "@mui/joy";
import { CommonBox, HistoryTypography, LinePlaceholder, RouterLink } from "../../components";
import { PromptDetailsDTO, AiModelInfoDTO } from "freechat-sdk";
import { getLocaleLabel } from "../../configs/i18n-config";

export default function PromptMeta(props: {
  record: PromptDetailsDTO | undefined,
  history: string[],
}) {
  const { record, history } = props;
  const { t } = useTranslation(['prompt']);

  const format = record?.format;
  const lang = record?.lang;
  const tags: string[] = record?.tags ?? [];
  const models: AiModelInfoDTO[] = record?.aiModels ?? [];

  return (
    <Card sx={{
      width: { xs: '100%', sm: '16rem' },
      my: 2,
      mx: { xs: 0, sm: 2 },
      p: 2,
      boxShadow: 'sm',
    }}>
      {format && (
        <Fragment>
          <Typography level="title-sm" textColor="neutral">
            {t('Format')}
          </Typography>
          <Typography level="body-sm">
            {format === 'f_string' ? 'F-String: ' : 'Mustache: '}&nbsp;<b>{format === 'f_string' ? '{placeholder}' : '{{placeholder}}'}</b>
          </Typography>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {lang && (
        <Fragment>
          <Typography level="title-sm" textColor="neutral">
            {t('Language')}
          </Typography>
          <Typography level="body-sm">
            {getLocaleLabel(lang.split('_')[0]) || lang}
          </Typography>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {tags.length > 0 && (
        <Fragment>
          <Typography level="title-sm" textColor="neutral">
            {t('Tags')}
          </Typography>
          <CommonBox>
            {tags.map((tag, index) => (
              <Chip variant="outlined" color="success" key={`tag-${tag}-${index}`}>{tag}</Chip>
            ))}
          </CommonBox>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {models.length > 0 && (
        <Fragment>
          <Typography level="title-sm" textColor="neutral">
            {t('Models')}
          </Typography>
          <CommonBox>
            {models.map((model, index) => (
              <Chip variant="outlined" color="warning" key={`model-${model.modelId}-${index}`}>{model.name}</Chip>
            ))}
          </CommonBox>
          <LinePlaceholder spacing={2} />
        </Fragment>
      )}

      {history.length > 0 && (
        <Fragment>
          <Typography level="title-sm" textColor="neutral">
            {t('History')}
          </Typography>
          {history.map(id => {
            if (id === record?.promptId) {
              return (
                <HistoryTypography textColor="gray" level="body-sm" key={`history-${id}`}>
                  {id}
                </HistoryTypography>
              );
            } else {
              return (
                <RouterLink href={`/w/prompt/${id}`} key={`history-${id}`}>
                  <HistoryTypography level="body-sm">
                    {id}
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