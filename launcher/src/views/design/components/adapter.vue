<template>
  <div class="components-container">
    <split-pane :min-percent="15" :default-percent="18" split="horizontal">
      <template slot="paneL">
        <div class="top-container">
          <el-input v-model="adapter.name" placeholder="适配器名称" class="inline prop"/>
          <el-input v-model="adapter.host" placeholder="适配器服务地址" class="inline prop"/>
          <el-input v-model="adapter.service" placeholder="服务路径" class="inline prop"/>
          <el-select v-model="adapter.netTool" placeholder="选择适配器处理工具" class="filter-item prop">
            <el-option v-for="item in defaults.netTools" :key="item" :label="item" :value="item"/>
          </el-select>
          <el-input v-model="adapter.connectTimeout" placeholder="连接超时时间(ms)" class="inline prop"/>
          <el-input v-model="adapter.responseTimeout" placeholder="响应超时时间(ms)" class="inline prop"/>
          <el-input v-model="adapter.charset" placeholder="字符集" class="inline prop"/>
          <el-input v-model="adapter.contentType" placeholder="数据格式ContentType (仅http)" class="inline prop"/>
          <el-button type="success" @click="saveAdapterConf">保存</el-button>
        </div>
      </template>
      <template slot="paneR">
        <split-pane :min-percent="20" :default-percent="32" split="horizontal">
          <template slot="paneL">
            <div class="middle-container">
              ||<el-tag style="font-size: 16px;font-weight:800;color: #FFFFFF">服务请求</el-tag>
              <el-button class="primary" style="padding: 0 25px;line-height: 20px" @click="handleEditRequestData">编辑[json]</el-button>
              <tree-table ref="requestTree" border style="margin: 5px 0" expand-all/>
            </div>
          </template>
          <template slot="paneR">
            <div class="bottom-container">
              ||<el-tag style="font-size: 16px;font-weight:800;color: #FFFFFF">服务响应</el-tag>
              <el-button class="primary" style="padding: 0 25px;line-height: 20px" @click="handleEditResponseData">编辑[json]</el-button>
              <tree-table ref="responseTree" border style="margin: 5px 0" expand-all/>
            </div>
          </template>
        </split-pane>
      </template>
    </split-pane>
    <!--对话框-->
    <FieldDialog/>
  </div>
</template>

<script>
import splitPane from 'vue-splitpane'
import FieldDialog from './FieldDialog'
import treeTable from '@/components/FieldTreeTable'
import bus from './bus'
import request from '@/utils/request'

export default {
  name: 'AdapterDesigner',
  components: {
    splitPane,
    FieldDialog,
    treeTable
  },
  data() {
    return {
      path: this.$route.query.adapterPath,
      adapter: {},
      defaults: {}
    }
  },
  created() {
    request.post('/api', { _path: '/loadAdapterDefaults' })
      .then(response => {
        this.defaults = response
      })
      .then(data => {
        request.post('/api', { adapter: this.path, _path: '/loadAdapterConf' })
          .then(response => {
            this.adapter = response
            this.adapter.host = this.adapter.host || this.defaults.host
            this.adapter.connectTimeout = this.adapter.connectTimeout || this.defaults.connectTimeout
            this.adapter.responseTimeout = this.adapter.responseTimeout || this.defaults.responseTimeout
            this.adapter.charset = this.adapter.charset || this.defaults.charset
            this.$refs['requestTree'].freshData(this.adapter.input)
            this.$refs['responseTree'].freshData(this.adapter.output)
          })
      })
  },
  mounted: function() {
    const vm = this
    bus.$on(['editRequestData', 'editResponseData'], (data, type) => {
      if (type === 'editRequestData') {
        vm.adapter.input = data
        vm.$refs['requestTree'].freshData(data)
      } else if (type === 'editResponseData') {
        vm.adapter.output = data
        this.$refs['responseTree'].freshData(data)
      }
    })
  },
  methods: {
    handleEditRequestData() {
      bus.$emit('request', this.adapter.input, 'request')
    },
    handleEditResponseData() {
      bus.$emit('response', this.adapter.output, 'response')
    },
    saveAdapterConf() {
      const params = Object.assign({}, this.adapter)
      params.path = this.path
      params['_path'] = '/saveAdapterConf'
      request.post('/api', params)
        .then(response => {
          this.$message.success('保存适配器配置成功')
        })
    }
  }
}
</script>

<style scoped>
  .components-container {
    position: relative;
    height: 150vh;
  }

  .top-container {
    background-color: rgb(48, 65, 86);
    height: 100%;
    line-height: 45px;
    padding: 5px 10px;
    overflow-y: auto;
  }

  .middle-container {
    background-color: #71A3BA;
    height: 100%;
    overflow-y: auto;
    padding: 0 10px;
  }

  .bottom-container {
    padding: 0 10px;
    background-color: darkgray;
    height: 100%;
    overflow-y: auto;
  }

  .inline {
    display: inline-block;
  }
  .prop {
    width: 33%;
  }
</style>
