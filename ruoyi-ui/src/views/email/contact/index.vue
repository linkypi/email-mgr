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
        <el-col :span="1.5">
          <el-button type="info" icon="el-icon-download" size="mini" @click="handleImportTemplate">
            下载模板
          </el-button>
        </el-col>
      </el-row>

      <!-- 联系人列表 -->
      <el-table v-loading="loading" :data="contactList" style="width: 100%">
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
            <el-tag v-for="tag in getTagsArray(scope.row.tags)" :key="tag" size="mini" style="margin-right: 2px;">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="replyRate" label="回复率" width="80">
          <template slot-scope="scope">
            {{ scope.row.replyRate || 0 }}%
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" @close="handleDialogClose">
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
            <el-option 
              v-for="group in groupList" 
              :key="group.groupId" 
              :label="group.groupName" 
              :value="group.groupId">
            </el-option>
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
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确 定</el-button>
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
      
      <el-checkbox v-model="isUpdateSupport" style="margin-top: 20px;">
        是否更新已存在的联系人数据
      </el-checkbox>
      
      <el-button type="primary" @click="handleImportSubmit" :disabled="!selectedFile" :loading="importLoading" style="margin-top: 20px;">
        开始导入
      </el-button>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="importVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { 
  listContact, 
  getContact, 
  addContact, 
  updateContact, 
  delContact, 
  exportContact, 
  importContact, 
  importTemplate 
} from "@/api/email/contact";
import { getAllGroups } from "@/api/email/group";

export default {
  name: 'Contact',
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 联系人表格数据
      contactList: [],
      // 群组列表
      groupList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: '',
        email: '',
        company: '',
        level: ''
      },
      // 对话框显示状态
      dialogVisible: false,
      dialogTitle: '新增收件人',
      // 表单对象
      form: {
        contactId: null,
        name: '',
        email: '',
        company: '',
        address: '',
        age: null,
        gender: '0',
        level: '',
        groupId: null,
        tags: '',
        remark: ''
      },
      // 表单校验规则
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      // 提交按钮加载状态
      submitLoading: false,
      // 导入对话框显示状态
      importVisible: false,
      // 导入文件列表
      fileList: [],
      // 选中的文件
      selectedFile: null,
      // 是否更新已存在的数据
      isUpdateSupport: false,
      // 导入按钮加载状态
      importLoading: false
    }
  },
  created() {
    this.getList();
    this.getGroupList();
  },
  methods: {
    /** 查询联系人列表 */
    getList() {
      this.loading = true;
      listContact(this.queryParams).then(response => {
        this.contactList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    /** 查询群组列表 */
    getGroupList() {
      getAllGroups().then(response => {
        this.groupList = response.data || [];
      });
    },
    /** 搜索按钮操作 */
    handleSearch() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    handleReset() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        name: '',
        email: '',
        company: '',
        level: ''
      };
      this.getList();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.dialogTitle = '新增收件人';
      this.form = {
        contactId: null,
        name: '',
        email: '',
        company: '',
        address: '',
        age: null,
        gender: '0',
        level: '',
        groupId: null,
        tags: '',
        remark: ''
      };
      this.dialogVisible = true;
    },
    /** 修改按钮操作 */
    handleEdit(row) {
      this.dialogTitle = '编辑收件人';
      this.form = { ...row };
      this.dialogVisible = true;
    },
    /** 提交按钮 */
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.submitLoading = true;
          if (this.form.contactId != null) {
            updateContact(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.dialogVisible = false;
              this.getList();
            }).finally(() => {
              this.submitLoading = false;
            });
          } else {
            addContact(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.dialogVisible = false;
              this.getList();
            }).finally(() => {
              this.submitLoading = false;
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除联系人名称为"' + row.name + '"的数据项？').then(() => {
        return delContact(row.contactId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有联系人数据项？').then(() => {
        this.$modal.loading("正在导出数据，请稍候...");
        exportContact(this.queryParams).then(response => {
          this.$download.excel(response, "联系人数据.xlsx");
          this.$modal.closeLoading();
        });
      });
    },
    /** 导入按钮操作 */
    handleImport() {
      this.importVisible = true;
      this.fileList = [];
      this.selectedFile = null;
      this.isUpdateSupport = false;
    },
    /** 下载模板操作 */
    handleImportTemplate() {
      importTemplate().then(response => {
        this.$download.excel(response, "联系人导入模板.xlsx");
      });
    },
    /** 文件上传中处理 */
    handleFileChange(file) {
      this.selectedFile = file.raw;
      this.fileList = [file];
    },
    /** 文件上传提交处理 */
    handleImportSubmit() {
      if (!this.selectedFile) {
        this.$modal.msgWarning("请先选择文件");
        return;
      }
      
      this.importLoading = true;
      const formData = new FormData();
      formData.append('file', this.selectedFile);
      formData.append('isUpdateSupport', this.isUpdateSupport);
      
      importContact(formData).then(response => {
        this.$modal.msgSuccess(response.msg);
        this.importVisible = false;
        this.getList();
      }).catch(error => {
        this.$modal.msgError("导入失败：" + error.message);
      }).finally(() => {
        this.importLoading = false;
      });
    },
    /** 对话框关闭处理 */
    handleDialogClose() {
      this.$refs.form.resetFields();
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
      if (!tags) return [];
      return tags.split(',').filter(tag => tag.trim());
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
