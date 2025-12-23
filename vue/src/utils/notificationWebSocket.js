// WebSocket 通知工具类
class NotificationWebSocket {
  constructor() {
    this.ws = null
    this.userId = null
    this.reconnectTimer = null
    this.heartbeatTimer = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.listeners = []
  }

  /**
   * 连接 WebSocket
   * @param {number} userId - 用户ID
   */
  connect(userId) {
    if (!userId) {
      console.error('WebSocket 连接失败: userId 不能为空')
      return
    }

    this.userId = userId

    // WebSocket 服务器地址 (注意添加 context-path /api/v1)
    const wsUrl = `ws://localhost:8080/api/v1/ws/notification/${userId}`

    try {
      this.ws = new WebSocket(wsUrl)

      // 连接成功
      this.ws.onopen = () => {
        console.log('✅ WebSocket 连接成功')
        this.reconnectAttempts = 0
        this.startHeartbeat()
      }

      // 接收消息
      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          console.log('📩 收到 WebSocket 消息:', data)

          // 通知所有监听器
          this.notifyListeners(data)

          // 处理不同类型的消息
          if (data.type === 'notification') {
            this.handleNotification(data)
          }
        } catch (error) {
          console.error('解析 WebSocket 消息失败:', error)
        }
      }

      // 连接关闭
      this.ws.onclose = () => {
        console.log('❌ WebSocket 连接关闭')
        this.stopHeartbeat()
        this.reconnect()
      }

      // 连接错误
      this.ws.onerror = (error) => {
        console.error('❌ WebSocket 错误:', error)
      }
    } catch (error) {
      console.error('WebSocket 连接异常:', error)
      this.reconnect()
    }
  }

  /**
   * 断开连接
   */
  disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    this.stopHeartbeat()
    this.clearReconnectTimer()
  }

  /**
   * 重新连接
   */
  reconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('WebSocket 重连次数超过限制，停止重连')
      return
    }

    this.clearReconnectTimer()

    this.reconnectAttempts++
    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000)

    console.log(`🔄 ${delay/1000}秒后尝试第 ${this.reconnectAttempts} 次重连...`)

    this.reconnectTimer = setTimeout(() => {
      this.connect(this.userId)
    }, delay)
  }

  /**
   * 清除重连定时器
   */
  clearReconnectTimer() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
  }

  /**
   * 启动心跳检测
   */
  startHeartbeat() {
    this.heartbeatTimer = setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send(JSON.stringify({ type: 'heartbeat' }))
      }
    }, 30000) // 每30秒发送一次心跳
  }

  /**
   * 停止心跳检测
   */
  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer)
      this.heartbeatTimer = null
    }
  }

  /**
   * 处理通知消息
   */
  handleNotification(data) {
    // 显示浏览器通知（需要用户授权）
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification(data.title, {
        body: data.content,
        icon: '/logo.png',
        tag: `notification-${data.relatedId}`
      })
    }

    // 可以在这里添加其他处理逻辑，比如更新未读数量
  }

  /**
   * 添加消息监听器
   * @param {Function} callback - 回调函数
   */
  addListener(callback) {
    if (typeof callback === 'function') {
      this.listeners.push(callback)
    }
  }

  /**
   * 移除消息监听器
   * @param {Function} callback - 回调函数
   */
  removeListener(callback) {
    this.listeners = this.listeners.filter(listener => listener !== callback)
  }

  /**
   * 通知所有监听器
   */
  notifyListeners(data) {
    this.listeners.forEach(callback => {
      try {
        callback(data)
      } catch (error) {
        console.error('监听器执行失败:', error)
      }
    })
  }

  /**
   * 请求浏览器通知权限
   */
  static requestNotificationPermission() {
    if ('Notification' in window && Notification.permission === 'default') {
      Notification.requestPermission().then(permission => {
        console.log('通知权限:', permission)
      })
    }
  }
}

// 创建单例
const notificationWS = new NotificationWebSocket()

export default notificationWS

