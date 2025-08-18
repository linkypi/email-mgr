<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-item">
            <div class="stats-icon" style="background: #409EFF;">
              <i class="el-icon-message"></i>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ todayStats.totalSent }}</div>
              <div class="stats-label">今日发送</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-item">
            <div class="stats-icon" style="background: #67C23A;">
              <i class="el-icon-check"></i>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ totalStats.totalSent }}</div>
              <div class="stats-label">总发送数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-item">
            <div class="stats-icon" style="background: #E6A23C;">
              <i class="el-icon-view"></i>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ totalStats.avgOpenRate }}%</div>
              <div class="stats-label">平均打开率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-item">
            <div class="stats-icon" style="background: #F56C6C;">
              <i class="el-icon-chat-dot-round"></i>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ totalStats.avgReplyRate }}%</div>
              <div class="stats-label">平均回复率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>发送趋势</span>
          </div>
          <div class="chart-container">
            <div ref="sendTrendChart" style="height: 300px;"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>回复率统计</span>
          </div>
          <div class="chart-container">
            <div ref="replyRateChart" style="height: 300px;"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细统计表格 -->
    <el-card>
      <div slot="header">
        <span>详细统计数据</span>
        <el-button style="float: right" type="primary" size="small" @click="handleExport">
          导出统计
        </el-button>
      </div>
      
      <!-- 筛选条件 -->
      <el-form :inline="true" :model="queryParams" class="mb20">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryParams.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="邮箱账号">
          <el-select v-model="queryParams.accountId" placeholder="请选择邮箱账号" clearable>
            <el-option label="marketing@company.com" value="1"></el-option>
            <el-option label="support@company.com" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计表格 -->
      <el-table :data="statisticsList" style="width: 100%">
        <el-table-column prop="date" label="日期" width="120"></el-table-column>
        <el-table-column prop="accountName" label="邮箱账号" width="180"></el-table-column>
        <el-table-column prop="totalSent" label="发送数" width="100"></el-table-column>
        <el-table-column prop="delivered" label="送达数" width="100"></el-table-column>
        <el-table-column prop="opened" label="打开数" width="100"></el-table-column>
        <el-table-column prop="replied" label="回复数" width="100"></el-table-column>
        <el-table-column prop="deliveryRate" label="送达率" width="100">
          <template slot-scope="scope">
            {{ scope.row.deliveryRate }}%
          </template>
        </el-table-column>
        <el-table-column prop="openRate" label="打开率" width="100">
          <template slot-scope="scope">
            {{ scope.row.openRate }}%
          </template>
        </el-table-column>
        <el-table-column prop="replyRate" label="回复率" width="100">
          <template slot-scope="scope">
            {{ scope.row.replyRate }}%
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'EmailStatistics',
  data() {
    return {
      queryParams: {
        dateRange: [],
        accountId: ''
      },
      todayStats: {
        totalSent: 156
      },
      totalStats: {
        totalSent: 12580,
        avgOpenRate: 45.2,
        avgReplyRate: 23.5
      },
      statisticsList: [
        {
          date: '2024-01-15',
          accountName: 'marketing@company.com',
          totalSent: 156,
          delivered: 148,
          opened: 67,
          replied: 23,
          deliveryRate: 94.9,
          openRate: 43.0,
          replyRate: 14.7
        },
        {
          date: '2024-01-14',
          accountName: 'marketing@company.com',
          totalSent: 142,
          delivered: 135,
          opened: 58,
          replied: 19,
          deliveryRate: 95.1,
          openRate: 40.8,
          replyRate: 13.4
        },
        {
          date: '2024-01-13',
          accountName: 'support@company.com',
          totalSent: 89,
          delivered: 85,
          opened: 42,
          replied: 15,
          deliveryRate: 95.5,
          openRate: 47.2,
          replyRate: 16.9
        }
      ]
    }
  },
  mounted() {
    this.initCharts()
  },
  methods: {
    handleSearch() {
      this.$message.info('查询功能待实现')
    },
    handleReset() {
      this.queryParams = {
        dateRange: [],
        accountId: ''
      }
    },
    handleExport() {
      this.$message.success('导出功能待实现')
    },
    initCharts() {
      // 发送趋势图表
      const sendTrendChart = this.$echarts.init(this.$refs.sendTrendChart)
      const sendTrendOption = {
        title: {
          text: '近7天发送趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: ['1-09', '1-10', '1-11', '1-12', '1-13', '1-14', '1-15']
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: [120, 132, 101, 134, 90, 142, 156],
          type: 'line',
          smooth: true,
          itemStyle: {
            color: '#409EFF'
          }
        }]
      }
      sendTrendChart.setOption(sendTrendOption)

      // 回复率统计图表
      const replyRateChart = this.$echarts.init(this.$refs.replyRateChart)
      const replyRateOption = {
        title: {
          text: '各账号回复率对比',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [{
          type: 'pie',
          radius: '50%',
          data: [
            { value: 14.7, name: 'marketing@company.com' },
            { value: 16.9, name: 'support@company.com' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }
      replyRateChart.setOption(replyRateOption)
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stats-item {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 24px;
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stats-label {
  font-size: 14px;
  color: #606266;
}

.chart-container {
  padding: 10px;
}
</style>
