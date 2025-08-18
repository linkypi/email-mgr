<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>发件箱</span>
        <el-button style="float: right" type="primary" size="small" @click="handleCompose">
          写邮件
        </el-button>
      </div>
      
      <el-table :data="emailList" style="width: 100%">
        <el-table-column prop="toAddress" label="收件人" width="200"></el-table-column>
        <el-table-column prop="subject" label="主题" min-width="300"></el-table-column>
        <el-table-column prop="sendTime" label="发送时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'EmailSent',
  data() {
    return {
      emailList: [
        {
          emailId: 1,
          toAddress: 'recipient@example.com',
          subject: '项目进度报告',
          sendTime: '2024-01-15 14:30:00',
          status: 'sent'
        },
        {
          emailId: 2,
          toAddress: 'team@company.com',
          subject: '会议安排通知',
          sendTime: '2024-01-15 11:20:00',
          status: 'delivered'
        }
      ]
    }
  },
  methods: {
    handleCompose() {
      this.$router.push('/email/personal/compose')
    },
    handleView(row) {
      this.$message.info('查看邮件: ' + row.subject)
    },
    handleDelete(row) {
      this.$confirm('确认删除这封邮件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('删除成功')
      })
    },
    getStatusType(status) {
      const types = {
        'sent': 'info',
        'delivered': 'success',
        'opened': 'warning',
        'replied': 'primary'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        'sent': '已发送',
        'delivered': '已送达',
        'opened': '已打开',
        'replied': '已回复'
      }
      return texts[status] || '未知'
    }
  }
}
</script>

