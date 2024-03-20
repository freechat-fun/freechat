import { useParams } from "react-router-dom";
import { PromptViewer } from "../../components/prompt";

export default function PromptInfo() {
  const { id } = useParams();
  
  return (
    <PromptViewer id={Number(id)} />
  )
}
