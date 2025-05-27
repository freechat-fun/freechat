import { createRef, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  CommonBox,
  CommonContainer,
  CommonGridBox,
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
  TabList,
  TabPanel,
  Tabs,
  Typography,
  tabClasses,
} from '@mui/joy';
import { FileUploadRounded } from '@mui/icons-material';
import { extractFilenameFromUrl } from '../../libs/url_utils';
import { RagTaskDTO } from 'freechat-sdk';
import { SxProps } from '@mui/joy/styles/types';
import { HelpIcon } from '../icon';

const MAX_FILE_SIZE = 3 * 1024 * 1024;
let idCounter = 0;

type CharacterDocumentUploaderProps = {
  characterUid?: string;
  open?: boolean;
  onClose?: (succeeded: boolean) => void;
  sx?: SxProps;
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
          defaultValue={0}
          onChange={(_event, newValue) =>
            setDocumentType(newValue === 0 ? 'file' : 'url')
          }
          sx={{
            bgcolor: 'transparent',
            minWidth: '760px',
          }}
        >
          <TabList
            tabFlex={1}
            size="sm"
            sx={{
              pl: { xs: 0, md: 4 },
              justifyContent: 'left',
              [`&& .${tabClasses.root}`]: {
                fontWeight: '600',
                flex: 'initial',
                color: 'text.tertiary',
                [`&.${tabClasses.selected}`]: {
                  bgcolor: 'transparent',
                  color: 'text.primary',
                  '&::after': {
                    height: '2px',
                    bgcolor: 'primary.500',
                  },
                },
              },
            }}
          >
            <Tab sx={{ borderRadius: '6px 6px 0 0' }} indicatorInset value={0}>
              {t('FILE')}
            </Tab>
            <Tab sx={{ borderRadius: '6px 6px 0 0' }} indicatorInset value={1}>
              {t('URL')}
            </Tab>
          </TabList>
          <TabPanel value={0}>
            <Stack spacing={2}>
              <Typography level="body-sm">
                {t(
                  'Supported file formats include txt, doc, docx, pdf, ppt, pptx, xls, xlsx, etc. The maximum size for a single file is 3MB.'
                )}
              </Typography>
              <CommonBox>
                <Chip sx={{ display: documentFile?.name ? 'unset' : 'none' }}>
                  {documentFile?.name}
                </Chip>
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
                      slotProps={{
                        input: {
                          ref: fileInputRef,
                          accept: '*/*',
                        },
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
                    <FormHelperText
                      sx={{ display: documentFileInvalid ? 'unset' : 'none' }}
                    >
                      {t('File too large!')}
                    </FormHelperText>
                  </FormControl>
                )}
              </CommonBox>
            </Stack>
          </TabPanel>

          <TabPanel value={1}>
            <Stack spacing={2}>
              <Typography level="body-sm">
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
                <FormHelperText
                  sx={{ display: documentUrlInvalid ? 'unset' : 'none' }}
                >
                  {t('Invalid URL')}
                </FormHelperText>
              </FormControl>
            </Stack>
          </TabPanel>
        </Tabs>

        <Divider sx={{ mt: 'auto', mx: 2 }}>{t('Splitter Settings')}</Divider>

        <CommonGridBox sx={{ gridTemplateColumns: '1fr 1fr' }}>
          <OptionCard>
            <CommonContainer>
              <Typography level="title-sm" textColor="neutral">
                {t('Max Segment Size')}
              </Typography>
              <OptionTooltip
                title={t('The maximum size of a segment in tokens.')}
              >
                <HelpIcon />
              </OptionTooltip>
              <CommonContainer sx={{ ml: 'auto' }}>
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
              </CommonContainer>
            </CommonContainer>
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
            <CommonContainer>
              <Typography level="title-sm" textColor="neutral">
                {t('Max Overlap Size')}
              </Typography>
              <OptionTooltip
                title={t(
                  'The maximum size of the overlap between segments in tokens.'
                )}
              >
                <HelpIcon />
              </OptionTooltip>
              <CommonContainer sx={{ ml: 'auto' }}>
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
              </CommonContainer>
            </CommonContainer>
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
        </CommonGridBox>
      </Stack>
    </ConfirmModal>
  );
}
