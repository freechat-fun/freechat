import { forwardRef } from 'react';
import { Card, CardContent, styled } from '@mui/material';
import { SxProps } from '@mui/material';
import {
  RadioButtonCheckedRounded,
  RadioButtonUncheckedRounded,
} from '@mui/icons-material';
import { InfoCardCover } from '..';

// Styled component for aspect ratio container
const AspectRatioContainer = styled('div')({
  position: 'relative',
  width: '100%',
  paddingTop: '100%', // 1:1 aspect ratio
  '& > img': {
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    objectFit: 'cover',
  },
});

type CharacterAlbumPictureProps = {
  url: string;
  checked?: boolean;
  sx?: SxProps;
  onView: () => void;
  onCheck: () => void;
  onDelete: () => void;
};

const CharacterAlbumPicture = forwardRef<
  HTMLDivElement,
  CharacterAlbumPictureProps
>((props, ref) => {
  const { url, checked = false, sx, onView, onCheck, onDelete } = props;

  const icons = {
    edit: checked ? RadioButtonCheckedRounded : RadioButtonUncheckedRounded,
  };

  return (
    <Card
      onClick={() => onView()}
      sx={{
        ...sx,
        transition: 'transform 0.4s, box-shadow 0.4s',
        boxShadow: 1,
        borderRadius: '6px',
        '&:hover': {
          boxShadow: 3,
          transform: 'translateY(-2px)',
        },
      }}
    >
      <CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}>
        <AspectRatioContainer ref={ref}>
          <img src={url} loading="lazy" />
          <InfoCardCover
            icons={icons}
            onEdit={() => onCheck()}
            onDelete={() => onDelete()}
          />
        </AspectRatioContainer>
      </CardContent>
    </Card>
  );
});

export default CharacterAlbumPicture;
