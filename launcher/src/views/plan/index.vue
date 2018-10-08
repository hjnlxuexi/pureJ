<template>
  <div class="components-container board">
    <Kanban :key="1" :list="done" :options="options" class="kanban done" header-text="Done"/>
    <Kanban :key="2" :list="working" :options="options" class="kanban working" header-text="Working"/>
    <Kanban :key="3" :list="todo" :options="options" class="kanban todo" header-text="Todo"/>
  </div>
</template>
<script>
import Kanban from '@/components/Kanban'

export default {
  name: 'Plan',
  components: {
    Kanban
  },
  data() {
    return {
      options: {
        group: 'mission'
      },
      done: [
        { name: '在线服务设计', id: 1, url: '' },
        { name: '在线服务测试', id: 2, url: '' }
      ],
      working: [
        { name: '项目脚手架引导', id: 3, url: '' },
        { name: 'GetStart文档', id: 4, url: '' },
        { name: '白皮书', id: 5, url: '' }
      ],
      todo: [
        { name: '回归测试', id: 6, url: '' },
        { name: 'API生成', id: 7, url: '' },
        { name: '测试/进度报告生成', id: 8, url: '' },
        { name: '压力测试', id: 9, url: '' },
        { name: '配置中心', id: 10, url: '' }
      ]
    }
  }
}
</script>
<style lang="scss">
  .board {
    width: 1000px;
    margin-left: 20px;
    display: flex;
    justify-content: space-around;
    flex-direction: row;
    align-items: flex-start;
  }
  .kanban {
    &.todo {
      .board-column-header {
        background: #4A9FF9;
      }
    }
    &.working {
      .board-column-header {
        background: #f9944a;
      }
    }
    &.done {
      .board-column-header {
        background: #2ac06d;
      }
    }
  }
</style>

