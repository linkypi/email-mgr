<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="账号名称" prop="accountName">
        <el-input v-model="queryParams.accountName" placeholder="请输入账号名称" clearable />
      </el-form-item>
      <el-form-item label="邮箱地址" prop="email">
        <el-input v-model="queryParams.email" placeholder="请输入邮箱地址" clearable />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="账号状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
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
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="accountList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账号ID" align="center" prop="accountId" />
      <el-table-column label="账号名称" align="center" prop="accountName" />
      <el-table-column label="邮箱地址" align="center" prop="email" />
      <el-table-column label="SMTP服务器" align="center" prop="smtpServer" />
      <el-table-column label="SMTP端口" align="center" prop="smtpPort" />
      <el-table-column label="每日限制" align="center" prop="dailyLimit" />
      <el-table-column label="已使用" align="center" prop="usedCount" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['email:account:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-connection" @click="handleTest(scope.row)" v-hasPermi="['email:account:test']">测试</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['email:account:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改邮箱账号对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="账号名称" prop="accountName">
          <el-input v-model="form.accountName" placeholder="请输入账号名称" />
        </el-form-item>
        <el-form-item label="邮箱地址" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        <el-form-item label="邮箱密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入邮箱密码" show-password />
        </el-form-item>
        <el-form-item label="SMTP服务器" prop="smtpServer">
          <el-input v-model="form.smtpServer" placeholder="请输入SMTP服务器地址" />
        </el-form-item>
        <el-form-item label="SMTP端口" prop="smtpPort">
          <el-input-number v-model="form.smtpPort" :min="1" :max="65535" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="每日限制" prop="dailyLimit">
          <el-input-number v-model="form.dailyLimit" :min="1" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="账号状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value">{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAccount, getAccount, delAccount, addAccount, updateAccount, testAccount } from "@/api/email/account";

export default {
  name: "Account",
  dicts: ['sys_normal_disable'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 账号表格数据
      accountList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        accountName: null,
        email: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        accountName: [
          { required: true, message: "账号名称不能为空", trigger: "blur" }
        ],
        email: [
          { required: true, message: "邮箱地址不能为空", trigger: "blur" },
          { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }
        ],
        password: [
          { required: true, message: "邮箱密码不能为空", trigger: "blur" }
        ],
        smtpServer: [
          { required: true, message: "SMTP服务器不能为空", trigger: "blur" }
        ],
        smtpPort: [
          { required: true, message: "SMTP端口不能为空", trigger: "blur" }
        ],
        dailyLimit: [
          { required: true, message: "每日限制不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询账号列表 */
    getList() {
      this.loading = true;
      listAccount(this.queryParams).then(response => {
        this.accountList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        accountId: null,
        accountName: null,
        email: null,
        password: null,
        smtpServer: null,
        smtpPort: 587,
        dailyLimit: 1000,
        status: "0",
        remark: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.accountId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加邮箱账号";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const accountId = row.accountId || this.ids
      getAccount(accountId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改邮箱账号";
      });
    },
    /** 测试按钮操作 */
    handleTest(row) {
      this.$modal.confirm('确认测试邮箱账号"' + row.accountName + '"吗？').then(function() {
        return testAccount(row);
      }).then(() => {
        this.$modal.msgSuccess("测试成功");
      }).catch(() => {});
    },
    /** 提交按钮 */
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
    /** 删除按钮操作 */
    handleDelete(row) {
      const accountIds = row.accountId || this.ids;
      this.$modal.confirm('是否确认删除邮箱账号编号为"' + accountIds + '"的数据项?').then(function() {
        return delAccount(accountIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
