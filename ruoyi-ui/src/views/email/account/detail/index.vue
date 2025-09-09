<template>
  <div class="app-container">
    <!-- 页面头部信息 -->
    <el-card class="box-card" style="margin-bottom: 20px;">
      <div slot="header" class="clearfix">
        <span>邮箱账号管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="goBack">
          <i class="el-icon-back"></i> 返回发件人管理
        </el-button>
      </div>
      <div v-if="currentSender" class="sender-info">
        <el-descriptions :column="4" border>
          <el-descriptions-item label="发件人姓名">{{ currentSender.senderName }}</el-descriptions-item>
          <el-descriptions-item label="公司名称">{{ currentSender.company }}</el-descriptions-item>
          <el-descriptions-item label="部门">{{ currentSender.department }}</el-descriptions-item>
          <el-descriptions-item label="职位">{{ currentSender.position }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 邮箱账号管理区域 -->
    <el-card class="box-card">

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
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImport" v-hasPermi="['email:account:import']">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['email:account:export']">导出</el-button>
      </el-col>
<!--      <el-col :span="1.5">-->
<!--        <el-button type="success" plain icon="el-icon-document" size="mini" @click="handleExportTemplate" v-hasPermi="['email:account:export']">导出模板</el-button>-->
<!--      </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账号ID" align="center" prop="accountId" />
      <el-table-column label="发件人" align="center" prop="senderName" width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.senderName" type="primary" size="small">
            {{ scope.row.senderName }}
          </el-tag>
          <span v-else style="color: #999;">未分配</span>
        </template>
      </el-table-column>
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
    </el-card>

    <!-- 添加或修改发件人对话框 -->
    <el-dialog :title="senderTitle" :visible.sync="senderOpen" width="800px" append-to-body>
      <el-form ref="senderForm" :model="senderForm" :rules="senderRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发件人姓名" prop="senderName">
              <el-input v-model="senderForm.senderName" placeholder="请输入发件人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司名称" prop="company">
              <el-input v-model="senderForm.company" placeholder="请输入公司名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input v-model="senderForm.department" placeholder="请输入部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="senderForm.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="senderForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="等级" prop="level">
              <el-select v-model="senderForm.level" placeholder="请选择等级">
                <el-option label="重要" value="1" />
                <el-option label="普通" value="2" />
                <el-option label="一般" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="senderForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="senderForm.tags" placeholder="请输入标签，多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="发件人描述" prop="description">
          <el-input v-model="senderForm.description" type="textarea" placeholder="请输入发件人描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="senderForm.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="senderForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitSenderForm">确 定</el-button>
        <el-button @click="cancelSender">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改邮箱账号对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="关联发件人" prop="senderId">
          <el-select v-model="form.senderId" placeholder="请选择发件人" style="width: 100%">
            <el-option label="不关联发件人" value="" />
            <el-option
              v-for="sender in senderOptions"
              :key="sender.senderId"
              :label="sender.senderName + ' (' + sender.company + ')'"
              :value="sender.senderId">
            </el-option>
          </el-select>
        </el-form-item>
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

    <!-- 导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的账号数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAccount, getAccount, getAccountForEdit, delAccount, addAccount, updateAccount, batchUpdateAccountStatus, exportAccount, exportAccountTemplate } from "@/api/email/account";
import { getSenderOptions, getSender } from "@/api/email/sender";
import { parseTime } from "@/utils/ruoyi";
import { getToken } from "@/utils/auth";

export default {
  name: "EmailAccountDetail",
  data() {
    return {
      // 邮箱账号相关
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
      // 当前发件人信息
      currentSender: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        senderId: null,
        accountName: null,
        emailAddress: null,
        status: null
      },
      // 发件人选项
      senderOptions: [],
      form: {},
      detailData: {},
      // 发件人对话框相关
      senderTitle: "",
      senderOpen: false,
      senderForm: {},
      senderRules: {
        senderName: [
          { required: true, message: "发件人姓名不能为空", trigger: "blur" }
        ],
        company: [
          { required: true, message: "公司名称不能为空", trigger: "blur" }
        ]
      },
      // 导入导出相关
      upload: {
        // 是否显示弹出层（用户导入）
        open: false,
        // 弹出层标题（用户导入）
        title: "",
        // 是否禁用上传
        isUploading: false,
        // 是否更新已经存在的用户数据
        updateSupport: 0,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/email/account/importData"
      },
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
    // 检查是否有发件人ID参数
    if (this.$route.query.senderId) {
      this.queryParams.senderId = parseInt(this.$route.query.senderId);
      this.loadCurrentSender();
    }
    this.getList();
    this.getSenderOptions();
  },
  watch: {
    // 监听路由参数变化
    '$route.query.senderId'(newSenderId) {
      if (newSenderId) {
        this.queryParams.senderId = parseInt(newSenderId);
        this.loadCurrentSender();
        this.getList();
      }
    }
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
        senderId: null,
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
    resetForm(refName) {
      if (this.$refs[refName]) {
        this.$refs[refName].resetFields();
      }
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

    /** 获取发件人选项 */
    getSenderOptions() {
      getSenderOptions().then(response => {
        this.senderOptions = response.data;
      });
    },
    /** 加载当前发件人信息 */
    loadCurrentSender() {
      if (this.$route.query.senderId) {
        getSender(this.$route.query.senderId).then(response => {
          this.currentSender = response.data;
        });
      }
    },
    /** 返回发件人管理页面 */
    goBack() {
      this.$router.push('/management/account');
    },
    /** 提交发件人表单 */
    submitSenderForm() {
      this.$refs["senderForm"].validate(valid => {
        if (valid) {
          // 这里可以添加发件人保存逻辑
          this.$modal.msgSuccess("保存成功");
          this.senderOpen = false;
        }
      });
    },
    /** 取消发件人编辑 */
    cancelSender() {
      this.senderOpen = false;
      this.resetSenderForm();
    },
    /** 重置发件人表单 */
    resetSenderForm() {
      this.senderForm = {
        senderId: null,
        senderName: null,
        company: null,
        department: null,
        position: null,
        phone: null,
        address: null,
        description: null,
        level: "2",
        tags: null,
        status: "0",
        remark: null
      };
      this.resetForm("senderForm");
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('email/account/export', {
        ...this.queryParams
      }, `邮箱账号_${new Date().getTime()}.xlsx`)
    },
    /** 导出模板按钮操作 */
    handleExportTemplate() {
      this.download('email/account/exportTemplate', {
        senderId: this.queryParams.senderId
      }, `邮箱账号导入模板_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "邮箱账号导入";
      this.upload.open = true;
    },
    /** 下载模板 */
    importTemplate() {
      this.download('email/account/exportTemplate', {
        senderId: this.queryParams.senderId
      }, `邮箱账号导入模板_${new Date().getTime()}.xlsx`)
    },
    // 文件上传中处理
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    // 文件上传成功处理
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    // 提交上传文件
    submitFileForm() {
      this.$refs.upload.submit();
    }
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
