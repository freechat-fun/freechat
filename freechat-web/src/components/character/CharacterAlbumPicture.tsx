import { forwardRef } from "react";
import { AspectRatio, Card, CardOverflow } from "@mui/joy";
import { SxProps } from "@mui/material";
import { RadioButtonCheckedRounded, RadioButtonUncheckedRounded } from "@mui/icons-material";
import { InfoCardCover } from "..";

type CharacterAlbumPictureProps = {
  url: string,
  checked?: boolean,
  sx?: SxProps,
  onView: () => void,
  onCheck: () => void,
  onDelete: () => void,
}

const CharacterAlbumPicture = forwardRef<HTMLDivElement, CharacterAlbumPictureProps>((props, ref) => {
  const { url, checked = false, sx, onView, onCheck, onDelete } = props;

  const icons = { edit: checked ? RadioButtonCheckedRounded : RadioButtonUncheckedRounded }

  return (
    <Card ref={ref} onClick={() => onView()} sx={{
      ...sx,
      transition: 'transform 0.4s, box-shadow 0.4s',
      boxShadow: 'sm',
      '&:hover': {
        boxShadow: 'lg',
        transform: 'translateY(-2px)',
      },
    }}>
      <CardOverflow>
        <AspectRatio ratio="1">
          <img
            src={url}
            loading="lazy"
            style={{
              objectFit: 'cover',
            }}
          />
        </AspectRatio>
        <InfoCardCover
          icons={icons}
          onEdit={() => onCheck()}
          onDelete={() => onDelete()}
        />
      </CardOverflow>
      
    </Card>
  )
});

export default CharacterAlbumPicture;