import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import inject from '@rollup/plugin-inject';

const BEARER_TOKEN = 'fc-cac2e3c7f6ca4f5b9c5d7e0da074f4f7';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    inject({
      Prism: 'prismjs',
      'window.Prism': 'prismjs',
    }),
  ],
  build: {
    rolldownOptions: {
      output: {
        entryFileNames: `assets/[name].js`,
        codeSplitting: {
          groups: [
            {
              test: /@mui/,
              name: 'mui',
            },
            {
              test: /openapi-explorer/,
              name: 'openapi-explorer',
            },
            {
              test: /node_modules/,
              name: 'modules',
            },
          ],
        },
      },
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        headers: {
          Authorization: `Bearer ${BEARER_TOKEN}`,
        },
      },
      '/public': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        headers: {
          Authorization: `Bearer ${BEARER_TOKEN}`,
        },
      },
      '/login': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        headers: {
          Authorization: `Bearer ${BEARER_TOKEN}`,
        },
      },
      '/logout': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        headers: {
          Authorization: `Bearer ${BEARER_TOKEN}`,
        },
      },
    },
  },
});
