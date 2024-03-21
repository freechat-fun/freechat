import { forwardRef } from "react";
import { Card } from "@mui/joy";
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
    <Card ref={ref} sx={{
      ...sx,
      transition: 'transform 0.4s, box-shadow 0.4s',
      boxShadow: 'sm',
      '&:hover': {
        boxShadow: 'lg',
        transform: 'translateY(-1px)',
      },
      position: 'relative',
      overflow: 'hidden',
      backgroundImage: `url(${url})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundRepeat: 'no-repeat',
    }}>
      <InfoCardCover
        icons={icons}
        onView={() => onView()}
        onEdit={() => onCheck()}
        onDelete={() => onDelete()}
      />
    </Card>
  )
});

export default CharacterAlbumPicture;