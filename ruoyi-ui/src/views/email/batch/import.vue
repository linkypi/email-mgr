<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>导入收件人</span>
      </div>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <div slot="header">文件导入</div>
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
            
            <el-button type="primary" @click="handleImport" :disabled="!selectedFile" style="margin-top: 20px;">
              开始导入
            </el-button>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card>
            <div slot="header">导入模板</div>
            <p>请按照以下格式准备Excel文件：</p>
            <el-table :data="templateData" border style="width: 100%">
              <el-table-column prop="name" label="姓名" width="100"></el-table-column>
              <el-table-column prop="email" label="邮箱" width="150"></el-table-column>
              <el-table-column prop="company" label="企业" width="120"></el-table-column>
              <el-table-column prop="level" label="等级" width="80"></el-table-column>
              <el-table-column prop="group" label="群组" width="100"></el-table-column>
              <el-table-column prop="tags" label="标签"></el-table-column>
            </el-table>
            
            <el-button type="success" @click="handleDownloadTemplate" style="margin-top: 20px;">
              下载模板
            </el-button>
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
export default {
  name: 'EmailImport',
  data() {
    return {
      fileList: [],
      selectedFile: null,
      templateData: [
        {
          name: '张三',
          email: 'zhangsan@example.com',
          company: 'ABC公司',
          level: '重要',
          group: 'VIP客户',
          tags: '重要,VIP'
        },
        {
          name: '李四',
          email: 'lisi@example.com',
          company: 'XYZ企业',
          level: '普通',
          group: '普通客户',
          tags: '项目'
        }
      ],
      importResult: null
    }
  },
  methods: {
    handleFileChange(file) {
      this.selectedFile = file.raw
      this.fileList = [file]
    },
    handleImport() {
      if (!this.selectedFile) {
        this.$message.warning('请先选择文件')
        return
      }
      
      // 模拟导入过程
      this.$message.info('正在导入...')
      setTimeout(() => {
        this.importResult = {
          total: 100,
          success: 95,
          failed: 5,
          errors: [
            { row: 3, email: 'invalid@email', error: '邮箱格式不正确' },
            { row: 7, email: 'duplicate@example.com', error: '邮箱已存在' }
          ]
        }
        this.$message.success('导入完成')
      }, 2000)
    },
    handleDownloadTemplate() {
      // 下载模板文件
      this.$message.success('模板下载中...')
    }
  }
}
</script>

<style scoped>
.upload-demo {
  text-align: center;
}
</style>

