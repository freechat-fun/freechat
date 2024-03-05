import { forwardRef } from "react";
import { IconButton, Typography, TypographyProps } from "@mui/joy";
import { UnfoldMoreRounded } from "@mui/icons-material";

type TruncatedTypographyProps = TypographyProps & {
  onExpand: () => void;
}

const TruncatedTypography = forwardRef<HTMLDivElement, TruncatedTypographyProps>((props, ref) => {
  const { sx, children, onExpand, ...others } = props;

  return (
    <Typography
      ref={ref}
      sx={{
        display: 'flex',
        alignItems: 'center',
        whiteSpace: 'nowrap',
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        width: '100%',
        ...sx
      }}
      {...others}
    >
      {children}
      <IconButton
        aria-label="expand"
        onClick={onExpand}
        size="sm"
        sx={{
          marginLeft: 'auto',
        }}
      >
        <UnfoldMoreRounded />
      </IconButton>
    </Typography>
  );
});

export default TruncatedTypography;
