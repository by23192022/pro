import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';

import axios from 'axios';

//11  3.7.2 前端请求携带token
import {Tool} from "@/utils/tool";

createApp(App).use(store).use(router).use(Antd).mount('#app')

//获取环境配置的 请求前缀
axios.defaults.baseURL = process.env.VUE_APP_SERVER;

//11  3.7.2 前端请求携带token
/*
* 使用axios拦截器打印请求和响应的数据
* */
axios.interceptors.request.use(function (config:any){
  console.log('请求参数：',config);
  const token = store.state.user.token;
  if (Tool.isNotEmpty(token)) {
    config.headers.token = token;
    console.log("请求headers增加token:", token);
  }
  return config;
},error => {
  return Promise.reject(error);
});
