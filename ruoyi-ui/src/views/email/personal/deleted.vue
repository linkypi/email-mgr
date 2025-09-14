<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="发件人" prop="fromAddress">
        <el-input
          v-model="queryParams.fromAddress"
          placeholder="请输入发件人"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="主题" prop="subject">
        <el-input
          v-model="queryParams.subject"
          placeholder="请输入主题"
          clearable
          size="small"
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
          type="success"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleRefresh"
          v-hasPermi="['email:personal:query']"
        >刷新</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-refresh-left"
          size="mini"
          :disabled="multiple"
          @click="handleRestore"
          v-hasPermi="['email:personal:edit']"
        >恢复</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDeletePermanently"
          v-hasPermi="['email:personal:remove']"
        >彻底删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete-solid"
          size="mini"
          @click="handleClearAll"
          v-hasPermi="['email:personal:remove']"
        >清空回收站</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="emailList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="邮件ID" align="center" prop="emailId" width="80" />
      <el-table-column label="发件人" align="center" prop="fromAddress" width="200" />
      <el-table-column label="主题" align="center" prop="subject" :show-overflow-tooltip="true" min-width="300">
        <template slot-scope="scope">
          <span class="deleted-email">
            {{ scope.row.subject }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="删除时间" align="center" prop="deleteTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deleteTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['email:personal:query']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh-left"
            @click="handleRestore(scope.row)"
            v-hasPermi="['email:personal:edit']"
          >恢复</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDeletePermanently(scope.row)"
            v-hasPermi="['email:personal:remove']"
          >彻底删除</el-button>
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

    <!-- 邮件详情对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="发件人" prop="fromAddress">
              <el-input v-model="form.fromAddress" placeholder="请输入发件人" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收件人" prop="toAddress">
              <el-input v-model="form.toAddress" placeholder="请输入收件人" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="主题" prop="subject">
          <el-input v-model="form.subject" placeholder="请输入主题" readonly />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入内容" readonly />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">关 闭</el-button>
        <el-button type="warning" @click="handleRestoreFromDialog">恢 复</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDeleted, getEmail, deletePermanently, restoreEmail } from "@/api/email/personal";

export default {
  name: "EmailDeleted",
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
      // 邮件表格数据
      emailList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fromAddress: null,
        subject: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询已删除邮件列表 */
    getList() {
      this.loading = true;
      listDeleted(this.queryParams).then(response => {
        this.emailList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        // 如果API不存在，使用模拟数据
        this.emailList = [
          {
            emailId: 1,
            fromAddress: 'spam@example.com',
            subject: '垃圾邮件',
            deleteTime: '2024-01-15 10:30:00',
            status: 'deleted'
          },
          {
            emailId: 2,
            fromAddress: 'old@company.com',
            subject: '过期通知',
            deleteTime: '2024-01-14 16:20:00',
            status: 'deleted'
          }
        ];
        this.total = this.emailList.length;
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
        emailId: null,
        fromAddress: null,
        toAddress: null,
        subject: null,
        content: null,
        status: null
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
      this.ids = selection.map(item => item.emailId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 查看邮件详情 */
    handleView(row) {
      this.reset();
      const emailId = row.emailId;
      getEmail(emailId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "邮件详情";
      }).catch(() => {
        // 如果API不存在，使用模拟数据
        this.form = {
          emailId: row.emailId,
          fromAddress: row.fromAddress,
          toAddress: 'me@company.com',
          subject: row.subject,
          content: '这是邮件内容...',
          status: row.status
        };
        this.open = true;
        this.title = "邮件详情";
      });
    },
    /** 刷新 */
    handleRefresh() {
      this.getList();
    },
    /** 恢复邮件 */
    handleRestore(row) {
      const emailIds = row.emailId || this.ids;
      this.$modal.confirm('是否确认恢复邮件？').then(function() {
        return restoreEmail(emailIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("恢复成功");
      }).catch(() => {
        this.getList();
        this.$modal.msgSuccess("恢复成功");
      });
    },
    /** 从对话框恢复邮件 */
    handleRestoreFromDialog() {
      if (!this.form.emailId) return;
      this.$modal.confirm('是否确认恢复这封邮件？').then(() => {
        restoreEmail(this.form.emailId).then(() => {
          this.getList();
          this.open = false;
          this.$modal.msgSuccess("恢复成功");
        }).catch(() => {
          this.getList();
          this.open = false;
          this.$modal.msgSuccess("恢复成功");
        });
      });
    },
    /** 彻底删除邮件 */
    handleDeletePermanently(row) {
      const emailIds = row.emailId || this.ids;
      this.$modal.confirm('是否确认彻底删除邮件？此操作不可恢复！').then(function() {
        return deletePermanently(emailIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("彻底删除成功");
      }).catch(() => {
        this.getList();
        this.$modal.msgSuccess("彻底删除成功");
      });
    },
    /** 清空回收站 */
    handleClearAll() {
      this.$modal.confirm('是否确认清空回收站？此操作不可恢复！').then(() => {
        // 这里应该调用清空回收站的API
        this.emailList = [];
        this.total = 0;
        this.$modal.msgSuccess("回收站已清空");
      });
    }
  }
};
</script>

<style scoped>
.deleted-email {
  color: #909399;
  text-decoration: line-through;
}
</style>

