<template>
  <div>
    <a-layout>
      <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
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
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          :pagination="false"
          :loading="loading"
          @change="handleTableChange"
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

      </a-layout-content>
    </a-layout>


    <a-modal
      title="电子书分类表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
    >
      <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

        <a-form-item label="名称">
          <a-input v-model:value="category.name" />
        </a-form-item>

        <a-form-item label="父分类">
          <a-select
            ref="select"
            v-model:value="category.parent"
            style="width: 120px"
          >
            <a-select-option value="0">无</a-select-option>
            <a-select-option v-for="c in level1"
                             :key="c.id"
                             :value="c.id"
                             :disabled="category.id === c.id"
            >{{c.name}}
            </a-select-option>
          </a-select>

        </a-form-item>

        <a-form-item label="排序">
          <a-input v-model:value="category.sort" />
        </a-form-item>

      </a-form>
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import axios from 'axios';
import { ref, onMounted } from 'vue';
//import { Tool } from "@/utils/tool.ts";
import { Tool } from "@/utils/tool";

import { message } from 'ant-design-vue';


const categorys = ref();//定义查询电子书返回集合
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
    title: 'id',
    dataIndex: 'id'
  },
  {
    title: '父分类',
    dataIndex: 'parent'
  },
  {
    title: '顺序',
    dataIndex: 'sort'
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
//数据查询 查询不携带参数
//定义树形结构 数组
const level1 = ref();
const  handleQuery = ()=>{
  loading.value = true;
  axios.get("/category/all").then((resp)=>{
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
/*
* 表格点击页码时触发
*
const handleTableChange =(pagination:any)=>{
  console.log("看看自带分页的参数都有些啥："+pagination);
  handleQuery({
    page:pagination.current,
    size:pagination.pageSize
  });
};*/

// 编辑相关功能
const category = ref();
const modalVisible = ref(false);
const modalLoading = ref(false);
//编辑
const edit = (record:any)=>{
  modalVisible.value =true;
  category.value = Tool.copy(record);
}
//新增
const add = ()=>{
  modalVisible.value =true;
  category.value = {};
}

//删除
const handleDelete = (id:bigint)=>{
  console.log("id:",id)

  let array = level1.value;
  let len = 0;//子节点数组长度
  for (let i = 0; i < array.length; i++) {
    let c = array[i];
    if (String(c.id) === String(id)){
      if(array[i].children){
        //找到该分类 判断是否存有子节点
        len = array[i].children.length
      }
    }
  }

  if(len>0){
    message.warning("该节点包含子节点,不能删除");
  }else{

    axios.delete('/category/delete/'+id).then((resp)=>{
      if (resp.data.success){
        //重新加载列表
        handleQuery();
      }
    })

  }
}

//保存
const handleModalOk = ()=>{
  modalLoading.value = true;
  axios.post("/category/save",category.value).then(resp =>{
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

</script>
<style scoped>

</style>
