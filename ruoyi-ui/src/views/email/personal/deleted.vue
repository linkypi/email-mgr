<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>已删除邮件</span>
        <el-button style="float: right" type="danger" size="small" @click="handleClearAll">
          清空回收站
        </el-button>
      </div>
      
      <el-table :data="emailList" style="width: 100%">
        <el-table-column prop="fromAddress" label="发件人" width="200"></el-table-column>
        <el-table-column prop="subject" label="主题" min-width="300"></el-table-column>
        <el-table-column prop="deleteTime" label="删除时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleRestore(scope.row)">恢复</el-button>
            <el-button size="mini" @click="handleView(scope.row)">查看</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">彻底删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'EmailDeleted',
  data() {
    return {
      emailList: [
        {
          emailId: 1,
          fromAddress: 'spam@example.com',
          subject: '垃圾邮件',
          deleteTime: '2024-01-15 10:30:00'
        },
        {
          emailId: 2,
          fromAddress: 'old@company.com',
          subject: '过期通知',
          deleteTime: '2024-01-14 16:20:00'
        }
      ]
    }
  },
  methods: {
    handleRestore(row) {
      this.$confirm('确认恢复这封邮件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('恢复成功')
      })
    },
    handleView(row) {
      this.$message.info('查看邮件: ' + row.subject)
    },
    handleDelete(row) {
      this.$confirm('确认彻底删除这封邮件吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        this.$message.success('彻底删除成功')
      })
    },
    handleClearAll() {
      this.$confirm('确认清空回收站吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }).then(() => {
        this.$message.success('回收站已清空')
      })
    }
  }
}
</script>

