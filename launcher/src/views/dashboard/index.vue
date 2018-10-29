<template>
  <div>
    <div style="width: 100%;height: 100%;text-align: center;line-height: 50px;font-size: 30px;color: #ccc">
      <p><img src="../../assets/programmer.jpeg" height="200"></p>
      <p>你现在付出的，</p>
      <p>可能不是你的技术，而是你的体力，</p>
      <p>而技术应该用在真正需要技术的地方。</p>
    </div>
  </div>
</template>
<script>
import Kanban from '@/components/Kanban'

export default {
  name: 'Dashboard',
  components: {
    Kanban
  },
  data() {
    return {}
  }
}
</script>

