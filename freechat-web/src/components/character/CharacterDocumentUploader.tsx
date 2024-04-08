import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { CommonBox, ConfirmModal } from "..";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { CircularProgress, FormControl, FormHelperText, IconButton, Input, Stack, Tab, TabList, TabPanel, Tabs, Typography, tabClasses } from "@mui/joy";
import { FileUploadRounded } from "@mui/icons-material";
import { extractFilenameFromUrl } from "../../libs/url_utils";
import { RagTaskDTO } from "freechat-sdk";
import { SxProps } from "@mui/joy/styles/types";

const MAX_FILE_SIZE = 10 * 1024 * 1024;
let idCounter = 0;

type CharacterDocumentUploaderProps = {
  characterId?: number;
  open?: boolean;
  onClose?: (succeeded: boolean) => void;
  sx?: SxProps;
}

export default function CharacterDocumentUploader({
  characterId,
  open = false,
  onClose = () => {},
  sx,
}: CharacterDocumentUploaderProps) {
  const { t } = useTranslation(['character', 'button']);
  const { characterApi, ragApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [documentFile, setDocumentFile] = useState<File>();
  const [documentUrl, setDocumentUrl] = useState<string>('');
  const [documentType, setDocumentType] = useState<'file' | 'url'>('file');
  const [documentUploading, setDocumentUploading] = useState<boolean>(false);
  const [documentFileInvalid, setDocumentFileInvalid] = useState<boolean>(false);
  const [documentUrlInvalid, setDocumentUrlInvalid] = useState<boolean>(false);

  const inputId = useRef(`file-upload-input-${idCounter}`).current;
  const inputRef = useRef<HTMLInputElement | null>(null);

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

  function handleDocumentFileChange(event: React.ChangeEvent<HTMLInputElement>): void {
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
    if (inputRef.current) {
      inputRef.current.click();
    }
  }

  function handleDocumentUrlModify(url: string): void {
    try {
      new URL(url);
      setDocumentUrlInvalid(false);
    } catch (error) {
      setDocumentUrlInvalid(true);
    }
    setDocumentUrl(url);
  }

  function handleDocumentConfirm(): void {
    if (!characterId) {
      return;
    }

    const startNewTask = (source: string, sourceType: string) => {
      const request = new RagTaskDTO();
      request.source = source;
      request.sourceType = sourceType;
      ragApi?.createRagTask(characterId, request)
        .then(taskId => {
          ragApi.startRagTask(taskId)
            .then(() => close(true))
            .catch(handleError);
        })
        .catch(handleError);
    };

    if (documentType === 'file' && documentFile) {
      setDocumentUploading(true);
      characterApi?.uploadCharacterDocument(characterId, documentFile)
        .then(url => {
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
      <Tabs
        defaultValue={0}
        onChange={(_event, newValue) => setDocumentType(newValue === 0 ? 'file' : 'url')}
        sx={{
          bgcolor: 'transparent',
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
              {t('Supported file formats include txt, doc, docx, pdf, ppt, pptx, xls, xlsx, etc. The maximum size for a single file is 10MB.')}
            </Typography>
            <CommonBox>
              <Typography>{documentFile?.name}</Typography>
              {documentUploading ? (
                <CircularProgress />
              ) : (
                <FormControl>
                  <Input
                    type="file"
                    id={inputId}
                    onChange={handleDocumentFileChange}
                    sx={{ display: 'none' }}
                    slotProps={{ input: {
                      ref: inputRef,
                      accept: "*/*",
                    }}}
                  />
                  <label htmlFor={inputId}>
                    <IconButton onClick={handleDocumentFileModify}>
                      <FileUploadRounded />
                    </IconButton>
                  </label>
                  <FormHelperText sx={{ display: documentFileInvalid ? 'unset' : 'none' }}>
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
                autoFocus
                type="text"
                value={documentUrl}
                onChange={(event => handleDocumentUrlModify(event.target.value))}
              />
              <FormHelperText sx={{ display: documentUrlInvalid ? 'unset' : 'none' }}>
                {t('Invalid URL')}
              </FormHelperText>
            </FormControl>
          </Stack>
        </TabPanel>
      </Tabs>
    </ConfirmModal>
  );
}