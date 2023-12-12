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
})
