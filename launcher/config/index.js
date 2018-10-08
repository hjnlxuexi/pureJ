'use strict'
// Template version: 1.2.6
// see http://vuejs-templates.github.io/webpack for documentation.

const path = require('path')

const fs = require('fs')
const bodyParser = require('body-parser')
const express = require('express')
const apiRoutes = express.Router()
const appConfig = require('../src/appConfig')

function createFile(filePath, fs) { // 文件写入
  const sep = path.sep
  const folders = path.dirname(filePath).split(sep)
  let p = ''
  // 1、创建目录
  while (folders.length) {
    p += folders.shift() + sep
    if (!fs.existsSync(p)) {
      fs.mkdirSync(p)
    }
  }
  // 2、创建文件
  fs.createWriteStream(filePath)
}

module.exports = {
  dev: {
    // Paths
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    proxyTable: {
      '/api': {
        target: appConfig[appConfig.enable].appUrl,
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/'
        }
      }
    },

    // Various Dev Server settings
    host: 'localhost', // can be overwritten by process.env.HOST
    port: 15910, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined
    autoOpenBrowser: true,
    errorOverlay: true,
    notifyOnErrors: false,
    poll: false, // https://webpack.js.org/configuration/dev-server/#devserver-watchoptions-

    // Use Eslint Loader?
    // If true, your code will be linted during bundling and
    // linting errors and warnings will be shown in the console.
    useEslint: true,
    // If true, eslint errors and warnings will also be shown in the error overlay
    // in the browser.
    showEslintErrorsInOverlay: false,

    /**
     * Source Maps
     */

    // https://webpack.js.org/configuration/devtool/#development
    devtool: 'cheap-source-map',

    // CSS Sourcemaps off by default because relative paths are "buggy"
    // with this option, according to the CSS-Loader README
    // (https://github.com/webpack/css-loader#sourcemaps)
    // In our experience, they generally work as expected,
    // just be aware of this issue when enabling this option.
    cssSourceMap: false,
    before(app) {
      app.use('/', apiRoutes)
      // app.use(bodyParser.urlencoded({extended: true}))
      app.post('/activeApp', bodyParser.json(), (req, res) => {
        res.json(appConfig[appConfig.enable])
      })
      const menuPath = 'menu/tree.json'
      app.post('/appList', bodyParser.json(), (req, res) => {
        const filePath = path.resolve(__dirname, '../src/appConfig.json')
        let result = { status: true }
        try {
          const data = JSON.parse(fs.readFileSync(filePath, { encoding: 'utf-8' }))
          const appList = []
          for (const key in data) {
            if (key === 'enable') {
              continue
            }
            const app = Object.assign({}, data[key])
            appList.push(app)
          }
          result.data = appList
        } catch (e) {
          console.error(e)
          result = { status: false, msg: filePath + '获取数据失败' }
        }
        res.json(result)
      })
      app.post('/saveAppConfig', bodyParser.json(), (req, res) => {
        const filePath = path.resolve(__dirname, '../src/appConfig.json')
        const content = req.body.appConfig
        fs.exists(filePath, function(exist) {
          // 文件不存在，则创建文件
          if (!exist) createFile(filePath, fs)
          // 写入内容
          fs.writeFile(filePath, content, function(err) {
            if (err) {
              res.json({ status: false, msg: filePath + '保存失败' })
            }
            res.json({ status: true, msg: '' })
          })
        })
      })
      app.post('/getMenu', bodyParser.json(), (req, res) => {
        const filePath = appConfig[appConfig.enable].baseDir + menuPath
        let result = { status: true }
        try {
          result.data = JSON.parse(fs.readFileSync(filePath, { encoding: 'utf-8' }))
        } catch (e) {
          result = { status: false, msg: filePath + '获取数据失败' }
        }
        res.json(result)
      })
      app.post('/saveMenu', bodyParser.json(), (req, res) => {
        let filePath = appConfig[appConfig.enable].baseDir + menuPath
        let content = req.body.content
        if (req.body.create) {
          filePath = appConfig[appConfig.enable].baseDir + menuPath
          content = '[]'
        }
        fs.exists(filePath, function(exist) {
          // 文件不存在，则创建文件
          if (!exist) createFile(filePath, fs)
          // 写入内容
          console.error(content)
          fs.writeFile(filePath, content, function(err) {
            if (err) {
              res.json({ status: false, msg: filePath + 'menu保存失败' })
            }
            res.json({ status: true, msg: '' })
          })
        })
      })
      app.post('/getJson', bodyParser.json(), (req, res) => {
        const filePath = appConfig[appConfig.enable].baseDir + req.body.jsonPath
        let result = { status: true }
        try {
          result.data = JSON.parse(fs.readFileSync(filePath, { encoding: 'utf-8' }))
        } catch (e) {
          result = { status: false, msg: filePath + '获取数据失败' }
        }
        res.json(result)
      })
      app.post('/saveJson', bodyParser.json(), (req, res) => {
        const filePath = appConfig[appConfig.enable].baseDir + req.body.jsonPath
        const content = req.body.content
        fs.exists(filePath, function(exist) {
          // 文件不存在，则创建文件
          if (!exist) createFile(filePath, fs)
          // 写入内容
          fs.writeFile(filePath, content, function(err) {
            if (err) {
              res.json({ status: false, msg: filePath + '保存失败' })
            }
            res.json({ status: true, msg: '' })
          })
        })
      })
    }
  },

  build: {
    // Template for index.html
    index: path.resolve(__dirname, '../dist/index.html'),

    // Paths
    assetsRoot: path.resolve(__dirname, '../dist'),
    assetsSubDirectory: 'static',

    /**
     * You can set by youself according to actual condition
     * You will need to set this if you plan to deploy your site under a sub path,
     * for example GitHub pages. If you plan to deploy your site to https://foo.github.io/bar/,
     * then assetsPublicPath should be set to "/bar/".
     * In most cases please use '/' !!!
     */
    assetsPublicPath: '/',

    /**
     * Source Maps
     */

    productionSourceMap: false,
    // https://webpack.js.org/configuration/devtool/#production
    devtool: 'source-map',

    // Gzip off by default as many popular static hosts such as
    // Surge or Netlify already gzip all static assets for you.
    // Before setting to `true`, make sure to:
    // npm install --save-dev compression-webpack-plugin
    productionGzip: false,
    productionGzipExtensions: ['js', 'css'],

    // Run the build command with an extra argument to
    // View the bundle analyzer report after build finishes:
    // `npm run build --report`
    // Set to `true` or `false` to always turn it on or off
    bundleAnalyzerReport: process.env.npm_config_report || false,

    // `npm run build:prod --generate_report`
    generateAnalyzerReport: process.env.npm_config_generate_report || false
  }
}
