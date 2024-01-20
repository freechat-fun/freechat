import { SvgIcon } from '@mui/joy';
import { useColorScheme } from "@mui/joy";

export default function ZhIcon() {
  const { mode } = useColorScheme();

  return (
    <SvgIcon viewBox="0 0 1024 1024">
      <path
        d="M160 144a32 32 0 0 0-32 32V864a32 32 0 0 0 32 32h688a32 32 0 0 0 32-32V176a32 32 0 0 0-32-32H160z m0-64h688a96 96 0 0 1 96 96V864a96 96 0 0 1-96 96H160a96 96 0 0 1-96-96V176a96 96 0 0 1 96-96z"
        fill={mode === 'dark' ? '#e0e0e0' : '#404040'}
      />
      <path
        d="M482.176 262.272h59.616v94.4h196v239.072h-196v184.416h-59.616v-184.416H286.72v-239.04h195.456V262.24z m-137.504 277.152h137.504v-126.4H344.64v126.4z m197.12 0h138.048v-126.4H541.76v126.4z"
        fill={mode === 'dark' ? '#e0e0e0' : '#404040'}
      />
    </SvgIcon>
  );
}