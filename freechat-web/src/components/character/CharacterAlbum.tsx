import { createRef, useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { Transition } from "react-transition-group";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { Box } from "@mui/joy";
import { defaultTransitionInterval, defaultTransitionSetting, initTransitionSequence, transitionStyles } from "../../libs/ui_utils";
import { CharacterAlbumPicture, CharacterAlbumPictureUploader } from ".";
import { ConfirmModal, ImagePreview, ImagePreviewWindow } from "..";
import { DeleteForeverRounded } from "@mui/icons-material";
import { extractFilenameFromUrl } from "../../libs/url_utils";

type CharacterAlbumProps = {
  characterId?: number;
  picture?: string | undefined;
  setPicture?: (url: string | undefined) => void;
}

export default function CharacterAlbum({
  characterId,
  picture,
  setPicture,
}: CharacterAlbumProps) {
  const { t } = useTranslation('character');
  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 10;
  const pictureWidth = '140px';

  const [picturePreviewUrl, setPicturePreviewUrl] = useState<string | null>(null);
  const [pictureDeleteUrl, setPictureDeleteUrl] = useState<string | null>(null);
  const [showPictures, setShowPictures] = useState(false);
  const [pictures, setPictures] = useState<string[]>([]);
  const cardRefs = useRef(Array(pageSize).fill(createRef()));

  useEffect(() => {
    characterId && characterApi?.listCharacterPictures(characterId)
      .then(setPictures)
      .catch(handleError);
      return initTransitionSequence(setShowPictures, undefined, pageSize);
  }, [characterApi, characterId, handleError]);

  function handlePictureUploaded(url: string): void {
    setPictures(prevPictures => [...prevPictures, url]);
  }

  function handlePictureDelete(url: string): void {
    if (!url) {
      return;
    }

    const key = extractFilenameFromUrl(url);
    characterApi?.deleteCharacterPicture(key)
      .then(() => setPictures(pictures.filter(item => item !== url)))
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
      {pictures.map((url, index) => (
        <Transition
          in={showPictures}
          timeout={index * defaultTransitionInterval}
          unmountOnExit
          key={`transition-${index}`}
          nodeRef={cardRefs.current[index]}
        >
          {(state) => (
            <CharacterAlbumPicture
              key={`picture-${url}`}
              ref={cardRefs.current[index]}
              url={url}
              checked={url === picture}
              onView={() => setPicturePreviewUrl(url)}
              onCheck={() => setPicture?.(url === picture ? '' : url)}
              onDelete={() => setPictureDeleteUrl(url)}
              sx={{
                transition: defaultTransitionSetting,
                ...transitionStyles[state],
              }}
            />
          )}
        </Transition>
      ))}

      {characterId && pictures.length < pageSize && (
        <Transition
          in={showPictures}
          timeout={pictures.length * defaultTransitionInterval}
          unmountOnExit
          key={`transition-${pageSize - 1}`}
          nodeRef={cardRefs.current[pageSize - 1]}
        >
          {(state) => (
            <CharacterAlbumPictureUploader
              key="picture-new"
              ref={cardRefs.current[pageSize - 1]}
              characterId={characterId}
              onUploaded={handlePictureUploaded}
              sx={{
                minHeight: pictureWidth,
                transition: defaultTransitionSetting,
                ...transitionStyles[state],
              }}
            />
          )}
        </Transition>
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
          color: 'danger',
          title: t('Do you really want to delete this picture?'),
        }}
        button={{
          color: 'danger',
          text: t('button:Delete'),
          startDecorator: <DeleteForeverRounded />
        }}
        onConfirm={handlePictureDelete}
      >
        <ImagePreview
          src={pictureDeleteUrl ?? ''}
          maxWidth={240}
        />
      </ConfirmModal>
    </Box>
  )
}