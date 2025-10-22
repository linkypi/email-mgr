<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="发件人姓名" prop="senderName">
        <el-input v-model="queryParams.senderName" placeholder="请输入发件人姓名" clearable />
      </el-form-item>
      <el-form-item label="公司名称" prop="company">
        <el-input v-model="queryParams.company" placeholder="请输入公司名称" clearable />
      </el-form-item>
      <el-form-item label="部门" prop="department">
        <el-input v-model="queryParams.department" placeholder="请输入部门" clearable />
      </el-form-item>
      <el-form-item label="等级" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择等级" clearable>
          <el-option label="重要" value="1" />
          <el-option label="普通" value="2" />
          <el-option label="一般" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['email:sender:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['email:sender:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['email:sender:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['email:sender:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="senderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="发件人ID" align="center" prop="senderId" />
      <el-table-column label="发件人姓名" align="center" prop="senderName" />
      <el-table-column label="公司名称" align="center" prop="company" />
      <el-table-column label="部门" align="center" prop="department" />
      <el-table-column label="职位" align="center" prop="position" />
      <el-table-column label="联系电话" align="center" prop="phone" />
      <el-table-column label="等级" align="center" prop="level">
        <template slot-scope="scope">
          <el-tag :type="scope.row.level === '1' ? 'danger' : scope.row.level === '2' ? 'warning' : 'info'">
            {{ scope.row.level === '1' ? '重要' : scope.row.level === '2' ? '普通' : '一般' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联账号数" align="center" prop="totalAccounts" />
      <el-table-column label="活跃账号数" align="center" prop="activeAccounts" />
      <el-table-column label="总发送数" align="center" prop="totalSent" />
      <el-table-column label="回复率" align="center" prop="replyRate">
        <template slot-scope="scope">
          <span>{{ scope.row.replyRate }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['email:sender:query']">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['email:sender:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-setting" @click="handleManageAccounts(scope.row)" v-hasPermi="['email:account:list']">管理账号</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['email:sender:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改发件人信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发件人姓名" prop="senderName">
              <el-input v-model="form.senderName" placeholder="请输入发件人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公司名称" prop="company">
              <el-input v-model="form.company" placeholder="请输入公司名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入部门" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择等级">
                <el-option label="重要" value="1" />
                <el-option label="普通" value="2" />
                <el-option label="一般" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="form.tags" placeholder="请输入标签，多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="发件人描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入发件人描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
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

    <!-- 发件人详情对话框 -->
    <el-dialog title="发件人详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="发件人ID">{{ detailData.senderId }}</el-descriptions-item>
        <el-descriptions-item label="发件人姓名">{{ detailData.senderName }}</el-descriptions-item>
        <el-descriptions-item label="公司名称">{{ detailData.company }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ detailData.department }}</el-descriptions-item>
        <el-descriptions-item label="职位">{{ detailData.position }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="等级">
          <el-tag :type="detailData.level === '1' ? 'danger' : detailData.level === '2' ? 'warning' : 'info'">
            {{ detailData.level === '1' ? '重要' : detailData.level === '2' ? '普通' : '一般' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === '0' ? 'success' : 'danger'">
            {{ detailData.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="关联账号数">{{ detailData.totalAccounts }}</el-descriptions-item>
        <el-descriptions-item label="活跃账号数">{{ detailData.activeAccounts }}</el-descriptions-item>
        <el-descriptions-item label="总发送数">{{ detailData.totalSent }}</el-descriptions-item>
        <el-descriptions-item label="回复率">{{ detailData.replyRate }}%</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ detailData.address }}</el-descriptions-item>
        <el-descriptions-item label="标签" :span="2">{{ detailData.tags }}</el-descriptions-item>
        <el-descriptions-item label="发件人描述" :span="2">{{ detailData.description }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(detailData.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listSender, getSender, delSender, addSender, updateSender, exportSender } from "@/api/email/sender";

export default {
  name: "EmailSender",
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
      // 发件人信息表格数据
      senderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        senderName: null,
        company: null,
        department: null,
        level: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        senderName: [
          { required: true, message: "发件人姓名不能为空", trigger: "blur" }
        ],
        company: [
          { required: true, message: "公司名称不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询发件人信息列表 */
    getList() {
      this.loading = true;
      listSender(this.queryParams).then(response => {
        this.senderList = response.rows;
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
        senderId: null,
        senderName: null,
        company: null,
        department: null,
        position: null,
        phone: null,
        address: null,
        description: null,
        level: "3",
        tags: null,
        totalAccounts: 0,
        activeAccounts: 0,
        totalSent: 0,
        totalReplied: 0,
        replyRate: 0.00,
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
      this.ids = selection.map(item => item.senderId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加发件人信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const senderId = row.senderId || this.ids
      getSender(senderId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改发件人信息";
      });
    },
    /** 查看按钮操作 */
    handleView(row) {
      getSender(row.senderId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    /** 管理账号按钮操作 */
    handleManageAccounts(row) {
      this.$router.push({
        path: '/email/account',
        query: { senderId: row.senderId, senderName: row.senderName }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.senderId != null) {
            updateSender(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSender(this.form).then(response => {
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
      const senderIds = row.senderId || this.ids;
      this.$modal.confirm('是否确认删除发件人信息编号为"' + senderIds + '"的数据项？').then(function() {
        return delSender(senderIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('email/sender/export', {
        ...this.queryParams
      }, `发件人信息_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>










