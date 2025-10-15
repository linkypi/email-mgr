<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="任务名称" prop="taskName">
        <el-input
          v-model="queryParams.taskName"
          placeholder="请输入任务名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择任务状态" clearable>
          <el-option label="未开始" value="0" />
          <el-option label="执行中" value="1" />
          <el-option label="已完成" value="2" />
          <el-option label="执行失败" value="3" />
          <el-option label="执行中断" value="4" />
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
          @click="handleAdd"
          v-hasPermi="['email:task:add']"
        >新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="taskTable" v-loading="loading" :data="taskList" @selection-change="handleSelectionChange" @sort-change="handleSortChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="任务ID" align="center" prop="taskId" width="80" />
      <el-table-column label="任务名称" align="center" prop="taskName" />
      <el-table-column label="邮件主题" align="center" prop="subject" show-overflow-tooltip />
      <el-table-column label="任务状态" align="center" prop="status" width="120" sortable="custom">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总数量" align="center" prop="totalCount" width="100" sortable="custom" />
      <el-table-column label="已发送" align="center" prop="sentCount" width="100" sortable="custom" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" sortable="custom" :sort-orders="['descending', 'ascending']">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleRestart(scope.row)"
            v-hasPermi="['email:task:restart']"
            v-if="scope.row.status !== '1'"
          >重新执行</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-copy-document"
            @click="handleCopy(scope.row)"
            v-hasPermi="['email:task:copy']"
          >复制任务</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-video-pause"
            @click="handleStop(scope.row)"
            v-hasPermi="['email:task:stop']"
            v-if="scope.row.status === '1'"
          >停止任务</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="handleExecution(scope.row)"
            v-hasPermi="['email:task:execution']"
          >执行记录</el-button>
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

    <!-- 执行记录对话框 -->
    <el-dialog
      title="任务执行记录"
      :visible.sync="executionDialogVisible"
      width="80%"
      :close-on-click-modal="false"
    >
      <div v-loading="executionLoading">
        <!-- 统计信息卡片 -->
        <el-row :gutter="20" style="margin-bottom: 20px;" v-if="executionStatistics && Object.keys(executionStatistics).length > 0">
          <el-col :span="6">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>执行次数</span>
              </div>
              <div class="text item">
                <div style="font-size: 24px; font-weight: bold; color: #409EFF;">
                  {{ executionStatistics.totalExecutions || 0 }}
                </div>
                <div style="font-size: 12px; color: #909399;">
                  总执行次数
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>成功次数</span>
              </div>
              <div class="text item">
                <div style="font-size: 24px; font-weight: bold; color: #67C23A;">
                  {{ executionStatistics.completedCount || 0 }}
                </div>
                <div style="font-size: 12px; color: #909399;">
                  已完成执行
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>失败次数</span>
              </div>
              <div class="text item">
                <div style="font-size: 24px; font-weight: bold; color: #F56C6C;">
                  {{ executionStatistics.failedCount || 0 }}
                </div>
                <div style="font-size: 12px; color: #909399;">
                  执行失败
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>成功率</span>
              </div>
              <div class="text item">
                <div style="font-size: 24px; font-weight: bold; color: #E6A23C;">
                  {{ executionStatistics.successRate || 0 }}%
                </div>
                <div style="font-size: 12px; color: #909399;">
                  发送成功率
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 执行记录表格 -->
        <el-table :data="executionList" style="width: 100%">
          <el-table-column prop="executionId" label="执行ID" width="80" />
          <el-table-column prop="executionStatus" label="执行状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getExecutionStatusType(scope.row.executionStatus)">
                {{ getExecutionStatusText(scope.row.executionStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="结束时间" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="executionUser" label="执行人" width="120" />
          <el-table-column prop="totalCount" label="总数" width="80" />
          <el-table-column prop="sentCount" label="已发送" width="80" />
          <el-table-column prop="successCount" label="成功" width="80" />
          <el-table-column prop="failedCount" label="失败" width="80" />
          <el-table-column prop="errorMessage" label="错误信息" show-overflow-tooltip />
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-document"
                @click="viewExecutionLog(scope.row)"
              >查看日志</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="executionDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="refreshExecutions">刷新</el-button>
      </div>
    </el-dialog>

    <!-- 执行日志对话框 -->
    <el-dialog
      title="执行日志"
      :visible.sync="logDialogVisible"
      width="60%"
      :close-on-click-modal="false"
    >
      <div class="execution-log">
        <pre>{{ currentExecutionLog }}</pre>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="logDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTask, restartTask, copyTask, stopTask, getTaskExecutions } from "@/api/email/task";

export default {
  name: "TaskManagement",
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
      // 任务列表
      taskList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskName: null,
        status: null,
        orderByColumn: 'createTime',
        isAsc: 'desc'
      },
      // 执行记录相关
      executionDialogVisible: false,
      executionLoading: false,
      executionList: [],
      executionStatistics: {},
      currentTaskId: null,
      // 执行日志相关
      logDialogVisible: false,
      currentExecutionLog: ''
    };
  },
  created() {
    this.getList();
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
    /** 排序变化处理 */
    handleSortChange(column) {
      if (column.prop && column.order) {
        this.queryParams.orderByColumn = column.prop;
        this.queryParams.isAsc = column.order === 'ascending' ? 'asc' : 'desc';
      } else {
        this.queryParams.orderByColumn = 'createTime';
        this.queryParams.isAsc = 'desc';
      }
      this.getList();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      // 重置排序参数为默认值
      this.queryParams.orderByColumn = 'createTime';
      this.queryParams.isAsc = 'desc';
      // 清除表格排序状态
      this.$nextTick(() => {
        this.$refs.taskTable.clearSort();
      });
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.taskId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push('/email/batch/send');
    },
    /** 重新执行任务 */
    handleRestart(row) {
      const taskId = row.taskId;
      this.$modal.confirm('是否确认重新执行任务 "' + row.taskName + '"？').then(function() {
        return restartTask(taskId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("重新执行成功");
      }).catch(() => {});
    },
    /** 复制任务 */
    handleCopy(row) {
      this.$router.push({
        path: '/email/batch/send',
        query: { copyTaskId: row.taskId }
      });
    },
    /** 停止任务 */
    handleStop(row) {
      const taskId = row.taskId;
      this.$modal.confirm('是否确认停止任务 "' + row.taskName + '"？').then(function() {
        return stopTask(taskId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("停止成功");
      }).catch(() => {});
    },
    /** 查看执行记录 */
    handleExecution(row) {
      this.currentTaskId = row.taskId;
      this.executionDialogVisible = true;
      this.loadExecutions();
    },
    /** 加载执行记录 */
    loadExecutions() {
      if (!this.currentTaskId) return;
      
      this.executionLoading = true;
      getTaskExecutions(this.currentTaskId).then(response => {
        // 处理新的返回数据结构
        if (response.data && response.data.executions) {
          this.executionList = response.data.executions || [];
          this.executionStatistics = response.data.statistics || {};
        } else {
          // 兼容旧的返回格式
          this.executionList = response.data || [];
          this.executionStatistics = {};
        }
        this.executionLoading = false;
      }).catch(error => {
        this.$message.error('加载执行记录失败：' + error.message);
        this.executionLoading = false;
      });
    },
    /** 刷新执行记录 */
    refreshExecutions() {
      this.loadExecutions();
    },
    /** 查看执行日志 */
    viewExecutionLog(row) {
      let logContent = '';
      
      // 基本信息
      logContent += `执行ID: ${row.executionId}\n`;
      logContent += `任务ID: ${row.taskId}\n`;
      logContent += `执行状态: ${this.getExecutionStatusText(row.executionStatus)}\n`;
      logContent += `开始时间: ${this.parseTime(row.startTime, '{y}-{m}-{d} {h}:{i}:{s}')}\n`;
      logContent += `结束时间: ${row.endTime ? this.parseTime(row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') : '未完成'}\n`;
      logContent += `执行人: ${row.executionUser}\n`;
      logContent += `执行IP: ${row.executionIp}\n`;
      logContent += `总数: ${row.totalCount || 0}\n`;
      logContent += `已发送: ${row.sentCount || 0}\n`;
      logContent += `成功: ${row.successCount || 0}\n`;
      logContent += `失败: ${row.failedCount || 0}\n`;
      logContent += `\n`;
      
      // 错误信息
      if (row.errorMessage) {
        logContent += `=== 错误信息 ===\n`;
        logContent += `${row.errorMessage}\n\n`;
      }
      
      // 执行日志
      if (row.executionLog) {
        logContent += `=== 执行日志 ===\n`;
        logContent += `${row.executionLog}\n`;
      } else {
        logContent += `=== 执行日志 ===\n`;
        logContent += `暂无详细日志信息\n`;
      }
      
      this.currentExecutionLog = logContent;
      this.logDialogVisible = true;
    },
    /** 获取状态类型 */
    getStatusType(status) {
      switch (status) {
        case '0':
          return 'info';
        case '1':
          return 'warning';
        case '2':
          return 'success';
        case '3':
          return 'danger';
        case '4':
          return 'danger';
        default:
          return 'info';
      }
    },
    /** 获取状态文本 */
    getStatusText(status) {
      switch (status) {
        case '0':
          return '未开始';
        case '1':
          return '执行中';
        case '2':
          return '已完成';
        case '3':
          return '执行失败';
        case '4':
          return '执行中断';
        default:
          return '未知';
      }
    },
    /** 获取执行状态类型 */
    getExecutionStatusType(status) {
      switch (status) {
        case '0':
          return 'info';
        case '1':
          return 'warning';
        case '2':
          return 'success';
        case '3':
          return 'danger';
        case '4':
          return 'danger';
        default:
          return 'info';
      }
    },
    /** 获取执行状态文本 */
    getExecutionStatusText(status) {
      switch (status) {
        case '0':
          return '未开始';
        case '1':
          return '执行中';
        case '2':
          return '已完成';
        case '3':
          return '执行失败';
        case '4':
          return '执行中断';
        default:
          return '未知';
      }
    }
  }
};
</script>

<style scoped>
/* 确保表格列标题和排序按钮在同一行显示 */
.el-table .el-table__header-wrapper .el-table__header th {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 调整排序按钮的显示 */
.el-table .el-table__header-wrapper .el-table__header th .cell {
  display: flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
}

/* 确保列标题文字和排序图标在同一行 */
.el-table .el-table__header-wrapper .el-table__header th .cell .caret-wrapper {
  margin-left: 4px;
}

/* 执行日志样式 */
.execution-log {
  max-height: 400px;
  overflow-y: auto;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 10px;
}

.execution-log pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.4;
  color: #333;
}

/* 对话框样式优化 */
.el-dialog__body {
  padding: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>
