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
            <span>地理分布</span>
        </div>
        <div
            v-loading="loading"
            id="chinaMap"
            :style="{ height: '470px' }"
            style="margin: 0; border-radius: 16px"
        ></div>
    </div>
</template>

<script setup>
import axios from "axios";
import * as echarts from "echarts";
import { computed, inject, onMounted, ref } from "vue";
import store from "../../../../store";
import chinaJson from "./china.json";
const loading = computed(()=>{
  let l = false;
  for(let i in store.state.loading){
    // if(store.state.loading[i]) l=true;
    if(store.state.loading.ChinaMap) l=true;
  }
  if(l==false) initChart();
  return l;
})
const url = inject('url');
const data = ref([]);
const maxNum=ref(0);
const minNum=ref(10000);
function initChart() {
    echarts.registerMap('chinaMap', chinaJson);
    var chart = echarts.init(document.getElementById("chinaMap"));
    const option  = {
        tooltip: {
          trigger: 'item'
        },
        geo: {
          map: 'chinaMap',
          zoom: 1.7,
          label: {
            emphasis: {
              show: false
            }
          },
          roam: true, // 是否开启鼠标缩放和平移漫游
          top:'30%',
          itemStyle: {
            borderColor: 'rgba(255, 0, 0, 0.2)', // 图形的描边颜色。
            borderWidth: 1, // 描边线宽。为 0 时无描边。
            borderType: 'solid', // 描边类型。
          },
          scaleLimit: {
            min: 1,
            max: 2
          }
        },
        series:[ 
            {
                name:'新闻数量',
                type: 'map',
                geoIndex: 0,
                data: data.value,
            }
        ],
        // 地图区域的多边形 图形样式。

        // 连续型数据不分段。
        visualMap: {
            type: 'continuous', // 连续型
            min: minNum.value, // 最小值
            max: maxNum.value, // 最大值
            borderColor: '#000', // 边框颜色       
            color: ['red','orange', 'yellow', 'lightskyblue','#f4f4f5'], // 定义 在选中范围中 的视觉元素。顺序是由数值 大 到 小
        },

    };
    chart.setOption(option);
    window.addEventListener("resize",function (){
        chart.resize();
    });
}
async function getData() {
    try {
        await store.dispatch('setLoading',{name:'ChinaMap',value:true})
        let res;
        if(sessionStorage.getItem('map')){
          res = JSON.parse(sessionStorage.getItem('map'))
        }
        else{
          res = await axios.get(url+'spy/news/provinceCount',{
            params:{
                // date:new Date().toISOString().slice(0, 10)
                date:'2024-06-15'
            }
            })
          sessionStorage.setItem('map',JSON.stringify(res))
        }
        console.log(res)
        const result = res.data.result
        console.log(result)
        data.value = result;
        for(let item in result){
            if(result[item].value>maxNum.value){
                maxNum.value=result[item].value;
            }
            if(result[item].value<minNum.value){
                minNum.value=result[item].value;
            }
        }
        await store.dispatch('setLoading',{name:'ChinaMap',value:false})

    } catch (error) {
        console.log(error);
    }
}
onMounted(() => {
    console.log('chinaMap mounted')
    getData();
});
</script>

<style scoped>

</style>