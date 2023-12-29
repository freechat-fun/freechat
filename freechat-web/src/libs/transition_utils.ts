import { Dispatch, SetStateAction } from "react";
import { TransitionStatus } from "react-transition-group";

export const defaultTransitionSetting = `opacity 200ms ease-in-out`;

export const defaultTransitionInterval = 100;

export const transitionStyles: { [key in TransitionStatus]?: React.CSSProperties } = {
  entering: { opacity: 0 },
  entered:  { opacity: 1 },
  exiting:  { opacity: 0 },
  exited:   { opacity: 0 },
};

export function initTransitionSequence(
  setShowItems: Dispatch<SetStateAction<boolean>>,
  setShowItemsFinish?: Dispatch<SetStateAction<boolean>>,
  itemCount: number = 1,
  delay: number = 200,
  interval: number = defaultTransitionInterval)
: () => void {
  if (typeof window === 'undefined') {
    return () => {};
  }

  const timeouts: number[] = [];

  timeouts.push(window.setTimeout(() => {
    setShowItems(true);
  }, delay));

  if (setShowItemsFinish) {
    timeouts.push(window.setTimeout(() => {
      setShowItemsFinish(true);
    }, delay + itemCount * interval));
  }

  return () => timeouts.forEach(window.clearTimeout);
}