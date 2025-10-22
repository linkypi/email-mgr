<template>
  <div class="app-container">
    <!-- 邮件工具栏 -->
    <div class="mail-toolbar">
      <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
      
      <el-button size="small" icon="el-icon-refresh" @click="handleRefresh">刷新</el-button>
      <el-button size="small" icon="el-icon-star-on" @click="handleMarkAsStarred" :disabled="multiple">标记星标</el-button>
      <el-button size="small" icon="el-icon-delete" @click="handleDelete" :disabled="multiple" type="danger">删除</el-button>
      
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索邮件..."
          prefix-icon="el-icon-search"
          size="small"
          @keyup.enter.native="handleSearch"
          clearable
        />
      </div>
      
      <el-button size="small" icon="el-icon-filter" @click="showFilterDialog = true">筛选</el-button>
    </div>

    <!-- 邮件视图区域 -->
    <div class="mail-view">
      <!-- 邮件列表 -->
      <div class="mail-list">
        <div class="list-header">
          <div>发件箱 ({{ total }})</div>
          <div>{{ (queryParams.pageNum - 1) * queryParams.pageSize + 1 }}-{{ Math.min(queryParams.pageNum * queryParams.pageSize, total) }}封，共{{ total }}封</div>
        </div>

        <div class="mail-list-content" v-loading="loading">
          <div
            v-for="mail in emailList"
            :key="mail.id"
            class="mail-item"
            :class="{ 
              'unread': mail.isRead === 0,
              'selected': selectedMail && selectedMail.id === mail.id 
            }"
            @click="handleMailClick(mail)"
          >
            <div class="item-checkbox">
              <el-checkbox 
                v-model="mail.selected" 
                @change="handleMailSelect(mail)"
                @click.stop
              />
            </div>
            
            <div class="item-star">
              <i 
                class="el-icon-star-on" 
                :class="{ 'starred': mail.isStarred === 1 }"
                @click.stop="handleToggleStar(mail)"
              ></i>
            </div>
            
            <div class="item-content">
              <div class="mail-header">
                <div class="recipient">{{ mail.recipient }}</div>
                <div class="time">{{ formatTime(mail.sentTime) }}</div>
              </div>
              <div class="mail-subject">{{ mail.subject }}</div>
              <div class="mail-preview">{{ getContentPreview(mail.content) }}</div>
              <div class="mail-status">
                <el-tag 
                  :type="getStatusType(mail.status)" 
                  size="mini"
                >
                  {{ getStatusText(mail.status) }}
                </el-tag>
                <span v-if="mail.openedTime" class="opened-time">
                  已读 {{ formatTime(mail.openedTime) }}
                </span>
                <span v-if="mail.repliedTime" class="replied-time">
                  已回复 {{ formatTime(mail.repliedTime) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="queryParams.pageNum"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="queryParams.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          />
        </div>
      </div>

      <!-- 邮件详情 -->
      <div class="mail-detail" v-if="selectedMail">
        <div class="detail-header">
          <h3>{{ selectedMail.subject }}</h3>
          <div class="detail-actions">
            <el-button size="small" @click="handleReply">回复</el-button>
            <el-button size="small" @click="handleForward">转发</el-button>
            <el-button size="small" @click="handleDelete">删除</el-button>
          </div>
        </div>
        
        <div class="detail-content">
          <div class="detail-info">
            <div><strong>收件人:</strong> {{ selectedMail.recipient }}</div>
            <div><strong>发送时间:</strong> {{ formatTime(selectedMail.sentTime) }}</div>
            <div><strong>状态:</strong> 
              <el-tag :type="getStatusType(selectedMail.status)" size="small">
                {{ getStatusText(selectedMail.status) }}
              </el-tag>
            </div>
            <div v-if="selectedMail.deliveredTime">
              <strong>送达时间:</strong> {{ formatTime(selectedMail.deliveredTime) }}
            </div>
            <div v-if="selectedMail.openedTime">
              <strong>打开时间:</strong> {{ formatTime(selectedMail.openedTime) }}
            </div>
            <div v-if="selectedMail.repliedTime">
              <strong>回复时间:</strong> {{ formatTime(selectedMail.repliedTime) }}
            </div>
            <div v-if="selectedMail.clickedTime">
              <strong>点击时间:</strong> {{ formatTime(selectedMail.clickedTime) }}
            </div>
          </div>
          
          <div class="detail-body" v-html="selectedMail.content"></div>
        </div>
      </div>
    </div>

    <!-- 筛选对话框 -->
    <el-dialog title="筛选邮件" :visible.sync="showFilterDialog" width="500px">
      <el-form :model="filterForm" label-width="80px">
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="已发送" value="SENT"></el-option>
            <el-option label="已送达" value="DELIVERED"></el-option>
            <el-option label="已打开" value="OPENED"></el-option>
            <el-option label="已回复" value="REPLIED"></el-option>
            <el-option label="已点击" value="CLICKED"></el-option>
            <el-option label="发送失败" value="FAILED"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="收件人">
          <el-input v-model="filterForm.recipient" placeholder="请输入收件人"></el-input>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showFilterDialog = false">取消</el-button>
        <el-button type="primary" @click="handleFilter">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSentList, markAsStarred, unmarkAsStarred, markAsRead, moveToDeleted } from '@/api/email/personalTrack'

export default {
  name: 'SentTrack',
  data() {
    return {
      // 邮件列表数据
      emailList: [],
      selectedMail: null,
      selectedMails: [],
      selectAll: false,
      multiple: true,
      loading: false,
      total: 0,
      
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        folderType: 'sent',
        subject: null,
        recipient: null,
        status: null
      },
      
      // 搜索关键词
      searchKeyword: '',
      
      // 筛选对话框
      showFilterDialog: false,
      filterForm: {
        status: '',
        recipient: '',
        dateRange: []
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询发件箱列表 */
    getList() {
      this.loading = true
      getSentList(this.queryParams).then(response => {
        this.emailList = response.rows.map(item => ({
          ...item,
          selected: false
        }))
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    
    /** 搜索邮件 */
    handleSearch() {
      this.queryParams.subject = this.searchKeyword
      this.queryParams.pageNum = 1
      this.getList()
    },
    
    /** 刷新列表 */
    handleRefresh() {
      this.getList()
    },
    
    /** 全选/取消全选 */
    handleSelectAll(val) {
      this.emailList.forEach(item => {
        item.selected = val
      })
      this.updateSelectedMails()
    },
    
    /** 选择邮件 */
    handleMailSelect(mail) {
      this.updateSelectedMails()
    },
    
    /** 更新选中的邮件 */
    updateSelectedMails() {
      this.selectedMails = this.emailList.filter(item => item.selected)
      this.multiple = this.selectedMails.length === 0
    },
    
    /** 点击邮件 */
    handleMailClick(mail) {
      this.selectedMail = mail
      // 标记为已读
      if (mail.isRead === 0) {
        markAsRead([mail.id]).then(() => {
          mail.isRead = 1
        })
      }
    },
    
    /** 切换星标 */
    handleToggleStar(mail) {
      const action = mail.isStarred === 1 ? unmarkAsStarred : markAsStarred
      action([mail.id]).then(() => {
        mail.isStarred = mail.isStarred === 1 ? 0 : 1
        this.$message.success(mail.isStarred === 1 ? '已标记为星标' : '已取消星标')
      })
    },
    
    /** 标记为星标 */
    handleMarkAsStarred() {
      const ids = this.selectedMails.map(item => item.id)
      markAsStarred(ids).then(() => {
        this.selectedMails.forEach(item => {
          item.isStarred = 1
        })
        this.$message.success('标记星标成功')
        this.updateSelectedMails()
      })
    },
    
    /** 删除邮件 */
    handleDelete() {
      const ids = this.selectedMails.map(item => item.id)
      this.$confirm('确定要删除选中的邮件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        moveToDeleted(ids).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    
    /** 回复邮件 */
    handleReply() {
      // 跳转到回复页面
      this.$router.push({
        path: '/email/personal/compose',
        query: { replyTo: this.selectedMail.id }
      })
    },
    
    /** 转发邮件 */
    handleForward() {
      // 跳转到转发页面
      this.$router.push({
        path: '/email/personal/compose',
        query: { forward: this.selectedMail.id }
      })
    },
    
    /** 筛选邮件 */
    handleFilter() {
      this.queryParams.status = this.filterForm.status
      this.queryParams.recipient = this.filterForm.recipient
      if (this.filterForm.dateRange && this.filterForm.dateRange.length === 2) {
        this.queryParams.params = {
          beginTime: this.filterForm.dateRange[0],
          endTime: this.filterForm.dateRange[1]
        }
      }
      this.queryParams.pageNum = 1
      this.getList()
      this.showFilterDialog = false
    },
    
    /** 分页大小改变 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getList()
    },
    
    /** 当前页改变 */
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
    
    /** 格式化时间 */
    formatTime(time) {
      if (!time) return ''
      return this.$moment(time).format('MM-DD HH:mm')
    },
    
    /** 获取内容预览 */
    getContentPreview(content) {
      if (!content) return ''
      // 移除HTML标签
      const text = content.replace(/<[^>]*>/g, '')
      return text.length > 100 ? text.substring(0, 100) + '...' : text
    },
    
    /** 获取状态类型 */
    getStatusType(status) {
      const statusMap = {
        'SENT': 'info',
        'DELIVERED': 'success',
        'OPENED': 'warning',
        'REPLIED': 'success',
        'CLICKED': 'success',
        'FAILED': 'danger'
      }
      return statusMap[status] || 'info'
    },
    
    /** 获取状态文本 */
    getStatusText(status) {
      const statusMap = {
        'SENT': '已发送',
        'DELIVERED': '已送达',
        'OPENED': '已打开',
        'REPLIED': '已回复',
        'CLICKED': '已点击',
        'FAILED': '发送失败'
      }
      return statusMap[status] || status
    }
  }
}
</script>

<style scoped>
.mail-toolbar {
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #e6e6e6;
  background: #f5f5f5;
}

.mail-toolbar .el-checkbox {
  margin-right: 15px;
}

.mail-toolbar .el-button {
  margin-right: 10px;
}

.search-box {
  margin-left: auto;
  margin-right: 15px;
}

.search-box .el-input {
  width: 200px;
}

.mail-view {
  display: flex;
  height: calc(100vh - 200px);
}

.mail-list {
  flex: 1;
  border-right: 1px solid #e6e6e6;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #f9f9f9;
  border-bottom: 1px solid #e6e6e6;
  font-size: 14px;
  color: #666;
}

.mail-list-content {
  height: calc(100% - 100px);
  overflow-y: auto;
}

.mail-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.mail-item:hover {
  background-color: #f5f5f5;
}

.mail-item.selected {
  background-color: #e6f7ff;
}

.mail-item.unread {
  background-color: #f0f9ff;
  font-weight: bold;
}

.item-checkbox {
  margin-right: 10px;
}

.item-star {
  margin-right: 10px;
  cursor: pointer;
}

.item-star .el-icon-star-on {
  color: #ddd;
  font-size: 16px;
}

.item-star .el-icon-star-on.starred {
  color: #f39c12;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.mail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.recipient {
  font-weight: bold;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
}

.mail-subject {
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.mail-preview {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mail-status {
  display: flex;
  align-items: center;
  gap: 10px;
}

.opened-time, .replied-time {
  font-size: 11px;
  color: #999;
}

.mail-detail {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e6e6e6;
}

.detail-header h3 {
  margin: 0;
  color: #333;
}

.detail-actions .el-button {
  margin-left: 10px;
}

.detail-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 4px;
}

.detail-info div {
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-body {
  line-height: 1.6;
  color: #333;
}

.pagination-container {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #e6e6e6;
}
</style>

