import axios from 'axios'
import { Message } from 'element-ui'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.BASE_API,
  timeout: 5000
})

// request拦截器
service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 3 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
