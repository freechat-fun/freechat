import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import typescript from 'rollup-plugin-typescript2';
import { terser } from 'rollup-plugin-terser';
import sourceMaps from 'rollup-plugin-sourcemaps';

export default [
  // build cjs
  {
    input: './index.ts',
    output: {
      file: 'dist/index.cjs.js',
      format: 'cjs',
      sourcemap: true
    },
    plugins: [
      resolve(),
      commonjs(),
      typescript({
        tsconfig: './tsconfig.json'
      }),
      sourceMaps()
    ],
    external: ['whatwg-fetch', 'es6-promise', 'url-parse']
  },
  // build esm module
  {
    input: './index.ts',
    output: {
      file: 'dist/index.esm.js',
      format: 'es',
      sourcemap: true
    },
    plugins: [
      resolve(),
      typescript({
        tsconfig: './tsconfig.json'
      }),
      sourceMaps()
    ],
    external: ['whatwg-fetch', 'es6-promise', 'url-parse']
  },
  // build umd
  {
    input: './index.ts',
    output: {
      file: 'dist/index.umd.js',
      format: 'umd',
      name: 'freechatSdk',
      sourcemap: true,
      globals: {
        'whatwg-fetch': 'fetch',
        'es6-promise': 'ES6Promise',
        'url-parse': 'UrlParse'
      }
    },
    plugins: [
      resolve(),
      commonjs(),
      typescript({
        tsconfig: './tsconfig.json'
      }),
      terser(),
      sourceMaps()
    ],
    external: ['whatwg-fetch', 'es6-promise', 'url-parse']
  }
];
