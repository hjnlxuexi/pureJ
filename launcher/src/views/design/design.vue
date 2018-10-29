<template>
  <div class="components-container">
    <split-pane :min-percent="15" :default-percent="18" split="vertical">
      <template slot="paneL">
        <div class="left-container">
          <tree :designer="true" @chooseService="chooseService"/>
        </div>
      </template>
      <template v-if="isShow" slot="paneR">
        <split-pane :min-percent="0" :default-percent="18" split="horizontal">
          <template slot="paneL">
            <div class="top-container">
              <el-input v-model="service.name" placeholder="服务名称" class="inline half"/>
              <el-input v-model="service.code" placeholder="服务路径" class="inline half" readonly/>
              <el-input v-model="service.desc" placeholder="服务描述" class="inline" />
              <el-input v-model="service.id" placeholder="服务ID" class="inline" />
              <div class="inline" style="text-align: left">
                <el-tag style="font-size: 16px;font-weight:800;color: #FFFFFF;margin-right: 10px">服务类型</el-tag>
              </div>
              <div class="inline" style="background-color: white;padding: 0 10px;line-height: 35px">[
                <el-radio-group v-model="service.type">
                  <el-radio v-for="item in types" :label="item.value" :key="item.value">
                    {{ item.name }}
                  </el-radio>
                </el-radio-group> ]
              </div>
              <div v-if="service.id && service.type=='flow'" class="inline" style="background-color: white;padding: 0 10px;line-height: 35px">[
                <router-link :target="'_blank'" :to="'/workbench/design/flow?serviceFlowPath='+service.id" class="pan-btn light-blue-btn" style="padding: 0 36px;line-height: 20px">流程设计</router-link> ]
              </div>
              <div v-if="service.id && service.type=='protocol'" class="inline" style="background-color: white;padding: 0 10px;line-height: 35px">[
                <router-link :target="'_blank'" :to="'/workbench/design/adapter?adapterPath='+service.id" class="pan-btn light-blue-btn" style="padding: 0 36px;line-height: 20px">适配器定义</router-link> ]
              </div>
              <el-button style="margin-left: 20px" type="success" class="el-icon-setting" @click="saveServiceConf">保存</el-button>
            </div>
          </template>
          <template slot="paneR">
            <split-pane :min-percent="0" :default-percent="32" split="horizontal">
              <template slot="paneL">
                <div class="middle-container">
                  ||<el-tag style="font-size: 16px;font-weight:800;color: #FFFFFF">服务请求</el-tag>
                  <el-button style="padding: 0 25px;line-height: 20px" @click="handleEditRequestData">编辑[json]</el-button>
                  <el-button style="padding: 0 25px;line-height: 20px" @click="handleEditConverters">管理字段转换器</el-button>
                  <tree-table ref="requestTree" border style="margin: 5px 0" expand-all/>
                </div>
              </template>
              <template slot="paneR">
                <div class="bottom-container">
                  ||<el-tag style="font-size: 16px;font-weight:800;color: #FFFFFF">服务响应</el-tag>
                  <el-button style="padding: 0 25px;line-height: 20px" @click="handleEditResponseData">编辑[json]</el-button>
                  <tree-table ref="responseTree" border style="margin: 5px 0" expand-all/>
                </div>
              </template>
            </split-pane>
          </template>
        </split-pane>
      </template>
    </split-pane>
    <!--对话框-->
    <FieldDialog/>
    <ConverterDialog/>
  </div>
</template>

<script>
import splitPane from 'vue-splitpane'
import tree from '@/components/Tree'
import FieldDialog from './components/FieldDialog'
import treeTable from '@/components/FieldTreeTable'
import bus from './components/bus'
import request from '@/utils/request'
import ConverterDialog from './components/ConverterDialog'

export default {
  name: 'Design',
  components: {
    splitPane,
    tree,
    FieldDialog,
    treeTable,
    ConverterDialog
  },
  data() {
    return {
      isShow: true,
      service: {},
      types: [
        { value: 'database', name: '数据库' },
        { value: 'single', name: '原子服务' },
        { value: 'flow', name: '流程服务' },
        { value: 'protocol', name: '外部接口' }
      ],
      converters: []
    }
  },
  created() {
    request.post('/api', { _path: '/loadConverters' })
      .then(response => {
        this.converters = response.converters
      })
  },
  mounted: function() {
    const vm = this
    bus.$on(['editRequestData', 'editResponseData', 'editConverterData'], (data, type) => {
      if (type === 'editRequestData') {
        vm.service.input = data
        vm.$refs['requestTree'].freshData(data)
      } else if (type === 'editResponseData') {
        vm.service.output = data
        this.$refs['responseTree'].freshData(data)
      } else if (type === 'editConverterData') {
        vm.converters = data
        const params = {}
        params.converters = data
        params['_path'] = '/saveConverterConf'
        request.post('/api', params)
          .then(response => {
            this.$message.success('保存字段转换器成功')
          })
      }
    })
  },
  methods: {
    chooseService(service) {
      this.service = {}
      this.$set(this.service, 'code', service.code)
      this.$set(this.service, 'name', service.label)
      request.post('/api', { service: this.service.code, _path: '/loadServiceConf' })
        .then(response => {
          this.$set(this.service, 'name', response.name)
          this.$set(this.service, 'type', response.type)
          this.$set(this.service, 'id', response.id)
          this.$set(this.service, 'desc', response.desc)
          this.$set(this.service, 'input', response.input)
          this.$set(this.service, 'output', response.output)
          this.$refs['requestTree'].freshData(this.service.input)
          this.$refs['responseTree'].freshData(this.service.output)
        })
    },
    handleEditRequestData() {
      bus.$emit('request', this.service.input, 'request')
    },
    handleEditResponseData() {
      bus.$emit('response', this.service.output, 'response')
    },
    handleEditConverters() {
      bus.$emit('converter', this.converters)
    },
    saveServiceConf() {
      if (!this.service.id) {
        this.$message.error('服务ID不能为空')
        return
      }
      const param = Object.assign({}, this.service)
      param['_path'] = '/saveServiceConf'
      request.post('/api', param)
        .then(response => {
          this.$message.success('保存服务配置成功')
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

  .left-container {
    background-color: #71A3BA;
    height: 100%;
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
  .half {
    width: 49.7%;
  }
</style>
