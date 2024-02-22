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
        pl: {xs: '80px', sm: '120px'},
        pr: {xs: '0px', sm: '120px'},
      }}>
        <Outlet />
      </Box>
    </>
  );
}