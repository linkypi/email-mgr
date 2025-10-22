<template>
  <div class="rich-text-editor">
    <div ref="toolbar" class="ql-toolbar ql-snow">
      <!-- 第一行工具栏 -->
      <div class="toolbar-row">
        <button type="button" class="ql-undo" title="撤销">
          <svg viewBox="0 0 18 18">
            <polyline class="ql-even ql-stroke" points="6 10 4 12 6 14"></polyline>
            <path class="ql-stroke" d="m8,12h8"></path>
          </svg>
        </button>
        <button type="button" class="ql-redo" title="重做">
          <svg viewBox="0 0 18 18">
            <polyline class="ql-even ql-stroke" points="12 10 14 12 12 14"></polyline>
            <path class="ql-stroke" d="m10,12h-8"></path>
          </svg>
        </button>
        
        <span class="ql-formats">
          <button type="button" class="ql-image" title="图片"></button>
          <button type="button" class="ql-link" title="链接"></button>
          <button type="button" class="ql-video" title="视频"></button>
        </span>
        
        <span class="ql-formats">
          <button type="button" class="ql-clean" title="清除格式"></button>
        </span>
      </div>
      
      <!-- 第二行工具栏 -->
      <div class="toolbar-row">
        <span class="ql-formats">
          <select class="ql-font" title="字体">
            <option value="SimSun">宋体</option>
            <option value="SimHei">黑体</option>
            <option value="Microsoft YaHei">微软雅黑</option>
            <option value="KaiTi">楷体</option>
            <option value="FangSong">仿宋</option>
            <option value="Arial">Arial</option>
            <option value="Times New Roman">Times New Roman</option>
            <option value="Courier New">Courier New</option>
          </select>
          <select class="ql-size" title="字号">
            <option value="10px">10px</option>
            <option value="12px">12px</option>
            <option value="14px" selected>14px</option>
            <option value="16px">16px</option>
            <option value="18px">18px</option>
            <option value="20px">20px</option>
            <option value="24px">24px</option>
            <option value="32px">32px</option>
          </select>
        </span>
        
        <span class="ql-formats">
          <button type="button" class="ql-bold" title="粗体"></button>
          <button type="button" class="ql-italic" title="斜体"></button>
          <button type="button" class="ql-underline" title="下划线"></button>
          <button type="button" class="ql-strike" title="删除线"></button>
        </span>
        
        <span class="ql-formats">
          <select class="ql-color" title="文字颜色"></select>
          <select class="ql-background" title="背景色"></select>
        </span>
        
        <span class="ql-formats">
          <button type="button" class="ql-align" value="" title="左对齐"></button>
          <button type="button" class="ql-align" value="center" title="居中"></button>
          <button type="button" class="ql-align" value="right" title="右对齐"></button>
          <button type="button" class="ql-align" value="justify" title="两端对齐"></button>
        </span>
        
        <span class="ql-formats">
          <button type="button" class="ql-list" value="ordered" title="有序列表"></button>
          <button type="button" class="ql-list" value="bullet" title="无序列表"></button>
          <button type="button" class="ql-indent" value="-1" title="减少缩进"></button>
          <button type="button" class="ql-indent" value="+1" title="增加缩进"></button>
        </span>
        
        <span class="ql-formats">
          <button type="button" class="ql-blockquote" title="引用"></button>
          <button type="button" class="ql-code-block" title="代码块"></button>
        </span>
      </div>
    </div>
    
    <div ref="editor" class="ql-container ql-snow"></div>
  </div>
</template>

<script>
import Quill from 'quill'
import 'quill/dist/quill.snow.css'

export default {
  name: 'RichTextEditor',
  props: {
    value: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: '请输入内容...'
    },
    height: {
      type: String,
      default: '200px'
    }
  },
  data() {
    return {
      quill: null
    }
  },
  mounted() {
    this.initQuill()
  },
  beforeDestroy() {
    if (this.quill) {
      this.quill = null
    }
  },
  methods: {
    initQuill() {
      const options = {
        theme: 'snow',
        placeholder: this.placeholder,
        modules: {
          toolbar: this.$refs.toolbar
        }
      }
      
      this.quill = new Quill(this.$refs.editor, options)
      
      // 设置初始内容
      if (this.value) {
        this.quill.root.innerHTML = this.value
      }
      
      // 监听内容变化
      this.quill.on('text-change', () => {
        const html = this.quill.root.innerHTML
        this.$emit('input', html)
        this.$emit('change', html)
      })
      
      // 设置编辑器高度
      this.$refs.editor.style.height = this.height
    },
    
    // 获取内容
    getContent() {
      return this.quill ? this.quill.root.innerHTML : ''
    },
    
    // 设置内容
    setContent(content) {
      if (this.quill) {
        this.quill.root.innerHTML = content
      }
    },
    
    // 清空内容
    clear() {
      if (this.quill) {
        this.quill.setText('')
      }
    },
    
    // 获取纯文本内容
    getText() {
      return this.quill ? this.quill.getText() : ''
    }
  },
  watch: {
    value(newVal) {
      if (this.quill && this.quill.root.innerHTML !== newVal) {
        this.quill.root.innerHTML = newVal
      }
    }
  }
}
</script>

<style scoped>
.rich-text-editor {
  border: 1px solid #e0e6ed;
  border-radius: 4px;
  overflow: hidden;
}

.ql-toolbar {
  border-bottom: 1px solid #e0e6ed;
  background: #fafafa;
}

.toolbar-row {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid #e0e6ed;
}

.toolbar-row:last-child {
  border-bottom: none;
}

.ql-formats {
  display: inline-flex;
  align-items: center;
  margin-right: 15px;
}

.ql-formats:last-child {
  margin-right: 0;
}

.ql-container {
  border: none;
  font-size: 14px;
  line-height: 1.6;
}

.ql-editor {
  padding: 15px;
  min-height: 120px;
}

.ql-editor.ql-blank::before {
  color: #999;
  font-style: normal;
}

/* 自定义工具栏按钮样式 */
.ql-toolbar button {
  width: 28px;
  height: 28px;
  margin: 0 2px;
  border: 1px solid transparent;
  border-radius: 3px;
  background: transparent;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.ql-toolbar button:hover {
  background: #e6f7ff;
  border-color: #91d5ff;
}

.ql-toolbar button.ql-active {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}

.ql-toolbar select {
  height: 28px;
  border: 1px solid #d9d9d9;
  border-radius: 3px;
  background: white;
  font-size: 12px;
  padding: 0 8px;
  margin: 0 2px;
}

.ql-toolbar select:hover {
  border-color: #40a9ff;
}

/* 颜色选择器样式 */
.ql-color .ql-picker-options,
.ql-background .ql-picker-options {
  width: 152px;
}

.ql-color .ql-picker-item,
.ql-background .ql-picker-item {
  width: 16px;
  height: 16px;
  margin: 2px;
  border: 1px solid #ccc;
}
</style>
