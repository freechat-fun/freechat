import { forwardRef } from "react";
import { Typography, TypographyProps } from "@mui/joy";


const ScrollableTypography = forwardRef<HTMLDivElement, TypographyProps>((props, ref) => {
  const { sx, children, ...others } = props;

  return (
    <Typography
      ref={ref}
      variant="outlined"
      component="div"
      sx={{
        maxHeight: 'calc(1.6em * 10)',
        overflowY: 'auto',
        padding: 2,
        width: '100%',
        boxSizing: 'border-box' ,
        whiteSpace: 'pre-wrap',
        ...sx
      }}
      {...others}
    >
      {children}
    </Typography>
  );
});

export default ScrollableTypography;