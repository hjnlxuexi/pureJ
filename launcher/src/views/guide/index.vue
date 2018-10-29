<template>
  <div style="text-align: center">
    <div style="margin: 20px">
      <span style="font-size: 100px;font-weight: bold;color: #2ac06d;font-family: fantasy">W</span>
      <span style="color: #97a8be;font-size: 50px">elcome &nbsp;&nbsp; to &nbsp;&nbsp; </span>
      <span style="color: #97a8be;font-size: 50px">pure</span>
      <span style="font-size: 100px;font-weight: bold;color: #2ac06d;font-family: fantasy">J</span>
    </div>
    <div style="margin: 20px 20px 0 20px">
      <span style="font-size: 100px;font-weight: bold;color: #2ac06d;font-family: fantasy">Y</span>
      <span style="color: #97a8be;font-size: 50px">es, all the stories begin at Hello World.</span>
    </div>
    <div style="text-align: center;color: #eee; font-size: 10px;">
      <span style="margin-left: 800px">
        ————鲁迅
        <img src="/src/assets/lx.png" height="12">
      </span>
    </div>
    <div style="margin-top: 60px">
      <span style="color: #97a8be;font-size: 20px">输入你项目的位置，带你搅</span>
      <span style="font-size: 50px;font-weight: bold;color: #66b1ff;font-family: fantasy">S</span>
      <span style="color: #97a8be;font-size: 20px">hi ~</span>
    </div>
    <div>
      <el-input ref="projectDir" v-model="currentApp.projectDir" placeholder="输入你项目的位置,如:/Users/home/Desktop/HJ/pureJ/" style="width: 500px;"/>
    </div>
    <div style="margin-top: 30px;line-height: 35px">
      <span style="color: #f9944a;font-size: 30px;font-weight:bold;font-family: fantasy">W</span>
      <span style="color: #97a8be;font-size: 16px">arning~~~ 请确保已经安装jdk 8+, maven 3+; </span>
      <span style="color: #97a8be;font-size: 16px"> 否则, 你的机器将会爆炸！！！</span>
      <br>
      <span style="color: #888;font-size: 20px;font-weight: 600">下一步</span>
      <span style="color: #97a8be;font-size: 16px">, 你应该启动项目里的demo模块
        <a href="https://github.com/hjnlxuexi/pureJ/blob/master/doc/start/GetStarted.md#启动demo" target="_blank" style="color: red">[如何启动?]</a>。
        <br> 如果已经启动, 那就轻点下面的按钮！
      </span>
    </div>
    <div v-if="currentApp.projectDir" style="margin-top: 30px">
      <el-button type="success" class="el-icon-caret-right" style="font-weight:bold; font-size: 30px" @click="launcher">Hello World</el-button>
    </div>
  </div>
</template>
<script>
import requestRaw from '@/utils/requestRaw'
export default {
  name: 'Guide',
  data() {
    return {
      currentApp: {
        'code': 'demo',
        'name': '样例应用',
        'appUrl': 'http://localhost:8080/demo'
      }
    }
  },
  created() {
    requestRaw.post('/getAppConfig', {})
      .then(appConfig => {
        const app = appConfig[appConfig.enable]
        // 如果已经初始化，则直接跳转我的应用
        if (app) {
          this.$router.push('/workbench/my-apps')
        }
      })
  },
  mounted() {
    // 自动获取焦点
    this.$nextTick(() => {
      this.$refs['projectDir'].focus()
    })
  },
  methods: {
    launcher() {
      if (!this.currentApp.projectDir.endsWith('/')) {
        this.currentApp.projectDir = this.currentApp.projectDir + '/'
      }
      this.currentApp.appBizDir = this.currentApp.projectDir + 'biz/'
      this.currentApp.baseDir = this.currentApp.projectDir + 'biz/data/'

      const appConfig = {}
      appConfig.enable = this.currentApp.code
      appConfig[appConfig.enable] = this.currentApp
      requestRaw.post('/saveAppConfig', { appConfig: JSON.stringify(appConfig, null, 2) })
        .then(response => {
          if (!response.status) {
            this.$message.error('你的配置保存出问题了！')
            return
          }
          this.$router.push('/workbench/my-apps')
        })
    }
  }
}
</script>
<style lang="scss" scoped>

</style>

