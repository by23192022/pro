import router from '@ohos.router'
import promptAction from '@ohos.promptAction'
import { createPost, login, sendCode } from '../http/Api'
import emitter from '@ohos.events.emitter'


@Entry
@Component
struct LoginPage {
  @State phone:string=''
  @State code:string=''
  //用于存储登录后的token字符串
  @StorageLink('token') token: string  = ''
  //用于存储Tabs组件当前访问的子页面的索引
  @StorageLink('currentTabIndex') currentTabIndex: number = 0

  build() {
    Column() {
      //1、 左上角的返回按钮------------------------------------------------
      Image($r('app.media.ic_back'))
        .backStyle()
        .alignSelf(ItemAlign.Start)
        .onClick(() => {
          //todo:返回上一页面
          // 补全
          router.back()
        })

      Blank()

      //2、登录表单----------------------------------------------------------------
      Column() {
        Text('欢迎登录')
          .titleStyle()

        //手机号输入框
        Row() {
          Image($r("app.media.ic_phone"))
            .iconStyle()
          TextInput({ placeholder: '请输入手机号码',text:this.phone })
            .inputStyle()
            .onChange((value)=>{
              this.phone=value;
            })
        }.margin({ top: 30 })

        Divider()
          .color(Color.Black)

        //验证码输入框
        Row() {
          Image($r("app.media.ic_code"))
            .iconStyle()
          TextInput({ placeholder: '请输入验证码',text:this.code })
            .inputStyle()
            .onChange((value)=>{
              this.code=value;
            })
          Button('获取验证码')
            .buttonStyle(Color.White, Color.Black)
              // 5.5. 获取验证码功能
            .onClick(async () => {
              //todo:获取验证码
              //获取验证码
              const response = await sendCode(this.phone)
              this.code = response.data.data
            })

        }.margin({ top: 20 })

        Divider()
          .margin({ right: 120 })
          .color(Color.Black)

        Button('立即登录')
          .buttonStyle(Color.Black, Color.White)
          .width('100%')
          .margin({ top: 50 })
          // 5.6. 登录功能的实现
          .onClick(async () => {
            //todo:登录
            //登录
            const response = await login(this.phone, this.code)
            this.token = response.data.data

            //打卡功能后续补充
            // 6.1. 场景1：点击打卡弹窗的“登录打卡”
            //如果是从答题页面跳转过来，则在登录后马上实现打卡功能
            if (this.currentTabIndex === 0) {
              // todo: 发送打卡请求
              //(1)获取从打卡弹窗传递过来的数据（答对题目数量、回答题目数量、使用时长）
              const params = router.getParams()
              //(2)请求后台API，完成打卡数据存储
              await createPost({
                rightCount: params['rightCount'],
                answeredCount: params['answeredCount'],
                timeUsed: params['timeUsed']
              })
              promptAction.showToast({ message: '打卡成功' })
              // 通知打卡圈
              emitter.emit({ eventId: 1 })
              // 打卡完成后跳转到打卡圈
              this.currentTabIndex = 1
            }

            //登录完成后返回上一个页面
            router.back()
          })

        Row() {
          Text('登录即表示已同意')
            .fontSize(10)
            .fontColor('#546B9D')
          Text('《用户使用协议》')
            .fontSize(10)
            .fontColor('#00B3FF')
        }.margin({ top: 20 })
      }.formBgStyle()


      //3、页面底部的logo与文本区域----------------------------------------------------------------
      Row({ space: 10 }) {
        Image($r('app.media.ic_logo'))
          .width(36)
          .height(36)
        Text('快速记单词神器')
          .fontColor('#546B9D')
          .fontWeight(FontWeight.Bold)
          .fontSize(20)
      }.margin({ top: 70 })

      Text('Developed By Togogo')
        .fontSize(12)
        .fontColor('#546B9D')
        .margin(10)
    }
    .loginBgStyle()
  }
}

@Styles function loginBgStyle() {
  .width('100%')
  .height('100%')
  .backgroundImage($r("app.media.img_login_bg"))
  .backgroundImageSize({ width: '100%', height: '100%' })
  .padding({
    top: 30, bottom: 30, left: 20, right: 20
  })
}

@Styles function backStyle() {
  .width(25)
  .height(25)
}

@Styles function formBgStyle() {
  .backgroundColor(Color.White)
  .padding(30)
  .borderRadius(20)
}

@Extend(Text) function titleStyle() {
  .fontWeight(FontWeight.Bold)
  .fontSize(22)
}

@Styles function iconStyle() {
  .width(24)
  .height(24)
}

@Extend(TextInput) function inputStyle() {
  .height(40)
  .layoutWeight(1)
  .fontSize(14)
  .backgroundColor(Color.Transparent)
}

@Extend(Button) function buttonStyle(bgColor: ResourceColor, fontColor: ResourceColor) {
  .type(ButtonType.Normal)
  .fontSize(14)
  .fontWeight(FontWeight.Medium)
  .borderWidth(1)
  .borderRadius(5)
  .backgroundColor(bgColor)
  .fontColor(fontColor)
}