import { forwardRef } from 'react';
import { Card, CardContent, Theme } from '@mui/material';
import { SxProps } from '@mui/material';
import { AddRounded } from '@mui/icons-material';
import { ImagePicker } from '..';
import {
  useFreeChatApiContext,
  useErrorMessageBusContext,
} from '../../contexts';
import { getCompressedImage } from '../../libs/ui_utils';

type CharacterAlbumPictureUploaderProps = {
  characterUid: string;
  sx?: SxProps<Theme>;
  onUploaded?: (url: string) => void;
};

const CharacterAlbumPictureUploader = forwardRef<
  HTMLDivElement,
  CharacterAlbumPictureUploaderProps
>((props, ref) => {
  const { characterUid, sx, onUploaded } = props;

  const { characterApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  function handleImageSelect(file: Blob, name: string) {
    getCompressedImage(file, 2 * 1024 * 1024)
      .then((imageInfo) => {
        const request = new File([imageInfo.blob], name);
        characterApi
          ?.uploadCharacterPicture(characterUid, request)
          .then((url) => onUploaded?.(url))
          .catch(handleError);
      })
      .catch(handleError);
  }

  return (
    <Card
      ref={ref}
      sx={{
        ...sx,
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        transition: 'transform 0.4s, box-shadow 0.4s',
        boxShadow: 1,
        borderRadius: '6px',
        border: 1,
        borderColor: 'divider',
        backgroundColor: 'background.default',
        '&:hover': {
          boxShadow: 3,
          transform: 'translateY(-2px)',
        },
      }}
    >
      <CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}>
        <ImagePicker
          key="picture-picker"
          onImageSelect={handleImageSelect}
          Icon={AddRounded}
          sx={{ color: 'primary.main' }}
        />
      </CardContent>
    </Card>
  );
});

CharacterAlbumPictureUploader.displayName = 'CharacterAlbumPictureUploader';

export default CharacterAlbumPictureUploader;
