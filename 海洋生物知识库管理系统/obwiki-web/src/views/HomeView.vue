<template>

  <a-layout>

    <a-layout-sider width="200" style="background: #fff">
      <a-menu
        mode="inline"
        :style="{ height: '100%', borderRight: 0 }"

        @click="handleClick"
      ><!--     ↑   菜单添加点击事件-->
        <a-menu-item key="welcome">
          <MailOutlined />
          <span>欢迎</span>
        </a-menu-item>

        <a-sub-menu v-for="item in level1" :key="item.id" >
          <template v-slot:title>
            <span><user-outlined />{{item.name}}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined /><span>{{child.name}}</span>
          </a-menu-item>
        </a-sub-menu>

      </a-menu>
    </a-layout-sider>

    <a-layout style="padding: 0 24px 24px">
      <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
<!--        设置欢迎页面-->
        <div class="welcome" v-show="isShowWelcome">
<!--          <h1>欢迎</h1>-->
          <the-welcome></the-welcome>
        </div>

<!--        通过isShowWelcome属性进行切换显示 ↓ -->
        <a-list     v-show="!isShowWelcome"
          item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">

          <template #renderItem="{ item }">
            <a-list-item key="item.name">
              <template #actions>
<!--                <span v-for="{ icon, text } in actions" :key="icon">-->
<!--                  <component :is="icon" style="margin-right: 8px" />-->
<!--                  {{ text }}-->
<!--                </span>-->
                <span >
                  <component :is="FileOutlined" style="margin-right: 8px" />
                  {{ item.docCount }}
                </span>
                <span >
                  <component :is="UserOutlined" style="margin-right: 8px" />
                  {{ item.viewCount }}
                </span>
                <span >
                  <component :is="LikeOutlined" style="margin-right: 8px" />
                  {{ item.voteCount }}
                </span>

              </template>

              <a-list-item-meta :description="item.description">
                <template #title>
<!--                  <a :href="item.href">{{ item.name }}</a>-->
                  <router-link :to="'/doc?ebookId='+item.id">
                    {{item.name}}
                  </router-link>
                </template>
                <template #avatar><a-avatar :src="item.cover" /></template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>

      </a-layout-content>
    </a-layout>

  </a-layout>

</template>

<script lang="ts" setup >
import { ref, onMounted } from 'vue';
import axios from "axios";
//import { StarOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue';
import { FileOutlined, UserOutlined, LikeOutlined, MailOutlined} from '@ant-design/icons-vue';

import TheWelcome from '@/components/the-welcome.vue';

import {Tool} from '@/utils/tool';
import {message} from 'ant-design-vue';

//定义响应式数据
const ebooks = ref();


//点击菜单显示分类电子书
const isShowWelcome = ref(true);
let categoryId2 = 0;

//完成渲染后执行
onMounted(()=>{
  //handleQueryEbook();
  handleQueryCategory();
})

const handleQueryEbook = ()=>{
  axios.get("/ebook/list",{
    params:{
      page:1,
      size:5,

      categoryId2:categoryId2  //修改查询方法 携带分类id参数
    }}).then(resp=>{
    console.log(resp);
    // ebooks.value = resp.data.content;
    ebooks.value = resp.data.content.list;
  });
}

/*
const actions: Record<string, any>[] = [
  { icon: StarOutlined, text: '156' },
  { icon: LikeOutlined, text: '156' },
  { icon: MessageOutlined, text: '2' },
];
 */


/*
  * 分类相关
  * 首页显示左侧分类菜单
  * */
const level1 = ref();
const  handleQueryCategory = ()=>{
  axios.get("/category/all").then((resp)=>{
    const  data = resp.data;

    if (data.success){
      console.log("原始数组",data.content);

      level1.value = [];
      level1.value = Tool.array2Tree(data.content,0);
      console.log("树形结构：",level1);
    } else {
      message.error(data.message);
    }
  });
};

//点击分类导航栏 获取选中id,发送查询请求
const handleClick = (value:any)=>{
  if (value.key === 'welcome'){
    isShowWelcome.value = true;
  } else {
    categoryId2 = value.key;
    isShowWelcome.value = false;
    handleQueryEbook();
  }
}

</script>

