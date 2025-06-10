import { Avatar, AvatarProps, Badge, BadgeProps } from '@mui/material';
import { getSenderStatusColor } from '../../libs/chat_utils';

type AvatarWithStatusProps = AvatarProps & {
  status?: 'online' | 'offline' | 'invisible';
};

export default function AvatarWithStatus(props: AvatarWithStatusProps) {
  const { status = 'offline', ...other } = props;

  // Map neutral to default for MUI Badge color
  const badgeColor =
    getSenderStatusColor(status) === 'neutral'
      ? 'default'
      : (getSenderStatusColor(status) as BadgeProps['color']);

  return (
    <div>
      <Badge
        color={badgeColor}
        overlap="circular"
        variant="dot"
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        sx={{
          '& .MuiBadge-badge': {
            width: 12,
            height: 12,
            borderRadius: '50%',
            border: '2px solid',
            borderColor: 'background.paper',
          },
        }}
      >
        <Avatar sx={{ width: 32, height: 32 }} {...other} />
      </Badge>
    </div>
  );
}
