<template>
  <div class="app-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="page-title">
        <i class="fas fa-users"></i>
        收件人管理
      </div>
      <div class="page-actions">
        <el-button type="primary" icon="fas fa-plus" @click="handleAdd">新增联系人</el-button>
        <el-button icon="fas fa-upload" @click="handleImport">批量导入</el-button>
        <el-button icon="fas fa-download" @click="handleExport">导出数据</el-button>
      </div>
    </div>

    <!-- 统计面板 -->
    <div class="stats-panel">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-users"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalContacts || 0 }}</div>
            <div class="stat-label">总联系人</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-user-check"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.activeContacts || 0 }}</div>
            <div class="stat-label">活跃联系人</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-envelope"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalSent || 0 }}</div>
            <div class="stat-label">总发送数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-reply"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.avgReplyRate || 0 }}%</div>
            <div class="stat-label">平均回复率</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-card">
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入联系人姓名"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="queryParams.email"
            placeholder="请输入邮箱地址"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="企业" prop="company">
          <el-input
            v-model="queryParams.company"
            placeholder="请输入企业名称"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="群组" prop="groupId">
          <el-select v-model="queryParams.groupId" placeholder="请选择群组" clearable size="small">
            <el-option
              v-for="group in groupOptions"
              :key="group.groupId"
              :label="group.groupName"
              :value="group.groupId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="等级" prop="level">
          <el-select v-model="queryParams.level" placeholder="请选择等级" clearable size="small">
            <el-option label="重要" value="1" />
            <el-option label="普通" value="2" />
            <el-option label="一般" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['email:contact:add']"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['email:contact:edit']"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['email:contact:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['email:contact:export']"
          >导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="contactList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="联系人ID" align="center" prop="contactId" width="100" />
        <el-table-column label="姓名" align="center" prop="name" width="120" />
        <el-table-column label="邮箱地址" align="center" prop="email" width="200" />
        <el-table-column label="企业名称" align="center" prop="company" width="150" />
        <el-table-column label="年龄" align="center" prop="age" width="80" />
        <el-table-column label="性别" align="center" prop="gender" width="80">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.gender"/>
          </template>
        </el-table-column>
        <el-table-column label="等级" align="center" prop="level" width="100">
          <template slot-scope="scope">
            <el-tag :type="getLevelType(scope.row.level)" size="mini">
              {{ getLevelText(scope.row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="群组" align="center" prop="groupName" width="120" />
        <el-table-column label="标签" align="center" prop="tags" width="150">
          <template slot-scope="scope">
            <el-tag v-for="tag in scope.row.tagList" :key="tag" size="mini" style="margin-right: 5px;">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发送/回复" align="center" width="120">
          <template slot-scope="scope">
            {{ scope.row.sendCount || 0 }} / {{ scope.row.replyCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="回复率" align="center" prop="replyRate" width="100">
          <template slot-scope="scope">
            <span class="rate-value">{{ scope.row.replyRate || 0 }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['email:contact:edit']"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['email:contact:remove']"
            >删除</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
            >查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <!-- 添加或修改联系人对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱地址" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="企业名称" prop="company">
              <el-input v-model="form.company" placeholder="请输入企业名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="form.age" :min="1" :max="150" placeholder="请输入年龄" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择性别">
                <el-option label="未知" value="0" />
                <el-option label="男" value="1" />
                <el-option label="女" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="社交媒体" prop="socialMedia">
              <el-input v-model="form.socialMedia" placeholder="请输入社交媒体账号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="粉丝数量" prop="followers">
              <el-input-number v-model="form.followers" :min="0" placeholder="请输入粉丝数量" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择等级">
                <el-option label="重要" value="1" />
                <el-option label="普通" value="2" />
                <el-option label="一般" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="群组" prop="groupId">
              <el-select v-model="form.groupId" placeholder="请选择群组">
                <el-option
                  v-for="group in groupOptions"
                  :key="group.groupId"
                  :label="group.groupName"
                  :value="group.groupId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="标签" prop="tags">
              <el-select v-model="form.tagList" multiple placeholder="请选择标签" style="width: 100%">
                <el-option
                  v-for="tag in tagOptions"
                  :key="tag.tagId"
                  :label="tag.tagName"
                  :value="tag.tagName"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" />
            是否更新已经存在的数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listContact, getContact, delContact, addContact, updateContact, importTemplate } from "@/api/email/contact";
import { listGroup } from "@/api/email/group";
import { listTag } from "@/api/email/tag";
import { getToken } from "@/utils/auth";

export default {
  name: "EmailContact",
  dicts: ['sys_user_sex', 'sys_normal_disable'],
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
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 联系人表格数据
      contactList: [],
      // 群组选项
      groupOptions: [],
      // 标签选项
      tagOptions: [],
      // 统计数据
      statistics: {},
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        email: null,
        company: null,
        groupId: null,
        level: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "联系人姓名不能为空", trigger: "blur" }
        ],
        email: [
          { required: true, message: "邮箱地址不能为空", trigger: "blur" },
          { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }
        ]
      },
      // 文件上传
      upload: {
        open: false,
        title: '',
        isUploading: false,
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/email/contact/importData"
      }
    };
  },
  created() {
    this.getList();
    this.getGroupOptions();
    this.getTagOptions();
  },
  methods: {
    /** 查询联系人列表 */
    getList() {
      this.loading = true;
      listContact(this.queryParams).then(response => {
        this.contactList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取群组选项 */
    getGroupOptions() {
      listGroup().then(response => {
        this.groupOptions = response.rows;
      });
    },
    /** 获取标签选项 */
    getTagOptions() {
      listTag().then(response => {
        this.tagOptions = response.rows;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        contactId: null,
        name: null,
        email: null,
        company: null,
        address: null,
        age: null,
        gender: "0",
        socialMedia: null,
        followers: 0,
        level: "3",
        groupId: null,
        tags: null,
        tagList: [],
        status: "0",
        remark: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.contactId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加联系人";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const contactId = row.contactId || this.ids
      getContact(contactId).then(response => {
        this.form = response.data;
        this.form.tagList = this.form.tags ? this.form.tags.split(',') : [];
        this.open = true;
        this.title = "修改联系人";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.tags = this.form.tagList.join(',');
          if (this.form.contactId != null) {
            updateContact(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addContact(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const contactIds = row.contactId || this.ids;
      this.$modal.confirm('是否确认删除联系人编号为"' + contactIds + '"的数据项？').then(function() {
        return delContact(contactIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('email/contact/export', {
        ...this.queryParams
      }, `联系人_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "联系人数据导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    importTemplate() {
      importTemplate().then(response => {
        this.download(response.msg);
      });
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    },
    // 获取等级类型
    getLevelType(level) {
      const types = {
        '1': 'danger',
        '2': 'warning',
        '3': 'info'
      };
      return types[level] || 'info';
    },
    // 获取等级文本
    getLevelText(level) {
      const texts = {
        '1': '重要',
        '2': '普通',
        '3': '一般'
      };
      return texts[level] || '未知';
    },
    // 查看详情
    handleView(row) {
      this.reset();
      const contactId = row.contactId;
      getContact(contactId).then(response => {
        this.form = response.data;
        this.form.tagList = this.form.tags ? this.form.tags.split(',') : [];
        this.open = true;
        this.title = "查看联系人详情";
        // 设置表单为只读
        this.$nextTick(() => {
          const formEl = this.$refs["form"];
          if (formEl) {
            const inputs = formEl.$el.querySelectorAll('input, textarea, select');
            inputs.forEach(input => {
              input.disabled = true;
            });
          }
        });
      });
    }
  }
};
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
