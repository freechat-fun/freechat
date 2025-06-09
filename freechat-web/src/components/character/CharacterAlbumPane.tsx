import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
} from '../../contexts';
import { Box } from '@mui/material';
import { CharacterAlbumPicture, CharacterAlbumPictureUploader } from '.';
import { ConfirmModal, ImagePreview, ImagePreviewWindow } from '..';
import { DeleteForeverRounded } from '@mui/icons-material';
import { extractFilenameFromUrl } from '../../libs/url_utils';

type CharacterAlbumPaneProps = {
  characterUid?: string;
  picture?: string | undefined;
  setPicture?: (url: string | undefined) => void;
};

export default function CharacterAlbumPane({
  characterUid,
  picture,
  setPicture,
}: CharacterAlbumPaneProps) {
  const { t } = useTranslation('character');
  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 10;
  const pictureWidth = '140px';

  const [picturePreviewUrl, setPicturePreviewUrl] = useState<string | null>(
    null
  );
  const [pictureDeleteUrl, setPictureDeleteUrl] = useState<string | null>(null);
  const [pictures, setPictures] = useState<string[]>([]);

  useEffect(() => {
    if (characterUid) {
      characterApi
        ?.listCharacterPictures(characterUid)
        .then(setPictures)
        .catch(handleError);
    }
  }, [characterApi, characterUid, handleError]);

  function handlePictureUploaded(url: string): void {
    setPictures((prevPictures) => [...prevPictures, url]);
  }

  function handlePictureDelete(url: string): void {
    if (!url) {
      return;
    }

    const key = extractFilenameFromUrl(url);
    characterApi
      ?.deleteCharacterPicture(key)
      .then(() => setPictures(pictures.filter((item) => item !== url)))
      .catch(handleError)
      .finally(() => setPictureDeleteUrl(null));
  }

  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: `repeat(auto-fill, minmax(${pictureWidth}, 1fr))`,
        gap: 3,
        alignItems: 'stretch',
      }}
    >
      {pictures.map((url) => (
        <CharacterAlbumPicture
          key={`picture-${url}`}
          url={url}
          checked={url === picture}
          onView={() => setPicturePreviewUrl(url)}
          onCheck={() => setPicture?.(url === picture ? '' : url)}
          onDelete={() => setPictureDeleteUrl(url)}
        />
      ))}

      {characterUid && pictures.length < pageSize && (
        <CharacterAlbumPictureUploader
          key="picture-new"
          characterUid={characterUid}
          onUploaded={handlePictureUploaded}
          sx={{
            minHeight: pictureWidth,
          }}
        />
      )}

      <ImagePreviewWindow
        src={picturePreviewUrl ?? ''}
        open={!!picturePreviewUrl}
        setOpen={() => setPicturePreviewUrl(null)}
      />

      <ConfirmModal
        open={!!pictureDeleteUrl}
        onClose={() => setPictureDeleteUrl(null)}
        obj={pictureDeleteUrl}
        dialog={{
          color: 'error',
          title: t('Do you really want to delete this picture?'),
        }}
        button={{
          color: 'error',
          text: t('button:Delete'),
          startIcon: <DeleteForeverRounded />,
        }}
        onConfirm={handlePictureDelete}
      >
        <ImagePreview src={pictureDeleteUrl ?? ''} maxWidth={240} />
      </ConfirmModal>
    </Box>
  );
}
