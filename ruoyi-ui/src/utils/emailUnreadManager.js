import { getInboxUnreadCount, getSentUnreadCount, getStarredUnreadCount, getDeletedUnreadCount } from "@/api/email/personal";

class EmailUnreadManager {
  constructor() {
    this.counts = {
      inbox: 0,
      sent: 0,
      starred: 0,
      deleted: 0
    };
    this.listeners = [];
    this.timer = null;
    this.isInitialized = false;
  }

  // 初始化管理器
  async init() {
    if (this.isInitialized) return;
    
    await this.fetchAllCounts();
    this.startAutoRefresh();
    this.isInitialized = true;
  }

  // 获取所有未读数量
  async fetchAllCounts() {
    try {
      const [inboxCount, sentCount, starredCount, deletedCount] = await Promise.all([
        getInboxUnreadCount().catch(() => ({ data: 0 })),
        getSentUnreadCount().catch(() => ({ data: 0 })),
        getStarredUnreadCount().catch(() => ({ data: 0 })),
        getDeletedUnreadCount().catch(() => ({ data: 0 }))
      ]);

      this.counts = {
        inbox: inboxCount.data || 0,
        sent: sentCount.data || 0,
        starred: starredCount.data || 0,
        deleted: deletedCount.data || 0
      };

      this.notifyListeners();
    } catch (error) {
      console.error('获取未读数量失败:', error);
    }
  }

  // 获取指定类型的未读数量
  getCount(type) {
    return this.counts[type] || 0;
  }

  // 获取所有未读数量
  getAllCounts() {
    return { ...this.counts };
  }

  // 手动刷新指定类型的未读数量
  async refreshCount(type) {
    try {
      let response;
      switch (type) {
        case 'inbox':
          response = await getInboxUnreadCount();
          break;
        case 'sent':
          response = await getSentUnreadCount();
          break;
        case 'starred':
          response = await getStarredUnreadCount();
          break;
        case 'deleted':
          response = await getDeletedUnreadCount();
          break;
        default:
          return;
      }
      
      this.counts[type] = response.data || 0;
      this.notifyListeners();
    } catch (error) {
      console.error(`刷新${type}未读数量失败:`, error);
    }
  }

  // 手动刷新所有未读数量
  async refreshAll() {
    await this.fetchAllCounts();
  }

  // 开始自动刷新
  startAutoRefresh() {
    if (this.timer) {
      clearInterval(this.timer);
    }
    // 每60秒自动刷新一次
    this.timer = setInterval(() => {
      this.fetchAllCounts();
    }, 60000);
  }

  // 停止自动刷新
  stopAutoRefresh() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }

  // 添加监听器
  addListener(callback) {
    this.listeners.push(callback);
  }

  // 移除监听器
  removeListener(callback) {
    const index = this.listeners.indexOf(callback);
    if (index > -1) {
      this.listeners.splice(index, 1);
    }
  }

  // 通知所有监听器
  notifyListeners() {
    this.listeners.forEach(callback => {
      try {
        callback(this.counts);
      } catch (error) {
        console.error('通知监听器失败:', error);
      }
    });
  }

  // 销毁管理器
  destroy() {
    this.stopAutoRefresh();
    this.listeners = [];
    this.isInitialized = false;
  }
}

// 创建单例实例
const emailUnreadManager = new EmailUnreadManager();

export default emailUnreadManager;


