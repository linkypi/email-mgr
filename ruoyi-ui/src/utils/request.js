import axios from 'axios'
import { Notification, MessageBox, Message, Loading } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { tansParams, blobValidate } from "@/utils/ruoyi"
import cache from '@/plugins/cache'
import { saveAs } from 'file-saver'
import { isGuestUser, isRegularUser, isLimitedUser, isForbiddenForGuest, isForbiddenForRegularUser, isForbiddenForLimitedUser } from '@/utils/guestUserCheck'

let downloadLoadingInstance
// 是否显示重新登录
export let isRelogin = { show: false }

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 创建axios实例
const service = axios.create({
  // axios中请求配置有baseURL选项，表示请求URL公共部分
  baseURL: process.env.VUE_APP_BASE_API,
  // 超时
  timeout: 10000
})

// request拦截器
service.interceptors.request.use(config => {
  // 用户权限检查 - 阻止访问禁止的接口
  const roles = store.getters.roles || [];
  const isGuest = isGuestUser(roles);
  const isRegular = isRegularUser(roles);
  const isLimited = isLimitedUser(roles);
  
  // 检查各种禁止访问的情况
  const isGuestForbidden = isForbiddenForGuest(config.url);
  const isRegularForbidden = isForbiddenForRegularUser(config.url);
  const isLimitedForbidden = isForbiddenForLimitedUser(config.url);
  
  console.log('🔍 请求拦截器检查:', {
    url: config.url,
    roles: roles,
    rolesLength: roles.length,
    rolesType: typeof roles,
    rolesIsArray: Array.isArray(roles),
    isGuest: isGuest,
    isRegular: isRegular,
    isLimited: isLimited,
    isGuestForbidden: isGuestForbidden,
    isRegularForbidden: isRegularForbidden,
    isLimitedForbidden: isLimitedForbidden
  });
  
  // 根据用户角色阻止相应的禁止接口
  let shouldBlock = false;
  let blockReason = '';
  
  // 优先检查角色未加载的情况
  if (!roles.length && (isGuestForbidden || isRegularForbidden || isLimitedForbidden)) {
    console.log('❌ 角色未加载检查触发:', {
      rolesLength: roles.length,
      isGuestForbidden,
      isRegularForbidden,
      isLimitedForbidden,
      url: config.url
    });
    shouldBlock = true;
    blockReason = '用户角色未加载，无权限访问此接口';
  }
  // 检查访客用户权限
  else if (isGuest && isGuestForbidden) {
    shouldBlock = true;
    blockReason = '访客用户无权限访问此接口';
  }
  // 检查普通账号用户权限
  else if (isRegular && isRegularForbidden) {
    shouldBlock = true;
    blockReason = '普通账号用户无权限访问此接口';
  }
  // 注意：移除了兜底检查，因为我们已经明确检查了访客用户和普通账号用户
  
  if (shouldBlock) {
    console.log('🚫 用户尝试访问禁止的接口:', config.url);
    console.log('🚫 用户角色:', roles);
    console.log('🚫 角色长度:', roles.length);
    console.log('🚫 阻止原因:', blockReason);
    // 返回一个被拒绝的Promise，阻止请求，但不显示错误提示
    return Promise.reject({
      code: 403,
      message: blockReason,
      url: config.url,
      silent: true // 标记为静默错误，不显示给用户
    });
  }
  
  // 是否需要设置 token
  const isToken = (config.headers || {}).isToken === false
  // 是否需要防止数据重复提交
  const isRepeatSubmit = (config.headers || {}).repeatSubmit === false
  if (getToken() && !isToken) {
    config.headers['Authorization'] = 'Bearer ' + getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  // get请求映射params参数
  if (config.method === 'get' && config.params) {
    let url = config.url + '?' + tansParams(config.params)
    url = url.slice(0, -1)
    config.params = {}
    config.url = url
  }
  if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
    // 跳过文件上传请求的防重复提交检查
    if (config.data instanceof FormData) {
      return config
    }
    
    const requestObj = {
      url: config.url,
      data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
      time: new Date().getTime()
    }
    const requestSize = Object.keys(JSON.stringify(requestObj)).length // 请求数据大小
    const limitSize = 5 * 1024 * 1024 // 限制存放数据5M
    if (requestSize >= limitSize) {
      console.warn(`[${config.url}]: ` + '请求数据大小超出允许的5M限制，无法进行防重复提交验证。')
      return config
    }
    const sessionObj = cache.session.getJSON('sessionObj')
    if (sessionObj === undefined || sessionObj === null || sessionObj === '') {
      cache.session.setJSON('sessionObj', requestObj)
    } else {
      const s_url = sessionObj.url                  // 请求地址
      const s_data = sessionObj.data                // 请求数据
      const s_time = sessionObj.time                // 请求时间
      const interval = 1000                         // 间隔时间(ms)，小于此时间视为重复提交
      if (s_data === requestObj.data && requestObj.time - s_time < interval && s_url === requestObj.url) {
        const message = '数据正在处理，请勿重复提交'
        console.warn(`[${s_url}]: ` + message)
        return Promise.reject(new Error(message))
      } else {
        cache.session.setJSON('sessionObj', requestObj)
      }
    }
  }
  return config
}, error => {
    console.log(error)
    Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(res => {
    // 未设置状态码则默认成功状态
    const code = res.data.code || 200
    // 获取错误信息
    const msg = errorCode[code] || res.data.msg || errorCode['default']
    // 二进制数据则直接返回
    if (res.request.responseType ===  'blob' || res.request.responseType ===  'arraybuffer') {
      return res.data
    }
    if (code === 401) {
      if (!isRelogin.show) {
        isRelogin.show = true
        MessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', { confirmButtonText: '重新登录', cancelButtonText: '取消', type: 'warning' }).then(() => {
          isRelogin.show = false
          store.dispatch('LogOut').then(() => {
            location.href = '/index'
          })
      }).catch(() => {
        isRelogin.show = false
      })
    }
      return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
    } else if (code === 500) {
      // 检查是否跳过全局错误处理
      if (res.config && res.config.skipGlobalErrorHandler) {
        return Promise.reject(new Error(msg))
      }
      Message({ message: msg, type: 'error' })
      return Promise.reject(new Error(msg))
    } else if (code === 601) {
      Message({ message: msg, type: 'warning' })
      return Promise.reject('error')
    } else if (code !== 200) {
      Notification.error({ title: msg })
      return Promise.reject('error')
    } else {
      return res.data
    }
  },
  error => {
    console.log('err' + error)
    
    // 检查是否为静默错误（访客用户访问禁止接口的错误）
    if (error && error.silent) {
      console.log('静默错误，不显示给用户:', error.message)
      return Promise.reject(error)
    }
    
    let { message } = error
    if (message == "Network Error") {
      message = "后端接口连接异常"
    } else if (message.includes("timeout")) {
      message = "系统接口请求超时"
    } else if (message.includes("Request failed with status code")) {
      message = "系统接口" + message.substr(message.length - 3) + "异常"
    }
    Message({ message: message, type: 'error', duration: 5 * 1000 })
    return Promise.reject(error)
  }
)

// 通用下载方法
export function download(url, params, filename, config) {
  downloadLoadingInstance = Loading.service({ text: "正在下载数据，请稍候", spinner: "el-icon-loading", background: "rgba(0, 0, 0, 0.7)", })
  return service.post(url, params, {
    transformRequest: [(params) => { return tansParams(params) }],
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    responseType: 'blob',
    ...config
  }).then(async (data) => {
    const isBlob = blobValidate(data)
    if (isBlob) {
      const blob = new Blob([data])
      saveAs(blob, filename)
    } else {
      const resText = await data.text()
      const rspObj = JSON.parse(resText)
      const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
      Message.error(errMsg)
    }
    downloadLoadingInstance.close()
  }).catch((r) => {
    console.error(r)
    Message.error('下载文件出现错误，请联系管理员！')
    downloadLoadingInstance.close()
  })
}

export default service
