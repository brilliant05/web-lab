import axios from "axios";
import { ElMessage } from 'element-plus';

const http = axios.create({
    baseURL: 'http://localhost:8080/api/v1',  // 修复: http而非https
    timeout: 30000,
});

//request拦截器
//自动对请求发起前进行一些处理
http.interceptors.request.use(config => {
    config.headers['content-type'] = 'application/json;charset=UTF-8';
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`; // 触发后端拦截器校验
    }
    return config;
},error =>{
    return Promise.reject(error);
});
//response拦截器
http.interceptors.response.use(response => {
    let res = response.data;
    if(typeof res === 'string'){
        res = res ? JSON.parse(res) : res;
    }
    
    // 统一处理业务错误码
    if(res.code === 200){
        return res;  // 成功
    } else if(res.code === 401){
        ElMessage.error(res.message || '未登录或登录已过期');
        localStorage.removeItem('token');
        window.location.href = '/login';
        return Promise.reject(res);
    } else {
        ElMessage.error(res.message || '操作失败');
        return Promise.reject(res);
    }
}, error => {
    // HTTP状态码错误处理
    if(error.response){
        const status = error.response.status;
        const data = error.response.data;
        
        if(status === 401){
            ElMessage.error(data.message || '登录过期，请重新登录');
            localStorage.removeItem('token');
            window.location.href = '/login';
        } else if(status === 403){
            ElMessage.error('没有权限访问');
        } else if(status === 404){
            ElMessage.error('请求的资源不存在');
        } else if(status === 500){
            ElMessage.error(data.message || '服务器内部错误');
        } else {
            ElMessage.error(data.message || '请求失败');
        }
    } else if(error.request){
        ElMessage.error('网络错误，请检查网络连接');
    } else {
        ElMessage.error(error.message || '请求配置错误');
    }
    
    return Promise.reject(error);
})

export default http