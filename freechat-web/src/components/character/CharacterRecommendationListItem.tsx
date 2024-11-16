import { Fragment, forwardRef, useEffect, useState } from 'react';
import {
  Card,
  CardCover,
  ListDivider,
  ListItem,
  ListItemButton,
  ListItemButtonProps,
  Typography,
} from '@mui/joy';
import { CharacterSummaryDTO } from 'freechat-sdk';
import { getSenderName } from '../../libs/chat_utils';
import { SxProps } from '@mui/joy/styles/types';

type CharacterRecommendationListItemProps = ListItemButtonProps & {
  record: CharacterSummaryDTO;
  selectedId: number;
  setSelectedId: (id?: number) => void;
  sx?: SxProps;
};

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
      <ListItem ref={ref}>
        <ListItemButton
          onClick={() => setSelectedId(record.characterId)}
          selected={selected}
          color="neutral"
          sx={{
            borderRadius: 6,
            p: 0,
          }}
        >
          <Card
            sx={{
              ...sx,
              width: '100%',
              transition: 'transform 0.4s, box-shadow 0.4s',
              borderRadius: 6,
              boxShadow: 'sm',
              '&:hover': {
                boxShadow: 'lg',
                transform: 'translateY(-1px)',
              },
              position: 'relative',
              overflow: 'hidden',
              backgroundImage: `url(${record.avatar})`,
              backgroundSize: 'cover',
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'center',
            }}
          >
            <CardCover
              sx={{
                '&:hover, &:focus-within': {
                  opacity: 1,
                },
                opacity: selectedId === record.characterId ? 1 : 0,
                transition: '0.3s ease-in',
                background:
                  selectedId === record.characterId
                    ? 'rgba(0 0 0 / 0.5)'
                    : undefined,
                backdropFilter:
                  selectedId === record.characterId ? undefined : 'blur(3px)',
              }}
            >
              <Typography
                level="title-lg"
                sx={{
                  color: 'white',
                  transition: 'transform 0.4s, box-shadow 0.4s',
                }}
              >
                {nickname}
              </Typography>
            </CardCover>
          </Card>
        </ListItemButton>
      </ListItem>
      <ListDivider sx={{ margin: 0 }} />
    </Fragment>
  );
});

export default CharacterRecommendationListItem;
