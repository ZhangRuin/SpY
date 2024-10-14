<template>
    <div style="width: 100%;display:flex;flex-direction: column;margin-top: 6px;align-items: end;" >
        <div style="display: flex;flex-direction: row;width: 100%;justify-content: flex-end;">
            
            <div style="display: flex;flex-direction: column;align-items: end;position: relative;" class="animate">   
                <div style="background-color: #409EFF;padding: 8px 16px;font-size: 18px;border-radius: 8px;color: white;text-align:end;">{{ answer }}</div>
                <div style="font-size: 12px;color: rgb(91, 91, 91);margin-left: 0px;"> {{ new Date().toLocaleTimeString() }}</div>
            </div>
            <img class="img" src="../../../assets/image/user2.png">
        </div>
    </div>


</template>

<script setup>

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
  margin:0px 0 0 6px;
  border-radius: 50%;
}
.animate{
    /* transition: all 3s; */
    animation:PopUp 0.5s 1 cubic-bezier(0.4, 0, 0.2, 1);
}
@keyframes PopUp {
    from {
        left:-100px;
        opacity: 0;
    }

    to {
        left:0px;
        opacity: 1;
    }
}
</style>

