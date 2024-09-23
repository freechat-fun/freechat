import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        entryFileNames: `assets/[name].js`,
        manualChunks(id) {
          if (id.indexOf('node_modules') !== -1) {
            if (id.indexOf('@mui') !== -1) {
              return 'mui';
            } else if (id.indexOf('openapi-explorer') !== -1) {
              return 'openapi-explorer';
            } else {
              return 'modules';
            }
          }
        }
      },
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on('proxyReq', (proxyReq) => {
            const bearerToken = 'fc-cac2e3c7f6ca4f5b9c5d7e0da074f4f7';
            proxyReq.setHeader('Authorization', `Bearer ${bearerToken}`);
          });
        },
      },
      '/public': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
      '/login': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
      '/logout': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
    },
  },
})
