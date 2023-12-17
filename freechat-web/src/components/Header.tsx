import { Box, Sheet } from "@mui/joy";
import { AccountMenu, FreeChatLogo, LanguageSelect, ColorSchemeToggle, SidebarToggle } from ".";

export default function Header() {
  return (
    <Sheet
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        top: 0,
        width: '100vw',
        height: 'var(--Header-height)',
        zIndex: 10000,
        p: 2,
        gap: 1,
        borderBottom: '1px solid',
        borderColor: 'background.level2',
        boxShadow: 'sx',
      }}
    >
      <Box sx={{
        display: 'flex',
        justifyContent: 'flex-start',
        flexWrap: 'nowrap',
      }}>
        <SidebarToggle />
        <FreeChatLogo />
      </Box>
      <Box sx={{
        justifyContent: 'flex-end',
        display: 'flex'
      }}>
        <LanguageSelect />
        <ColorSchemeToggle />
        <AccountMenu />
      </Box>
    </Sheet>
  );
}