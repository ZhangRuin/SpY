<template>
    <div style="width: 100%;display:flex;flex-direction: column;margin-top: 6px;align-items: start;position: relative;" >
        
        <div style="display: flex;flex-direction: row;">
            <img class="img" src="../../../assets/image/robot.png">
            <div style="display: flex;flex-direction: column;align-items: start;">   
                <div style="width:auto;background-color: #F7F7F7;padding: 8px 16px;font-size: 18px;border-radius: 8px;text-align: start;">
                    <TextBoxFast :textContent="props.answer"></TextBoxFast>
                </div>
                <div style="font-size: 12px;color: rgb(91, 91, 91);margin-left: 0px;"> {{ new Date().toLocaleTimeString() }}</div>
            </div>
            
        </div>
    </div>


</template>

<script setup>
import TextBoxFast from '../../../components/TextBoxFast.vue';
const props=defineProps({
    answer:{
        type:String,
        default:'此处没有内容'
    }
})
</script>

<style scoped>
.img {
  width: 42px;
  height: 42px;
  margin:0px 6px 0 0;
  border-radius: 50%;
}
.animate{
    /* transition: all 3s; */
    animation:PopUp 0.5s 1 cubic-bezier(0.4, 0, 0.2, 1);
}
@keyframes PopUp {
    from {
        right:-100px;
        opacity: 0;
    }

    to {
        right:0px;
        opacity: 1;
    }
}
</style>