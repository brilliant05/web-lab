import http from '../utils/request'

// 认证相关 API
export const authApi = {
  // 登录
  login: (data) => {
    return http.post('/auth/login', data)
  },
  
  // 注册
  register: (data) => {
    return http.post('/auth/register', data)
  },
  
  // 退出登录
  logout: () => {
    return http.post('/auth/logout')
  },
  
  // 获取当前用户信息
  getCurrentUser: () => {
    return http.get('/users/current')
  }
}

