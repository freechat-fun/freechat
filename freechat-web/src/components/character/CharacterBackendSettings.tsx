import { DialogContent, DialogTitle, Divider, IconButton, Slider, Switch, Typography } from "@mui/joy";
import { CharacterBackendDetailsDTO } from "freechat-sdk"
import { CommonContainer, OptionCard, OptionTooltip, Sidedrawer, TinyInput } from "..";
import { t } from "i18next";
import { HelpIcon } from "../icon";
import { createRef, useRef, useState } from "react";
import { ArrowOutwardRounded, TuneRounded } from "@mui/icons-material";

interface CharacterBackendSettingsProps {
  open: boolean;
  backend?: CharacterBackendDetailsDTO;
  onClose?: (backend: CharacterBackendDetailsDTO, redirectToChatPrompt: boolean) => void;
}

export default function CharacterBackendSettings({
  open,
  backend,
  onClose,
}: CharacterBackendSettingsProps) {
  const [messageWindowSize, setMessageWindowSize] = useState(backend?.messageWindowSize ?? 50);

  const inputRefs = useRef(Array(1).fill(createRef<HTMLInputElement | null>()));

  function handleClose(redirectToChatPrompt: boolean = false) {
    let editBackend;
    if (backend) {
      editBackend = {...backend, messageWindowSize};
    } else {
      editBackend = new CharacterBackendDetailsDTO();
      editBackend.messageWindowSize = messageWindowSize;
    }
    onClose?.(editBackend, redirectToChatPrompt);
  }
  
  return (
    <Sidedrawer open={open} onClose={() => handleClose(false)}>
      <DialogTitle>{t('Character backend parameters')}</DialogTitle>
      <Divider sx={{ mt: 'auto' }} />

      <DialogContent>
        <OptionCard>
          <CommonContainer>
            <Typography>{t('message window size')}</Typography>
            <OptionTooltip title="Set the maximum number of historical messages sent to the model.">
              <HelpIcon />
            </OptionTooltip>
            <CommonContainer sx={{ ml: 'auto' }}>
              <TinyInput
                type="number"
                slotProps={{
                  input: {
                    ref: inputRefs.current[0],
                    min: 10,
                    max: 200,
                    step: 1,
                  },
                }}
                value={messageWindowSize}
                onChange={(event => setMessageWindowSize(+event.target.value))}
              />
            </CommonContainer>
          </CommonContainer>
          <Slider
            value={messageWindowSize}
            step={1}
            min={10}
            max={200}
            valueLabelDisplay="auto"
            onChange={(_event: Event, newValue: number | number[]) => setMessageWindowSize(newValue as number)}
          />
       </OptionCard>

       <OptionCard>
          <CommonContainer>
            <Typography>{t('prompt & model')}</Typography>
            <OptionTooltip title="Adjust prompt words and model parameters.">
              <IconButton onClick={() => handleClose(true)}>
                <ArrowOutwardRounded />
              </IconButton>
            </OptionTooltip>
          </CommonContainer>
       </OptionCard>

       <OptionCard>
          <CommonContainer>
            <Typography>{t('moderation model settings')}</Typography>
            <OptionTooltip title="Set up the moderation model.">
              <IconButton disabled>
                <TuneRounded />
              </IconButton>
            </OptionTooltip>
            <Switch checked={false} disabled sx={{ml: 'auto'}} />
          </CommonContainer>
       </OptionCard>

      </DialogContent>
    </Sidedrawer>
  );
}