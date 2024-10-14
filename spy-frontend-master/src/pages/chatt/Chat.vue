<template>
  <div style="width: 100%;height: 100%;display: flex;flex-direction: column;align-items: center;">
    <div class="table">
      <div
        style="flex: 1; padding: 12px; display: flex; flex-direction: column;overflow-y: scroll;" ref="scrollbarRef" 
      >
        <div ref="innerRef">
          <div v-for="item in chatList" :key="item.type + item.content" ref="innerRefs">
            <LeftItem v-if="item.type === 'left'" :answer="item.content" />
            <RightItem v-else-if="item.type === 'right'" :answer="item.content" />
          </div>
          <div ref="placeholderRef" style="opacity:0;"></div>
        </div>
        
      </div>
  
      <div class="input">
        <el-input
          v-model="input"
          placeholder="与情报助手交谈"
          style="
            flex: 1;
            height: 50px;
            border-radius: 16px !important ;
            margin: 0px 12px 12px 12px;
          "
          size="large"
          @keyup.enter="send"
        ></el-input>
        <el-button
          @click="send"
          type="primary"
          size="large"
          :icon="ArrowRightBold"
          circle
          style="position: absolute; right: 17px; bottom: 17px"
        ></el-button>
      </div>
    </div>
  </div>

    
  </template>
  
  <script setup>
  import { ArrowRightBold } from "@element-plus/icons-vue";
import axios from "axios";
import { ElMessage } from 'element-plus';
import { inject, nextTick, ref, watchEffect } from "vue";
import LeftItem from "../chatt/components/LeftItem.vue";
import RightItem from "../chatt/components/RightItem.vue";

    const scrollbarRef = ref(null)
    const placeholderRef = ref(null)
    const innerRef = ref(null)
    const innerRefs = ref(null)
    const url = inject("url");
    const totalNum = ref(0);
    const loading = ref(true);
    const input = ref("");
    const isSearch = ref(false);
    const chatList = ref([
      {
        type: "left",
        content:
          "你好! 我是SpY情报助手, 有什么可以帮助你的吗? 有关新闻的任何问题都可以问我哦~ ",
      },
    ]);
    watchEffect(() => {
      let temp= chatList.value
      nextTick(() => {
        console.log(innerRefs.value[innerRefs.value.length-1].clientHeight)
        const placeholderHeight = Math.max(0, scrollbarRef.value.clientHeight - innerRefs.value[innerRefs.value.length-1].clientHeight-100);
        console.log("place:",placeholderHeight)
          // 设置占位符的高度
          placeholderRef.value.style.height = `${placeholderHeight}px`;
      })
    })
    function send() {
      if(isSearch.value) {
        ElMessage({
          message: "正在查询中，请稍后再试",
          type: "warning",
        });
        return;
      }
      if(input.value === "") {
        ElMessage({
          message: "请输入内容",
          type: "warning",
        });
        return;
      }
      chatList.value.push({
        type: "right",
        content: input.value,
      });
      input.value = "";
      nextTick(() => {
        // scrollbarRef.value.scrollTop=scrollbarRef.value.scrollHeight
        scrollbarRef.value.scrollTop = scrollbarRef.value.scrollHeight;
      })
      setTimeout(() => {
        chatList.value.push({
          type: "left",
          content: "正在查询...",
        });
        isSearch.value = true;
        axios.get(url+"chat/question", {
          params: {
            question: chatList.value[chatList.value.length - 2].content,
          },
        }).then((res) => {
          console.log(res);
          chatList.value[chatList.value.length - 1].content = res.data.result;
          isSearch.value = false;
        });

      }, 600);

    }

  </script>
  
  <style scoped>
  .table {
    position: relative;
    display: flex;
    flex-direction: column;
    width: 768px;
    height: calc(100vh - 64px - 24px);
    margin: 24px 24px 24px 24px;
    /* padding: 24px 24px 16px 24px; */
    background-color: white;
    border-radius: 8px;
    overflow: hidden;
    transition: all 0.3s;
  }
  .input {
    /* position: fixed;
    bottom: 12px; */
    position: relative;
    display: flex;
    flex-direction: row;
    width: 768px;
    justify-content: space-between;
    align-items: center;
  }
  :deep(.el-input__wrapper){
    border-radius: 20px;
    border: 1px solid #4a4a4a;
  }
  :deep(.el-input__wrapper.is-focus){
    border-radius: 20px;
    border: 1px solid #409EFF;
  }
  </style>
  