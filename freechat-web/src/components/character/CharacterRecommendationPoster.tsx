import { Fragment, forwardRef } from "react";
import { Box, Chip, Divider, Stack, Typography } from "@mui/joy";
import { CharacterSummaryDTO } from "freechat-sdk";
import { CommonBox, CommonGridBox } from "..";
import { getSenderName } from "../../libs/chat_utils";

type CharacterRecommendationPosterProps = {
  record?: CharacterSummaryDTO,
}

const CharacterRecommendationPoster = forwardRef<HTMLDivElement, CharacterRecommendationPosterProps>((props, ref) => {
  const { record } = props;

  const nickname = getSenderName(record);

  return (
    <Fragment>
      <CommonGridBox ref={ref} sx={{
        p: 2,
        alignItems: 'flex-start',
        gridTemplateColumns: '1fr auto 1fr',
        position: 'relative',
        overflow: 'hidden',
        backgroundImage: `url(${record?.picture})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
      }}>
        <Stack sx={{ gap: 1 }}>
          <Box sx={{
            borderRadius: 6,
            bgcolor: 'transparent',
            backdropFilter: 'blur(10px)',
            alignSelf: 'flex-start',
            px: 1,
          }}>
            <Typography level="h2" sx={{ color: 'white' }}>
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

          <Box
            sx={{
              mt: 'auto',
              display: record?.greeting ? 'flex' : 'none',
              p: 1.25,
              borderRadius: 'lg',
              borderTopRightRadius: 'lg',
              borderTopLeftRadius: 0,
              bgcolor: 'transparent',
              backdropFilter: 'blur(10px)',
            }}
          >
            <Typography level="body-md" sx={{ color: 'white' }}>
              {record?.greeting}
            </Typography>
          </Box>
          
        </Stack>

        <Divider orientation="vertical" sx={{
          mx: 'auto',
          '--Divider-lineColor': 'white',
        }} />

        <Stack sx={{
          height: '100%',
          justifyContent: 'center',
        }}>
          <Box sx={{
            borderRadius: 6,
            background: 'rgba(0 0 0 / 0.2)',
            backdropFilter: 'blur(10px)',
            p: 1,
          }}>
            <Typography level="body-md" sx={{
              color: 'white',
              transition: 'transform 0.4s, box-shadow 0.4s',
            }}>
              {record?.description}
            </Typography>
          </Box>
        </Stack>

      </CommonGridBox>
    </Fragment>
  );
});

export default CharacterRecommendationPoster;