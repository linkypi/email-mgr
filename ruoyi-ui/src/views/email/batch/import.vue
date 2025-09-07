<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>批量导入收件人</span>
      </div>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <div slot="header">文件导入</div>
            
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
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card>
            <div slot="header">导入说明</div>
            <div class="import-instructions">
              <el-alert
                title="导入说明"
                type="info"
                :closable="false"
                show-icon>
                <div slot="description">
                  <p>1. 请先下载导入模板，按照模板格式填写数据</p>
                  <p>2. 支持导入的字段：姓名、邮箱、等级、群组名称、标签、状态</p>
                  <p>3. 邮箱地址不能重复，已删除的联系人可以重新导入</p>
                  <p>4. 如果选择"更新支持"，已存在的联系人将被更新</p>
                  <p>5. 导入过程中如有错误，会显示详细的错误信息</p>
                </div>
              </el-alert>
            </div>
            
            <div style="margin-top: 20px;">
              <h4>模板格式示例：</h4>
              <el-table :data="templateData" border style="width: 100%" size="mini">
                <el-table-column prop="name" label="姓名" width="80"></el-table-column>
                <el-table-column prop="email" label="邮箱" width="150"></el-table-column>
                <el-table-column prop="level" label="等级" width="80"></el-table-column>
                <el-table-column prop="groupName" label="群组名称" width="100"></el-table-column>
                <el-table-column prop="tags" label="标签" width="100"></el-table-column>
                <el-table-column prop="status" label="状态" width="80"></el-table-column>
              </el-table>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 导入结果 -->
      <el-card style="margin-top: 20px;" v-if="importResult">
        <div slot="header">导入结果</div>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="总记录数">{{ importResult.total }}</el-descriptions-item>
          <el-descriptions-item label="成功导入">{{ importResult.success }}</el-descriptions-item>
          <el-descriptions-item label="失败记录">{{ importResult.failed }}</el-descriptions-item>
        </el-descriptions>
        
        <div v-if="importResult.errors && importResult.errors.length > 0" style="margin-top: 20px;">
          <h4>错误详情：</h4>
          <el-table :data="importResult.errors" border style="width: 100%">
            <el-table-column prop="row" label="行号" width="80"></el-table-column>
            <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
            <el-table-column prop="error" label="错误信息"></el-table-column>
          </el-table>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script>
// 直接导入 request 工具
import request from '@/utils/request'

export default {
  name: 'EmailImport',
  data() {
    return {
      fileList: [],
      selectedFile: null,
      importLoading: false,
      isUpdateSupport: false,
      importForm: {},
      templateData: [
        {
          name: '张三',
          email: 'zhangsan@example.com',
          level: '重要',
          groupName: 'VIP客户',
          tags: '重要,VIP',
          status: '正常'
        },
        {
          name: '李四',
          email: 'lisi@example.com',
          level: '普通',
          groupName: '普通客户',
          tags: '项目',
          status: '正常'
        }
      ],
      importResult: null
    }
  },
  methods: {
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
        
        // 解析导入结果
        this.parseImportResult(response.msg)
        
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
    
    /** 导入联系人 API */
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
    
    /** 解析导入结果 */
    parseImportResult(message) {
      // 简单的解析逻辑，实际项目中可能需要更复杂的解析
      const successMatch = message.match(/共 (\d+) 条/)
      const failedMatch = message.match(/共 (\d+) 条数据格式不正确/)
      
      if (successMatch || failedMatch) {
        const total = parseInt(successMatch ? successMatch[1] : 0) + parseInt(failedMatch ? failedMatch[1] : 0)
        const success = parseInt(successMatch ? successMatch[1] : 0)
        const failed = parseInt(failedMatch ? failedMatch[1] : 0)
        
        this.importResult = {
          total: total,
          success: success,
          failed: failed,
          errors: failed > 0 ? [{ row: 0, email: '详见错误信息', error: message }] : []
        }
      }
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
    
    /** 下载文件 */
    download(url, params, filename) {
      request({
        url: url,
        method: 'get',
        params: params,
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
      }).catch(error => {
        console.error('下载失败:', error)
        this.$modal.msgError('文件下载失败')
      })
    }
  }
}
</script>

<style scoped>
.upload-demo {
  text-align: center;
}

.import-instructions {
  margin-bottom: 20px;
}

.import-instructions p {
  margin: 5px 0;
  line-height: 1.5;
}

.app-container {
  padding: 20px;
}

.el-card {
  margin-bottom: 20px;
}

.el-descriptions {
  margin-top: 20px;
}
</style>

