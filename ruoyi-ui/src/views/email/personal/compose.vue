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
import { sendEmail, getEmail } from "@/api/email/personal";
import request from '@/utils/request';

export default {
  name: 'EmailCompose',
  data() {
    return {
      emailForm: {
        toAddress: '',
        ccAddress: '',
        subject: '',
        content: '',
        originalMessageId: ''
      },
      fileList: []
    }
  },
  mounted() {
    // 检查是否是回复或转发
    const { replyTo, forward, subject, recipient } = this.$route.query
    if (replyTo) {
      this.handleReply(replyTo, subject, recipient)
    } else if (forward) {
      this.handleForward(forward, subject)
    }
  },
  methods: {
    handleFileChange(file) {
      this.fileList.push(file)
    },
    async handleSend() {
      if (!this.emailForm.toAddress || !this.emailForm.subject || !this.emailForm.content) {
        this.$message.warning('请填写完整信息')
        return
      }
      
      this.$confirm('确认发送邮件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 判断是回复、转发还是普通发送
          const isReply = this.emailForm.originalMessageId && this.emailForm.content.includes('--- 原邮件内容 ---')
          const isForward = this.emailForm.originalMessageId && this.emailForm.content.includes('--- 转发邮件 ---')
          
          let apiUrl = '/email/personal/send'
          if (isReply) {
            apiUrl = '/email/personal/reply'
          } else if (isForward) {
            apiUrl = '/email/personal/forward'
          }
          
          const requestData = {
            recipient: this.emailForm.toAddress,
            cc: this.emailForm.ccAddress,
            subject: this.emailForm.subject,
            content: this.emailForm.content,
            attachments: this.fileList
          }
          
          // 如果是回复，添加原邮件信息
          if (isReply) {
            requestData.originalMessageId = this.emailForm.originalMessageId
            requestData.remark = `reply_to:${this.emailForm.originalMessageId}`
          } else if (isForward) {
            // 如果是转发，添加转发标记
            requestData.originalMessageId = this.emailForm.originalMessageId
            requestData.remark = `forward_from:${this.emailForm.originalMessageId}`
          }
          
          // 调用发送邮件API
          const response = await request({
            url: apiUrl,
            method: 'post',
            data: requestData
          })
          
          if (response.code === 200) {
            let successMsg = '邮件发送成功'
            if (isReply) {
              successMsg = '回复发送成功'
            } else if (isForward) {
              successMsg = '转发发送成功'
            }
            this.$message.success(successMsg)
            this.$router.push('/email/personal/sent')
          } else {
            this.$message.error(response.msg || '邮件发送失败')
          }
        } catch (error) {
          console.error('发送邮件失败:', error)
          this.$message.error('邮件发送失败，请稍后重试')
        }
      })
    },
    handleSaveDraft() {
      this.$message.success('草稿已保存')
    },
    handleCancel() {
      this.$router.go(-1)
    },
    async handleReply(emailId, subject, recipient) {
      try {
        // 获取原邮件详情
        const response = await request({
          url: `/email/personal/detail/${emailId}`,
          method: 'get'
        })
        if (response.code === 200) {
          const originalEmail = response.data
          
          // 设置回复邮件信息
          this.emailForm.subject = subject || `Re: ${originalEmail.subject}`
          // 对于发件箱的回复，收件人应该是原邮件的收件人
          this.emailForm.toAddress = recipient || originalEmail.recipient
          
          // 格式化回复内容
          const replyContent = `\n\n--- 原邮件内容 ---\n` +
            `发件人: ${originalEmail.sender}\n` +
            `收件人: ${originalEmail.recipient}\n` +
            `时间: ${this.formatDateTime(originalEmail.sentTime || originalEmail.receiveTime)}\n` +
            `主题: ${originalEmail.subject}\n\n` +
            `${originalEmail.content}\n\n` +
            `--- 回复内容 ---\n`
          
          this.emailForm.content = replyContent
          
          // 设置回复标记
          this.emailForm.originalMessageId = originalEmail.messageId
        } else {
          this.$message.error('获取原邮件详情失败')
        }
      } catch (error) {
        console.error('获取原邮件详情失败:', error)
        this.$message.error('获取原邮件详情失败')
      }
    },
    async handleForward(emailId, subject) {
      try {
        // 获取原邮件详情
        const response = await request({
          url: `/email/personal/detail/${emailId}`,
          method: 'get'
        })
        if (response.code === 200) {
          const originalEmail = response.data
          
          // 设置转发邮件信息
          this.emailForm.subject = subject || `Fwd: ${originalEmail.subject}`
          
          // 格式化转发内容
          const forwardContent = `\n\n--- 转发邮件 ---\n` +
            `发件人: ${originalEmail.sender}\n` +
            `收件人: ${originalEmail.recipient}\n` +
            `时间: ${this.formatDateTime(originalEmail.sentTime || originalEmail.receiveTime)}\n` +
            `主题: ${originalEmail.subject}\n\n` +
            `${originalEmail.content}\n\n` +
            `--- 转发说明 ---\n`
          
          this.emailForm.content = forwardContent
          
          // 设置转发标记
          this.emailForm.originalMessageId = originalEmail.messageId
        } else {
          this.$message.error('获取原邮件详情失败')
        }
      } catch (error) {
        console.error('获取原邮件详情失败:', error)
        this.$message.error('获取原邮件详情失败')
      }
    },
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

