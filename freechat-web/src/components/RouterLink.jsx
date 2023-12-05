import { useNavigate } from 'react-router-dom';
import { PropTypes } from 'prop-types';
import { Link } from '@mui/joy';

RouterLink.propTypes = {
  href: PropTypes.string,
  onClick: PropTypes.func,
};

export default function RouterLink(props) {
  const navigate = useNavigate();
  
  if (props && (!props.href || props.onClick)) {
    return (<Link {...props} />);
  }
  
  return (
    <Link onClick={(ev) => {
      ev.preventDefault();
      navigate(props.href);
    }} {...props} />
  );
}
