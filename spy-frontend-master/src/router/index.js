import { ElMessage } from "element-plus";
import { createRouter, createWebHashHistory } from "vue-router";

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',   //登录页
        name: 'login',
        component: () => import('../pages/login/Login.vue')
    },
    {
        path: '/',   
        name: '',
        component: () => import('../Home.vue'),
        children:[
            {
                path:'home',
                name:'Home',
                component: () => import('../pages/home/Home.vue')
            },
            {
                path:'new',
                name:'New',
                component: () => import('../pages/new/New.vue')
            },
            {
                path:'chat',
                name:'Chat',
                component: () => import('../pages/chatt/Chat.vue')
            },
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})
function getCookie(cname)
{
  var name = cname + "=";
  var ca = document.cookie.split(';');
  for(var i=0; i<ca.length; i++) 
  {
    var c = ca[i].trim();
    if (c.indexOf(name)==0) return c.substring(name.length,c.length);
  }
  return "";
}
// router.beforeEach((to, from, next) => {
//     // 如果访问的是登录界面则直接放行
//     if (to.path === '/login') return next()
//     //获取用户页面token信息
//     let saToken = document.cookie;
//     //如果token数据为null则跳转到指定路径
//     if (!saToken) {
//         ElMessage({
//             message: '请先登录',
//             type: 'error',
//         })
//         return next("/login")
//     }
//     next()
//   })
export default router