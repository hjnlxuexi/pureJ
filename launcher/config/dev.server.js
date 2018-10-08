/**
 * <p>Title : 本地服务</p>
 * <p>Description : 开发类服务工具</p>
 * <p>Date : 2018/10/8 </p>
 *
 * @author : hejie
 */

const path = require('path')
const fs = require('fs')
const bodyParser = require('body-parser')
const express = require('express')
const apiRoutes = express.Router()
let appConfig = require('../src/appConfig')
const menuPath = 'menu/tree.json'
const createFile = function(filePath, fs) {
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
  appUrl: appConfig[appConfig.enable] ? appConfig[appConfig.enable].appUrl : 'http://localhost:8080/demo',
  before(app) {
    app.use('/', apiRoutes)
    // app.use(bodyParser.urlencoded({extended: true}))
    app.post('/getAppConfig', bodyParser.json(), (req, res) => {
      const filePath = path.resolve(__dirname, '../src/appConfig.json')
      const data = JSON.parse(fs.readFileSync(filePath, { encoding: 'utf-8' }))
      res.json(data)
    })
    app.post('/activeApp', bodyParser.json(), (req, res) => {
      res.json(appConfig[appConfig.enable])
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
          appConfig = JSON.parse(content)
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
}
