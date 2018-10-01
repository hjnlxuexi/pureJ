<template>
  <div class="app-container">
    <el-input v-model="filterText" placeholder="Filter keyword" style="margin-bottom:5px;" />
    <el-button type="warning" style="padding: 0 10px;line-height: 20px;margin: 5px 0" @click="handleEditMenu">编辑[json]</el-button>
    <el-tree
      ref="tree"
      :data="menuData"
      :props="defaultProps"
      :filter-node-method="filterNode"
      class="filter-tree"
      default-expand-all
      highlight-current
      @node-click="handleClick"
    />
    <EditDialog/>
  </div>
</template>

<script>
import requestRaw from '@/utils/requestRaw'
import EditDialog from './components/EditDialog'
import bus from './components/bus'
export default {
  components: {
    EditDialog
  },

  data() {
    return {
      filterText: '',
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      menuData: []
    }
  },

  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    }
  },

  created() {
    requestRaw.post('/getMenu', {})
      .then(response => {
        if (!response.status) {
          this.$message.error(response.msg)
          return
        }
        this.menuData = response.data
      })
  },

  mounted() {
    const vm = this
    bus.$on(['editMenuData'], (data) => {
      this.menuData = data
      requestRaw.post('/saveMenu', { content: JSON.stringify(data, null, 2) })
        .then(response => {
          if (!response.status) {
            vm.$message.error(response.msg)
            return
          }
          vm.$message.success('保存成功！')
        })
    })
  },

  methods: {
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    handleClick(value) {
      if (!value.children) {
        this.$emit('chooseService', value)
      }
    },
    handleEditMenu() {
      bus.$emit('menu', this.menuData)
    }
  }
}
</script>

