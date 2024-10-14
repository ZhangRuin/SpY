<template>
    <div class="processBox">
        <el-scrollbar max-width="100%" >
        <div class="timelineProcessBox" v-loading = "loading">
            <el-timeline class="timeline">
                <el-timeline-item
                        class="lineitem"
                        v-for="(activity, index) in activities"
                        :class = 'active'
                        :key="index"
                        :timestamp="activity.time"
                        placement="top"
                >
                    <span style="display: flex; flex-direction: column">
                        <span style="margin: 10px ; font-size: 14px;letter-spacing: 1px;width:120px;white-space: normal;line-height: 1.5;">
                            {{ activity.event }}
                        </span>
<!--                        <span style="color: #1a1a1a; font-size: 14px;">-->
<!--                            {{ activity.event }}-->
<!--                        </span>-->
                    </span>
                </el-timeline-item>
            </el-timeline>
        </div>
        </el-scrollbar>
    </div>
</template>
<script setup>
import {inject, onMounted, ref, watch} from "vue";
import axios from "axios";
const url = inject('url');

const props = defineProps({
      newsNumber:{
          value:0
      }
})
const loading = ref(true);
const activities = ref([{
    time:"2023-11",
    event:"发生了一件大事"
},
    {
        time:"2023-12-01",
        event: "发生了两件大事"
    },
    {
        time:"2024-01-05",
        event: "发生了两件小事和三件大事"
    },{
        time:"2023-11",
        event:"发生了一件大事"
    },
    {
        time:"2023-12-01",
        event: "发生了两件大事"
    },
    {
        time:"2024-01-05",
        event: "发生了两件小事"
    },{
        time:"2024-01-05",
        event: "发生了两件小事和三件大事"
    },{
        time:"2023-11",
        event:"发生了一件大事"
    },
    {
        time:"2023-12-01",
        event: "发生了两件大事"
    },
    {
        time:"2024-01-05",
        event: "发生了两件小事"
    },
])

async function getLineData() {
    try {
        console.log(props.newsNumber)
        loading.value = true;
        let res = await axios.get(url+'spy/news/event/'+props.newsNumber+1);
        console.log("得到返回")
        console.log(res)
        activities.value = res.data.result;
        loading.value = false;
    } catch (error) {
        console.log(error);
    }
}
onMounted(()=>{
    getLineData();
})
watch(()=>props.newsNumber, () => {
    getLineData();
});
</script>

<style lang="scss" scoped>
.processBox {
    background-color: #fff;
    height: 210px;
    width: 100%;
    max-width: 100%;
    margin-bottom: 20px;
    padding: 0;
    white-space: nowrap; /* 防止时间节点换行 */
    border-radius:8px;box-shadow: 0 1px 2px -2px rgba(0, 0, 0, 0.16), 0 3px 6px 0 rgba(0, 0, 0, 0.12),
0 5px 12px 4px rgba(0, 0, 0, 0.09);

    .timelineProcessBox {
        padding: 10px;
        padding-left: 25px;
        .loading {
            /* 加载时样式，例如将背景变为灰色 */
            background-color: #f4f4f4;
            opacity: 100%; /* 可调整透明度 */
        }
        .timeline {
            display: flex;
            margin-top: 95px;
            .lineitem {
                position: relative;
                min-width: 150px; /* 设置节点的最小宽度 */
                white-space: normal; /* 允许节点内容换行 */

                :deep(.el-timeline-item__node) {
                    background-image: linear-gradient(to right, #3F9DFD, #8BC34A);
                    transition: background-color 0.3s ease;
                    position: relative;
                }
                :deep(.el-timeline-item__line) {
                    background-image: linear-gradient(to right, #3F9DFD, #8BC34A); /* 设置渐变色 */
                }
                :deep(.el-timeline-item__tail) {
                    border-color:#3F9DFD; /* 设置渐变色 */
                }
                &:hover {
                    :deep(.el-timeline-item__node) {
                        background-image: linear-gradient(to right, #63adfd, #86f822);
                        transform: scale(1.4);
                    }
                }

                :deep(.el-timeline-item__timestamp) {
                    position: absolute;
                    top: -60px;
                    left: 50%;
                    transform: translateX(-50%);
                    font-size: 14px;
                    white-space: nowrap; /* 防止时间戳换行 */
                }
            }
        }
    }
}

:deep(.el-timeline-item__tail) {
    border-left: none;
    border-top: 2px solid #e4e7ed;
    width: 100%;
    position: absolute;
    top: 6px;
}
:deep(.el-timeline-item__wrapper) {
    padding-left: 0;
    position: absolute;
    top: 20px;
    transform: translateX(-50%);
    text-align: center;
}
:deep(.el-timeline-item__timestamp) {
    font-size: 14px;
}

:deep(.el-scrollbar__bar) {
    // display: none;
}

</style>