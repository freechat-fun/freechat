import React from 'react';
import { SxProps, Theme } from '@mui/material/styles';
import { Link, Skeleton, Table, styled } from '@mui/material';
import { BlockquoteContent, CodeContent } from '.';

const Markdown = React.lazy(async () => import('markdown-to-jsx'));

const MarkdownContainer = styled('div')(({ theme }: { theme: Theme }) => ({
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
    ...theme.typography.h5,
    marginTop: 4,
    marginBottom: 4,
  },
  '& h6': {
    ...theme.typography.h6,
    marginTop: 4,
    marginBottom: 4,
  },
  '& p': {
    marginTop: 12,
    marginBottom: 12,
  },
  '& *:first-of-type': {
    marginTop: 0,
  },
  '& *:last-child': {
    marginBottom: 0,
  },
}));

type ImageProps = React.ImgHTMLAttributes<HTMLImageElement> & {
  alt: string;
};

const ImageContainer: React.FC<ImageProps> = ({ alt, ...props }) => {
  let width;
  let height;

  // Support image content like:
  // ![xxx.jpg|720x480](https://xxx.jpg)
  const match = /(.*?)\|(\d+)(x?)(\d*)(x?)/.exec(alt);
  if (match) {
    alt = match[1];
    const [, , widthValue, widthDelimiter, heightValue] = match;
    if (widthDelimiter) {
      width = widthValue;

      if (heightValue) {
        height = heightValue;
      }
    }
  }

  const style = {
    maxWidth: '100%',
    height: 'auto',
    ...(width && { width: `${width}px` }),
    ...(height && { height: `${height}px` }),
  };

  return <img alt={alt} {...props} style={style} />;
};

type MarkdownContentProps = {
  loading?: boolean;
  sx?: SxProps<Theme>;
  children: React.ReactNode | undefined;
};

export default function MarkdownContent({
  loading,
  sx,
  children = '',
}: MarkdownContentProps) {
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
                  component: CodeContent,
                },
                img: {
                  component: ImageContainer,
                },
                table: {
                  component: Table,
                },
                blockquote: {
                  component: BlockquoteContent,
                },
              },
              slugify: () => '',
            }}
          >
            {children as string}
          </Markdown>
        </React.Suspense>
      )}
    </MarkdownContainer>
  );
}
