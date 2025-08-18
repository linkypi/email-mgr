<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>写邮件</span>
      </div>
      
      <el-form :model="emailForm" label-width="80px">
        <el-form-item label="收件人">
          <el-input v-model="emailForm.toAddress" placeholder="请输入收件人邮箱"></el-input>
        </el-form-item>
        
        <el-form-item label="抄送">
          <el-input v-model="emailForm.ccAddress" placeholder="请输入抄送邮箱（可选）"></el-input>
        </el-form-item>
        
        <el-form-item label="主题">
          <el-input v-model="emailForm.subject" placeholder="请输入邮件主题"></el-input>
        </el-form-item>
        
        <el-form-item label="内容">
          <el-input type="textarea" v-model="emailForm.content" :rows="10" placeholder="请输入邮件内容"></el-input>
        </el-form-item>
        
        <el-form-item label="附件">
          <el-upload
            action="#"
            :auto-upload="false"
            :file-list="fileList"
            :on-change="handleFileChange">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png/pdf文件，且不超过10MB</div>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSend">发送</el-button>
          <el-button @click="handleSaveDraft">保存草稿</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'EmailCompose',
  data() {
    return {
      emailForm: {
        toAddress: '',
        ccAddress: '',
        subject: '',
        content: ''
      },
      fileList: []
    }
  },
  mounted() {
    // 检查是否是回复或转发
    const { type, emailId } = this.$route.query
    if (type === 'reply') {
      this.handleReply(emailId)
    } else if (type === 'forward') {
      this.handleForward(emailId)
    }
  },
  methods: {
    handleFileChange(file) {
      this.fileList.push(file)
    },
    handleSend() {
      if (!this.emailForm.toAddress || !this.emailForm.subject || !this.emailForm.content) {
        this.$message.warning('请填写完整信息')
        return
      }
      
      this.$confirm('确认发送邮件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('邮件发送成功')
        this.$router.push('/email/personal/inbox')
      })
    },
    handleSaveDraft() {
      this.$message.success('草稿已保存')
    },
    handleCancel() {
      this.$router.go(-1)
    },
    handleReply(emailId) {
      // 模拟获取原邮件信息
      this.emailForm.subject = '回复：重要会议通知'
      this.emailForm.content = '\n\n--- 原邮件内容 ---\n请参加明天下午2点的项目会议...'
    },
    handleForward(emailId) {
      // 模拟获取原邮件信息
      this.emailForm.subject = '转发：重要会议通知'
      this.emailForm.content = '\n\n--- 转发邮件 ---\n请参加明天下午2点的项目会议...'
    }
  }
}
</script>

