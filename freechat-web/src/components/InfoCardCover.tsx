import { Box, CardCover, IconButton } from "@mui/joy";
import { VisibilityRounded, EditRounded, DeleteRounded } from "@mui/icons-material";

function CardIconButton({ Icon, onClick }: {
  Icon: React.ElementType,
  onClick: () => void,
}) {
  return (
    <IconButton
      variant="solid"
      color="neutral"
      sx={{ bgcolor: 'rgba(0 0 0 / 0)' }}
      onClick={onClick}
    >
      <Icon />
    </IconButton>
  );
}

export default function InfoCardCover(props: {
  onView: () => void,
  onEdit: () => void,
  onDelete: () => void,
}) {
  const { onView, onEdit, onDelete } = props;

  return (
    <CardCover
        className="gradient-cover"
        sx={{
          '&:hover, &:focus-within': {
            opacity: 1,
          },
          opacity: 0,
          transition: '0.3s ease-in',
          background:
            'linear-gradient(180deg, transparent 67%, rgba(0,0,0,0.00345888) 68.94%, rgba(0,0,0,0.014204) 70.89%, rgba(0,0,0,0.0326639) 72.83%, rgba(0,0,0,0.0589645) 74.78%, rgba(0,0,0,0.0927099) 76.72%, rgba(0,0,0,0.132754) 78.67%, rgba(0,0,0,0.177076) 80.61%, rgba(0,0,0,0.222924) 82.56%, rgba(0,0,0,0.267246) 84.5%, rgba(0,0,0,0.30729) 86.44%, rgba(0,0,0,0.341035) 88.39%, rgba(0,0,0,0.367336) 90.33%, rgba(0,0,0,0.385796) 92.28%, rgba(0,0,0,0.396541) 94.22%, rgba(0,0,0,0.4) 96.17%)',
        }}
      >
        <div>
          <Box sx={{
            display: 'flex',
            justifyContent: 'flex-end',
            alignItems: 'flex-end',
            alignSelf: 'flex-end',
            gap: 1,
            width: '100%',
          }}>
            <CardIconButton Icon={VisibilityRounded} onClick={onView} />
            <CardIconButton Icon={EditRounded} onClick={onEdit} />
            <CardIconButton Icon={DeleteRounded} onClick={onDelete} />
          </Box>
      </div>
      </CardCover>
  );
}