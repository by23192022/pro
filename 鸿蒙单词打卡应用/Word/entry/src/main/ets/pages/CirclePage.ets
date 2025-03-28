// A page configured in 'main_pages.json' must have one and only one '@Entry' decorator.
//'main_pages.json'  删除  "pages/CirclePage",

import router from '@ohos.router';
import { PostInfo } from '../model/PostInfo'
import { StatItem } from '../pages/PracticlePage'
import { convertMillisecondsToTime } from '../utils/DateUtil'
import { like,cancelLike, getAllPost } from '../http/Api'
import promptAction from '@ohos.promptAction';
import emitter from '@ohos.events.emitter';

// CirclePage显示异常：点击“圈子”，一直加载中...
// 问题：65行 refresh()函数里的getPostInfoPage()一直占用，isLoading 恒= true导致的。
// 解决：去除await关键字
// 原因：
//
// 添加↓@Entry。并且修改'main_pages.json'，加回  "pages/CirclePage",
// 有没有（ @Entry 和 "pages/CirclePage", ）都不影响！！！！！！！
@Entry
@Component
export struct CirclePage {
  @StorageProp('token') @Watch('onTokenChange') token: string = ''
  page: number = 1
  total: number = 0
  @State isLoading: boolean = false
  scroller: Scroller = new Scroller()
  @State postInfoList: PostInfo[] = []
 /* @State postInfoList: PostInfo[] = [new PostInfo({
    id: 1,
    postText: "既然选择远方，当不负青春，砥砺前行",
    rightCount: 3,
    answeredCount: 4,
    timeUsed: 5747,
    createTime: "2024-03-19 18:54:33",
    likeCount: 1,
    nickname: "138****8888",
    avatarUrl: "https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg",
    isLike: false
  })]*/


  //获取打卡数据的方法，默认获取第1页，每页10条数据
  async getPostInfoPage() {
    let response = await getAllPost(this.page, 10)
    response.data.data.records.forEach(post => this.postInfoList.push(new PostInfo(post)))
    this.total = response.data.data.total
    this.page++
  }

  //监听登录token发生改变
  onTokenChange() {
    if (this.token) {
      this.getPostInfoPage()
    } else {
      this.postInfoList = []
      this.total = 0
      this.page = 1
    }
  }

  //刷新打卡记录方法
  async refresh() {
    this.isLoading = true
    this.postInfoList = []
    this.page = 1
    this.total = 0
    // await this.getPostInfoPage()         /////////////
    this.getPostInfoPage()                  /////////////
    this.isLoading = false
  }

  //生命周期函数，在组件创建时调用，执行build()方法之前
  aboutToAppear() {
    if (this.token) {
      // console.log('1111111111')
      // console.log(JSON.stringify('1111111111'))
      // console.log("11111" + this.token.toString())

      console.log("11111" + JSON.stringify(this.token))
      // console.log("11111"+"22222")
      this.getPostInfoPage()

    }

    //订阅与打卡事件（即eventId为1的时间），接受到打卡事件后，调用refresh()方法刷新打卡数据
    emitter.on({ eventId: 1 }, () => {
      this.refresh()
    })
  }


  build() {
    Column() {
      Text('英语打卡圈')
        .fontSize(18)
        .margin({ top: 45 })
        .fontWeight(FontWeight.Bold)
      Divider()
        .color(Color.Black)
        .margin({ left: 20, right: 20, top: 10 })
      if (this.token) {
        // 打卡列表
        if (this.isLoading) {

          this.loadingBuilder()
        } else {
          this.listBuilder()
        }
      } else {
        //todo:未登录
        this.unLoginBuilder()
      }
    }.height('100%')
    .width('100%')
  }

  //未登录页面UI
  @Builder
  unLoginBuilder() {
    Column({ space: 30 }) {
      Image($r("app.media.ic_unLogin_bg"))
        .width(177)
        .height(177)

      Text('未登录暂无数据')
        .fontSize(14)
        .fontColor('#999999')

      Button('去登录')
        .fontColor(Color.White)
        .fontSize(14)
        .width(100)
        .height(34)
        .backgroundColor('#43C6A0')
        .onClick(() => router.pushUrl({ url: 'pages/LoginPage' }))
    }
    .width('100%')
    .layoutWeight(1)
    .justifyContent(FlexAlign.Center)
  }

  //已登录页面UI
  @Builder
  listBuilder() {
    Stack() {
      //打卡列表--------------------------------------------------
      List({ scroller: this.scroller }) {
        ForEach(this.postInfoList, (post) => {
          ListItem() {
            PostItem({ post: post })
          }
        })
      }.width('100%')
      .height('100%')
      .alignListItem(ListItemAlign.Center)
      .onReachEnd(() => {
        if (this.postInfoList.length < this.total) {
          this.getPostInfoPage()
        } else {
          promptAction.showToast({ message: '已经到底啦' })
        }
      })

      //控制按钮------------------------------------------
      Column({ space: 20 }) {
        //置顶按钮---------------------------------------
        Button({ type: ButtonType.Circle }) {
          Image($r('app.media.ic_top'))
            .height(14)
            .width(14)
        }
        .height(40)
        .width(40)
        .backgroundColor(Color.Black)
        .opacity(0.5)
        .onClick(() => {
          //todo:返回顶部
          this.scroller.scrollToIndex(0)
        })

        //刷新按钮---------------------------------------
        Button({ type: ButtonType.Circle }) {
          Image($r('app.media.ic_refresh'))
            .height(14)
            .width(14)
        }
        .height(40)
        .width(40)
        .backgroundColor(Color.Black)
        .opacity(0.5)
        .onClick(() => {
          //todo:刷新
          this.refresh()
        })
      }
      .offset({ x: -20, y: -50 })
    }.width('100%')
    .layoutWeight(1)
    .alignContent(Alignment.BottomEnd)
  }


  //加载组件---------------------------------------------
  @Builder
  loadingBuilder() {
    Column({ space: 15 }) {
      Image($r('app.media.ic_loading'))
        .width(30)
        .height(30)
      Text('加载中...')
        .fontSize(16)
        .fontWeight(FontWeight.Medium)
        .fontColor('#7e8892')
    }.width('100%')
    .layoutWeight(1)
    .justifyContent(FlexAlign.Center)
  }
}


//打卡记录子组件
@Component
struct PostItem {
  @ObjectLink post: PostInfo;

  build() {
    //用户信息展示区域--------------------------------------
    Column({ space: 10 }) {
      Row({ space: 10 }) {
        Image(this.post.avatarUrl)
          .height(40)
          .width(40)
          .borderRadius(20)
        Text(this.post.nickname)
          .height(40)
          .fontSize(14)
          .fontWeight(FontWeight.Bold)
        Blank()
        Text(this.post.createTime)
          .height(40)
          .fontSize(14)
          .fontColor('#999999')
          .fontWeight(FontWeight.Medium)
      }.width('100%')

      //打卡语句展示区域--------------------------------------
      Text(this.post.postText)
        .width('100%')

      //打卡信息展示区域--------------------------------------
      Row() {
        Column() {
          StatItem({
            icon: $r('app.media.ic_timer_white'),
            name: '用时',
            fontColor: Color.White }) {
            Text(convertMillisecondsToTime(this.post.timeUsed))
              .statTextStyle()
          }

          StatItem({
            icon: $r('app.media.ic_accuracy_white'),
            name: '准确率',
            fontColor: Color.White
          }) {
            Text((this.post.answeredCount === 0 ? 0 : this.post.rightCount / this.post.answeredCount * 100).toFixed(0) + '%')
              .statTextStyle()
          }

          StatItem({
            icon: $r('app.media.ic_count_white'),
            name: '个数',
            fontColor: Color.White
          }) {
            Text(this.post.answeredCount.toString())
              .statTextStyle()
          }
        }
        .padding(10)
        .borderRadius(10)
        .layoutWeight(1)
        .backgroundImage($r('app.media.img_post_bg'))
        .backgroundImageSize(ImageSize.Cover)
        //点赞按钮区域
        Column() {
          Text(this.post.likeCount.toString())
            .fontSize(12)
            .fontWeight(FontWeight.Medium)
            .fontColor(this.post.isLike ? '#3ECBA1' : '#000000')
          Image(this.post.isLike ? $r('app.media.ic_post_like_selected') : $r('app.media.ic_post_like'))
            .width(26)
            .height(26)
            .onClick(() => {
              //todo：点赞/取消点赞
              //todo：点赞/取消点赞
              if (this.post.isLike) {
                // 取消点赞
                this.post.isLike = false
                this.post.likeCount--
                //需要在从Api.ets文件中导入cancelLike方法
                //调用cancelLike()方法，向后台发起取消点赞的请求
                cancelLike(this.post.id)
              } else {
                // 点赞
                this.post.isLike = true
                this.post.likeCount++
                //需要在从Api.ets文件中导入like方法
                //调用like()方法，向后台发起点赞的请求
                like(this.post.id)
              }
            })
        }.width(50)
      }.width('100%')
      .alignItems(VerticalAlign.Bottom)
    }
    .padding(10)
    .width('90%')
    .margin({ top: 10 })
    .borderRadius(10)
    .shadow({ radius: 20 })
  }
}


@Extend(Text) function statTextStyle() {
  .width(100)
  .fontSize(16)
  .textAlign(TextAlign.End)
  .fontWeight(FontWeight.Medium)
  .fontColor(Color.White)
}
