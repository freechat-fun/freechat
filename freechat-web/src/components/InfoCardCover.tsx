/* eslint-disable @typescript-eslint/no-explicit-any */
import { ElementType } from 'react';
import { Box, IconButton, styled } from '@mui/material';
import {
  VisibilityRounded,
  EditRounded,
  DeleteRounded,
  FileDownloadRounded,
} from '@mui/icons-material';
import { SxProps, Theme } from '@mui/material/styles';

// Styled component for the card cover overlay
const CardCoverOverlay = styled(Box)(() => ({
  position: 'absolute',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  opacity: 0,
  transition: '0.3s ease-in',
  background:
    'linear-gradient(180deg, transparent 67%, rgba(0,0,0,0.00345888) 68.94%, rgba(0,0,0,0.014204) 70.89%, rgba(0,0,0,0.0326639) 72.83%, rgba(0,0,0,0.0589645) 74.78%, rgba(0,0,0,0.0927099) 76.72%, rgba(0,0,0,0.132754) 78.67%, rgba(0,0,0,0.177076) 80.61%, rgba(0,0,0,0.222924) 82.56%, rgba(0,0,0,0.267246) 84.5%, rgba(0,0,0,0.30729) 86.44%, rgba(0,0,0,0.341035) 88.39%, rgba(0,0,0,0.367336) 90.33%, rgba(0,0,0,0.385796) 92.28%, rgba(0,0,0,0.396541) 94.22%, rgba(0,0,0,0.4) 96.17%)',
  '&:hover, &:focus-within': {
    opacity: 1,
  },
}));

function CardIconButton({
  Icon,
  onClick,
  sx,
}: {
  Icon: React.ElementType;
  onClick: () => void;
  sx?: SxProps<Theme>;
}) {
  return (
    <IconButton
      size="small"
      sx={{
        bgcolor: 'transparent',
        transition: 'transform 0.4s, box-shadow 0.4s',
        '&:hover': {
          bgcolor: 'transparent',
          transform: 'translateY(-2px)',
        },
        ...sx,
      }}
      onClick={(event) => {
        event.stopPropagation();
        onClick();
      }}
    >
      <Icon sx={{ fill: '#fefefe' }} />
    </IconButton>
  );
}

export default function InfoCardCover(props: {
  icons?: {
    view?: ElementType<any>;
    edit?: ElementType<any>;
    download?: ElementType<any>;
    delete?: ElementType<any>;
  };
  onView?: () => void;
  onEdit?: () => void;
  onDownload?: () => void;
  onDelete?: () => void;
}) {
  const { icons, onView, onEdit, onDownload, onDelete } = props;

  const iconSet = {
    view: VisibilityRounded,
    edit: EditRounded,
    download: FileDownloadRounded,
    delete: DeleteRounded,
    ...icons,
  };

  return (
    <CardCoverOverlay>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'flex-end',
          alignItems: 'flex-end',
          alignSelf: 'flex-end',
          gap: 1,
          width: '100%',
          height: '100%',
          p: 1,
        }}
      >
        {onView && <CardIconButton Icon={iconSet.view} onClick={onView} />}
        {onEdit && <CardIconButton Icon={iconSet.edit} onClick={onEdit} />}
        {onDownload && (
          <CardIconButton Icon={iconSet.download} onClick={onDownload} />
        )}
        {onDelete && (
          <CardIconButton Icon={iconSet.delete} onClick={onDelete} />
        )}
      </Box>
    </CardCoverOverlay>
  );
}
