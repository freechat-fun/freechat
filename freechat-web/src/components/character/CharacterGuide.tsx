import { useTranslation } from 'react-i18next';
import {
  FiberManualRecordRounded,
  FiberSmartRecordRounded,
} from '@mui/icons-material';
import {
  Card,
  CardProps,
  Stepper,
  Step,
  StepLabel,
  Typography,
  Box,
} from '@mui/material';

export default function CharacterGuide(props: CardProps) {
  const { t } = useTranslation('character');

  const { sx, ...others } = props;

  return (
    <Card
      sx={{
        margin: 2,
        p: 2,
        boxShadow: 1,
        borderRadius: '6px',
        border: 1,
        borderColor: 'divider',
        ...sx,
      }}
      {...others}
    >
      <Stepper
        orientation="vertical"
        sx={{
          '& .MuiStepConnector-line': {
            minHeight: '2rem',
          },
        }}
      >
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => <FiberSmartRecordRounded color="primary" />,
            }}
          >
            <Typography variant="subtitle1">
              {t('Set character metadata')}
            </Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Description')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Tags')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => <FiberSmartRecordRounded color="primary" />,
            }}
          >
            <Box>
              <Typography variant="subtitle1">
                {t('Set character profile')}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {t('Affects character performance')}
              </Typography>
            </Box>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Style')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Knowledge')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Album')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => <FiberSmartRecordRounded color="primary" />,
            }}
          >
            <Box>
              <Typography variant="subtitle1">
                {t('Set character backend')}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {t('The character can connect to different backends')}
              </Typography>
            </Box>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('History messages')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Long term memory')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Quota limit')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Model parameters')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Select tools')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Moderation model')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Prompt template')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">{t('Preset memory')}</Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => <FiberSmartRecordRounded color="primary" />,
            }}
          >
            <Typography variant="subtitle1">
              {t('Set another backend')}
            </Typography>
          </StepLabel>
        </Step>
        <Step>
          <StepLabel
            slots={{
              stepIcon: () => (
                <FiberManualRecordRounded
                  color="primary"
                  sx={{ ml: 1, fontSize: '0.5rem' }}
                />
              ),
            }}
          >
            <Typography variant="body2">...</Typography>
          </StepLabel>
        </Step>
      </Stepper>
    </Card>
  );
}
