import { ReactNode } from "react";
import { useTranslation } from "react-i18next";
import { ModalProps, Modal, ModalDialog, ModalClose, DialogTitle, DialogContent, Stack, DialogActions, Button } from "@mui/joy";

export default function ConfirmModal(props: ModalProps & {
  obj: string | number,
  button?: {
    text?: string,
    color?: 'danger' | 'neutral' | 'primary' | 'success' | 'warning',
  }
  onConfirm: (obj: string | number) => void,
  children: ReactNode
}) {
  const { obj, button, onConfirm, children, ...modalProps } = props;
  const { t } = useTranslation(['button']);

  return (
    <Modal {...modalProps}>
      <ModalDialog>
        <ModalClose />
        <DialogTitle color="warning">{t('Please confirm carefully!')}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center'
          }}>
            {children}
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button color={button?.color} onClick={() => onConfirm(obj)}>{button?.text || t('button:Confirm')}</Button>
        </DialogActions>
      </ModalDialog>
    </Modal>
  );
}
