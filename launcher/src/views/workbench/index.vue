<template>
  <div>
    <div style="text-align: center; margin: 20px 0">
      <el-button type="success" class="el-icon-caret-right" style="font-weight:800;margin: 10px" @click="enterActive">进入Active应用</el-button>
      <span @click="showHelp = !showHelp"><svg-icon icon-class="icon-question" class="help"/></span>
      <div class="help-block">
        <div v-if="showHelp" style="color: #aaa;">
          { {&nbsp;&nbsp;
          <strong style="color: #409EFF;">如何切换Active应用？</strong>&nbsp;&nbsp;
          ① 当前已创建多个应用&nbsp;&nbsp;
          ② 编辑 src/appConfig.json，设置 "enable": "新的应用编码"；&nbsp;&nbsp;③ 执行：npm run dev
          &nbsp;&nbsp;} }
        </div>
        <div v-else style="color: #aaa;">
          { {&nbsp;&nbsp;建议:  在demo应用的基础上Get Started &nbsp;&nbsp;} }
        </div>
      </div>
    </div>
    <hr>
    <div class="container">
      <div v-if="appList.length>0" class="split"/>
      <div v-for="app in appList" v-if="activeApp == app.code" :key="app.code" class="app active" @click="chooseApp(app)">
        <svg-icon icon-class="active" class="tag"/>
        <div class="diamonds">
          <svg-icon :icon-class="app.icon" />
        </div>
        <div class="desc">
          <p>{{ app.code }}</p>
          <p>{{ app.name }}</p>
          <p>{{ app.appUrl }}</p>
        </div>
      </div>
      <div v-for="app in appList" v-if="activeApp != app.code" :key="app.code" class="app" @click="chooseApp(app)">
        <div class="diamonds">
          <svg-icon :icon-class="app.icon" />
        </div>
        <div class="desc">
          <p>{{ app.code }}</p>
          <p>{{ app.name }}</p>
          <p>{{ app.appUrl }}</p>
        </div>
      </div>
      <div v-if="appList.length>0" class="split"/>
      <div class="app" style="background-color: white;">
        <div class="diamonds" style="color: #42b983">
          <svg-icon icon-class="plus"/>
        </div>
        <div class="desc" style="color: #1f2d3d">
          <p style="color: green"><strong>创建应用</strong></p>
          <p>❝ 5项基础配置 ❞</p>
          <p>❝ 一键生成项目脚手架 ❞</p>
        </div>
      </div>
    </div>
    <hr>
    <transition name="fade">
      <div v-if="showDetail" id="app_detail" class="app-detail">
        <div class="left">开发数据根路径：</div>
        <div class="right">
          <el-input :value="currentApp.baseDir" placeholder="开发数据根路径" class="input-text"/>
        </div>
        <hr>
        <div class="left">应用编码：</div>
        <div class="right">
          {{ currentApp.code }}
        </div>
        <div class="left">应用名称：</div>
        <div class="right">
          {{ currentApp.name }}
        </div>
        <div class="left">项目代码路径：</div>
        <div class="right">
          {{ currentApp.projectDir }}
        </div>
        <div class="left">应用访问路径：</div>
        <div class="right">
          {{ currentApp.appUrl }}
        </div>
        <div class="left">业务配置根路径：</div>
        <div class="right">
          {{ currentApp.appBizDir }}
        </div>
      </div>
    </transition>
  </div>
</template>
<script>
import requestRaw from '@/utils/requestRaw'
export default {
  name: 'Workbench',
  data() {
    return {
      appList: [],
      activeApp: '',
      showDetail: false,
      currentApp: {},
      showHelp: false
    }
  },
  created() {
    requestRaw.post('/getAppConfig', {})
      .then(appConfig => {
        const app = appConfig[appConfig.enable]
        // 未初始化
        if (!app) {
          this.$router.push('/')
          return
        }
        this.activeApp = app.code
        this.currentApp = app

        const appList = []
        for (const key in appConfig) {
          if (key === 'enable') {
            continue
          }
          const app = Object.assign({}, appConfig[key])
          app.icon = 'app' + (Math.floor(Math.random() * 100) % 4 + 1)
          appList.push(app)
        }
        this.appList = appList
      })
  },
  methods: {
    chooseApp(app) {
      Promise.resolve(this.showDetail).then(data => {
        this.showDetail = !data
      }).then(data => {
        this.showDetail = true
        this.currentApp = app
      })
    },
    enterActive() {
      requestRaw.post('/api/checkActive', {})
        .then(response => {
          if (!response.header || !response.header.status) {
            this.$message.error('Active应用：[' + this.currentApp.code + '] 未启动或无法连接。')
            return
          }
          // 进入能力地图
          this.$router.push('/workbench/capacity')
        })
    }
  }
}
</script>
<style scoped>
  hr {
    margin: 0 10px;
    border: 1px solid rgba(66,185,131,.1)
  }
  .container {
    text-align: center;
    margin: 10px 10px;
    background-color: rgb(48, 65, 86);
    min-height: 220px;
  }
  .container .app {
    padding: 10px;
    margin: 10px 10px;
    width: 350px;
    text-align: center;
    height: 200px;
    display: inline-block;
    border: solid #d3dce6 5px;
    border-radius: 5px;
    cursor: pointer;
    color: #d3dce6;
    font-size: 18px;
  }
  .container .app .diamonds {
    width: 80px;
    height: 80px;
    display: inline-block;
  }
  .container .app .desc {
    margin-top: 10px;
  }
  .container .app .desc p {
    margin: 5px 0;
    height: 25px;
  }
  .container .app .svg-icon {
    width: 100%;
    height: 100%;
  }
  .container .active, .container .app:hover {
    color: #409EFF;
    background-color: #3a71a8;
    border: solid #409EFF 5px;
    font-weight: 800;
  }
  .container .active .tag {
    width: 50px;
    height: 50px;
    margin-right: -55px;
    position: relative;
    top: -53px;
    left: 187px;
  }
  .split {
    display: inline-block;
    height: 200px;
    border: dashed 2px gray;
    margin: 0 20px -18px 20px;
  }
  .help {
    width: 25px;
    height: 25px;
    margin-bottom: -5px;
    color: #d3dce6;
  }
  .help:hover {
    color: #409EFF;
  }
  .help-block {
    height: 18px;
    margin-bottom: 5px;
  }
  .app-detail {
    margin: 10px 5%;
  }
  .app-detail div {
    line-height: 35px;
    display: inline-block;
  }
  .app-detail .left {
    width: 30%;
    text-align: right;
  }
  .app-detail .right {
    width: 65%;
    text-align: left;
   }
  .app-detail div .input-text {
    line-height: 45px;
  }
  .fade-enter-active, .fade-leave-active {
    transition: opacity 1s;
  }
  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
</style>

