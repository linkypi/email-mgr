<template>
  <div class="app-container">
    <!-- 测试标记：确认页面已更新 -->
    <!-- <el-alert
      title="✅ 页面已更新 - 修复subject和content字段问题，完善模板发送逻辑"
      type="success"
      :closable="false"
      show-icon
      style="margin-bottom: 20px;">
    </el-alert> -->

    <el-card>
      <div slot="header">
        <span>批量发送</span>
      </div>
      
      <el-form :model="sendForm" :rules="rules" ref="sendForm" label-width="120px">
        <!-- 基本信息 -->
        <el-card class="box-card" style="margin-bottom: 20px;">
          <div slot="header" class="clearfix">
            <span>基本信息</span>
          </div>
          <el-form-item label="任务名称" prop="taskName">
            <el-input v-model="sendForm.taskName" placeholder="请输入任务名称"></el-input>
          </el-form-item>
          
          <el-form-item label="发件人" prop="senderId">
            <el-select 
              v-model="sendForm.senderId" 
              filterable
              remote
              reserve-keyword
              placeholder="选择发件人（支持搜索）" 
              style="width: 100%"
              :remote-method="searchSenders"
              :loading="senderLoading">
              <el-option
                v-for="item in senderOptions"
                :key="item.senderId"
                :label="item.senderName + ' (' + item.company + ')'"
                :value="item.senderId">
              </el-option>
            </el-select>
            <div class="form-tip">
              <i class="el-icon-info"></i>
              选择一个发件人，系统将使用其关联的邮箱账号进行发送
            </div>
          </el-form-item>
          
          <el-form-item label="收件人类型" prop="recipientType">
            <el-radio-group v-model="sendForm.recipientType" @change="handleRecipientTypeChange">
              <el-radio label="all">全部收件人</el-radio>
              <el-radio label="group">指定群组</el-radio>
              <el-radio label="tag">指定标签</el-radio>
              <el-radio label="manual">手动选择</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 群组选择 -->
          <el-form-item label="选择群组" v-if="sendForm.recipientType === 'group'" prop="groupIds">
            <el-select v-model="sendForm.groupIds" multiple placeholder="请选择群组" style="width: 100%">
              <el-option
                v-for="item in groupOptions"
                :key="item.groupId"
                :label="item.groupName"
                :value="item.groupId">
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 标签选择 -->
          <el-form-item label="选择标签" v-if="sendForm.recipientType === 'tag'" prop="tagIds">
            <el-select v-model="sendForm.tagIds" multiple placeholder="请选择标签" style="width: 100%">
              <el-option
                v-for="item in tagOptions"
                :key="item.tagId"
                :label="item.tagName"
                :value="item.tagId">
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 手动选择收件人 -->
          <el-form-item label="选择收件人" v-if="sendForm.recipientType === 'manual'" prop="contactIds">
            <el-select v-model="sendForm.contactIds" multiple placeholder="请选择收件人" style="width: 100%">
              <el-option
                v-for="item in contactOptions"
                :key="item.contactId"
                :label="item.name + ' (' + item.email + ')'"
                :value="item.contactId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-card>

        <!-- 邮件模板选择 -->
        <el-card class="box-card" style="margin-bottom: 20px;">
          <div slot="header" class="clearfix">
            <span>邮件模板选择</span>
          </div>
          <el-form-item label="发送方式">
            <el-radio-group v-model="sendForm.sendType" @change="handleSendTypeChange">
              <el-radio label="template">使用模板</el-radio>
              <el-radio label="direct">直接发送</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="选择模板" v-if="sendForm.sendType === 'template'" prop="templateId">
            <el-select v-model="sendForm.templateId" placeholder="请选择邮件模板" style="width: 100%" @change="handleTemplateChange">
              <el-option
                v-for="item in templateOptions"
                :key="item.templateId"
                :label="item.templateName"
                :value="item.templateId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-card>

        <!-- 模板预览 -->
        <el-card class="box-card" style="margin-bottom: 20px;" v-if="selectedTemplate && sendForm.sendType === 'template'">
          <div slot="header" class="clearfix">
            <span>模板预览</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="previewTemplate">预览效果</el-button>
          </div>
          <div>
            <p><strong>邮件主题：</strong>{{ selectedTemplate.subject }}</p>
            <p><strong>邮件内容：</strong></p>
            <div v-html="selectedTemplate.content" style="border: 1px solid #dcdfe6; padding: 15px; border-radius: 4px; background-color: #fafafa; max-height: 200px; overflow-y: auto;"></div>
          </div>
        </el-card>


        <!-- 直接发送内容 -->
        <el-card class="box-card" style="margin-bottom: 20px;" v-if="sendForm.sendType === 'direct'">
          <div slot="header" class="clearfix">
            <span>邮件内容</span>
          </div>
          <el-form-item label="邮件主题" prop="subject">
            <el-input v-model="sendForm.subject" placeholder="请输入邮件主题"></el-input>
          </el-form-item>
          
          <el-form-item label="邮件内容" prop="content">
            <el-input type="textarea" v-model="sendForm.content" :rows="6" placeholder="请输入邮件内容"></el-input>
          </el-form-item>
        </el-card>

        <!-- 发送设置 -->
        <el-card class="box-card" style="margin-bottom: 20px;">
          <div slot="header" class="clearfix">
            <span>发送设置</span>
          </div>
          <el-form-item label="发送方式" prop="sendMode">
            <div class="send-mode-selector">
              <el-tag 
                :type="sendForm.sendMode === 'immediate' ? 'success' : ''"
                :effect="sendForm.sendMode === 'immediate' ? 'dark' : 'plain'"
                size="medium"
                style="margin-right: 12px; cursor: pointer;"
                @click="sendForm.sendMode = 'immediate'; handleSendModeChange('immediate')">
                立即发送
              </el-tag>
              <el-tag 
                :type="sendForm.sendMode === 'scheduled' ? 'warning' : ''"
                :effect="sendForm.sendMode === 'scheduled' ? 'dark' : 'plain'"
                size="medium"
                style="cursor: pointer;"
                @click="sendForm.sendMode = 'scheduled'; handleSendModeChange('scheduled')">
                定时发送
              </el-tag>
            </div>
          </el-form-item>
          
          <el-row :gutter="20" v-if="sendForm.sendMode === 'scheduled'">
            <el-col :span="6">
              <el-form-item label="发送间隔(秒)" prop="sendInterval">
                <el-input-number v-model="sendForm.sendInterval" :min="1" :max="3600" style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="发送时间" prop="sendTime">
                <el-date-picker
                  v-model="sendForm.sendTime"
                  type="datetime"
                  placeholder="选择发送时间"
                  :picker-options="{
                    disabledDate(time) {
                      return time.getTime() < Date.now() - 8.64e7; // 禁用今天之前的日期
                    }
                  }"
                  style="width: 100%">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
        
        <el-form-item>
          <el-button type="primary" @click="handleSend" :loading="sending" :disabled="sending" v-hasPermi="['email:batch:send:create']">
            {{ sending ? '发送中...' : '开始发送' }}
          </el-button>
          <el-button @click="handleReset" :disabled="sending">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 模板预览对话框 -->
    <el-dialog title="邮件发送预览" :visible.sync="previewOpen" width="900px" append-to-body>
      <div v-if="previewData" class="email-preview">
        <!-- 发件人信息和发送设置 -->
        <div class="preview-section">
          <div class="sender-send-mode-row">
            <!-- 发件人信息 -->
            <div class="info-item">
              <h4><i class="el-icon-user"></i> 发件人</h4>
              <div class="preview-content">
                <span class="sender-info">{{ previewData.sender ? previewData.sender.senderName + ' (' + previewData.sender.company + ')' : '未选择' }}</span>
              </div>
            </div>
            
            <!-- 发送方式信息 -->
            <div class="info-item">
              <h4><i class="el-icon-setting"></i> 发送方式</h4>
              <div class="preview-content">
                <div class="send-mode-info">
                  <el-tag 
                    :type="sendForm.sendMode === 'immediate' ? 'success' : 'warning'"
                    size="small"
                    style="margin-right: 8px;">
                    {{ sendForm.sendMode === 'immediate' ? '立即发送' : '定时发送' }}
                  </el-tag>
                  <span v-if="sendForm.sendMode === 'scheduled'" class="send-details">
                    <span class="detail-item">间隔：{{ sendForm.sendInterval }}秒</span>
                    <span v-if="sendForm.sendTime" class="detail-item">时间：{{ formatDateTime(sendForm.sendTime) }}</span>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 收件人信息 -->
        <div class="preview-section">
          <h4><i class="el-icon-message"></i> 收件人信息</h4>
          <div class="preview-content">
            <!-- 全部收件人 -->
            <div v-if="previewData.recipient.type === 'all'">
              {{ previewData.recipient.display }}
            </div>
            
            <!-- 指定群组 -->
            <div v-else-if="previewData.recipient.type === 'group'">
              <span>指定群组：</span>
              <el-tag 
                v-for="group in previewData.recipient.items" 
                :key="group.id"
                size="small" 
                style="margin-right: 8px; margin-bottom: 4px;">
                {{ group.name }}({{ group.count }})
              </el-tag>
            </div>
            
            <!-- 指定标签 -->
            <div v-else-if="previewData.recipient.type === 'tag'">
              <span>指定标签：</span>
              <el-tag 
                v-for="tag in previewData.recipient.items" 
                :key="tag.id"
                size="small" 
                style="margin-right: 8px; margin-bottom: 4px;">
                {{ tag.name }}({{ tag.count }})
              </el-tag>
            </div>
            
            <!-- 手动选择 -->
            <div v-else-if="previewData.recipient.type === 'manual'">
              {{ previewData.recipient.display }}
            </div>
            
            <!-- 默认情况 -->
            <div v-else>
              {{ previewData.recipient.display }}
            </div>
          </div>
        </div>

        <!-- 邮件内容 -->
        <div class="preview-section">
          <h4><i class="el-icon-document"></i> 邮件内容</h4>
          <div class="preview-content">
            <p><strong>邮件主题：</strong>{{ previewData.subject }}</p>
            <div class="email-body">
              <strong>邮件正文：</strong>
              <div v-html="previewData.content" class="email-content"></div>
            </div>
          </div>
        </div>


      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="previewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAllTemplates, getTemplate } from "@/api/email/template";
import { getAllAccounts } from "@/api/email/account";
import { getAllContacts } from "@/api/email/contact";
import { getAllGroups } from "@/api/email/group";
import { getAllTags } from "@/api/email/tag";
import { createSendTask, getTask } from "@/api/email/task";
import { listSender, getSenderOptions } from "@/api/email/sender";

export default {
  name: 'EmailSend',
  data() {
    return {
      sending: false,
      sendForm: {
        taskName: '',
        senderId: null,
        recipientType: 'all',
        groupIds: [],
        tagIds: [],
        contactIds: [],
        sendType: 'template',
        templateId: null,
        subject: '',
        content: '',
        sendMode: 'immediate',
        sendInterval: 10,
        sendTime: null
      },
      // 选项数据
      accountOptions: [],
      senderOptions: [],
      contactOptions: [],
      groupOptions: [],
      tagOptions: [],
      templateOptions: [],
      // 发件人搜索相关
      senderLoading: false,
      // 模板相关
      selectedTemplate: null,
      previewOpen: false,
      previewData: null,
      // 表单验证规则
      rules: {
        taskName: [
          { required: true, message: "任务名称不能为空", trigger: "blur" }
        ],
        senderId: [
          { required: true, message: "请选择发件人", trigger: "change" }
        ],
        recipientType: [
          { required: true, message: "请选择收件人类型", trigger: "change" }
        ],
        templateId: [
          { 
            validator: (rule, value, callback) => {
              if (this.sendForm.sendType === 'template' && !value) {
                callback(new Error('请选择邮件模板'));
              } else {
                callback();
              }
            }, 
            trigger: "change" 
          }
        ],
        groupIds: [
          { 
            validator: (rule, value, callback) => {
              if (this.sendForm.recipientType === 'group' && (!value || value.length === 0)) {
                callback(new Error('请选择群组'));
              } else {
                callback();
              }
            }, 
            trigger: "change" 
          }
        ],
        tagIds: [
          { 
            validator: (rule, value, callback) => {
              if (this.sendForm.recipientType === 'tag' && (!value || value.length === 0)) {
                callback(new Error('请选择标签'));
              } else {
                callback();
              }
            }, 
            trigger: "change" 
          }
        ],
        contactIds: [
          { 
            validator: (rule, value, callback) => {
              if (this.sendForm.recipientType === 'manual' && (!value || value.length === 0)) {
                callback(new Error('请选择收件人'));
              } else {
                callback();
              }
            }, 
            trigger: "change" 
          }
        ],
        subject: [
          { 
            validator: (rule, value, callback) => {
              if (this.sendForm.sendType === 'direct' && (!value || value.trim() === '')) {
                callback(new Error('邮件主题不能为空'));
              } else {
                callback();
              }
            }, 
            trigger: "blur" 
          }
        ],
        content: [
          { 
            validator: (rule, value, callback) => {
              if (this.sendForm.sendType === 'direct' && (!value || value.trim() === '')) {
                callback(new Error('邮件内容不能为空'));
              } else {
                callback();
              }
            }, 
            trigger: "blur" 
          }
        ],
        sendTime: [
          { 
            validator: (rule, value, callback) => {
              if (this.sendForm.sendMode === 'scheduled' && !value) {
                callback(new Error('请选择发送时间'));
              } else if (value && new Date(value) <= new Date()) {
                callback(new Error('发送时间必须是将来的时间'));
              } else {
                callback();
              }
            }, 
            trigger: "change" 
          }
        ]
      }
    }
  },
  created() {
    this.getAccountOptions();
    this.getSenderOptions();
    this.getContactOptions();
    this.getGroupOptions();
    this.getTagOptions();
    this.getTemplateOptions();
    
    // 检查是否是复制任务
    this.checkCopyTask();
  },
  methods: {
    /** 检查并处理复制任务 */
    checkCopyTask() {
      const copyTaskId = this.$route.query.copyTaskId;
      if (copyTaskId) {
        this.loadTaskData(copyTaskId);
      }
    },
    /** 加载任务数据 */
    loadTaskData(taskId) {
      getTask(taskId).then(response => {
        const taskData = response.data;
        if (taskData) {
          // 填充表单数据
          this.sendForm.taskName = taskData.taskName + '_复制';
          this.sendForm.senderId = taskData.senderId;
          this.sendForm.recipientType = taskData.recipientType;
          this.sendForm.groupIds = taskData.groupIds ? taskData.groupIds.split(',').map(id => parseInt(id)) : [];
          this.sendForm.tagIds = taskData.tagIds ? taskData.tagIds.split(',').map(id => parseInt(id)) : [];
          this.sendForm.contactIds = taskData.contactIds ? taskData.contactIds.split(',').map(id => parseInt(id)) : [];
          this.sendForm.sendType = taskData.templateId ? 'template' : 'direct';
          this.sendForm.templateId = taskData.templateId;
          this.sendForm.subject = taskData.subject;
          this.sendForm.content = taskData.content;
          this.sendForm.sendMode = taskData.sendTime ? 'scheduled' : 'immediate';
          this.sendForm.sendInterval = taskData.sendInterval || 10;
          this.sendForm.sendTime = taskData.sendTime;
          
          // 如果有模板，加载模板信息
          if (taskData.templateId) {
            this.handleTemplateChange(taskData.templateId);
          }
          
          this.$message.success('任务信息已加载，请检查并修改后发送');
        }
      }).catch(error => {
        this.$message.error('加载任务信息失败：' + error.message);
      });
    },
    /** 获取发件人选项 */
    getAccountOptions() {
      getAllAccounts().then(response => {
        this.accountOptions = response.data;
      });
    },
    /** 获取发件人选项 */
    getSenderOptions() {
      getSenderOptions().then(response => {
        this.senderOptions = response.data || response.rows || [];
      }).catch(error => {
        console.error('获取发件人选项失败:', error);
        this.senderOptions = [];
      });
    },
    /** 搜索发件人 */
    searchSenders(query) {
      if (query !== '') {
        this.senderLoading = true;
        listSender({
          senderName: query,
          pageNum: 1,
          pageSize: 50
        }).then(response => {
          this.senderOptions = response.rows || [];
          this.senderLoading = false;
        }).catch(error => {
          console.error('搜索发件人失败:', error);
          this.senderOptions = [];
          this.senderLoading = false;
        });
      } else {
        // 如果查询为空，重新获取所有发件人选项
        this.getSenderOptions();
      }
    },
    /** 获取收件人选项 */
    getContactOptions() {
      getAllContacts().then(response => {
        this.contactOptions = response.data;
      });
    },
    /** 获取群组选项 */
    getGroupOptions() {
      getAllGroups().then(response => {
        this.groupOptions = response.data;
      });
    },
    /** 获取标签选项 */
    getTagOptions() {
      getAllTags().then(response => {
        this.tagOptions = response.data;
      });
    },
    /** 获取模板选项 */
    getTemplateOptions() {
      getAllTemplates().then(response => {
        this.templateOptions = response.data;
      });
    },
    /** 收件人类型变化 */
    handleRecipientTypeChange(type) {
      this.sendForm.groupIds = [];
      this.sendForm.tagIds = [];
      this.sendForm.contactIds = [];
    },
    /** 发送方式变化 */
    handleSendTypeChange(type) {
      if (type === 'direct') {
        this.sendForm.templateId = null;
        this.selectedTemplate = null;
      }
    },
    /** 发送模式变化 */
    handleSendModeChange(mode) {
      if (mode === 'immediate') {
        this.sendForm.sendTime = null;
      }
    },
    /** 格式化日期时间 */
    formatDateTime(dateTime) {
      if (!dateTime) return '';
      const date = new Date(dateTime);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    /** 模板选择变化 */
    handleTemplateChange(templateId) {
      if (templateId) {
        getTemplate(templateId).then(response => {
          this.selectedTemplate = response.data;
        });
      } else {
        this.selectedTemplate = null;
      }
    },

    /** 预览模板 */
    previewTemplate() {
      if (!this.selectedTemplate) {
        this.$message.warning('请先选择模板');
        return;
      }
      
      // 获取发件人信息
      const selectedSender = this.senderOptions.find(sender => sender.senderId === this.sendForm.senderId);
      
      // 获取收件人信息
      let recipientInfo = this.getRecipientInfo();
      
      this.previewData = {
        sender: selectedSender,
        recipient: recipientInfo,
        subject: this.selectedTemplate.subject,
        content: this.selectedTemplate.content
      };
      this.previewOpen = true;
    },
    
    /** 获取收件人信息 */
    getRecipientInfo() {
      const recipientType = this.sendForm.recipientType;
      
      switch (recipientType) {
        case 'all':
          return {
            type: 'all',
            display: `全部收件人(${this.contactOptions.length})`
          };
        case 'group':
          if (this.sendForm.groupIds && this.sendForm.groupIds.length > 0) {
            const selectedGroups = this.groupOptions.filter(group => 
              this.sendForm.groupIds.includes(group.groupId)
            );
            return {
              type: 'group',
              display: '指定群组',
              items: selectedGroups.map(group => ({
                id: group.groupId,
                name: group.groupName,
                count: group.contactCount || 0
              }))
            };
          }
          return {
            type: 'group',
            display: '未选择群组'
          };
        case 'tag':
          if (this.sendForm.tagIds && this.sendForm.tagIds.length > 0) {
            const selectedTags = this.tagOptions.filter(tag => 
              this.sendForm.tagIds.includes(tag.tagId)
            );
            return {
              type: 'tag',
              display: '指定标签',
              items: selectedTags.map(tag => ({
                id: tag.tagId,
                name: tag.tagName,
                count: tag.contactCount || 0
              }))
            };
          }
          return {
            type: 'tag',
            display: '未选择标签'
          };
        case 'manual':
          if (this.sendForm.contactIds && this.sendForm.contactIds.length > 0) {
            const selectedContacts = this.contactOptions.filter(contact => 
              this.sendForm.contactIds.includes(contact.contactId)
            );
            const contactList = selectedContacts.map(contact => 
              `${contact.name}(${contact.email})`
            ).join('，');
            return {
              type: 'manual',
              display: contactList
            };
          }
          return {
            type: 'manual',
            display: '未选择联系人'
          };
        default:
          return {
            type: 'unknown',
            display: '收件人类型未知'
          };
      }
    },
    /** 发送邮件 */
    handleSend() {
      // 防重复提交检查
      if (this.sending) {
        this.$message.warning('正在处理中，请勿重复提交');
        return;
      }
      
      this.$refs.sendForm.validate(valid => {
        if (valid) {
          // 验证收件人选择
          if (this.sendForm.recipientType === 'group' && (!this.sendForm.groupIds || this.sendForm.groupIds.length === 0)) {
            this.$message.error('请选择群组');
            return;
          }
          if (this.sendForm.recipientType === 'tag' && (!this.sendForm.tagIds || this.sendForm.tagIds.length === 0)) {
            this.$message.error('请选择标签');
            return;
          }
          if (this.sendForm.recipientType === 'manual' && (!this.sendForm.contactIds || this.sendForm.contactIds.length === 0)) {
            this.$message.error('请选择收件人');
            return;
          }

          // 验证模板选择
          if (this.sendForm.sendType === 'template') {
            if (!this.sendForm.templateId) {
              this.$message.error('请选择邮件模板');
              return;
            }
          } else {
            if (!this.sendForm.subject || !this.sendForm.content) {
              this.$message.error('请填写邮件主题和内容');
              return;
            }
          }
          
          this.$confirm('确认开始批量发送吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.sending = true;
            
            // 构建发送任务数据
            const taskData = {
              taskName: this.sendForm.taskName,
              senderId: this.sendForm.senderId,
              sendMode: this.sendForm.sendMode,
              sendInterval: this.sendForm.sendInterval,
              sendTime: this.sendForm.sendTime
            };

            // 设置收件人类型（使用英文名称）
            taskData.recipientType = this.sendForm.recipientType;

            // 设置模板ID（如果使用模板）
            if (this.sendForm.sendType === 'template' && this.sendForm.templateId) {
              taskData.templateId = this.sendForm.templateId;
            }

            // 根据发送类型设置subject和content
            if (this.sendForm.sendType === 'template') {
              // 使用模板时，从模板中获取subject和content
              if (this.selectedTemplate) {
                taskData.subject = this.selectedTemplate.subject;
                taskData.content = this.selectedTemplate.content;
              }
            } else {
              // 直接发送时，使用用户输入的内容
              taskData.subject = this.sendForm.subject;
              taskData.content = this.sendForm.content;
            }

            // 根据收件人类型添加相应的参数
            if (this.sendForm.recipientType === 'group') {
              taskData.groupIds = this.sendForm.groupIds && this.sendForm.groupIds.length > 0 ? this.sendForm.groupIds.join(',') : null;
            } else if (this.sendForm.recipientType === 'tag') {
              taskData.tagIds = this.sendForm.tagIds && this.sendForm.tagIds.length > 0 ? this.sendForm.tagIds.join(',') : null;
            } else if (this.sendForm.recipientType === 'manual') {
              taskData.contactIds = this.sendForm.contactIds && this.sendForm.contactIds.length > 0 ? this.sendForm.contactIds.join(',') : null;
            }
            // 对于 'all' 类型，不需要添加额外参数
            
            // 创建发送任务
            createSendTask(taskData).then(response => {
              this.$message.success('发送任务已创建，正在后台处理...');
              // 重置表单，但保持在当前页面
              this.handleReset();
            }).catch(error => {
              // 只显示后端返回的错误消息，避免重复提示
              if (error.response && error.response.data && error.response.data.msg) {
                // 显示后端返回的具体错误消息
                this.$message.error(error.response.data.msg);
              } else {
                // 如果没有后端错误消息，显示通用错误
                this.$message.error('创建发送任务失败');
              }
            }).finally(() => {
              this.sending = false;
            });
          });
        }
      });
    },
    /** 重置表单 */
    handleReset() {
      this.sendForm = {
        taskName: '',
        senderId: null,
        recipientType: 'all',
        groupIds: [],
        tagIds: [],
        contactIds: [],
        sendType: 'template',
        templateId: null,
        subject: '',
        content: '',
        sendMode: 'immediate',
        sendInterval: 10,
        sendTime: null
      };
      this.selectedTemplate = null;
      this.$refs.sendForm.resetFields();
    }
  }
}
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}

/* 邮件预览样式 */
.email-preview {
  max-height: 600px;
  overflow-y: auto;
}

.preview-section {
  margin-bottom: 25px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}

.preview-section h4 {
  margin: 0;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.preview-section h4 i {
  margin-right: 8px;
  color: #409eff;
}

.preview-content {
  padding: 16px;
  background-color: #fff;
}

.preview-content p {
  margin: 8px 0;
  line-height: 1.6;
  color: #606266;
}

.preview-content strong {
  color: #303133;
  font-weight: 600;
  margin-right: 8px;
}

.email-body {
  margin-top: 12px;
}

.email-content {
  margin-top: 8px;
  padding: 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fafafa;
  max-height: 300px;
  overflow-y: auto;
  line-height: 1.6;
  color: #303133;
}

.email-content img {
  max-width: 100%;
  height: auto;
}

.email-content table {
  border-collapse: collapse;
  width: 100%;
}

.email-content table td,
.email-content table th {
  border: 1px solid #dcdfe6;
  padding: 8px;
}

.email-content a {
  color: #409eff;
  text-decoration: none;
}

.email-content a:hover {
  text-decoration: underline;
}

/* 发件人信息和发送方式同行显示 */
.sender-send-mode-row {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  gap: 20px;
}

.info-item {
  flex: 1;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}

.info-item h4 {
  margin: 0;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.info-item h4 i {
  margin-right: 8px;
  color: #409eff;
}

.info-item .preview-content {
  padding: 16px;
  background-color: #fff;
  min-height: 60px;
  display: flex;
  align-items: center;
}

.sender-info {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.send-mode-info {
  color: #606266;
  font-size: 13px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.send-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item {
  color: #909399;
  font-size: 12px;
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  border: 1px solid #e4e7ed;
}

/* 发送方式选择器样式 */
.send-mode-selector {
  display: flex;
  align-items: center;
}

.send-mode-selector .el-tag {
  transition: all 0.3s ease;
}

.send-mode-selector .el-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 表单提示样式 */
.form-tip {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
}

.form-tip i {
  margin-right: 4px;
  color: #409EFF;
}
</style>
