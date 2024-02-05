import { useParams } from "react-router-dom";
import { PromptEditor } from "../../components/prompt";

export default function PromptEdit() {
  const { id } = useParams();
  
  return (
    <PromptEditor id={id} />
  )
}
