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
            <span>类型统计</span>
        </div>
        <div
            id="mychart"
            :style="{ height: '240px' }"
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
const catName= ["财经","彩票", "房产","股票","家居","教育","科技","社会","时尚","时政","体育","星座","游戏","娱乐"]
const catToday = ref([110,210,330,120,40,210,320,150,120,150,190,100,120,330])
const catYes = ref([130,110,30,20,20,110,230,160,100,100,100,140,150,110])
const catMinus = ref([110,210,330,-120,40,210,320,150,-120,150,190,100,120,330])
const url = inject('url');
const loading = computed(()=>{
  let l = false;
  for(let i in store.state.loading){
    // if(store.state.loading[i]) l=true;
    if(store.state.loading.Category) l=true;
  }
  if(l==false) initChart();
  return l;
})

function initChart() {
    var chart = echarts.init(document.getElementById("mychart"));
    const option  = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['昨日数量', '今日数量', '变化量']
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                data: catName
            }
        ],
        yAxis: [
            {
                type: 'value',
                axisTick: {
                    show: false
                }
            }
        ],
        series: [
            {
                name: '昨日数量',
                type: 'bar',
                stack: 'Total',
                label: {
                    show: false,
                    position: 'inside'
                },
                emphasis: {
                    focus: 'series'
                },
                data: catYes.value
            },
            {
                name: '今日数量',
                type: 'bar',
                label: {
                    show: false,

                },
                emphasis: {
                    focus: 'series'
                },
                data: catToday.value
            },
            {
                name: '变化量',
                type: 'bar',
                stack: 'Total',
                label: {
                    show: false,
                    position: 'inside'
                },
                emphasis: {
                    focus: 'series'
                },
                data: catMinus.value
            }
        ]
    };
    chart.setOption(option);
    window.addEventListener("resize",function (){
        chart.resize();
    });
}

async function getCount() {
    try {

        await store.dispatch('setLoading',{name:'Category',value:true})
        let res;
        if(sessionStorage.getItem('category')){
          res = JSON.parse(sessionStorage.getItem('category'))
        }
        else{
          res = await axios.get(url+'spy/news/2days/category',{
            params:{
                // date:new Date().toISOString().slice(0, 10)
                date:'2024-06-15'
            }
            })
          sessionStorage.setItem('category',JSON.stringify(res))
        }
        console.log(res)
        const result = res.data.result
        console.log(result)
        for (let i = 0; i <result.length; i++) {
            catToday.value[i]=result[i].todayNum
            catYes.value[i]=result[i].lastdayNum
            catMinus.value[i]=catToday.value[i]-catYes.value[i]
        }
        console.log(catToday.value)
        console.log(catYes.value)
        console.log(catMinus.value)
        initData()
        await store.dispatch('setLoading',{name:'Category',value:false})
    } catch (error) {
        console.log(error);
    }
}
const initData = () => {
    for (let i = 0; i <catMinus.value.length; i++) {
        catMinus.value[i]=catToday.value[i]-catYes.value[i]
    }
}
onMounted(async () => {
    await getCount()
});


</script>

<style scoped>
* {
    margin: 0;
    padding: 0;
}

</style>