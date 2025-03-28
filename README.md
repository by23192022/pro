# 简要项目介绍

## 1、鸿蒙单词打卡应用

2024.09.02-2024.09.13

### 项目展示

|          |                                                              |                                                              |                                                              |
| -------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **操作** | 开始                                                         | 结束                                                         | 登陆打卡                                                     |
| **显示** | ![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps8.jpg) | ![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps9.jpg) | ![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps10.jpg) |
| **操作** | 打卡圈、刷新、点赞                                           | 我的                                                         | 打卡记录                                                     |
| **显示** | ![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps11.jpg) | ![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps12.jpg) | ![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps13.jpg) |

### 项目说明

​		该App主要分为三个功能模块：答题模块、打卡圈和个人中心。答题模块包括答题、统计和打卡功能；打卡圈模块包括打卡列表和点赞功能；个人中心模块包括登录、退出登录和查看个人打卡记录功能。

|          |                                                              |                                                              |                                                              |
| -------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **模块** | 答题模块                                                     | 打卡圈                                                       | 个人中心                                                     |
| **图示** | ![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps32.jpg) | ![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps33.jpg) | ![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps34.jpg) |
| **功能** | 答题、统计、打卡                                             | 打卡列表、点赞                                               | 登录、退出登录、个人打卡记录                                 |

**页面结构**

- pages
  - Index.etc							整体页面
  - PracticlePage.etc			  答题页面
  - SplashPage.etc				 欢迎页面
  - LoginPage.etc				   登录页面
  - CirclePage.etc				   打卡圈页面
  - MinePage.etc				    个人中心页面
  - PostHistoryPage.etc		个人打卡记录页面



## 2、海洋生物知识库项目

2024.10.14-2024.11.8

### 项目展示

（1）首页分类显示

![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps1.jpg) 

（2）用户管理

![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps2.jpg) 

（3）海洋生物电子书页面管理

![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps3.jpg) 

（4）海洋生物文档管理

![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps4.jpg) 

（5）海洋生物分类管理

![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps5.jpg) 

（6）海洋生物文档阅读点赞功能

![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps6.jpg) 

（7）海洋生物知识库统计功能

![img](file:///C:\Users\ADMINI~1\AppData\Local\Temp\ksohtml19792\wps7.jpg) 

### 项目说明

| 模块                                                       | 主要内容                                                     |
| ---------------------------------------------------------- | ------------------------------------------------------------ |
| 海洋生物电子书页面管理                                     | 包括对电子书数据的模糊查询、新增、编辑、删除功能，使用了SnowFlake雪花算法生成18位数ID，解决了Vue超过16位数字解析精度丢失问题。 |
| 海洋生物文档管理                                           | 包括对电子书文档数据的新增、编辑、删除，还使用到了树形组件a-tree-select和富文本编辑器wangEditor。 |
| 海洋生物分类管理、首页显示左侧分类菜单                     | 包括对电子书分类数据的新增、编辑、删除，使用到了级联选择器Cascader显示分类下拉菜单，解决了分类id和分类名称转换的问题。 |
| 用户管理功能、海洋生物文档阅读点赞及海洋生物知识库统计功能 | 包括对用户表基本增删改查功能、Md5密码加密、登录校验，使用到了redis、VueX、SessionStorage存储用户信息。包括电子书/文档阅读数更新和文档点赞、定时更新电子书信息和收集电子书快照，使用到了WebSocket连接、RocketMQ消息队列。 |