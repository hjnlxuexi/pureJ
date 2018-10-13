<template>
  <div>
    <div style="text-align: center; margin: 20px 0">
      <el-button type="success" class="el-icon-caret-right" style="font-weight:800;margin: 10px" @click="enterActive">进入Active应用</el-button>
      <div class="help-block">
        <div style="color: #aaa;">
          { {&nbsp;&nbsp;建议:  在demo应用的基础上 Get Started &nbsp;&nbsp;} }
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
      <div class="app" style="background-color: white;" @click="createApp">
        <div class="diamonds" style="color: #42b983">
          <svg-icon icon-class="plus"/>
        </div>
        <div class="desc" style="color: #1f2d3d">
          <p style="color: green"><strong>创建应用</strong></p>
          <p>❝ demo玩腻了, 给我来个干净的 ❞</p>
          <p>❝ 一键生成项目脚手架 ❞</p>
        </div>
      </div>
    </div>
    <hr>
    <transition name="fade">
      <div v-if="showDetail" id="app_detail" class="app-detail">
        <template v-if="activeApp != currentApp.code">
          <div class="left">设置为Active：</div>
          <div class="right">
            <el-button style="line-height: 5px" type="warning" @click="setActive(currentApp.code)">Active</el-button>
          </div>
        </template>
        <div class="left">开发数据根路径：</div>
        <div class="right">
          {{ currentApp.baseDir }}
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
        <div class="left">项目路径：</div>
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
    <EditDialog/>
  </div>
</template>
<script>
import requestRaw from '@/utils/requestRaw'
import EditDialog from './components/EditDialog'
import bus from './components/bus'
export default {
  name: 'Workbench',
  components: {
    EditDialog
  },
  data() {
    return {
      appList: [],
      activeApp: '',
      showDetail: false,
      currentApp: {}
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
  mounted() {
    const vm = this
    bus.$on(['createApp'], (data) => {
      JSON.stringify(data)
      requestRaw.post('/generateScaffold', data)
        .then(response => {
          if (response.status) {
            data.icon = 'app' + (Math.floor(Math.random() * 100) % 4 + 1)
            vm.appList.push(data)
            vm.$message.success('牛逼了搅shi官，快去看看你的文件夹！')
          } else {
            vm.$message.success(response.msg)
          }
        })
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
      requestRaw.post('/api', { _path: '/checkActive' })
        .then(response => {
          if (!response.header || !response.header.status) {
            this.$message.error('Active应用：[' + this.currentApp.code + '] 未启动或无法连接。')
            return
          }
          // 进入能力地图
          this.$router.push('/workbench/capacity')
        })
    },
    createApp() {
      bus.$emit('app', {})
    },
    setActive(code) {
      requestRaw.post('/setActive', { code: code })
        .then(response => {
          this.$router.go(0)
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

