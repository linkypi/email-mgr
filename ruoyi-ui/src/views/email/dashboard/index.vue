<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <div class="stats-item">
            <div class="stats-number">{{ todayStats.totalSent }}</div>
            <div class="stats-label">今日发件数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stats-item">
            <div class="stats-number">{{ totalStats.totalSent }}</div>
            <div class="stats-label">总发件数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stats-item">
            <div class="stats-number">{{ totalStats.totalRecipients }}</div>
            <div class="stats-label">总收件人</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stats-item">
            <div class="stats-number">{{ totalStats.avgReplyRate }}%</div>
            <div class="stats-label">平均回复率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <div slot="header">快捷操作</div>
          <el-button type="primary" @click="$router.push('/email/personal')">写邮件</el-button>
          <el-button type="warning" @click="$router.push('/email/group')">群组管理</el-button>
          <el-button type="info" @click="$router.push('/email/tag')">标签管理</el-button>
          <el-button type="danger" @click="$router.push('/email/batch')">批量发送</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'EmailDashboard',
  data() {
    return {
      todayStats: { totalSent: 156 },
      totalStats: {
        totalSent: 12580,
        totalRecipients: 3420,
        avgReplyRate: 23.5
      }
    }
  }
}
</script>

<style scoped>
.stats-item {
  text-align: center;
  padding: 20px;
}

.stats-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.stats-label {
  margin-top: 10px;
  color: #909399;
}
</style>
