import { Fragment, useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { CharacterBackendDetailsDTO } from "freechat-sdk";
import { Box, IconButton, Radio, Stack, Table, Typography } from "@mui/joy";
import { SxProps } from "@mui/material";
import { AddCircleRounded, ArticleRounded, DeleteForeverRounded, DeleteRounded, EditRounded } from "@mui/icons-material";
import { formatDateTime } from "../../libs/date_utils";
import { ConfirmModal } from "..";

interface CharacterBackendListProps {
  characterId?: string;
  defaultBackends?: CharacterBackendDetailsDTO[];
  editMode?: boolean;
  sx?: SxProps;
  onEdit?: (backend: CharacterBackendDetailsDTO, backends: CharacterBackendDetailsDTO[]) => void;
}

export default function CharacterBackends({
  characterId,
  defaultBackends = [],
  editMode = false,
  sx,
  onEdit,
}: CharacterBackendListProps) {
  const navigator = useNavigate();
  const { t } = useTranslation(['character', 'button']);
  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [backends, setBackends] = useState<Array<CharacterBackendDetailsDTO>>(defaultBackends ?? []);
  const [backendIdToConfirm, setBackendIdToConfirm] = useState<string>();

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
    id && characterApi?.removeCharacterBackend(id)
      .then(() => getBackends())
      .catch(handleError);
    
    setBackendIdToConfirm(undefined);
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
      <Box sx={{
        display: 'flex',
        justifyContent: 'start',
        alignItems: 'center',
        gap: 3,
      }}>
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
      </Box>

      <Table>
        <thead>
          <tr>
            <th>#</th>
            <th>{t('Creation Time')}</th>
            <th>{t('Message Window Size')}</th>
            <th>{t('Moderation Model')}</th>
            <th>{t('As Default')}</th>
            <th>{t('Actions')}</th>
          </tr>
        </thead>
        <tbody>
          {backends.map((backend, index) => {
              return (
                <tr
                  tabIndex={-1}
                  key={backend.backendId || `unknown-backend-${index}`}
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
              );
            })}
        </tbody>
      </Table>

      <ConfirmModal
        open={!!backendIdToConfirm}
        onClose={() => setBackendIdToConfirm(undefined)}
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