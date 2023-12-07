import { useNavigate } from 'react-router-dom';
import { Link, LinkProps } from '@mui/joy';

export default function RouterLink(props: LinkProps) {
  const navigate = useNavigate();
  
  if (props && (!props.href || props.onClick)) {
    return (<Link {...props} />);
  }
  
  return (
    <Link onClick={() => {
      if (props.href) {
        navigate(props.href);
      }
    }} {...props} />
  );
}
