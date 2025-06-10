import { Fragment, forwardRef, useEffect, useState } from 'react';
import {
  ListItem,
  ListItemButton,
  ListItemButtonProps,
  Typography,
  Divider,
  styled,
  Box,
  Stack,
} from '@mui/material';
import { CharacterSummaryDTO } from 'freechat-sdk';
import { getSenderName } from '../../libs/chat_utils';
import { SxProps } from '@mui/material/styles';

type CharacterRecommendationListItemProps = ListItemButtonProps & {
  record: CharacterSummaryDTO;
  selectedId: number;
  setSelectedId: (id?: number) => void;
  sx?: SxProps;
};

const StyledCard = styled(Stack)(({ theme }) => ({
  width: '100%',
  transition: 'transform 0.4s, box-shadow 0.4s',
  borderRadius: theme.spacing(0.5),
  boxShadow: theme.shadows[1],
  '&:hover, &:focus-within': {
    boxShadow: theme.shadows[4],
    transform: 'translateY(-1px)',
  },
  position: 'relative',
  overflow: 'hidden',
  backgroundSize: 'cover',
  backgroundRepeat: 'no-repeat',
  backgroundPosition: 'center',
}));

const StyledCardContent = styled(Box)(() => ({
  position: 'absolute',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  transition: '0.3s ease-in',
  '&:hover, &:focus-within': {
    opacity: 1,
  },
}));

const CharacterRecommendationListItem = forwardRef<
  HTMLLIElement,
  CharacterRecommendationListItemProps
>((props, ref) => {
  const { record, selectedId, setSelectedId, sx } = props;

  const [selected, setSelected] = useState(selectedId === record.characterId);
  const nickname = getSenderName(record);

  useEffect(() => {
    setSelected(selectedId === record.characterId);
  }, [record, selectedId]);

  return (
    <Fragment>
      <ListItem ref={ref} sx={{ p: 0 }}>
        <ListItemButton
          onClick={() => setSelectedId(record.characterId)}
          selected={selected}
          sx={{
            borderRadius: 3,
            p: 0,
          }}
        >
          <StyledCard
            sx={{
              ...sx,
              backgroundImage: `url(${record.avatar})`,
            }}
          >
            <StyledCardContent
              sx={{
                opacity: selectedId === record.characterId ? 1 : 0,
                background:
                  selectedId === record.characterId
                    ? 'rgba(0 0 0 / 0.5)'
                    : undefined,
                backdropFilter:
                  selectedId === record.characterId ? undefined : 'blur(3px)',
              }}
            >
              <Typography
                variant="h6"
                sx={{
                  color: 'white',
                  transition: 'transform 0.4s, box-shadow 0.4s',
                }}
              >
                {nickname}
              </Typography>
            </StyledCardContent>
          </StyledCard>
        </ListItemButton>
      </ListItem>
      <Divider sx={{ margin: 0 }} />
    </Fragment>
  );
});

export default CharacterRecommendationListItem;
