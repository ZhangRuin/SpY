<template>
  <el-scrollbar style="max-height: calc(100vh - 64px);">
    <div class="table">
        <NewsNum/>
        <div style="display: flex;flex-direction: row;">
            <div style="flex: 7 7 0;background-color: white;border-radius: 8px;margin:12px 12px 0 0;"> <CategoryChart/></div>
            <WordCloud />
        </div>
      <div style="display: flex;flex-direction: row;">
        <div style="flex: 7 7 0;background-color: white;border-radius: 8px;margin:12px 12px 0 0;"> 
          <Bar/>
        </div>
        <Gauge />
      </div>

      <div style="display: flex;flex-direction: row;">
        <div style="flex:7 7 0;background-color: white;border-radius: 8px;margin:12px 12px 0 0;">
          <ChinaMap />
        </div>
        <TimeLine />
      </div>
    </div>
  </el-scrollbar>
</template>

<script setup>
import axios from "axios";
import { inject, onMounted, ref } from 'vue';
import store from "../../store";
import Bar from './components/Bar.vue';
import CategoryChart from "./components/CategoryChart.vue";
import ChinaMap from './components/chinaMap/ChinaMap.vue';
import Gauge from './components/Gauge.vue';
import NewsNum from "./components/NewsNum.vue";
import TimeLine from './components/TimeLine.vue';
import WordCloud from './components/WordCloud.vue';
const url=inject('url');
const totalNum = ref(0);
const loading = ref(true)
store.dispatch('refreshLoading')
</script>

<style scoped>
.table {
  /* margin:24px 24px 16px 24px; */
  position:relative;
  display: flex;
  flex-direction: column;
  justify-content: start;
  width: auto;
  overflow: hidden;
  padding: 24px 24px 16px 24px;
  transition: all 0.3s;
  /* animation: PopUp 0.5s 1 cubic-bezier(0.4, 0, 0.2, 1); */
}
</style>