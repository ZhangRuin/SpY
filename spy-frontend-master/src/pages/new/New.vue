<script setup>
import { CloseBold, Edit, Search } from '@element-plus/icons-vue';
import axios from "axios";
import { ElMessage, ElMessageBox } from 'element-plus';
import { inject, onMounted, ref } from "vue";
import Credibility from './components/Credibility.vue';
import Gauge from './components/Gauge.vue';
import Graph from './components/Graph.vue';
import ItemTimeline from './components/ItemTimeline.vue';
const url=inject('url');
console.log(url);
const drawer = ref(false);
const currentId = ref(0);
const pageSize = ref(10);
const currentPage = ref(1);
const totalNum = ref(0);
const currentNews = ref([])
const currentNews_dateFormat = ref([])
const updateDrawer = ref(false);
const addDrawer = ref(false);
let toBeUpdated = ref();
let toBeAdded = ref();
const maxPage = ref();
let submitClickable =ref(true)
const size = ref('large');
const loading = ref(true)

//new
const input = ref('')
const StartandEndTime = ref('')
const sourceList = ref([])
const source = ref('')
async function updataNews() {
  try {
    loading.value = true;
    const res = await axios.get(url+'spy/news/criteria', {
      params: {
        pageSize: pageSize.value,
        currentPage: currentPage.value,
        keyword: input.value,
        startDate: formatTime(StartandEndTime.value[0]),
        endDate: formatTime(StartandEndTime.value[1]),
        source: source.value
      }
    });
    console.log(res)
    currentNews.value = formatDates(res.data.result.list);
    totalNum.value = res.data.result.totalCount;
    loading.value = false;
  } catch (error) {
    console.log(error);
  }
}


const searchOrSelect = async () => {
  currentPage.value = 1;
  updataNews();
}

const formatTime = (date) => {
  if (StartandEndTime.value.length == 0) {
    return null;
  }
  console.log(date)
  const year = date.getFullYear();
  const month = date.getMonth() + 1; // 月份从0开始，需要加1
  const day = date.getDate();
  return `${year}-${month}-${day}`;
};

function formatDates(list) {
  return list.map(item => {
    const date = new Date(item.date);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    item.date = `${year}-${month}-${day}`;
    return item;
  });
}

function generateOptions(data) {
  return data.map((item, index) => {
    return {
      value: item,
      label: item
    };
  });
}

const getSources = async () => {
  try {
    const res = await axios.get(url+'spy/news/sources',);
    console.log("getSources");
    console.log(res);
    sourceList.value = generateOptions(res.data.result);
  } catch (error) {
    console.log(error);
  }
};

const getNews = async () => {
  try {
    const res = await axios.get(url+'spy/news/criteria', {
      params: {
        pageSize: pageSize.value,
        currentPage: currentPage.value,
        keyword: '',
        startDate: '',
        endDate: '',
        source: ''
      }
    });
    console.log("getNews");
    currentNews.value = formatDates(res.data.result.list);
  } catch (error) {
    console.log(error);
  }
};

const load = async () => {
  try {
    input.value = '';
    StartandEndTime.value = '';
    source.value = '';
    currentPage.value = 1;
    if (sourceList.value.length == 0) {
      getSources();
    }
    getNews();
    axios.get(url+'spy/news/count')
      .then(function (res) {
        totalNum.value = res.data.result;
        // console.log("totalNum:"+totalNum.value)
      })
      .catch(function (error) {
        console.log(error);
      });
  } catch (error) {
    console.log(error);
  }
};
onMounted(() => {
  load();

});
const handleClicks = (index) => {
  currentId.value = index;
  drawer.value = true;
}

const handleCurrentChange = async (current) => {
  // console.log(currentPage.value)
  currentPage.value = current;
  updataNews();
}
const post = async () => {
    try {
        axios.post(url+'spy/news',{
            title:toBeAdded.value.title,
            date:toBeAdded.value.date,
            content:toBeAdded.value.content,
            source:toBeAdded.value.source,
            categories:[]

        })
            .then(function (res) {
                console.log("Post Success!")
                console.log(res.data.message)
                if(res.data.code==="2000") {
                    updateSuccess()
                    updataNews()
                    addDrawer.value = false
                }
                // console.log("totalNum:"+totalNum.value)
            })
            .catch(function (error) {
                console.log("Post Error!")
                console.log(error);
                submitFail()
            });
    } catch (error) {
        console.log("Post Error!")
        console.log(error);
        submitFail()

    }
};
const put = async () => {
    try {
      axios.put(url+'spy/news/'+toBeUpdated.value.id,{
            title:toBeUpdated.value.title,
            date:toBeUpdated.value.date,
            content:toBeUpdated.value.content,
            source:toBeUpdated.value.source,
            categories:[]

        })
            .then(function (res) {
                console.log("Put Success!")
                console.log(res.data.message)
                if(res.data.code==="2000") {
                    updateSuccess()
                    updataNews()
                    updateDrawer.value = false
                }
            })
            .catch(function (error) {
                console.log("Put Error!")
                console.log(error);
                submitFail()
            });
    } catch (error) {
        console.log("Put Error!")
        console.log(error);
        submitFail()
    }
};

const deleteNews = async (id) => {
    try {
      axios.delete(url+'spy/news/'+id,{
        })
            .then(function (res) {
                console.log("Delete Success!")
                console.log(res.data.message)
                if(res.status===204) {
                    deleteSuccess()
                    updataNews()
                }
            })
            .catch(function (error) {
                console.log("Put Error!")
                console.log(error);
            });
    } catch (error) {
        console.log("Put Error!")
        console.log(error);
    }
};
onMounted(() => {
  updataNews();

});

const updateSuccess = () => {
  ElMessage({
    message: '提交成功',
    type: 'success'
  });
};
const submitFail=()=>{
    ElMessage({
        message: '提交失败，请稍后再试',
        type: 'error'
    });
}
const disabledDate = (time) => {
  return time.getTime() > Date.now(); // 当前时间之后的日期不可用
};

const pickerOptions = {
  disabledDate, // 将disabledDate方法传递给pickerOptions
};

const titleEmpty = () => {
  ElMessage({
    message: '标题不能为空',
    type: 'error'
  });
};
const sourceEmpty = () => {
  ElMessage({
    message: '来源不能为空',
    type: 'error'
  });
};
const contentEmpty = () => {
  ElMessage({
    message: '内容不能为空',
    type: 'error'
  });
};
const deleteSuccess = () => {
  ElMessage({
    message: '删除成功',
    type: 'success'
  });
};
const drawerClose = () => {
  ElMessageBox.confirm('确定要不保存退出吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(_ => {
      addDrawer.value = false;
      updateDrawer.value = false;
    })
    .catch(_ => { });
}
const deleteMessageBox = (id) => {
  ElMessageBox.confirm('此操作将永久删除内容', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(_ => {
      deleteNews(id)
    })
    .catch(_ => { });
}

const updateClick = (index) => {
  currentId.value = index + (currentPage.value - 1) * pageSize.value;
  updateDrawer.value = true;
    let v = currentNews.value[currentId.value]
    toBeUpdated.value = {id:v.id,title:v.title,source:v.source,date:v.date,content:v.content};
    console.log(toBeUpdated.value)
}
const doneUpdateDrawer=()=>{
    console.log(toBeUpdated)
    if(toBeUpdated.value.title===""){
        titleEmpty();
        return
    }
    if(toBeUpdated.value.source===""){
        sourceEmpty();
        return
    }if(toBeUpdated.value.content===""){
        contentEmpty();
        return
    }
    put()
}
const doneAddDrawer=()=>{
    console.log(toBeAdded)

    if(toBeAdded.value.title===""){
        titleEmpty();
        return
    }
    if(toBeAdded.value.source===""){
        sourceEmpty();
        return
    }if(toBeAdded.value.content===""){
        contentEmpty();
        return
    }
    post()

}

const addClick=()=>{
    addDrawer.value =true;
    toBeAdded.value = {id:0,title:"",source:"",date:Date.now(),content:""};
}

const contentChange = ()=>{
    submitClickable.value=false;
}

function showKeyWord(val) {
    // 使用正则表达式替换 <em> 标签为带有样式的 <span> 标签
    return val.replace(/<em>(.*?)<\/em>/g, '<span style="color: #da2f40">$1</span>');
}

</script>

<template>
  <div class="table">
    <div  style="width: 100%; display: flex;flex-direction: row;justify-content: space-between;flex-wrap:wrap;">
      <div style="display:flex;flex-direction: row;flex-wrap: wrap;">
        <el-input @change="searchOrSelect()" v-model="input"  :size="size" style="margin-right:15px;margin-bottom: 16px;width:250px" placeholder="输入搜索内容"
          class="searchInput" />
        <div class="demo-date-picker">
          <div class="block">
            <el-date-picker v-model="StartandEndTime" type="daterange" range-separator="To" start-placeholder="起始时间"
              end-placeholder="截止时间" :size="size" style="margin-right: 15px;width:250px" />
          </div>
        </div>
        <el-select v-model="source" placeholder="选择来源"  :size="size" style="margin-right: 15px; width:250px">
          <el-option v-for="item in sourceList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button @click="searchOrSelect()" type="primary" :icon="Search" size="large"
          style="; margin-right: 15px;">搜索</el-button>
        <el-button color="#DA2F40"
          style=" color-scheme: #3F9DFD; margin-bottom: 16px; margin-right: 15px;margin-left: 0px;"   :size="size" type="primary"
          :icon="CloseBold" @click="load()">重置</el-button>
      </div>
      
      <el-button  :size="size" style="color-scheme: #3F9DFD; margin-bottom: 16px; margin-right: 15px;"
        type="primary" :icon="Edit" @click="addClick()">添加新闻</el-button>

    </div>
   
    <el-table v-loading="loading" :data="currentNews" border size="large" style="width: 100%" table-layout="auto">
      <el-table-column prop="id" label="编号" align="center" width="120px" />
      <el-table-column prop="title" label="标题" width="" >
        <template #default="scope">
          <span v-html="showKeyWord( currentNews[scope.$index].title )"></span>
        </template>

      </el-table-column>
      <el-table-column prop="source" label="来源" align="center" width="160px" />
      <el-table-column prop="date" label="时间" align="center" width="160px"/>
      <el-table-column label="操作" align="center" width="160px">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleClicks(scope.$index)">查看</el-button>
          <el-button link type="primary" size="small" @click="updateClick(scope.$index)">修改</el-button>
          <el-button link type="danger" size="small"
            @click="deleteMessageBox(currentNews[scope.$index].id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div  style="width: 100%; display: flex;flex-direction: row;justify-content: space-between;flex-wrap:wrap;">
      <div style="color:rgb(129, 129, 129);margin:14px;font-size: 12px;">共{{totalNum}}条新闻</div>
      <el-pagination @current-change="handleCurrentChange"  layout="prev, pager, next" :total="totalNum"
      :current-page="currentPage" :page-size="pageSize" size="large" style="background-color:#ffffff; align-self: flex-end; margin-top: 16px;" />

    </div>

    <el-drawer v-model="drawer" title="新闻详情" direction="rtl" style="text-align: justify; " size="90%" >
      <div style="display: flex; flex-direction: row;max-width: 100%">
        <div style="flex:1 1 0 ;max-width: calc(100% - 596px);">
          <el-form label-position="left" label-width="60px" style="margin:8px 16px 8px 8px;" :inline="true" >
              <el-form-item label="编号" style="margin-right: 120px;">
                <div>{{ currentNews[currentId].id }}</div>
              </el-form-item>
              <el-form-item label="来源" style="margin-right: 120px;">
                <div>{{ currentNews[currentId].source }}</div>
              </el-form-item>
              <el-form-item label="时间" style="margin-right: 60px;">
                <div>{{ currentNews[currentId].date }}</div>
              </el-form-item>
          </el-form>
          <el-form label-position="left" label-width="60px" style="margin:8px 16px 8px 8px;" >
              <el-form-item label="标题">
                <div>{{ currentNews[currentId].title }}</div>
              </el-form-item>
          </el-form>
            <el-form label-position="left" label-width="60px" style="margin:8px 16px 8px 8px;" >
                <el-form-item label="内容">
                  <div>{{ currentNews[currentId].content }}</div>
                </el-form-item>
                <el-form-item label="进度" style="">
                    <ItemTimeline :news-number=currentId />
                </el-form-item>
            </el-form>
        </div>
        <div style="display: flex; flex-direction: row ;">
          <el-form label-position="left" label-width="80px" style="margin:8px;width:100%" >

            <el-form-item label="立场" style="width: 100%;">
              <Gauge :standPoint="currentNews[currentId].standPoint"/>
            </el-form-item>
            <el-form-item label="可信度" style="width: 100%;">
              <Credibility :newsId="currentNews[currentId].id"/>
            </el-form-item>
            <el-form-item label="知识图谱" style="width: 100%;">
              <Graph :newsId="currentNews[currentId].id"/>
            </el-form-item>
          </el-form>
        </div>
      </div>  
    </el-drawer>

    <el-drawer v-model="updateDrawer" title="修改内容" direction="rtl" style="text-align: justify; " size="40%"
      :before-close="drawerClose">
      <el-form label-position="left" label-width="80px" style="margin:8px">
        <el-form-item label="编号">
          <div>{{ toBeUpdated.id }}</div>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="toBeUpdated.title" autocomplete="off" @change="contentChange()"></el-input>
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="toBeUpdated.source" autocomplete="off" @change="contentChange()"></el-input>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker v-model="toBeUpdated.date" type="datetime" placeholder="选择日期时间" default-time="12:00:00"
            :disabled-date="disabledDate" @change="contentChange()">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="toBeUpdated.content" autocomplete="off" type="textarea" size="large"
            :autosize="{ minRows: 10, maxRows:16} " @change="contentChange()"></el-input>
        </el-form-item>
      </el-form>
      <div class="demo-drawer__footer">
        <el-button style="float: right; margin-bottom: 16px;" type="primary" @click="doneUpdateDrawer()" size="large"
          :disabled="submitClickable">提交</el-button>
        <el-button style="float: right; margin-right: 16px;" @click="drawerClose()" size="large">取 消</el-button>
      </div>
    </el-drawer>
    <el-drawer v-model="addDrawer" title="添加新闻" direction="rtl" style="text-align: justify; " size="40%"
      :before-close="drawerClose">
      <el-form label-position="left" label-width="80px" style="margin:8px">

        <el-form-item label="标题">
          <el-input v-model="toBeAdded.title" autocomplete="off" @change="contentChange()"></el-input>
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="toBeAdded.source" autocomplete="off" @change="contentChange()"></el-input>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker v-model="toBeAdded.date" type="datetime" placeholder="选择日期时间" default-time="12:00:00"
            :disabled-date="disabledDate" @change="contentChange()">
          </el-date-picker>
          <div>{{ toBeAdded.time }}</div>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="toBeAdded.content" autocomplete="off" type="textarea" size="large" @change="contentChange()"
            :autosize="{ minRows: 10, maxRows:16}"></el-input>
        </el-form-item>
      </el-form>
      <div class="demo-drawer__footer">
        <el-button style="float: right; margin-bottom: 16px;" type="primary" @click="doneAddDrawer()" size="large"
          :disabled="submitClickable">提交</el-button>
        <el-button style="float: right; margin-right: 16px;" @click="drawerClose()" size="large">取 消</el-button>
      </div>
    </el-drawer>
  </div>


</template>

<style scoped>
.table {
  position:relative;
  display: flex;
  flex-direction: column;
  width: auto;
  margin:24px 24px 16px 24px;
  padding: 24px 24px 16px 24px;
  background-color: white;
  border-radius: 8px;
  transition: all 0.3s;
  /* animation: PopUp 0.5s 1 cubic-bezier(0.4, 0, 0.2, 1); */
}

.demo-date-picker {
  display: flex;
  padding: 0;
  flex-wrap: wrap;

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
:deep(.el-drawer__header){
  margin-bottom: 0px;
}
:deep(.el-drawer__body){
  padding-top:6px;
}
</style>
