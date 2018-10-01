<template>
  <div class="components-container">
    <split-pane :min-percent="15" :default-percent="15" split="vertical">
      <template slot="paneL">
        <div class="left-container">
          <tree @chooseService="chooseService"/>
        </div>
      </template>
      <template v-if="isShow" slot="paneR">
        <split-pane :min-percent="15" :default-percent="16" split="horizontal">
          <template slot="paneL">
            <div class="top-container">
              <div class="inline block">
                <el-tag class="el-tag-label">服务名称</el-tag>
                <el-input :value="service.name" placeholder="服务名称" class="inline input-text" readonly/>
              </div>
              <div class="inline block">
                <el-tag class="el-tag-label">服务路径</el-tag>
                <el-input :value="service.code" placeholder="服务路径" class="inline input-text" readonly/>
              </div>
              <div class="inline block">
                <el-tag class="el-tag-label">服务ID</el-tag>
                <el-input :value="service.id" placeholder="服务ID" class="inline input-text" readonly/>
              </div>
              <div class="inline block">
                <el-tag class="el-tag-label">服务类型</el-tag>
                <el-input :value="types[service.type]" placeholder="服务类型" class="inline input-text" readonly/>
              </div>
              <div class="inline" style="width: 66%">
                <el-tag class="el-tag-label" style="width: 12%">服务描述</el-tag>
                <el-input :value="service.desc" placeholder="服务描述" class="inline" style="width: 86%" readonly/>
              </div>
              <div style="text-align: center;line-height: 60px">
                <el-button style="margin: 0 20px" type="success" class="el-icon-setting" @click="test">测试</el-button>
                <el-button style="margin: 0 20px" class="el-icon-delete" @click="reset">清空</el-button>
                <el-button style="margin: 0 20px" type="primary" class="el-icon-document" @click="saveTestData">保存</el-button>
              </div>
            </div>
          </template>
          <template slot="paneR">
            <split-pane :min-percent="30" :default-percent="40" split="horizontal">
              <template slot="paneL">
                <div class="middle-container">
                  ||
                  <el-tag style="font-size: 16px;font-weight:800;color: #FFFFFF">服务请求</el-tag>
                  <el-button class="req-button" @click="handleEditRequestData">
                    {{ requestEdit ? '查看字段定义' : '输入请求数据 [json]' }}
                  </el-button>
                  <el-button type="primary" class="req-button" style="margin-left: 50px" @click="fillRequestTestData">
                    填充测试数据
                  </el-button>
                  <hr>
                  <json-editor v-show="requestEdit" ref="jsonReqEditor" v-model="req_text" style="height: 80%; overflow-y: auto" @changed="editRequest"/>
                  <tree-table v-show="!requestEdit" ref="requestTree" border style="margin: 5px 0;" expand-all/>
                </div>
              </template>
              <template slot="paneR">
                <div class="bottom-container">
                  ||
                  <el-tag style="font-size: 16px;font-weight:800;color: #FFFFFF">服务响应</el-tag>
                  <el-button class="req-button" @click="handleEditResponseData">
                    {{ responseEdit ? '查看字段定义' : '响应数据 [json]' }}
                  </el-button>
                  <el-button type="primary" class="req-button" style="margin-left: 50px" @click="fillResponseTestData">
                    填充测试数据
                  </el-button>
                  <hr>
                  <json-editor v-show="responseEdit" ref="jsonRespEditor" v-model="resp_text" style="height: 90%; overflow-y: auto"/>
                  <tree-table v-show="!responseEdit" ref="responseTree" border style="margin: 5px 0" expand-all/>
                </div>
              </template>
            </split-pane>
          </template>
        </split-pane>
      </template>
    </split-pane>
  </div>
</template>

<script>
import splitPane from 'vue-splitpane'
import tree from '@/components/Tree'
import treeTable from '@/components/FieldTreeTable'
import JsonEditor from '@/components/JsonEditor'
import request from '@/utils/request'
import requestRaw from '@/utils/requestRaw'

export default {
  name: 'UnitTest',
  components: {
    splitPane,
    tree,
    treeTable,
    JsonEditor
  },
  data() {
    return {
      isShow: true,
      requestEdit: true,
      responseEdit: true,
      service: {},
      types: {
        'database': '数据库',
        'single': '原子服务',
        'flow': '流程服务',
        'protocol': '外部接口'
      },
      req_value: {},
      req_text: {},
      resp_value: {},
      resp_text: {}
    }
  },
  methods: {
    chooseService(service) {
      this.service = {}
      this.$set(this.service, 'code', service.code)
      this.$set(this.service, 'name', service.label)
      request.post('/api/loadServiceConf', { service: this.service.code })
        .then(response => {
          this.$set(this.service, 'name', response.name)
          this.$set(this.service, 'type', response.type)
          this.$set(this.service, 'id', response.id)
          this.$set(this.service, 'desc', response.desc)
          this.$set(this.service, 'input', response.input)
          this.$set(this.service, 'output', response.output)
          this.$refs['requestTree'].freshData(this.service.input)
          this.$refs['responseTree'].freshData(this.service.output)
          return response
        })
        .then(data => {
          const request = data.input
          if (this.req_value.header && this.service.code === this.req_value.header.service) {
            return
          }
          const fields = {}
          this.setRequestValue(request, fields)
          this.req_text = this.req_value = this.buildPocket(fields)
          this.resp_text = this.resp_value = {}
        })
    },
    setRequestValue(request, data) {
      for (const field of request) {
        if (field.type !== 'E') {
          data[field.name] = ''
        } else {
          const list = []
          const first = {}
          const fields = field.children
          this.setRequestValue(fields, first)
          list.push(first)
          data[field.name] = list
        }
      }
    },
    handleEditRequestData() {
      this.requestEdit = !this.requestEdit
    },
    handleEditResponseData() {
      this.responseEdit = !this.responseEdit
    },
    fillRequestTestData() {
      if (!this.service.code) return
      requestRaw.post('/getJson', { jsonPath: 'channel/input/' + this.service.code + '.json' })
        .then(response => {
          if (!response.status) {
            this.$message.error(response.msg)
            return
          }
          this.req_text = this.req_value = response.data
        })
    },
    fillResponseTestData() {
      if (!this.service.code) return
      requestRaw.post('/getJson', { jsonPath: 'channel/output/' + this.service.code + '.json' })
        .then(response => {
          if (!response.status) {
            this.$message.error(response.msg)
            return
          }
          this.resp_text = this.resp_value = response.data
        })
    },
    test() {
      requestRaw.post('/api', this.req_value)
        .then(response => {
          this.resp_text = this.resp_value = response
        })
    },
    reset() {
      if (this.req_value.body) this.req_value.body = {}
      this.req_text = this.req_value
      this.resp_text = this.resp_value = {}
    },
    saveTestData() {
      if (!this.service.code) return
      // 保存请求数据
      if (this.req_value.body) {
        requestRaw.post('/saveJson', { jsonPath: 'channel/input/' + this.service.code + '.json', content: this.req_text })
          .then(response => {
            if (!response.status) {
              this.$message.error(response.msg)
            } else {
              this.$message.success('保存请求数据成功！')
            }
          })
      }
      // 保存响应数据
      if (this.resp_value.body) {
        requestRaw.post('/saveJson', { jsonPath: 'channel/output/' + this.service.code + '.json', content: this.resp_text })
          .then(response => {
            if (!response.status) {
              this.$message.error(response.msg)
            } else {
              this.$message.success('保存响应数据成功！')
            }
          })
      }
    },
    buildPocket(data) {
      const pocket = {}
      pocket.header = { service: this.service.code }
      pocket.body = data
      return pocket
    },
    editRequest(data) {
      this.req_value = JSON.parse(data)
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
  .block {
    text-align: left;
    width: 33%;
  }

  .input-text {
    width: 70%;
  }

  .el-tag-label {
    font-size: 16px;
    font-weight: 800;
    color: #FFFFFF;
    margin-right: 2px;
    display: inline-block;
    width: 24%;
    text-align: center;
    margin-top: 10px;
  }
  .req-button {
    margin-top: 10px;
    padding: 0 20px;
    line-height: 30px
  }
</style>
