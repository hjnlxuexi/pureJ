<template>
  <el-table :data="data" :row-style="showRow" v-bind="$attrs">
    <el-table-column v-if="columns.length===0" width="150">
      <template slot-scope="scope">
        <span v-for="space in scope.row._level" :key="space" class="ms-tree-space"/>
        <span v-if="iconShow(0,scope.row)" class="tree-ctrl" @click="toggleExpanded(scope.$index)">
          <i v-if="!scope.row._expanded" class="el-icon-plus"/>
          <i v-else class="el-icon-minus"/>
        </span>
        {{ scope.$index }}
      </template>
    </el-table-column>
    <el-table-column v-for="(column, index) in columns" v-else :key="column.value" :label="column.text" :width="column.width">
      <template slot-scope="scope">
        <!-- Todo -->
        <!-- eslint-disable-next-line vue/no-confusing-v-for-v-if -->
        <span v-for="space in scope.row._level" v-if="index === 0" :key="space" class="ms-tree-space"/>
        <span v-if="iconShow(index,scope.row)" class="tree-ctrl" @click="toggleExpanded(scope.$index)">
          <i v-if="!scope.row._expanded" class="el-icon-plus"/>
          <i v-else class="el-icon-minus"/>
        </span>
        <!--转换字段类型-->
        {{ column.value === 'type' && transformType(scope.row[column.value]) ? transformType(scope.row[column.value]) : scope.row[column.value] }}
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import treeToArray from './eval'
export default {
  name: 'TreeTable',
  /* eslint-disable */
  props: {
    evalFunc: Function,
    evalArgs: Array,
    expandAll: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      f_types: {
        S: '字符串',
        I: '整数',
        F: '小数',
        B: '布尔',
        T: '时间',
        E: '列表'
      },
      data: [],
      columns: [
        {
          text: '字段名称',
          value: 'name'
        },
        {
          text: '字段类型',
          value: 'type'
        },
        {
          text: '是否必须',
          value: 'required'
        },
        {
          text: '描述',
          value: 'desc'
        },
        {
          text: '正则表达式',
          value: 'regexp'
        },
        {
          text: '目标名称',
          value: 'targetName'
        },
        {
          text: '字段转换器',
          value: 'converter'
        }
      ]
    }
  },
  methods: {
    showRow: function(row) {
      const show = (row.row.parent ? (row.row.parent._expanded && row.row.parent._show) : true)
      row.row._show = show
      return show ? 'animation:treeTableShow 1s;-webkit-animation:treeTableShow 1s;' : 'display:none;'
    },
    // 切换下级是否展开
    toggleExpanded: function(trIndex) {
      const record = this.data[trIndex]
      record._expanded = !record._expanded
    },
    // 图标显示
    iconShow(index, record) {
      return (index === 0 && record.children && record.children.length > 0)
    },
    transformType: function(type) {
      return this.f_types[type]
    },
    freshData(data) {
      let tmp
      if (!Array.isArray(data)) {
        tmp = [data]
      } else {
        tmp = data
      }
      const func = this.evalFunc || treeToArray
      const args = this.evalArgs ? Array.concat([tmp, this.expandAll], this.evalArgs) : [tmp, this.expandAll]
      this.data = func.apply(null, args)
    }
  }
}
</script>
<style rel="stylesheet/css">
  @keyframes treeTableShow {
    from {opacity: 0;}
    to {opacity: 1;}
  }
  @-webkit-keyframes treeTableShow {
    from {opacity: 0;}
    to {opacity: 1;}
  }
</style>

<style lang="scss" rel="stylesheet/scss" >
  $color-blue: #2196F3;
  $space-width: 18px;
  .ms-tree-space {
    position: relative;
    top: 1px;
    display: inline-block;
    font-style: normal;
    font-weight: 400;
    line-height: 1;
    width: $space-width;
    height: 14px;
    &::before {
      content: ""
    }
  }
  .processContainer{
    width: 100%;
    height: 100%;
  }
  table td {
    line-height: 26px;
  }

  .tree-ctrl{
    position: relative;
    cursor: pointer;
    color: $color-blue;
    margin-left: -$space-width;
  }
  .el-table th .cell {
    text-align: center;
    color: #99a9bf;
    font-size: 16px;
  }
</style>
