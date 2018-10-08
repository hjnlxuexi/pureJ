<template>
  <div class="board">
    <div class="tag">
      <div class="words">
        <strong>应用编码: </strong>
        {{ currentApp.code }}
      </div>
      <div class="words">
        <strong>应用名称: </strong>
        {{ currentApp.name }}
      </div>
    </div>
    <div id="design" class="cell design" @click="$router.push('/workbench/design/service')">
      <svg-icon icon-class="service"/> 服务设计
    </div>
    <div id="test" class="cell test" @click="$router.push('/workbench/test/unit-test')">
      <svg-icon icon-class="monkey"/> 服务测试
    </div>
    <div id="api" class="cell api" @click="$message.warning('TODO 计划中')">
      <svg-icon icon-class="documentation"/> API生成
    </div>
    <div id="regress" class="cell regress" @click="$message.warning('Working 开发中')">
      <svg-icon icon-class="test"/> 回归测试
    </div>
    <div id="integration" class="cell integration" @click="$message.warning('TODO 计划中')">
      <svg-icon icon-class="integration"/> 集成测试
    </div>
    <div id="report" class="cell report" @click="$message.warning('TODO 计划中')">
      <svg-icon icon-class="report"/> 测试&进度
    </div>
  </div>
</template>
<script>
import { jsPlumb } from 'jsplumb'
import requestRaw from '@/utils/requestRaw'
const defaultConfig = {
  anchor: ['Left', 'Right', 'Top', 'Bottom', 'BottomCenter', 'LeftMiddle', 'RightMiddle', 'TopCenter'],
  connector: ['StateMachine'],
  endpoint: 'Blank',
  // 添加样式
  paintStyle: { stroke: '#909399', strokeWidth: 3 }
}

export default {
  name: 'Capacity',
  data() {
    return {
      jsPlumbInstance: {},
      currentApp: {}
    }
  },
  created() {
    this.jsPlumbInstance = jsPlumb.getInstance()
    requestRaw.post('/activeApp', {})
      .then(response => {
        this.currentApp = response
      })
  },
  mounted() {
    const lines = [
      { source: 'design', target: 'test' },
      { source: 'design', target: 'api' },
      { source: 'test', target: 'regress' },
      { source: 'test', target: 'integration' },
      { source: 'integration', target: 'report' }
    ]
    const vm = this
    this.jsPlumbInstance.ready(function() {
      for (const line of lines) {
        vm.jsPlumbInstance.connect(line, defaultConfig)
      }
    })
  },
  beforeDestroy() {
    this.jsPlumbInstance.reset()
  }
}
</script>
<style scoped>
  .board {
    margin: 10px;
    font-weight: 800;
    font-size: 20px;
  }
  .board .tag {
    text-align: right;
    position: fixed;
    top: 80px;
    right: 30px;
    border: solid 1px #97a8be;
    padding: 10px 10px;
  }
  .board .tag .words {
    text-align: left;
    min-width: 150px;
    color: #97a8be;
    font-size: 14px;
    line-height: 30px;
    font-weight: 400;
  }
  .board .cell {
    width: 150px;
    height: 150px;
    padding: 10px 10px;
    border: solid 1px;
    border-radius: 150px;
    line-height: 130px;
    text-align: center;
    display: inline-block;
    position: absolute;
    cursor: pointer;
  }
  .design {
    background-color: #3a71a8;
    border-color: #3a71a8;
    color: white;
    left: 50px;
    top: 180px;
  }
  .test {
    background-color: #30b08f;
    border-color: #30b08f;
    color: white;
    top: 380px;
    left: 300px;
  }
  .api {
    background-color: #e65d6e;
    border-color: #e65d6e;
    color: white;
    left: 280px;
  }
  .regress {
    background-color: #409eff;
    border-color: #409eff;
    color: white;
    top: 200px;
    left: 660px;
  }
  .integration {
    background-color: #000;
    border-color: #000;
    color: white;
    top: 480px;
    left: 650px;
  }
  .report {
    background-color: #f9944a;
    border-color: #f9944a;
    color: white;
    top: 350px;
    left: 950px;
  }
</style>

