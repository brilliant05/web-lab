import axios  from "axios";
import {ElMessage} from '@element-plus';

const http = axios.create({
    baseURL: 'https://localhost:8080/',
    timeout: 30000,
});

//request拦截器
//自动对请求发起前进行一些处理
request.interceptors.request.use(config => {
    config.headers['content-type'] = 'application/json;charset=UTF-8';
    return config;
},error =>{
    return Promise.reject(error);
});
//response拦截器
request.interceptors.response.use(response => {
    let res = response.data;
    if(typeof res === 'string'){
        res=res?JSON.parse(res):res;
    }
    return res;
},error =>{
    if(error.response.status === 404){
        ElMessage.error('未找到请求接口')
    }else if(error.response.status === 500){
        ElMessage.error('系统异常，请查看系统后台报错')
    }else {
        console.log(error.message)
    }
    return Promise.reject(error)
})

export default http