<template>
  <div style="flex:3 3 0;padding:20px; background-color: white;margin-top:12px ;text-align: start; border-radius: 8px;box-shadow: 1px 5px 5px #e4e6e9;">
    <div style="text-align: start; margin-bottom: 8px;">
      <span>最新动态</span>
    </div>
    <el-scrollbar max-height="460" v-loading="loading">
      <el-timeline>
        <el-timeline-item
          v-for="(item, index) in latestNewsData"
          :key="index"
          center
          placement="bottom"
          :icon="markRaw(useRenderFlicker({
            background: generateRandomGradient(),
          }))"
          :timestamp="item.date"
        >
          <p style="margin: 6px 0;">
            {{
              `新增 ${item.requiredNumber} 条新闻`
            }}
          </p>
        </el-timeline-item>
      </el-timeline>
    </el-scrollbar>
  </div>
</template>

<script setup>
import axios from "axios";
import { computed, inject, markRaw, onMounted, ref } from "vue";
import { useRenderFlicker } from "../../../components/ReFlicker/";

import store from "../../../store";
import { generateRandomGradient } from "../utils";
const loading = computed(()=>{
  let l = false;
  for(let i in store.state.loading){
    // if(store.state.loading[i]) l=true;
    if(store.state.loading.TimeLine) l=true;
  }
  return l;
})
const url = inject('url');
const latestNewsData = ref([
  {
    date: "2024-03-21",
    requiredNumber: 10,
  },
  {
    date: "2024-03-22",
    requiredNumber: 20,
  },
  {
    date: "2024-03-23",
    requiredNumber: 30,
  },
  {
    date: "2024-03-24",
    requiredNumber: 40,
  },
  {
    date: "2024-03-25",
    requiredNumber: 50,
  },
  {
    date: "2024-03-21",
    requiredNumber: 10,
  },
  {
    date: "2024-03-22",
    requiredNumber: 20,
  },
  {
    date: "2024-03-23",
    requiredNumber: 30,
  },
  {
    date: "2024-03-24",
    requiredNumber: 40,
  },
  {
    date: "2024-03-25",
    requiredNumber: 50,
  },
  {
    date: "2024-03-21",
    requiredNumber: 10,
  },
  {
    date: "2024-03-22",
    requiredNumber: 20,
  },
  {
    date: "2024-03-23",
    requiredNumber: 30,
  },
  {
    date: "2024-03-24",
    requiredNumber: 40,
  },
]);
async function getData() {
    try {
        await store.dispatch('setLoading',{name:'TimeLine',value:true})
        let res;
        if(sessionStorage.getItem('timeline')){
          res = JSON.parse(sessionStorage.getItem('timeline'))
        }
        else{
          res = await axios.get(url+'spy/news/14num',{
            params:{
                // date:new Date().toISOString().slice(0, 10)
                date:'2024-06-15'
            }
          })
          sessionStorage.setItem('timeline',JSON.stringify(res))
        }
        console.log(res)
        const result = res.data.result
        console.log(result)
        let data = []
        for (let i = result.length-1; i >=0; i--) {
            data.push({
                date:result[i].date.slice(0,10),
                requiredNumber:result[i].num
            })
        }
        latestNewsData.value = data
        await store.dispatch('setLoading',{name:'TimeLine',value:false})
        
    } catch (error) {
      console.log(error);
    }
}
onMounted(() => {
  getData()
})
</script>

<style lang="scss" scoped>

  /* 解决概率进度条宽度 */
  :deep(.el-progress--line) {
    width: 85%;
  }

  /* 解决概率进度条字体大小 */
  :deep(.el-progress-bar__innerText) {
    font-size: 15px;
  }

  /* 隐藏 el-scrollbar 滚动条 */
  :deep(.el-scrollbar__bar) {
    display: none;
  }

  /* el-timeline 每一项上下、左右边距 */
  :deep(.el-timeline-item) {
    margin: 0 6px;
  }
:deep(.el-timeline){
  // padding-left:24px;
  padding: 0px;
}
</style>
