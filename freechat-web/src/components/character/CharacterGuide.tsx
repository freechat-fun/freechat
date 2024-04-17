import { useTranslation } from "react-i18next";
import { CircleOutlined, CircleRounded } from "@mui/icons-material";
import { Card, CardProps, Step, StepIndicator, Stepper, Typography } from "@mui/joy";

export default function CharacterGuide(props: CardProps) {
  const { t } = useTranslation('character');

  const { sx, ...others } = props;
  
  return (
    <Card sx={{
      margin: 2,
      p: 2,
      boxShadow: 'sm',
      ...sx
    }}
    {...others}
    >
      <Stepper
        orientation="vertical"
        sx={{
          '--Stepper-verticalGap': '2rem',
      }}>
        <Step indicator={
          <StepIndicator color="primary">
            <CircleRounded fontSize="small" />
          </StepIndicator>
        }>
          <Typography level="title-sm">{t('Set character metadata')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Description')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Tags')}</Typography>
        </Step>
        <Step indicator={
          <StepIndicator color="primary">
            <CircleRounded fontSize="small" />
          </StepIndicator>
        }>
          <div>
            <Typography level="title-sm">{t('Set character profile')}</Typography>
            <Typography level="body-xs">{t('Affects character performance')}</Typography>
          </div>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Style')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Knowledge')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Album')}</Typography>
        </Step>
        <Step indicator={
          <StepIndicator color="primary">
            <CircleRounded fontSize="small" />
          </StepIndicator>
        }>
          <div>
            <Typography level="title-sm">{t('Set character backend')}</Typography>
            <Typography level="body-xs">{t('The character can connect to different backends')}</Typography>
          </div>
        </Step>
        <Step>
          <Typography level="body-sm">{t('History messages')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Long term memory')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Quota limit')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Model parameters')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Moderation model')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">{t('Prompt template')}</Typography>
        </Step>
        <Step indicator={
          <StepIndicator color="primary">
            <CircleOutlined fontSize="small" />
          </StepIndicator>
        }>
          <Typography level="title-sm">{t('Set another backend')}</Typography>
        </Step>
        <Step>
          <Typography level="body-sm">...</Typography>
        </Step>
      </Stepper>
    </Card>
  )
}