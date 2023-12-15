import { Box } from "@mui/joy";
import {  Sidebar } from "../components";
import { Outlet } from "react-router-dom";

export default function Console() {
  return (
    <Box sx={{
      display: 'flex',
    }}>
      <Sidebar />
      <Box
          component="main"
          className="MainContent"
          sx={{
            pb: { xs: 2, sm: 2, md: 3 },
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            minWidth: 0,
            gap: 1,
            overflow: 'auto',
          }}
        >
          <Outlet />
        </Box>
    </Box>
  );
}