/* eslint-disable */
'use strict'
const path = require('path')
const utils = require('./utils')
const webpack = require('webpack')
const config = require('../config')
const merge = require('webpack-merge')
const baseWebpackConfig = require('./webpack.base.conf')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const FriendlyErrorsPlugin = require('friendly-errors-webpack-plugin')
const portfinder = require('portfinder')
const fs = require('fs')
const bodyParser = require('body-parser')
const express = require('express')
const app = express()
const apiRoutes = express.Router()
const appConfig = require('../src/appConfig')

function resolve(dir) {
  return path.join(__dirname, '..', dir)
}

function createFile (filePath, fs) { //文件写入
  const sep = path.sep
  const folders = path.dirname(filePath).split(sep)
  let p = ''
  // 1、创建目录
  while (folders.length) {
    p += folders.shift() + sep;
    if (!fs.existsSync(p)) {
      fs.mkdirSync(p);
    }
  }
  // 2、创建文件
  fs.createWriteStream(filePath);
}

const HOST = process.env.HOST
const PORT = process.env.PORT && Number(process.env.PORT)

const devWebpackConfig = merge(baseWebpackConfig, {
  mode: 'development',
  module: {
    rules: utils.styleLoaders({
      sourceMap: config.dev.cssSourceMap,
      usePostCSS: true
    })
  },
  // cheap-module-eval-source-map is faster for development
  devtool: config.dev.devtool,

  // these devServer options should be customized in /config/index.js
  devServer: {
    clientLogLevel: 'warning',
    historyApiFallback: true,
    hot: true,
    compress: true,
    host: HOST || config.dev.host,
    port: PORT || config.dev.port,
    open: config.dev.autoOpenBrowser,
    overlay: config.dev.errorOverlay
      ? { warnings: false, errors: true }
      : false,
    publicPath: config.dev.assetsPublicPath,
    proxy: config.dev.proxyTable,
    quiet: true, // necessary for FriendlyErrorsPlugin
    watchOptions: {
      poll: config.dev.poll
    },
    before(app) {
      app.use('/', apiRoutes)
      // app.use(bodyParser.urlencoded({extended: true}))
      app.post('/getMenu', bodyParser.json(), (req, res) => {
        const filePath = appConfig.baseDir + appConfig.menu
        let result = {status: true}
        try {
          result.data = JSON.parse(fs.readFileSync(filePath, {encoding: 'utf-8'}))
        } catch(e) {
          result = {status: false, msg: filePath + '获取数据失败'}
        }
        res.json(result)
      })
      app.post('/saveMenu', bodyParser.json(), (req, res) => {
        let filePath = appConfig.baseDir + appConfig.menu
        let content = req.body.content
        if (req.body.menuPath){
          filePath = appConfig.baseDir + req.body.menuPath
          content = '[]'
        }
        fs.exists(filePath, function (exist) {
          // 文件不存在，则创建文件
          if (!exist) createFile(filePath, fs)
          // 写入内容
          console.error(content)
          fs.writeFile(filePath, content, function (err) {
            if (err) {
              res.json({status: false, msg: filePath + 'menu保存失败'})
            }
            res.json({status: true, msg: ''})
          })
        })
      })
      app.post('/getJson', bodyParser.json(), (req, res) => {
        const filePath = appConfig.baseDir + req.body.jsonPath
        let result = {status: true}
        try {
          result.data = JSON.parse(fs.readFileSync(filePath, {encoding: 'utf-8'}))
        } catch(e) {
          result = {status: false, msg: filePath + '获取数据失败'}
        }
        res.json(result)
      })
      app.post('/saveJson', bodyParser.json(), (req, res) => {
        const filePath = appConfig.baseDir + req.body.jsonPath
        const content = req.body.content
        fs.exists(filePath, function (exist) {
          // 文件不存在，则创建文件
          if (!exist) createFile(filePath, fs)
          // 写入内容
          fs.writeFile(filePath, content, function (err) {
            if (err) {
              res.json({status: false, msg: filePath + '保存失败'})
            }
            res.json({status: true, msg: ''})
          })
        })
      })
    }
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': require('../config/dev.env')
    }),
    new webpack.HotModuleReplacementPlugin(),
    // https://github.com/ampedandwired/html-webpack-plugin
    new HtmlWebpackPlugin({
      filename: 'index.html',
      template: 'index.html',
      inject: true,
      favicon: resolve('favicon.ico'),
      title: 'pureJ-launcher'
    })
  ]
})

module.exports = new Promise((resolve, reject) => {
  portfinder.basePort = process.env.PORT || config.dev.port
  portfinder.getPort((err, port) => {
    if (err) {
      reject(err)
    } else {
      // publish the new Port, necessary for e2e tests
      process.env.PORT = port
      // add port to devServer config
      devWebpackConfig.devServer.port = port

      // Add FriendlyErrorsPlugin
      devWebpackConfig.plugins.push(
        new FriendlyErrorsPlugin({
          compilationSuccessInfo: {
            messages: [
              `Your application is running here: http://${
                devWebpackConfig.devServer.host
              }:${port}`
            ]
          },
          onErrors: config.dev.notifyOnErrors
            ? utils.createNotifierCallback()
            : undefined
        })
      )

      resolve(devWebpackConfig)
    }
  })
})
