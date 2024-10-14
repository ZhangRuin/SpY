<template>
  <div style="flex:3 3 0;padding:20px; background-color: white;margin-top:12px ;text-align: start; border-radius: 8px;box-shadow: 1px 5px 5px #e4e6e9;" >
    <div style="text-align: start;">
      <span>今日关键词</span>
    </div>
    <div
    id="mywordcloud"
    :style="{  height: '240px'}"
    style="margin: 0px 0 0 0 ;"
    :data="worddata"
    v-loading="loading"
   ></div>
  </div>

</template>
<script setup>
import axios from "axios";
import * as echarts from 'echarts';
import "echarts-wordcloud";
import { computed, inject, onMounted, ref } from "vue";
import store from "../../../store";
const loading = computed(()=>{
  let l = false;
  for(let i in store.state.loading){
    // if(store.state.loading[i]==true) l=true;
    if(store.state.loading.WordCloud) l=true;
  }
  if(l==false) initChart();
  return l;
})
const url = inject('url');
let worddata = ref([
  {
    name: "十九大精神",
    value: 15000,
  },
  {
    name: "两学一做",
    value: 10081,
  },
  {
    name: "中华民族",
    value: 9386,
  },
  {
    name: "马克思主义",
    value: 7500,
  },
  {
    name: "民族复兴",
    value: 7500,
  },
  {
    name: "社会主义制度",
    value: 6500,
  },
  {
    name: "国防白皮书",
    value: 6500,
  },
  {
    name: "创新",
    value: 6000,
  },
  {
    name: "民主革命",
    value: 4500,
  },
  {
    name: "文化强国",
    value: 3800,
  },
  {
    name: "国家主权",
    value: 3000,
  },
  {
    name: "伟大复兴",
    value: 2500,
  },
  {
    name: "领土完整",
    value: 2300,
  },
  {
    name: "安全",
    value: 2000,
  },
  {
    name: "从严治党",
    value: 1900,
  },
  {
    name: "现代化经济体系",
    value: 1800,
  },
  {
    name: "国防动员",
    value: 1700,
  },
  {
    name: "信息化战争",
    value: 1600,
  },
  {
    name: "局部战争",
    value: 1500,
  },
  {
    name: "教育",
    value: 1200,
  },
  {
    name: "中国梦",
    value: 1100,
  },
  {
    name: "孙子兵法",
    value: 900,
  },
  {
    name: "一国两制",
    value: 800,
  },
  {
    name: "特色社会主义思想",
    value: 700,
  },
]);

function initChart() {
  let image_cloud ="data:image/png;base64,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";
  let image_circle="data:image/png;base64,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";
  let maskImage = new Image();
  maskImage.src =image_circle;
  var chart = echarts.init(document.getElementById("mywordcloud"));
      const option = {
        backgroundColor: "#fff",
        tooltip: {//提示框组件
              trigger: 'item', //item数据项图形触发，主要在散点图，饼图等无类目轴的图表中使用。
              showContent:true,  
        },    
        series: [
          {
            type: "wordCloud",
            shape: 'circle',
            maskImage: maskImage,
            //用来调整词之间的距离
            gridSize:6,
            //用来调整字的大小范围
            // Text size range which the value in data will be mapped to.
            // Default to have minimum 12px and maximum 60px size.
            sizeRange: [20,40],
            //文本旋转范围和步进度。文本将通过rotationStep:45在[- 90,90]范围内随机旋转
            rotationRange: [-0,0],
            rotationStep: 15,
            //随机生成字体颜色
            // maskImage: maskImage,
            textStyle: {
              
  
                color: function () {
                  return (
                    "rgb(" +
                    Math.round(Math.random() * 255) +
                    "," +
                    Math.round(Math.random() * 255) +
                    "," +
                    Math.round(Math.random() * 255) +
                    ")"
                  );
              },
              
            },
            emphasis: {
                focus: 'self',
    
                textStyle: {
                    textShadowBlur:4,
                    textShadowColor: 'rgb(65, 182, 255)'
                }
            },
            //位置相关设置
            // Folllowing left/top/width/height/right/bottom are used for positioning the word cloud
            // Default to be put in the center and has 75% x 80% size.
            left: "center",
            top: "center",
            right: "center",
            bottom: "center",
            width: "140%",
            height: "140%",
            //数据
            data: worddata.value,
          },
        ],
      };
  maskImage.onload = function(){  

    chart.setOption(option);
  };
  window.addEventListener("resize",function (){
    chart.resize();
  });
}
async function getData() {
    try {
        await store.dispatch('setLoading',{name:'WordCloud',value:true})
        let res;
        if(sessionStorage.getItem('worddata')){
          res = JSON.parse(sessionStorage.getItem('worddata'))
        }
        else{
          res = await axios.get(url+'spy/news/keyword',{
            params:{
                // date:new Date().toISOString().slice(0, 10)
                date:'2024-06-15'
            }
          })
          sessionStorage.setItem('worddata',JSON.stringify(res))
        }
        console.log(res)
        const result = res.data.result
        console.log(result)
        let data = []
        let filterResult = result.filter(item=>item.name.length<5)
        if(filterResult.length>25){
          for(let i = 0;i<=25;i++){
            data.push({
              name:filterResult[i].name,
              value:filterResult[i].value
            })
          }
        }
        else data=filterResult;
        worddata.value =data;
        await store.dispatch('setLoading',{name:'WordCloud',value:false})
    } catch (error) {
        console.log(error);
    }
}
onMounted(async () => {
  await getData();
  
});
</script>