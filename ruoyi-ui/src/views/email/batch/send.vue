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
          
          <el-form-item label="发件人" prop="senderIds">
            <el-select
              v-model="sendForm.senderIds"
              multiple
              filterable
              remote
              reserve-keyword
              placeholder="选择发件人（支持多选和搜索）"
              style="width: 100%"
              :remote-method="searchSenders"
              :loading="senderLoading">
              <el-option
                v-for="item in senderOptions"
                :key="item.senderId"
                :label="item.senderName + ' (' + item.company + ') - 剩余:' + (item.dailyRemainingCount || 0) + '封'"
                :value="item.senderId">
              </el-option>
            </el-select>
            
            <!-- 选中发件人显示区域 -->
            <div v-if="sendForm.senderIds && sendForm.senderIds.length > 0" class="selected-senders-display">
              <div class="selected-senders-header">
                <span class="sender-count">已选择 {{ sendForm.senderIds.length }} 个发件人：</span>
                <el-button 
                  @click="clearAllSenders" 
                  type="text" 
                  size="mini" 
                  icon="el-icon-close">
                  清空
                </el-button>
              </div>
              <div class="selected-senders-list">
                <el-tag
                  v-for="senderId in sendForm.senderIds"
                  :key="senderId"
                  closable
                  @close="removeSender(senderId)"
                  size="small"
                  class="sender-tag">
                  {{ getSenderDisplayName(senderId) }}
                </el-tag>
              </div>
            </div>
            
            <div class="form-tip">
              <i class="el-icon-info"></i>
              可选择多个发件人，系统将使用所有选中发件人关联的邮箱账号进行发送
            </div>
          </el-form-item>
          
          <!-- 选择收件人 -->
          <el-form-item label="选择收件人" prop="contactIds">
            <div class="recipient-selector">
              <div class="recipient-actions">
                <el-button type="primary" @click="openRecipientDialog('to')" icon="el-icon-plus">
                  {{ selectedContacts.length > 0 ? '重新选择收件人' : '选择收件人' }}
                </el-button>
                <el-button type="success" @click="openRecipientDialog('cc')" icon="el-icon-copy-document">
                  选择抄送
                </el-button>
                <el-button type="warning" @click="openRecipientDialog('bcc')" icon="el-icon-view">
                  选择密送
                </el-button>
                <el-button v-if="hasAnyRecipients" @click="clearAllContacts" type="danger" plain size="small">
                  清空所有
                </el-button>
              </div>
              
              <div v-if="selectedContacts.length > 0" class="selected-contacts">
                <div class="contacts-summary">
                  <div class="summary-info">
                    <i class="el-icon-user"></i>
                    <span class="count-text">已选择 <strong>{{ selectedContacts.length }}</strong> 个收件人</span>
                  </div>
                  <div class="summary-actions">
                    <el-button type="text" @click="toggleContactsExpanded" size="small">
                      {{ contactsExpanded ? '收起详情' : '查看详情' }}
                    </el-button>
                  </div>
                </div>
                
                <div v-if="contactsExpanded" class="contacts-detail">
                  <div class="contacts-list">
                    <div 
                      v-for="(contact, index) in selectedContacts" 
                      :key="contact.contactId" 
                      class="contact-item">
                      <div class="contact-info">
                        <span class="contact-name">{{ contact.name }}</span>
                        <span class="contact-email">{{ contact.email }}</span>
                        <el-tag v-if="contact.level === '1'" type="danger" size="mini">重要</el-tag>
                        <el-tag v-else-if="contact.level === '2'" type="warning" size="mini">普通</el-tag>
                        <el-tag v-else-if="contact.level === '3'" type="info" size="mini">一般</el-tag>
                      </div>
                      <el-button 
                        type="text" 
                        @click="removeContact(contact.contactId)" 
                        size="mini" 
                        icon="el-icon-close"
                        class="remove-btn">
                      </el-button>
                    </div>
                  </div>
                </div>
                
                <div v-else class="contacts-preview">
                  <div class="preview-list">
                    <span 
                      v-for="(contact, index) in previewContacts" 
                      :key="contact.contactId" 
                      class="preview-item">
                      {{ contact.name }}({{ contact.email }})
                    </span>
                    <span v-if="selectedContacts.length > maxPreviewCount" class="more-indicator">
                      ...等{{ selectedContacts.length - maxPreviewCount }}个
                    </span>
                  </div>
                </div>
              </div>
              
              <!-- 抄送显示 -->
              <div v-if="ccContacts.length > 0" class="selected-contacts">
                <div class="contacts-summary">
                  <div class="summary-info">
                    <i class="el-icon-copy-document"></i>
                    <span class="count-text">抄送 <strong>{{ ccContacts.length }}</strong> 个联系人</span>
                  </div>
                  <div class="summary-actions">
                    <el-button type="text" @click="toggleCcExpanded" size="small">
                      {{ ccExpanded ? '收起详情' : '查看详情' }}
                    </el-button>
                  </div>
                </div>
                
                <div v-if="ccExpanded" class="contacts-detail">
                  <div class="contacts-list">
                    <div 
                      v-for="(contact, index) in ccContacts" 
                      :key="contact.contactId" 
                      class="contact-item">
                      <div class="contact-info">
                        <span class="contact-name">{{ contact.name }}</span>
                        <span class="contact-email">{{ contact.email }}</span>
                        <el-tag v-if="contact.level === '1'" type="danger" size="mini">重要</el-tag>
                        <el-tag v-else-if="contact.level === '2'" type="warning" size="mini">普通</el-tag>
                        <el-tag v-else-if="contact.level === '3'" type="info" size="mini">一般</el-tag>
                      </div>
                      <el-button 
                        type="text" 
                        @click="removeContact(contact.contactId, 'cc')" 
                        size="mini" 
                        icon="el-icon-close"
                        class="remove-btn">
                      </el-button>
                    </div>
                  </div>
                </div>
                
                <div v-else class="contacts-preview">
                  <div class="preview-list">
                    <span 
                      v-for="(contact, index) in ccPreviewContacts" 
                      :key="contact.contactId" 
                      class="preview-item">
                      {{ contact.name }}({{ contact.email }})
                    </span>
                    <span v-if="ccContacts.length > maxPreviewCount" class="more-indicator">
                      ...等{{ ccContacts.length - maxPreviewCount }}个
                    </span>
                  </div>
                </div>
              </div>
              
              <!-- 密送显示 -->
              <div v-if="bccContacts.length > 0" class="selected-contacts">
                <div class="contacts-summary">
                  <div class="summary-info">
                    <i class="el-icon-view"></i>
                    <span class="count-text">密送 <strong>{{ bccContacts.length }}</strong> 个联系人</span>
                  </div>
                  <div class="summary-actions">
                    <el-button type="text" @click="toggleBccExpanded" size="small">
                      {{ bccExpanded ? '收起详情' : '查看详情' }}
                    </el-button>
                  </div>
                </div>
                
                <div v-if="bccExpanded" class="contacts-detail">
                  <div class="contacts-list">
                    <div 
                      v-for="(contact, index) in bccContacts" 
                      :key="contact.contactId" 
                      class="contact-item">
                      <div class="contact-info">
                        <span class="contact-name">{{ contact.name }}</span>
                        <span class="contact-email">{{ contact.email }}</span>
                        <el-tag v-if="contact.level === '1'" type="danger" size="mini">重要</el-tag>
                        <el-tag v-else-if="contact.level === '2'" type="warning" size="mini">普通</el-tag>
                        <el-tag v-else-if="contact.level === '3'" type="info" size="mini">一般</el-tag>
                      </div>
                      <el-button 
                        type="text" 
                        @click="removeContact(contact.contactId, 'bcc')" 
                        size="mini" 
                        icon="el-icon-close"
                        class="remove-btn">
                      </el-button>
                    </div>
                  </div>
                </div>
                
                <div v-else class="contacts-preview">
                  <div class="preview-list">
                    <span 
                      v-for="(contact, index) in bccPreviewContacts" 
                      :key="contact.contactId" 
                      class="preview-item">
                      {{ contact.name }}({{ contact.email }})
                    </span>
                    <span v-if="bccContacts.length > maxPreviewCount" class="more-indicator">
                      ...等{{ bccContacts.length - maxPreviewCount }}个
                    </span>
                  </div>
                </div>
              </div>
              
              <div v-if="!hasAnyRecipients" class="no-recipients">
                <i class="el-icon-info"></i>
                <span>请点击上方按钮选择收件人</span>
              </div>
            </div>
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

    <!-- 收件人选择对话框 -->
    <el-dialog :title="recipientDialogTitle" :visible.sync="recipientDialogOpen" width="1000px" append-to-body>
      <div class="recipient-dialog">
        <!-- 搜索条件 -->
        <el-form :model="searchForm" :inline="true" class="search-form">
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable></el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable></el-input>
          </el-form-item>
          <el-form-item label="标签">
            <el-select v-model="searchForm.tagIds" multiple placeholder="请选择标签" clearable>
              <el-option
                v-for="item in tagOptions"
                :key="item.tagId"
                :label="item.tagName"
                :value="item.tagId">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="群组">
            <el-select v-model="searchForm.groupIds" multiple placeholder="请选择群组" clearable>
              <el-option
                v-for="item in groupOptions"
                :key="item.groupId"
                :label="item.groupName"
                :value="item.groupId">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="等级">
            <el-select v-model="searchForm.levels" multiple placeholder="请选择等级" clearable>
              <el-option label="重要" value="1"></el-option>
              <el-option label="普通" value="2"></el-option>
              <el-option label="一般" value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchContacts" :loading="searchLoading">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 操作栏 -->
        <div class="dialog-actions">
          <div class="selection-info">
            <span>已选择 {{ selectedContactIds.length }} 个收件人</span>
            <el-button type="text" @click="selectAllContacts" size="small">全选当前页</el-button>
            <el-button type="text" @click="selectAllContactsAllPages" size="small">全选所有页</el-button>
            <el-button type="text" @click="clearSelection" size="small">清空选择</el-button>
          </div>
        </div>

        <!-- 联系人列表 -->
        <el-table 
          ref="contactTable"
          :data="contactList" 
          @selection-change="handleSelectionChange"
          v-loading="contactLoading"
          height="400">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="name" label="姓名" width="120"></el-table-column>
          <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
          <el-table-column prop="level" label="等级" width="80">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.level === '1'" type="danger" size="mini">重要</el-tag>
              <el-tag v-else-if="scope.row.level === '2'" type="warning" size="mini">普通</el-tag>
              <el-tag v-else-if="scope.row.level === '3'" type="info" size="mini">一般</el-tag>
              <span v-else class="no-data">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="tags" label="标签" width="150">
            <template slot-scope="scope">
              <el-tag 
                v-for="tag in getContactTags(scope.row.tags)" 
                :key="tag" 
                size="mini" 
                style="margin-right: 4px;">
                {{ tag }}
              </el-tag>
              <span v-if="!scope.row.tags || scope.row.tags.trim() === ''" class="no-data">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="company" label="公司" width="150">
            <template slot-scope="scope">
              <span v-if="scope.row.company">{{ scope.row.company }}</span>
              <span v-else class="no-data">-</span>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="电话" width="120">
            <template slot-scope="scope">
              <span v-if="scope.row.phone">{{ scope.row.phone }}</span>
              <span v-else class="no-data">-</span>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="searchForm.pageNum"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="searchForm.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="contactTotal">
          </el-pagination>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="recipientDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="confirmRecipients">确认选择 ({{ selectedContactIds.length }})</el-button>
      </div>
    </el-dialog>

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
import { getAllContacts, searchContacts } from "@/api/email/contact";
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
        senderIds: [],
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
      // 收件人选择相关
      recipientDialogOpen: false,
      recipientDialogType: 'to', // 当前对话框类型：to, cc, bcc
      selectedContacts: [], // 已选择的收件人列表
      ccContacts: [], // 抄送联系人列表
      bccContacts: [], // 密送联系人列表
      selectedContactIds: [], // 当前弹窗中选中的收件人ID列表
      contactList: [], // 弹窗中的联系人列表
      contactTotal: 0, // 联系人总数
      contactLoading: false,
      searchLoading: false,
      contactsExpanded: false, // 是否展开显示所有收件人
      ccExpanded: false, // 是否展开显示抄送联系人
      bccExpanded: false, // 是否展开显示密送联系人
      maxPreviewCount: 5, // 预览显示的最大收件人数量
      // 搜索表单
      searchForm: {
        name: '',
        email: '',
        tagIds: [],
        groupIds: [],
        levels: [],
        pageNum: 1,
        pageSize: 20
      },
      // 表单验证规则
      rules: {
        taskName: [
          { required: true, message: "任务名称不能为空", trigger: "blur" }
        ],
        senderIds: [
          { required: true, message: "请选择至少一个发件人", trigger: "change" },
          {
            validator: (rule, value, callback) => {
              if (!value || value.length === 0) {
                callback(new Error('请选择至少一个发件人'));
              } else {
                callback();
              }
            },
            trigger: "change"
          }
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
        contactIds: [
          { 
            validator: (rule, value, callback) => {
              if (!this.selectedContacts || this.selectedContacts.length === 0) {
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
  computed: {
    // 预览显示的收件人列表（限制数量）
    previewContacts() {
      return this.selectedContacts.slice(0, this.maxPreviewCount);
    },
    ccPreviewContacts() {
      return this.ccContacts.slice(0, this.maxPreviewCount);
    },
    bccPreviewContacts() {
      return this.bccContacts.slice(0, this.maxPreviewCount);
    },
    hasAnyRecipients() {
      return this.selectedContacts.length > 0 || this.ccContacts.length > 0 || this.bccContacts.length > 0;
    },
    recipientDialogTitle() {
      switch (this.recipientDialogType) {
        case 'cc':
          return '选择抄送联系人';
        case 'bcc':
          return '选择密送联系人';
        default:
          return '选择收件人';
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
          this.sendForm.contactIds = taskData.contactIds ? taskData.contactIds.split(',').map(id => parseInt(id)) : [];
          // 如果有手动选择的联系人，需要加载联系人信息
          if (this.sendForm.contactIds.length > 0) {
            this.loadSelectedContacts(this.sendForm.contactIds);
          }
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
      const selectedSenders = this.senderOptions.filter(sender => 
        this.sendForm.senderIds && this.sendForm.senderIds.includes(sender.senderId)
      );
      
      // 获取收件人信息
      let recipientInfo = this.getRecipientInfo();
      
      this.previewData = {
        senders: selectedSenders,
        recipient: recipientInfo,
        subject: this.selectedTemplate.subject,
        content: this.selectedTemplate.content
      };
      this.previewOpen = true;
    },
    
    /** 获取收件人信息 */
    getRecipientInfo() {
      if (this.selectedContacts && this.selectedContacts.length > 0) {
        const contactList = this.selectedContacts.map(contact =>
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
    },
    /** 获取发件人显示名称 */
    getSenderDisplayName(senderId) {
      const sender = this.senderOptions.find(item => item.senderId == senderId);
      if (sender) {
        return `${sender.senderName}(${sender.company}) - 剩余:${sender.dailyRemainingCount || 0}封`;
      }
      return `发件人ID: ${senderId}`;
    },
    /** 移除单个发件人 */
    removeSender(senderId) {
      const index = this.sendForm.senderIds.indexOf(senderId);
      if (index > -1) {
        this.sendForm.senderIds.splice(index, 1);
      }
    },
    /** 清空所有发件人 */
    clearAllSenders() {
      this.sendForm.senderIds = [];
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
          if (!this.hasAnyRecipients) {
            this.$message.error('请选择至少一个收件人');
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
              senderIds: this.sendForm.senderIds.join(','), // 将数组转换为逗号分隔的字符串
              sendMode: this.sendForm.sendMode,
              sendInterval: this.sendForm.sendInterval,
              sendTime: this.sendForm.sendTime
            };

            // 设置收件人类型为手动选择
            taskData.recipientType = 'manual';

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

            // 设置收件人ID列表
            taskData.contactIds = this.selectedContacts && this.selectedContacts.length > 0 ? this.selectedContacts.map(c => c.contactId).join(',') : null;
            
            // 设置抄送联系人ID列表
            taskData.ccContactIds = this.ccContacts && this.ccContacts.length > 0 ? this.ccContacts.map(c => c.contactId).join(',') : null;
            
            // 设置密送联系人ID列表
            taskData.bccContactIds = this.bccContacts && this.bccContacts.length > 0 ? this.bccContacts.map(c => c.contactId).join(',') : null;
            
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
        senderIds: [],
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
      this.selectedContacts = [];
      this.ccContacts = [];
      this.bccContacts = [];
      this.contactsExpanded = false;
      this.ccExpanded = false;
      this.bccExpanded = false;
      this.$refs.sendForm.resetFields();
    },
    
    // ========== 收件人选择相关方法 ==========
    
    /** 打开收件人选择对话框 */
    openRecipientDialog(type = 'to') {
      this.recipientDialogType = type;
      this.recipientDialogOpen = true;
      this.resetSearch();
      this.searchContacts();
    },
    
    /** 搜索联系人 */
    searchContacts() {
      this.contactLoading = true;
      this.searchLoading = true;
      
      // 构建搜索参数
      const searchParams = {
        name: this.searchForm.name,
        email: this.searchForm.email,
        pageNum: this.searchForm.pageNum,
        pageSize: this.searchForm.pageSize
      };
      
      // 添加等级条件
      if (this.searchForm.levels && this.searchForm.levels.length > 0) {
        searchParams.levels = this.searchForm.levels;
      }
      
      // 添加标签和群组条件
      if (this.searchForm.tagIds && this.searchForm.tagIds.length > 0) {
        searchParams.tagIds = this.searchForm.tagIds;
      }
      if (this.searchForm.groupIds && this.searchForm.groupIds.length > 0) {
        searchParams.groupIds = this.searchForm.groupIds;
      }
      
      searchContacts(searchParams).then(response => {
        this.contactList = response.rows || [];
        this.contactTotal = response.total || 0;
        this.contactLoading = false;
        this.searchLoading = false;
      }).catch(error => {
        console.error('搜索联系人失败:', error);
        this.$message.error('搜索联系人失败');
        this.contactList = [];
        this.contactTotal = 0;
        this.contactLoading = false;
        this.searchLoading = false;
      });
    },
    
    /** 重置搜索条件 */
    resetSearch() {
      this.searchForm = {
        name: '',
        email: '',
        tagIds: [],
        groupIds: [],
        levels: [],
        pageNum: 1,
        pageSize: 20
      };
    },
    
    /** 处理表格选择变化 */
    handleSelectionChange(selection) {
      this.selectedContactIds = selection.map(item => item.contactId);
    },
    
    /** 全选当前页 */
    selectAllContacts() {
      this.$refs.contactTable && this.$refs.contactTable.toggleAllSelection();
    },
    
    /** 全选所有页 */
    selectAllContactsAllPages() {
      this.$confirm('确定要选择所有页的收件人吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 获取所有符合条件的数据
        const allPagesParams = {
          name: this.searchForm.name,
          email: this.searchForm.email,
          pageNum: 1,
          pageSize: 9999 // 设置一个很大的值来获取所有数据
        };
        
        // 添加等级条件
        if (this.searchForm.levels && this.searchForm.levels.length > 0) {
          allPagesParams.levels = this.searchForm.levels;
        }
        
        // 添加标签和群组条件
        if (this.searchForm.tagIds && this.searchForm.tagIds.length > 0) {
          allPagesParams.tagIds = this.searchForm.tagIds;
        }
        if (this.searchForm.groupIds && this.searchForm.groupIds.length > 0) {
          allPagesParams.groupIds = this.searchForm.groupIds;
        }
        
        searchContacts(allPagesParams).then(response => {
          const allContacts = response.rows || [];
          const allContactIds = allContacts.map(contact => contact.contactId);
          
          // 合并到已选择的收件人列表中（去重）
          const existingIds = this.selectedContacts.map(c => c.contactId);
          const newContacts = allContacts.filter(contact => 
            !existingIds.includes(contact.contactId)
          );
          
          this.selectedContacts = [...this.selectedContacts, ...newContacts];
          this.sendForm.contactIds = this.selectedContacts.map(c => c.contactId);
          
          this.$message.success(`已添加 ${newContacts.length} 个收件人，当前共选择 ${this.selectedContacts.length} 个收件人（共 ${allContacts.length} 个符合条件）`);
        }).catch(error => {
          console.error('全选所有页失败:', error);
          this.$message.error('全选所有页失败');
        });
      });
    },
    
    /** 清空选择 */
    clearSelection() {
      this.$refs.contactTable && this.$refs.contactTable.clearSelection();
      this.selectedContactIds = [];
    },
    
    /** 确认选择收件人 */
    confirmRecipients() {
      if (this.selectedContactIds.length === 0) {
        this.$message.warning('请选择至少一个联系人');
        return;
      }
      
      // 获取选中的联系人信息
      const selectedContacts = this.contactList.filter(contact => 
        this.selectedContactIds.includes(contact.contactId)
      );
      
      // 根据对话框类型添加到不同的列表
      let targetList, existingIds, listName;
      
      switch (this.recipientDialogType) {
        case 'cc':
          targetList = this.ccContacts;
          listName = '抄送';
          break;
        case 'bcc':
          targetList = this.bccContacts;
          listName = '密送';
          break;
        default:
          targetList = this.selectedContacts;
          listName = '收件人';
          break;
      }
      
      // 合并到目标列表中（去重）
      existingIds = targetList.map(c => c.contactId);
      const newContacts = selectedContacts.filter(contact => 
        !existingIds.includes(contact.contactId)
      );
      
      // 更新目标列表
      if (this.recipientDialogType === 'cc') {
        this.ccContacts = [...this.ccContacts, ...newContacts];
      } else if (this.recipientDialogType === 'bcc') {
        this.bccContacts = [...this.bccContacts, ...newContacts];
      } else {
        this.selectedContacts = [...this.selectedContacts, ...newContacts];
        this.sendForm.contactIds = this.selectedContacts.map(c => c.contactId);
      }
      
      // 关闭对话框并清空选择
      this.recipientDialogOpen = false;
      this.clearSelection();
      
      this.$message.success(`已添加 ${newContacts.length} 个${listName}联系人`);
    },
    
    /** 移除单个收件人 */
    removeContact(contactId, type = 'to') {
      switch (type) {
        case 'cc':
          this.ccContacts = this.ccContacts.filter(c => c.contactId !== contactId);
          this.$message.success('已移除该抄送联系人');
          break;
        case 'bcc':
          this.bccContacts = this.bccContacts.filter(c => c.contactId !== contactId);
          this.$message.success('已移除该密送联系人');
          break;
        default:
          this.selectedContacts = this.selectedContacts.filter(c => c.contactId !== contactId);
          this.sendForm.contactIds = this.selectedContacts.map(c => c.contactId);
          this.$message.success('已移除该收件人');
          break;
      }
    },
    
    /** 清空所有收件人 */
    clearAllContacts() {
      this.$confirm('确定要清空所有已选择的联系人吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.selectedContacts = [];
        this.ccContacts = [];
        this.bccContacts = [];
        this.sendForm.contactIds = [];
        this.contactsExpanded = false;
        this.ccExpanded = false;
        this.bccExpanded = false;
        this.$message.success('已清空所有联系人');
      });
    },
    
    /** 切换收件人列表展开状态 */
    toggleContactsExpanded() {
      this.contactsExpanded = !this.contactsExpanded;
    },
    /** 切换抄送联系人展开状态 */
    toggleCcExpanded() {
      this.ccExpanded = !this.ccExpanded;
    },
    /** 切换密送联系人展开状态 */
    toggleBccExpanded() {
      this.bccExpanded = !this.bccExpanded;
    },
    
    /** 获取联系人标签列表 */
    getContactTags(tags) {
      if (!tags) return [];
      return tags.split(',').filter(tag => tag.trim());
    },
    
    /** 分页大小变化 */
    handleSizeChange(val) {
      this.searchForm.pageSize = val;
      this.searchForm.pageNum = 1;
      this.searchContacts();
    },
    
    /** 当前页变化 */
    handleCurrentChange(val) {
      this.searchForm.pageNum = val;
      this.searchContacts();
    },
    
    /** 加载已选择的联系人信息 */
    loadSelectedContacts(contactIds) {
      // 从contactOptions中查找对应的联系人信息
      this.selectedContacts = this.contactOptions.filter(contact => 
        contactIds.includes(contact.contactId)
      );
    }
  }
}
</script>

<style scoped>
/* 选中发件人显示区域样式 */
.selected-senders-display {
  margin-top: 10px;
  padding: 12px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
}

.selected-senders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.sender-count {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.selected-senders-list {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
}

.sender-tag {
  max-width: 280px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sender-tag {
    max-width: 200px;
  }
  .selected-senders-list {
    gap: 4px;
  }
}
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

/* 收件人选择器样式 */
.recipient-selector {
  width: 100%;
}

.recipient-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.selected-contacts {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background-color: #fafafa;
  overflow: hidden;
}

.contacts-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.summary-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-info i {
  color: #409eff;
  font-size: 16px;
}

.count-text {
  font-size: 14px;
  color: #606266;
}

.count-text strong {
  color: #409eff;
  font-weight: 600;
}

.contacts-detail {
  max-height: 300px;
  overflow-y: auto;
}

.contacts-list {
  padding: 10px 15px;
}

.contact-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.contact-name {
  font-weight: 500;
  color: #303133;
  min-width: 80px;
}

.contact-email {
  color: #606266;
  font-size: 13px;
  flex: 1;
}

.remove-btn {
  color: #f56c6c;
  padding: 4px;
}

.remove-btn:hover {
  background-color: #fef0f0;
}

.contacts-preview {
  padding: 12px 15px;
  background-color: #fff;
}

.preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.preview-item {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  border: 1px solid #d9ecff;
}

.more-indicator {
  color: #909399;
  font-size: 12px;
  font-style: italic;
}

.no-recipients {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px;
  text-align: center;
  color: #909399;
  background-color: #f9f9f9;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  margin-top: 10px;
}

.no-recipients i {
  font-size: 16px;
}

/* 收件人选择对话框样式 */
.recipient-dialog {
  padding: 0;
}

.search-form {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.dialog-actions {
  margin-bottom: 15px;
  padding: 10px 0;
  border-bottom: 1px solid #e4e7ed;
}

.selection-info {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
  color: #606266;
}

.pagination-container {
  margin-top: 15px;
  text-align: right;
}

.no-data {
  color: #c0c4cc;
  font-style: italic;
}
</style>
