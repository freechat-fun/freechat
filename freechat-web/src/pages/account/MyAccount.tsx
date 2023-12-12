import { Button, Typography } from "@mui/joy";
import { useErrorMessageBusContext } from "../../context";

export default function MyAccount() {
  const { putMessage } = useErrorMessageBusContext();
  return (
    <>
      <Typography>My Account</Typography>
      <Button onClick={() => {
        putMessage({ code: 404, message: 'Test1' });
        putMessage({ code: 403, message: 'Test2' });
      }}>logout</Button>
    </>
  );
}