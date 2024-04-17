import { Fragment, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { CharacterBackendDetailsDTO } from "freechat-sdk";
import { IconButton, Radio, Stack, Table, Typography } from "@mui/joy";
import { SxProps } from "@mui/material";
import { AddCircleRounded, ArticleRounded, DeleteForeverRounded, DeleteRounded, EditRounded } from "@mui/icons-material";
import { formatDateTime } from "../../libs/date_utils";
import { CommonBox, ConfirmModal } from "..";

type CharacterBackendsPaneProps = {
  characterId?: number;
  defaultBackends?: CharacterBackendDetailsDTO[];
  editMode?: boolean;
  sx?: SxProps;
  onEdit?: (backend: CharacterBackendDetailsDTO, backends: CharacterBackendDetailsDTO[]) => void;
}

export default function CharacterBackendsPane({
  characterId,
  defaultBackends = [],
  editMode = false,
  sx,
  onEdit,
}: CharacterBackendsPaneProps) {
  const navigator = useNavigate();
  const { t } = useTranslation(['character', 'button']);
  const { characterApi, promptTaskApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [backends, setBackends] = useState<Array<CharacterBackendDetailsDTO>>(defaultBackends ?? []);
  const [backendIdToConfirm, setBackendIdToConfirm] = useState('');

  const getBackends = useCallback(() => {
    characterId && characterApi?.listCharacterBackends(characterId)
      .then(setBackends)
      .catch(handleError);
  }, [characterApi, handleError, characterId]);

  useEffect(() => {
    getBackends();
  }, [getBackends]);

  useEffect(() => {
    setBackends(defaultBackends);
  }, [defaultBackends]);

  function handleView(backend: CharacterBackendDetailsDTO | undefined): void {
    backend?.chatPromptTaskId && navigator(`/w/prompt/task/${backend.chatPromptTaskId}`);
  }

  function handleDelete(id: string | undefined): void {
    if (id) {
      const backend = backends.find(b => b.backendId === id);
      backend?.chatPromptTaskId && promptTaskApi?.deletePromptTask(backend.chatPromptTaskId);
      characterApi?.removeCharacterBackend(id)
          .then(() => getBackends())
          .catch(handleError);
    }
    setBackendIdToConfirm('');
  }

  function handleTryDelete(id: string | undefined): void {
    id && setBackendIdToConfirm(id);
  }

  function handleDefaultChange(id: string | undefined): void {
    id && characterApi?.setDefaultCharacterBackend(id)
      .then(() => getBackends())
      .catch(handleError);
  }

  return (
    <Stack spacing={3} sx={{...sx}}>
      <CommonBox>
        <Typography level="title-md">{t('Character backends: (maximum of 3 backends allowed)')}</Typography>
        {editMode && (
          <IconButton
            disabled={backends.length >= 5}
            color="primary"
            onClick={() => onEdit?.(new CharacterBackendDetailsDTO(), [...backends])}
          >
            <AddCircleRounded />
          </IconButton>
        )}
      </CommonBox>

      <Table sx={{ display: backends.length > 0 ? 'table' : 'none' }}>
        <thead>
          <tr>
            <th style={{ width: '10%' }}>#</th>
            <th>{t('Creation Time')}</th>
            <th>{t('Message Window')}</th>
            <th>{t('Moderation Model')}</th>
            <th>{t('As Default')}</th>
            <th>{t('Actions')}</th>
          </tr>
        </thead>
        <tbody>
          {backends.map((backend, index) => (
            <tr
              tabIndex={-1}
              key={`backend-${backend.backendId ?? index}`}
            >
              <td>{index}</td>
              <td>{formatDateTime(backend.gmtCreate)}</td>
              <td>{backend.messageWindowSize}</td>
              <td>{backend.moderationModelId}</td>
              <td>
                <Radio
                  disabled={!editMode}
                  value={backend.backendId}
                  checked={backend.isDefault}
                  name="backend-default"
                  onChange={(event) => handleDefaultChange(event.target.value)}
                />
              </td>
              <td>
                <IconButton disabled={!backend.chatPromptTaskId} onClick={() => handleView(backend)}>
                  <ArticleRounded fontSize="small" />
                </IconButton>
                {editMode && (
                  <Fragment>
                    <IconButton onClick={() => onEdit?.({...backend}, [...backends])}>
                      <EditRounded fontSize="small" />
                    </IconButton>
                    <IconButton onClick={() => handleTryDelete(backend.backendId)}>
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
        open={!!backendIdToConfirm}
        onClose={() => setBackendIdToConfirm('')}
        obj={backendIdToConfirm}
        dialog={{
          color: 'danger',
          title: t('Do you really want to delete this character backend?'),
        }}
        button={{
          color: 'danger',
          text: t('button:Delete'),
          startDecorator: <DeleteForeverRounded />
        }}
        onConfirm={handleDelete}
      >
        <Typography />
      </ConfirmModal>
    </Stack>
  );
}