import { Textarea, TextareaProps, styled } from '@mui/joy';

const ContentTextarea = styled(Textarea)<TextareaProps>(() => ({
  whiteSpace: 'pre-wrap',
  overflowWrap: 'break-word',
  flex: 1,
}));

export default ContentTextarea;
