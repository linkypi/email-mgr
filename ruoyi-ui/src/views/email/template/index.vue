<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable />
      </el-form-item>
      <el-form-item label="模板类型" prop="templateType">
        <el-select v-model="queryParams.templateType" placeholder="模板类型" clearable>
          <el-option label="产品推广" value="product" />
          <el-option label="节日问候" value="holiday" />
          <el-option label="活动邀请" value="activity" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['email:template:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['email:template:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['email:template:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模板ID" align="center" prop="templateId" />
      <el-table-column label="模板名称" align="center" prop="templateName" />
      <el-table-column label="邮件主题" align="center" prop="subject" />
      <el-table-column label="模板类型" align="center" prop="templateType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.email_template_type" :value="scope.row.templateType"/>
        </template>
      </el-table-column>
      <el-table-column label="使用次数" align="center" prop="useCount" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['email:template:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['email:template:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-view" @click="handlePreview(scope.row)" v-hasPermi="['email:template:preview']">预览</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['email:template:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改邮件模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="邮件主题" prop="subject">
          <el-input v-model="form.subject" placeholder="请输入邮件主题" />
        </el-form-item>
        <el-form-item label="模板类型" prop="templateType">
          <el-select v-model="form.templateType" placeholder="请选择模板类型">
            <el-option label="产品推广" value="product"></el-option>
            <el-option label="节日问候" value="holiday"></el-option>
            <el-option label="活动邀请" value="activity"></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="邮件内容" prop="content">
          <el-input type="textarea" v-model="form.content" placeholder="请输入邮件内容" :rows="10" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 模板预览对话框 -->
    <el-dialog title="模板预览" :visible.sync="previewOpen" width="600px" append-to-body>
      <div class="template-preview">
        <h3>{{ previewData.subject }}</h3>
        <div class="template-content" v-html="previewData.content"></div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="previewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTemplate, getTemplate, delTemplate, addTemplate, updateTemplate, previewTemplate } from "@/api/email/template";

export default {
  name: "Template",
  dicts: ['email_template_type'],
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
      // 模板表格数据
      templateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示预览弹出层
      previewOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateName: null,
        templateType: null
      },
      // 表单参数
      form: {},
      // 预览数据
      previewData: {},
      // 表单校验
      rules: {
        templateName: [
          { required: true, message: "模板名称不能为空", trigger: "blur" }
        ],
        subject: [
          { required: true, message: "邮件主题不能为空", trigger: "blur" }
        ],
        templateType: [
          { required: true, message: "模板类型不能为空", trigger: "change" }
        ],
        content: [
          { required: true, message: "邮件内容不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询模板列表 */
    getList() {
      this.loading = true;
      listTemplate(this.queryParams).then(response => {
        this.templateList = response.rows;
        this.total = response.total;
        this.loading = false;
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
        templateId: null,
        templateName: null,
        subject: null,
        templateType: null,
        content: null,
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
      this.ids = selection.map(item => item.templateId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加邮件模板";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const templateId = row.templateId || this.ids
      getTemplate(templateId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改邮件模板";
      });
    },
    /** 查看按钮操作 */
    handleView(row) {
      getTemplate(row.templateId).then(response => {
        this.previewData = response.data;
        this.previewOpen = true;
      });
    },
    /** 预览按钮操作 */
    handlePreview(row) {
      previewTemplate(row.templateId).then(response => {
        this.previewData = response.data;
        this.previewOpen = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.templateId != null) {
            updateTemplate(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTemplate(this.form).then(response => {
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
      const templateIds = row.templateId || this.ids;
      this.$modal.confirm('是否确认删除模板编号为"' + templateIds + '"的数据项?').then(function() {
        return delTemplate(templateIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.template-preview {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #fafafa;
}

.template-preview h3 {
  margin: 0 0 15px 0;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 10px;
}

.template-content {
  line-height: 1.6;
  color: #606266;
}
</style>
