import { createRef, useEffect, useRef, useState } from "react";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { Box } from "@mui/joy";
import { Transition } from "react-transition-group";
import { defaultTransitionInterval, defaultTransitionSetting, initTransitionSequence, transitionStyles } from "../../libs/transition_utils";
import { CharacterAlbumPicture, CharacterAlbumPictureUploader } from ".";
import { ImagePreviewWindow } from "..";

type CharacterAlbumProps = {
  characterId?: number;
  picture?: string;
  setPicture?: (url: string) => void;
}

export default function CharacterAlbum({
  characterId
}: CharacterAlbumProps) {
  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const pageSize = 10;

  const [imagePreviewUrl, setImagePreviewUrl] = useState<string | null>(null);
  const [showPictures, setShowPictures] = useState(false);
  const [pictures, setPictures] = useState<string[]>([]);
  const cardRefs = useRef(Array(pageSize).fill(createRef()));

  useEffect(() => {
    characterId && characterApi?.listCharacterPictures(characterId)
      .then(setPictures)
      .catch(handleError);
      return initTransitionSequence(setShowPictures, undefined, pageSize);
  }, [characterApi, characterId, handleError]);

  function handleImageUploaded(url: string): void {
    setPictures(prevPictures => [...prevPictures, url]);
  }

  function handleImageView(url: string): void {
    setImagePreviewUrl(url);
  }

  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))',
        gap: 3,
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
              onView={() => handleImageView(url)}
              onCheck={() => {}}
              onDelete={() => {}}
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
              onUploaded={handleImageUploaded}
              sx={{
                transition: defaultTransitionSetting,
                ...transitionStyles[state],
              }}
            />
          )}
        </Transition>
      )}
      <ImagePreviewWindow
        src={imagePreviewUrl ?? ''}
        open={!!imagePreviewUrl}
        setOpen={() => setImagePreviewUrl(null)}
      />
    </Box>
  )
}