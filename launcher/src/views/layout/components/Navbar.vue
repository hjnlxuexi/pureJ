<template>
  <el-menu class="navbar" mode="horizontal">
    <hamburger :toggle-click="toggleSideBar" :is-active="sidebar.opened" class="hamburger-container"/>
    <breadcrumb class="breadcrumb-container"/>
    <div class="right-menu">
      <el-tooltip content="Active App" effect="dark" placement="bottom">
        <el-dropdown trigger="click">
          <div>
            <svg-icon icon-class="component" style="width: 20px;height: 20px"/>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="medium">应用编码: {{ currentApp.code }}</el-dropdown-item>
            <el-dropdown-item command="small">应用名称: {{ currentApp.name }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-tooltip>
    </div>
  </el-menu>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import requestRaw from '@/utils/requestRaw'

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  data() {
    return {
      currentApp: {}
    }
  },
  computed: {
    ...mapGetters([
      'sidebar'
    ])
  },
  created() {
    requestRaw.post('/activeApp', {})
      .then(response => {
        this.currentApp = response
      })
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('ToggleSideBar')
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.navbar {
  height: 50px;
  line-height: 50px;
  border-radius: 0px !important;
  .hamburger-container {
    line-height: 58px;
    height: 50px;
    float: left;
    padding: 0 10px;
  }
  .breadcrumb-container {
    float: left;
  }
  .right-menu {
    float: right;
    height: 100%;
    padding: 0 30px;
  }
}
</style>

