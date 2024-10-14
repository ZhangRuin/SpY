<script setup>

import axios from "axios";
import { computed, inject, onMounted, ref } from "vue";
import store from "../../../store";
const loading = computed(()=>{
  let l = false;
  for(let i in store.state.loading){
    if(store.state.loading[i]) l=true;
  }
  return l;
})
const url=inject('url');
const totalNum = ref(0);
const weekNum = ref(0);
const todayNum = ref(0)
const todayLoading = ref(true)
const today = ref("2024-03-01")
async function getCount() {
    try {
        //today.value =  new Date().toISOString().slice(0, 10);
        today.value = "2024-06-15"
        console.log(today.value)
        todayLoading.value = true;
        let res;
        if(sessionStorage.getItem('14num')){
          res = JSON.parse(sessionStorage.getItem('14num'))
        }
        else{
          res =await axios.get(url+'spy/news/14num', {
            params: {
                date: today.value
            }
          });
          sessionStorage.setItem('14num',JSON.stringify(res))
        }
        let result = res.data.result
        
        let sum = 0
        for (let i = result.length-1; i >=result.length-7; i--) {
            sum+=result[i].num
        }
        console.log(sum)
        weekNum.value=sum
        todayNum.value=result[result.length-1].num
        if(sessionStorage.getItem('totalnum')){
          res = JSON.parse(sessionStorage.getItem('totalnum'))
        }
        else{
          res =await axios.get(url+'spy/news/count');
          sessionStorage.setItem('totalnum',JSON.stringify(res))
        }
        console.log(res)
        totalNum.value = res.data.result;
        todayLoading.value = false;
    } catch (error) {
        console.log(error);
    }
}
onMounted(()=>{
    getCount()
})
</script>
<template>
    <div style="display: flex;flex-direction: row;">
        <div v-loading="todayLoading" style="width:33%; background-color: white;border-radius: 8px; display: flex;flex-direction: row;justify-content: space-between;padding: 16px 32px;align-items: center;margin-right: 6px; box-shadow: 1px 5px 5px #e4e6e9;">
            <div style="font-size:20px ;">新闻数量</div>
            <div style="font-size:28px ;">{{totalNum}}</div>
        </div>
        <div v-loading="todayLoading" style=" width:33%;background-color: white;border-radius: 8px; display: flex;flex-direction: row;justify-content: space-between;padding: 16px 32px;align-items: center;margin-right: 6px;box-shadow: 1px 5px 5px #e4e6e9;">
            <div style="font-size:20px ;">今日新闻数量</div>
            <div style="font-size:28px ;">{{todayNum}}</div>
        </div>
        <div v-loading="todayLoading" style="width:33%; background-color: white;border-radius: 8px; display: flex;flex-direction: row;justify-content: space-between;padding: 16px 32px;align-items: center;box-shadow: 1px 5px 5px #e4e6e9;">
            <div style="font-size:20px ;">本周新闻数量</div>
            <div style="font-size:28px ;">{{weekNum}}</div>
        </div>
    </div>
</template>



<style scoped>

</style>