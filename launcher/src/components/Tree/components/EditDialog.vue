<template>
  <el-dialog v-el-drag-dialog :title="'服务目录'" :visible.sync="dialogVisible" top="10px">
    <code style="line-height: 10px">
      <p>label: 菜单名称； code: 菜单路径； children: 子菜单</p>
    </code>
    <div class="editor-container">
      <json-editor ref="jsonEditor" v-model="text"/>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="doCancel">取消</el-button>
      <el-button type="primary" @click="editMenuData">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import bus from './bus'
import elDragDialog from '@/directive/el-dragDialog'
import JsonEditor from '@/components/JsonEditor'

export default {
  name: 'EditDialog',
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
    bus.$on(['menu'], (data) => {
      vm.text = vm.value = data || []
      vm.dialogVisible = true
    })
  },
  methods: {
    editMenuData() {
      if (this.value !== this.text) {
        this.value = JSON.parse(this.text)
      }
      bus.$emit('editMenuData', this.value)
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
