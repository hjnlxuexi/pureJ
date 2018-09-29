<template>
  <el-dialog v-el-drag-dialog :title="'OP列表'" :visible.sync="dialogVisible" width="600px">
    <hr>
    <p><strong>系统OP</strong></p>
    <div style="height: 100px;line-height: 100px;padding: 0 20px; text-align: center">
      <el-button v-for="(op, index) in reservedOPList" :key="index" type="danger" style="width: 30%">{{ op }}</el-button>
    </div>
    <p><strong>自定义OP</strong></p>
    <div style="min-height: 100px;max-height: 400px;overflow-y: auto;line-height: 60px;padding: 0 20px; border: solid 1px #409EFF;">
      <el-button v-for="(op, index) in customOPList" :key="index" type="primary" style="min-width: 22%;margin-left: 10px">{{ op }}</el-button>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="doCancel">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import bus from './bus'
import elDragDialog from '@/directive/el-dragDialog'

export default {
  name: 'OpDialog',
  directives: { elDragDialog },
  data() {
    return {
      dialogVisible: false,
      reservedOPList: [],
      customOPList: []
    }
  },
  mounted: function() {
    const vm = this
    bus.$on(['opList'], (data) => {
      vm.reservedOPList = data.reservedList
      vm.customOPList = data.customList
      vm.dialogVisible = true
    })
  },
  methods: {
    doCancel() {
      this.dialogVisible = false
    }
  }
}
</script>

<style>
  .el-dialog__body{
    padding: 0 20px;
  }
</style>
