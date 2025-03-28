import router from '@ohos.router';

//6. 打卡功能。添加token数据的永久存储
// 变量持久化
PersistentStorage.PersistProp('token', '')

//在AppStorage中创建一个currentTabIndex数据
AppStorage.SetOrCreate('currentTabIndex', 0)



@Entry
@Component
struct SplashPage {
  //动画组件是否展示，初始化不展示
  @State flag: boolean = false;

  onPageShow() {

    //执行动画的参数与触发执行的事件
    animateTo({
      duration: 1500,
      onFinish: () => {
        setTimeout(() => {
          router.replaceUrl({
            url: 'pages/Index'
          })
        }, 200)
      }
    },
      () => {
        this.flag = true
      })

  }

  build() {
    Column() {

      if (this.flag) {
        Image($r('app.media.ic_logo'))
          .width(90)
          .height(90)
          .margin({ top: 100 })
            //指定动画效果
          .transition({ type: TransitionType.Insert, opacity: 0, translate: { x: -150 } })

        Text('快速单词记忆神器')
          .fontSize(21)
          .fontColor(Color.White)
          .fontWeight(FontWeight.Bold)
          .margin({ top: 15 })
            //指定动画效果
          .transition({ type: TransitionType.Insert, opacity: 0, translate: { x: 150 } })
      }

      Blank()

      Text('Welcome')
        .fontSize(12)
        .fontColor('#ffe5e3e3')
        .fontWeight(FontWeight.Bold)
        .margin({ bottom: 30 })

    }
    .width('100%')
    .height('100%')
    .backgroundImage($r('app.media.img_splash_bg'))
    .backgroundImageSize({ width: '100%', height: '100%' })
  }
}