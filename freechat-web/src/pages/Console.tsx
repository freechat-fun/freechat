import { Box } from "@mui/joy";
import { Outlet } from "react-router-dom";
import { ThinSidebar } from "../components";

export default function Console() {
  return (
    <Box sx={{
      display: 'flex',
    }}>
      <ThinSidebar />
      <Box sx={{
        display: 'flex',
        flexDirection: 'column',
        minWidth: 0,
        gap: 1,
        overflow: 'auto',
        px: {
          xs: 2,
          md: '120px',
        }
      }}>
        <Outlet />
      </Box>
    </Box>
  );
}