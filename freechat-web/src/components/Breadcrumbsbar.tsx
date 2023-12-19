import { ChevronRightRounded, HomeRounded } from "@mui/icons-material";
import { Box, Breadcrumbs, Typography } from "@mui/joy";
import { RouterLink } from ".";

interface BreadcrumbsbarProps {
  breadcrumbs: { [name: string]: string | undefined; };
}

export default function Breadcrumbsbar({ breadcrumbs }: BreadcrumbsbarProps) {
  return (
    <Box sx={{ width: '100%' }}>
      <Breadcrumbs
        size="sm"
        aria-label="breadcrumbs"
        separator={<ChevronRightRounded />}
        sx={{ pl: 0 }}
      >
        {Object.entries(breadcrumbs).map(([name, value]) => {
          if (value) {
            return (
              <RouterLink
                key={name}
                underline="hover"
                color="primary"
                fontWeight={500}
                href={value}
                aria-label={name}
              >
                {name === 'Home' ? <HomeRounded /> : name}
              </RouterLink>
            );
          } else {
            return (
              <Typography
                key={name}
                color="neutral"
                fontWeight={500}
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
