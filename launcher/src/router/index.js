import Vue from 'vue'
import Router from 'vue-router'

// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
  }
**/
export const constantRouterMap = [
  { path: '/404', component: () => import('@/views/404'), hidden: true },
  {
    path: '/',
    component: () => import('@/views/guide/index'),
    name: 'Guide',
    hidden: true
  },
  {
    path: '/dashboard',
    component: Layout,
    redirect: '/dashboard',
    name: 'Dashboard',
    children: [{
      path: '',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '主页', icon: 'main' }
    }]
  },
  {
    path: '/workbench',
    component: Layout,
    redirect: '/workbench/my-apps',
    name: 'Workbench',
    meta: { title: '工作台', icon: 'workbench' },
    children: [
      {
        path: 'my-apps',
        name: 'MyApps',
        component: () => import('@/views/workbench/index'),
        meta: { title: '我的应用', icon: 'project' }
      },
      {
        path: 'capacity',
        name: 'Capacity',
        hidden: true,
        component: () => import('@/views/capacity/index'),
        meta: { title: '能力地图', icon: 'capacity' }
      },
      {
        path: 'design',
        component: () => import('@/views/design/index'),
        redirect: '/workbench/design/service',
        name: 'Design',
        hidden: true,
        meta: { title: '设计', icon: 'design' },
        children: [
          {
            path: 'service',
            name: 'Service',
            component: () => import('@/views/design/design'),
            meta: { title: '服务设计', icon: 'design' }
          },
          {
            path: 'flow',
            name: 'Flow',
            hidden: true,
            component: () => import('@/views/design/components/flow'),
            meta: { title: '流程服务', icon: 'flow' }
          },
          {
            path: 'adapter',
            name: 'Adapter',
            hidden: true,
            component: () => import('@/views/design/components/adapter'),
            meta: { title: '外部服务', icon: 'adapter' }
          }
        ]
      },
      {
        path: 'test',
        component: () => import('@/views/test/index'),
        redirect: '/workbench/test/unit-test',
        name: 'Test',
        hidden: true,
        meta: { title: '测试', icon: 'test' },
        children: [
          {
            path: 'unit-test',
            name: 'UnitTest',
            component: () => import('@/views/test/unitTest/UnitTest'),
            meta: { title: '服务测试', icon: 'monkey' }
          }
        ]
      },
      {
        path: 'plan',
        name: 'Plan',
        component: () => import('@/views/plan/index'),
        meta: { title: '项目计划', icon: 'plan' }
      }
    ]
  },

  { path: '*', redirect: '/404', hidden: true }
]

export default new Router({
  // mode: 'history', //后端支持可开
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})
