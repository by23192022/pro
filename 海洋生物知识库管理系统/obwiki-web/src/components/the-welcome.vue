<template>
  <div>
    <a-row>
      <a-col :span="24">
        <a-card>
          <a-row>
            <a-col :span="8">
              <a-statistic title="总阅读量" :value="statistic.viewCount">
                <template #suffix>
                  <UserOutlined />
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="8">
              <a-statistic title="总点赞量" :value="statistic.voteCount">
                <template #suffix>
                  <like-outlined />
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="8">
              <a-statistic title="点赞率" :value="statistic.voteCount / statistic.viewCount * 100"
                           :precision="2"
                           suffix="%"
                           :value-style="{ color: '#cf1322' }">
                <template #suffix>
                  <like-outlined />
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
    <br>
    <a-row :gutter="16">
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col :span="12">
              <a-statistic title="今日阅读" :value="statistic.todayViewCount" style="margin-right: 50px">
                <template #suffix>
                  <UserOutlined />
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="12">
              <a-statistic title="今日点赞" :value="statistic.todayVoteCount">
                <template #suffix>
                  <like-outlined />
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card>
          <a-row>
            <a-col :span="12">
              <a-statistic
                title="预计今日阅读"
                :value="statistic.todayViewIncrease"
                :value-style="{ color: '#0000ff' }"
              >
                <template #suffix>
                  <UserOutlined />
                </template>
              </a-statistic>
            </a-col>
            <a-col :span="12">
              <a-statistic
                title="预计今日阅读增长"
                :value="statistic.todayViewIncreaseRateAbs"
                :precision="2"
                suffix="%"
                class="demo-class"
                :value-style="statistic.todayViewIncreaseRate < 0 ? { color: '#3f8600' } : { color: '#cf1322' }"
              >
                <template #prefix>
                  <arrow-down-outlined v-if="statistic.todayViewIncreaseRate < 0"/>
                  <arrow-up-outlined v-if="statistic.todayViewIncreaseRate >= 0"/>
                </template>
              </a-statistic>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
    <br>
    <a-row>
      <a-col :span="24" id="main-col">
        <div id="main" style="width: 100%;height:300px;"></div>
      </a-col>
    </a-row>
  </div>


</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import axios from 'axios';
import { FileOutlined, UserOutlined, LikeOutlined, MailOutlined} from '@ant-design/icons-vue';

import * as echarts from 'echarts';


const statistic = ref();
/*
statistic.value = {
  viewCount: 0,
  voteCount: 0,
  todayViewCount: 0,
  todayVoteCount: 0
};
 */

statistic.value = {};


const getStatistic = () => {
  axios.get('/ebookSnapshot/getStatistic').then((response) => {
    const data = response.data;

    //console.log('【response.data】：', response.data);

    if (data.success) {
      const statisticResp = data.content;

      console.log('【statisticResp】：', statisticResp);

      /*纠错过程
            //let statisticResp : any ;
            //statisticResp.value = data.content;

      statistic.value.viewCount = 1;
      console.log('【statistic.value.viewCount】：', statistic.value.viewCount);
      console.log('【1】：', statisticResp[1]);
      statistic.value.voteCount = 1;
      statistic.value.todayViewCount = 1;
      statistic.value.todayVoteCount = 1;
       */

      /*
      11-05 根据打印的【statisticResp】对象，发现并没有statisticResp[1]，只有statisticResp[0]
       11-06，今天打印的【statisticResp】对象有（11-05）statisticResp[0]和（11-06）statisticResp[1]了。
       */
      if(statisticResp[1] != null){
        console.log('【statisticResp[1]】：', statisticResp[1]);
        //下一天
        //设置今天点赞数及阅读数
        statistic.value.viewCount = statisticResp[1].viewCount;
        statistic.value.voteCount = statisticResp[1].voteCount;
        statistic.value.todayViewCount = statisticResp[1].viewIncrease;
        statistic.value.todayVoteCount = statisticResp[1].voteIncrease;
      } else{
        console.log('【statisticResp[0]】：', statisticResp[0]);
        //首天（statisticResp[1] == null）
        //设置今天点赞数及阅读数
        statistic.value.viewCount = statisticResp[0].viewCount;
        statistic.value.voteCount = statisticResp[0].voteCount;
        statistic.value.todayViewCount = statisticResp[0].viewIncrease;
        statistic.value.todayVoteCount = statisticResp[0].voteIncrease;
      }


      // 按分钟计算当前时间点，占一天的百分比
      const now = new Date();
      //今天的分钟/一天的分钟数  获得 今天 过去的百分比
      const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);
      // console.log(nowRate)
      //今天阅读数/今天的百分比 = 今天预计总阅读数
      statistic.value.todayViewIncrease = parseInt(String(statisticResp[0].viewIncrease / nowRate));
      // todayViewIncreaseRate：今日预计增长率
      if (statisticResp[0].viewIncrease === 0){
        //同比增长率计算时，上期值为0，则增长率为100%。 昨日增长率为0的话，那么考虑增长率无意义
        statistic.value.todayViewIncreaseRate = 100;
      } else {
        //今日增长率 = (今天阅读数-昨天阅读数) /昨天阅读数*100
        statistic.value.todayViewIncreaseRate = (statistic.value.todayViewIncrease - statisticResp[0].viewIncrease) / statisticResp[0].viewIncrease * 100;
      }
      //获取绝对值显示  Math.abs
      statistic.value.todayViewIncreaseRateAbs = Math.abs(statistic.value.todayViewIncreaseRate);
    }
  })
  .catch(error => {
    console.log('【数据加载失败】：', error);
  })
  ;
};



const init30DayEcharts = (list: any) => {
  // 发布生产后出现问题：切到别的页面，再切回首页，报表显示不出来
  // 解决方法：把原来的id=main的区域清空，重新初始化
  const mainDom = document.getElementById('main-col');
  if (mainDom) {
    mainDom.innerHTML = '<div id="main" style="width: 100%;height:300px;"></div>';
  }
  // 基于准备好的dom，初始化echarts实例
  const myChart = echarts.init(document.getElementById('main'));

  const xAxis = [];
  const seriesView = [];
  const seriesVote = [];
  for (let i = 0; i < list.length; i++) {
    const record = list[i];
    xAxis.push(record.date);//构造x轴 数据 日期
    seriesView.push(record.viewIncrease);
    seriesVote.push(record.voteIncrease);
  }

  // 指定图表的配置项和数据
  const option = {
    title: {
      text: '30天趋势图'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['总阅读量', '总点赞量']
    },
    grid: {
      left: '1%',
      right: '3%',
      bottom: '3%',
      containLabel: true
    },
    toolbox: {
      feature: {
        saveAsImage: {}
      }
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxis
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '总阅读量',
        type: 'line',
        // stack: '总量', 不堆叠
        data: seriesView,
        smooth: true
      },
      {
        name: '总点赞量',
        type: 'line',
        // stack: '总量', 不堆叠
        data: seriesVote,
        smooth: true
      }
    ]
  };
  // 使用刚指定的配置项和数据显示图表。
  myChart.setOption(option);
};

const get30DayStatistic = () => {
  axios.get('/ebookSnapshot/get30Statistic').then((response) => {
    const data = response.data;

    console.log('【response.data】：', response.data);

    if (data.success) {
      const statisticList = data.content;

      console.log('【statisticList】：', statisticList);

      init30DayEcharts(statisticList);
    }
  });
};


onMounted(() => {
  getStatistic();
  get30DayStatistic();
});

</script>

<style scoped>

</style>

<style scoped>

</style>
