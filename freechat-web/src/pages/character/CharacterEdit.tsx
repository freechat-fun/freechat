import { useParams } from 'react-router-dom';
import { CharacterEditor } from '../../components/character';
import { useMetaInfoContext } from '../../contexts';

export default function CharacterEdit() {
  const { id } = useParams();
  const { username } = useMetaInfoContext();

  return username ? (
    <CharacterEditor key={`${username}:${id}`} id={Number(id)} />
  ) : null;
}
