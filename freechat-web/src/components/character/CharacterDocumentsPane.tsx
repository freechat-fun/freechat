import { Fragment, ReactNode, useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { IconButton, Link, Stack, Table, Typography } from '@mui/joy';
import { SxProps } from '@mui/joy/styles/types';
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
  sx?: SxProps;
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
        return <PendingOutlined />;
      case 'running':
        return <RunCircleOutlined />;
      case 'succeeded':
        return <CheckCircleOutlined />;
      case 'failed':
        return <ErrorOutlineRounded />;
      default:
        return <HelpOutlineRounded />;
    }
  }

  return (
    <Stack spacing={3} sx={{ ...sx }}>
      <CommonBox>
        <Typography level="title-md">
          {t(
            'Character documents: (A maximum of 10 documents are allowed, each document is less than 3M)'
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

      <Table sx={{ display: ragTasks.length > 0 ? 'table' : 'none' }}>
        <thead>
          <tr>
            <th style={{ width: '10%' }}>#</th>
            <th>{t('Creation Time')}</th>
            <th>{t('Start Time')}</th>
            <th>{t('End Time')}</th>
            <th>{t('Source')}</th>
            <th>{t('Status')}</th>
            <th>{t('Actions')}</th>
          </tr>
        </thead>
        <tbody>
          {ragTasks.map((task) => (
            <tr tabIndex={-1} key={`rag-task-${task.id}`}>
              <td>{task.id}</td>
              <td>{formatDateTime(task.gmtCreate)}</td>
              <td>{formatDateTime(task.gmtStart)}</td>
              <td>{formatDateTime(task.gmtEnd)}</td>
              <td>
                <Link
                  startDecorator={
                    task?.sourceType === 'url' ? (
                      <LinkRounded />
                    ) : (
                      <ArticleRounded />
                    )
                  }
                  target="_blank"
                  href={getTaskLink(task)}
                >
                  {getTaskFilename(task)}
                </Link>
              </td>
              <td>
                <Typography startDecorator={getTaskStatusIcon(task)}>
                  {task.status?.toUpperCase()}
                </Typography>
              </td>
              <td>
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
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <ConfirmModal
        open={!!taskToConfirm}
        onClose={() => setTaskToConfirm(undefined)}
        dialog={{
          color: 'danger',
          title: t('Do you really want to delete this document?'),
        }}
        button={{
          color: 'danger',
          text: t('button:Delete'),
          startDecorator: <DeleteForeverRounded />,
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
