<template>
  <el-dialog v-el-drag-dialog :title="'创建应用'" :visible.sync="dialogVisible" top="10px">
    <div style="line-height: 50px">
      <el-input v-model="value.name" placeholder="应用名称" class="input-text"/>
      <el-input v-model="value.code" placeholder="应用编码" class="input-text"/>
      <el-input v-model="value.projectDir" placeholder="项目路径" class="input-text" @change="changeDir"/>
      <el-input v-model="value.appBizDir" placeholder="业务配置根路径" class="input-text"/>
      <el-input v-model="value.baseDir" placeholder="开发数据根路径" class="input-text"/>
      <el-input v-model="value.port" placeholder="端口号" class="input-text"/>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="doCancel">取消</el-button>
      <el-button type="primary" @click="createApp">确定</el-button>
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
      value: {},
      text: {}
    }
  },
  mounted: function() {
    const vm = this
    bus.$on(['app'], (data) => {
      vm.text = vm.value = data || []
      vm.dialogVisible = true
    })
  },
  methods: {
    createApp() {
      if (this.value !== this.text) {
        this.value = JSON.parse(this.text)
      }
      this.value.projectDir = this.value.projectDir.endsWith('/') ? this.value.projectDir : this.value.projectDir + '/'
      this.value.appBizDir = this.value.appBizDir.endsWith('/') ? this.value.appBizDir : this.value.appBizDir + '/'
      this.value.baseDir = this.value.baseDir.endsWith('/') ? this.value.baseDir : this.value.baseDir + '/'
      this.value.appUrl = 'http://localhost:' + this.value.port + '/' + this.value.code

      bus.$emit('createApp', this.value)
      this.dialogVisible = false
    },
    doCancel() {
      this.dialogVisible = false
    },
    changeDir() {
      this.value.projectDir = this.value.projectDir.endsWith('/') ? this.value.projectDir : this.value.projectDir + '/'
      this.value.appBizDir = this.value.projectDir + 'biz/'
      this.value.baseDir = this.value.projectDir + 'data/'
    }
  }
}
</script>
