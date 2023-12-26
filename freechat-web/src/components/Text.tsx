import React from 'react';
import { useErrorMessageBusContext } from "../contexts";
import { SxProps } from '@mui/joy/styles/types';
import { Link, Skeleton, Textarea, Typography, TypographyProps, styled } from '@mui/joy';

const Markdown = React.lazy(async () => import('markdown-to-jsx'));

const StyledTextareaAutosize = styled(Textarea)(({ theme }) => ({
  width: '100%',
  resize: 'none',
  border: 'none',
  outline: 'none',
  padding: 0,

  ...Object.fromEntries(
    Object.keys(theme.typography).map((variant) => [
      [`&.variant-${variant}`],
      theme.typography[variant as keyof typeof theme.typography],
    ]),
  ),
}));

interface TextProps {
  mode: 'markdown' | 'link' | 'text';
  value: string;
  href?: string;
  openInNewTab?: boolean;
  loading?: boolean;
  error?: unknown;
  sx?: SxProps;
  variant?: TypographyProps['variant'];
}

const MarkdownContainer = styled('div')(({ theme }) => ({
  display: 'block',
  overflowWrap: 'anywhere',
  '&:empty::before, & > span:empty::before': {
    content: '""',
    display: 'inline-block',
  },
  '& h1': {
    ...theme.typography.h1,
    marginTop: 16,
    marginBottom: 16,
  },
  '& h2': {
    ...theme.typography.h2,
    marginTop: 12,
    marginBottom: 12,
  },
  '& h3': {
    ...theme.typography.h3,
    marginTop: 12,
    marginBottom: 12,
  },
  '& h4': {
    ...theme.typography.h4,
    marginTop: 12,
    marginBottom: 12,
  },
  '& h5': {
    ...theme.typography['title-lg'],
    marginTop: 4,
    marginBottom: 4,
  },
  '& h6': {
    ...theme.typography['title-md'],
    marginTop: 4,
    marginBottom: 4,
  },
  '& p': {
    marginTop: 12,
    marginBottom: 12,
  },
  '& *:first-child': {
    marginTop: 0,
  },
  '& *:last-child': {
    marginBottom: 0,
  },
}));

interface MarkdownContentProps {
  value: string;
  loading?: boolean;
  sx?: SxProps;
}

const CodeContainer = styled('pre')(({ theme }) => ({
  backgroundColor: theme.palette.background.surface[200],
  marginLeft: theme.spacing(1),
  marginRight: theme.spacing(1),
  padding: theme.spacing(1),
  overflow: 'auto',
}));

function parseInput(text: unknown): string {
  return String(text).replace(/\n/g, '');
}

interface LinkContentProps {
  value: string;
  href?: string;
  loading?: boolean;
  openInNewTab?: boolean;
  sx?: SxProps;
}

function LinkContent({ value, href, loading, sx, openInNewTab }: LinkContentProps) {
  const content = React.useMemo(() => {
    if (loading) {
      return <Skeleton variant="text" />;
    }
    return value;
  }, [value, loading]);

  return (
    <Link
      href={href}
      target={openInNewTab ? '_blank' : undefined}
      rel="noopener noreferrer"
      sx={{
        minWidth: loading || !value ? 150 : undefined,
        // Same as Typography
        [`&:empty::before`]: { content: '""', display: 'inline-block' },
        overflowWrap: 'anywhere',
        ...sx,
      }}
    >
      {content}
    </Link>
  );
}

interface MarkdownContentProps {
  value: string;
  loading?: boolean;
  sx?: SxProps;
}

function MarkdownContent({ value, loading, sx }: MarkdownContentProps) {
  const loadingFallback = <Skeleton variant="text" width={'100%'} />;

  return (
    <MarkdownContainer sx={sx}>
      {loading ? (
        loadingFallback
      ) : (
        <React.Suspense fallback={loadingFallback}>
          <Markdown
            options={{
              overrides: {
                a: {
                  component: Link,
                  props: {
                    target: '_blank',
                    rel: 'noopener noreferrer',
                  },
                },
                pre: {
                  component: CodeContainer,
                },
              },
              slugify: () => '',
            }}
          >
            {value}
          </Markdown>
        </React.Suspense>
      )}
    </MarkdownContainer>
  );
}

interface TextContentProps {
  value: string;
  loading?: boolean;
  sx?: SxProps;
  variant?: TypographyProps['variant'];
}

function TextContent({ value, loading, sx, variant = 'plain' }: TextContentProps) {
  const [contentEditable, setContentEditable] = React.useState<null | {
    selectionStart: number;
    selectionEnd: number;
  }>(null);
  const [input, setInput] = React.useState<string>(parseInput(value));
  React.useEffect(() => {
    setInput(parseInput(value));
  }, [value]);

  return contentEditable ? (
    <StyledTextareaAutosize
      value={input}
      onChange={(event) => {
        setInput(parseInput(event.target.value));
      }}
      onKeyDown={(event) => {
        if (event.key === 'Enter') {
          event.preventDefault();
        }
      }}
      autoFocus
      onFocus={(event) => {
        event.currentTarget.selectionStart = contentEditable.selectionStart;
        event.currentTarget.selectionEnd = Math.max(
          contentEditable.selectionStart,
          contentEditable.selectionEnd,
        );
      }}
      onBlur={() => setContentEditable(null)}
      className={`variant-${variant}`}
    />
  ) : (
    <Typography
      sx={{
        ...sx,
        // This will give it height, even when empty.
        // REMARK: Does it make sense to put it in MUI core?
        [`&:empty::before`]: { content: '""', display: 'inline-block' },
        outline: 'none',
        whiteSpace: 'pre-wrap',
        overflowWrap: 'anywhere',
      }}
      variant={variant}
    >
      {loading ? <Skeleton variant="text" /> : input}
    </Typography>
  );
}

export default function Text(props: TextProps) {
  const { handleError } = useErrorMessageBusContext();
  if (props.error) {
    handleError(props.error);
    return;
  }
  switch (props.mode) {
    case 'markdown':
      return <MarkdownContent {...props} />;
    case 'link':
      return <LinkContent {...props} />;
    case 'text':
    default:
      return <TextContent {...props} />;
  }
}
