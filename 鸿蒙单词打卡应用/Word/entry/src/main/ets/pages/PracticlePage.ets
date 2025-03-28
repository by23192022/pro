import { PracticeStatus } from '../enums/PracticeStatus'
import {AnswerStatus} from '../enums/AnswerStatus'
import {OptionStatus} from '../enums/OptionStatus'

import { Question, getRandomQuestions } from '../model/Question'
import promptAction from '@ohos.promptAction'
import {convertMillisecondsToTime} from '../utils/DateUtil'
import router from '@ohos.router'
import { createPost } from '../http/Api'
import emitter from '@ohos.events.emitter'


@Entry
@Component
export struct PracticlePage {
  //存储当前练习状态的变量
  @State practiceStatus: PracticeStatus = PracticeStatus.Stopped

  //存储题目个数的变量
  @State totalCount: number = 10

  //存储题目的数组，调用model中的随机获取题目方法获取题目
  @State questions: Question[] = getRandomQuestions(this.totalCount)

  //存储当前题目的索引
  @State currentIndex: number = 0

  //存储答题状态
  @State answerStatus: AnswerStatus = AnswerStatus.Answering

  //存储当前题目选择的答案
  @State selectedOption: string = ''


  @State answeredCount: number = 0

  @State rightCount: number = 0

  timeUsed:number = 0

  //定义一个定时器控制器
  timerController: TextTimerController = new TextTimerController();

  dialogController: CustomDialogController = new CustomDialogController({
    builder: ResultDialog({
      answeredCount: this.answeredCount,
      rightCount: this.rightCount,
      timeUsed: this.timeUsed,
      resetPractice: () => {
        this.questions = getRandomQuestions(this.totalCount)
        this.currentIndex = 0
        this.answeredCount = 0
        this.rightCount = 0
        this.timeUsed = 0
        this.timerController.reset()
        this.answerStatus = AnswerStatus.Answering
      },
      startPractice: () => {
        this.practiceStatus = PracticeStatus.Running
        // 启动计时器
        this.timerController.start()
      }
    }),
    customStyle: true,
    autoCancel: false
  })

  build() {
    Column({ space: 40 }) {
      //1、统计面板区域
      Column() {
        //准确率
        Row() {
          StatItem({
            icon: $r('app.media.ic_accuracy'),
            name: '准确率',
            fontColor: Color.Black,
          }) {
            Text((this.answeredCount === 0 ? 0 : this.rightCount / this.answeredCount * 100).toFixed() + '%')
              .width(100)
              .textAlign(TextAlign.Center)
          }
        }

        //进度
        Row() {
          StatItem({
            icon: $r('app.media.ic_progress'),
            name: '进度',
            fontColor: Color.Black,
          }) {
            Progress({ value: this.answeredCount, total: this.totalCount })
              .width(100)
          }
        }

        //个数
        Row() {
          StatItem({
            icon: $r('app.media.ic_count'),
            name: '个数',
            fontColor: Color.Black,
          }) {
            Button(this.totalCount.toString())
              .width(100)
              .height(25)
              .backgroundColor('#EBEBEB')
              .fontColor(Color.Black)
              .enabled(this.practiceStatus === PracticeStatus.Stopped)
              .onClick(() => {
                TextPickerDialog.show({
                  range: ['5', '10', '15', '20'],
                  value: this.totalCount.toString(),
                  onAccept: (result) => {
                    this.totalCount = parseInt(result.value)
                    this.questions = getRandomQuestions(this.totalCount)
                  }
                })

              })
          }
        }

        //用时
        Row() {
          StatItem({
            icon: $r('app.media.ic_timer'),
            name: '用时',
            fontColor: Color.Black,
          }) {
            TextTimer({ controller: this.timerController })
              .onTimer((utc, elapsedTime) => {
                this.timeUsed = elapsedTime
              })
          }
        }

      }
      .margin(30)
      .stateBgStyle()

      //--统计面板区域结束-------------------------------------------------
      //2、单词展示区域
      // 题目
      Column() {
        Text(this.questions[this.currentIndex].word)
          .wordStyle()
        Text(this.questions[this.currentIndex].sentence)
          .sentenceStyle()
      }

      //--单词展示区域结束-------------------------------------------------
      //3、选项按钮区域
      Column({ space: 10 }) {
        //循环遍历当前题目的答案选项数组，生成选项按钮
        ForEach(this.questions[this.currentIndex].options,(option)=>{
          optionButtonItem({
            option:option,
            answer: this.questions[this.currentIndex].answer,
            selectedOption: this.selectedOption,
            // 307行 报错：The '@Prop' decorated attribute 'answerStatus' must be of the string, number, or boolean type. <etsLint>
            // answerStatus: this.answerStatus
            answerStatus: this.answerStatus as number

          })
            .enabled(this.answerStatus === AnswerStatus.Answering)
            .onClick(()=>{
              // 判断练习状态
              if (this.practiceStatus !== PracticeStatus.Running) {
                promptAction.showToast({ message: '请先点击开始测试' })
                return
              }

              // 判断正误
              this.selectedOption = option

              //切换当前题目答题状态
              this.answerStatus = AnswerStatus.Answered

              // 更新统计信息
              this.answeredCount++
              if (option === this.questions[this.currentIndex].answer) {
                this.rightCount++
              }

              //切换题目
              if(this.currentIndex<this.questions.length-1){
                setTimeout(() => {
                  this.currentIndex++
                  this.answerStatus = AnswerStatus.Answering
                  this.selectedOption = ''
                }, 500)
              }
              else{
                //停止计时器
                this.timerController.pause()
                //切换练习状态
                this.practiceStatus = PracticeStatus.Stopped

                //打开弹窗
                this.dialogController.open()
              }
            })
        },
          option => this.questions[this.currentIndex].word + '-' + option)
      }

      //--选项按钮区域结束-------------------------------------------------
      //4、控制按钮区域
      Row({ space: 40 }) {
        Button('结束测试').controlButtonStyle({
          bg: Color.Transparent,
          border: this.practiceStatus === PracticeStatus.Stopped ? Color.Gray : Color.Black,
          font: this.practiceStatus === PracticeStatus.Stopped ? Color.Gray : Color.Black
        })
          .enabled(this.practiceStatus !== PracticeStatus.Stopped)
          .onClick(() => {
            //停止计时器
            this.timerController.pause();
            //切换练习状态
            this.practiceStatus = PracticeStatus.Stopped

            //打开弹窗
            this.dialogController.open()
          })


        Button(this.practiceStatus === PracticeStatus.Running ? '暂停测试' : '开始测试')
          .controlButtonStyle({
            bg: this.practiceStatus === PracticeStatus.Running ? '#555555' : Color.Black,
            border: this.practiceStatus === PracticeStatus.Running ? '#555555' : Color.Black,
            font: Color.White
          })
          .stateEffect(false)
          .onClick(() => {
            if (this.practiceStatus === PracticeStatus.Running) {
              //停止计时器
              this.timerController.pause();
              //切换练习状态
              this.practiceStatus = PracticeStatus.Paused
            }
            else {
              //开始计时器
              this.timerController.start();
              //切换练习状态
              this.practiceStatus = PracticeStatus.Running
            }
          })
      }
    }
    .practiceBgStyle()
    .justifyContent(FlexAlign.Start)
  }
}

@Extend(Column) function practiceBgStyle() {
  .width('100%')
  .height('100%')
  .backgroundImage($r('app.media.img_practice_bg'))
  .backgroundImageSize({ width: '100%', height: '100%' })
  .justifyContent(FlexAlign.SpaceEvenly)
}

@Styles function stateBgStyle() {
  .backgroundColor(Color.White)
  .width('90%')
  .borderRadius(10)
  .padding(20)
}


//统计区域子组件
@Component
// 6.3.1.3. 完整页面布局代码。在其前面加上“export”关键字将其导出。
export struct StatItem {
  icon: Resource;
  name: string;
  @BuilderParam statComp: () => void;
  fontColor: Color

  build() {
    Row({ space: 10 }) {
      Image(this.icon)
        .height(14)
        .width(14)
      Text(this.name)
        .fontWeight(FontWeight.Medium)
        .fontSize(14)
        .fontColor(this.fontColor)
      Blank()
      this.statComp()
    }
    .width('100%')
    .height(30)
  }
}


//选项按钮子组件
@Component
struct optionButtonItem {
  option: string
  answer: string
  @State optionStatus: OptionStatus = OptionStatus.Default
  @Prop selectedOption: string
  // 163行 传参
  // answerStatus  报错：The '@Prop' decorated attribute 'answerStatus' must be of the string, number, or boolean type. <etsLint>
  //@Prop @Watch('onAnswerStatusChange') answerStatus: AnswerStatus
  @Prop @Watch('onAnswerStatusChange') answerStatus: number
  //
  // Teacher方案：
  // (1) 307行改成@Link @Watch('onAnswerStatusChange') answerStatus: AnswerStatus
  // 对应163行改成 answerStatus: $answerStatus
  // 且(2) 304行改成@Link selectedOption: string
  // 对应  行改成  $


  onAnswerStatusChange() {
    if (this.option === this.answer) {
      this.optionStatus = OptionStatus.Right
    } else {
      if (this.option === this.selectedOption) {
        this.optionStatus = OptionStatus.Wrong
      } else {
        this.optionStatus = OptionStatus.Default
      }
    }
  }

  getBgColor() {
    switch (this.optionStatus) {
      case OptionStatus.Right:
        return '#1DBF7B'
      case OptionStatus.Wrong:
        return '#FA635F'
      default:
        return Color.White
    }
  }

  build() {
    Stack() {
      Button(this.option.toString())
        .optionButtonStyle({
          bg: this.getBgColor(),
          font: this.optionStatus === OptionStatus.Default ? Color.Black : Color.White
        })
      if (this.optionStatus === OptionStatus.Right) {
        Image($r('app.media.ic_right'))
          .width(22)
          .height(22)
          .offset({ x: 10 })
      } else if (this.optionStatus === OptionStatus.Wrong) {
        Image($r('app.media.ic_wrong'))
          .width(22)
          .height(22)
          .offset({ x: 10 })
      }
    }.alignContent(Alignment.Start)
  }
}


//打卡弹窗
// 5.3.访问登录页面
//打卡弹窗
@CustomDialog
struct ResultDialog {
  answeredCount: number;
  rightCount: number;
  timeUsed: number;
  resetPractice: () => void;
  startPractice: () => void;
  controller: CustomDialogController = new CustomDialogController({
    builder: ResultDialog
  })

  //用于存储登录后的token字符串。可通过判断该字符串来判断是否登录
  @StorageProp('token') token: string = ''

  //用于存储当前Tabs组件中访问的子页面的索引，方便登录后跳转到对应的子页面
  @StorageLink('currentTabIndex') currentTabIndex: number = 0

  build() {
    Column({ space: 10 }) {
      Image($r('app.media.ic_close'))
        .width(25)
        .height(25)
        .alignSelf(ItemAlign.End)
        .onClick(() => {
          this.controller.close()
          this.resetPractice()
        })
      Column({ space: 10 }) {
        Image($r('app.media.img_post'))
          .width('100%')
          .borderRadius(10)
        StatItem({
          icon: $r('app.media.ic_timer'),
          name: '用时',
          fontColor: Color.Black
        }) {
          Text(convertMillisecondsToTime(this.timeUsed))
        }

        StatItem({
          icon: $r('app.media.ic_accuracy'),
          name: '准确率',
          fontColor: Color.Black
        }) {
          Text((this.answeredCount === 0 ? 0 : (this.rightCount / this.answeredCount * 100).toFixed()) + '%')
        }

        StatItem({
          icon: $r('app.media.ic_count'),
          name: '个数',
          fontColor: Color.Black
        }) {
          Text(this.answeredCount.toString())
        }

        Divider()
        Row({ space: 30 }) {
          Button('再来一局')
            .controlButtonStyle({
              bg: Color.Transparent,
              border: Color.Black,
              font: Color.Black
            })
            .onClick(() => {
              this.controller.close()
              this.resetPractice()
              this.startPractice()
            })


          Button(this.token ? '立即打卡' : '登陆打卡')
            .controlButtonStyle({
              bg: Color.Black,
              border: Color.Black,
              font: Color.White
            })
            .onClick(async () => {
              this.controller.close()
              this.resetPractice()
              if (this.token) {
                //如已经登陆，发送打卡请求，后续补充
                // 6.2. 场景2：点击打卡弹窗的“立即打卡”
                // 发送打卡请求。需要在页面最上方导入 import {createPost} from '../http/Api'
                await createPost({
                  rightCount: this.rightCount,
                  answeredCount: this.answeredCount,
                  timeUsed: this.timeUsed
                })
                promptAction.showToast({ message: '打卡成功' })
                // 通知,需要再页面最上方导入import emitter from '@ohos.events.emitter'
                emitter.emit({ eventId: 1 })
                // 打卡后跳转到打卡圈
                this.currentTabIndex = 1

              }
              else {
                // 如未登录，跳转到登录页面
                router.pushUrl({
                  url: 'pages/LoginPage',
                  //传递打卡参数，用于后续实现打卡功能
                  params: {
                    rightCount: this.rightCount,
                    answeredCount: this.answeredCount,
                    timeUsed: this.timeUsed
                  } })
              }
            })
        }
      }
      .backgroundColor(Color.White)
      .width('100%')
      .padding(20)
      .borderRadius(10)
    }
    .backgroundColor(Color.Transparent)
    .width('80%')
  }
}


@Extend(Text) function wordStyle() {
  .fontSize(50)
  .fontWeight(FontWeight.Bold)
}

@Extend(Text) function sentenceStyle() {
  .height(40)
  .fontSize(16)
  .fontColor('#9BA1A5')
  .fontWeight(FontWeight.Medium)
  .width('80%')
  .textAlign(TextAlign.Center)
}

@Extend(Button) function optionButtonStyle(color: {
  bg: ResourceColor,
  font: ResourceColor
}) {
  .width(240)
  .height(48)
  .fontSize(16)
  .type(ButtonType.Normal)
  .fontWeight(FontWeight.Medium)
  .borderRadius(8)
  .backgroundColor(color.bg)
  .fontColor(color.font)
  .border({ width: 2, color: Color.White, radius: 10 })
}


@Extend(Button) function controlButtonStyle(color: {
  bg: ResourceColor,
  border: ResourceColor,
  font: ResourceColor
}) {
  .fontSize(16)
  .borderWidth(1)
  .backgroundColor(color.bg)
  .borderColor(color.border)
  .borderWidth(2)
  .fontColor(color.font)
}