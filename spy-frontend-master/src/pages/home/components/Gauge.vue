<template>
  <div
    style="
      flex: 3 3 0;
      padding: 20px;
      background-color: white;
      margin-top: 12px;
      text-align: start;
      border-radius: 8px;
      box-shadow: 1px 5px 5px #e4e6e9;
    "
  >
    <div style="text-align: start">
      <span>当日立场极性</span>
    </div>
    <div
      id="myGuage"
      :style="{ height: '300px' }"
      style="margin: 0; border-radius: 16px"
      v-loading="loading"
    ></div>
  </div>
</template>
<script setup>
import axios from "axios";
import * as echarts from "echarts";
import { computed, inject, onMounted, ref } from "vue";
import store from "../../../store";
const loading = computed(()=>{
  let l = false;
  for(let i in store.state.loading){
    // if(store.state.loading[i]) l=true;
    if(store.state.loading.Gauge) l=true;
  }
  if(l==false) initChart();
  return l;
})
const url = inject('url');
const data=ref([{ name: "立场极性", value: -0.46 }]);
function initChart() {
  var chart = echarts.init(document.getElementById("myGuage"));
  const option = {
    //backgroundColor: '#000000',
    tooltip: {
      formatter: "{b} : {c}",
    },

    series: [
      {
        startAngle: 180, //开始角度 左侧角度
        endAngle: 0, //结束角度 右侧
        name: "业务指标",
        type: "gauge",
        radius:	"95%",
        splitNumber: 5,
        detail: {
          formatter: function (value) {
            if (value<-0.3) {
              return "负面 (平均值 : "+value+')';
            } else if (value >0.3) {
              return "正面 (平均值 : "+value+')';
            } else {
              return "中立 (平均值 : "+value+')';
            }
          },
          fontSize: 20,
          color: "auto",
        },
        min: -1,
        max: 1,
        axisLine: {
          // 坐标轴线
          lineStyle: {
            // 属性lineStyle控制线条样式
            color: [
              [0.35, "#f56c6c"],
              [0.65, "rgb(65, 182, 255)"],
              [1, "rgb(38, 206, 131)"],
            ],
          },
        },
        title: {
          // 仪表盘标题。
          show: false, // 是否显示标题,默认 true。
        },
        center:["50%","68%"],
        data: data.value,
      },
    ],
  };
  chart.setOption(option);
  window.addEventListener("resize",function (){
    chart.resize();
  });
}
async function getData() {
    try {
        await store.dispatch('setLoading',{name:'Gauge',value:true})
        let res;
        if(sessionStorage.getItem('guage')){
          res = JSON.parse(sessionStorage.getItem('guage'))
        }
        else{
          res = await axios.get(url+'spy/news/standpoint',{
            params:{
                date:'2024-06-15'
            }
          })
          sessionStorage.setItem('guage',JSON.stringify(res))
        }
        console.log(res)
        const result = res.data.result
        console.log(result)
        data.value = [{ name: "立场极性", value: result.averageStandPoint.toFixed(2) }];
        await store.dispatch('setLoading',{name:'Gauge',value:false})
    } catch (error) {
        console.log(error);
    }
}
onMounted(async () => {
  await getData();
});
</script>
