import { forwardRef } from "react";
import { Card } from "@mui/joy";
import { SxProps } from "@mui/material";
import { AddRounded } from "@mui/icons-material";
import { ImagePicker } from "..";
import { useFreeChatApiContext, useErrorMessageBusContext } from "../../contexts";
import { getCompressedImage } from "../../libs/ui_utils";

type CharacterAlbumPictureUploaderProps = {
  characterUid: string,
  sx?: SxProps,
  onUploaded?: (url: string) => void,
}

const CharacterAlbumPictureUploader = forwardRef<HTMLDivElement, CharacterAlbumPictureUploaderProps>((props, ref) => {
  const { characterUid, sx, onUploaded } = props;

  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  function handleImageSelect(file: Blob, name: string) {
    getCompressedImage(file, 2 * 1024 * 1024)
      .then(imageInfo => {
        const request = new File([imageInfo.blob], name);
        characterApi?.uploadCharacterPicture(characterUid, request)
          .then(url => onUploaded?.(url))
          .catch(handleError);
      })
      .catch(handleError);
  }
  
  return (
    <Card ref={ref} sx={{
      ...sx,
      justifyContent: 'center',
      alignItems: 'center',
      transition: 'transform 0.4s, box-shadow 0.4s',
      boxShadow: 'sm',
      '&:hover': {
        boxShadow: 'lg',
        transform: 'translateY(-2px)',
      },
    }}>
      <ImagePicker
        key="picture-picker"
        onImageSelect={handleImageSelect}
        variant="plain"
        color="neutral"
        Icon={AddRounded}
      />
    </Card>
  )
});

export default CharacterAlbumPictureUploader;