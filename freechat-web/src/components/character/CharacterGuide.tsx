import { Card, Typography } from "@mui/joy";

export default function CharacterGuide() {
  return (
    <Card sx={{
      width: { xs: '100%', sm: '16rem' },
      my: 2,
      mx: { xs: 0, sm: 2 },
      p: 2,
      boxShadow: 'sm',
    }}>
      <Typography>How to create a character?</Typography>
    </Card>
  )
}