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
      <el-form-item label="监听状态" prop="listenerStatus">
        <el-select v-model="queryParams.listenerStatus" placeholder="请选择监听状态" clearable>
          <el-option label="运行中" value="running" />
          <el-option label="已停止" value="stopped" />
          <el-option label="错误" value="error" />
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
          icon="el-icon-video-play"
          size="mini"
          :disabled="multiple"
          @click="handleStartAll"
          v-hasPermi="['email:imap:start']"
        >启动全部</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-video-pause"
          size="mini"
          :disabled="multiple"
          @click="handleStopAll"
          v-hasPermi="['email:imap:stop']"
        >停止全部</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-refresh"
          size="mini"
          :disabled="multiple"
          @click="handleRestartAll"
          v-hasPermi="['email:imap:restart']"
        >重启全部</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-refresh-right"
          size="mini"
          :disabled="multiple"
          @click="handleSyncAll"
          v-hasPermi="['email:imap:sync']"
        >同步全部</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="imapList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="邮箱地址" align="center" prop="emailAddress" />
      <el-table-column label="IMAP服务器" align="center" prop="imapHost" />
      <el-table-column label="IMAP端口" align="center" prop="imapPort" />
      <el-table-column label="监听状态" align="center" prop="listenerStatus">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.listenerStatus)">
            {{ getStatusText(scope.row.listenerStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最后同步时间" align="center" prop="lastSyncTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastSyncTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="同步邮件数" align="center" prop="syncCount" />
      <el-table-column label="错误信息" align="center" prop="errorMessage" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-video-play"
            @click="handleStart(scope.row)"
            v-hasPermi="['email:imap:start']"
            v-if="scope.row.listenerStatus !== 'running'"
          >启动</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-video-pause"
            @click="handleStop(scope.row)"
            v-hasPermi="['email:imap:stop']"
            v-if="scope.row.listenerStatus === 'running'"
          >停止</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleRestart(scope.row)"
            v-hasPermi="['email:imap:restart']"
          >重启</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh-right"
            @click="handleSync(scope.row)"
            v-hasPermi="['email:imap:sync']"
          >同步</el-button>
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
</template>

<script>
import { listImapListeners, startImapListener, stopImapListener, restartImapListener, syncImapListener } from "@/api/email/imap";

export default {
  name: "ImapListener",
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
      // IMAP监听列表
      imapList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        emailAddress: null,
        listenerStatus: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询IMAP监听列表 */
    getList() {
      this.loading = true;
      listImapListeners(this.queryParams).then(response => {
        this.imapList = response.rows;
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
        email: null,
        imapHost: null,
        imapPort: null,
        listenerStatus: null
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
    /** 启动监听 */
    handleStart(row) {
      const accountId = row.accountId || this.ids
      this.$modal.confirm('是否确认启动邮箱 "' + row.emailAddress + '" 的IMAP监听？').then(function() {
        return startImapListener(accountId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("启动成功");
      }).catch(() => {});
    },
    /** 停止监听 */
    handleStop(row) {
      const accountId = row.accountId || this.ids
      this.$modal.confirm('是否确认停止邮箱 "' + row.emailAddress + '" 的IMAP监听？').then(function() {
        return stopImapListener(accountId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("停止成功");
      }).catch(() => {});
    },
    /** 重启监听 */
    handleRestart(row) {
      const accountId = row.accountId || this.ids
      this.$modal.confirm('是否确认重启邮箱 "' + row.emailAddress + '" 的IMAP监听？').then(function() {
        return restartImapListener(accountId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("重启成功");
      }).catch(() => {});
    },
    /** 同步邮件 */
    handleSync(row) {
      const accountId = row.accountId || this.ids
      this.$modal.confirm('是否确认同步邮箱 "' + row.emailAddress + '" 的邮件？').then(function() {
        return syncImapListener(accountId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("同步成功");
      }).catch(() => {});
    },
    /** 启动全部监听 */
    handleStartAll() {
      this.$modal.confirm('是否确认启动所有邮箱的IMAP监听？').then(function() {
        return startImapListener('all');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("启动成功");
      }).catch(() => {});
    },
    /** 停止全部监听 */
    handleStopAll() {
      this.$modal.confirm('是否确认停止所有邮箱的IMAP监听？').then(function() {
        return stopImapListener('all');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("停止成功");
      }).catch(() => {});
    },
    /** 重启全部监听 */
    handleRestartAll() {
      this.$modal.confirm('是否确认重启所有邮箱的IMAP监听？').then(function() {
        return restartImapListener('all');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("重启成功");
      }).catch(() => {});
    },
    /** 同步全部邮件 */
    handleSyncAll() {
      this.$modal.confirm('是否确认同步所有邮箱的邮件？').then(function() {
        return syncImapListener('all');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("同步成功");
      }).catch(() => {});
    },
    /** 获取状态类型 */
    getStatusType(status) {
      switch (status) {
        case 'running':
          return 'success';
        case 'stopped':
          return 'info';
        case 'error':
          return 'danger';
        default:
          return 'info';
      }
    },
    /** 获取状态文本 */
    getStatusText(status) {
      switch (status) {
        case 'running':
          return '运行中';
        case 'stopped':
          return '已停止';
        case 'error':
          return '错误';
        default:
          return '未知';
      }
    }
  }
};
</script>
