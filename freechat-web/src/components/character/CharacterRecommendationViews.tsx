import { useEffect, useRef, useState } from "react";
import { Box, Stack, StackProps, Step, StepButton, StepIndicator, Stepper } from "@mui/joy";
import CharacterRecommendationPane from "./CharacterRecommendationPane";

type CharacterRecommendationViewsProps = StackProps & {
  interval?: number
};

export default function CharacterRecommendationViews({
  interval = 5000,
  sx,
  ...others
}: CharacterRecommendationViewsProps) {
  const [activeIndex, setActiveIndex] = useState(0);
  const intervalRef = useRef<number | null>(null);

  const languages = ['en', 'zh'];

  useEffect(() => {
    intervalRef.current = setInterval(() => setActiveIndex(prevIndex => (prevIndex + 1) % languages.length), interval);

    // Clear the interval when the component unmounts
    return () => {
      intervalRef.current && clearInterval(intervalRef.current);
      intervalRef.current = null;
    };
  }, [interval, languages.length]);

  function handleMouseEnter(): void {
    intervalRef.current && clearInterval(intervalRef.current);
    intervalRef.current = null;
  }

  function handleMouseLeave(): void {
    intervalRef.current = setInterval(() => setActiveIndex(prevIndex => (prevIndex + 1) % languages.length), interval);
  }

  return (
    <Stack sx={{
      mx: 'auto',
      borderRadius: 'md',
      p: 0,
      display: 'flex',
      alignItems: 'stretch',
      ...sx
      }}
      {...others}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
    >
      <Box sx={{ width: '100%', overflow: 'hidden' }}>
        <Box
            sx={{
                display: 'flex',
                transition: 'transform 0.5s ease-out',
                transform: `translateX(-${activeIndex * 100}%)`,
            }}
        >
          {languages.map((lang) => (
            <Box
              key={lang}
              sx={{ width: '100%', flexShrink: 0 }}
            >
              <CharacterRecommendationPane lang={lang} />
            </Box>
          ))}
        </Box>
      </Box>

      <Stepper sx={{ mx: 'auto' }}>
        {languages.map((lang, index) => (
          <Step
            key={lang}
            indicator={
              <StepIndicator
                variant={ activeIndex === index ? 'solid' : 'soft' }
                color="primary"
              />
            }
          >
            <StepButton onClick={() => setActiveIndex(index)} />
          </Step>
        ))}
      </Stepper>
    </Stack>
  );
}