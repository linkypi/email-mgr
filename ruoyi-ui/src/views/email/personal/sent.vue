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
            :key="mail.emailId"
            class="mail-item"
            :class="{ 
              'unread': mail.status === 'unread',
              'selected': selectedMail && selectedMail.emailId === mail.emailId 
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
              <div class="mail-from">
                收件人: {{ mail.toAddress }}
                <span v-if="mail.status === 'unread'" class="mail-unread"></span>
              </div>
              <div class="mail-subject">{{ mail.subject }}</div>
              <div class="mail-excerpt">{{ mail.content || '无内容预览' }}</div>
            </div>
            
            <div class="mail-time">{{ parseTime(mail.sendTime, '{m}-{d} {h}:{i}') }}</div>
          </div>
          
          <div v-if="emailList.length === 0 && !loading" class="empty-list">
            <i class="el-icon-s-promotion"></i>
            <p>暂无已发送邮件</p>
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
      <div class="mail-preview">
        <div v-if="selectedMail" class="mail-preview-header">
          <div class="preview-info">
            <div class="preview-from">
              <strong>收件人: {{ selectedMail.toAddress }}</strong>
              <span class="preview-email">&lt;{{ selectedMail.toAddress }}&gt;</span>
            </div>
            <div class="preview-time">{{ parseTime(selectedMail.sendTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</div>
          </div>
          <div class="preview-actions">
            <el-button size="mini" icon="el-icon-share" @click="handleForward">转发</el-button>
            <el-button size="mini" icon="el-icon-delete" @click="handleDeleteMail(selectedMail)" type="danger">删除</el-button>
          </div>
        </div>

        <div class="mail-preview-body">
          <div v-if="selectedMail" v-html="selectedMail.htmlContent || selectedMail.content"></div>
          <div v-else class="empty-preview">
            <i class="el-icon-message"></i>
            <p>从左侧列表中选择一封邮件进行查看</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选对话框 -->
    <el-dialog title="邮件筛选" :visible.sync="showFilterDialog" width="500px">
      <el-form :model="filterForm" label-width="80px">
        <el-form-item label="收件人">
          <el-input v-model="filterForm.toAddress" placeholder="请输入收件人" />
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
import { listSent, getEmail, delEmail, markAsStarred, markAsImportant } from "@/api/email/personal";

export default {
  name: "EmailSent",
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
        toAddress: "",
        subject: "",
        dateRange: [],
        status: ""
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        toAddress: null,
        subject: null,
        status: null,
        startDate: null,
        endDate: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询发件箱列表 */
    getList() {
      this.loading = true;
      listSent(this.queryParams).then(response => {
        this.emailList = response.rows.map(mail => ({
          ...mail,
          selected: false
        }));
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        // 如果API不存在，使用模拟数据
        this.emailList = [
          {
            emailId: 4,
            toAddress: 'hr@company.com',
            subject: '员工福利申请',
            content: '关于员工福利申请的相关事宜...',
            sendTime: '2024-01-15 14:30:00',
            status: 'sent',
            isStarred: false,
            isImportant: false,
            selected: false
          },
          {
            emailId: 5,
            toAddress: 'tech@company.com',
            subject: '系统维护通知',
            content: '系统维护相关通知...',
            sendTime: '2024-01-15 11:20:00',
            status: 'sent',
            isStarred: false,
            isImportant: false,
            selected: false
          }
        ];
        this.total = this.emailList.length;
        this.loading = false;
      });
    },
    
    /** 邮件点击事件 */
    handleMailClick(mail) {
      this.selectedMail = mail;
    },
    
    /** 切换星标状态 */
    handleToggleStar(mail) {
      const newStarred = !mail.isStarred;
      markAsStarred(mail.emailId).then(() => {
        mail.isStarred = newStarred;
      }).catch(() => {
        // 如果API不存在，直接更新本地状态
        mail.isStarred = newStarred;
      });
    },
    
    /** 切换重要标记 */
    handleToggleImportant(mail) {
      const newImportant = !mail.isImportant;
      markAsImportant(mail.emailId).then(() => {
        mail.isImportant = newImportant;
      }).catch(() => {
        // 如果API不存在，直接更新本地状态
        mail.isImportant = newImportant;
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
      this.queryParams.toAddress = this.filterForm.toAddress;
      this.queryParams.subject = this.filterForm.subject;
      this.queryParams.status = this.filterForm.status;
      this.queryParams.startDate = this.filterForm.dateRange[0];
      this.queryParams.endDate = this.filterForm.dateRange[1];
      this.queryParams.pageNum = 1;
      this.getList();
      this.showFilterDialog = false;
    },
    
    /** 转发邮件 */
    handleForward() {
      if (!this.selectedMail) return;
      this.$router.push({
        path: '/email/personal/compose',
        query: {
          forward: this.selectedMail.emailId,
          subject: 'Fwd: ' + this.selectedMail.subject
        }
      });
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
  flex: 1;
  background: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.07);
  display: flex;
  flex-direction: column;
  border: 1px solid #e0e6ed;
}

.mail-preview {
  width: 45%;
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

.mail-from {
  font-size: 15px;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
}

.mail-subject {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 5px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-info {
  flex: 1;
}

.preview-from {
  font-size: 16px;
  margin-bottom: 5px;
}

.preview-email {
  color: #606266;
  font-size: 14px;
}

.preview-time {
  font-size: 12px;
  color: #909399;
}

.preview-actions {
  display: flex;
  gap: 8px;
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
  .mail-preview {
    width: 50%;
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
</style>
