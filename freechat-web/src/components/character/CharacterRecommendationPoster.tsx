import { Fragment, forwardRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, BoxProps, Button, Chip, Stack, Typography } from "@mui/joy";
import { CharacterSummaryDTO, ChatCreateDTO } from "freechat-sdk";
import { CommonBox, CommonGridBox, LinePlaceholder, SummaryTypography } from "..";
import { getSenderName } from "../../libs/chat_utils";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";

type CharacterRecommendationPosterProps = BoxProps & {
  record?: CharacterSummaryDTO,
  maxDescriptionLines?: number,
  disabled?: boolean;
}

const CharacterRecommendationPoster = forwardRef<HTMLDivElement, CharacterRecommendationPosterProps>((props, ref) => {
  const { record, maxDescriptionLines = 3, disabled = false, sx, ...others } = props;
  const navigate = useNavigate();
  const { accountApi, chatApi, interactiveStatisticsApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  
  const [isHovered, setIsHovered] = useState(false);

  const nickname = getSenderName(record);

  function handleView(record?: CharacterSummaryDTO): void {
    if (!record || !record.characterUid || !record.characterId) {
      return;
    }
    interactiveStatisticsApi?.increaseStatistic('character', record.characterUid, 'view_count')
      .finally(() => {
        chatApi?.getDefaultChatId(record.characterId as number)
          .then(resp => {
            navigate(`/w/chat/${resp}`);
          })
          .catch(() => {
            accountApi?.getUserDetails()
              .then(userDetails => {
                const request = new ChatCreateDTO();
                request.userNickname = userDetails.nickname ?? userDetails.username;
                request.userProfile = userDetails.profile;
                request.characterNickname = record.nickname ?? record.name;
                request.characterId = record.characterId as number;
                request.about = record.defaultScene;

                chatApi.startChat(request)
                  .then(chatId => {
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
      <CommonGridBox ref={ref} sx={{
        display: disabled ? 'none' : 'grid',
        p: 4,
        alignItems: 'flex-start',
        gridTemplateColumns: '70% 30%',
        position: 'relative',
        overflow: 'hidden',
        borderRadius: 6,
        backgroundImage: `url(${record?.picture})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        ...sx,
        }}
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
        {...others}
      >
        <Stack sx={{
          gap: 1,
          alignItems: 'flex-start',
          height: '100%',
        }}>
          <Box sx={{
            borderRadius: 6,
            bgcolor: 'transparent',
            backdropFilter: 'blur(3px)',
            alignSelf: 'flex-start',
            px: 1,
            mr: 'auto',
          }}>
            <Typography level="h2" sx={{
              color: 'white',
              '-webkit-text-stroke': '1px gray',
              textShadow: `
                -1px -1px 0 gray,
                1px -1px 0 gray,
                -1px  1px 0 gray,
                1px  1px 0 gray
              `,
            }}>
              {nickname}
            </Typography>
          </Box>

          <CommonBox>
            {record?.tags && record.tags.length > 0 && record.tags.map((tag, index) => (
              <Chip
                variant="outlined"
                color="success"
                key={`tag-${tag}-${index}`}
              >
                {tag}
              </Chip>
            ))}
          </CommonBox>

          <LinePlaceholder />

          <Stack sx={{
            justifyContent: 'center',
          }}>
            <Box sx={{
              overflow: 'hidden',
              borderRadius: 6,
              background: 'rgba(0 0 0 / 0.4)',
              backdropFilter: 'blur(10px)',
              p: 1,
            }}>
              <SummaryTypography level="body-md" sx={{
                color: 'white',
                transition: 'transform 0.4s, box-shadow 0.4s',
                maxHeight: '100%',
                minHeight: '1.5rem',
                WebkitLineClamp: maxDescriptionLines,
              }}>
                {record?.description}
              </SummaryTypography>
            </Box>
          </Stack>

          <Button
            sx={{
              mt: 'auto',
              ml: 1,
              display: 'flex',
              py: 1.25,
              px: 2,
              border: 1,
              borderRadius: 'lg',
              borderTopLeftRadius: (record?.greeting && !isHovered) ? 0 : 'lg',
              borderTopRightRadius: 'lg',
              borderBottomLeftRadius: (record?.greeting && !isHovered) ? 'lg' : 0,
              borderBottomRightRadius: 'lg',
              borderColor: 'white',
              background: (record?.greeting && !isHovered) ? '#000000C0' : '#0B6BCBC0',
              backdropFilter: 'blur(3px)',
            }}
            onClick={() => handleView(record)}
          >
            <Typography level="body-md" sx={{ color: 'white', whiteSpace: 'pre-wrap' }}>
              {isHovered? (record?.lang === 'zh' ? '聊一聊' : 'have a chat') : record?.greeting}
            </Typography>
          </Button>
          
        </Stack>

        {/* <Divider orientation="vertical" sx={{
          mx: 'auto',
          '--Divider-lineColor': 'white',
        }} /> */}
      </CommonGridBox>
    </Fragment>
  );
});

export default CharacterRecommendationPoster;