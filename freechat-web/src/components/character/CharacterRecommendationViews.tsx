import { useEffect, useRef, useState } from 'react';
import {
  Box,
  Stack,
  StackProps,
  Stepper,
  Step,
  StepLabel,
} from '@mui/material';
import CharacterRecommendationPane from './CharacterRecommendationPane';
import { CircleRounded } from '@mui/icons-material';

type CharacterRecommendationViewsProps = StackProps & {
  interval?: number;
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
    intervalRef.current = setInterval(
      () => setActiveIndex((prevIndex) => (prevIndex + 1) % languages.length),
      interval
    );

    // Clear the interval when the component unmounts
    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
      intervalRef.current = null;
    };
  }, [interval, languages.length]);

  function handleMouseEnter(): void {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
    }
    intervalRef.current = null;
  }

  function handleMouseLeave(): void {
    intervalRef.current = setInterval(
      () => setActiveIndex((prevIndex) => (prevIndex + 1) % languages.length),
      interval
    );
  }

  return (
    <Stack
      sx={{
        mx: 'auto',
        borderRadius: 1,
        p: 0,
        display: 'flex',
        alignItems: 'stretch',
        ...sx,
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
            <Box key={lang} sx={{ width: '100%', flexShrink: 0 }}>
              <CharacterRecommendationPane lang={lang} />
            </Box>
          ))}
        </Box>
      </Box>

      <Stepper
        activeStep={activeIndex}
        sx={{
          mx: 'auto',
          '& .MuiStepLabel-root': {
            cursor: 'pointer',
          },
        }}
      >
        {languages.map((lang, index) => (
          <Step key={lang} onClick={() => setActiveIndex(index)}>
            <StepLabel
              slots={{
                stepIcon: () => (
                  <CircleRounded
                    color={index === activeIndex ? 'primary' : 'disabled'}
                    sx={{ fontSize: '2rem' }}
                  />
                ),
              }}
            />
          </Step>
        ))}
      </Stepper>
    </Stack>
  );
}
