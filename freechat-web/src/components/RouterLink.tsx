import { forwardRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Link, LinkProps } from '@mui/material';

const RouterLink = forwardRef<HTMLAnchorElement, LinkProps>((props, ref) => {
  const navigate = useNavigate();

  if (props && (!props.href || props.onClick)) {
    return <Link {...props} />;
  }

  return (
    <Link
      ref={ref}
      onClick={(event) => {
        event.preventDefault();
        if (props.href) {
          navigate(props.href);
        }
      }}
      {...props}
    />
  );
});

export default RouterLink;
