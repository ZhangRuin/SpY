<script setup>
import { ref, watchEffect } from 'vue'
import {HomeFilled, List,Service} from '@element-plus/icons-vue'
import {useRouter} from 'vue-router'
const handleOpen = (key, keyPath) => {
  // console.log(key, keyPath);
};
const handleClose = (key, keyPath) => {
  // console.log(key, keyPath);
};
const router = useRouter();
const path = ref('/home');
watchEffect(() => {
  path.value = router.currentRoute.value.path
  console.log('路由发生了变化')
  console.log(router.currentRoute.value.path)
})
</script>

<template>
  <div
    style="
      border-right: 1px solid rgb(213, 213, 213);
      width: 210px;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: start;
      align-items: center;
    "
  >
    <div
      style="
        height: 48px;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
      "
    >
      <img src="../assets/image/logo.png" style="width: 30px; height: 30px" />
      <div
        style="
          font-weight: 600;
          font-size: 18px;
          margin-left: 8px;
          margin-right: 8px;
        "
      >
        SpY情报分析
      </div>
    </div>
    <el-menu
      :router="true"
      :default-active="path"
      style="width: 194px; font-weight: 500; border: 0px solid #fff"
      @open="handleOpen"
      @close="handleClose"
    >
      <el-menu-item index="/home" class="menuItem">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="/new" class="menuItem">
        <el-icon><List /></el-icon>
        <span>新闻列表</span>
      </el-menu-item>
      <el-menu-item index="/chat" class="menuItem">
        <el-icon><Service /></el-icon>
        <span>情报助手</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<style scoped>
:deep(.el-menu-item.is-active) {
  background-color: #409eff;
  color: #fff;
}
:deep(.el-menu-item.is-active:hover) {
  background-color: #409eff;
  color: #fff;
}
:deep(.el-menu-item) {
  color: #606266;
}
:deep(.el-menu-item:hover) {
  color: #000000;
  background-color: #fff;
}
.menuItem {
  border-radius: 4px;
  width: 194px;
  height: 42px;
  margin: 4px 0px;
}
</style>
