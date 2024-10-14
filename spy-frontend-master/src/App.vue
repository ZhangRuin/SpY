<script setup>
import { provide } from "vue";
import './assets/css/index.css';
import initial from "./pages/home/components/initial";
 
const url='http://123.60.77.90:9010/';
provide('url',url);
initial();

</script>

<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style scoped>

</style>
