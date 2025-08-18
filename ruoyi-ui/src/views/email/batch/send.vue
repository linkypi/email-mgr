<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>批量发送</span>
      </div>
      
      <el-form :model="sendForm" label-width="120px">
        <el-form-item label="任务名称">
          <el-input v-model="sendForm.taskName" placeholder="请输入任务名称"></el-input>
        </el-form-item>
        
        <el-form-item label="邮件主题">
          <el-input v-model="sendForm.subject" placeholder="请输入邮件主题"></el-input>
        </el-form-item>
        
        <el-form-item label="邮件内容">
          <el-input type="textarea" v-model="sendForm.content" :rows="6" placeholder="请输入邮件内容"></el-input>
        </el-form-item>
        
        <el-form-item label="发件邮箱">
          <el-select v-model="sendForm.accountId" placeholder="选择发件邮箱">
            <el-option label="marketing@company.com" value="1"></el-option>
            <el-option label="support@company.com" value="2"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="收件人">
          <el-select v-model="sendForm.recipients" multiple placeholder="选择收件人">
            <el-option label="VIP客户群组" value="1"></el-option>
            <el-option label="普通客户群组" value="2"></el-option>
            <el-option label="潜在客户群组" value="3"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSend">开始发送</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'EmailSend',
  data() {
    return {
      sendForm: {
        taskName: '',
        subject: '',
        content: '',
        accountId: '',
        recipients: []
      }
    }
  },
  methods: {
    handleSend() {
      if (!this.sendForm.taskName || !this.sendForm.subject || !this.sendForm.content) {
        this.$message.warning('请填写完整信息')
        return
      }
      
      this.$confirm('确认开始批量发送吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('发送任务已创建')
        this.$router.push('/email/batch')
      })
    },
    handleReset() {
      this.sendForm = {
        taskName: '',
        subject: '',
        content: '',
        accountId: '',
        recipients: []
      }
    }
  }
}
</script>
