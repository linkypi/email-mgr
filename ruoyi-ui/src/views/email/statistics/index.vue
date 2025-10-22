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
            <div style="float: right;">
              <el-radio-group v-model="trendTimeRange" @change="handleTrendTimeRangeChange" size="mini">
                <el-radio-button label="7">近一周</el-radio-button>
                <el-radio-button label="30">近一个月</el-radio-button>
                <el-radio-button label="180">近半年</el-radio-button>
                <el-radio-button label="365">近一年</el-radio-button>
              </el-radio-group>
            </div>
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
    <el-card v-if="!isGuestUser">
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
            <el-option 
              v-for="account in accounts" 
              :key="account.value" 
              :label="account.label" 
              :value="account.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计表格 -->
      <el-table :data="statisticsList" style="width: 100%" v-loading="loading">
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
import { getTodayStats, getTotalStats, getSendTrends, getReplyRates, getDetailedStats, getAccounts, exportStatistics } from '@/api/email/statistics'
import { isGuestUser } from '@/utils/guestUserCheck'

export default {
  name: 'EmailStatistics',
  data() {
    return {
      loading: false,
      queryParams: {
        dateRange: [],
        accountId: ''
      },
      todayStats: {
        totalSent: 0,
        totalReceived: 0,
        totalReplied: 0
      },
      totalStats: {
        totalSent: 0,
        totalContacts: 0,
        activeAccounts: 0,
        avgOpenRate: 0,
        avgReplyRate: 0
      },
      statisticsList: [],
      accounts: [],
      trendsData: {
        dateLabels: [],
        sendData: [],
        receivedData: []
      },
      replyRatesData: [],
      trendTimeRange: '7' // 默认近一周
    }
  },
  mounted() {
    this.loadData()
  },
  computed: {
    // 检查是否为访客用户
    isGuestUser() {
      const roles = this.$store.getters.roles || [];
      console.log("roles", roles)
      return isGuestUser(roles);
    }
  },
  methods: {
    // 加载所有数据
    async loadData() {
      this.loading = true
      try {
        console.log('开始加载数据，isGuestUser:', this.isGuestUser);
        
        // 根据用户角色决定加载哪些数据
        let apiCalls = [
          getTodayStats(),
          getTotalStats(),
          getSendTrends(parseInt(this.trendTimeRange)),
          getReplyRates()
        ];
        
        // 访客用户不请求 accounts 和 detailed 接口
        if (!this.isGuestUser) {
          console.log('非访客用户，添加 accounts 和 detailed 接口');
          apiCalls.push(getAccounts());
          apiCalls.push(getDetailedStats());
        } else {
          console.log('访客用户，跳过 accounts 和 detailed 接口');
        }
        
        // 并行加载所有数据
        const results = await Promise.allSettled(apiCalls);
        const [todayRes, totalRes, trendsRes, replyRatesRes, accountsRes, detailedRes] = results;

        // 处理今日统计数据
        if (todayRes.status === 'fulfilled' && todayRes.value.code === 200) {
          const todayData = todayRes.value.data
          this.todayStats = {
            totalSent: todayData.sent || 0,
            totalReceived: todayData.delivered || 0,
            totalReplied: 0 // 今日回复数需要单独计算
          }
        }

        // 处理总体统计数据
        if (totalRes.status === 'fulfilled' && totalRes.value.code === 200) {
          const totalData = totalRes.value.data
          this.totalStats = {
            totalSent: totalData.emails || 0,
            totalContacts: 0, // 需要单独获取
            activeAccounts: 0, // 需要单独获取
            avgOpenRate: totalData.emails > 0 ? Math.round((totalData.opened / totalData.emails) * 100 * 10) / 10 : 0,
            avgReplyRate: totalData.emails > 0 ? Math.round((totalData.replied / totalData.emails) * 100 * 10) / 10 : 0
          }
        }

        // 处理趋势数据
        if (trendsRes.status === 'fulfilled' && trendsRes.value.code === 200) {
          this.trendsData = trendsRes.value.data
        }

        // 处理回复率数据
        if (replyRatesRes.status === 'fulfilled' && replyRatesRes.value.code === 200) {
          this.replyRatesData = replyRatesRes.value.data.accountReplyRates || []
        }

        // 处理账号列表（仅非访客用户）
        if (!this.isGuestUser && accountsRes && accountsRes.status === 'fulfilled' && accountsRes.value.code === 200) {
          this.accounts = accountsRes.value.data
        }

        // 处理详细统计数据（仅非访客用户）
        if (!this.isGuestUser && detailedRes && detailedRes.status === 'fulfilled' && detailedRes.value.code === 200) {
          this.statisticsList = detailedRes.value.data.statisticsList || []
        }

        // 初始化图表
        this.$nextTick(() => {
          this.initCharts()
        })

      } catch (error) {
        console.error('加载统计数据失败:', error)
        this.$message.error('加载统计数据失败')
      } finally {
        this.loading = false
      }
    },

    // 搜索功能
    async handleSearch() {
      this.loading = true
      try {
        const params = {}
        if (this.queryParams.dateRange && this.queryParams.dateRange.length === 2) {
          params.startDate = this.queryParams.dateRange[0]
          params.endDate = this.queryParams.dateRange[1]
        }
        if (this.queryParams.accountId) {
          params.accountId = this.queryParams.accountId
        }

        const response = await getDetailedStats(params)
        if (response.code === 200) {
          this.statisticsList = response.data.statisticsList || []
          // 移除查询成功的提示，因为这是正常的查询操作
        } else {
          this.$message.error('查询失败')
        }
      } catch (error) {
        console.error('查询失败:', error)
        this.$message.error('查询失败')
      } finally {
        this.loading = false
      }
    },

    // 重置功能
    handleReset() {
      this.queryParams = {
        dateRange: [],
        accountId: ''
      }
      this.loadData()
    },

    // 导出功能
    async handleExport() {
      try {
        this.loading = true
        
        // 准备导出参数
        const params = {}
        
        // 如果有筛选条件，添加到参数中
        if (this.queryParams.dateRange && this.queryParams.dateRange.length === 2) {
          params.startDate = this.queryParams.dateRange[0]
          params.endDate = this.queryParams.dateRange[1]
        }
        if (this.queryParams.accountId) {
          params.accountId = this.queryParams.accountId
        }
        
        // 调用导出API
        const response = await exportStatistics(params)
        
        // 创建下载链接
        const blob = new Blob([response], { 
          type: 'application/vnd.ms-excel' 
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        
        // 生成文件名
        const now = new Date()
        const dateStr = now.getFullYear() + 
                       String(now.getMonth() + 1).padStart(2, '0') + 
                       String(now.getDate()).padStart(2, '0')
        link.download = `邮件详细统计_${dateStr}.csv`
        
        // 触发下载
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        this.$message.success('导出成功')
        
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败: ' + (error.message || '未知错误'))
      } finally {
        this.loading = false
      }
    },

    // 处理时间范围变更
    async handleTrendTimeRangeChange() {
      try {
        this.loading = true
        const response = await getSendTrends(parseInt(this.trendTimeRange))
        if (response.code === 200) {
          this.trendsData = response.data
          // 重新初始化发送趋势图表
          this.$nextTick(() => {
            this.initSendTrendChart()
          })
        } else {
          this.$message.error('获取趋势数据失败')
        }
      } catch (error) {
        console.error('获取趋势数据失败:', error)
        this.$message.error('获取趋势数据失败')
      } finally {
        this.loading = false
      }
    },

    // 获取时间范围文本
    getTimeRangeText(days) {
      const dayMap = {
        '7': '近7天发送趋势',
        '30': '近30天发送趋势',
        '180': '近半年发送趋势',
        '365': '近一年发送趋势'
      }
      return dayMap[days] || '发送趋势'
    },

    // 获取X轴标签显示间隔
    getXAxisInterval() {
      const days = parseInt(this.trendTimeRange)
      if (days <= 7) return 0 // 显示所有标签
      if (days <= 30) return 1 // 每隔一个显示
      if (days <= 180) return 4 // 每隔4个显示
      return 9 // 每隔9个显示
    },

    // 获取X轴标签旋转角度
    getXAxisRotate() {
      const days = parseInt(this.trendTimeRange)
      if (days <= 7) return 0 // 不旋转
      if (days <= 30) return 0 // 不旋转
      return 45 // 旋转45度
    },

    // 初始化图表
    initCharts() {
      this.initSendTrendChart()
      this.initReplyRateChart()
    },


    // 初始化发送趋势图表
    initSendTrendChart() {
      if (!this.$refs.sendTrendChart) {
        console.warn('发送趋势图表容器不存在')
        return
      }
      
      // 尝试多种方式获取ECharts
      let echartsInstance = null
      if (this.$echarts) {
        echartsInstance = this.$echarts
      } else if (window.echarts) {
        echartsInstance = window.echarts
      } else {
        console.error('无法找到ECharts实例')
        return
      }
      
      try {
        const sendTrendChart = echartsInstance.init(this.$refs.sendTrendChart)
      const timeRangeText = this.getTimeRangeText(this.trendTimeRange)
      const sendTrendOption = {
        title: {
          text: timeRangeText,
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['发送', '接收'],
          top: 30
        },
        xAxis: {
          type: 'category',
          data: this.trendsData.dateLabels || [],
          axisLabel: {
            interval: this.getXAxisInterval(),
            rotate: this.getXAxisRotate()
          }
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '发送',
            data: this.trendsData.sendData || [],
            type: 'line',
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '接收',
            data: this.trendsData.receivedData || [],
            type: 'line',
            smooth: true,
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      }
      sendTrendChart.setOption(sendTrendOption)
      
      // 强制重绘
      setTimeout(() => {
        sendTrendChart.resize()
      }, 100)
      
      } catch (error) {
        console.error('发送趋势图表初始化失败:', error)
      }
    },

    // 初始化回复率图表
    initReplyRateChart() {
      if (!this.$refs.replyRateChart) {
        console.warn('回复率图表容器不存在')
        return
      }
      
      // 尝试多种方式获取ECharts
      let echartsInstance = null
      if (this.$echarts) {
        echartsInstance = this.$echarts
      } else if (window.echarts) {
        echartsInstance = window.echarts
      } else {
        console.error('无法找到ECharts实例')
        return
      }
      
      try {
        const replyRateChart = echartsInstance.init(this.$refs.replyRateChart)
      const replyRateOption = {
        title: {
          text: '各账号回复率对比',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c}% ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: this.replyRatesData.map(item => item.name)
        },
        series: [{
          name: '回复率',
          type: 'pie',
          radius: '50%',
          data: this.replyRatesData,
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
      
      // 强制重绘
      setTimeout(() => {
        replyRateChart.resize()
      }, 100)
      
      } catch (error) {
        console.error('回复率图表初始化失败:', error)
      }
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
