/* eslint-disable @typescript-eslint/no-explicit-any */
import { ReactNode } from "react";
import { useTranslation } from "react-i18next";
import { ModalProps, Modal, ModalDialog, ModalClose, DialogTitle, DialogContent, Stack, DialogActions, Button } from "@mui/joy";

export default function ConfirmModal(props: ModalProps & {
  obj?: any,
  dialog?: {
    title?: string,
    color?: 'danger' | 'neutral' | 'primary' | 'success' | 'warning',
  },
  button?: {
    text?: string,
    color?: 'danger' | 'neutral' | 'primary' | 'success' | 'warning',
    startDecorator?: ReactNode,
  },
  onConfirm?: (obj: any) => void,
  children: ReactNode
}) {
  const { obj, dialog, button, onConfirm, children, ...modalProps } = props;
  const { t } = useTranslation(['button']);

  return (
    <Modal {...modalProps}>
      <ModalDialog color={dialog?.color}>
        <ModalClose />
        <DialogTitle sx={{
          pr: 4,
          alignItems: 'center',
        }}>{dialog?.title}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{
            p: 2,
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center'
          }}>
            {children}
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button
            color={button?.color}
            onClick={() => onConfirm && onConfirm(obj)}
            startDecorator={button?.startDecorator}
          >
            {button?.text || t('button:Confirm')}
          </Button>
        </DialogActions>
      </ModalDialog>
    </Modal>
  );
}
