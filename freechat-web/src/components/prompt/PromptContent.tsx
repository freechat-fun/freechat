import { useTranslation } from "react-i18next";
import { Card, Divider, Stack, Table, Typography } from "@mui/joy";
import { MarkdownContent } from "../../components";
import { PromptDetailsDTO } from "freechat-sdk";
import { extractJson } from "../../libs/template_utils";
import { TemplateContent } from ".";

export default function PromptContent(props: {
  record: PromptDetailsDTO | undefined,
}) {
  const { record } = props;
  const { t } = useTranslation(['prompt']);
  const inputs = extractJson(record?.inputs);

  return (
    <Stack spacing={3} sx={{
      minWidth: { sm: '16rem' },
      mt: 2,
      flex: 1,
      transition: 'width 0.3s',
    }}>
      <Card sx={{
        minWidth: { sm: '12rem' },
        p: 2,
        boxShadow: 'sm',
      }}>
        <Typography level="title-lg" color="primary">
          {t('Description')}
        </Typography>
        <Divider />
        <Typography level="body-md">
          <MarkdownContent>
            {record?.description}
          </MarkdownContent>
        </Typography>
      </Card>

      <Card sx={{
        minWidth: { sm: '12rem' },
        p: 2,
        boxShadow: 'sm',
      }}>
        <Typography level="title-lg" color="primary">
          {t('Template')}
        </Typography>
        <Divider />
        <TemplateContent record={record} />
      </Card>

      {inputs && Object.keys(inputs).length > 0 && (
        <Table sx={{ my: 1 }}>
          <thead>
            <tr>
              <th style={{ width: '30%', maxWidth: '50%', overflowWrap: 'break-word' }}>{t('Placeholder')}</th>
              <th>{t('Default value')}</th>
            </tr>
          </thead>
          <tbody>
            {Object.entries(inputs).map(([k, v]) => (
              <tr key={`input-${k}`}>
                <td>{k}</td>
                <td>{v}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {record?.example && (
        <Card sx={{
          minWidth: { sm: '12rem' },
          p: 2,
        }}>
          <Typography level="title-lg" color="primary">
            {t('Example')}
          </Typography>
          <Divider />
          <Typography level="body-md">
            <MarkdownContent>
              {record?.example}
            </MarkdownContent>
          </Typography>
        </Card>
      )}
    </Stack>
  );
}