<template>
  <el-dialog v-el-drag-dialog :title="'字段转换器'" :visible.sync="dialogVisible" top="10px">
    <code style="line-height: 10px">
      <p>name:转换器名称； rules:转换规则集合； 规则为 key:value 键值对；</p>
      <p>常量字符串："_null_" 表示空值， key 和 value 都可以为空值</p>
    </code>
    <div class="editor-container">
      <json-editor ref="jsonEditor" v-model="text"/>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="doCancel">取消</el-button>
      <el-button type="primary" @click="editConverterData">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import bus from './bus'
import elDragDialog from '@/directive/el-dragDialog'
import JsonEditor from '@/components/JsonEditor'

export default {
  name: 'ConverterDialog',
  components: { JsonEditor },
  directives: { elDragDialog },
  data() {
    return {
      dialogVisible: false,
      value: [],
      text: []
    }
  },
  mounted: function() {
    const vm = this
    bus.$on(['converter'], (data) => {
      vm.text = vm.value = data || []
      vm.dialogVisible = true
    })
  },
  methods: {
    editConverterData() {
      if (this.value !== this.text) {
        this.value = JSON.parse(this.text)
      }
      bus.$emit('editConverterData', this.value, 'editConverterData')
      this.dialogVisible = false
    },
    doCancel() {
      this.dialogVisible = false
    }
  }
}
</script>

<style>
  .editor-container{
    position: relative;
    overflow-y: auto;
    height: 480px;
  }
  .el-dialog__body{
    padding: 0 20px;
  }
</style>
