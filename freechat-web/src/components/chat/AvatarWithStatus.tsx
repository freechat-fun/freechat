import { Avatar, AvatarProps, Badge } from "@mui/joy";
import { getSenderStatusColor } from "../../libs/chat_utils";

type AvatarWithStatusProps = AvatarProps & {
  status?: 'online' | 'offline' | 'invisible';
};

export default function AvatarWithStatus(props: AvatarWithStatusProps) {
  const { status = 'offline', ...other } = props;

  return (
    <div>
      <Badge
        color={getSenderStatusColor(status)}
        variant='solid'
        size="sm"
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        badgeInset="4px 4px"
      >
        <Avatar size="sm" {...other} />
      </Badge>
    </div>
  );
}