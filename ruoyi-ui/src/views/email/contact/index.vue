<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>收件人管理</span>
        <el-button style="float: right" type="primary" size="small" @click="handleAdd">
          新增收件人
        </el-button>
      </div>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="queryParams.email" placeholder="请输入邮箱" clearable></el-input>
        </el-form-item>
        <el-form-item label="企业">
          <el-input v-model="queryParams.company" placeholder="请输入企业" clearable></el-input>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="queryParams.level" placeholder="请选择等级" clearable>
            <el-option label="重要" value="1"></el-option>
            <el-option label="普通" value="2"></el-option>
            <el-option label="一般" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="success" icon="el-icon-upload" size="mini" @click="handleImport">
            导入
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" icon="el-icon-download" size="mini" @click="handleExport">
            导出
          </el-button>
        </el-col>
      </el-row>

      <!-- 联系人列表 -->
      <el-table :data="contactList" style="width: 100%">
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="company" label="企业" width="150"></el-table-column>
        <el-table-column prop="level" label="等级" width="80">
          <template slot-scope="scope">
            <el-tag :type="getLevelType(scope.row.level)">
              {{ getLevelText(scope.row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="groupName" label="群组" width="100"></el-table-column>
        <el-table-column prop="tags" label="标签" width="150">
          <template slot-scope="scope">
            <el-tag v-for="tag in scope.row.tags" :key="tag" size="mini" style="margin-right: 2px;">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="replyRate" label="回复率" width="80">
          <template slot-scope="scope">
            {{ scope.row.replyRate }}%
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="企业">
          <el-input v-model="form.company" placeholder="请输入企业"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址"></el-input>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="1" :max="120" placeholder="年龄"></el-input-number>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="1">男</el-radio>
            <el-radio label="2">女</el-radio>
            <el-radio label="0">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="form.level" placeholder="请选择等级">
            <el-option label="重要" value="1"></el-option>
            <el-option label="普通" value="2"></el-option>
            <el-option label="一般" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="群组">
          <el-select v-model="form.groupId" placeholder="请选择群组">
            <el-option label="VIP客户" value="1"></el-option>
            <el-option label="普通客户" value="2"></el-option>
            <el-option label="潜在客户" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="请输入标签，用逗号分隔"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog title="导入收件人" :visible.sync="importVisible" width="500px">
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
      
      <el-button type="primary" @click="handleImportSubmit" :disabled="!selectedFile" style="margin-top: 20px;">
        开始导入
      </el-button>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="importVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Contact',
  data() {
    return {
      queryParams: {
        name: '',
        email: '',
        company: '',
        level: ''
      },
      contactList: [
        {
          contactId: 1,
          name: '张经理',
          email: 'zhang@company.com',
          company: 'ABC公司',
          level: '1',
          groupName: 'VIP客户',
          tags: ['重要', 'VIP'],
          replyRate: 67.42
        },
        {
          contactId: 2,
          name: '王晓明',
          email: 'wang@company.com',
          company: 'XYZ企业',
          level: '3',
          groupName: '普通客户',
          tags: ['项目'],
          replyRate: 53.85
        },
        {
          contactId: 3,
          name: '李思思',
          email: 'li@company.com',
          company: 'DEF集团',
          level: '2',
          groupName: 'VIP客户',
          tags: ['重要', '待处理'],
          replyRate: 76.79
        }
      ],
      dialogVisible: false,
      dialogTitle: '新增收件人',
      form: {
        name: '',
        email: '',
        company: '',
        address: '',
        age: null,
        gender: '0',
        level: '',
        groupId: '',
        tags: '',
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      importVisible: false,
      fileList: [],
      selectedFile: null
    }
  },
  methods: {
    handleSearch() {
      this.$message.info('搜索功能待实现')
    },
    handleReset() {
      this.queryParams = {
        name: '',
        email: '',
        company: '',
        level: ''
      }
    },
    handleAdd() {
      this.dialogTitle = '新增收件人'
      this.form = {
        name: '',
        email: '',
        company: '',
        address: '',
        age: null,
        gender: '0',
        level: '',
        groupId: '',
        tags: '',
        remark: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑收件人'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确认删除该收件人吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('删除成功')
      })
    },
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$message.success('保存成功')
          this.dialogVisible = false
        }
      })
    },
    handleImport() {
      this.importVisible = true
    },
    handleExport() {
      this.$message.success('导出功能待实现')
    },
    handleFileChange(file) {
      this.selectedFile = file.raw
      this.fileList = [file]
    },
    handleImportSubmit() {
      if (!this.selectedFile) {
        this.$message.warning('请先选择文件')
        return
      }
      
      this.$message.info('正在导入...')
      setTimeout(() => {
        this.$message.success('导入完成')
        this.importVisible = false
      }, 2000)
    },
    getLevelType(level) {
      const types = {
        '1': 'danger',
        '2': 'warning',
        '3': 'info'
      }
      return types[level] || 'info'
    },
    getLevelText(level) {
      const texts = {
        '1': '重要',
        '2': '普通',
        '3': '一般'
      }
      return texts[level] || '一般'
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
