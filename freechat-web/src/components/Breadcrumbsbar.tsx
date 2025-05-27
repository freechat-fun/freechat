import {
  ChevronRight as ChevronRightIcon,
  Home as HomeIcon,
} from '@mui/icons-material';
import { Box, Breadcrumbs, Typography } from '@mui/material';
import { RouterLink } from '.';

type BreadcrumbsbarProps = {
  breadcrumbs: { [name: string]: string | undefined };
};

export default function Breadcrumbsbar({ breadcrumbs }: BreadcrumbsbarProps) {
  return (
    <Box sx={{ width: '100%' }}>
      <Breadcrumbs
        aria-label="breadcrumbs"
        separator={<ChevronRightIcon />}
        sx={{ pl: 0 }}
      >
        {Object.entries(breadcrumbs).map(([name, value]) => {
          if (value) {
            return (
              <RouterLink
                key={name}
                underline="hover"
                color="primary"
                sx={{ fontWeight: 500 }}
                href={value}
                aria-label={name}
              >
                {name === 'Home' ? <HomeIcon /> : name}
              </RouterLink>
            );
          } else {
            return (
              <Typography
                key={name}
                color="text.secondary"
                sx={{ fontWeight: 500 }}
              >
                {name}
              </Typography>
            );
          }
        })}
      </Breadcrumbs>
    </Box>
  );
}
