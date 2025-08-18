<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>邮件分类</span>
          </div>
          <el-menu
            :default-active="activeMenu"
            class="email-menu"
            @select="handleMenuSelect">
            <el-menu-item index="inbox">
              <i class="el-icon-inbox"></i>
              <span>收件箱</span>
              <el-badge v-if="unreadCount > 0" :value="unreadCount" class="item" type="primary"></el-badge>
            </el-menu-item>
            <el-menu-item index="sent">
              <i class="el-icon-s-promotion"></i>
              <span>发件箱</span>
            </el-menu-item>
            <el-menu-item index="starred">
              <i class="el-icon-star-on"></i>
              <span>星标邮件</span>
            </el-menu-item>
            <el-menu-item index="deleted">
              <i class="el-icon-delete"></i>
              <span>已删除</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      
      <el-col :span="18">
        <el-card>
          <div slot="header" class="clearfix">
            <span>{{ getMenuTitle() }}</span>
            <el-button v-if="activeMenu === 'inbox'" style="float: right" type="primary" size="small" @click="handleCompose">
              写邮件
            </el-button>
          </div>
          
          <!-- 收件箱内容 -->
          <div v-if="activeMenu === 'inbox'">
            <el-table :data="inboxList" style="width: 100%">
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
          </div>
          
          <!-- 发件箱内容 -->
          <div v-if="activeMenu === 'sent'">
            <el-table :data="sentList" style="width: 100%">
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
          </div>
          
          <!-- 星标邮件内容 -->
          <div v-if="activeMenu === 'starred'">
            <el-table :data="starredList" style="width: 100%">
              <el-table-column prop="fromAddress" label="发件人" width="200"></el-table-column>
              <el-table-column prop="subject" label="主题" min-width="300"></el-table-column>
              <el-table-column prop="receiveTime" label="时间" width="180"></el-table-column>
              <el-table-column label="操作" width="200">
                <template slot-scope="scope">
                  <el-button size="mini" @click="handleView(scope.row)">查看</el-button>
                  <el-button size="mini" type="warning" @click="handleUnstar(scope.row)">取消星标</el-button>
                  <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <!-- 已删除邮件内容 -->
          <div v-if="activeMenu === 'deleted'">
            <el-table :data="deletedList" style="width: 100%">
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
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'EmailPersonal',
  data() {
    return {
      activeMenu: 'inbox',
      unreadCount: 12,
      inboxList: [
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
      ],
      sentList: [
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
      ],
      starredList: [
        {
          emailId: 1,
          fromAddress: 'boss@company.com',
          subject: '重要项目通知',
          receiveTime: '2024-01-15 10:30:00'
        },
        {
          emailId: 2,
          fromAddress: 'hr@company.com',
          subject: '年终奖金通知',
          receiveTime: '2024-01-14 16:20:00'
        }
      ],
      deletedList: [
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
    handleMenuSelect(key) {
      this.activeMenu = key
    },
    getMenuTitle() {
      const titles = {
        'inbox': '收件箱',
        'sent': '发件箱',
        'starred': '星标邮件',
        'deleted': '已删除'
      }
      return titles[this.activeMenu] || '邮件'
    },
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
    handleUnstar(row) {
      this.$confirm('确认取消星标吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('已取消星标')
      })
    },
    handleRestore(row) {
      this.$confirm('确认恢复这封邮件吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('恢复成功')
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

<style scoped>
.email-menu {
  border-right: none;
}

.email-menu .el-menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.email-menu .el-menu-item i {
  margin-right: 8px;
}

.el-badge {
  margin-left: auto;
}
</style>
