import { Box } from "@mui/joy";

export default function LinePlaceholder(props: {
  spacing?: number
}) {
  const { spacing } = props;
  return (
    <Box sx={{
      mt: spacing || 3
    }}/>
  );
}