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
    component: Layout,
    redirect: '/dashboard',
    name: 'Dashboard',
    hidden: true,
    children: [{
      path: 'dashboard',
      component: () => import('@/views/dashboard/index')
    }]
  },
  {
    path: '/design',
    component: Layout,
    redirect: '/design/service',
    name: '设计',
    alwaysShow: true,
    meta: { title: '设计', icon: 'design' },
    children: [
      {
        path: 'service',
        name: '服务设计',
        component: () => import('@/views/design/design'),
        meta: { title: '服务设计', icon: 'service' }
      },
      {
        path: 'flow',
        name: '流程服务',
        hidden: true,
        component: () => import('@/views/design/components/flow'),
        meta: { title: '流程服务', icon: 'flow' }
      },
      {
        path: 'adapter',
        name: '外部服务',
        hidden: true,
        component: () => import('@/views/design/components/adapter'),
        meta: { title: '外部服务', icon: 'adapter' }
      }
    ]
  },
  {
    path: '/test',
    component: Layout,
    redirect: '/test/unit-test',
    name: '测试',
    alwaysShow: true,
    meta: { title: '测试', icon: 'test' },
    children: [
      {
        path: 'unit-test',
        name: '单元测试',
        component: () => import('@/views/unitTest/UnitTest'),
        meta: { title: '单元测试', icon: 'monkey' }
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
