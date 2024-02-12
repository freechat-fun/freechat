import React, { useRef, useEffect, PropsWithChildren } from 'react';
import Prism from 'prismjs';
import 'prismjs/themes/prism-okaidia.css';
import 'prismjs/components/prism-javascript';
import 'prismjs/components/prism-jsx';
import 'prismjs/components/prism-typescript';
import 'prismjs/components/prism-tsx';
import 'prismjs/components/prism-css';
import 'prismjs/components/prism-markup';
import 'prismjs/components/prism-uri';
import 'prismjs/components/prism-json';
import 'prismjs/components/prism-markdown';
import 'prismjs/components/prism-dot';
import 'prismjs/components/prism-bash';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-c';
import 'prismjs/components/prism-cpp';
import 'prismjs/components/prism-csharp';
import 'prismjs/components/prism-python';



const CodeContent: React.FC<PropsWithChildren> = ({ children }) => {
  const codeRef = useRef<HTMLPreElement>(null);

  const className = typeof children === 'object' && children !== null &&('props' in children) ?
    (children.props.className ?? '') : '';

  const codeText = typeof children === 'object' && children !== null &&('props' in children) ?
    (children.props.children ?? '') : '';

  useEffect(() => {
    const highlightCode = async () => {
      if (codeRef.current) {
        let language = 'none';
        const languageMatch = className?.match(/language-(\w+)/);
        if (languageMatch) {
          language = languageMatch[1];
          try {
            await import(`/* @vite-ignore */prismjs/components/prism-${language}.js`);
          } catch (error) {
            console.error('PrismJS load language failed:', language);
            language = 'none';
          }
        }
        Prism.highlightElement(codeRef.current);
      }
    };

    highlightCode();
  }, [className]);

  return (
    <pre>
      <code ref={codeRef} className={className || 'language-none'}>
        {codeText}
      </code>
    </pre>
  );
};

export default CodeContent;