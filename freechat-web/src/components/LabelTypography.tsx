import { forwardRef } from "react";
import { Typography, TypographyProps } from "@mui/joy";

const LabelTypography = forwardRef<HTMLDivElement, TypographyProps>((props, ref) => {
  const { sx, children, ...others } = props;
  return (
    <Typography
      ref={ref}
      level="title-md"
      textColor="neutral"
      sx={{
        mr: 2,
        ...sx
      }}
      {...others}
    >
      {children}
    </Typography>
  );
});

export default LabelTypography;
