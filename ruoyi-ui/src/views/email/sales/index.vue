<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="联系人" prop="contactName">
        <el-input v-model="queryParams.contactName" placeholder="请输入联系人姓名" clearable />
      </el-form-item>
      <el-form-item label="产品名称" prop="productName">
        <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable />
      </el-form-item>
      <el-form-item label="销售状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="销售状态" clearable>
          <el-option label="已完成" value="已完成" />
          <el-option label="进行中" value="进行中" />
          <el-option label="已取消" value="已取消" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['email:sales:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['email:sales:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['email:sales:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="salesList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="销售ID" align="center" prop="salesId" />
      <el-table-column label="联系人" align="center" prop="contactName" />
      <el-table-column label="产品名称" align="center" prop="productName" />
      <el-table-column label="销售金额" align="center" prop="amount">
        <template slot-scope="scope">
          ¥{{ scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column label="数量" align="center" prop="quantity" />
      <el-table-column label="销售日期" align="center" prop="saleDate" width="120" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '已完成' ? 'success' : scope.row.status === '进行中' ? 'warning' : 'danger'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['email:sales:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['email:sales:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改销售数据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="联系人" prop="contactId">
          <el-select v-model="form.contactId" placeholder="请选择联系人" style="width: 100%">
            <el-option
              v-for="item in contactOptions"
              :key="item.contactId"
              :label="item.name"
              :value="item.contactId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="销售金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="销售日期" prop="saleDate">
          <el-date-picker
            v-model="form.saleDate"
            type="date"
            placeholder="选择销售日期"
            style="width: 100%">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="销售状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择销售状态">
            <el-option label="已完成" value="已完成"></el-option>
            <el-option label="进行中" value="进行中"></el-option>
            <el-option label="已取消" value="已取消"></el-option>
          </el-select>
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
  </div>
</template>

<script>
import { listSales, getSales, delSales, addSales, updateSales } from "@/api/email/sales";
import { listContact } from "@/api/email/contact";

export default {
  name: "Sales",
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
      // 销售数据表格数据
      salesList: [],
      // 联系人选项
      contactOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contactName: null,
        productName: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        contactId: [
          { required: true, message: "联系人不能为空", trigger: "change" }
        ],
        productName: [
          { required: true, message: "产品名称不能为空", trigger: "blur" }
        ],
        amount: [
          { required: true, message: "销售金额不能为空", trigger: "blur" }
        ],
        quantity: [
          { required: true, message: "数量不能为空", trigger: "blur" }
        ],
        saleDate: [
          { required: true, message: "销售日期不能为空", trigger: "change" }
        ],
        status: [
          { required: true, message: "销售状态不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getContactOptions();
  },
  methods: {
    /** 查询销售数据列表 */
    getList() {
      this.loading = true;
      listSales(this.queryParams).then(response => {
        this.salesList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取联系人选项 */
    getContactOptions() {
      listContact().then(response => {
        this.contactOptions = response.rows;
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
        salesId: null,
        contactId: null,
        productName: null,
        amount: null,
        quantity: null,
        saleDate: null,
        status: null,
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
      this.ids = selection.map(item => item.salesId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加销售数据";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const salesId = row.salesId || this.ids
      getSales(salesId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改销售数据";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.salesId != null) {
            updateSales(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addSales(this.form).then(response => {
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
      const salesIds = row.salesId || this.ids;
      this.$modal.confirm('是否确认删除销售数据编号为"' + salesIds + '"的数据项?').then(function() {
        return delSales(salesIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
