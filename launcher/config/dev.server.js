/**
 * <p>Title : 本地服务</p>
 * <p>Description : 开发类服务工具</p>
 * <p>Date : 2018/10/8 </p>
 *
 * @author : hejie
 */
const http = require('http')
const path = require('path')
const fs = require('fs')
const child_process = require('child_process')
const bodyParser = require('body-parser')
const express = require('express')
const apiRoutes = express.Router()
let appConfig = require('../src/appConfig')
const menuPath = 'menu/tree.json'
const createFile = function(filePath, fs, callback) {
  // 1、创建目录
  createDir(filePath, fs)
  // 2、创建文件
  const writeStream = fs.createWriteStream(filePath)
  if (callback) writeStream.on('close', callback)
}
const createDir = function(filePath, fs) {
  const sep = path.sep
  const folders = path.dirname(filePath).split(sep)
  let p = ''
  while (folders.length) {
    p += folders.shift() + sep
    if (!fs.existsSync(p)) {
      fs.mkdirSync(p)
    }
  }
}
const getServerOption = function(_path) {
  const filePath = path.resolve(__dirname, '../src/appConfig.json')
  const data = JSON.parse(fs.readFileSync(filePath, { encoding: 'utf-8' }))
  const activeAPP = data[data.enable]
  const host = activeAPP['appUrl'].substring(7, activeAPP['appUrl'].indexOf(':', 7))
  const port = activeAPP['appUrl'].substring(activeAPP['appUrl'].indexOf(':', 7) + 1, activeAPP['appUrl'].indexOf('/', 7))
  return {
    host: host,
    port: port,
    path: '/' + activeAPP['code'] + _path,
    method: 'post',
    headers: {
      'Content-Type': 'application/json'
    }
  }
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
      const filePath = path.resolve(__dirname, '../src/appConfig.json')
      const data = JSON.parse(fs.readFileSync(filePath, { encoding: 'utf-8' }))
      res.json(data[data.enable])
    })
    app.post('/setActive', bodyParser.json(), (req, res) => {
      const filePath = path.resolve(__dirname, '../src/appConfig.json')
      const data = JSON.parse(fs.readFileSync(filePath, { encoding: 'utf-8' }))
      data['enable'] = req.body.code
      const content = JSON.stringify(data, null, 2)
      fs.exists(filePath, function(exist) {
        if (!exist) createFile(filePath, fs)
        fs.writeFile(filePath, content, function(err) {
          if (err) {
            res.json({ status: false, msg: filePath + '保存失败' })
          }
          appConfig = JSON.parse(content)
          res.json({ status: true, msg: '' })
        })
      })
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
    app.post('/generateScaffold', bodyParser.json(), (req, res) => {
      try {
        // 1、应用配置
        const config = req.body
        const filePath = path.resolve(__dirname, '../src/appConfig.json')
        const data = JSON.parse(fs.readFileSync(filePath, { encoding: 'utf-8' }))
        if (data[config.code]) {
          res.json({ status: false, msg: config.code + ', 应用已存在。' })
          return
        }
        data[config.code] = config
        // 2、copy工程样板
        const projectDir = config.projectDir
        const appDir = projectDir + config.code + '/'
        const appScaffold = path.resolve(__dirname, '../scaffold/app')
        // 2.1、创建目标文件夹
        createDir(appDir, fs)
        // 2.2、copy样板到目标文件夹
        child_process.spawnSync('cp', ['-r', appScaffold, appDir])
        // 2.3、修改配置
        // 2.3.1、系统配置
        const sysConfig = appDir + 'src/main/resources/config/application-app.yml'
        let src = fs.readFileSync(sysConfig, { encoding: 'utf-8' })
        src = src.replace(/\${{code}}/g, config.code)
        src = src.replace(/\${{port}}/g, config.port)
        src = src.replace(/\${{appBizDir}}/g, config.appBizDir)
        fs.writeFileSync(sysConfig, src)
        // 2.3.2、mapper生成器
        const gConfig = appDir + 'src/main/resources/generatorConfig.xml'
        let g_config = fs.readFileSync(gConfig, { encoding: 'utf-8' })
        g_config = g_config.replace('${{appBizDir}}', config.appBizDir)
        fs.writeFileSync(gConfig, g_config)
        // 3、copy配置样板
        const appBizDir = config.appBizDir
        const bizScaffold = path.resolve(__dirname, '../scaffold/biz')
        createDir(appBizDir, fs)
        child_process.spawnSync('cp', ['-r', bizScaffold, appBizDir])
        const confFile = appBizDir + 'conf/' + config.code + '.yml'
        createFile(confFile, fs)
        // 4、copy开发数据样板
        const baseDir = config.baseDir
        const dataScaffold = path.resolve(__dirname, '../scaffold/data')
        createDir(baseDir, fs)
        child_process.spawnSync('cp', ['-r', dataScaffold, baseDir])
        // 5、保存应用配置
        const content = JSON.stringify(data, null, 2)
        fs.exists(filePath, function(exist) {
          if (!exist) createFile(filePath, fs)
          fs.writeFile(filePath, content, function(err) {
            if (err) {
              res.json({ status: false, msg: filePath + '创建失败' })
            }
          })
        })
      } catch (e) {
        console.error(e.message)
        res.json({ status: false, msg: '应用创建失败' })
      }
      res.json({ status: true, msg: '' })
    })
    app.post('/api', bodyParser.json(), (req, resp) => {
      const _path = req.body['_path'] || '/'
      const iData = Object.assign({}, req.body)
      delete iData['_path']
      const input = JSON.stringify(iData)
      let output = ''
      const options = getServerOption(_path)
      const connect = http.request(options, function(res) {
        res.setEncoding('utf8')
        res.on('data', function(chunk) {
          output += chunk
        })
        res.on('end', function() {
          resp.json(JSON.parse(output))
        })
      })
      connect.on('error', function(e) {
        console.error(e.message)
      })
      connect.write(input)
      connect.end()
    })
  }
}
