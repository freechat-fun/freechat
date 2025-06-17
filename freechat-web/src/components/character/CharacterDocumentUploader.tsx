import { createRef, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  FlexBox,
  DynamicFlexBox,
  GridBox,
  ConfirmModal,
  OptionCard,
  OptionTooltip,
  TinyInput,
} from '..';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import {
  Chip,
  CircularProgress,
  Divider,
  FormControl,
  FormHelperText,
  IconButton,
  Input,
  Slider,
  Stack,
  Tab,
  Tabs,
  Typography,
} from '@mui/material';
import { FileUploadRounded } from '@mui/icons-material';
import { extractFilenameFromUrl } from '../../libs/url_utils';
import { RagTaskDTO } from 'freechat-sdk';
import { SxProps, Theme } from '@mui/material/styles';
import { HelpIcon } from '../icon';

const MAX_FILE_SIZE = 3 * 1024 * 1024;
let idCounter = 0;

type CharacterDocumentUploaderProps = {
  characterUid?: string;
  open?: boolean;
  onClose?: (succeeded: boolean) => void;
  sx?: SxProps<Theme>;
};

export default function CharacterDocumentUploader({
  characterUid,
  open = false,
  onClose = () => {},
  sx,
}: CharacterDocumentUploaderProps) {
  const { t } = useTranslation(['character', 'button']);
  const { characterApi, ragApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [documentFile, setDocumentFile] = useState<File>();
  const [documentUrl, setDocumentUrl] = useState('');
  const [documentType, setDocumentType] = useState<'file' | 'url'>('file');
  const [documentUploading, setDocumentUploading] = useState(false);
  const [documentFileInvalid, setDocumentFileInvalid] = useState(false);
  const [documentUrlInvalid, setDocumentUrlInvalid] = useState(false);
  const [documentMaxSegmentSize, setDocumentMaxSegmentSize] = useState(300);
  const [documentMaxOverlapSize, setDocumentMaxOverlapSize] = useState(30);

  const fileInputId = useRef(`file-upload-input-${idCounter}`).current;
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const splitterInputRefs = useRef(
    Array(2).fill(createRef<HTMLInputElement | null>())
  );

  useEffect(() => {
    idCounter++;
  }, []);

  function close(succeeded: boolean): void {
    setDocumentFile(undefined);
    setDocumentUrl('');
    setDocumentType('file');
    setDocumentUploading(false);
    setDocumentFileInvalid(false);
    setDocumentUrlInvalid(false);
    onClose(succeeded);
  }

  function handleDocumentFileChange(
    event: React.ChangeEvent<HTMLInputElement>
  ): void {
    const file = event.target.files && event.target.files[0];
    if (file) {
      if (file.size > MAX_FILE_SIZE) {
        setDocumentFileInvalid(true);
      } else {
        setDocumentFileInvalid(false);
        setDocumentFile(file);
      }
    } else {
      setDocumentFileInvalid(false);
    }
  }

  function handleDocumentFileModify(): void {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  }

  function handleDocumentUrlModify(url: string): void {
    try {
      new URL(url);
      setDocumentUrlInvalid(false);
    } catch {
      setDocumentUrlInvalid(true);
    }
    setDocumentUrl(url);
  }

  function handleDocumentConfirm(): void {
    if (!characterUid) {
      return;
    }

    const startNewTask = (source: string, sourceType: string) => {
      const request = new RagTaskDTO();
      request.source = source;
      request.sourceType = sourceType;
      request.maxSegmentSize = documentMaxSegmentSize;
      request.maxOverlapSize = documentMaxOverlapSize;
      ragApi
        ?.createRagTask(characterUid, request)
        .then((taskId) => {
          ragApi
            .startRagTask(taskId)
            .then(() => close(true))
            .catch(handleError);
        })
        .catch(handleError);
    };

    if (documentType === 'file' && documentFile) {
      setDocumentUploading(true);
      characterApi
        ?.uploadCharacterDocument(characterUid, documentFile)
        .then((url) => {
          const key = extractFilenameFromUrl(url);
          startNewTask(key, documentType);
        })
        .finally(() => setDocumentUploading(false));
    } else if (documentType === 'url' && documentUrl) {
      startNewTask(documentUrl, documentType);
    }
  }

  return (
    <ConfirmModal
      open={open}
      onClose={() => close(false)}
      dialog={{
        title: t('Add Document'),
      }}
      onConfirm={handleDocumentConfirm}
      sx={sx}
    >
      <Stack spacing={2}>
        <Tabs
          value={documentType === 'file' ? 0 : 1}
          onChange={(_event, newValue) =>
            setDocumentType(newValue === 0 ? 'file' : 'url')
          }
          sx={{
            bgcolor: 'transparent',
            minWidth: '760px',
          }}
        >
          <Tab
            label={t('FILE')}
            sx={{
              fontWeight: 600,
              color: 'text.secondary',
              '&.Mui-selected': {
                color: 'text.primary',
              },
            }}
          />
          <Tab
            label={t('URL')}
            sx={{
              fontWeight: 600,
              color: 'text.secondary',
              '&.Mui-selected': {
                color: 'text.primary',
              },
            }}
          />
        </Tabs>

        {documentType === 'file' ? (
          <Stack spacing={2}>
            <Typography variant="body2">
              {t(
                'Supported file formats include txt, doc, docx, pdf, ppt, pptx, xls, xlsx, etc. The maximum size for a single file is 3MB.'
              )}
            </Typography>
            <FlexBox>
              {documentFile?.name && <Chip label={documentFile.name} />}
              {documentUploading ? (
                <CircularProgress />
              ) : (
                <FormControl>
                  <Input
                    disabled={documentUploading}
                    type="file"
                    id={fileInputId}
                    onChange={handleDocumentFileChange}
                    sx={{ display: 'none' }}
                    inputProps={{
                      ref: fileInputRef,
                      accept: '*/*',
                    }}
                  />
                  <label htmlFor={fileInputId}>
                    <IconButton
                      onClick={handleDocumentFileModify}
                      disabled={documentUploading}
                    >
                      <FileUploadRounded />
                    </IconButton>
                  </label>
                  {documentFileInvalid && (
                    <FormHelperText error>
                      {t('File too large!')}
                    </FormHelperText>
                  )}
                </FormControl>
              )}
            </FlexBox>
          </Stack>
        ) : (
          <Stack spacing={2}>
            <Typography variant="body2">
              {t('Only publicly accessible URLs are supported.')}
            </Typography>
            <FormControl>
              <Input
                disabled={documentUploading}
                autoFocus
                type="text"
                value={documentUrl}
                onChange={(event) =>
                  handleDocumentUrlModify(event.target.value)
                }
              />
              {documentUrlInvalid && (
                <FormHelperText error>{t('Invalid URL')}</FormHelperText>
              )}
            </FormControl>
          </Stack>
        )}

        <Divider sx={{ mt: 'auto', mx: 2 }}>{t('Splitter Settings')}</Divider>

        <GridBox sx={{ gridTemplateColumns: '1fr 1fr' }}>
          <OptionCard>
            <DynamicFlexBox>
              <Typography variant="subtitle1" color="text.secondary">
                {t('Max Segment Size')}
              </Typography>
              <OptionTooltip
                title={t('The maximum size of a segment in tokens.')}
              >
                <HelpIcon />
              </OptionTooltip>
              <DynamicFlexBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={documentUploading}
                  type="number"
                  slotProps={{
                    htmlInput: {
                      ref: splitterInputRefs.current[0],
                      step: 100,
                      min: 0,
                      max: 1000,
                    },
                  }}
                  value={documentMaxSegmentSize}
                  onChange={(event) =>
                    setDocumentMaxSegmentSize(+event.target.value)
                  }
                />
              </DynamicFlexBox>
            </DynamicFlexBox>
            <Slider
              disabled={documentUploading}
              value={documentMaxSegmentSize}
              step={100}
              min={0}
              max={1000}
              valueLabelDisplay="auto"
              onChange={(_event, newValue) =>
                setDocumentMaxSegmentSize(newValue as number)
              }
            />
          </OptionCard>

          <OptionCard>
            <DynamicFlexBox>
              <Typography variant="subtitle1" color="text.secondary">
                {t('Max Overlap Size')}
              </Typography>
              <OptionTooltip
                title={t(
                  'The maximum size of the overlap between segments in tokens.'
                )}
              >
                <HelpIcon />
              </OptionTooltip>
              <DynamicFlexBox sx={{ ml: 'auto' }}>
                <TinyInput
                  disabled={documentUploading}
                  type="number"
                  slotProps={{
                    htmlInput: {
                      ref: splitterInputRefs.current[1],
                      step: 10,
                      min: 0,
                      max: 100,
                    },
                  }}
                  value={documentMaxOverlapSize}
                  onChange={(event) =>
                    setDocumentMaxOverlapSize(+event.target.value)
                  }
                />
              </DynamicFlexBox>
            </DynamicFlexBox>
            <Slider
              disabled={documentUploading}
              value={documentMaxOverlapSize}
              step={10}
              min={0}
              max={100}
              valueLabelDisplay="auto"
              onChange={(_event, newValue) =>
                setDocumentMaxOverlapSize(newValue as number)
              }
            />
          </OptionCard>
        </GridBox>
      </Stack>
    </ConfirmModal>
  );
}
