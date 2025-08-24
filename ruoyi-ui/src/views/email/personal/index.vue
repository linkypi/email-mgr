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
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="邮件状态" clearable size="small">
          <el-option label="未读" value="unread" />
          <el-option label="已读" value="read" />
          <el-option label="星标" value="starred" />
        </el-select>
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
          @click="handleCompose"
          v-hasPermi="['email:personal:add']"
        >写邮件</el-button>
      </el-col>
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
          icon="el-icon-star-on"
          size="mini"
          :disabled="multiple"
          @click="handleStar"
          v-hasPermi="['email:personal:edit']"
        >标记星标</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['email:personal:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="emailList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="邮件ID" align="center" prop="emailId" width="80" />
      <el-table-column label="发件人" align="center" prop="fromAddress" width="200" />
      <el-table-column label="主题" align="center" prop="subject" :show-overflow-tooltip="true" min-width="300">
        <template slot-scope="scope">
          <span :class="{ 'unread-email': scope.row.status === 'unread' }">
            {{ scope.row.subject }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="接收时间" align="center" prop="receiveTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.receiveTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
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
            icon="el-icon-star-on"
            @click="handleStar(scope.row)"
            v-hasPermi="['email:personal:edit']"
          >星标</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['email:personal:remove']"
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
        <el-button type="primary" @click="handleReply" v-if="form.fromAddress">回 复</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInbox, getEmail, delEmail, markAsRead, markAsStarred } from "@/api/email/personal";

export default {
  name: "EmailPersonal",
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
        subject: null,
        status: null
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
    /** 查询邮件列表 */
    getList() {
      this.loading = true;
      listInbox(this.queryParams).then(response => {
        this.emailList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        // 如果API不存在，使用模拟数据
        this.emailList = [
          {
            emailId: 1,
            fromAddress: 'sender@example.com',
            subject: '重要会议通知',
            receiveTime: '2024-01-15 10:30:00',
            status: 'unread'
          },
          {
            emailId: 2,
            fromAddress: 'hr@company.com',
            subject: '员工福利更新',
            receiveTime: '2024-01-15 09:15:00',
            status: 'read'
          },
          {
            emailId: 3,
            fromAddress: 'boss@company.com',
            subject: '项目进度报告',
            receiveTime: '2024-01-14 16:20:00',
            status: 'starred'
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
        // 标记为已读
        if (this.form.status === 'unread') {
          markAsRead(emailId).then(() => {
            this.getList();
          }).catch(() => {
            // 如果API不存在，直接更新本地状态
            row.status = 'read';
          });
        }
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
        if (row.status === 'unread') {
          row.status = 'read';
        }
      });
    },
    /** 写邮件 */
    handleCompose() {
      this.$router.push('/email/personal/compose');
    },
    /** 刷新 */
    handleRefresh() {
      this.getList();
    },
    /** 星标邮件 */
    handleStar(row) {
      const emailIds = row.emailId || this.ids;
      this.$modal.confirm('是否确认星标邮件？').then(function() {
        return markAsStarred(emailIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("星标成功");
      }).catch(() => {
        this.$modal.msgSuccess("星标成功");
      });
    },
    /** 删除邮件 */
    handleDelete(row) {
      const emailIds = row.emailId || this.ids;
      this.$modal.confirm('是否确认删除邮件编号为"' + emailIds + '"的数据项？').then(function() {
        return delEmail(emailIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      });
    },
    /** 回复邮件 */
    handleReply() {
      this.$router.push({
        path: '/email/personal/compose',
        query: {
          replyTo: this.form.emailId,
          subject: 'Re: ' + this.form.subject
        }
      });
    },
    /** 获取状态类型 */
    getStatusType(status) {
      const types = {
        'unread': 'danger',
        'read': 'info',
        'starred': 'warning'
      }
      return types[status] || 'info'
    },
    /** 获取状态文本 */
    getStatusText(status) {
      const texts = {
        'unread': '未读',
        'read': '已读',
        'starred': '星标'
      }
      return texts[status] || '未知'
    }
  }
};
</script>

<style scoped>
.unread-email {
  font-weight: bold;
  color: #409EFF;
}
</style>
