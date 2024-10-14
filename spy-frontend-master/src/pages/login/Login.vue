
<script setup>
import axios from 'axios';
import { reactive, ref ,inject, provide} from 'vue';
import { useRouter } from 'vue-router';
import TextBox from '../../components/TextBox.vue';
import { ElMessage } from 'element-plus'
const tabIndex =ref(0)
const url = inject('url');
const router = useRouter();
const form = reactive({
    name: '',
    password: '',
})
const form2 = reactive({
    name: '',
    password: '',
    password2: '',
})
async function onSubmit() {
    if (form.name === '' || form.password === '') {
        ElMessage({
            message: '账号或密码不能为空',
            type: 'error',
        })
        return 
    }
    try{
        const res = await axios.get(url+'usr/doLogin', {
            params:{
                userId: form.name,
                pwd: form.password
            }
        })
        if(res.data.code === 200){
            ElMessage({
                message: '登录成功！',
                type: 'success',
            })

            router.push('/home')
        }
        else{
            ElMessage({
                message: '登录失败',
                type: 'error',
            })
        }
    } catch(e){
        ElMessage({
            message: '登录失败',
            type: 'error',
        })
    }
}
async function onSubmit2() {
    if (form2.name === '' || form2.password === '' || form2.password2 === '') {
        ElMessage({
            message: '账号或密码不能为空',
            type: 'error',
        })
        return 
    }
    if(form2.password !== form2.password2){
        ElMessage({
            message: '密码不一致',
            type: 'error',
        })
        return 
    }
    try{
        const res = await axios.post(url+'usr/register',{}, {
            params:{
                userId: form2.name,
                pwd: form2.password
            }
        })
        if(res.data.code === 200){
            ElMessage({
                message: '注册成功！',
                type: 'success',
            })
            router.push('/home')
        }
        else{
            ElMessage({
                message: '注册失败',
                type: 'error',
            })
        }
    } catch(e){
        ElMessage({
            message: '注册失败',
            type: 'error',
        })
    }

}
</script>

<template>
    <div class="login" >
        <div style="flex-direction: column;display: flex;min-width: 400px;">
            <TextBox class="title" style="margin-bottom: 20px;" :boxShadow=false color="black" textContent="SpY情报分析系统">
                
            </TextBox>
            <TextBox textContent="开源情报分析网站" style="text-align: center;font-size: 32px;color: #000;font-weight:bolder;"/>
        </div>
        <div class="login-box">
                <TextBox class="form-title" textContent="Login to SpY">     
                </TextBox>
                <transition name="tab-transform" mode="out-in">
                    <div v-if="tabIndex === 0" style="width: 100%;display: flex;flex-direction: column;align-items: center;">
                    <input type="text" v-model="form.name" name="username" placeholder="请输入用户账号" class="input-item">
                    <input type="password" v-model="form.password" name="password" id="pwd" placeholder="请输入密码"
                            class="input-item" style=" margin-top: 20px;">
                        <div  style="width: 100%; display: flex;flex-direction: row;align-items: center;justify-content: end;margin: 16px 0 0 0;">
                            <div style="margin-right: 20px; color:#409eff; cursor: pointer;" class="textBut" @click="tabIndex = 1">注册账号</div>
                        </div>
                        <div class="btn" @click="onSubmit">登录</div>
                    </div>
                    <div v-else style="width: 100%;display: flex;flex-direction: column;align-items: center;">
                        <input type="text" v-model="form2.name" name="username" placeholder="请输入用户账号" class="input-item">
                        <input type="password" v-model="form2.password" name="password" id="pwd" placeholder="请输入密码" class="input-item" style=" margin-top: 20px;">  
                        <input type="password" v-model="form2.password2" name="password2" id="pwd2" placeholder="请确认密码" class="input-item" style=" margin-top: 20px;">  
                        <div  style="width: 100%; display: flex;flex-direction: row;align-items: center;justify-content: end;margin: 16px 0 0 0;">
                            <div style="margin-right: 20px; color:#409eff; cursor: pointer;" class="textBut" @click="tabIndex = 0">登录</div>
                        </div>
                        <div class="btn" @click="onSubmit2">注册</div>
                    </div>   
                </transition>     
        </div>
    </div>

</template>

<style scoped>
.textBut{
    border-bottom:solid 1px #40a0ff00;
}
.textBut:hover{
    border-bottom:solid 1px #409eff;
}
.tab-transform-leave-active{
  transition: all 0.2s;
}
.tab-transform-enter-active {
  transition: all 0.3s;

}
.tab-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.tab-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

</style>
