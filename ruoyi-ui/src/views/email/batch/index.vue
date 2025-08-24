<template>
  <div class="app-container">
    <!-- 测试标记：确认页面已更新 -->
    <el-alert
      title="✅ 页面已更新 - 包含邮件模板选择、占位符参数、测试数据、自定义发送等功能"
      type="success"
      :closable="false"
      show-icon
      style="margin-bottom: 20px;">
    </el-alert>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务名称" prop="taskName">
        <el-input v-model="queryParams.taskName" placeholder="请输入任务名称" clearable />
      </el-form-item>
      <el-form-item label="任务状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="任务状态" clearable>
          <el-option label="等待中" value="pending" />
          <el-option label="进行中" value="running" />
          <el-option label="已完成" value="completed" />
          <el-option label="失败" value="failed" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['email:task:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['email:task:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['email:task:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="taskList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="任务ID" align="center" prop="taskId" />
      <el-table-column label="任务名称" align="center" prop="taskName" />
      <el-table-column label="模板名称" align="center" prop="templateName" />
      <el-table-column label="发件人" align="center" prop="accountName" />
      <el-table-column label="收件人数量" align="center" prop="recipientCount" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="进度" align="center" prop="progress" width="200">
        <template slot-scope="scope">
          <el-progress :percentage="scope.row.progress" :status="getProgressStatus(scope.row.status)"></el-progress>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['email:task:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['email:task:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-play" @click="handleStart(scope.row)" v-if="scope.row.status === 'pending'" v-hasPermi="['email:task:start']">启动</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-pause" @click="handlePause(scope.row)" v-if="scope.row.status === 'running'" v-hasPermi="['email:task:pause']">暂停</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['email:task:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改发送任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <!-- 基本信息 -->
        <el-card class="box-card" style="margin-bottom: 20px;">
          <div slot="header" class="clearfix">
            <span>基本信息</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="任务名称" prop="taskName">
                <el-input v-model="form.taskName" placeholder="请输入任务名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选择发件人" prop="accountId">
                <el-select v-model="form.accountId" placeholder="请选择发件人账号" style="width: 100%">
                  <el-option
                    v-for="item in accountOptions"
                    :key="item.accountId"
                    :label="item.accountName + ' (' + item.emailAddress + ')'"
                    :value="item.accountId">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="发送间隔(秒)" prop="sendInterval">
                <el-input-number v-model="form.sendInterval" :min="1" :max="3600" style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="发送时间" prop="sendTime">
                <el-date-picker
                  v-model="form.sendTime"
                  type="datetime"
                  placeholder="选择发送时间"
                  style="width: 100%">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 邮件模板选择 -->
        <el-card class="box-card" style="margin-bottom: 20px;">
          <div slot="header" class="clearfix">
            <span>邮件模板选择</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="选择模板" prop="templateId">
                <el-select v-model="form.templateId" placeholder="请选择邮件模板" style="width: 100%" @change="handleTemplateChange">
                  <el-option
                    v-for="item in templateOptions"
                    :key="item.templateId"
                    :label="item.templateName"
                    :value="item.templateId">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="发送策略" prop="strategy">
                <el-select v-model="form.strategy" placeholder="请选择发送策略">
                  <el-option label="轮流发送" value="round"></el-option>
                  <el-option label="顺序发送" value="sequential"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 模板预览 -->
        <el-card class="box-card" style="margin-bottom: 20px;" v-if="selectedTemplate">
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

        <!-- 模板参数 -->
        <el-card class="box-card" style="margin-bottom: 20px;" v-if="templateVariables.length > 0">
          <div slot="header" class="clearfix">
            <span>模板参数设置</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="8" v-for="variable in templateVariables" :key="variable">
              <el-form-item :label="variable" :prop="'variables.' + variable">
                <el-input v-model="form.variables[variable]" :placeholder="'请输入' + variable"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 自定义发送内容 -->
        <el-card class="box-card" style="margin-bottom: 20px;">
          <div slot="header" class="clearfix">
            <span>自定义发送内容</span>
          </div>
          <el-form-item label="启用自定义">
            <el-switch v-model="form.customSend" active-text="启用自定义内容"></el-switch>
          </el-form-item>
          <el-form-item label="自定义主题" v-if="form.customSend" prop="customSubject">
            <el-input v-model="form.customSubject" placeholder="请输入自定义邮件主题"></el-input>
          </el-form-item>
          <el-form-item label="自定义内容" v-if="form.customSend" prop="customContent">
            <el-input type="textarea" v-model="form.customContent" :rows="6" placeholder="请输入自定义邮件内容"></el-input>
          </el-form-item>
        </el-card>

        <!-- 收件人选择 -->
        <el-card class="box-card" style="margin-bottom: 20px;">
          <div slot="header" class="clearfix">
            <span>收件人选择</span>
          </div>
          <el-form-item label="收件人类型" prop="recipientType">
            <el-radio-group v-model="form.recipientType" @change="handleRecipientTypeChange">
              <el-radio label="all">全部联系人</el-radio>
              <el-radio label="group">指定群组</el-radio>
              <el-radio label="tag">指定标签</el-radio>
              <el-radio label="manual">手动选择</el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 群组选择 -->
          <el-form-item label="选择群组" v-if="form.recipientType === 'group'">
            <el-select v-model="form.groupIds" multiple placeholder="请选择群组" style="width: 100%">
              <el-option
                v-for="item in groupOptions"
                :key="item.groupId"
                :label="item.groupName"
                :value="item.groupId">
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 标签选择 -->
          <el-form-item label="选择标签" v-if="form.recipientType === 'tag'">
            <el-select v-model="form.tagIds" multiple placeholder="请选择标签" style="width: 100%">
              <el-option
                v-for="item in tagOptions"
                :key="item.tagId"
                :label="item.tagName"
                :value="item.tagId">
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 手动选择联系人 -->
          <el-form-item label="选择联系人" v-if="form.recipientType === 'manual'">
            <el-select v-model="form.contactIds" multiple placeholder="请选择联系人" style="width: 100%">
              <el-option
                v-for="item in contactOptions"
                :key="item.contactId"
                :label="item.name + ' (' + item.email + ')'"
                :value="item.contactId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-card>

        <!-- 备注 -->
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 任务详情对话框 -->
    <el-dialog title="任务详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务名称">{{ detailData.taskName }}</el-descriptions-item>
        <el-descriptions-item label="模板名称">{{ detailData.templateName }}</el-descriptions-item>
        <el-descriptions-item label="发件人">{{ detailData.accountName }}</el-descriptions-item>
        <el-descriptions-item label="收件人数量">{{ detailData.recipientCount }}</el-descriptions-item>
        <el-descriptions-item label="发送间隔">{{ detailData.sendInterval }}秒</el-descriptions-item>
        <el-descriptions-item label="发送策略">{{ detailData.strategy === 'round' ? '轮流发送' : '顺序发送' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 模板预览对话框 -->
    <el-dialog title="邮件预览" :visible.sync="previewOpen" width="800px" append-to-body>
      <div v-if="previewData">
        <h4>邮件主题：{{ previewData.subject }}</h4>
        <div v-html="previewData.content" style="border: 1px solid #dcdfe6; padding: 20px; border-radius: 4px; background-color: #fafafa; margin-top: 10px; max-height: 400px; overflow-y: auto;"></div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="previewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTask, getTask, delTask, addTask, updateTask, startTask, pauseTask } from "@/api/email/task";
import { getAllTemplates, getTemplate } from "@/api/email/template";
import { getAllAccounts } from "@/api/email/account";
import { getAllGroups } from "@/api/email/group";
import { getAllTags } from "@/api/email/tag";
import { getAllContacts } from "@/api/email/contact";

export default {
  name: "BatchSend",
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
      // 任务表格数据
      taskList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示预览弹出层
      previewOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskName: null,
        status: null
      },
      // 表单参数
      form: {
        taskId: null,
        taskName: null,
        templateId: null,
        accountId: null,
        recipientType: "all",
        groupIds: [],
        tagIds: [],
        contactIds: [],
        sendInterval: 10,
        sendTime: null,
        strategy: "round",
        customSend: false,
        customSubject: "",
        customContent: "",
        variables: {},
        remark: null
      },
      // 详情数据
      detailData: {},
      // 预览数据
      previewData: null,
      // 模板选项
      templateOptions: [],
      // 账号选项
      accountOptions: [],
      // 群组选项
      groupOptions: [],
      // 标签选项
      tagOptions: [],
      // 联系人选项
      contactOptions: [],
      // 选中的模板
      selectedTemplate: null,
      // 模板变量
      templateVariables: [],
      // 表单校验
      rules: {
        taskName: [
          { required: true, message: "任务名称不能为空", trigger: "blur" }
        ],
        templateId: [
          { required: true, message: "请选择邮件模板", trigger: "change" }
        ],
        accountId: [
          { required: true, message: "请选择发件人账号", trigger: "change" }
        ],
        recipientType: [
          { required: true, message: "请选择收件人类型", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getTemplateOptions();
    this.getAccountOptions();
    this.getGroupOptions();
    this.getTagOptions();
    this.getContactOptions();
  },
  methods: {
    /** 查询任务列表 */
    getList() {
      this.loading = true;
      listTask(this.queryParams).then(response => {
        this.taskList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取模板选项 */
    getTemplateOptions() {
      getAllTemplates().then(response => {
        this.templateOptions = response.data;
      });
    },
    /** 获取账号选项 */
    getAccountOptions() {
      getAllAccounts().then(response => {
        this.accountOptions = response.data;
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
    /** 获取联系人选项 */
    getContactOptions() {
      getAllContacts().then(response => {
        this.contactOptions = response.data;
      });
    },
    /** 模板选择变化 */
    handleTemplateChange(templateId) {
      if (templateId) {
        getTemplate(templateId).then(response => {
          this.selectedTemplate = response.data;
          this.extractTemplateVariables();
        });
      } else {
        this.selectedTemplate = null;
        this.templateVariables = [];
        this.form.variables = {};
      }
    },
    /** 提取模板变量 */
    extractTemplateVariables() {
      if (!this.selectedTemplate) return;
      
      const content = this.selectedTemplate.content;
      const subject = this.selectedTemplate.subject;
      const variables = [];
      const regex = /\$\{([^}]+)\}/g;
      let match;
      
      // 从内容中提取变量
      while ((match = regex.exec(content)) !== null) {
        if (!variables.includes(match[1])) {
          variables.push(match[1]);
        }
      }
      
      // 从主题中提取变量
      regex.lastIndex = 0;
      while ((match = regex.exec(subject)) !== null) {
        if (!variables.includes(match[1])) {
          variables.push(match[1]);
        }
      }
      
      this.templateVariables = variables;
      this.form.variables = {};
      variables.forEach(variable => {
        this.form.variables[variable] = '';
      });
    },
    /** 加载测试数据 */
    loadTestData() {
      const testData = {
        'name': '张三',
        'email': 'zhangsan@example.com',
        'company': '测试公司',
        'phone': '13800138000',
        'address': '北京市朝阳区',
        'position': '经理',
        'department': '技术部',
        'website': 'www.example.com',
        'product': '测试产品',
        'price': '999元',
        'discount': '8折',
        'date': '2024-01-01',
        'time': '10:00',
        'location': '会议室A',
        'meeting': '项目讨论会',
        'username': 'testuser',
        'password': '123456',
        'url': 'https://example.com',
        'code': 'ABC123',
        'amount': '1000元',
        'percent': '85%'
      };
      
      this.templateVariables.forEach(variable => {
        this.form.variables[variable] = testData[variable] || `测试${variable}`;
      });
      
      this.$message.success('测试数据已加载');
    },
    /** 预览模板 */
    previewTemplate() {
      if (!this.selectedTemplate) {
        this.$message.warning('请先选择模板');
        return;
      }
      
      let content = this.selectedTemplate.content;
      let subject = this.selectedTemplate.subject;
      
      // 替换变量
      this.templateVariables.forEach(variable => {
        const value = this.form.variables[variable] || `[${variable}]`;
        const regex = new RegExp(`\\$\\{${variable}\\}`, 'g');
        content = content.replace(regex, value);
        subject = subject.replace(regex, value);
      });
      
      this.previewData = {
        subject: subject,
        content: content
      };
      this.previewOpen = true;
    },
    /** 收件人类型变化 */
    handleRecipientTypeChange(type) {
      this.form.groupIds = [];
      this.form.tagIds = [];
      this.form.contactIds = [];
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        taskId: null,
        taskName: null,
        templateId: null,
        accountId: null,
        recipientType: "all",
        groupIds: [],
        tagIds: [],
        contactIds: [],
        sendInterval: 10,
        sendTime: null,
        strategy: "round",
        customSend: false,
        customSubject: "",
        customContent: "",
        variables: {},
        remark: null
      };
      this.selectedTemplate = null;
      this.templateVariables = [];
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
      this.ids = selection.map(item => item.taskId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加发送任务";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const taskId = row.taskId || this.ids
      getTask(taskId).then(response => {
        this.form = response.data;
        if (this.form.templateId) {
          this.handleTemplateChange(this.form.templateId);
        }
        this.open = true;
        this.title = "修改发送任务";
      });
    },
    /** 查看按钮操作 */
    handleView(row) {
      getTask(row.taskId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    /** 启动任务操作 */
    handleStart(row) {
      this.$confirm('确认启动该任务吗？', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        startTask(row.taskId).then(response => {
          this.$modal.msgSuccess("启动成功");
          this.getList();
        });
      });
    },
    /** 暂停任务操作 */
    handlePause(row) {
      this.$confirm('确认暂停该任务吗？', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        pauseTask(row.taskId).then(response => {
          this.$modal.msgSuccess("暂停成功");
          this.getList();
        });
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 验证收件人选择
          if (this.form.recipientType === 'group' && (!this.form.groupIds || this.form.groupIds.length === 0)) {
            this.$message.error('请选择群组');
            return;
          }
          if (this.form.recipientType === 'tag' && (!this.form.tagIds || this.form.tagIds.length === 0)) {
            this.$message.error('请选择标签');
            return;
          }
          if (this.form.recipientType === 'manual' && (!this.form.contactIds || this.form.contactIds.length === 0)) {
            this.$message.error('请选择联系人');
            return;
          }
          
          if (this.form.taskId != null) {
            updateTask(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTask(this.form).then(response => {
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
      const taskIds = row.taskId || this.ids;
      this.$modal.confirm('是否确认删除任务编号为"' + taskIds + '"的数据项?').then(function() {
        return delTask(taskIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    getStatusType(status) {
      const statusMap = {
        'pending': 'info',
        'running': 'warning',
        'completed': 'success',
        'failed': 'danger'
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        'pending': '等待中',
        'running': '进行中',
        'completed': '已完成',
        'failed': '失败'
      }
      return statusMap[status] || '未知'
    },
    getProgressStatus(status) {
      if (status === 'failed') return 'exception'
      if (status === 'completed') return 'success'
      return ''
    }
  }
};
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}
</style>
