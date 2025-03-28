import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'

//11  3.7.3 前端页面增加登录校验
import store from "@/store";
import {Tool} from "@/utils/tool";
import {message} from "ant-design-vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
  {
    path: '/admin/ebook',
    name: 'AdminEbook',
    component: () => import( '../views/admin/admin-ebook.vue')
  },
  {
    path: '/admin/category',
    name: 'AdminCategory',
    component: () => import( '../views/admin/admin-category.vue')
  },
  {
    path: '/admin/doc',
    name: 'AdminDoc',
    component: () => import( '../views/admin/admin-doc.vue')
  },
  {
    path: '/doc',
    name: 'Doc',
    component: () => import( '../views/DocView.vue')
  },
  {
    path: '/admin/user',
    name: 'AdminUser',
    component: () => import( '../views/admin/admin-user.vue'),
    meta:{
    loginRequire: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router

//11  3.7.3 前端页面增加登录校验
// 最下方路由登录拦截  每次路由拦截
router.beforeEach((to, from, next) => {
  // 要不要对meta.loginRequire属性做监控拦截
  if (to.matched.some(function (item) {
    console.log(item, "是否需要登录校验：", item.meta.loginRequire);
    return item.meta.loginRequire
  })) {
    const loginUser = store.state.user;
    if (Tool.isEmpty(loginUser)) {
      // console.log("用户未登录！");
      message.error("请登录！")
      next('/');
    } else {
      next();
    }
  } else {
    next();
  }
});
