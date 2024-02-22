import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { PromptTaskDetailsDTO } from "freechat-sdk";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { PromptViewer } from "../../components/prompt";

export default function PromptTaskInfo() {
  const { id } = useParams();
  const { promptTaskApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [promptTask, setPromptTask] = useState<PromptTaskDetailsDTO>();
  const [promptId, setPromptId] = useState(promptTask?.promptRef?.promptId);
  const [parameters, setParameters] = useState({...promptTask?.params, modelId: promptTask?.modelId, apiKeyName: promptTask?.apiKeyName, apiKey: promptTask?.apiKeyValue});
  const [variables, setVariables] = useState({...promptTask?.promptRef?.variables});
  
  useEffect(() => {
    if (id) {
      promptTaskApi?.getPromptTask(id)
        .then(setPromptTask)
        .catch(handleError);
    }
  }, [promptTaskApi, handleError, id]);

  useEffect(() => {
    setPromptId(promptTask?.promptRef?.promptId);
    setParameters({
      ...promptTask?.params,
      modelId: promptTask?.modelId,
      apiKeyName: promptTask?.apiKeyName,
      apiKey: promptTask?.apiKeyValue
    });
    setVariables({...promptTask?.promptRef?.variables});
  }, [promptTask]);
  
  useEffect(() => {
    if (id) {
      promptTaskApi?.getPromptTask(id)
        .then(setPromptTask)
        .catch(handleError);
    }
  }, [promptTaskApi, handleError, id]);
  return (
    <PromptViewer 
      id={promptId}
      parameters={parameters}
      variables={variables}
    />
  );
}