<template>
  <div
    v-loading="loading"
    style="
      width: 500px;
      border-radius: 8px;
      box-shadow: 0 1px 2px -2px rgba(0, 0, 0, 0.16),
        0 3px 6px 0 rgba(0, 0, 0, 0.12), 0 5px 12px 4px rgba(0, 0, 0, 0.09);
    "
  >
    <div
      id="myGuage2"
      :style="{ height: '300px', width: '500px' }"
      style="margin: 0; border-radius: 16px"
    ></div>
  </div>
</template>
<script setup>
import * as echarts from "echarts";
import { ref,onMounted, watch } from "vue";
const props = defineProps({
  standPoint: {
    type: Number,
    default: 0,
  },
});
const loading = ref(false);
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
      radius: "100%",
      splitNumber: 5,
      detail: {
        formatter: function (value) {
          if (value < -0.3) {
            return "负面 (" + value + ")";
          } else if (value > 0.3) {
            return "正面 (" + value + ")";
          } else {
            return "中立 (" + value + ")";
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
      center: ["50%", "68%"],
      data: [{ name: "立场极性", value: props.standPoint.toFixed(2) }],
    },
  ],
};
function initChart() {
  if(!document.getElementById("myGuage2")) return;
  loading.value = true;
  var chart = echarts.init(document.getElementById("myGuage2"));
  chart.setOption(option, true);
  window.addEventListener("resize", function () {
    chart.resize();
  });
  loading.value = false;
}

onMounted(() => {
  initChart();
});
watch(()=>props.standPoint, () => {
  option.series[0].data[0].value = props.standPoint.toFixed(2);
  initChart();
});
</script>
