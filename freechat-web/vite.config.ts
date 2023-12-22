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
            const bearerToken = 'fc-bd01ea2850d84e6ea821ed920a2bd0f5';
            proxyReq.setHeader('Authorization', `Bearer ${bearerToken}`);
          });
        },
      },
    },
  },
})
