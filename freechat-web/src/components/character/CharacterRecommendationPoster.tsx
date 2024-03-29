import { Fragment, forwardRef } from "react";
import { Box, BoxProps, Chip, Divider, Stack, Typography } from "@mui/joy";
import { CharacterSummaryDTO } from "freechat-sdk";
import { CommonBox, CommonGridBox, SummaryTypography } from "..";
import { getSenderName } from "../../libs/chat_utils";

type CharacterRecommendationPosterProps = BoxProps & {
  record?: CharacterSummaryDTO,
  maxDescriptionLines?: number,
  disabled?: boolean;
}

const CharacterRecommendationPoster = forwardRef<HTMLDivElement, CharacterRecommendationPosterProps>((props, ref) => {
  const { record, maxDescriptionLines = 3, disabled = false, sx, ...others } = props;

  const nickname = getSenderName(record);

  return (
    <Fragment>
      <CommonGridBox ref={ref} sx={{
        display: disabled ? 'none' : 'grid',
        p: 2,
        alignItems: 'flex-start',
        gridTemplateColumns: '1fr auto 1fr',
        position: 'relative',
        overflow: 'hidden',
        borderRadius: 6,
        backgroundImage: `url(${record?.picture})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundRepeat: 'no-repeat',
        ...sx,
        }}
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
              py: 1.25,
              px: 2,
              border: 1,
              borderRadius: 'lg',
              borderTopRightRadius: 'lg',
              borderTopLeftRadius: 0,
              borderColor: 'white',
              background: 'rgba(0 0 0 / 0.2)',
              backdropFilter: 'blur(3px)',
            }}
          >
            <Typography level="body-md" sx={{ color: 'white', whiteSpace: 'pre-wrap' }}>
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
            overflow: 'hidden',
            borderRadius: 6,
            background: 'rgba(0 0 0 / 0.3)',
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

      </CommonGridBox>
    </Fragment>
  );
});

export default CharacterRecommendationPoster;