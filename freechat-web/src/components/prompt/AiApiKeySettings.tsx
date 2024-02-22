import { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { FormLabel, Select, Option, Typography, Input, FormControl, IconButton, SelectStaticProps, Stack } from "@mui/joy";
import { CheckRounded, CloseRounded, KeyRounded } from "@mui/icons-material";
import { ConfirmModal } from "../../components"

type AiApiKeySettingsProps = {
  defaultKeyName: string | undefined,
  defaultKeyValue: string | undefined,
  keyNames: (string | undefined)[] | undefined,
  open: boolean,
  onClose: () => void,
  onConfirm: (keyName: string | undefined, keyValue: string | undefined) => void;
};

export default function AiApiKeySettings(props: AiApiKeySettingsProps) {
  const { defaultKeyName, defaultKeyValue, keyNames, open, onClose, onConfirm } = props;
  const { t } = useTranslation('prompt');

  const [apiKeyName, setApiKeyName] = useState(defaultKeyName ?? '');
  const [apiKeyValue, setApiKeyValue] = useState(defaultKeyValue ?? '');

  const action: SelectStaticProps['action'] = useRef(null);

  useEffect(() => {
    setApiKeyName(defaultKeyName ?? '');
  }, [defaultKeyName]);

  useEffect(() => {
    setApiKeyValue(defaultKeyValue ?? '');
  }, [defaultKeyValue]);

  function handleClose(_event: React.MouseEvent<HTMLButtonElement>, reason: string): void {
    if (reason === 'backdropClick') {
      return;
    }
    onClose();
  }

  function handleConfirm(): void {
    onConfirm(apiKeyName, apiKeyValue);
  }

  function handleSelectChange(_event: React.SyntheticEvent | null, newValue: string | null): void {
    if (newValue !== apiKeyName && newValue !== 'No API Key') {
      setApiKeyName(newValue ?? '');
    }
  }

  function handleValueChange(event: React.ChangeEvent<HTMLInputElement>): void {
    if (event.target.value !== apiKeyValue) {
      setApiKeyValue(event.target.value);
    }
  }

  return (
    <ConfirmModal
        open={open}
        onClose={handleClose}
        dialog={{
          title: t('Set API Key'),
        }}
        button={{
          text: t('button:Confirm'),
          startDecorator: <CheckRounded />
        }}
        onConfirm={handleConfirm}
      >
        <Stack spacing={2}>
          <FormControl>
            <FormLabel>{t('Select a key')}</FormLabel>
            <Select
              action={action}
              name="apiKeyName"
              placeholder={<Typography textColor="gray">No API Key</Typography>}
              value={apiKeyName || 'No API Key'}
              onChange={handleSelectChange}
              sx={{
                flex: 1,
              }}
              {...(apiKeyName && {
                // display the button and remove select indicator
                // when user has selected a value
                endDecorator: (
                  <IconButton
                    size="sm"
                    variant="plain"
                    color="neutral"
                    onMouseDown={(event) => {
                      // don't open the popup when clicking on this button
                      event.stopPropagation();
                    }}
                    onClick={() => {
                      setApiKeyName('');
                      action.current?.focusVisible();
                    }}
                  >
                    <CloseRounded fontSize="small"/>
                  </IconButton>
                ),
                indicator: null,
              })}
            >
              {keyNames && keyNames.length > 0 ? keyNames?.map(keyName => keyName && (
                <Option value={keyName} key={`option-${keyName}`}>{keyName}</Option>
              )) : (
                <Option value="No API Key" key='option-unknown'>--No API Key--</Option>
              )}
            </Select>
          </FormControl>
          <FormControl>
            <FormLabel>{t('or you can use a temporary key')}</FormLabel>
            <Input
              type="password"
              placeholder="Paste a key here..."
              startDecorator={<KeyRounded />}
              value={apiKeyValue}
              onChange={handleValueChange}
              sx={{
                minWidth: '20rem',
              }}
            />
          </FormControl>
        </Stack>
      </ConfirmModal>
  );
}