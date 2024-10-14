<script setup>
import Header from './components/Header.vue';
import Menu from './components/Menu.vue';

</script>

<template>
  <Menu />
  <div style="position:absolute;right:0;left:210px;bottom:0;top:0;display: flex;flex-direction: column;justify-content: start;background-color: #f0f2f5;">
    <Header /> 
    <router-view v-slot="{ Component }" >
      <transition name="fade-transform" mode="out-in">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </transition>
    </router-view>
  </div>
</template>

<style scoped>


/* fade-transform */
.fade-transform-leave-active{
  transition: all 0.2s;
}
.fade-transform-enter-active {
  transition: all 0.3s;

}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}


</style>