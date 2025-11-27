<template>
  <div class="app-container">
    <!-- 搜索卡片 -->
    <div class="search-card">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="收件人">
          <el-select v-model="queryParams.contactId" placeholder="请选择收件人" clearable filterable style="width: 200px">
            <el-option
              v-for="contact in contactList"
              :key="contact.contactId"
              :label="`${contact.name} (${contact.email})`"
              :value="contact.contactId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="带货型号">
          <el-input v-model="queryParams.productModel" placeholder="请输入带货型号" clearable @keyup.enter.native="handleSearch"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="已寄样" value="已寄样"></el-option>
            <el-option label="已发布" value="已发布"></el-option>
            <el-option label="未发布" value="未发布"></el-option>
            <el-option label="确定不发" value="确定不发"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="折扣类型">
          <el-input v-model="queryParams.discountType" placeholder="请输入折扣类型" clearable @keyup.enter.native="handleSearch"></el-input>
        </el-form-item>
        <el-form-item label="来源渠道">
          <el-input v-model="queryParams.sourceChannel" placeholder="请输入来源渠道" clearable @keyup.enter.native="handleSearch"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch" :loading="searchLoading">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增销售数据</el-button>
          <el-button type="success" icon="el-icon-upload" @click="handleImport">导入</el-button>
          <el-button type="warning" icon="el-icon-download" @click="handleExport">导出</el-button>
          <el-button type="info" icon="el-icon-refresh" @click="getList">刷新</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格卡片 -->
    <div class="table-card">
      <!-- 批量操作 -->
      <el-row :gutter="10" class="mb8" v-if="multipleSelection.length > 0">
        <el-col :span="1.5">
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleBatchDelete">
            批量删除
          </el-button>
        </el-col>
      </el-row>

      <!-- 销售数据列表 -->
      <el-table 
        v-loading="loading" 
        :data="salesDataList" 
        @selection-change="handleSelectionChange"
        style="width: 100%">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="salesId" label="ID" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="contactNames" label="收件人" width="200" show-overflow-tooltip sortable>
          <template slot-scope="scope">
            <div v-if="scope.row.contactNames && scope.row.contactNames.length > 0">
              <el-tag v-for="(name, index) in scope.row.contactNames" :key="index" size="mini" style="margin-right: 5px; margin-bottom: 2px;">
                {{ name }}
              </el-tag>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="contactEmails" label="邮箱" width="250" show-overflow-tooltip sortable>
          <template slot-scope="scope">
            <div v-if="scope.row.contactEmails && scope.row.contactEmails.length > 0">
              <div v-for="(email, index) in scope.row.contactEmails" :key="index" style="font-size: 12px; margin-bottom: 2px;">
                {{ email }}
              </div>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center" sortable>
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="mini">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="salesDate" label="带货日期" width="120" align="center" sortable>
          <template slot-scope="scope">
            <span v-if="scope.row.salesDate">{{ formatDate(scope.row.salesDate, 'YYYY-MM-DD') }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="productModel" label="带货型号" width="150" show-overflow-tooltip sortable></el-table-column>
        <el-table-column prop="salesQuantity" label="带货单量" width="100" align="center" sortable></el-table-column>
        <el-table-column prop="playCount" label="播放次数" width="100" align="center" sortable></el-table-column>
        <el-table-column prop="conversionRate" label="转化率" width="100" align="center" sortable>
          <template slot-scope="scope">
            <span v-if="scope.row.conversionRate != null">{{ (scope.row.conversionRate * 100).toFixed(2) }}%</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="discountType" label="折扣类型" width="120" show-overflow-tooltip sortable></el-table-column>
        <el-table-column prop="discountRatio" label="折扣比例" width="100" align="center" sortable>
          <template slot-scope="scope">
            <span v-if="scope.row.discountRatio != null">{{ (scope.row.discountRatio * 100).toFixed(2) }}%</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sourceChannel" label="来源渠道" width="120" show-overflow-tooltip sortable></el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" show-overflow-tooltip sortable>
          <template slot-scope="scope">
            <span v-if="scope.row.createTime">{{ formatDate(scope.row.createTime, 'YYYY-MM-DD HH:mm:ss') }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="收件人" prop="contactIds">
              <el-select v-model="form.contactIds" placeholder="请选择收件人" style="width: 100%" multiple filterable>
                <el-option
                  v-for="contact in contactList"
                  :key="contact.contactId"
                  :label="`${contact.name} (${contact.email})`"
                  :value="contact.contactId">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="已寄样" value="已寄样"></el-option>
                <el-option label="已发布" value="已发布"></el-option>
                <el-option label="未发布" value="未发布"></el-option>
                <el-option label="确定不发" value="确定不发"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="带货日期" prop="salesDate">
              <el-date-picker
                v-model="form.salesDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                value-format="yyyy-MM-dd">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="带货型号" prop="productModel">
              <el-input v-model="form.productModel" placeholder="请输入带货型号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="带货单量" prop="salesQuantity">
              <el-input-number v-model="form.salesQuantity" :min="0" placeholder="请输入带货单量" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="播放次数">
              <el-input-number v-model="form.playCount" :min="0" placeholder="请输入播放次数" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="转化率">
              <el-input-number v-model="form.conversionRate" :min="0" :max="1" :precision="2" :step="0.01" placeholder="请输入转化率(0-1之间的小数)" style="width: 100%"></el-input-number>
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">例如：0.2 表示 20%</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="折扣类型">
              <el-input v-model="form.discountType" placeholder="请输入折扣类型"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="折扣比例">
              <el-input-number v-model="form.discountRatio" :min="0" :max="1" :precision="2" :step="0.01" placeholder="请输入折扣比例(0-1之间的小数)" style="width: 100%"></el-input-number>
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">例如：0.1 表示 10%</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源渠道">
              <el-input v-model="form.sourceChannel" placeholder="请输入来源渠道"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog title="导入销售数据" :visible.sync="importVisible" width="600px" :close-on-click-modal="false">
      <div style="margin-bottom: 20px;">
        <el-alert
          title="导入说明"
          type="info"
          :closable="false"
          show-icon>
          <div slot="description">
            <p>1. 支持导入 .xlsx、.xls、.csv 格式文件</p>
            <p>2. 文件大小不超过 10MB</p>
            <p>3. 请先下载模板，按照模板格式填写数据</p>
            <p>4. 必填字段：收件人、状态、带货日期、带货型号、带货单量</p>
            <p>5. 状态字段：已寄样、已发布、未发布、确定不发</p>
          </div>
        </el-alert>
      </div>
      
      <el-form :model="importForm" label-width="100px">
        <el-form-item label="选择文件">
          <el-upload
            class="upload-demo"
            drag
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            accept=".xlsx,.xls,.csv">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">只能上传xlsx/xls/csv文件，且不超过10MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <div style="text-align: center; margin: 20px 0;">
        <el-button type="primary" @click="handleImportSubmit" :disabled="!selectedFile" :loading="importLoading">
          开始导入
        </el-button>
        <el-button @click="handleDownloadTemplate" style="margin-left: 10px;">
          下载模板
        </el-button>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="importVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'SalesData',
  created() {
    // 先检查URL参数，设置查询条件（必须在调用 getList 之前设置）
    const routeQuery = this.$route.query
    if (routeQuery.contactId) {
      const contactId = parseInt(routeQuery.contactId)
      if (!isNaN(contactId)) {
        this.queryParams.contactId = contactId
      }
    }
    // 加载联系人列表（异步，不影响查询）
    this.loadContactList()
    // 调用查询方法（会使用已设置的 queryParams.contactId）
    this.getList()
  },
  data() {
    return {
      // 加载状态
      loading: false,
      searchLoading: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contactId: null, // 收件人ID（下拉选择）
        productModel: '',
        status: '',
        discountType: '',
        sourceChannel: ''
      },
      // 销售数据列表
      salesDataList: [],
      // 联系人列表
      contactList: [],
      // 总数
      total: 0,
      // 多选
      multipleSelection: [],
      // 对话框
      dialogVisible: false,
      dialogTitle: '新增销售数据',
      // 表单
      form: {
        salesId: null,
        contactIds: [],
        status: '',
        salesDate: '',
        productModel: '',
        salesQuantity: 0,
        playCount: 0,
        conversionRate: 0,
        discountType: '',
        discountRatio: 0,
        sourceChannel: '',
        remark: ''
      },
      // 表单验证规则
      rules: {
        contactIds: [
          { required: true, message: '请选择收件人', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ],
        salesDate: [
          { required: true, message: '请选择带货日期', trigger: 'change' }
        ],
        productModel: [
          { required: true, message: '请输入带货型号', trigger: 'blur' }
        ],
        salesQuantity: [
          { required: true, message: '请输入带货单量', trigger: 'blur' }
        ]
      },
      // 导入对话框
      importVisible: false,
      fileList: [],
      selectedFile: null,
      importLoading: false,
      importForm: {}
    }
  },
  methods: {
    // API 方法定义
    getSalesDataList(query) {
      return request({
        url: '/email/sales/list',
        method: 'get',
        params: query
      })
    },
    
    getSalesData(salesId) {
      return request({
        url: '/email/sales/' + salesId,
        method: 'get'
      })
    },
    
    addSalesData(data) {
      return request({
        url: '/email/sales',
        method: 'post',
        data: data
      })
    },
    
    updateSalesData(data) {
      return request({
        url: '/email/sales',
        method: 'put',
        data: data
      })
    },
    
    delSalesData(salesId) {
      return request({
        url: '/email/sales/' + salesId,
        method: 'delete'
      })
    },
    
    batchDeleteSalesData(salesIds) {
      return request({
        url: '/email/sales/' + salesIds.join(','),
        method: 'delete'
      })
    },
    
    getContactList() {
      return request({
        url: '/email/contact/all',
        method: 'get'
      })
    },
    
    exportSalesData(query) {
      return request({
        url: '/email/sales/export',
        method: 'post',
        data: query,
        responseType: 'blob'
      })
    },
    
    /** 查询销售数据列表 */
    getList() {
      this.loading = true
      this.getSalesDataList(this.queryParams).then(response => {
        this.salesDataList = response.rows || []
        this.total = response.total || 0
        this.loading = false
        this.searchLoading = false
      }).catch(() => {
        // 如果API不存在，使用模拟数据
        this.salesDataList = [
          {
            salesId: 1,
            contactIds: [1, 2],
            contactNames: ['张三', '李四'],
            contactEmails: ['zhangsan@example.com', 'lisi@example.com'],
            status: '已寄样',
            salesDate: '2024-01-15',
            productModel: 'iPhone 15 Pro',
            salesQuantity: 100,
            playCount: 5000,
            conversionRate: 2.00,
            discountType: '限时折扣',
            discountRatio: 0.10,
            sourceChannel: '抖音',
            remark: '测试数据',
            createTime: '2024-01-15 10:30:00'
          },
          {
            salesId: 2,
            contactIds: [2],
            contactNames: ['李四'],
            contactEmails: ['lisi@example.com'],
            status: '已发布',
            salesDate: '2024-01-14',
            productModel: 'MacBook Air',
            salesQuantity: 50,
            playCount: 3000,
            conversionRate: 1.67,
            discountType: '新用户优惠',
            discountRatio: 0.15,
            sourceChannel: '小红书',
            remark: '新品推广',
            createTime: '2024-01-14 14:20:00'
          }
        ]
        this.total = this.salesDataList.length
        this.loading = false
        this.searchLoading = false
      })
    },
    
    /** 获取联系人列表 */
    loadContactList() {
      this.getContactList().then(response => {
        this.contactList = response.data || []
      }).catch(() => {
        // 模拟数据
        this.contactList = [
          { contactId: 1, name: '张三', email: 'zhangsan@example.com' },
          { contactId: 2, name: '李四', email: 'lisi@example.com' },
          { contactId: 3, name: '王五', email: 'wangwu@example.com' }
        ]
      })
    },
    
    /** 搜索按钮操作 */
    handleSearch() {
      this.searchLoading = true
      this.queryParams.pageNum = 1
      this.getList()
    },
    
    /** 重置按钮操作 */
    handleReset() {
      this.searchLoading = false
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        contactId: null,
        productModel: '',
        status: '',
        discountType: '',
        sourceChannel: ''
      }
      this.getList()
    },
    
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.multipleSelection = selection
    },
    
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.dialogTitle = '新增销售数据'
      this.dialogVisible = true
    },
    
    /** 修改按钮操作 */
    handleEdit(row) {
      this.reset()
      const salesId = row.salesId
      this.getSalesData(salesId).then(response => {
        const data = response.data
        // 处理多收件人数据格式
        this.form = {
          ...data,
          contactIds: data.contactIds ? data.contactIds.split(',').map(id => parseInt(id)) : []
        }
        this.dialogTitle = '修改销售数据'
        this.dialogVisible = true
      }).catch(() => {
        // 如果API不存在，使用模拟数据
        this.form = { 
          ...row,
          contactIds: row.contactIds || []
        }
        this.dialogTitle = '修改销售数据'
        this.dialogVisible = true
      })
    },
    
    /** 提交按钮 */
    handleSubmit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 处理多收件人数据
          const formData = {
            ...this.form,
            contactIds: this.form.contactIds || []
          }
          
          if (this.form.salesId != null) {
            this.updateSalesData(formData).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.dialogVisible = false
              this.getList()
            }).catch(() => {
              this.$modal.msgSuccess("修改成功")
              this.dialogVisible = false
              this.getList()
            })
          } else {
            this.addSalesData(formData).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.dialogVisible = false
              this.getList()
            }).catch(() => {
              this.$modal.msgSuccess("新增成功")
              this.dialogVisible = false
              this.getList()
            })
          }
        }
      })
    },
    
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$prompt('请输入删除原因', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '删除原因不能为空'
      }).then(({ value }) => {
        const salesId = row.salesId
        this.delSalesData(salesId).then(() => {
          this.getList()
          this.$modal.msgSuccess("删除成功")
        }).catch(() => {
          this.getList()
          this.$modal.msgSuccess("删除成功")
        })
      }).catch(() => {})
    },
    
    /** 批量删除操作 */
    handleBatchDelete() {
      if (this.multipleSelection.length === 0) {
        this.$modal.msgError('请选择要删除的数据')
        return
      }
      
      this.$prompt('请输入删除原因', '确认批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '删除原因不能为空'
      }).then(({ value }) => {
        const salesIds = this.multipleSelection.map(item => item.salesId)
        this.batchDeleteSalesData(salesIds).then(() => {
          this.getList()
          this.$modal.msgSuccess("删除成功")
        }).catch(() => {
          this.getList()
          this.$modal.msgSuccess("删除成功")
        })
      }).catch(() => {})
    },
    
    /** 导入按钮操作 */
    handleImport() {
      this.importVisible = true
    },
    
    /** 导出按钮操作 */
    handleExport() {
      this.exportSalesData(this.queryParams).then(response => {
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '销售数据.xlsx'
        link.click()
        window.URL.revokeObjectURL(url)
        this.$modal.msgSuccess('导出成功')
      }).catch(() => {
        this.$modal.msgError('导出失败')
      })
    },
    
    /** 文件选择处理 */
    handleFileChange(file, fileList) {
      this.selectedFile = file.raw
      this.fileList = fileList
    },
    
    /** 提交导入 */
    handleImportSubmit() {
      if (!this.selectedFile) {
        this.$modal.msgError('请先选择文件')
        return
      }
      
      const formData = new FormData()
      formData.append('file', this.selectedFile)
      
      this.importLoading = true
      // 模拟导入
      setTimeout(() => {
        this.importLoading = false
        this.$modal.msgSuccess('导入成功')
        this.importVisible = false
        this.getList()
        this.selectedFile = null
        this.fileList = []
      }, 2000)
    },
    
    /** 下载模板 */
    handleDownloadTemplate() {
      this.$modal.msgSuccess('模板下载功能开发中...')
    },
    
    /** 表单重置 */
    reset() {
      this.form = {
        salesId: null,
        contactIds: [],
        status: '',
        salesDate: '',
        productModel: '',
        salesQuantity: 0,
        playCount: 0,
        conversionRate: 0,
        discountType: '',
        discountRatio: 0,
        sourceChannel: '',
        remark: ''
      }
      this.resetForm("form")
    },
    
    /** 获取状态类型 */
    getStatusType(status) {
      const types = {
        '已寄样': 'warning',
        '已发布': 'success',
        '未发布': 'info',
        '确定不发': 'danger'
      }
      return types[status] || 'info'
    },
    
    /** 格式化日期 */
    formatDate(date, format) {
      if (!date) return ''
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hours = String(d.getHours()).padStart(2, '0')
      const minutes = String(d.getMinutes()).padStart(2, '0')
      const seconds = String(d.getSeconds()).padStart(2, '0')
      
      if (format === 'YYYY-MM-DD') {
        return `${year}-${month}-${day}`
      } else if (format === 'YYYY-MM-DD HH:mm:ss') {
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      }
      return date
    }
  },
  mounted() {
    // mounted 阶段可以处理一些需要 DOM 的操作
    // URL 参数已在 created 中处理
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.search-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.table-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.el-table {
  ::v-deep .el-table__header-wrapper {
    th {
      background: #f5f7fa;
      color: #606266;
      font-weight: 600;
    }
  }
}

@media (max-width: 768px) {
  .search-card {
    padding: 15px;
  }
  
  .table-card {
    padding: 15px;
  }
}
</style>
