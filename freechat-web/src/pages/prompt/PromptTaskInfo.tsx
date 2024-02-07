import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { PromptTaskDetailsDTO } from "freechat-sdk";
import { useErrorMessageBusContext, useFreeChatApiContext } from "../../contexts";
import { PromptViewer } from "../../components/prompt";

export default function PromptTaskEdit() {
  const { id } = useParams();
  const { promptTaskApi } = useFreeChatApiContext();
  const { handleError } = useErrorMessageBusContext();

  const [promptTask, setPromptTask] = useState<PromptTaskDetailsDTO>()
  
  useEffect(() => {
    if (id) {
      promptTaskApi?.getPromptTask(id)
        .then(setPromptTask)
        .catch(handleError);
    }
  }, [promptTaskApi, handleError, id]);
  return (
    <PromptViewer 
      id={promptTask?.promptRef?.promptId}
      parameters={promptTask?.params}
      variables={promptTask?.promptRef?.variables}
    />
  );
}