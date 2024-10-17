import { forwardRef } from "react";
import { AspectRatio, Card, CardOverflow } from "@mui/joy";
import { SxProps } from "@mui/material";

type MessageAssistantProps = {
  url?: string,
  checked?: boolean,
  sx?: SxProps,
  onSelect: () => void,
}

const MessageAssistant = forwardRef<HTMLDivElement, MessageAssistantProps>((props, ref) => {
  const { url, checked = false, sx, onSelect } = props;

  return (
    <Card ref={ref} onClick={() => onSelect()} sx={{
      ...sx,
      transition: 'transform 0.4s, box-shadow 0.4s',
      border: checked ? 2 : 0,
      boxShadow: 'sm',
      '&:hover': {
        boxShadow: 'lg',
        transform: 'translateY(-2px)',
      },
    }}>
      <CardOverflow>
        <AspectRatio ratio="1" sx={{
          p: 0.5,
          filter: checked ? 'brightness(60%)' : undefined,
        }}>
          <img
            src={url}
            loading="lazy"
            style={{
              objectFit: 'cover',
            }}
          />
        </AspectRatio>
        </CardOverflow>
      </Card>
  );
});

export default MessageAssistant;