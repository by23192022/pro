<template>
  <a-layout-header class="header">
<!--    <div class="logo" />-->
    <div class="logo" >海洋生物知识库 </div>
    <a-menu
      v-model:selectedKeys="selectedKeys1"
      theme="dark"
      mode="horizontal"
      :style="{ lineHeight: '64px'  , display:'block'}"
    >
      <a-menu-item key="1" :style="{float:'left'}">
        <router-link to="/">首页</router-link>
      </a-menu-item>

      <a-menu-item key="/admin/user" :style="{float:'left'}">
        <router-link to="/admin/user" v-show="user.id">用户管理</router-link>
      </a-menu-item>

      <a-menu-item key="2" :style="{float:'left'}">
        <router-link to="/admin/ebook" v-show="user.id">海洋生物种类管理</router-link>
      </a-menu-item>

      <a-menu-item key="/admin/category" :style="{float:'left'}">
        <router-link to="/admin/category" v-show="user.id">海洋生物分类管理</router-link>
      </a-menu-item>

      <a-menu-item key="3" :style="{float:'left'}">
        <router-link to="/about">关于我们</router-link>
      </a-menu-item>


      <a-menu-item key="user"  :style="{float:'right'}"  >
        <span v-show="user.id" >您好：{{user.name}}</span>
      </a-menu-item>
      <a-menu-item key="login"  :style="{float:'right'}" >
        <a v-show="!user.id" @click="showLoginModal">登录</a>
      </a-menu-item>

      <a-menu-item   :style="{float:'right'}" >
        <a-popconfirm
          title="确认退出登录?"
          ok-text="是"
          cancel-text="否"
          @confirm="logout()"
        >
          <a  v-show="user.id" >退出登录</a>
<!--          <router-link to="/"></router-link>-->
        </a-popconfirm>
      </a-menu-item>

    </a-menu>

    <a-modal
      title="登录"
      v-model:visible="loginModalVisible"
      :confirm-loading="loginModalLoading"
      @ok="login"
    >
      <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="登录名">
          <a-input v-model:value="loginUser.loginName" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input v-model:value="loginUser.password" type="password" />
        </a-form-item>
      </a-form>
    </a-modal>

  </a-layout-header>

</template>

<script lang="ts"  setup>
  import { message } from "ant-design-vue";
  import axios from 'axios';
  import { computed, defineComponent, ref } from 'vue';

  //3.5.4 集成VueX共享数据
  import store from '@/store';

  //任意页面 点击退出登录 跳转到初始页
  //import router from "@/router";
  import { useRouter } from 'vue-router';


  declare let hexMd5: any;
  declare let KEY: any;

  //登录之后保存的用户
  const user = computed(()=>store.state.user);
  /*
  const user =ref();
  user.value={};
  //用来登录
  */

  const loginUser = ref({
    loginName: "",
    password: ""
  });

  const loginModalVisible = ref(false);
  const loginModalLoading = ref(false);
  const showLoginModal = () => {
    loginModalVisible.value = true;
  };


  // 登录
  const login = () => {
    console.log("开始登录");
    loginModalLoading.value = true;
    loginUser.value.password = hexMd5(loginUser.value.password + KEY);
    axios.post('/user/login', loginUser.value).then((response) => {
      loginModalLoading.value = false;
      const data = response.data;
      if (data.success) {
        loginModalVisible.value = false;
        console.log("登录成功返回数据:",data);
        //user.value = data.content;

        //3.5.4 集成VueX共享数据
        //store.commit("setUser",user.value); //调用commit方法 参数1 触发方法名 参数2 传入值
        store.commit("setUser",data.content); //调用commit方法 参数1 触发方法名 参数2 传入值

        message.success("【"+user.value.loginName+"】" + "登录成功！");

      } else {
        message.error(data.message);
        loginModalLoading.value = false;
      }
    });
  };

  //任意页面 点击退出登录 跳转到初始页
  const router = useRouter();
  // 退出登录
  const logout = () => {
    console.log("退出登录开始");
    axios.get('/user/logout/' + user.value.token).then((response) => {
      const data = response.data;
      if (data.success) {
        message.success("退出登录成功！");
        store.commit("setUser", {});

        //任意页面 点击退出登录 跳转到初始页
        //if (router.path !== '/'){
        if (router.currentRoute.value.fullPath !== '/'){
          router.replace('/')
        }
        //清空model显示的数据
        loginUser.value = {};
        /*
        loginUser.value = {
          loginName: "",
          password: ""
        };
         */

      } else {
        message.error(data.message);
      }
    });
  };

</script>

<style scoped>
  .logo {
    width: 150px;
    height: 31px;
    /*background: rgba(255, 255, 255, 0.2);*/
    /*margin: 16px 28px 16px 0;*/
    float: left;
    color: white;
    font-size: 18px;
  }
</style>

