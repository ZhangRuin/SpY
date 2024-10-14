<template>
  <div v-loading="loading" style="width:500px ;border-radius:8px;box-shadow: 0 1px 2px -2px rgba(0, 0, 0, 0.16), 0 3px 6px 0 rgba(0, 0, 0, 0.12),
    0 5px 12px 4px rgba(0, 0, 0, 0.09);">
    <div  id="myGraph" :style="{ height: '600px', width: '500px' }" style="margin: 0; border-radius: 16px">    
    </div>
    <div v-if="code == -1 " style="position: absolute; top:0;left:0;right:0;bottom: 0;margin:auto;">
      <el-empty description="该新闻无法提取知识图谱" style="position: absolute; top:0;left:0;right:0;bottom: 0;margin:auto;"/>
    </div>
  </div>
</template>
<script setup>
import axios from 'axios';
import * as echarts from "echarts";
import { inject, onMounted, ref, watch } from "vue";
import { generateRandomGradient } from '../utils';
const url = inject('url');
const loading= ref(true);
const code =ref(-1)
const props =defineProps({
  newsId: {
    type: Number,
    default: 0,
  },
})
let data =[
    {
        id:1,
        name:'测试节点',
        category:"人物",
        // symbolSize: 60
    },
    {
        id:2,
        name:'测试节点',
        category:"人物",
        // symbolSize: 40
    },
    {
        id:3,
        name:'测试节点',
        category:"地点",
    },
    {
        id:4,
        name:'测试节点',
        category:"地点",
    },
    {
        id:5,
        name:'测试节点',
        category:"人物",
    },
    {
        id:6,
        name:'测试节点',
        category:"事件",
    },
    {
        id:7,
        name:'测试节点',
        category:"事件",
    },
    {
        id:8,
        name:'测试节点',
        category:"人物",
    },
    {
        id:9,
        name:'测试节点',
        category:"人物",
    },
    {
        id:10,
        name:'测试节点',
        category:"人物",
    }
];
//节点连线
let linkData = [
    {source: '2', target: '3', name: '情人'},
    {source: '3', target: '4', name: '认识'},
    {source: '3', target: '5', name: '有关系'},
    {source: '5', target: '6', name: ''},
    {source: '5', target: '7', name: ''},
    {source: '5', target: '8', name: ''},
    {source: '9', target: '10', name: '关系'},
    {source: '1', target: '2' , name: ''},
    {source: '1', target: '10', name: ''}
]
let categories = [
    {
        name: '人物',
        itemStyle: {
          color: generateRandomGradient()
          // color: "#f56c6c"
        }
    },
    {
        name: '地点',
        itemStyle: {
            // color: "rgb(65, 182, 255)"
            color: generateRandomGradient()
        }
    },
    {
        name: '事件',
        itemStyle: {
            // color: "rgb(38, 206, 131)"
            color: generateRandomGradient()
        }
    },
]
let option = {
  // 动画更新变化时间
  animationDurationUpdate: 500,
  animationEasingUpdate: "quinticInOut",
  tooltip: {
    show: true,
    formatter: function(params) {
      // params 为当前鼠标所在数据项的信息
      var value = params.name; // 获取数值
      // 在这里可以对 value 进行自定义的格式化和处理
      // 返回的字符串将作为 tooltip 的内容显示
      return '名称: ' + value;
    }
  },
  legend: [{
    x:"left",
    y:'bottom',
    selectedMode:'multiple',
    //设置可以根据类别显示or隐藏节点
    data: categories.map(function (a) {
      return a.name;
    }),
    hoverLink: true
  }],
  series: [
    {
      type: "graph",
      layout: "force",
      
      legendHoverLink: false, //是否启用图例 hover(悬停) 时的联动高亮。
      hoverAnimation: true, //是否开启鼠标悬停节点的显示动画
      focusNodeAdjacency: true,
      edgeLabel: {
        normal: { 
          show: true,
          textStyle: {
            fontSize: 12,
            color: "#000000",
          },
          position:'middle',
          color:'#000000',
          offset:[0,0],
          formatter: function (x) {
            return x.data.name;
          },
        }
      },
      edgeSymbol: ['', 'arrow'],
      edgeSymbolSize:8,
      force: {
        repulsion: 3000,
        edgeLength:200,
        gravity : 1
      },
      roam: true,
      draggable: true, //每个节点的拖拉
      itemStyle: {
        
          color: "#000000",
          cursor: "pointer",
        //鼠标放上去有阴影效果
        emphasis: {
          shadowColor: "#00FAE1",
          shadowOffsetX: 0,
          shadowOffsetY: 0,
          shadowBlur: 40,

        },
      },
      lineStyle: {
        normal: {
                    width: 2,
                    color: '#000000',
                }
      },
      label: {
        normal: {
          show: true,
          textStyle: {
            color: "#000000",
            fontSize: 12,
          },
          nodeStyle: {
              brushType: "both",
              borderColor: "rgba(255,215,0,0.4)",
              borderWidth: 1,
            },
        }
      },
      symbolSize: 60, //节点大小
      links: linkData,
      data: data,
      categories: categories,
      cursor: "pointer",
    },
  ],
};
function clearChart(){
  var chart = echarts.init(document.getElementById("myGraph"));
  chart.clear();
  window.addEventListener("resize", function () {
    chart.resize();
  });
}
function initChart() {
  if(!document.getElementById("myGraph")) return;
  var chart = echarts.init(document.getElementById("myGraph"));

  chart.on("click", (params) => {
    if (params.dataType === "node") {
      //判断点击的是图表的节点部分
      this.nodeClick(params);
    }
  })
  // 指定图表的配置项和数据
  chart.setOption(option);
  window.addEventListener("resize", function () {
    chart.resize();
  });
}
async function getData() {
    try {
        clearChart();
        loading.value = true;
        console.log(props.newsId)
        const res = await axios.get(url+'neo4j/getNews/'+props.newsId.toString());
        console.log (res)
        code.value = res.data.code;
        data=[]
        for(let item of res.data.result.entityList){
            data.push({
                id:item.id.toString(),
                name:item.name,
                category:item.category
            })
        }
        option.series[0].data = data;
        console.log(data)
        linkData=[]
        for(let item of res.data.result.relationLinks){
            linkData.push({
                source:item.source.toString(),
                target:item.target.toString(),
                name:item.name
            })
        }
        option.series[0].links= linkData;
        console.log(linkData)
        categories=[];
        for(let item of res.data.result.categories){
            categories.push({
                name: item.name,
                itemStyle: {
                    color: generateRandomGradient()
                }
            })
        }
        option.series[0].categories= categories;
        option.legend[0].data= categories.map(function (a) {
            return a.name;
        })
        console.log(categories)
        initChart();
        loading.value =false;
    } catch (error) {
        console.log(error);
        loading.value =false;
    }
}
onMounted(() => {
  getData();
});
watch(()=>props.newsId, () => {
  getData();
});
</script>
