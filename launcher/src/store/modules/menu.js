/**
 * <p>Title : 服务目录</p>
 * <p>Description : 所有接口定义的层次结构</p>
 * <p>Date : 2018/9/19 </p>
 *
 * @author : hejie
 */

const menu = {
  state: {
    menuData: [
      {
        label: '测试服务',
        children: [{
          label: '过路服务',
          code: 'demo/demoDirectService'
        }, {
          label: '过路服务-单OP',
          code: 'demo/demoDirectSingleService'
        }, {
          label: '流程服务',
          code: 'demo/demoFlowService'
        }, {
          label: '新增用户',
          code: 'demo/userInfoService'
        }]
      }, {
        label: '其他服务',
        children: [{
          label: '过路外部服务',
          code: 'demo/demoOutService'
        }, {
          label: 'app',
          code: 'app/appService'
        }]
      }
    ]
  }
}

export default menu
