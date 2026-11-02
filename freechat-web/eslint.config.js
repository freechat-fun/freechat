import js from '@eslint/js';
import globals from 'globals';
import tseslint from 'typescript-eslint';
import eslintReact from '@eslint-react/eslint-plugin';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import prettierRecommended from 'eslint-plugin-prettier/recommended';

export default [
  {
    // `public` holds static assets (service worker, images) that are not part
    // of the application source and were never linted before.
    ignores: ['dist', 'node_modules', 'public'],
  },
  {
    files: ['**/*.{js,jsx,mjs,cjs,ts,tsx}'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: {
        ...globals.browser,
      },
      parserOptions: {
        ecmaFeatures: { jsx: true },
      },
    },
  },
  js.configs.recommended,
  ...tseslint.configs.recommended,
  eslintReact.configs['recommended-typescript'],
  {
    // @eslint-react's recommended set is far broader than the previous
    // eslint-plugin-react "recommended" preset. The rules below are additive /
    // opinionated checks that were never enforced here, so they are turned off
    // to keep this migration behavior-preserving. Re-enable incrementally.
    rules: {
      '@eslint-react/set-state-in-effect': 'off',
      '@eslint-react/use-state': 'off',
      '@eslint-react/purity': 'off',
      '@eslint-react/no-forward-ref': 'off',
      '@eslint-react/no-array-index-key': 'off',
      '@eslint-react/no-context-provider': 'off',
      '@eslint-react/no-use-context': 'off',
      '@eslint-react/naming-convention-ref-name': 'off',
      '@eslint-react/web-api-no-leaked-event-listener': 'off',
      // Redundant with react-hooks/exhaustive-deps configured below.
      '@eslint-react/exhaustive-deps': 'off',
    },
  },
  {
    // Keep the classic react-hooks behavior. The v7 "recommended" preset also
    // bundles React Compiler rules (refs, immutability, purity, set-state-in-*)
    // which are a separate, opt-in adoption.
    plugins: {
      'react-hooks': reactHooks,
    },
    rules: {
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'warn',
    },
  },
  {
    plugins: {
      'react-refresh': reactRefresh,
    },
    rules: {
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
    },
  },
  prettierRecommended,
];
