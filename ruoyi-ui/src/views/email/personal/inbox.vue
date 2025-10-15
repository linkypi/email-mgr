<template>
  <div class="app-container">
    <!-- 邮件工具栏 -->
    <div class="mail-toolbar">
      <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
      
      <el-button size="small" icon="el-icon-refresh" @click="handleRefresh">刷新</el-button>
      <el-button size="small" icon="el-icon-star-on" @click="handleMarkAsStarred" :disabled="multiple">标记星标</el-button>
      <el-button size="small" icon="el-icon-delete" @click="handleDelete" :disabled="multiple" type="danger">删除</el-button>
      <el-button size="small" icon="el-icon-check" @click="handleMarkAsRead" :disabled="multiple">标记已读</el-button>
      
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
      <div class="mail-list custom-left-panel" style="width: 40% !important; min-width: 300px; max-width: 40%; flex: none !important;">
        <div class="list-header">
          <div>收件箱 ({{ total }})</div>
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
            
            <div 
              class="item-star"
              :class="{ 'starred': mail.isStarred }"
              @click.stop="handleToggleStar(mail)"
            >
              <i :class="mail.isStarred ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
            </div>
            
            <div 
              class="item-important"
              :class="{ 'high': mail.isImportant }"
              @click.stop="handleToggleImportant(mail)"
            >
              <i :class="mail.isImportant ? 'el-icon-warning' : 'el-icon-warning-outline'"></i>
            </div>
            
            <div class="mail-content">
              <!-- 第一行：邮件标题 -->
              <div class="mail-subject">{{ mail.subject }}</div>
              
              <!-- 第二行：发件人信息 -->
              <div class="mail-sender">
                <span class="sender-info">发件人: {{ mail.sender }}</span>
                <span v-if="mail.isRead === 0" class="mail-unread"></span>
              </div>
              
              <!-- 第三行：收件人信息 -->
              <div class="mail-recipient">
                <span class="recipient-info">收件人: 我</span>
              </div>
              
              <!-- 第四行：邮件状态 -->
              <div class="mail-status">
                <el-tag 
                  :type="getStatusType(mail.status)" 
                  size="mini"
                >
                  {{ getStatusText(mail.status) }}
                </el-tag>
                <span v-if="mail.repliedTime" class="replied-time">
                  已回复 {{ parseTime(mail.repliedTime, '{m}-{d} {h}:{i}') }}
                </span>
              </div>
              
              <!-- 第五行：内容预览 -->
              <div class="mail-excerpt">{{ mail.content || '无内容预览' }}</div>
            </div>
            
            <div class="mail-time">{{ parseTime(mail.receiveTime, '{m}-{d} {h}:{i}') }}</div>
          </div>
          
          <div v-if="emailList.length === 0 && !loading" class="empty-list">
            <i class="el-icon-message"></i>
            <p>暂无收件</p>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <pagination
            v-show="total>0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
          />
        </div>
      </div>

      <!-- 邮件预览 -->
      <div class="mail-preview custom-right-panel" style="width: 60% !important; min-width: 400px; max-width: 60%; flex: none !important;">
        <div v-if="selectedMail" class="mail-preview-header">
          <!-- 第一行：标题单独占用一行 -->
          <div class="preview-title-row">
            <h2 class="preview-title">{{ selectedMail.subject }}</h2>
          </div>
          
          <!-- 第二行：星标和操作按钮 -->
          <div class="preview-actions-row">
            <!-- 星标图标 -->
            <div class="preview-star">
              <i :class="selectedMail.isStarred ? 'el-icon-star-on' : 'el-icon-star-off'" 
                 @click="handleToggleStar(selectedMail)"></i>
            </div>
            
            <!-- 操作按钮 -->
            <div class="preview-actions">
              <el-button size="mini" icon="el-icon-chat-line-round" @click="handleReply">回复</el-button>
              <el-button size="mini" icon="el-icon-share" @click="handleForward">转发</el-button>
              <el-button size="mini" icon="el-icon-star-off" @click="handleStarMail(selectedMail)">星标</el-button>
              <el-button size="mini" icon="el-icon-delete" @click="handleDeleteMail(selectedMail)" type="danger">删除</el-button>
            </div>
          </div>
          
          <!-- 第三行：发件人信息单独占用一行 -->
          <div class="preview-sender-row">
            <span class="sender-label">发件人</span>
            <span class="sender-email">{{ selectedMail.sender }}</span>
          </div>
          
          <!-- 第四行：收件人信息单独占用一行 -->
          <div class="preview-recipient-row">
            <span class="recipient-label">收件人</span>
            <span class="recipient-name">我</span>
            <span class="recipient-email">&lt;{{ selectedMail.recipient }}&gt;</span>
          </div>
        </div>

        <div class="mail-preview-body">
          <!-- 邮件查看模式 -->
          <div v-if="selectedMail && !showCompose" v-html="selectedMail.htmlContent || selectedMail.content"></div>
          
          <!-- 回复/转发编辑模式 -->
          <div v-if="selectedMail && showCompose" class="compose-form">
            <!-- 邮件头部信息 -->
            <div class="compose-header">
              <div class="compose-title">
                <span class="compose-mode">{{ composeMode === 'reply' ? '回复' : '转发' }}</span>
                <span class="compose-sender">{{ selectedMail.sender }}</span>
              </div>
              <div class="compose-actions">
                <el-button size="mini" @click="handleCancelCompose">取消</el-button>
              </div>
            </div>
            
            <!-- 收件人和主题 -->
            <div class="compose-fields">
              <div class="field-row">
                <label>收件人</label>
                <el-input v-model="composeForm.toAddress" placeholder="请输入收件人邮箱" size="small"></el-input>
              </div>
              
              <div class="field-row">
                <label>抄送</label>
                <el-input v-model="composeForm.ccAddress" placeholder="请输入抄送邮箱（可选）" size="small"></el-input>
              </div>
              
              <div class="field-row">
                <label>主题</label>
                <el-input v-model="composeForm.subject" placeholder="请输入邮件主题" size="small"></el-input>
              </div>
            </div>
            
            <!-- 富文本编辑器 -->
            <div class="compose-content">
              <RichTextEditor
                v-model="composeForm.content"
                placeholder="输入正文"
                height="200px"
                @change="handleContentChange"
              />
            </div>
            
            <!-- 原邮件引用框 -->
            <div class="original-email-box">
              <div class="original-email-header">原始邮件</div>
              <div class="original-email-content">
                <div class="original-email-info">
                  <div><strong>发件人:</strong> {{ selectedMail.sender }} &lt;{{ selectedMail.sender }}&gt;</div>
                  <div><strong>发件时间:</strong> {{ parseTime(selectedMail.receiveTime, '{y}年{m}月{d}日 {h}:{i}') }}</div>
                  <div><strong>收件人:</strong> {{ selectedMail.recipient || '我' }} &lt;{{ selectedMail.recipient || '我' }}&gt;</div>
                  <div><strong>主题:</strong> {{ selectedMail.subject }}</div>
                </div>
                <div class="original-email-body" v-html="selectedMail.htmlContent || selectedMail.content"></div>
              </div>
            </div>
            
            <!-- 底部操作按钮 -->
            <div class="compose-footer">
              <div class="footer-left">
                <el-button size="mini" icon="el-icon-star-off">表情</el-button>
              </div>
              <div class="footer-right">
                <el-button @click="handleCancelCompose">取消</el-button>
                <el-button type="primary" @click="handleSend" :loading="sending">
                  {{ composeMode === 'reply' ? '发送回复' : '发送转发' }}
                </el-button>
              </div>
            </div>
          </div>
          
          <div v-if="!selectedMail" class="empty-preview">
            <i class="el-icon-message"></i>
            <p>从左侧列表中选择一封邮件进行查看</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选对话框 -->
    <el-dialog title="邮件筛选" :visible.sync="showFilterDialog" width="500px">
      <el-form :model="filterForm" label-width="80px">
        <el-form-item label="发件人">
          <el-input v-model="filterForm.sender" placeholder="请输入发件人" />
        </el-form-item>
        <el-form-item label="主题">
          <el-input v-model="filterForm.subject" placeholder="请输入主题关键词" />
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
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable>
            <el-option label="未读" value="unread" />
            <el-option label="已读" value="read" />
            <el-option label="星标" value="starred" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showFilterDialog = false">取消</el-button>
        <el-button type="primary" @click="handleFilter">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInbox, markAsStarred, unmarkAsStarred, markAsImportant, markAsRead, moveToDeleted } from "@/api/email/personal";
import request from '@/utils/request';
import RichTextEditor from '@/components/RichTextEditor';

export default {
  name: "EmailInbox",
  components: {
    RichTextEditor
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 总条数
      total: 0,
      // 邮件表格数据
      emailList: [],
      // 选中的邮件
      selectedMail: null,
      // 全选状态
      selectAll: false,
      // 搜索关键词
      searchKeyword: "",
      // 筛选对话框
      showFilterDialog: false,
      // 筛选表单
      filterForm: {
        sender: "",
        subject: "",
        dateRange: [],
        status: ""
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        folderType: 'inbox',
        sender: null,
        subject: null,
        status: null,
        startDate: null,
        endDate: null
      },
      // 内联编辑相关
      showCompose: false,
      composeMode: '', // 'reply' 或 'forward'
      sending: false,
      composeForm: {
        toAddress: '',
        ccAddress: '',
        subject: '',
        content: '',
        originalMessageId: ''
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询收件箱列表 */
    getList() {
      this.loading = true;
      listInbox(this.queryParams).then(response => {
        this.emailList = response.rows.map(mail => ({
          ...mail,
          selected: false
        }));
        this.total = response.total;
        this.loading = false;
        
        // 自动选中第一封邮件
        if (this.emailList.length > 0 && !this.selectedMail) {
          this.selectedMail = this.emailList[0];
        }
      }).catch(() => {
        // 如果API不存在，使用模拟数据
        this.emailList = [
          {
            id: 1,
            sender: 'sender@example.com',
            subject: '重要会议通知',
            content: '关于明天的重要会议安排...',
            createTime: '2024-01-15 10:30:00',
            status: 'RECEIVED',
            isStarred: false,
            isImportant: true,
            isRead: 0,
            selected: false
          },
          {
            id: 2,
            sender: 'hr@company.com',
            subject: '员工福利更新',
            content: '员工福利政策更新通知...',
            createTime: '2024-01-15 09:15:00',
            status: 'RECEIVED',
            isStarred: false,
            isImportant: false,
            isRead: 1,
            selected: false
          },
          {
            id: 3,
            sender: 'boss@company.com',
            subject: '项目进度报告',
            content: '本月项目进度报告...',
            createTime: '2024-01-14 16:20:00',
            status: 'RECEIVED',
            isStarred: true,
            isImportant: false,
            isRead: 1,
            selected: false
          },
          {
            id: 4,
            sender: 'client@customer.com',
            subject: '合作提案',
            content: '关于新项目的合作提案...',
            createTime: '2024-01-14 14:45:00',
            status: 'RECEIVED',
            isRead: 0,
            isStarred: false,
            isImportant: false,
            selected: false
          },
          {
            emailId: 5,
            fromAddress: 'system@company.com',
            subject: '系统维护通知',
            content: '系统将于今晚进行维护...',
            receiveTime: '2024-01-14 11:20:00',
            status: 'read',
            isStarred: false,
            isImportant: false,
            selected: false
          }
        ];
        this.total = this.emailList.length;
        this.loading = false;
        
        // 自动选中第一封邮件
        if (this.emailList.length > 0 && !this.selectedMail) {
          this.selectedMail = this.emailList[0];
        }
      });
    },
    
    /** 邮件点击事件 */
    handleMailClick(mail) {
      this.selectedMail = mail;
      // 标记为已读
      if (mail.isRead === 0) {
        markAsRead([mail.id]).then(() => {
          mail.isRead = 1;
        }).catch(() => {
          // 如果API不存在，直接更新本地状态
          mail.isRead = 1;
        });
      }
    },
    
    /** 切换星标状态 */
    handleToggleStar(mail) {
      const newStarred = !mail.isStarred;
      const action = newStarred ? markAsStarred : unmarkAsStarred;
      action([mail.id]).then(() => {
        mail.isStarred = newStarred ? 1 : 0;
        this.$message.success(newStarred ? '已标记为星标' : '已取消星标');
      }).catch(() => {
        // 如果API不存在，直接更新本地状态
        mail.isStarred = newStarred ? 1 : 0;
      });
    },
    
    /** 切换重要标记 */
    handleToggleImportant(mail) {
      const newImportant = !mail.isImportant;
      markAsImportant([mail.id]).then(() => {
        mail.isImportant = newImportant ? 1 : 0;
      }).catch(() => {
        // 如果API不存在，直接更新本地状态
        mail.isImportant = newImportant ? 1 : 0;
      });
    },
    
    /** 邮件选择事件 */
    handleMailSelect(mail) {
      this.updateSelection();
    },
    
    /** 全选事件 */
    handleSelectAll(val) {
      this.emailList.forEach(mail => {
        mail.selected = val;
      });
      this.updateSelection();
    },
    
    /** 更新选择状态 */
    updateSelection() {
      const selectedMails = this.emailList.filter(mail => mail.selected);
      this.ids = selectedMails.map(mail => mail.emailId);
      this.single = selectedMails.length !== 1;
      this.multiple = selectedMails.length === 0;
    },
    
    /** 刷新 */
    handleRefresh() {
      this.getList();
    },
    
    /** 标记星标 */
    handleMarkAsStarred() {
      if (this.ids.length === 0) return;
      
      this.$modal.confirm('是否确认标记选中的邮件为星标？').then(() => {
        this.emailList.forEach(mail => {
          if (mail.selected) {
            mail.isStarred = true;
            mail.status = 'starred';
          }
        });
        this.$modal.msgSuccess("标记成功");
      });
    },
    
    /** 标记已读 */
    handleMarkAsRead() {
      if (this.ids.length === 0) return;
      
      this.$modal.confirm('是否确认标记选中的邮件为已读？').then(() => {
        this.emailList.forEach(mail => {
          if (mail.selected) {
            mail.status = 'read';
          }
        });
        this.$modal.msgSuccess("标记成功");
      });
    },
    
    /** 删除邮件 */
    handleDelete() {
      if (this.ids.length === 0) return;
      
      this.$modal.confirm('是否确认删除选中的邮件？').then(() => {
        delEmail(this.ids).then(() => {
          this.getList();
          this.selectedMail = null;
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {
          this.getList();
          this.selectedMail = null;
          this.$modal.msgSuccess("删除成功");
        });
      });
    },
    
    /** 删除单个邮件 */
    handleDeleteMail(mail) {
      this.$modal.confirm('是否确认删除这封邮件？').then(() => {
        delEmail(mail.emailId).then(() => {
          this.getList();
          this.selectedMail = null;
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {
          this.getList();
          this.selectedMail = null;
          this.$modal.msgSuccess("删除成功");
        });
      });
    },
    
    /** 搜索 */
    handleSearch() {
      this.queryParams.subject = this.searchKeyword;
      this.queryParams.pageNum = 1;
      this.getList();
    },
    
    /** 筛选 */
    handleFilter() {
      this.queryParams.sender = this.filterForm.sender;
      this.queryParams.subject = this.filterForm.subject;
      this.queryParams.status = this.filterForm.status;
      this.queryParams.startDate = this.filterForm.dateRange[0];
      this.queryParams.endDate = this.filterForm.dateRange[1];
      this.queryParams.pageNum = 1;
      this.getList();
      this.showFilterDialog = false;
    },
    
    /** 回复邮件 */
    handleReply() {
      if (!this.selectedMail) return;
      
      this.composeMode = 'reply';
      this.showCompose = true;
      
      // 设置回复邮件信息
      this.composeForm.toAddress = this.selectedMail.sender;
      this.composeForm.subject = 'Re: ' + this.selectedMail.subject;
      this.composeForm.originalMessageId = this.selectedMail.messageId;
      
      // 富文本编辑器中只显示用户输入区域，不包含原邮件内容
      this.composeForm.content = '';
    },
    
    /** 转发邮件 */
    handleForward() {
      if (!this.selectedMail) return;
      
      this.composeMode = 'forward';
      this.showCompose = true;
      
      // 设置转发邮件信息
      this.composeForm.toAddress = '';
      this.composeForm.subject = 'Fwd: ' + this.selectedMail.subject;
      this.composeForm.originalMessageId = this.selectedMail.messageId;
      
      // 富文本编辑器中只显示用户输入区域，不包含原邮件内容
      this.composeForm.content = '';
    },
    
    /** 发送邮件 */
    async handleSend() {
      if (!this.composeForm.toAddress || !this.composeForm.subject || !this.composeForm.content) {
        this.$message.warning('请填写完整信息');
        return;
      }
      
      this.sending = true;
      try {
        // 判断是回复、转发还是普通发送
        const isReply = this.composeMode === 'reply';
        const isForward = this.composeMode === 'forward';
        
        let apiUrl = '/email/personal/send';
        if (isReply) {
          apiUrl = '/email/personal/reply';
        } else if (isForward) {
          apiUrl = '/email/personal/forward';
        }
        
        // 处理收件人和抄送地址
        let recipient = this.composeForm.toAddress;
        if (this.composeForm.ccAddress && this.composeForm.ccAddress.trim()) {
          recipient += ',' + this.composeForm.ccAddress;
        }
        
        const requestData = {
          recipient: recipient,
          subject: this.composeForm.subject,
          content: this.composeForm.content
        };
        
        // 如果是回复或转发，添加原邮件信息
        if (isReply || isForward) {
          requestData.remark = isReply ? 
            `reply_to:${this.composeForm.originalMessageId}` : 
            `forward_from:${this.composeForm.originalMessageId}`;
        }
        
        // 调用发送邮件API
        const response = await request({
          url: apiUrl,
          method: 'post',
          data: requestData
        });
        
        if (response.code === 200) {
          let successMsg = '邮件发送成功';
          if (isReply) {
            successMsg = '回复发送成功';
          } else if (isForward) {
            successMsg = '转发发送成功';
          }
          this.$message.success(successMsg);
          
          // 重置表单并退出编辑模式
          this.handleCancelCompose();
          
          // 刷新邮件列表
          this.getList();
        } else {
          this.$message.error(response.msg || '邮件发送失败');
        }
      } catch (error) {
        console.error('发送邮件失败:', error);
        this.$message.error('邮件发送失败，请稍后重试');
      } finally {
        this.sending = false;
      }
    },
    
    /** 取消编辑 */
    handleCancelCompose() {
      this.showCompose = false;
      this.composeMode = '';
      this.composeForm = {
        toAddress: '',
        ccAddress: '',
        subject: '',
        content: '',
        originalMessageId: ''
      };
    },
    
    /** 内容变化处理 */
    handleContentChange(content) {
      this.composeForm.content = content;
    },
    
    /** 获取发件人首字母 */
    getSenderInitials(sender) {
      if (!sender) return '我';
      // 如果是邮箱地址，取@前的部分
      if (sender.includes('@')) {
        const name = sender.split('@')[0];
        return name.substring(0, 2).toUpperCase();
      }
      // 如果是中文名，取前两个字符
      if (/[\u4e00-\u9fa5]/.test(sender)) {
        return sender.substring(0, 2);
      }
      // 如果是英文名，取前两个字符
      return sender.substring(0, 2).toUpperCase();
    },
    
    /** 获取状态类型 */
    getStatusType(status) {
      const statusMap = {
        'SEND_SUCCESS': 'success',
        'DELIVERED': 'info',
        'OPENED': 'warning',
        'CLICKED': 'primary',
        'REPLIED': 'success',
        'FAILED': 'danger'
      };
      return statusMap[status] || 'info';
    },
    
    /** 获取状态文本 */
    getStatusText(status) {
      const statusMap = {
        'SEND_SUCCESS': '发送成功',
        'DELIVERED': '已送达',
        'OPENED': '已打开',
        'CLICKED': '已点击',
        'REPLIED': '已回复',
        'FAILED': '发送失败'
      };
      return statusMap[status] || status;
    }
  }
};
</script>

<style scoped>
.mail-toolbar {
  padding: 12px 20px;
  background: white;
  border-bottom: 1px solid #e0e6ed;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.07);
}

.search-box {
  flex-grow: 1;
  max-width: 350px;
  margin: 0 15px;
}

.mail-view {
  display: flex;
  gap: 20px;
  height: calc(100vh - 200px);
}

.mail-list {
  flex: 0 0 40%;
  background: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.07);
  display: flex;
  flex-direction: column;
  border: 1px solid #e0e6ed;
}

.mail-preview {
  flex: 0 0 60%;
  background: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.07);
  display: flex;
  flex-direction: column;
  border: 1px solid #e0e6ed;
}

.list-header {
  padding: 12px 15px;
  border-bottom: 1px solid #e0e6ed;
  display: flex;
  justify-content: space-between;
  background: #f5f7fa;
  font-size: 13px;
  color: #606266;
}

.mail-list-content {
  flex: 1;
  overflow-y: auto;
}

.mail-item {
  padding: 15px;
  border-bottom: 1px solid #e0e6ed;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
}

.mail-item:hover {
  background: #f5f9ff;
}

.mail-item.selected {
  background: #e8f4ff;
}

.mail-item.unread {
  background: #f7fbff;
  font-weight: bold;
}

.item-checkbox {
  margin-right: 12px;
}

.item-star {
  color: #ddd;
  margin-right: 12px;
  font-size: 16px;
  transition: all 0.2s;
  cursor: pointer;
}

.item-star.starred {
  color: #FFB800;
}

.item-star:hover {
  color: #FFB800;
}

.item-important {
  color: #ddd;
  margin-right: 12px;
  transition: all 0.2s;
  cursor: pointer;
}

.item-important.high {
  color: #F56C6C;
}

.item-important:hover {
  color: #F56C6C;
}

.mail-content {
  flex: 1;
  min-width: 0;
  margin-right: 15px;
}

.mail-subject {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mail-sender {
  font-size: 14px;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
}

.mail-recipient {
  font-size: 14px;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
}

.sender-info {
  color: #409EFF;
  font-weight: 500;
}

.recipient-info {
  color: #606266;
}

.mail-excerpt {
  font-size: 13px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mail-time {
  font-size: 12px;
  color: #606266;
  white-space: nowrap;
  margin-left: 15px;
}

.mail-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.opened-time,
.replied-time {
  font-size: 12px;
  color: #606266;
  white-space: nowrap;
}

.mail-unread {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #409EFF;
  margin-left: 8px;
  vertical-align: middle;
}

.mail-preview-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e0e6ed;
}

/* 标题行 */
.preview-title-row {
  margin-bottom: 15px;
}

.preview-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  line-height: 1.2;
}

/* 操作行 */
.preview-actions-row {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.preview-star {
  flex-shrink: 0;
}

.preview-star i {
  font-size: 20px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
}

.preview-star i:hover,
.preview-star i.el-icon-star-on {
  color: #FFB800;
}

.preview-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-shrink: 0;
}

/* 发件人行 */
.preview-sender-row {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
}

.sender-label {
  margin-right: 8px;
}

.sender-email {
  color: #303133;
}

/* 收件人行 */
.preview-recipient-row {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
}

.recipient-label {
  margin-right: 8px;
}

.recipient-name {
  margin-right: 8px;
  font-weight: 500;
}

.recipient-email {
  color: #909399;
}

/* 分隔线 */
.preview-divider {
  height: 1px;
  background: #e0e6ed;
  margin: 15px 0;
}

/* 发件人信息部分 */
.preview-sender-section {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.sender-avatar {
  margin-right: 12px;
}

.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #8B5CF6;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.sender-info {
  flex: 1;
}

.sender-name {
  font-size: 16px;
  color: #303133;
  margin-bottom: 2px;
}

.sender-email {
  font-size: 14px;
  color: #909399;
}

/* 收件人信息部分 */
.preview-recipient-section {
  margin-bottom: 15px;
  font-size: 14px;
  color: #606266;
}

.recipient-label {
  margin-right: 8px;
}

.recipient-name {
  margin-right: 8px;
  font-weight: 500;
}

.recipient-email {
  color: #909399;
}

.preview-actions {
  display: flex;
  gap: 8px;
}

.compose-form {
  background: #fff;
  border: 1px solid #e0e6ed;
  border-radius: 8px;
  margin: 10px 0;
  overflow: hidden;
}

/* 邮件头部 */
.compose-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e6ed;
}

.compose-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.compose-mode {
  font-weight: bold;
  color: #409EFF;
}

.compose-sender {
  color: #606266;
  font-size: 14px;
}

/* 收件人和主题字段 */
.compose-fields {
  padding: 15px 20px;
  border-bottom: 1px solid #e0e6ed;
}

.field-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.field-row:last-child {
  margin-bottom: 0;
}

.field-row label {
  width: 60px;
  font-size: 14px;
  color: #606266;
  margin-right: 10px;
}

.field-row .el-input {
  flex: 1;
}

/* 内容编辑区域 */
.compose-content {
  padding: 0;
}

/* 原邮件引用框 */
.original-email-box {
  margin: 15px 20px;
  border: 1px solid #e0e6ed;
  border-radius: 6px;
  background: #f8f9fa;
}

.original-email-header {
  padding: 10px 15px;
  background: #e9ecef;
  border-bottom: 1px solid #e0e6ed;
  font-weight: bold;
  color: #495057;
  font-size: 14px;
}

.original-email-content {
  padding: 15px;
}

.original-email-info {
  margin-bottom: 10px;
  font-size: 13px;
  color: #6c757d;
  line-height: 1.5;
}

.original-email-info div {
  margin-bottom: 3px;
}

.original-email-body {
  font-size: 14px;
  line-height: 1.6;
  color: #495057;
  border-top: 1px solid #e0e6ed;
  padding-top: 10px;
}

/* 底部操作按钮 */
.compose-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-top: 1px solid #e0e6ed;
}

.footer-left .el-button {
  margin-right: 0;
}

.footer-right {
  display: flex;
  gap: 10px;
}

.mail-preview-body {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
  line-height: 1.7;
}

.empty-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
  text-align: center;
}

.empty-preview i {
  font-size: 60px;
  margin-bottom: 20px;
  color: #e0e0e0;
}

.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  text-align: center;
}

.empty-list i {
  font-size: 48px;
  margin-bottom: 15px;
  color: #e0e0e0;
}

.pagination-container {
  padding: 12px;
  border-top: 1px solid #e0e6ed;
}

@media (max-width: 1200px) {
  .mail-list {
    flex: 0 0 40%;
  }
  .mail-preview {
    flex: 0 0 60%;
  }
}

@media (max-width: 980px) {
  .mail-view {
    flex-direction: column;
  }

  .mail-list, .mail-preview {
    width: 100%;
    height: 50vh;
  }
}

/* 强制布局样式 */
.custom-left-panel {
  width: 40% !important;
  min-width: 300px !important;
  max-width: 40% !important;
  flex: none !important;
  flex-grow: 0 !important;
  flex-shrink: 0 !important;
  flex-basis: 40% !important;
}

.custom-right-panel {
  width: 60% !important;
  min-width: 400px !important;
  max-width: 60% !important;
  flex: none !important;
  flex-grow: 0 !important;
  flex-shrink: 0 !important;
  flex-basis: 60% !important;
}
</style>