import React, { PropsWithChildren } from 'react';
import { PrismAsyncLight as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism'; 

const CodeContent: React.FC<PropsWithChildren> = ({ children }) => {
  const className =
    typeof children === 'object' && children !== null && 'props' in children &&
    typeof children.props === 'object' && children.props !== null &&
    'className' in children.props && typeof children.props.className === 'string'
      ? (children.props.className ?? '')
      : '';

  const match = /language-(\w+)/.exec(className || '');
  const language = match ? match[1] : 'text';

  const codeText =
    typeof children === 'object' && children !== null && 'props' in children &&
    typeof children.props === 'object' && children.props !== null &&
    'children' in children.props && typeof children.props.children === 'string'
      ? (children.props.children ?? '')
      : '';

  return (
    <SyntaxHighlighter
      style={vscDarkPlus}
      language={language}
      PreTag="div"
    >
      {String(codeText).replace(/\n$/, '')}
    </SyntaxHighlighter>
  );
};

export default CodeContent;