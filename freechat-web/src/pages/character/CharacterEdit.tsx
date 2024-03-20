import { useParams } from "react-router-dom";
import { CharacterEditor } from "../../components/character";

export default function CharacterEdit() {
  const { id } = useParams();
  
  return (
    <CharacterEditor id={Number(id)} />
  )
}
