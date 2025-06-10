import { useTranslation } from 'react-i18next';
import {
  Divider,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import {
  LinePlaceholder,
  MarkdownContent,
  StyledStack,
} from '../../components';
import { PromptDetailsDTO } from 'freechat-sdk';
import { extractJson } from '../../libs/template_utils';
import { TemplateContent } from '.';

export default function PromptContent(props: {
  record: PromptDetailsDTO | undefined;
}) {
  const { record } = props;
  const { t } = useTranslation('prompt');
  const inputs = extractJson(record?.inputs);

  return (
    <Stack
      spacing={3}
      sx={{
        minWidth: { sm: '16rem' },
        mt: 2,
        flex: 1,
      }}
    >
      <StyledStack sx={{ minWidth: { sm: '12rem' } }}>
        <Typography variant="h6" color="primary">
          {t('Description')}
        </Typography>
        <Divider sx={{ my: 1 }} />
        <Typography component="span" variant="body1">
          <MarkdownContent>{record?.description}</MarkdownContent>
        </Typography>
      </StyledStack>

      <StyledStack sx={{ minWidth: { sm: '12rem' } }}>
        <Typography variant="h6" color="primary">
          {t('Template')}
        </Typography>
        <Divider sx={{ my: 1 }} />
        <TemplateContent record={record} />
      </StyledStack>

      {inputs && Object.keys(inputs).length > 0 && (
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell
                  sx={{
                    width: '30%',
                    maxWidth: '50%',
                    wordWrap: 'break-word',
                    backgroundColor: 'action.hover',
                  }}
                >
                  {t('Placeholder')}
                </TableCell>
                <TableCell sx={{ backgroundColor: 'action.hover' }}>
                  {t('Default value')}
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {Object.entries(inputs).map(([k, v]) => (
                <TableRow key={`input-${k}`}>
                  <TableCell>{k}</TableCell>
                  <TableCell>{v}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {record?.example && (
        <StyledStack sx={{ minWidth: { sm: '12rem' } }}>
          <Typography variant="h6" color="primary">
            {t('Example')}
          </Typography>
          <Divider sx={{ my: 1 }} />
          <Typography component="span" variant="body1">
            <MarkdownContent>{record?.example}</MarkdownContent>
          </Typography>
        </StyledStack>
      )}
      <LinePlaceholder spacing={2} />
    </Stack>
  );
}
