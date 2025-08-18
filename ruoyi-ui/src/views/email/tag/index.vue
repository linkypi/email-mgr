<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>标签管理</span>
        <el-button style="float: right" type="primary" size="small" @click="handleAdd">
          新增标签
        </el-button>
      </div>
      
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="标签名称">
          <el-input v-model="queryParams.tagName" placeholder="请输入标签名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 标签列表 -->
      <el-table :data="tagList" style="width: 100%">
        <el-table-column prop="tagName" label="标签名称" width="150"></el-table-column>
        <el-table-column prop="contactCount" label="联系人数量" width="120"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="form" label-width="80px">
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="form.tagName" placeholder="请输入标签名称"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" placeholder="请输入描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Tag',
  data() {
    return {
      queryParams: {
        tagName: ''
      },
      tagList: [
        {
          tagId: 1,
          tagName: '重要',
          contactCount: 15,
          description: '重要客户标签',
          createTime: '2024-01-15 10:30:00'
        },
        {
          tagId: 2,
          tagName: '项目',
          contactCount: 8,
          description: '项目相关联系人',
          createTime: '2024-01-14 16:20:00'
        },
        {
          tagId: 3,
          tagName: 'VIP',
          contactCount: 12,
          description: 'VIP客户标签',
          createTime: '2024-01-13 09:15:00'
        },
        {
          tagId: 4,
          tagName: '待处理',
          contactCount: 5,
          description: '需要跟进的联系人',
          createTime: '2024-01-12 14:30:00'
        }
      ],
      dialogVisible: false,
      dialogTitle: '新增标签',
      form: {
        tagName: '',
        description: ''
      },
      rules: {
        tagName: [
          { required: true, message: '请输入标签名称', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleSearch() {
      this.$message.info('搜索功能待实现')
    },
    handleReset() {
      this.queryParams = {
        tagName: ''
      }
    },
    handleAdd() {
      this.dialogTitle = '新增标签'
      this.form = {
        tagName: '',
        description: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑标签'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确认删除该标签吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('删除成功')
      })
    },
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$message.success('保存成功')
          this.dialogVisible = false
        }
      })
    }
  }
}
</script>
