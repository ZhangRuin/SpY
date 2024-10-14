import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  test: {
    // 启用类似 jest 的全局测试 API
    globals: true,
    // 使用 happy-dom 模拟 DOM
    // 这需要你安装 happy-dom 作为对等依赖（peer dependency）
    environment: 'happy-dom'
  },
    // server: {
    //   proxy:{
    //     '/api':{
    //       //target:'http://localhost:9000',
    //       target:'http://111.229.131.214:9010',
    //       ws:true,
    //       changeOrigin:true,
    //       rewrite:(path) =>path.replace(/^\/api/,'')
    //     }
    //   }
    // }

})