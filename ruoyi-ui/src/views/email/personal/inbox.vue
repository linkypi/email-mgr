<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>收件箱</span>
        <el-button style="float: right" type="primary" size="small" @click="handleCompose">
          写邮件
        </el-button>
      </div>
      
      <el-table :data="emailList" style="width: 100%">
        <el-table-column prop="fromAddress" label="发件人" width="200"></el-table-column>
        <el-table-column prop="subject" label="主题" min-width="300"></el-table-column>
        <el-table-column prop="receiveTime" label="时间" width="180"></el-table-column>
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
  name: 'EmailInbox',
  data() {
    return {
      emailList: [
        {
          emailId: 1,
          fromAddress: 'sender@example.com',
          subject: '重要会议通知',
          receiveTime: '2024-01-15 10:30:00'
        },
        {
          emailId: 2,
          fromAddress: 'hr@company.com',
          subject: '员工福利更新',
          receiveTime: '2024-01-15 09:15:00'
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
    }
  }
}
</script>
