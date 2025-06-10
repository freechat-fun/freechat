/* eslint-disable prettier/prettier */
import { Fragment, forwardRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  BoxProps,
  Button,
  Chip,
  Stack,
  Typography,
  styled,
} from '@mui/material';
import { CharacterSummaryDTO, ChatCreateDTO } from 'freechat-sdk';
import {
  CommonBox,
  CommonGridBox,
  LinePlaceholder,
  SummaryTypography,
} from '..';
import { getSenderName } from '../../libs/chat_utils';
import {
  useErrorMessageBusContext,
  useFreeChatApiContext,
  useMetaInfoContext,
} from '../../contexts';

type CharacterRecommendationPosterProps = BoxProps & {
  record?: CharacterSummaryDTO;
  maxDescriptionLines?: number;
  disabled?: boolean;
};

const StyledCommonGridBox = styled(CommonGridBox)(({ theme }) => ({
  display: 'grid',
  padding: theme.spacing(4),
  alignItems: 'flex-start',
  gridTemplateColumns: '70% 30%',
  position: 'relative',
  overflow: 'hidden',
  borderRadius: theme.spacing(0.5),
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat',
}));

const StyledButton = styled(Button)(({ theme }) => ({
  marginTop: 'auto',
  marginLeft: theme.spacing(1),
  display: 'flex',
  padding: theme.spacing(1.25, 2),
  border: '1px solid white',
  borderRadius: theme.shape.borderRadius,
  backdropFilter: 'blur(3px)',
}));

const CharacterRecommendationPoster = forwardRef<
  HTMLDivElement,
  CharacterRecommendationPosterProps
>((props, ref) => {
  const {
    record,
    maxDescriptionLines = 3,
    disabled = false,
    sx,
    ...others
  } = props;
  const navigate = useNavigate();
  const { isAuthorized } = useMetaInfoContext();
  const { accountApi, chatApi, interactiveStatisticsApi } =
    useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [isHovered, setIsHovered] = useState(false);

  const nickname = getSenderName(record);

  function handleView(record?: CharacterSummaryDTO): void {
    if (!isAuthorized()) {
      navigate('/w/login');
      return;
    } else if (!record || !record.characterUid || !record.characterId) {
      return;
    }

    interactiveStatisticsApi
      ?.increaseStatistic('character', record.characterUid, 'view_count')
      .finally(() => {
        chatApi
          ?.getDefaultChatId(record.characterUid as string)
          .then((resp) => {
            navigate(`/w/chat/${resp}`);
          })
          .catch(() => {
            accountApi
              ?.getUserDetails()
              .then((userDetails) => {
                const request = new ChatCreateDTO();
                request.userNickname =
                  userDetails.nickname ?? userDetails.username;
                request.userProfile = userDetails.profile;
                request.characterNickname = record.nickname ?? record.name;
                request.characterUid = record.characterUid as string;
                request.about = record.defaultScene;

                chatApi
                  .startChat(request)
                  .then((chatId) => {
                    navigate(`/w/chat/${chatId}`);
                  })
                  .catch(handleError);
              })
              .catch(handleError);
          });
      });
  }

  return (
    <Fragment>
      <StyledCommonGridBox
        ref={ref}
        sx={{
          display: disabled ? 'none' : 'grid',
          backgroundImage: `url(${record?.picture})`,
          ...sx,
        }}
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
        {...others}
      >
        <Stack
          sx={{
            gap: 1,
            alignItems: 'flex-start',
            height: '100%',
          }}
        >
          <Box
            sx={{
              borderRadius: 3,
              bgcolor: 'transparent',
              backdropFilter: 'blur(3px)',
              alignSelf: 'flex-start',
              px: 1,
              mr: 'auto',
            }}
          >
            <Typography
              variant="h3"
              sx={{
                color: 'white',
                '-webkit-text-stroke': '1px gray',
                textShadow: `
                -1px -1px 0 gray,
                1px -1px 0 gray,
                -1px  1px 0 gray,
                1px  1px 0 gray
              `,
              }}
            >
              {nickname}
            </Typography>
          </Box>

          <CommonBox>
            {record?.tags &&
              record.tags.length > 0 &&
              record.tags.map((tag, index) => (
                <Chip
                  variant="filled"
                  color="secondary"
                  key={`tag-${tag}-${index}`}
                  label={tag}
                  size="small"
                  sx={{
                    opacity: 0.8,
                    fontSize: '1rem',
                  }}
                />
              ))}
          </CommonBox>

          <LinePlaceholder />

          <Stack sx={{ justifyContent: 'center' }}>
            <Box
              sx={{
                overflow: 'hidden',
                borderRadius: 3,
                background: 'rgba(0 0 0 / 0.4)',
                backdropFilter: 'blur(10px)',
                p: 1,
              }}
            >
              <SummaryTypography
                variant="body1"
                sx={{
                  color: 'white',
                  transition: 'transform 0.4s, box-shadow 0.4s',
                  maxHeight: '100%',
                  minHeight: '1.5rem',
                  WebkitLineClamp: maxDescriptionLines,
                }}
              >
                {record?.description}
              </SummaryTypography>
            </Box>
          </Stack>

          <StyledButton
            sx={{
              borderTopLeftRadius:
                record?.greeting && !isHovered ? 0 : '16px',
              borderTopRightRadius: '16px',
              borderBottomLeftRadius:
                record?.greeting && !isHovered ? '16px' : 0,
              borderBottomRightRadius: '16px',
              background:
                record?.greeting && !isHovered ? '#000000C0' : '#0B6BCBC0',
            }}
            onClick={() => handleView(record)}
          >
            <Typography
              variant="body1"
              sx={{ color: 'white', whiteSpace: 'pre-wrap' }}
            >
              {isHovered
                ? record?.lang === 'zh'
                  ? '聊一聊'
                  : 'have a chat'
                : record?.greeting}
            </Typography>
          </StyledButton>
        </Stack>
      </StyledCommonGridBox>
    </Fragment>
  );
});

export default CharacterRecommendationPoster;
