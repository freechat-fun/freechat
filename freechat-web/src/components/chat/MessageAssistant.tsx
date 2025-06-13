import { forwardRef } from 'react';
import { Box, SxProps, Theme } from '@mui/material';
import { StyledStack } from '..';

type MessageAssistantProps = {
  url?: string;
  checked?: boolean;
  sx?: SxProps<Theme>;
  onSelect: () => void;
};

const MessageAssistant = forwardRef<HTMLDivElement, MessageAssistantProps>(
  (props, ref) => {
    const { url, checked = false, sx, onSelect } = props;

    return (
      <StyledStack
        ref={ref}
        onClick={() => onSelect()}
        sx={{
          ...sx,
          p: 0,
          m: 0,
          border: checked ? 2 : 0,
        }}
      >
        <Box sx={{ position: 'relative', width: '100%', paddingTop: '100%' }}>
          <Box
            sx={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              padding: 0.5,
              filter: checked ? 'brightness(60%)' : undefined,
            }}
          >
            <img
              src={url}
              loading="lazy"
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'cover',
              }}
            />
          </Box>
        </Box>
      </StyledStack>
    );
  }
);

MessageAssistant.displayName = 'MessageAssistant';

export default MessageAssistant;
