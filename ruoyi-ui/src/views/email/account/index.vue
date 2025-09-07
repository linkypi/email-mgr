<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="账号名称" prop="accountName">
        <el-input v-model="queryParams.accountName" placeholder="请输入账号名称" clearable />
      </el-form-item>
      <el-form-item label="邮箱地址" prop="emailAddress">
        <el-input v-model="queryParams.emailAddress" placeholder="请输入邮箱地址" clearable />
      </el-form-item>
      <el-form-item label="启用状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="启用状态" clearable>
          <el-option label="启用" value="0" />
          <el-option label="禁用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['email:account:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['email:account:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['email:account:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-check" size="mini" :disabled="multiple" @click="handleBatchEnable" v-hasPermi="['email:account:edit']">批量启用</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-close" size="mini" :disabled="multiple" @click="handleBatchDisable" v-hasPermi="['email:account:edit']">批量禁用</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账号ID" align="center" prop="accountId" />
      <el-table-column label="账号名称" align="center" prop="accountName" />
      <el-table-column label="邮箱地址" align="center" prop="emailAddress" />
      <el-table-column label="SMTP服务器" align="center" prop="smtpHost" />
      <el-table-column label="SMTP端口" align="center" prop="smtpPort" />
      <el-table-column label="SSL" align="center" prop="smtpSsl">
        <template slot-scope="scope">
          <el-tag :type="scope.row.smtpSsl === '1' ? 'success' : 'info'">
            {{ scope.row.smtpSsl === '1' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="每日限制" align="center" prop="dailyLimit" />
      <el-table-column label="发送间隔(秒)" align="center" prop="sendIntervalSeconds" />
      <el-table-column label="已发送" align="center" prop="usedCount" />
      <el-table-column label="启用状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="最后发送时间" align="center" prop="lastSendTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastSendTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
             <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
         <template slot-scope="scope">
           <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['email:account:query']">查看</el-button>
           <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['email:account:edit']">修改</el-button>
           <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['email:account:remove']">删除</el-button>
         </template>
       </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改邮箱账号对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="账号名称" prop="accountName">
          <el-input v-model="form.accountName" placeholder="请输入账号名称" />
        </el-form-item>
        <el-form-item label="邮箱地址" prop="emailAddress">
          <el-input v-model="form.emailAddress" placeholder="请输入邮箱地址" @blur="autoFillImapConfig" />
        </el-form-item>
        <el-form-item label="邮箱密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入邮箱密码" show-password />
        </el-form-item>
        <el-form-item label="SMTP服务器" prop="smtpHost">
          <el-input v-model="form.smtpHost" placeholder="请输入SMTP服务器地址" />
        </el-form-item>
        <el-form-item label="SMTP端口" prop="smtpPort">
          <el-input-number v-model="form.smtpPort" :min="1" :max="65535" placeholder="请输入SMTP端口" />
        </el-form-item>
        <el-form-item label="启用SSL" prop="smtpSsl">
          <el-radio-group v-model="form.smtpSsl">
            <el-radio label="1">是</el-radio>
            <el-radio label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="每日发送限制" prop="dailyLimit">
          <el-input-number v-model="form.dailyLimit" :min="1" :max="10000" placeholder="请输入每日发送限制" />
        </el-form-item>
        <el-form-item label="发送时间间隔(秒)" prop="sendIntervalSeconds">
          <el-input-number v-model="form.sendIntervalSeconds" :min="1" :max="3600" placeholder="请输入发送时间间隔" />
          <div class="form-tip">
            <i class="el-icon-info"></i>
            设置邮件发送之间的间隔时间，建议30-300秒，避免被识别为垃圾邮件
          </div>
        </el-form-item>
        
        <!-- IMAP 配置部分 -->
        <el-divider content-position="left">IMAP 配置</el-divider>
        <el-form-item label="IMAP服务器" prop="imapHost">
          <el-input v-model="form.imapHost" placeholder="请输入IMAP服务器地址" />
        </el-form-item>
        <el-form-item label="IMAP端口" prop="imapPort">
          <el-input-number v-model="form.imapPort" :min="1" :max="65535" placeholder="请输入IMAP端口" />
        </el-form-item>
        <el-form-item label="IMAP启用SSL" prop="imapSsl">
          <el-radio-group v-model="form.imapSsl">
            <el-radio label="1">是</el-radio>
            <el-radio label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="IMAP用户名" prop="imapUsername">
          <el-input v-model="form.imapUsername" placeholder="请输入IMAP用户名（通常与邮箱地址相同）" />
        </el-form-item>
        <el-form-item label="IMAP密码" prop="imapPassword">
          <el-input v-model="form.imapPassword" type="password" placeholder="请输入IMAP密码（通常与邮箱密码相同）" show-password />
        </el-form-item>
        
        
        
        <!-- 其他配置 -->
        <el-divider content-position="left">其他配置</el-divider>
        <el-form-item label="启用邮件跟踪" prop="trackingEnabled">
          <el-radio-group v-model="form.trackingEnabled">
            <el-radio label="1">是</el-radio>
            <el-radio label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Webhook回调地址" prop="webhookUrl">
          <el-input v-model="form.webhookUrl" placeholder="请输入Webhook回调地址（可选）" />
        </el-form-item>
        <el-form-item label="Webhook密钥" prop="webhookSecret">
          <el-input v-model="form.webhookSecret" placeholder="请输入Webhook密钥（可选）" />
        </el-form-item>
        <el-form-item label="发送状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用发送邮件</el-radio>
            <el-radio label="1">禁用发送邮件</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 账号详情对话框 -->
    <el-dialog title="账号详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="账号名称">{{ detailData.accountName }}</el-descriptions-item>
        <el-descriptions-item label="邮箱地址">{{ detailData.emailAddress }}</el-descriptions-item>
        <el-descriptions-item label="SMTP服务器">{{ detailData.smtpHost }}</el-descriptions-item>
        <el-descriptions-item label="SMTP端口">{{ detailData.smtpPort }}</el-descriptions-item>
        <el-descriptions-item label="SMTP SSL">{{ detailData.smtpSsl === '1' ? '启用' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="每日限制">{{ detailData.dailyLimit }}</el-descriptions-item>
        <el-descriptions-item label="发送间隔(秒)">{{ detailData.sendIntervalSeconds }}</el-descriptions-item>
        <el-descriptions-item label="已发送">{{ detailData.usedCount }}</el-descriptions-item>
        <el-descriptions-item label="发送状态">{{ detailData.status === '0' ? '启用发送邮件' : '禁用发送邮件' }}</el-descriptions-item>
        <el-descriptions-item label="IMAP服务器">{{ detailData.imapHost }}</el-descriptions-item>
        <el-descriptions-item label="IMAP端口">{{ detailData.imapPort }}</el-descriptions-item>
        <el-descriptions-item label="IMAP SSL">{{ detailData.imapSsl === '1' ? '启用' : '禁用' }}</el-descriptions-item>
                 <el-descriptions-item label="IMAP用户名">{{ detailData.imapUsername }}</el-descriptions-item>
         <el-descriptions-item label="邮件跟踪">{{ detailData.trackingEnabled === '1' ? '启用' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="最后发送时间">{{ parseTime(detailData.lastSendTime) }}</el-descriptions-item>
        <el-descriptions-item label="最后同步时间">{{ parseTime(detailData.lastSyncTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(detailData.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAccount, getAccount, getAccountForEdit, delAccount, addAccount, updateAccount, batchUpdateAccountStatus } from "@/api/email/account";

export default {
  name: "EmailAccount",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      accountList: [],
      title: "",
      open: false,
      detailOpen: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        accountName: null,
        emailAddress: null,
        status: null
      },
      form: {},
      detailData: {},
      rules: {
        accountName: [
          { required: true, message: "账号名称不能为空", trigger: "blur" }
        ],
        emailAddress: [
          { required: true, message: "邮箱地址不能为空", trigger: "blur" },
          { type: "email", message: "请输入正确的邮箱格式", trigger: "blur" }
        ],
        password: [
          { required: true, message: "邮箱密码不能为空", trigger: "blur" }
        ],
        smtpHost: [
          { required: true, message: "SMTP服务器不能为空", trigger: "blur" }
        ],
        smtpPort: [
          { required: true, message: "SMTP端口不能为空", trigger: "blur" }
        ],
        imapHost: [
          { required: true, message: "IMAP服务器不能为空", trigger: "blur" }
        ],
        imapPort: [
          { required: true, message: "IMAP端口不能为空", trigger: "blur" }
        ],
        imapUsername: [
          { required: true, message: "IMAP用户名不能为空", trigger: "blur" }
        ],
        imapPassword: [
          { required: true, message: "IMAP密码不能为空", trigger: "blur" }
        ],
        dailyLimit: [
          { required: true, message: "每日发送限制不能为空", trigger: "blur" }
        ],
        sendIntervalSeconds: [
          { required: true, message: "发送时间间隔不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listAccount(this.queryParams).then(response => {
        this.accountList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        accountId: null,
        accountName: null,
        emailAddress: null,
        password: null,
        smtpHost: null,
        smtpPort: 587,
        smtpSsl: "1",
        imapHost: null,
        imapPort: 993,
        imapSsl: "1",
        imapUsername: null,
        imapPassword: null,
        webhookUrl: null,
        webhookSecret: null,
        trackingEnabled: "0",
        dailyLimit: 100,
        sendIntervalSeconds: 60,
        usedCount: 0,
        status: "0",
        remark: null
      };
      this.resetForm("form");
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.accountId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加邮箱账号";
    },
    handleUpdate(row) {
      this.reset();
      const accountId = row.accountId || this.ids
      getAccountForEdit(accountId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改邮箱账号";
      });
    },
    handleView(row) {
      getAccount(row.accountId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.accountId != null) {
            updateAccount(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAccount(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const accountIds = row.accountId || this.ids;
      this.$modal.confirm("是否确认删除账号编号为\"" + accountIds + "\"的数据项?").then(function() {
        return delAccount(accountIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleBatchEnable() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning("请选择要启用的账号");
        return;
      }
      this.$modal.confirm("是否确认启用选中的 " + this.ids.length + " 个账号?").then(() => {
        const updateData = {
          accountIds: this.ids,
          status: "0"
        };
        return batchUpdateAccountStatus(updateData);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("批量启用成功");
      }).catch(() => {});
    },
    handleBatchDisable() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning("请选择要禁用的账号");
        return;
      }
      this.$modal.confirm("是否确认禁用选中的 " + this.ids.length + " 个账号?").then(() => {
        const updateData = {
          accountIds: this.ids,
          status: "1"
        };
        return batchUpdateAccountStatus(updateData);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("批量禁用成功");
      }).catch(() => {});
    },
    
    /** 自动填充IMAP配置 */
    autoFillImapConfig() {
      if (!this.form.emailAddress) return;
      
      const email = this.form.emailAddress.toLowerCase();
      let imapHost = '';
      let imapPort = 993;
      
      if (email.includes('@gmail.com')) {
        imapHost = 'imap.gmail.com';
        imapPort = 993;
      } else if (email.includes('@qq.com')) {
        imapHost = 'imap.qq.com';
        imapPort = 993;
      } else if (email.includes('@163.com')) {
        imapHost = 'imap.163.com';
        imapPort = 993;
      } else if (email.includes('@126.com')) {
        imapHost = 'imap.126.com';
        imapPort = 993;
      } else if (email.includes('@sina.com')) {
        imapHost = 'imap.sina.com';
        imapPort = 993;
      } else if (email.includes('@outlook.com') || email.includes('@hotmail.com')) {
        imapHost = 'outlook.office365.com';
        imapPort = 993;
      } else if (email.includes('@yahoo.com')) {
        imapHost = 'imap.mail.yahoo.com';
        imapPort = 993;
      } else {
        // 默认配置
        imapHost = 'imap.example.com';
        imapPort = 993;
      }
      
      this.form.imapHost = imapHost;
      this.form.imapPort = imapPort;
      this.form.imapSsl = '1';
      this.form.imapUsername = this.form.emailAddress;
      this.form.imapPassword = this.form.password; // 通常IMAP密码与邮箱密码相同
    },

    

    
  }
};
</script>

<style scoped>
.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
}

.el-dropdown {
  margin-left: 8px;
}

.el-table .el-button--text {
  padding: 0 4px;
}
</style>
