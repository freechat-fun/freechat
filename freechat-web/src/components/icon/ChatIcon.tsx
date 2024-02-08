import { forwardRef } from "react";
import { SvgIcon, SvgIconProps, useColorScheme } from "@mui/joy";

const ChatIcon = forwardRef<SVGSVGElement, SvgIconProps>((props, ref) => {
  const { mode } = useColorScheme();
  const { fill } = {...props};

  return (
    <SvgIcon ref={ref} viewBox="0 0 976 976" sx={{width: '100%'}} {...props}>
      <path
        d="M512 910.2336c-92.7744 0-178.9952-24.2688-250.88-65.9456l-145.92 47.104c-6.3488 2.048-13.312 1.8432-19.5584-0.7168-14.5408-5.9392-21.504-22.528-15.5648-37.0688l50.7904-123.8016C84.0704 667.136 56.9344 592.384 56.9344 512c0-219.9552 203.776-398.2336 455.0656-398.2336S967.0656 292.0448 967.0656 512 763.392 910.2336 512 910.2336z m0-341.2992c31.4368 0 56.9344-25.4976 56.9344-56.9344 0-31.4368-25.4976-56.9344-56.9344-56.9344s-56.9344 25.4976-56.9344 56.9344 25.4976 56.9344 56.9344 56.9344z m-227.5328 0c31.4368 0 56.9344-25.4976 56.9344-56.9344 0-31.4368-25.4976-56.9344-56.9344-56.9344-31.4368 0-56.9344 25.4976-56.9344 56.9344 0 31.4368 25.4976 56.9344 56.9344 56.9344z m455.0656 0c31.4368 0 56.9344-25.4976 56.9344-56.9344 0-31.4368-25.4976-56.9344-56.9344-56.9344-31.4368 0-56.9344 25.4976-56.9344 56.9344 0.1024 31.4368 25.4976 56.9344 56.9344 56.9344z"
        fill={fill ?? (mode === 'dark' ? '#e0e0e0' : '#404040')}
      />
    </SvgIcon>
  );
});

export default ChatIcon;