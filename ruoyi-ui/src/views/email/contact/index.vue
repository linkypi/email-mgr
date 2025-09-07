<template>
  <div class="app-container">
    <!-- 统计面板 -->
    <!-- <div class="stats-panel" v-if="statistics">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalCount }}</div>
            <div class="stat-label">总联系人</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.activeCount }}</div>
            <div class="stat-label">活跃联系人</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="el-icon-star-on"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.highLevelCount }}</div>
            <div class="stat-label">重要联系人</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="el-icon-chat-line-round"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.avgReplyRate }}%</div>
            <div class="stat-label">平均回复率</div>
          </div>
        </div>
      </div>
    </div> -->

    <!-- 搜索卡片 -->
    <div class="search-card">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter.native="handleSearch"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="queryParams.email" placeholder="请输入邮箱" clearable @keyup.enter.native="handleSearch"></el-input>
        </el-form-item>
        <el-form-item label="企业">
          <el-input v-model="queryParams.company" placeholder="请输入企业" clearable @keyup.enter.native="handleSearch"></el-input>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="queryParams.level" placeholder="请选择等级" clearable>
            <el-option label="重要" value="1"></el-option>
            <el-option label="普通" value="2"></el-option>
            <el-option label="一般" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="群组">
          <el-select v-model="queryParams.groupId" placeholder="请选择群组" clearable>
            <el-option v-for="group in groupList" :key="group.groupId" :label="group.groupName" :value="group.groupId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="0"></el-option>
            <el-option label="停用" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch" :loading="searchLoading">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">
          新增收件人
        </el-button>
        <el-button type="success" icon="el-icon-upload" @click="handleImport">
          导入
        </el-button>
        <el-button type="warning" icon="el-icon-download" @click="handleExport">
          导出
        </el-button>
        <el-button type="info" icon="el-icon-refresh" @click="getList">
          刷新
        </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <!-- 批量操作 -->
      <el-row :gutter="10" class="mb8" v-if="multipleSelection.length > 0">
        <el-col :span="1.5">
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" icon="el-icon-refresh" size="mini" @click="handleBatchUpdateStatistics">
            更新统计
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="info" icon="el-icon-upload2" size="mini" @click="handleBatchRestore">
            批量恢复
          </el-button>
        </el-col>
      </el-row>

      <!-- 联系人列表 -->
      <el-table 
        v-loading="loading" 
        :data="contactList" 
        @selection-change="handleSelectionChange"
        style="width: 100%">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="contactId" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="level" label="等级" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="getLevelType(scope.row.level)" size="mini">
              {{ getLevelText(scope.row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="groupName" label="群组" width="100" show-overflow-tooltip></el-table-column>
        <el-table-column prop="tags" label="标签" width="150">
          <template slot-scope="scope">
            <el-tag v-for="tag in getTagsArray(scope.row.tags)" :key="tag" size="mini" style="margin-right: 2px;">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sendCount" label="发送数" width="80" align="center"></el-table-column>
        <el-table-column prop="replyCount" label="回复数" width="80" align="center"></el-table-column>
        <el-table-column prop="replyRate" label="回复率" width="80" align="center">
          <template slot-scope="scope">
            <span class="rate-value">{{ scope.row.replyRate }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" size="mini">
              {{ scope.row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
            <el-button size="mini" type="text" icon="el-icon-refresh" @click="handleUpdateStatistics(scope.row)">更新统计</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="800px" :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" maxlength="100"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="200"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="等级">
              <el-select v-model="form.level" placeholder="请选择等级" style="width: 100%">
                <el-option label="重要" value="1"></el-option>
                <el-option label="普通" value="2"></el-option>
                <el-option label="一般" value="3"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="群组">
              <el-select v-model="form.groupId" placeholder="请选择群组" style="width: 100%">
                <el-option v-for="group in groupList" :key="group.groupId" :label="group.groupName" :value="group.groupId"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="请输入标签，用逗号分隔" maxlength="500"></el-input>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注" maxlength="500" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog title="导入收件人" :visible.sync="importVisible" width="600px" :close-on-click-modal="false">
      <div style="margin-bottom: 20px;">
        <el-alert
          title="导入说明"
          type="info"
          :closable="false"
          show-icon>
          <div slot="description">
            <p>1. 支持导入 .xlsx、.xls、.csv 格式文件</p>
            <p>2. 文件大小不超过 10MB</p>
            <p>3. 请先下载模板，按照模板格式填写数据</p>
            <p>4. 邮箱地址必须唯一，不能重复</p>
          </div>
        </el-alert>
      </div>
      
      <el-form :model="importForm" label-width="100px">
        <el-form-item label="选择文件">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            accept=".xlsx,.xls,.csv">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">只能上传xlsx/xls/csv文件，且不超过10MB</div>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="更新支持">
          <el-switch
            v-model="isUpdateSupport"
            active-text="支持更新已存在的联系人"
            inactive-text="仅新增联系人">
          </el-switch>
        </el-form-item>
      </el-form>
      
      <div style="text-align: center; margin: 20px 0;">
        <el-button type="primary" @click="handleImportSubmit" :disabled="!selectedFile" :loading="importLoading">
          开始导入
        </el-button>
        <el-button @click="handleDownloadTemplate" style="margin-left: 10px;">
          下载模板
        </el-button>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="importVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 直接导入 request 工具
import request from '@/utils/request'

export default {
  name: 'Contact',
  created() {
    console.log('=== Contact 组件 created ===')
  },
  mounted() {
    console.log('=== Contact 组件 mounted ===')
    this.getList()
    // this.getStatistics()
  },
  data() {
      return {
        // 加载状态
        loading: false,
        // 搜索按钮加载状态
        searchLoading: false,
        // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: '',
        email: '',
        company: '',
        level: '',
        groupId: '',
        status: ''
      },
      // 联系人列表
      contactList: [],
      // 总数
      total: 0,
      // 多选
      multipleSelection: [],
      // 统计信息
      statistics: null,
      // 群组列表
      groupList: [
        { groupId: 1, groupName: 'VIP客户' },
        { groupId: 2, groupName: '普通客户' },
        { groupId: 3, groupName: '潜在客户' },
        { groupId: 4, groupName: '合作伙伴' }
      ],
      // 对话框
      dialogVisible: false,
      dialogTitle: '新增收件人',
      // 表单
      form: {
        contactId: null,
        name: '',
        email: '',
        level: '3',
        groupId: '',
        tags: '',
        status: '0',
        remark: ''
      },
      // 表单验证规则
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      // 导入对话框
      importVisible: false,
      fileList: [],
      selectedFile: null,
      isUpdateSupport: false,
      importLoading: false,
      importForm: {}
    }
  },
  methods: {
    // API 方法定义
    getContactList(query) {
      return request({
        url: '/email/contact/list',
        method: 'get',
        params: query
      })
    },
    
    getContact(contactId) {
      return request({
        url: '/email/contact/' + contactId,
        method: 'get'
      })
    },
    
    addContact(data) {
      return request({
        url: '/email/contact',
        method: 'post',
        data: data
      })
    },
    
    updateContact(data) {
      return request({
        url: '/email/contact',
        method: 'put',
        data: data
      })
    },
    
    delContact(contactId) {
      return request({
        url: '/email/contact/' + contactId,
        method: 'delete'
      })
    },
    
    batchDeleteContacts(contactIds) {
      return request({
        url: '/email/contact/batchDelete',
        method: 'post',
        data: contactIds
      })
    },
    
    importContact(formData) {
      return request({
        url: '/email/contact/importData',
        method: 'post',
        data: formData,
        transformRequest: [function (data) {
          return data
        }],
        // 添加特殊标记，跳过全局错误处理
        skipGlobalErrorHandler: true
      })
    },
    
    getContactStatistics() {
      return request({
        url: '/email/contact/statistics',
        method: 'get'
      })
    },
    
    updateContactStatistics(contactId) {
      return request({
        url: '/email/contact/updateStatistics/' + contactId,
        method: 'put'
      })
    },
    
    batchUpdateContactStatistics(contactIds) {
      return request({
        url: '/email/contact/batchUpdateStatistics',
        method: 'put',
        data: contactIds
      })
    },
    
    batchRestoreContacts(contactIds) {
      return request({
        url: '/email/contact/batchRestore',
        method: 'post',
        data: contactIds
      })
    },
    /** 查询联系人列表 */
    getList() {
      this.loading = true
      console.log('查询参数:', this.queryParams) // 添加调试信息
      this.getContactList(this.queryParams).then(response => {
        console.log('查询结果:', response) // 添加调试信息
        this.contactList = response.rows || []
        this.total = response.total || 0
        this.loading = false
        this.searchLoading = false // 重置搜索按钮loading状态
      }).catch(error => {
        console.error('查询失败:', error) // 添加错误信息
        this.$modal.msgError('查询失败: ' + (error.message || '未知错误'))
        this.contactList = []
        this.total = 0
        this.loading = false
        this.searchLoading = false // 重置搜索按钮loading状态
      })
    },
    
    /** 获取统计信息 */
    getStatistics() {
      this.getContactStatistics().then(response => {
        this.statistics = response.data
      })
    },
    
    /** 搜索按钮操作 */
    handleSearch() {
      console.log('=== 搜索按钮被点击了 ===') // 添加调试信息
      console.log('当前查询参数:', this.queryParams) // 添加调试信息
      this.searchLoading = true
      this.queryParams.pageNum = 1
      this.getList()
    },
    
    /** 重置按钮操作 */
    handleReset() {
      console.log('=== 重置按钮被点击了 ===') // 添加调试信息
      this.searchLoading = false // 重置搜索按钮loading状态
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        name: '',
        email: '',
        company: '',
        level: '',
        groupId: '',
        status: ''
      }
      this.getList()
    },
    
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.multipleSelection = selection
    },
    
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.dialogTitle = '新增收件人'
      this.dialogVisible = true
    },
    
    /** 修改按钮操作 */
    handleEdit(row) {
      this.reset()
      const contactId = row.contactId || row.id
      this.getContact(contactId).then(response => {
        this.form = response.data
        this.dialogTitle = '修改收件人'
        this.dialogVisible = true
      })
    },
    
    /** 提交按钮 */
    handleSubmit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.contactId != null) {
            this.updateContact(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.dialogVisible = false
              this.getList()
            })
          } else {
            this.addContact(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.dialogVisible = false
              this.getList()
            })
          }
        }
      })
    },
    
    /** 删除按钮操作 */
    handleDelete(row) {
      const contactIds = row.contactId || row.id
      this.$modal.confirm('是否确认删除收件人编号为"' + contactIds + '"的数据项？').then(function() {
        return this.delContact(contactIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    
    /** 批量删除操作 */
    handleBatchDelete() {
      if (this.multipleSelection.length === 0) {
        this.$modal.msgError('请选择要删除的数据')
        return
      }
      
      const contactIds = this.multipleSelection.map(item => item.contactId || item.id)
      this.$modal.confirm('是否确认删除选中的' + contactIds.length + '条数据？').then(() => {
        return this.batchDeleteContacts(contactIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    
    /** 导出按钮操作 */
    handleExport() {
      this.download('email/contact/export', {
        ...this.queryParams
      }, `收件人_${new Date().getTime()}.xlsx`)
    },
    
    /** 下载模板 */
    downloadTemplate(url, filename) {
      request({
        url: url,
        method: 'post',
        responseType: 'blob'
      }).then(response => {
        const blob = new Blob([response])
        const downloadUrl = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = downloadUrl
        link.download = filename
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(downloadUrl)
        this.$modal.msgSuccess('模板下载成功')
      }).catch(error => {
        console.error('下载失败:', error)
        this.$modal.msgError('模板下载失败')
      })
    },
    
    /** 导入按钮操作 */
    handleImport() {
      this.importVisible = true
    },
    
    /** 文件选择处理 */
    handleFileChange(file, fileList) {
      console.log('=== 文件选择事件触发 ===')
      console.log('file:', file)
      console.log('file.raw:', file.raw)
      console.log('fileList:', fileList)
      
      this.selectedFile = file.raw
      this.fileList = fileList
      
      console.log('设置后的 selectedFile:', this.selectedFile)
      console.log('设置后的 fileList:', this.fileList)
    },
    
    /** 提交导入 */
    handleImportSubmit() {
      if (!this.selectedFile) {
        this.$modal.msgError('请先选择文件')
        return
      }
      
      console.log('选择的文件:', this.selectedFile)
      console.log('文件名称:', this.selectedFile.name)
      console.log('文件大小:', this.selectedFile.size)
      console.log('文件类型:', this.selectedFile.type)
      
      const formData = new FormData()
      formData.append('file', this.selectedFile)
      formData.append('isUpdateSupport', this.isUpdateSupport)
      
      console.log('FormData 内容:')
      for (let [key, value] of formData.entries()) {
        console.log(key, value)
      }
      
      this.importLoading = true
      this.importContact(formData).then(response => {
        this.importLoading = false
        this.$modal.msgSuccess(response.msg)
        this.importVisible = false
        this.getList()
        this.getStatistics()
        // 重置导入状态
        this.selectedFile = null
        this.fileList = []
        this.isUpdateSupport = false
      }).catch(error => {
        this.importLoading = false
        console.error('导入失败:', error)
        
        // 显示详细的错误信息，支持HTML换行
        let errorMessage = '导入失败'
        if (error.response && error.response.data && error.response.data.msg) {
          errorMessage = error.response.data.msg
        } else if (error.message) {
          errorMessage = error.message
        }
        
        // 使用 alertError 显示支持HTML的错误信息
        this.$modal.alertError(errorMessage)
      })
    },
    
    /** 下载模板 */
    handleDownloadTemplate() {
      this.downloadTemplate('email/contact/importTemplate', `收件人导入模板_${new Date().getTime()}.xlsx`)
    },
    
    /** 更新统计信息 */
    handleUpdateStatistics(row) {
      const contactId = row.contactId || row.id
      this.updateContactStatistics(contactId).then(() => {
        this.$modal.msgSuccess("统计信息更新成功")
        this.getList()
        this.getStatistics()
      })
    },
    
    /** 批量更新统计信息 */
    handleBatchUpdateStatistics() {
      if (this.multipleSelection.length === 0) {
        this.$modal.msgError('请选择要更新的数据')
        return
      }
      
      const contactIds = this.multipleSelection.map(item => item.contactId || item.id)
      this.batchUpdateContactStatistics(contactIds).then(response => {
        this.$modal.msgSuccess(response.msg)
        this.getList()
        this.getStatistics()
      })
    },
    
    /** 批量恢复 */
    handleBatchRestore() {
      if (this.multipleSelection.length === 0) {
        this.$modal.msgError('请选择要恢复的数据')
        return
      }
      
      const contactIds = this.multipleSelection.map(item => item.contactId || item.id)
      this.$modal.confirm('是否确认恢复选中的' + contactIds.length + '条数据？').then(() => {
        return this.batchRestoreContacts(contactIds)
      }).then(response => {
        this.$modal.msgSuccess(response.msg)
        this.getList()
      }).catch(() => {})
    },
    
    /** 表单重置 */
    reset() {
      this.form = {
        contactId: null,
        name: '',
        email: '',
        company: '',
        address: '',
        age: null,
        gender: '0',
        socialMedia: '',
        followers: 0,
        level: '3',
        groupId: '',
        tags: '',
        status: '0',
        remark: ''
      }
      this.resetForm("form")
    },
    
    /** 获取等级类型 */
    getLevelType(level) {
      const types = {
        '1': 'danger',
        '2': 'warning',
        '3': 'info'
      }
      return types[level] || 'info'
    },
    
    /** 获取等级文本 */
    getLevelText(level) {
      const texts = {
        '1': '重要',
        '2': '普通',
        '3': '一般'
      }
      return texts[level] || '一般'
    },
    
    /** 获取标签数组 */
    getTagsArray(tags) {
      if (!tags) return []
      return tags.split(',').filter(tag => tag.trim())
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  .page-title {
    font-size: 22px;
    font-weight: 600;
    color: #303133;
    display: flex;
    align-items: center;

    i {
      margin-right: 10px;
      color: #409eff;
      background: rgba(64, 158, 255, 0.1);
      padding: 8px;
      border-radius: 50%;
      font-size: 18px;
    }
  }

  .page-actions {
    display: flex;
    gap: 10px;
  }
}

.stats-panel {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }

  .stat-card {
    display: flex;
    align-items: center;
    padding: 15px;
    background: #f9fafc;
    border-radius: 6px;
    border: 1px solid #e0e6ed;

    .stat-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
      font-size: 20px;
      color: white;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .stat-info {
      flex: 1;

      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 5px;
      }

      .stat-label {
        font-size: 14px;
        color: #606266;
      }
    }
  }
}

.search-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.table-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.rate-value {
  font-weight: 600;
  color: #67c23a;
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-table {
  ::v-deep .el-table__header-wrapper {
    th {
      background: #f5f7fa;
      color: #606266;
      font-weight: 600;
    }
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
  
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
}
</style>
