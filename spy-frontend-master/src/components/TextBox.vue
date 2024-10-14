<template>
    <text :class="{box_shadow: boxShadow}" :style="{color:color}">
        {{ value }}
    </text>
</template>
  
<script setup> 
  import { onMounted, ref } from 'vue';
  const props=defineProps({
    textContent: {
      type: String,
      default: "此处暂时没有内容",
    },
    boxShadow:{
      type: Boolean,
      default:false,
    },
    color:{
      type: String,
      default:'black'
    }
  });
  let value = ref('')
  for(let i=0;i<props.textContent.length;i++){
    value.value += ' '
  }
    function changeValue() {
        value.value ='_'
        let index = 0;
        let inrerval = setInterval(()=>{
            if(index === props.textContent.length){
                value.value = props.textContent.substring(0,index)
                clearInterval(inrerval)
                return;
            }
            value.value = props.textContent.substring(0,index)+'_';
            index++;
        },100)
    }
    let index2 = 0;
    function changeValueRandomly(){
        var min =8,max=30;
        var rand = Math.floor(Math.random()*(max-min+1)+min);
        setTimeout(()=>{
            if(index2 === props.textContent.length){
                value.value = props.textContent.substring(0,index2)
                return;
            }
            value.value = props.textContent.substring(0,index2)+'_';
            index2++;
            changeValueRandomly()
        },rand*10)
    }
    onMounted(()=>{
        changeValueRandomly()
    })
</script>
  
<style scoped>
.box_shadow {
  box-shadow: 4px 4px 10px #40a0ff7b;
}
</style>