/* eslint-disable @typescript-eslint/no-explicit-any */
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate, useParams } from "react-router-dom";
import { useErrorMessageBusContext, useFreeChatApiContext, useUserInfoContext } from "../../contexts";
import { Box, Button, ButtonGroup, Chip, Divider, Tooltip, Typography } from "@mui/joy";
import { ArrowBackRounded, ChatBubbleOutlineRounded, ContentCopyRounded, EditRounded, PlayCircleOutlineRounded, PublicOffRounded, PublicRounded, Title } from "@mui/icons-material";
import { CommonContainer, LinePlaceholder } from "../../components";
import { PromptDetailsDTO } from "freechat-sdk";
import { getDateLabel } from "../../libs/date_utils";
import { PromptContent, PromptMeta, PromptRunner } from "../../components/prompt";

export default function PromptInfo() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { t, i18n } = useTranslation(['prompt', 'button']);
  const { promptApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();
  const { username } = useUserInfoContext();

  const [record, setRecord] = useState<PromptDetailsDTO>();
  const [history, setHistory] = useState<string[]>([]);
  const [play, setPlay] = useState(false);
  const [defaultParameters, setDefaultParameters] = useState<{ [key: string]: any }>()
  const [defaultVariables, setDefaultVariables] = useState<{ [key: string]: any }>()
  const [defaultOutputText, setDefaultOutputText] = useState<string>();

  useEffect(() => {
    id && promptApi?.getPromptDetails(id)
      .then(resp => {
        setRecord(resp);
        resp?.name && promptApi?.listPromptVersionsByName(resp?.name)
          .then(resp => resp.map(item => item.promptId))
          .then(ids => setHistory(ids as string[]))
          .catch(handleError);
      })
      .catch(handleError);
  }, [handleError, id, promptApi]);

  function handleEdit(): void {
    id && navigate(`/w/prompt/edit/${id}`);
  }

  function handleCopy(): void {
    id && promptApi?.clonePrompt(id)
      .then(resp => {
        if (resp) {
          navigate(`/w/prompt/edit/${resp}`);
        }
      })
      .catch(handleError);
  }

  return (
    <>
      <LinePlaceholder />
      <CommonContainer sx={{
        alignItems: { xs: 'flex-start', sm: 'flex-end' },
        justifyContent: 'flex-end',
      }}>
        <Box sx={{
          justifySelf: 'flex-start',
          display: 'flex',
          alignItems: 'flex-end',
          flex: 1,
          gap: 2,
        }}>
          <Typography level="h3">{record?.name}</Typography>
          <Chip color="success" variant="soft">v{record?.version}</Chip>
          <Tooltip title={record?.type === 'chat' ? 'chat template' : 'string template'} size="sm">
            {record?.type === 'chat' ?
              <ChatBubbleOutlineRounded color="info" fontSize="small" /> :
              <Title color="info" fontSize="small" />
            }
          </Tooltip>
          <Tooltip title={record?.visibility} size="sm">
            {record?.visibility === 'public' ?
              <PublicRounded color="info" fontSize="small" /> :
              <PublicOffRounded color="info" />}
          </Tooltip>
        </Box>
        <Typography level="body-sm">
          {t('Updated on')} {getDateLabel(record?.gmtModified || new Date(0), i18n.language, true)}
        </Typography>

        <ButtonGroup
          size="sm"
          variant="soft"
          color="primary"
          sx={{
          borderRadius: '16px',
        }}>
          <Button
            startDecorator={<ContentCopyRounded />}
            onClick={handleCopy}
          >
            {t('button:Copy')}
          </Button>
          {record?.username === username && (
            <Button
              startDecorator={<EditRounded />}
              onClick={handleEdit}
            >
              {t('button:Edit')}
            </Button>
          )}
          {play ? (
            <Button
            startDecorator={<ArrowBackRounded />}
            onClick={() => setPlay(false)}
          >
            {t('button:Back')}
          </Button>
          ) : (
            <Button
              startDecorator={<PlayCircleOutlineRounded />}
              onClick={() => setPlay(true)}
            >
              {t('Try it', { ns: 'button' })}
            </Button>
          )}
        </ButtonGroup>
        
      </CommonContainer>
      <Divider />
      <CommonContainer sx={{ flex: 1, alignItems: 'flex-start' }}>
        <PromptContent record={record} />
        {play ? 
          <PromptRunner
            minWidth="16rem"
            maxWidth="50%"
            apiPath="/api/v1/prompt/send/stream"
            record={record}
            defaultVariables={defaultVariables}
            defaultParameters={defaultParameters}
            defaultOutputText={defaultOutputText}
            onPlaySuccess={(request, response) => {
              request?.params && Object.keys(request?.params).length > 0 ?
                setDefaultParameters(request?.params) :
                setDefaultParameters(undefined);

              if (request?.promptRef?.variables && Object.keys(request?.promptRef?.variables).length > 0) {
                setDefaultVariables(request?.promptRef?.variables); 
              } else if (request?.promptTemplate?.variables && Object.keys(request?.promptTemplate?.variables).length > 0) {
                setDefaultVariables(request?.promptTemplate?.variables);
              } else {
                setDefaultVariables(undefined);
              }

              setDefaultOutputText(response?.message?.content ?? response?.text);
            }}
          /> :
          <PromptMeta record={record} history={history} />}
      </CommonContainer>
    </>
  );
}