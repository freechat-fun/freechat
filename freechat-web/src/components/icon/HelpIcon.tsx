import { forwardRef } from "react";
import { HelpOutlineRounded } from "@mui/icons-material";
import { SvgIconProps } from "@mui/material";

const HelpIcon = forwardRef<SVGSVGElement, SvgIconProps>((props, ref) => {
  return (
    <HelpOutlineRounded {...props} ref={ref} fontSize="small" />
  );
});

export default HelpIcon;