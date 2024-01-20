import { Box } from "@mui/joy";
import { Outlet } from "react-router-dom";
import { ThinSidebar } from "../components";

export default function SidebarFrame() {
  return (
    <>
      <ThinSidebar />
      <Box sx={{
        display: 'flex',
        flexDirection: 'column',
        gap: 1,
        overflow: 'auto',
        pl: '120px',
        pr: {xs: '40px', sm: '120px'},
      }}>
        <Outlet />
      </Box>
    </>
  );
}