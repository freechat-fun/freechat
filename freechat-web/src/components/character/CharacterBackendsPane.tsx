import { Fragment, useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import { CharacterBackendDetailsDTO } from 'freechat-sdk';
import {
  DialogContentText,
  IconButton,
  Radio,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Theme,
  Typography,
} from '@mui/material';
import { SxProps } from '@mui/material';
import {
  AddCircleRounded,
  ArticleRounded,
  DeleteForeverRounded,
  DeleteRounded,
  EditRounded,
} from '@mui/icons-material';
import { formatDateTime } from '../../libs/date_utils';
import { CommonBox, ConfirmModal } from '..';

type CharacterBackendsPaneProps = {
  characterUid?: string;
  defaultBackends?: CharacterBackendDetailsDTO[];
  editMode?: boolean;
  sx?: SxProps<Theme>;
  onEdit?: (
    backend: CharacterBackendDetailsDTO,
    backends: CharacterBackendDetailsDTO[]
  ) => void;
};

export default function CharacterBackendsPane({
  characterUid,
  defaultBackends = [],
  editMode = false,
  sx,
  onEdit,
}: CharacterBackendsPaneProps) {
  const navigator = useNavigate();
  const { t } = useTranslation(['character', 'button']);
  const { characterApi, promptTaskApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [backends, setBackends] = useState<Array<CharacterBackendDetailsDTO>>(
    defaultBackends ?? []
  );
  const [backendIdToConfirm, setBackendIdToConfirm] = useState('');

  const getBackends = useCallback(() => {
    if (characterUid) {
      characterApi
        ?.listCharacterBackends(characterUid)
        .then(setBackends)
        .catch(handleError);
    }
  }, [characterApi, handleError, characterUid]);

  useEffect(() => {
    getBackends();
  }, [getBackends]);

  useEffect(() => {
    setBackends(defaultBackends);
  }, [defaultBackends]);

  function handleView(backend: CharacterBackendDetailsDTO | undefined): void {
    if (backend?.chatPromptTaskId) {
      navigator(`/w/prompt/task/${backend.chatPromptTaskId}`);
    }
  }

  function handleDelete(id: string | undefined): void {
    if (id) {
      const backend = backends.find((b) => b.backendId === id);
      if (backend?.chatPromptTaskId) {
        promptTaskApi?.deletePromptTask(backend.chatPromptTaskId);
      }
      characterApi
        ?.removeCharacterBackend(id)
        .then(() => getBackends())
        .catch(handleError);
    }
    setBackendIdToConfirm('');
  }

  function handleTryDelete(id: string | undefined): void {
    if (id) {
      setBackendIdToConfirm(id);
    }
  }

  function handleDefaultChange(id: string | undefined): void {
    if (id) {
      characterApi
        ?.setDefaultCharacterBackend(id)
        .then(() => getBackends())
        .catch(handleError);
    }
  }

  return (
    <Stack spacing={3} sx={{ ...sx }}>
      <CommonBox>
        <Typography variant="subtitle1">
          {t('Character backends: (maximum of 3 backends allowed)')}
        </Typography>
        {editMode && (
          <IconButton
            disabled={backends.length >= 5}
            color="primary"
            onClick={() =>
              onEdit?.(new CharacterBackendDetailsDTO(), [...backends])
            }
          >
            <AddCircleRounded />
          </IconButton>
        )}
      </CommonBox>

      <TableContainer sx={{ display: backends.length > 0 ? 'block' : 'none' }}>
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
                {t('Message Window')}
              </TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>
                {t('Moderation Model')}
              </TableCell>
              <TableCell sx={{ fontWeight: 'bold' }}>
                {t('As Default')}
              </TableCell>
              <TableCell width="25%" sx={{ fontWeight: 'bold' }}>
                {t('Actions')}
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {backends.map((backend, index) => (
              <TableRow key={`backend-${backend.backendId ?? index}`}>
                <TableCell>{index}</TableCell>
                <TableCell>{formatDateTime(backend.gmtCreate)}</TableCell>
                <TableCell>{backend.messageWindowSize}</TableCell>
                <TableCell>{backend.moderationModelId}</TableCell>
                <TableCell>
                  <Radio
                    disabled={!editMode}
                    value={backend.backendId}
                    checked={backend.isDefault}
                    name="backend-default"
                    onChange={(event) =>
                      handleDefaultChange(event.target.value)
                    }
                  />
                </TableCell>
                <TableCell>
                  <IconButton
                    disabled={!backend.chatPromptTaskId}
                    onClick={() => handleView(backend)}
                  >
                    <ArticleRounded fontSize="small" />
                  </IconButton>
                  {editMode && (
                    <Fragment>
                      <IconButton
                        onClick={() => onEdit?.({ ...backend }, [...backends])}
                      >
                        <EditRounded fontSize="small" />
                      </IconButton>
                      <IconButton
                        onClick={() => handleTryDelete(backend.backendId)}
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
        open={!!backendIdToConfirm}
        onClose={() => setBackendIdToConfirm('')}
        obj={backendIdToConfirm}
        dialog={{
          color: 'error',
          title: t('Do you really want to delete this character backend?'),
        }}
        button={{
          color: 'error',
          text: t('button:Delete'),
          startIcon: <DeleteForeverRounded />,
        }}
        onConfirm={handleDelete}
      >
        <DialogContentText />
      </ConfirmModal>
    </Stack>
  );
}
