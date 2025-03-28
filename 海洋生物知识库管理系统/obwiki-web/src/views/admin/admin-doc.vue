<template>
  <div>
    <a-layout>
      <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >

        <a-row>
          <!--修改布局-->
          <a-col :span="8">
            <p>
              <a-form layout="inline" :model="param">
                <a-form-item>
                  <a-button type="primary" @click="add()">
                    新增
                  </a-button>
                </a-form-item>
              </a-form>
            </p>

            <a-table
              v-if="level1.length > 0"
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1"
              :pagination="false"
              :loading="loading"
              :defaultExpandAllRows="true"
            >
              <template #bodyCell="{ column,record}">

                <template v-if="column.dataIndex === 'action'">
                  <a-space size="small">

                    <a-button type="primary"  @click="edit(record)">
                      编辑
                    </a-button>
                    <a-popconfirm
                      title="删除后不可以恢复，确认删除?"
                      ok-text="是"
                      cancel-text="否"
                      @confirm="handleDelete(record.id)"
                    >
                      <a-button type="danger" >
                        删除
                      </a-button>
                    </a-popconfirm>

                  </a-space>
                </template>
              </template>
            </a-table>

          </a-col>

          <!--修改布局-->
          <a-col :span="16">

            <p>
              <a-form layout="inline" >
                <a-form-item>
                  <a-button type="primary"  @click="handleSave()">
                    保存
                  </a-button>
                </a-form-item>
              </a-form>
            </p>

            <!--      <a-modal title="电子书文档表单" ... @ok="handleModalOk"-->
            <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

              <a-form-item label="名称">
                <a-input v-model:value="doc.name" />
              </a-form-item>

              <a-form-item label="父分类">

                <a-tree-select
                  v-model:value="doc.parent"
                  show-search
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="请选择树文档"
                  allow-clear
                  tree-default-expand-all
                  :tree-data="treeSelectData"
                  tree-node-filter-prop="label"
                  :replaceFields ="{children:'children', label:'name', key:'id', value: 'id' }"
                >
                </a-tree-select>
              </a-form-item>


              <a-form-item label="排序">
                <a-input v-model:value="doc.sort" />
              </a-form-item>

              <a-form-item label="内容">
<!--  （1）使用wangeditor时显示图片自适应框架  class="wangeditor" 。。。:innerHTML="html"-->
<!--报错html  （2）富文本编辑器在回显html时偶尔会报错:Cannot find a descendant at path [1] in node: {  。。  删v-if="dialogFormVisible"-->
<!--。。解决（1）后，（2）也没出现了-->
                <div class="wangeditor" :innerHTML="html"
                  style="border: 1px solid #ccc">
                  <Toolbar
                    style="border-bottom: 1px solid #ccc"
                    :editor="editorRef"
                    :defaultConfig="toolbarConfig"
                    :mode="mode"
                  />
                  <Editor
                    style="height: 500px; overflow-y: hidden;"
                    v-model="doc.content"
                    :defaultConfig="editorConfig"
                    :mode="mode"
                    @onCreated="handleCreated"

                    v-if="onMounted"
                  />
                </div>
              </a-form-item>

            </a-form>

          </a-col>

        </a-row>

      </a-layout-content>
    </a-layout>

  </div>
</template>

<script lang="ts" setup>
import axios from 'axios';
//import { Tool } from "@/utils/tool.ts";
import { Tool } from "@/utils/tool";
import { message } from 'ant-design-vue';
import {useRoute} from 'vue-router';
import { ref, onMounted,shallowRef } from 'vue';
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const docs = ref();//定义查询电子书返回集合
const param = ref();
param.value = {};
//定义分页参数
const pagination = ref({
  current: 1,
  pageSize: 8,
  total: 0
});
const loading = ref(false);
const columns = [
  {
    title: '名称',
    dataIndex: 'name'
  },
  {
    title: 'Action',
    key: 'action',
    dataIndex:'action'
  }
];

onMounted(() => {
  handleQuery();
})

// 数据查询
const level1 = ref([]);
const  handleQuery = ()=>{
  loading.value = true;
  axios.get("/doc/all").then((resp)=>{
    loading.value = false;
    const  data = resp.data;
    if (data.success){
      level1.value = [];//每次查询清空分类数组
      //通过工具类 递归父分类及子分类  参数1 分类所有数据 参数2 父分类id为0
      level1.value = Tool.array2Tree(data.content,0);
      console.log("树形结构：",level1);
    }
  });
};
// 编辑相关功能
const doc1 = ref({});
const doc = ref({
  id:0,
  name:"",
  parent:0,
  ebook_id:0,
  sort:0
});
const modalVisible = ref(false);
const modalLoading = ref(false);

//优化：
//定义显示文档树 响应式数据,因为树选择组件属性转台会随当前编辑节点而变化  初始值为空
const treeSelectData = ref();
treeSelectData.value = [];

//编辑
const edit = (record:any)=>{
  doc.value = Tool.copy(record);
  modalVisible.value =true;

  handleQueryContent();//查询文档内容

  //优化：
  //不能选择当前节点及其子节点，作为父节点，会使得树断开
  treeSelectData.value = Tool.copy(level1.value);
  setDisable(treeSelectData.value,record.id);
  //为树选择添加一个“无”的选项
  treeSelectData.value.unshift({id:0,name:'无'});
}
/**
 * 将某节点及其子孙节点全部置为disabled
 */
const setDisable = (treeSelectData: any, id: any) => {
  // console.log(treeSelectData, id);
  // 遍历数组，即遍历某一层节点
  for (let i = 0; i < treeSelectData.length; i++) {
    const node = treeSelectData[i];
    if (node.id === id) {
      // 如果当前节点就是目标节点
      console.log("disabled", node);
      // 将目标节点设置为disabled
      node.disabled = true;

      // 遍历所有子节点，将所有子节点全部都加上disabled
      const children = node.children;
      if (Tool.isNotEmpty(children)) {
        for (let j = 0; j < children.length; j++) {
          setDisable(children, children[j].id)
        }
      }
    } else {
      // 如果当前节点不是目标节点，则到其子节点再找找看。
      const children = node.children;
      if (Tool.isNotEmpty(children)) {
        setDisable(children, id);
      }
    }
  }
};

//内容查询
const handleQueryContent = () => {
  axios.get("/content/findContent/" + doc.value.id).then((response) => {
    const data = response.data;
    if (data.success) {
      editorRef.value.setHtml(data.content)
    } else {
      message.error(data.message);
    }
  });
};


const route = useRoute();//声明路由
//新增
const add = ()=>{
  modalVisible.value =true;

  doc.value =  {
    // id: number; name: string; parent: number; ebookId: number; sort: number; ;
    ebookId: route.query.ebookId//获取电子书id
  }
  // doc.value.ebookId = route.query.ebookId;

  //不能选择当前节点及其子节点，作为父节点，会使得树断开
  treeSelectData.value = Tool.copy(level1.value);
  //为树选择添加一个“无”的选项
  treeSelectData.value.unshift({id:0,name:'无'});

}

const ids:Array<string> = [];
const deleteNames:Array<string> = [];
//删除
const handleDelete = (id:number)=>{
  // 清空数组，否则多次删除时，数组会一直增加
  ids.length = 0;
  deleteNames.length = 0;
  getDeleteIds(level1.value,id);//获取要删除的节点及子节点id

  axios.get('/doc/remove?ids='+ids.join(",")).then((response) => {
    const data = response.data;
    if (data.success) {
      // 重新加载列表
      handleQuery();
    } else {
      message.error(data.message);
    }
  })
}
/**
 * 查找整根树枝
 */
const getDeleteIds = (treeSelectData: any, id: any) => {
  // console.log(treeSelectData, id);
  // 遍历数组，即遍历某一层节点
  for (let i = 0; i < treeSelectData.length; i++) {
    const node = treeSelectData[i];
    if (node.id === id) {
      // 如果当前节点就是目标节点
      console.log("delete", node);
      // 将目标ID放入结果集ids
      // node.disabled = true;
      ids.push(id);
      deleteNames.push(node.name);

      // 遍历所有子节点
      const children = node.children;
      if (Tool.isNotEmpty(children)) {
        for (let j = 0; j < children.length; j++) {
          getDeleteIds(children, children[j].id)
        }
      }
    } else {
      // 如果当前节点不是目标节点，则到其子节点再找找看。
      const children = node.children;
      if (Tool.isNotEmpty(children)) {
        getDeleteIds(children, id);
      }
    }
  }
};


//保存
const handleModalOk = ()=>{
  modalLoading.value = true;
  axios.post("/doc/save",doc.value).then(resp =>{
    //回去返回参数
    const data = resp.data;
    if(data.success){
      modalLoading.value = false;//关闭等待
      modalVisible.value = false;//关闭对话框
      //重新加载列表
      handleQuery()
    }
  })
}

const toolbarConfig = {}
const editorConfig = { placeholder: '请输入内容...' }
//富文本编辑器
const editorRef = shallowRef();
// 编辑器回调函数
const handleCreated = (editor: any) => {
  console.log('created', editor);
  editorRef.value = editor; // 记录 editor 实例，重要！
};

const handleSave = () => {
  modalLoading.value = true;

  doc.value.content = editorRef.value.getHtml();
  axios.post("/doc/save",doc.value).then((resp)=>{
    const data = resp.data;
    if (data.success){
      modalLoading.value = false;
      message.success("保存成功！");
      //重新加载列表
      handleQuery();
    }
  })
};



</script>

<style scoped>

  /* 图片自适应 */
  .wangeditor img {
    max-width: 100%;
    height: auto;
  }

  /* 视频自适应 */
  .wangeditor iframe {
    width: 100%;
    height: 400px;
  }

</style>
