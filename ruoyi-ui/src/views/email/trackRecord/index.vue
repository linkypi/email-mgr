<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务ID" prop="taskId">
        <el-input
          v-model="queryParams.taskId"
          placeholder="请输入任务ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="邮件主题" prop="subject">
        <el-input
          v-model="queryParams.subject"
          placeholder="请输入邮件主题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收件人" prop="recipient">
        <el-input
          v-model="queryParams.recipient"
          placeholder="请输入收件人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发件人" prop="sender">
        <el-input
          v-model="queryParams.sender"
          placeholder="请输入发件人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="邮件状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择邮件状态" clearable>
          <el-option
            v-for="dict in dict.type.email_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="邮箱账号" prop="accountId">
        <el-input
          v-model="queryParams.accountId"
          placeholder="请输入邮箱账号ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['email:track-record:add']"
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
          v-hasPermi="['email:track-record:edit']"
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
          v-hasPermi="['email:track-record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['email:track-record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="emailTrackRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键ID" align="center" prop="id" />
      <el-table-column label="任务ID" align="center" prop="taskId" />
      <el-table-column label="邮件Message-ID" align="center" prop="messageId" :show-overflow-tooltip="true" />
      <el-table-column label="邮件主题" align="center" prop="subject" :show-overflow-tooltip="true" />
      <el-table-column label="收件人" align="center" prop="recipient" :show-overflow-tooltip="true" />
      <el-table-column label="发件人" align="center" prop="sender" :show-overflow-tooltip="true" />
      <el-table-column label="邮件状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.email_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sentTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sentTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="送达时间" align="center" prop="deliveredTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deliveredTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="打开时间" align="center" prop="openedTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.openedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="回复时间" align="center" prop="repliedTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.repliedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="点击时间" align="center" prop="clickedTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.clickedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="重试次数" align="center" prop="retryCount" />
      <el-table-column label="邮箱账号ID" align="center" prop="accountId" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['email:track-record:query']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['email:track-record:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['email:track-record:remove']"
          >删除</el-button>
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

    <!-- 添加或修改邮件跟踪记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务ID" prop="taskId">
              <el-input v-model="form.taskId" placeholder="请输入任务ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮件Message-ID" prop="messageId">
              <el-input v-model="form.messageId" placeholder="请输入邮件Message-ID" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮件主题" prop="subject">
              <el-input v-model="form.subject" placeholder="请输入邮件主题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收件人" prop="recipient">
              <el-input v-model="form.recipient" placeholder="请输入收件人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="发件人" prop="sender">
              <el-input v-model="form.sender" placeholder="请输入发件人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮件状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择邮件状态">
                <el-option
                  v-for="dict in dict.type.email_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="邮箱账号ID" prop="accountId">
              <el-input v-model="form.accountId" placeholder="请输入邮箱账号ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="重试次数" prop="retryCount">
              <el-input-number v-model="form.retryCount" :min="0" :max="999" controls-position="right" placeholder="请输入重试次数" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="邮件内容" prop="content">
          <el-input v-model="form.content" type="textarea" placeholder="请输入邮件内容" />
        </el-form-item>
        <el-form-item label="错误日志" prop="errorLogs">
          <el-input v-model="form.errorLogs" type="textarea" placeholder="请输入错误日志" />
        </el-form-item>
        <el-form-item label="跟踪像素URL" prop="trackingPixelUrl">
          <el-input v-model="form.trackingPixelUrl" placeholder="请输入跟踪像素URL" />
        </el-form-item>
        <el-form-item label="跟踪链接URL" prop="trackingLinkUrl">
          <el-input v-model="form.trackingLinkUrl" placeholder="请输入跟踪链接URL" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看邮件跟踪记录对话框 -->
    <el-dialog title="查看邮件跟踪记录" :visible.sync="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="主键ID">{{ form.id }}</el-descriptions-item>
        <el-descriptions-item label="任务ID">{{ form.taskId }}</el-descriptions-item>
        <el-descriptions-item label="邮件Message-ID">{{ form.messageId }}</el-descriptions-item>
        <el-descriptions-item label="邮件主题">{{ form.subject }}</el-descriptions-item>
        <el-descriptions-item label="收件人">{{ form.recipient }}</el-descriptions-item>
        <el-descriptions-item label="发件人">{{ form.sender }}</el-descriptions-item>
        <el-descriptions-item label="邮件状态">
          <dict-tag :options="dict.type.email_status" :value="form.status"/>
        </el-descriptions-item>
        <el-descriptions-item label="重试次数">{{ form.retryCount }}</el-descriptions-item>
        <el-descriptions-item label="邮箱账号ID">{{ form.accountId }}</el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ parseTime(form.sentTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
        <el-descriptions-item label="送达时间">{{ parseTime(form.deliveredTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
        <el-descriptions-item label="打开时间">{{ parseTime(form.openedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
        <el-descriptions-item label="回复时间">{{ parseTime(form.repliedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
        <el-descriptions-item label="点击时间">{{ parseTime(form.clickedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(form.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(form.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
        <el-descriptions-item label="邮件内容" :span="2">{{ form.content }}</el-descriptions-item>
        <el-descriptions-item label="错误日志" :span="2">{{ form.errorLogs }}</el-descriptions-item>
        <el-descriptions-item label="跟踪像素URL" :span="2">{{ form.trackingPixelUrl }}</el-descriptions-item>
        <el-descriptions-item label="跟踪链接URL" :span="2">{{ form.trackingLinkUrl }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listEmailTrackRecord, getEmailTrackRecord, delEmailTrackRecord, addEmailTrackRecord, updateEmailTrackRecord } from "@/api/email/trackRecord";

export default {
  name: "EmailTrackRecord",
  dicts: ['email_status'],
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
      // 邮件跟踪记录表格数据
      emailTrackRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示查看弹出层
      viewOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskId: null,
        messageId: null,
        subject: null,
        recipient: null,
        sender: null,
        status: null,
        accountId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        messageId: [
          { required: true, message: "邮件Message-ID不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询邮件跟踪记录列表 */
    getList() {
      this.loading = true;
      listEmailTrackRecord(this.queryParams).then(response => {
        this.emailTrackRecordList = response.rows;
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
        id: null,
        taskId: null,
        messageId: null,
        subject: null,
        recipient: null,
        sender: null,
        content: null,
        status: null,
        sentTime: null,
        deliveredTime: null,
        openedTime: null,
        repliedTime: null,
        clickedTime: null,
        retryCount: 0,
        errorLogs: null,
        accountId: null,
        trackingPixelUrl: null,
        trackingLinkUrl: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加邮件跟踪记录";
    },
    /** 查看按钮操作 */
    handleView(row) {
      this.reset();
      const id = row.id || this.ids
      getEmailTrackRecord(id).then(response => {
        this.form = response.data;
        this.viewOpen = true;
        this.title = "查看邮件跟踪记录";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getEmailTrackRecord(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改邮件跟踪记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateEmailTrackRecord(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addEmailTrackRecord(this.form).then(response => {
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
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除邮件跟踪记录编号为"' + ids + '"的数据项？').then(function() {
        return delEmailTrackRecord(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('email/track-record/export', {
        ...this.queryParams
      }, `email_track_record_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

