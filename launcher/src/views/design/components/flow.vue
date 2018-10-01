<template>
  <div>
    <div id="wrapper">
      <strong style="display: block; margin-bottom: 30px;color: #99a9bf">{{ title }}</strong>
      <div v-for="(array,index) in steps" :key="index" class="line-wrap">
        <div v-for="step in array" :ref="step.index" :id="step.index" :key="step.index" class="state-item">{{ step.desc }}</div>
      </div>
    </div>
    <div id="editor">
      <div style="height: 240px;padding: 0 10px; margin: 10px 0; border: 1px solid #909399">
        <code style="line-height: 12px">
          <p>title:流程名称； steps:步骤集合； mapping:条件映射集合；</p>
          <p>index:步骤索引； next:下一步骤； to:转向步骤； desc:描述；</p>
          <p style="color: #409EFF"><strong>ref:映射后台原子服务 [OP]:</strong></p>
          <p> &nbsp;&nbsp;后台beanId，或者beanId:serviceId。</p>
          <p> &nbsp;&nbsp;举例："firstOP" "exceptionOP:0001"  "databaseOP:userInfoMapper/getAll"</p>
          <p style="color: #409EFF"><strong>condition:条件表达式:</strong></p>
          <p> &nbsp;&nbsp;@:变量前缀； and:与； or:或； !:非； eq:等于； uneq:不等于；</p>
          <p> &nbsp;&nbsp;举例："@var1 and @var2"  "!@var"  "@var1 eq var2"</p>
        </code>
      </div>
      <div id="buttons">
        <el-button type="primary" icon="el-icon-document" @click="opList">OP列表</el-button>
        <el-button type="warning" icon="el-icon-refresh" @click="preview">预览</el-button>
        <el-button type="success" icon="el-icon-setting" @click="save">保存</el-button>
      </div>
      <div class="editor-container">
        <json-editor ref="jsonEditor" v-model="flow"/>
      </div>
    </div>
    <OpDialog/>
  </div>
</template>
<script>
import { jsPlumb } from 'jsplumb'
import JsonEditor from '@/components/JsonEditor'
import bus from './bus'
import OpDialog from './OpDialog'
import request from '@/utils/request'

const defaultConfig = {
  // 对应上述基本概念
  anchor: ['Left', 'Right', 'Top', 'Bottom', [0.3, 0, 0, -1], [0.7, 0, 0, -1], [0.3, 1, 0, 1], [0.7, 1, 0, 1]],
  connector: ['Straight'],
  endpoint: 'Blank',
  // 添加样式
  paintStyle: { stroke: '#909399', strokeWidth: 1 }, // connector
  // endpointStyle: { fill: 'lightgray', outlineStroke: 'darkgray', outlineWidth: 2 } // endpoint
  // 添加 overlay，如箭头
  overlays: [
    ['Arrow', { width: 8, length: 8, location: 1 }],
    ['Label', { location: 0.3, id: 'label' }]
  ]
}
const instance = jsPlumb.getInstance()

export default {
  name: 'FlowDesigner',
  components: { JsonEditor, OpDialog },
  data() {
    return {
      title: '',
      flow: {},
      steps: [],
      flowPath: this.$route.query.serviceFlowPath
    }
  },
  mounted() {
    const vm = this
    request.post('/api/loadFlowConf', { serviceFlowPath: this.flowPath })
      .then(response => {
        this.flow = response
        this.title = response.title
        return this.translateFlow(response.steps)
      }).then(data => {
        instance.ready(function() {
          vm.showFlow(data)
        })
      })
  },
  methods: {
    translateFlow(steps) {
      this.steps = []
      const NUM = 2
      let array = []
      const lines = []
      for (const index in steps) {
        const step = steps[index]
        array.push(step)
        if (array.length === NUM) {
          this.steps.push(array)
          array = []
        }
        const arrows = step['mapping']
        for (const arrow of arrows) {
          if (index === arrow.to) {
            this.$message.error('不能自调用哦！！！')
            continue
          }
          lines.push({ index: index, to: arrow.to, desc: arrow.desc })
        }
        if (step.next) {
          if (index === step.next) {
            this.$message.error('不能自调用哦！！！')
            continue
          }
          lines.push({ index: index, to: step.next, desc: '下一步' })
        }
      }
      if (array.length > 0) {
        this.steps.push(array)
      }
      return lines
    },
    showFlow(lines) {
      for (const line of lines) {
        const connection = instance.connect({ source: line.index, target: line.to }, defaultConfig)
        connection.getOverlay('label').setLabel(line.desc)
      }
    },
    opList() {
      request.post('/api/loadOPList', {})
        .then(response => {
          bus.$emit('opList', response)
        })
    },
    preview() {
      Promise.resolve(this.flow)
        .then(data => {
          this.flow = JSON.parse(data)
          this.title = this.flow.title
          return this.translateFlow(this.flow.steps)
        })
        .then(data => {
          instance.reset()
          this.showFlow(data)
        })
    },
    save() {
      Promise.resolve(this.flow)
        .then(data => {
          this.flow = JSON.parse(data)
          this.title = this.flow.title
          return this.translateFlow(this.flow.steps)
        })
        .then(data => {
          instance.reset()
          this.showFlow(data)

          const param = Object.assign({}, JSON.parse(this.flow))
          param.path = this.flowPath
          request.post('/api/saveFlowConf', param)
            .then(response => {
              this.$message.success('保存流程配置成功！')
            })
        })
    }
  }
}
</script>
<style scoped>
  #buttons {
    height: 50px;
    width: 100%;
    text-align: center;
  }
  #buttons button {
    margin: 0 50px;
  }
  #wrapper {
    background:
      radial-gradient(
        ellipse at top left,
        rgba(255, 255, 255, 1) 40%,
        rgba(229, 229, 229, .9) 100%
      );
    height: 800px;
    padding: 10px 20px;
    width: 45vw;
    overflow-x: auto;
    display: inline-block;
  }
  .state-item {
    width: 150px;
    height: 60px;
    color: #606266;
    background: #f6f6f6;
    border: 2px solid rgba(0, 0, 0, 0.05);
    text-align: center;
    line-height: 60px;
    font-family: sans-serif;
    border-radius: 4px;
    margin: 0 90px 180px;
    padding: 0 10px;
    cursor: pointer;
  }
  .line-wrap {
    display: flex;
  }
  #editor {
    height: 800px;
    width: 50vw;
    display: inline-block;
  }
  .editor-container{
    overflow-y: auto;
    height: 500px;
  }
</style>
