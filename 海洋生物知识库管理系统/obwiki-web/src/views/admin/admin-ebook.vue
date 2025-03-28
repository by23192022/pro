<template>
  <div>

  <a-layout>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >

      <p>
        <a-form layout="inline" :model="param">

          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              查询
            </a-button>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>

<!--      :scroll="{ x: 1300, y: 1000 }"-->
      <a-table
        :columns="columns"
        :row-key="record => record.id"
        :data-source="ebooks"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record}">
          <template v-if="column.key === 'cover'">
            <a-image   :src="record.cover" alt="图片加载失败" style="width:80px;height:80px"/>
          </template>

          <template v-if="column.key === 'category'">
            <span>{{getCategoryName(record.category1Id)}}/{{getCategoryName(record.category2Id)}}</span>
          </template>

          <template v-if="column.dataIndex === 'action'">
            <a-space size="small">

<!--              点击某个电子书->进行该电子书的文档管理-->
              <a-button type="primary"  >
                <router-link :to="{path:'/admin/doc', query: {ebookId:record.id}}"> 文档管理</router-link>
              </a-button>

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
      title="电子书表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
    >
      <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="封面">
          <a-input v-model:value="ebook.cover" />
        </a-form-item>
        <a-form-item label="名称">
          <a-input v-model:value="ebook.name" />
        </a-form-item>

        <a-form-item label="分类">
          <a-cascader v-model:value="categoryIds"
                      :field-names="{ label: 'name', value: 'id', children: 'children' }"
                      :options="level1"  />
        </a-form-item >

        <a-form-item label="描述">
          <a-textarea v-model:value="ebook.description" type="textarea" />
        </a-form-item>
      </a-form>
    </a-modal>

  </div>

</template>

<script lang="ts" setup>
  import axios from 'axios';
  import { ref, onMounted } from 'vue';

  import { Tool } from "@/utils/tool";

  import {message} from "ant-design-vue";



  const ebooks = ref();//定义查询电子书返回集合

  // 编辑相关功能
  const ebook = ref();
  const modalVisible = ref(false);
  const modalLoading = ref(false);

  //条件查询
  const param = ref();
  param.value = {};

  //定义分页参数
  const pagination = ref({
    current: 1,
    pageSize: 5,
    total: 0
  });
  const loading = ref(false);
  const columns = [
    {
      title: 'id',
      key:'id',
      dataIndex: 'id',

    },
    {
      title: '封面',
      key:'cover',
      dataIndex: 'cover',

    },
    {
      title: '名称',
      dataIndex: 'name'
    },
    {
      title: '分类',
      key:'category',
      dataIndex: 'category'
    },
    {
      title: '文档数',
      dataIndex: 'docCount'
    },
    {
      title: '阅读数',
      dataIndex: 'viewCount'
    },
    {
      title: '点赞数',
      dataIndex: 'voteCount'
    },
    {
      title: 'Action',
      key: 'action',
      dataIndex:'action',
      width: "20%"

    }
  ];


  //查询分类数据级联展示
  const categoryIds = ref(); //保存选中分类数组[100:101] 对应海洋植物:藻类植物
  const level1 = ref();//保存分类数据
  let categorys:any;
  //let categoryIds_num :any;   //中间变量，edit()中改变categoryIds的值为name，handleModalOk()需要又获取categoryIds

  onMounted(() => {

    handleQueryCategory();

  })

  /*
  * 数据查询
  * */
  const  handleQuery = (params:any)=>{
    loading.value = true;
    axios.get("/ebook/list",{
      params:{
        page:params.page,
        size:params.size,
//修改查询方法 添加name参数
        name:param.value.name
      }}).then((resp)=>{
      loading.value = false;
      const  data = resp.data;
      if (data.success){
        //获取查询数据
        ebooks.value = data.content.list;

        //console.log("============ebooks==="+data.content.list.id);

        //重置分页按钮
        pagination.value.current = params.page;
        //设置总条数
        pagination.value.total = data.content.total;
      }
    });
  };
  /*
  * 表格点击页码时触发
  * */
  const handleTableChange =(pagination:any)=>{
    console.log("看看自带分页的参数都有些啥："+pagination);
    handleQuery({
      page:pagination.current,
      size:pagination.pageSize
    });

  };


  //编辑
  const edit = (record:any)=>{
    modalVisible.value =true;
    // ebook.value = record;
    ebook.value = Tool.copy(record);

    /*赋值以初始化categoryIds类型为[]。
    // 避免每次都初始化一遍，加if。
    // （初始化放到外面的话，因为add()那里赋了null，这里要赋[0,0]，会冲突，故加if。
*/
    if(categoryIds.value == null){
      categoryIds.value = ["0","0"];
    }

    /* NO
    // categoryIds_num = categoryIds.value;
    categoryIds_num[0] = record.category1Id;
    categoryIds_num[1] = record.category2Id;
     */


    /*首次打开categoryIds = ref()，还未被查询方法赋值、类型。下面需要使用[0]、[1]
*/
    //if(categoryIds.value != null){
      //显示为当前ebook的分类名
      //categoryIds.value[0] = getCategoryName(data.category1Id);
      categoryIds.value[0] = getCategoryName(record.category1Id);
      categoryIds.value[1] = getCategoryName(record.category2Id);
    //}

  }

  //保存 。按下保存按钮触发
  const handleModalOk = ()=>{
    modalLoading.value = true;

    //获取分类1 及分类2的值
    ebook.value.category1Id = categoryIds.value[0];
    ebook.value.category2Id = categoryIds.value[1];
    /*ebook.value.category1Id = getCategoryNum(categoryIds.value[0]);
      ebook.value.category2Id = getCategoryNum(categoryIds.value[1]);*/
    /*ebook.value.category1Id = categoryIds_num[0];
      ebook.value.category2Id = categoryIds_num[1];*/
    //console.log(ebook.value);

    axios.post("/ebook/save",ebook.value).then(resp =>{
      //回去返回参数
      const data = resp.data;
      if(data.success){
        modalLoading.value = false;//关闭等待
        modalVisible.value = false;//关闭对话框
        //重新加载列表
        handleQuery({
          page:pagination.value.current,
          size:pagination.value.pageSize
        })

        /*
        *错。需要，按下保存按钮前，显示分类名称。
        //////////////////（1）效果可
        //categoryIds.value = 0;//重置分类选择器内容，0显示为空
        * categoryIds.value = [0, 0];
        //////////////////（2）
        //categoryIds.value[0] = getCategoryName(data.category1Id);//显示为当前ebook的分类名
        //categoryIds.value[1] = getCategoryName(data.category2Id);
         */

      } else {
        message.error(data.message);
      }
    })
  }

  //新增
  const add = ()=>{
    modalVisible.value =true;
    //清空对话框数据
    ebook.value = {};

    categoryIds.value = null;
  }

  //删除
  const handleDelete = (id:number)=>{
    axios.delete('/ebook/delete/'+id).then((resp)=>{
      if (resp.data.success){
        //重新加载列表
        handleQuery({
          page:pagination.value.current,
          size:pagination.value.pageSize
        });
      }
    }
      // , error => {
      //   message.error("waiting...");
      //   return error;
      // }
    )
  }


  //查询分类数据级联展示
  const  handleQueryCategory = ()=>{
    loading.value = true;
    axios.get("/category/all").then((resp)=>{
      loading.value = false;
      const  data = resp.data;
      categorys = data.content;

      if (data.success){
        console.log("原始数组",categorys);

        level1.value = [];
        level1.value = Tool.array2Tree(categorys,0);
        console.log("树形结构：",level1);
        //加载完分类后，再加载电子书，否则如果分类树加载很慢，则电子书的渲染会报错
        handleQuery({
          page:1,
          size:pagination.value.pageSize
        });
      } else {
        message.error(data.message);
      }
    });
  };

  /*分析cid
  上文调用为：
 <span>{{getCategoryName(record.category1Id)}}/{{getCategoryName(record.category2Id)}}</span>
  *
  * *cid:number可能引发的问题：
  本文的雪花算法会生成18位的id，number类型最大能表示~16位id，导致无法接收到cid值
 */
  //或const getCategoryName = (cid:number)=>{
    const getCategoryName = (cid:string)=>{

    //cid = "504975452461469696";  //一个雪花算法会生成的id

    //传递的cid为number类型 与categorys中的id为string类型 需要修改
    let cidstr:string = cid.toString();
    let result = "";

    /*分析item
    const  handleQueryCategory = ()=>{}内，为categorys赋值
    另外定义是，let categorys:any;
    故categorys存储的数据是any类型。
    *
    *JSON数据是string类型
     */
    //错categorys.forEach((item:string)=>{
    categorys.forEach((item:any)=>{
      if (item.id == cidstr){

        /*
        console.log("============item"+item.id);
        console.log("============ cid"+cid);
        //item、cid都没有存到雪花算法会生成的id
        vue前端处理 >16位数据精度丢失问题：category1Id、category2Id没加@JsonSerialize。
        */

        result = item.name;
      }
    });
    return result;
  }

  const getCategoryNum = (cidstr:string)=>{

    let result = 0;

    categorys.forEach((item:any)=>{
      if (item.name == cidstr){
        result = item.id;
      }
    });
    return result;
  }



</script>


<style scoped>
th.column-money,
td.column-money {
  text-align: right !important;
}
</style>

