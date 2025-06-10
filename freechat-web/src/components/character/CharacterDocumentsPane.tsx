import { Fragment, ReactNode, useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  IconButton,
  Link,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import { SxProps, Theme } from '@mui/material/styles';
import {
  AddCircleRounded,
  ArticleRounded,
  CheckCircleOutlined,
  DeleteForeverRounded,
  DeleteRounded,
  ErrorOutlineRounded,
  HelpOutlineRounded,
  LinkRounded,
  PendingOutlined,
  RefreshRounded,
  ReplayRounded,
  RunCircleOutlined,
  StopCircleOutlined,
} from '@mui/icons-material';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import { RagTaskDetailsDTO } from 'freechat-sdk';
import { formatDateTime } from '../../libs/date_utils';
import { base64PathDecode, extractFilenameFromUrl } from '../../libs/url_utils';
import { CommonBox, ConfirmModal } from '..';
import { CharacterDocumentUploader } from '.';

type CharacterDocumentsPaneProps = {
  characterUid?: string;
  editMode?: boolean;
  sx?: SxProps<Theme>;
};

export default function CharacterDocumentsPane({
  characterUid,
  editMode = false,
  sx,
}: CharacterDocumentsPaneProps) {
  const { t } = useTranslation(['character', 'button']);
  const { ragApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [ragTasks, setRagTasks] = useState<Array<RagTaskDetailsDTO>>([]);
  const [taskToConfirm, setTaskToConfirm] = useState<RagTaskDetailsDTO>();
  const [newTask, setNewTask] = useState<boolean>(false);

  const getBackends = useCallback(() => {
    if (characterUid) {
      ragApi?.listRagTasks(characterUid).then(setRagTasks).catch(handleError);
    }
  }, [characterUid, ragApi, handleError]);

  useEffect(() => {
    getBackends();
  }, [getBackends]);

  function handleRefreshTasks(): void {
    if (characterUid) {
      ragApi?.listRagTasks(characterUid).then(setRagTasks).catch(handleError);
    }
  }

  function handleRerunTask(task: RagTaskDetailsDTO): void {
    if (task.id) {
      ragApi?.startRagTask(task.id).finally(() => handleRefreshTasks());
    }
  }

  function handleCancelTask(task: RagTaskDetailsDTO): void {
    if (task.id) {
      ragApi?.cancelRagTask(task.id).finally(() => handleRefreshTasks());
    }
  }

  function handleDeleteTask(task: RagTaskDetailsDTO | undefined): void {
    if (!task?.id) {
      return;
    }
    ragApi
      ?.deleteRagTask(task.id)
      .then(() => handleRefreshTasks())
      .catch(handleError);

    setTaskToConfirm(undefined);
  }

  function handleUploaderClose(succeeded: boolean): void {
    if (succeeded) {
      getBackends();
    }
    setNewTask(false);
  }

  function getTaskFilename(task: RagTaskDetailsDTO | undefined): string {
    if (!task?.source) {
      return '';
    }

    const handleFilename = (filename: string) => {
      if (filename.length < 34 || filename[32] !== '-') {
        return filename;
      }
      let wrapped = filename.substring(33);
      if (wrapped.length > 8) {
        wrapped = wrapped.substring(0, 5) + '...';
      }
      return wrapped;
    };

    if (task?.sourceType === 'url') {
      return handleFilename(extractFilenameFromUrl(task.source));
    } else if (task?.sourceType === 'file') {
      const path = base64PathDecode(task.source);
      const parts = path.split('/');
      return handleFilename(parts[parts.length - 1]);
    } else {
      return handleFilename(task.source);
    }
  }

  function getTaskLink(task: RagTaskDetailsDTO | undefined): string {
    if (!task?.source) {
      return '';
    }

    if (task?.sourceType === 'file') {
      const protocol = window.location.protocol;
      const host = window.location.hostname;
      const port = window.location.port;
      return `${protocol}//${host}${port ? `:${port}` : ''}/my/document/${task.source}`;
    } else {
      return task.source;
    }
  }

  function getTaskStatusIcon(task: RagTaskDetailsDTO | undefined): ReactNode {
    if (!task?.status) {
      return '';
    }

    switch (task.status) {
      case 'pending':
        return <PendingOutlined fontSize="small" />;
      case 'running':
        return <RunCircleOutlined fontSize="small" />;
      case 'succeeded':
        return <CheckCircleOutlined fontSize="small" />;
      case 'failed':
        return <ErrorOutlineRounded fontSize="small" />;
      default:
        return <HelpOutlineRounded fontSize="small" />;
    }
  }

  return (
    <Stack spacing={2} sx={{ ...sx }}>
      <CommonBox>
        <Typography variant="subtitle1">
          {t(
            'Character documents: (You can have up to 10 documents, each under 3MB)'
          )}
        </Typography>
        <IconButton onClick={() => handleRefreshTasks()}>
          <RefreshRounded />
        </IconButton>

        {editMode && (
          <IconButton
            disabled={ragTasks.length >= 10}
            color="primary"
            onClick={() => setNewTask(true)}
          >
            <AddCircleRounded />
          </IconButton>
        )}
      </CommonBox>

      <TableContainer sx={{ display: ragTasks.length > 0 ? 'block' : 'none' }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell width="2%" sx={{ fontWeight: 'bold' }}>
                #
              </TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>
                {t('Creation Time')}
              </TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>
                {t('Start Time')}
              </TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>{t('End Time')}</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>{t('Source')}</TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>{t('Status')}</TableCell>
              <TableCell width="25%" sx={{ fontWeight: 'bold' }}>
                {t('Actions')}
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {ragTasks.map((task) => (
              <TableRow key={`rag-task-${task.id}`}>
                <TableCell>{task.id}</TableCell>
                <TableCell>{formatDateTime(task.gmtCreate)}</TableCell>
                <TableCell>{formatDateTime(task.gmtStart)}</TableCell>
                <TableCell>{formatDateTime(task.gmtEnd)}</TableCell>
                <TableCell>
                  <Link
                    component="a"
                    sx={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 1,
                      textDecoration: 'none',
                      '&:hover, &:focus-within': {
                        textDecoration: 'underline',
                      },
                    }}
                    target="_blank"
                    href={getTaskLink(task)}
                  >
                    {task?.sourceType === 'url' ? (
                      <LinkRounded fontSize="small" />
                    ) : (
                      <ArticleRounded fontSize="small" />
                    )}
                    {getTaskFilename(task)}
                  </Link>
                </TableCell>
                <TableCell>
                  <Typography
                    variant="body2"
                    sx={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 1,
                      fontSize: '0.6rem',
                    }}
                  >
                    {getTaskStatusIcon(task)}
                    {task.status?.toUpperCase()}
                  </Typography>
                </TableCell>
                <TableCell>
                  {editMode && (
                    <Fragment>
                      {task.status === 'running' ? (
                        <IconButton onClick={() => handleCancelTask(task)}>
                          <StopCircleOutlined fontSize="small" />
                        </IconButton>
                      ) : (
                        <IconButton onClick={() => handleRerunTask(task)}>
                          <ReplayRounded fontSize="small" />
                        </IconButton>
                      )}

                      <IconButton
                        onClick={() => task.id && setTaskToConfirm(task)}
                      >
                        <DeleteRounded fontSize="small" />
                      </IconButton>
                    </Fragment>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <ConfirmModal
        open={!!taskToConfirm}
        onClose={() => setTaskToConfirm(undefined)}
        dialog={{
          color: 'error',
          title: t('Do you really want to delete this document?'),
        }}
        button={{
          color: 'error',
          text: t('button:Delete'),
          startIcon: <DeleteForeverRounded />,
        }}
        onConfirm={() => handleDeleteTask(taskToConfirm)}
      >
        <Typography>{getTaskFilename(taskToConfirm)}</Typography>
      </ConfirmModal>

      <CharacterDocumentUploader
        key="document-uploader"
        characterUid={characterUid}
        open={newTask}
        onClose={handleUploaderClose}
      />
    </Stack>
  );
}
