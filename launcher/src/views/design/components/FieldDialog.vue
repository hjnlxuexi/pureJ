<template>
  <el-dialog v-el-drag-dialog :title="textMap[dialogType]" :visible.sync="dialogVisible" top="10px">
    <code style="line-height: 10px">
      <p>name:字段名称； targetName:目标名称； converter:字段转换器；</p>
      <p>type:字段类型 [S-string,I-integer,F-float,B-boolean,T-time,E-列表]；</p>
      <p>regexp:正则表达式； required:是否必须； desc:描述； children:列表子集</p>
    </code>
    <div class="editor-container">
      <json-editor ref="jsonEditor" v-model="text"/>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="doCancel">取消</el-button>
      <el-button v-if="dialogType=='request'" type="primary" @click="editRequestData">确定</el-button>
      <el-button v-else-if="dialogType=='response'" type="primary" @click="editResponseData">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import bus from './bus'
import elDragDialog from '@/directive/el-dragDialog'
import JsonEditor from '@/components/JsonEditor'

export default {
  name: 'FieldDialog',
  components: { JsonEditor },
  directives: { elDragDialog },
  data() {
    return {
      dialogType: 'request',
      dialogVisible: false,
      textMap: {
        request: '服务请求',
        response: '服务响应'
      },
      value: [],
      text: []
    }
  },
  mounted: function() {
    const vm = this
    bus.$on(['request', 'response'], (data, type) => {
      data = data || []
      if (type === 'request') {
        vm.text = vm.value = data
        vm.dialogType = 'request'
        vm.dialogVisible = true
      } else if (type === 'response') {
        vm.text = vm.value = data
        vm.dialogType = 'response'
        vm.dialogVisible = true
      }
    })
  },
  methods: {
    editRequestData() {
      if (this.value !== this.text) {
        this.value = JSON.parse(this.text)
      }
      bus.$emit('editRequestData', this.value, 'editRequestData')
      this.dialogVisible = false
    },
    editResponseData() {
      if (this.value !== this.text) {
        this.value = JSON.parse(this.text)
      }
      bus.$emit('editResponseData', this.value, 'editResponseData')
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
