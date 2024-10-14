<script setup>
import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import {inject, ref, watchEffect } from 'vue';
import { useRouter } from 'vue-router';
const url = inject('url');
const router = useRouter();
const logout = () => {
  ElMessageBox.confirm(
    '确定退出登录?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      axios.get(url+'usr/logout').then((res) => {
        if(res.data.code === 200){
          ElMessage({
            type: 'success',
            message: '退出登录成功',
          })
          router.push('/login')
        }
        else{
          ElMessage({
            type: 'error',
            message: '退出登录失败',
          })
        }
      })
      .catch(() => {
        ElMessage({
          type: 'error',
          message: '退出登录失败',
        })
      })
    })
    .catch(() => {
      ElMessage({
        type: 'success',
        message: '取消退出登录',
      })
    })
}
const bread = ref('');
watchEffect(() => {
  bread.value = router.currentRoute.value.name
  console.log('路由发生了变化')
  console.log(router.currentRoute.value.name)
})
const time= ref(new Date().toLocaleTimeString());
// const evtSource = new EventSource('http://localhost:3000/events');
// evtSource.onmessage = function(event) {
//   //console.log('New message:', event.data);
//   time.value = event.data;
// };
let connected = false;
// evtSource.onopen = function() {
//   connected = true;
//   console.log('Connection established.');
// };
if(!connected){
  //evtSource.close();
  setInterval(() => {
    time.value = new Date().toLocaleTimeString();
  },1000)
}
</script>

<template>
  <div class="header">  
    <transition name="breadcrumb" mode="out-in">
      <div class="icon_font" :key="bread">{{bread}}</div>
    </transition>
    <div style="display: flex;flex-direction: row;align-items: center;">
      <div class="icon_font" style="margin-right: 16px;">{{ time }}</div>
      <el-dropdown>
        <img class="img" src="../assets/image/user2.png">
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped>
.header{
    width:100%;
    height:50px;
    min-height: 50px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    background-color: #ffffff;
    /* border-bottom: 1px solid #e4e6e9; */
    /* border-left: 1px solid #e4e6e9; */
    box-shadow: 1px 5px 5px #e4e6e9;
}
.icon_font{
    color:#606266;
    font-size: 14px;
    margin-left: 32px;
  
}
.img {
  width: 40px;
  height: auto;
  margin-right: 24px;
}
/* breadcrumb transition */
.breadcrumb-enter-active {
  transition: all 0.2s;
}

.breadcrumb-leave-active {
  transition: all 0.3s;
}

.breadcrumb-enter-from{
  opacity: 0;
  transform: translateX(-20px);
}
.breadcrumb-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

</style>