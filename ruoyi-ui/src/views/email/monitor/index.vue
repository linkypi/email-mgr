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
        <el-button type="text" icon="el-icon-arrow-down" size="mini" @click="toggleAdvancedSearch">
          {{ showAdvancedSearch ? '收起' : '展开' }}
        </el-button>
      </el-form-item>
      
             <!-- 高级搜索条件 -->
       <div v-show="showAdvancedSearch" style="margin-top: 10px;" class="advanced-search">
         <el-form-item label="IMAP状态" prop="imapStatus" label-width="80px">
           <el-select v-model="queryParams.imapStatus" placeholder="请选择IMAP状态" clearable style="width: 200px;">
             <el-option
               v-for="dict in dict.type.email_service_status"
               :key="dict.value"
               :label="dict.label"
               :value="dict.value"
             />
           </el-select>
         </el-form-item>
         <el-form-item label="SMTP状态" prop="smtpStatus" label-width="80px">
           <el-select v-model="queryParams.smtpStatus" placeholder="请选择SMTP状态" clearable style="width: 200px;">
             <el-option
               v-for="dict in dict.type.email_service_status"
               :key="dict.value"
               :label="dict.label"
               :value="dict.value"
             />
           </el-select>
         </el-form-item>
         <el-form-item label="监控状态" prop="monitorStatus" label-width="80px">
           <el-select v-model="queryParams.monitorStatus" placeholder="请选择监控状态" clearable style="width: 200px;">
             <el-option
               v-for="dict in dict.type.monitor_status"
               :key="dict.value"
               :label="dict.label"
               :value="dict.value"
             />
           </el-select>
         </el-form-item>
       </div>
    </el-form>

    <!-- 系统状态概览 -->
    <el-card class="mb8" shadow="never">
      <div slot="header" class="clearfix">
        <span><i class="el-icon-data-analysis"></i> 系统状态概览</span>
      </div>
             <el-row :gutter="20">
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">监听服务状态:</span>
               <span class="status-value" :style="{color: listenerStatus.running ? '#67C23A' : '#F56C6C'}">
                 {{ listenerStatus.running ? '运行中' : '已停止' }}
               </span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">活跃连接数:</span>
               <span class="status-value" style="color: #409EFF;">{{ getActiveConnectionCount() }}</span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">监控账号数:</span>
               <span class="status-value">{{ monitorList.length }}</span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">运行中账号:</span>
               <span class="status-value" style="color: #67C23A;">{{ getRunningAccountCount() }}</span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">连接中账号:</span>
               <span class="status-value" style="color: #E6A23C;">{{ getConnectingAccountCount() }}</span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">已连接账号:</span>
               <span class="status-value" style="color: #409EFF;">{{ getConnectedAccountCount() }}</span>
             </div>
           </div>
         </el-col>
       </el-row>
             <el-row :gutter="20" style="margin-top: 15px;">
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">网络超时:</span>
               <span class="status-value" style="color: #F56C6C;">{{ getTimeoutAccountCount() }}</span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">认证失败:</span>
               <span class="status-value" style="color: #F56C6C;">{{ getAuthFailedAccountCount() }}</span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">SSL错误:</span>
               <span class="status-value" style="color: #F56C6C;">{{ getSslErrorAccountCount() }}</span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">端口错误:</span>
               <span class="status-value" style="color: #F56C6C;">{{ getPortErrorAccountCount() }}</span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">主机不可达:</span>
               <span class="status-value" style="color: #F56C6C;">{{ getHostUnreachableAccountCount() }}</span>
             </div>
           </div>
         </el-col>
         <el-col :span="4">
           <div class="status-item">
             <div class="status-content">
               <span class="status-label">其他异常:</span>
               <span class="status-value" style="color: #F56C6C;">{{ getOtherErrorAccountCount() }}</span>
             </div>
           </div>
         </el-col>
       </el-row>
    </el-card>

    <!-- 全局服务控制区域 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="24">
        <el-divider content-position="left">
          <span style="font-weight: bold; color: #409EFF;">
            <i class="el-icon-setting"></i> 全局服务控制
          </span>
        </el-divider>
      </el-col>
    </el-row>
    
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-video-play"
          size="mini"
          @click="handleStartListener"
          :disabled="listenerStatus.running"
          v-hasPermi="['email:monitor:listener:start']"
        >启动全局监听</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-video-pause"
          size="mini"
          @click="handleStopListener"
          :disabled="!listenerStatus.running"
          v-hasPermi="['email:monitor:listener:stop']"
        >停止全局监听</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleRestartListener"
          v-hasPermi="['email:monitor:listener:restart']"
        >重启全局监听</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-video-play"
          size="mini"
          @click="handleStartGlobalMonitor"
          v-hasPermi="['email:monitor:start']"
        >启动全局监控</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-video-pause"
          size="mini"
          @click="handleStopGlobalMonitor"
          v-hasPermi="['email:monitor:stop']"
        >停止全局监控</el-button>
      </el-col>
           </el-row>
 
     <!-- 列表操作区域 -->
     <el-row :gutter="10" class="mb8">
       <el-col :span="24">
         <el-divider content-position="left">
           <span style="font-weight: bold; color: #67C23A;">
             <i class="el-icon-list"></i> 列表操作控制
           </span>
         </el-divider>
       </el-col>
     </el-row>
     
           <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-video-play"
            size="mini"
            @click="handleBatchStartMonitor"
            :disabled="ids.length === 0"
            v-hasPermi="['email:monitor:start']"
          >批量启动监控</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="el-icon-video-pause"
            size="mini"
            @click="handleBatchStopMonitor"
            :disabled="ids.length === 0"
            v-hasPermi="['email:monitor:stop']"
          >批量停止监控</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-refresh"
            size="mini"
            @click="handleBatchRestartMonitor"
            :disabled="ids.length === 0"
            v-hasPermi="['email:monitor:restart']"
          >批量重启监控</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-refresh"
            size="mini"
            @click="handleRefresh"
          >刷新数据</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            :type="isAutoRefresh ? 'success' : 'info'"
            plain
            :icon="isAutoRefresh ? 'el-icon-video-play' : 'el-icon-video-pause'"
            size="mini"
            @click="toggleAutoRefresh"
          >{{ isAutoRefresh ? '自动刷新开' : '自动刷新关' }}</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['email:monitor:export']"
          >导出数据</el-button>
        </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="monitorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="邮箱地址" align="center" prop="emailAddress" />
             <el-table-column label="IMAP状态" align="center" prop="imapStatus">
         <template slot-scope="scope">
           <el-tooltip :content="getStatusDescription(scope.row.imapStatus)" placement="top">
             <el-tag :type="getStatusType(scope.row.imapStatus)">
               {{ getStatusText(scope.row.imapStatus) }}
             </el-tag>
           </el-tooltip>
         </template>
       </el-table-column>
             <el-table-column label="SMTP状态" align="center" prop="smtpStatus">
         <template slot-scope="scope">
           <el-tooltip :content="getStatusDescription(scope.row.smtpStatus)" placement="top">
             <el-tag :type="getStatusType(scope.row.smtpStatus)">
               {{ getStatusText(scope.row.smtpStatus) }}
             </el-tag>
           </el-tooltip>
         </template>
       </el-table-column>
      <el-table-column label="监控状态" align="center" prop="monitorStatus">
        <template slot-scope="scope">
          <el-tag :type="scope.row.monitorStatus === '1' ? 'success' : 'info'">
            {{ scope.row.monitorStatus === '1' ? '运行中' : '停止' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最后监控时间" align="center" prop="lastMonitorTime" width="180">
        <template slot-scope="scope">
          <span v-if="scope.row.lastMonitorTime">{{ parseTime(scope.row.lastMonitorTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="handleViewLogs(scope.row)"
            v-hasPermi="['email:monitor:log']"
          >查看监控记录</el-button>
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

    <!-- 监控记录对话框 -->
    <el-dialog title="监控记录" :visible.sync="logDialogVisible" width="1000px" append-to-body>
      <el-table :data="logList" v-loading="logLoading">
        <el-table-column label="服务类型" align="center" prop="serviceType">
          <template slot-scope="scope">
            <el-tag :type="scope.row.serviceType === 'IMAP' ? 'primary' : 'success'">
              {{ scope.row.serviceType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'success' ? 'success' : 'danger'">
              {{ scope.row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态信息" align="center" prop="message" :show-overflow-tooltip="true" />
        <el-table-column label="响应时间" align="center" prop="responseTime" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.responseTime">{{ scope.row.responseTime }}ms</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="检查时间" align="center" prop="checkTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.checkTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="logTotal>0"
        :total="logTotal"
        :page.sync="logQueryParams.pageNum"
        :limit.sync="logQueryParams.pageSize"
        @pagination="getLogList"
      />
    </el-dialog>

    <!-- 最近异常对话框 -->
    <el-dialog title="最近异常信息" :visible.sync="recentErrorDialogVisible" width="600px" append-to-body>
      <div v-if="recentErrorData">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="邮箱地址">
            {{ recentErrorData.emailAddress }}
          </el-descriptions-item>
          <el-descriptions-item label="服务类型">
            <el-tag :type="recentErrorData.serviceType === 'IMAP' ? 'primary' : 'success'">
              {{ recentErrorData.serviceType }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(recentErrorData.status)">
              {{ getStatusText(recentErrorData.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后检查时间">
            <span v-if="recentErrorData.lastCheckTime">{{ parseTime(recentErrorData.lastCheckTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="异常信息" v-if="recentErrorData.errorMessage">
            <div style="color: #F56C6C; word-break: break-all;">{{ recentErrorData.errorMessage }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="异常时间" v-if="recentErrorData.errorTime">
            <span style="color: #F56C6C;">{{ parseTime(recentErrorData.errorTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMonitor, startGlobalMonitor, stopGlobalMonitor, startAccountMonitor, stopAccountMonitor, restartAccountMonitor, getAccountMonitorLogs, exportMonitor, getEmailListenerStatus, startEmailListener, stopEmailListener, restartEmailListener } from "@/api/email/monitor";

export default {
  name: "EmailServiceMonitor",
  dicts: ['email_service_status', 'monitor_status'],
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
      // 邮件服务监控表格数据
      monitorList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        emailAddress: null,
        imapStatus: null,
        smtpStatus: null,
        monitorStatus: null
      },
      // 监控记录相关
      logDialogVisible: false,
      logLoading: false,
      logList: [],
      logTotal: 0,
      logQueryParams: {
        pageNum: 1,
        pageSize: 10,
        accountId: null
      },
      // 监听服务状态
      listenerStatus: {
        running: false,
        activeConnections: 0
      },
      // 自动刷新相关
      autoRefreshTimer: null,
      autoRefreshInterval: 10000, // 10秒自动刷新
      isAutoRefresh: true,
      // 高级搜索
      showAdvancedSearch: false,
      // 最近异常对话框
      recentErrorDialogVisible: false,
      recentErrorData: null
    };
  },
  created() {
    this.getList();
    this.getListenerStatus();
    this.startAutoRefresh();
  },
  beforeDestroy() {
    this.stopAutoRefresh();
  },
  methods: {
    /** 查询邮件服务监控列表 */
    getList() {
      this.loading = true;
      listMonitor(this.queryParams).then(response => {
        this.monitorList = response.rows;
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
        accountId: null,
        emailAddress: null,
        imapStatus: null,
        imapLastCheckTime: null,
        imapErrorMessage: null,
        imapErrorTime: null,
        smtpStatus: null,
        smtpLastCheckTime: null,
        smtpErrorMessage: null,
        smtpErrorTime: null,
        monitorStatus: null,
        lastMonitorTime: null
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
    /** 切换高级搜索 */
    toggleAdvancedSearch() {
      this.showAdvancedSearch = !this.showAdvancedSearch;
    },
         /** 获取运行中的账号数量 */
     getRunningAccountCount() {
       return this.monitorList.filter(item => {
         // 检查IMAP或SMTP状态是否为运行中状态（1=运行中，2=连接中，3=已连接）
         const imapRunning = item.imapStatus && ['1','2','3'].includes(item.imapStatus);
         const smtpRunning = item.smtpStatus && ['1','2','3'].includes(item.smtpStatus);
         return imapRunning || smtpRunning;
       }).length;
     },
     /** 获取活跃连接数（已连接的账号数量） */
     getActiveConnectionCount() {
       return this.monitorList.filter(item => {
         const imapConnected = item.imapStatus === '3';
         const smtpConnected = item.smtpStatus === '3';
         return imapConnected || smtpConnected;
       }).length;
     },
     /** 获取连接中的账号数量 */
     getConnectingAccountCount() {
       return this.monitorList.filter(item => {
         const imapConnecting = item.imapStatus === '2';
         const smtpConnecting = item.smtpStatus === '2';
         return imapConnecting || smtpConnecting;
       }).length;
     },
     /** 获取已连接的账号数量 */
     getConnectedAccountCount() {
       return this.monitorList.filter(item => {
         const imapConnected = item.imapStatus === '3';
         const smtpConnected = item.smtpStatus === '3';
         return imapConnected || smtpConnected;
       }).length;
     },
     /** 获取网络超时的账号数量 */
     getTimeoutAccountCount() {
       return this.monitorList.filter(item => {
         const imapTimeout = item.imapStatus === '4';
         const smtpTimeout = item.smtpStatus === '4';
         return imapTimeout || smtpTimeout;
       }).length;
     },
     /** 获取认证失败的账号数量 */
     getAuthFailedAccountCount() {
       return this.monitorList.filter(item => {
         const imapAuthFailed = item.imapStatus === '5';
         const smtpAuthFailed = item.smtpStatus === '5';
         return imapAuthFailed || smtpAuthFailed;
       }).length;
     },
     /** 获取SSL错误的账号数量 */
     getSslErrorAccountCount() {
       return this.monitorList.filter(item => {
         const imapSslError = item.imapStatus === '6';
         const smtpSslError = item.smtpStatus === '6';
         return imapSslError || smtpSslError;
       }).length;
     },
     /** 获取端口错误的账号数量 */
     getPortErrorAccountCount() {
       return this.monitorList.filter(item => {
         const imapPortError = item.imapStatus === '7';
         const smtpPortError = item.smtpStatus === '7';
         return imapPortError || smtpPortError;
       }).length;
     },
     /** 获取主机不可达的账号数量 */
     getHostUnreachableAccountCount() {
       return this.monitorList.filter(item => {
         const imapHostUnreachable = item.imapStatus === '8';
         const smtpHostUnreachable = item.smtpStatus === '8';
         return imapHostUnreachable || smtpHostUnreachable;
       }).length;
     },
     /** 获取其他异常的账号数量 */
     getOtherErrorAccountCount() {
       return this.monitorList.filter(item => {
         const imapOtherError = item.imapStatus && ['9','10'].includes(item.imapStatus);
         const smtpOtherError = item.smtpStatus && ['9','10'].includes(item.smtpStatus);
         return imapOtherError || smtpOtherError;
       }).length;
     },
     /** 获取异常账号数量（所有错误状态） */
     getErrorAccountCount() {
       return this.monitorList.filter(item => {
         // 检查IMAP或SMTP状态是否为错误状态（4-10）
         const imapError = item.imapStatus && ['4','5','6','7','8','9','10'].includes(item.imapStatus);
         const smtpError = item.smtpStatus && ['4','5','6','7','8','9','10'].includes(item.smtpStatus);
         return imapError || smtpError;
       }).length;
     },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 启动全局监控按钮操作 */
    handleStartGlobalMonitor() {
      this.$modal.confirm('是否确认启动全局监控？').then(function() {
        return startGlobalMonitor();
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("启动成功");
      }).catch(() => {});
    },
    /** 停止全局监控按钮操作 */
    handleStopGlobalMonitor() {
      this.$modal.confirm('是否确认停止全局监控？').then(function() {
        return stopGlobalMonitor();
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("停止成功");
      }).catch(() => {});
    },


    /** 查看记录按钮操作 */
    handleViewLogs(row) {
      this.logDialogVisible = true;
      this.logQueryParams.accountId = row.accountId;
      // 重置分页参数
      this.logQueryParams.pageNum = 1;
      this.logQueryParams.pageSize = 10;
      this.getLogList();
    },
    /** 获取监控记录列表 */
    getLogList() {
      this.logLoading = true;
      const params = {
        pageNum: this.logQueryParams.pageNum,
        pageSize: this.logQueryParams.pageSize
      };
      getAccountMonitorLogs(this.logQueryParams.accountId, params).then(response => {
        this.logList = response.rows;
        this.logTotal = response.total;
        this.logLoading = false;
      });
    },
    /** 刷新按钮操作 */
    handleRefresh() {
      this.getList();
      this.getListenerStatus();
    },
    /** 切换自动刷新 */
    toggleAutoRefresh() {
      this.isAutoRefresh = !this.isAutoRefresh;
      if (this.isAutoRefresh) {
        this.startAutoRefresh();
        this.$message.success('自动刷新已开启');
      } else {
        this.stopAutoRefresh();
        this.$message.info('自动刷新已关闭');
      }
    },
    /** 启动自动刷新 */
    startAutoRefresh() {
      if (this.autoRefreshTimer) {
        clearInterval(this.autoRefreshTimer);
      }
      this.autoRefreshTimer = setInterval(() => {
        if (this.isAutoRefresh) {
          this.getList();
          this.getListenerStatus();
        }
      }, this.autoRefreshInterval);
    },
    /** 停止自动刷新 */
    stopAutoRefresh() {
      if (this.autoRefreshTimer) {
        clearInterval(this.autoRefreshTimer);
        this.autoRefreshTimer = null;
      }
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('email/monitor/export', {
        ...this.queryParams
      }, `邮件服务监控_${new Date().getTime()}.xlsx`)
    },
    
    /** 批量启动监控 */
    handleBatchStartMonitor() {
      this.$modal.confirm(`是否确认启动选中的 ${this.ids.length} 个账号的监控？`).then(() => {
        const promises = this.ids.map(id => {
          const row = this.monitorList.find(item => item.id === id);
          return startAccountMonitor(row.accountId);
        });
        return Promise.all(promises);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(`成功启动 ${this.ids.length} 个账号的监控`);
      }).catch(() => {});
    },
    
    /** 批量停止监控 */
    handleBatchStopMonitor() {
      this.$modal.confirm(`是否确认停止选中的 ${this.ids.length} 个账号的监控？`).then(() => {
        const promises = this.ids.map(id => {
          const row = this.monitorList.find(item => item.id === id);
          return stopAccountMonitor(row.accountId);
        });
        return Promise.all(promises);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(`成功停止 ${this.ids.length} 个账号的监控`);
      }).catch(() => {});
    },
    
    /** 批量重启监控 */
    handleBatchRestartMonitor() {
      this.$modal.confirm(`是否确认重启选中的 ${this.ids.length} 个账号的监控？`).then(() => {
        const promises = this.ids.map(id => {
          const row = this.monitorList.find(item => item.id === id);
          return restartAccountMonitor(row.accountId);
        });
        return Promise.all(promises);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(`成功重启 ${this.ids.length} 个账号的监控`);
      }).catch(() => {});
    },
    
    /** 批量测试连接 */
    handleBatchTestConnection() {
      this.$modal.confirm(`是否确认测试选中的 ${this.ids.length} 个账号的连接？`).then(() => {
        const promises = this.ids.map(id => {
          const row = this.monitorList.find(item => item.id === id);
          return Promise.all([
            testImapService(row.accountId),
            testSmtpService(row.accountId)
          ]);
        });
        return Promise.all(promises);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess(`成功测试 ${this.ids.length} 个账号的连接`);
      }).catch(() => {});
    },
    
         /** 批量查看记录 */
     handleBatchViewLogs() {
       if (this.ids.length > 1) {
         this.$message.info('批量查看记录功能暂不支持，请选择单个账号查看');
         return;
       }
       const row = this.monitorList.find(item => item.id === this.ids[0]);
       this.handleViewLogs(row);
     },
         // 获取状态类型
     getStatusType(status) {
       switch (status) {
         case '0': return 'info';      // 停止
         case '1': return 'success';   // 运行中
         case '2': return 'warning';   // 连接中
         case '3': return 'primary';   // 已连接
         case '4': return 'danger';    // 网络超时
         case '5': return 'danger';    // 认证失败
         case '6': return 'danger';    // SSL错误
         case '7': return 'danger';    // 端口错误
         case '8': return 'danger';    // 主机不可达
         case '9': return 'danger';    // 防火墙阻止
         case '10': return 'danger';   // 服务异常
         default: return 'info';
       }
     },
     // 获取状态文本
     getStatusText(status) {
       switch (status) {
         case '0': return '停止';
         case '1': return '运行中';
         case '2': return '连接中';
         case '3': return '已连接';
         case '4': return '网络超时';
         case '5': return '认证失败';
         case '6': return 'SSL错误';
         case '7': return '端口错误';
         case '8': return '主机不可达';
         case '9': return '防火墙阻止';
         case '10': return '服务异常';
         default: return '未知';
       }
     },
     // 获取状态描述
     getStatusDescription(status) {
       switch (status) {
         case '0': return '服务已停止';
         case '1': return '服务正常运行';
         case '2': return '正在建立网络连接';
         case '3': return '网络连接已建立';
         case '4': return '网络连接超时，请检查网络设置';
         case '5': return '用户名或密码认证失败，请检查账号配置';
         case '6': return 'SSL/TLS连接错误，请检查SSL配置';
         case '7': return '端口配置错误或端口被占用，请检查端口设置';
         case '8': return '邮件服务器主机不可达，请检查服务器地址';
         case '9': return '防火墙阻止连接，请检查防火墙设置';
         case '10': return '服务发生异常，请检查服务配置';
         default: return '未知状态';
       }
     },

    /** 获取监听服务状态 */
    getListenerStatus() {
      getEmailListenerStatus().then(response => {
        this.listenerStatus = response.data || { running: false, activeConnections: 0 };
      }).catch(() => {
        this.listenerStatus = { running: false, activeConnections: 0 };
      });
    },
    /** 启动监听服务按钮操作 */
    handleStartListener() {
      this.$modal.confirm('是否确认启动邮件监听服务？').then(function() {
        return startEmailListener();
      }).then(() => {
        this.getListenerStatus();
        this.$modal.msgSuccess("监听服务启动成功");
      }).catch(() => {});
    },
    /** 停止监听服务按钮操作 */
    handleStopListener() {
      this.$modal.confirm('是否确认停止邮件监听服务？').then(function() {
        return stopEmailListener();
      }).then(() => {
        this.getListenerStatus();
        this.$modal.msgSuccess("监听服务停止成功");
      }).catch(() => {});
    },
    /** 查看IMAP最近异常 */
    handleViewImapRecentError(row) {
      this.recentErrorData = {
        emailAddress: row.emailAddress,
        serviceType: 'IMAP',
        status: row.imapStatus,
        lastCheckTime: row.imapLastCheckTime,
        errorMessage: row.imapErrorMessage,
        errorTime: row.imapErrorTime
      };
      this.recentErrorDialogVisible = true;
    },
    /** 查看SMTP最近异常 */
    handleViewSmtpRecentError(row) {
      this.recentErrorData = {
        emailAddress: row.emailAddress,
        serviceType: 'SMTP',
        status: row.smtpStatus,
        lastCheckTime: row.smtpLastCheckTime,
        errorMessage: row.smtpErrorMessage,
        errorTime: row.smtpErrorTime
      };
      this.recentErrorDialogVisible = true;
    },
    /** 重启监听服务按钮操作 */
    handleRestartListener() {
      this.$modal.confirm('是否确认重启邮件监听服务？').then(function() {
        return restartEmailListener();
      }).then(() => {
        this.getListenerStatus();
        this.$modal.msgSuccess("监听服务重启成功");
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
 .status-item {
   text-align: center;
   padding: 10px;
   border: 1px solid #ebeef5;
   border-radius: 4px;
   background-color: #fafafa;
 }

 .status-content {
   display: flex;
   align-items: center;
   justify-content: center;
   gap: 8px;
 }

 .status-label {
   font-size: 12px;
   color: #909399;
   white-space: nowrap;
 }

 .status-value {
   font-size: 16px;
   font-weight: bold;
   color: #303133;
   white-space: nowrap;
 }

/* 防止搜索条件标签换行 */
.el-form-item__label {
  white-space: nowrap !important;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: keep-all;
}

/* 高级搜索条件样式 */
.advanced-search .el-form-item {
  margin-bottom: 10px;
}

.advanced-search .el-form-item__label {
  min-width: 80px;
  text-align: right;
  white-space: nowrap !important;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: keep-all;
}
</style>
