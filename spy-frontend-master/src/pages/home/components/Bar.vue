<template>
  <div
    style="
      border-radius: 8px;
      padding: 20px;
      background-color: white;
      text-align: start;
      box-shadow: 1px 5px 5px #e4e6e9;
    "
  >
    <div style="text-align: start">
      <span>14日立场分析</span>
    </div>
    <div
      v-loading="loading"
      id="myBar"
      :style="{ height: '300px' }"
      style="margin: 0; border-radius: 16px"
    ></div>
  </div>
</template>
<script setup>
import axios from "axios";
import * as echarts from "echarts";
import "echarts-wordcloud";
import { computed, inject, onMounted } from "vue";
import store from "../../../store";
const loading = computed(()=>{
  let l = false;
  for(let i in store.state.loading){
    // if(store.state.loading[i]) l=true;
    if(store.state.loading.Bar) l=true;
  }
  if(l==false) initChart();
  return l;
})
const url = inject('url')
let wordData = ["负面","中立","正面"];
let negative =[1,2,3,4,5,6,7,8,9,10,11,12,13,14]
let neutrality = [2,3,4,5,6,7,8,9,10,23,14,35,10,26]
let positive = [5,3,6,8,1,6,9,4,8,7,2,1,3,8]
let fourteenDate = ['03-01',"03-02",'03-01',"03-02",'03-01',"03-02",'03-01',"03-02",'03-01',"03-02",'03-01',"03-02",'03-01',"03-02"]

function initChart() {
  var chart = echarts.init(document.getElementById("myBar"));
  const option = {
      title: {
          text: ''
      },
      tooltip: {
          trigger: 'axis'
      },
      legend: {
          data: wordData
      },
      grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
      },
      toolbox: {
      },
      xAxis: {
          type: 'category',
          boundaryGap: false,
          data:fourteenDate
      },
      yAxis: {
          type: 'value'
      },
      series: [
          {
              name: '正面',
              type: 'line',
              stack: 'pos',
              data:positive,
              lineStyle: {
                  color: 'rgb(38, 206, 131)' // 正面折线的颜色
              },
              itemStyle: {
                  color: 'rgb(38, 206, 131)' // 正面折线的颜色
              }
          },
          {
              name:'负面',
              type: 'line',
              stack: 'neg',
              data:negative,
              lineStyle: {
                  color: '#f56c6c' // 正面折线的颜色
              },
              itemStyle: {
                  color: '#f56c6c' // 正面折线的颜色
              }
          },{
              name:'中立',
              type: 'line',
              stack: 'neu',
              data:neutrality,
              lineStyle: {
                  color: 'rgb(65, 182, 255)'
              },
              itemStyle: {
                  color: 'rgb(65, 182, 255)'
              }
          }
      ]
  };
    // xAxis: {
    //   type: "value",
    // },
    // yAxis: {
    //   data: ["负面", "中立", "正面"],
    //   type: "category",
    // },
    // backgroundColor: "#fff",
    // tooltip: {
    //   trigger: "axis", //坐标轴触发，主要在柱状图，折线图等会使用类目轴的图表中使用
    //   axisPointer: {
    //     // 坐标轴指示器，坐标轴触发有效
    //     type: "shadow", // 默认为直线，可选为：'line' | 'shadow'
    //   },
    // },
    // grid: {
    //   left: "2%",
    //   right: "5%",
    //   bottom: "8%",
    //   top: "8%",
    //   containLabel: true,
    // },
    // series: [
    //   {
    //     type: "bar",
    //     itemStyle: {
    //       // 根据数据值设置不同的颜色
    //       color: function (params) {
    //         //console.log(params);
    //         var value = params.data.name;
    //         if (value === "负面") {
    //           return "#f56c6c"; // 负面柱体的颜色
    //         } else if (value === "中立") {
    //           return "rgb(65, 182, 255)"; // 中立柱体的颜色
    //         } else if (value === "正面") {
    //           return "rgb(38, 206, 131)"; // 正面柱体的颜色
    //         }
    //       },
    //       borderRadius: [0, 10, 10, 0],
    //     },
    //
    //     //数据
    //     data: worddata,
    //   },
    // ],
  chart.setOption(option);
  window.addEventListener("resize", function () {
    chart.resize();
  });
}
async function getData() {
    try {
        await store.dispatch('setLoading',{name:'Bar',value:true})
        let res;
        if(sessionStorage.getItem('bar')){
          res = JSON.parse(sessionStorage.getItem('bar'))
        }
        else{
          res = await axios.get(url+'spy/news/standpoint',{
            params:{
                date:'2024-06-15'
            }
          })
          sessionStorage.setItem('bar',JSON.stringify(res))
        }
        console.log(res)
        const result = res.data.result
        console.log("Bar result"+result)
        const data = result.standPointNums

        for (const single in data) {
            // console.log(single)
            // console.log(data[single])
            negative[single]=data[single].negativeNum
            positive[single]=data[single].positiveNum
            neutrality[single]=data[single].neutralNum
            fourteenDate[single] = data[single].date.slice(5,10)
        }
        await store.dispatch('setLoading',{name:'Bar',value:false})
        
    } catch (error) {
        console.log(error);
    }
}
onMounted(async () => {
    await getData();
});

</script>
