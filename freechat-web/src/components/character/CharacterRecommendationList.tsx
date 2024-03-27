import { createRef, useEffect, useRef, useState } from "react";
import { List, ListProps } from "@mui/joy";
import { CharacterSummaryDTO } from "freechat-sdk";
import { CharacterRecommendationListItem } from ".";
import { defaultTransitionInterval, defaultTransitionSetting, initTransitionSequence, transitionStyles } from "../../libs/transition_utils";
import { Transition } from "react-transition-group";


type CharacterRecommendationListProps = ListProps & {
  records: CharacterSummaryDTO[],
  selectedId: number;
  setSelectedId: (id?: number) => void;
}

export default function CharacterRecommendationList(props: CharacterRecommendationListProps) {
  const { records, selectedId, setSelectedId, sx } = props;

  const [showCards, setShowCards] = useState(false);
  const itemRefs = useRef(Array(records.length || 6).fill(createRef()));

  useEffect(() => {
    return initTransitionSequence(setShowCards, undefined, records.length);
  }, [records.length]);

  return (
      <List
        sx={{
          py: 0,
          '--ListItem-paddingY': '0.75rem',
          '--ListItem-paddingX': '1rem',
          ...sx,
        }}
      >
        {records.map((record, index) => (
          <Transition
            in={showCards}
            timeout={index * defaultTransitionInterval}
            unmountOnExit
            key={`transition-${record.characterId || index}`}
            nodeRef={itemRefs.current[index]}
          >
            {(state) => (
              <CharacterRecommendationListItem
                key={`record-${record.characterId || index}`}
                ref={itemRefs.current[index]}
                record={record}
                selectedId={selectedId}
                setSelectedId={setSelectedId}
                sx={{
                  minHeight: '80px',
                  transition: defaultTransitionSetting,
                  ...transitionStyles[state],
                }}
              />
            )}
          </Transition>
        ))}
      </List>
  );
}