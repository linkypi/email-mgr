<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="邮箱地址" prop="emailAddress">
        <el-input
          v-model="queryParams.emailAddress"
          placeholder="请输入邮箱地址"
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
          @click="handleDiagnoseAll"
        >诊断所有账户</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleRefresh"
        >刷新状态</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-question"
          size="mini"
          @click="handleShowConfigSuggestions"
        >配置建议</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账号ID" align="center" prop="accountId" width="80" />
      <el-table-column label="邮箱地址" align="center" prop="emailAddress" :show-overflow-tooltip="true" />
      <el-table-column label="IMAP服务器" align="center" prop="imapHost" :show-overflow-tooltip="true" />
      <el-table-column label="IMAP端口" align="center" prop="imapPort" width="100" />
      <el-table-column label="连接状态" align="center" width="120">
        <template slot-scope="scope">
          <el-tag :type="getConnectionStatusType(scope.row.connectionStatus)">
            {{ scope.row.connectionStatus || '未测试' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="监控状态" align="center" width="120">
        <template slot-scope="scope">
          <el-tag :type="getMonitoringStatusType(scope.row.monitoringStatus)">
            {{ scope.row.monitoringStatus || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="连接时间" align="center" prop="connectionTime" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.connectionTime">{{ scope.row.connectionTime }}ms</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="错误信息" align="center" prop="message" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-connection"
            @click="handleTestConnection(scope.row)"
          >测试连接</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="handleManualScan(scope.row)"
          >手动扫描</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 配置建议对话框 -->
    <el-dialog
      title="邮箱配置建议"
      :visible.sync="configDialogVisible"
      width="60%"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeConfigTab">
        <el-tab-pane label="QQ邮箱" name="qq">
          <div class="config-suggestion">
            <h4>QQ邮箱IMAP配置</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="IMAP服务器">imap.qq.com</el-descriptions-item>
              <el-descriptions-item label="IMAP端口">993</el-descriptions-item>
              <el-descriptions-item label="SSL">启用</el-descriptions-item>
              <el-descriptions-item label="用户名">邮箱地址</el-descriptions-item>
              <el-descriptions-item label="密码">授权码（不是登录密码）</el-descriptions-item>
            </el-descriptions>
            <h4>设置步骤：</h4>
            <ol>
              <li>登录QQ邮箱网页版</li>
              <li>进入设置 -> 账户</li>
              <li>开启IMAP/SMTP服务</li>
              <li>生成授权码</li>
              <li>使用授权码作为IMAP密码</li>
            </ol>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Gmail" name="gmail">
          <div class="config-suggestion">
            <h4>Gmail IMAP配置</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="IMAP服务器">imap.gmail.com</el-descriptions-item>
              <el-descriptions-item label="IMAP端口">993</el-descriptions-item>
              <el-descriptions-item label="SSL">启用</el-descriptions-item>
              <el-descriptions-item label="用户名">邮箱地址</el-descriptions-item>
              <el-descriptions-item label="密码">应用专用密码</el-descriptions-item>
            </el-descriptions>
            <h4>设置步骤：</h4>
            <ol>
              <li>登录Google账户</li>
              <li>进入安全设置</li>
              <li>开启两步验证</li>
              <li>生成应用专用密码</li>
              <li>使用应用专用密码作为IMAP密码</li>
            </ol>
          </div>
        </el-tab-pane>
        <el-tab-pane label="163邮箱" name="163">
          <div class="config-suggestion">
            <h4>163邮箱IMAP配置</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="IMAP服务器">imap.163.com</el-descriptions-item>
              <el-descriptions-item label="IMAP端口">993</el-descriptions-item>
              <el-descriptions-item label="SSL">启用</el-descriptions-item>
              <el-descriptions-item label="用户名">邮箱地址</el-descriptions-item>
              <el-descriptions-item label="密码">授权码</el-descriptions-item>
            </el-descriptions>
            <h4>设置步骤：</h4>
            <ol>
              <li>登录163邮箱网页版</li>
              <li>进入设置 -> POP3/SMTP/IMAP</li>
              <li>开启IMAP服务</li>
              <li>生成授权码</li>
              <li>使用授权码作为IMAP密码</li>
            </ol>
          </div>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button @click="configDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 诊断结果对话框 -->
    <el-dialog
      title="诊断结果"
      :visible.sync="diagnosticDialogVisible"
      width="70%"
      :close-on-click-modal="false"
    >
      <el-table :data="diagnosticResults" style="width: 100%">
        <el-table-column prop="emailAddress" label="邮箱地址" width="200" />
        <el-table-column prop="connectionStatus" label="连接状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="getConnectionStatusType(scope.row.connectionStatus)">
              {{ scope.row.connectionStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="monitoringStatus" label="监控状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="getMonitoringStatusType(scope.row.monitoringStatus)">
              {{ scope.row.monitoringStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="详细信息" :show-overflow-tooltip="true" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="diagnosticDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAccount } from "@/api/email/account";
import { diagnoseImapStatus, testImapConnection, manualReplyScan } from "@/api/email/diagnostic";

export default {
  name: "EmailDiagnostic",
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
      // 邮箱账户表格数据
      accountList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        emailAddress: null,
      },
      // 配置建议对话框
      configDialogVisible: false,
      activeConfigTab: 'qq',
      // 诊断结果对话框
      diagnosticDialogVisible: false,
      diagnosticResults: []
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询邮箱账户列表 */
    getList() {
      this.loading = true;
      listAccount(this.queryParams).then(response => {
        this.accountList = response.rows;
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
        accountId: null,
        senderId: null,
        accountName: null,
        emailAddress: null,
        password: null,
        smtpHost: null,
        smtpPort: null,
        smtpSsl: "1",
        imapHost: null,
        imapPort: null,
        imapSsl: "1",
        imapUsername: null,
        imapPassword: null,
        webhookUrl: null,
        webhookSecret: null,
        trackingEnabled: "1",
        dailyLimit: 100,
        sendIntervalSeconds: 30,
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
      this.ids = selection.map(item => item.accountId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 诊断所有账户 */
    handleDiagnoseAll() {
      this.loading = true;
      diagnoseImapStatus().then(response => {
        this.diagnosticResults = Object.values(response.data);
        this.diagnosticDialogVisible = true;
        this.loading = false;
        this.$modal.msgSuccess("诊断完成");
      }).catch(() => {
        this.loading = false;
      });
    },
    /** 刷新状态 */
    handleRefresh() {
      this.getList();
      this.$modal.msgSuccess("状态已刷新");
    },
    /** 显示配置建议 */
    handleShowConfigSuggestions() {
      this.configDialogVisible = true;
    },
    /** 测试连接 */
    handleTestConnection(row) {
      this.loading = true;
      testImapConnection(row.accountId).then(response => {
        this.loading = false;
        if (response.data.success) {
          this.$modal.msgSuccess("连接测试成功");
        } else {
          this.$modal.msgError("连接测试失败: " + response.data.message);
        }
        this.getList(); // 刷新列表
      }).catch(() => {
        this.loading = false;
      });
    },
    /** 手动扫描回复 */
    handleManualScan(row) {
      this.$modal.confirm('是否手动扫描该账户的回复邮件？').then(() => {
        this.loading = true;
        manualReplyScan(row.accountId).then(response => {
          this.loading = false;
          this.$modal.msgSuccess("手动扫描已触发");
        }).catch(() => {
          this.loading = false;
        });
      }).catch(() => {});
    },
    /** 获取连接状态类型 */
    getConnectionStatusType(status) {
      switch (status) {
        case '成功':
          return 'success';
        case '失败':
        case '异常':
          return 'danger';
        default:
          return 'info';
      }
    },
    /** 获取监控状态类型 */
    getMonitoringStatusType(status) {
      switch (status) {
        case '监控中':
          return 'success';
        case '未监控':
          return 'warning';
        default:
          return 'info';
      }
    }
  }
};
</script>

<style scoped>
.config-suggestion {
  padding: 20px;
}

.config-suggestion h4 {
  margin-bottom: 15px;
  color: #303133;
}

.config-suggestion ol {
  margin-top: 15px;
  padding-left: 20px;
}

.config-suggestion li {
  margin-bottom: 8px;
  line-height: 1.6;
}
</style>
