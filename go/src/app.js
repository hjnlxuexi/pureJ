/**
 * 应用入口
 * @author HJ
 * @date 2017/3/23
 * @mail hjnlxuexi@126.com
 */
var NAME = "∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷\n" +
    "∷∷∷※※※※∷∷∷∷∷∷∷∷∷∷※※∷∷∷∷∷∷∷※※※∷∷※※※※∷∷∷∷※※※※※※∷∷∷\n" +
    "∷∷∷∷※∷∷∷∷∷∷∷∷∷∷∷∷※※※∷∷∷∷∷∷∷※※∷∷※※∷∷∷∷∷∷∷※∷∷※※∷∷∷\n" +
    "∷∷∷∷※∷∷∷∷∷∷∷∷∷∷∷∷※※※∷∷∷∷∷∷∷※※※∷※※∷∷∷∷∷∷∷※∷∷※※∷∷∷\n" +
    "∷∷∷∷※∷∷∷∷∷∷∷∷∷∷∷※※※※※∷∷∷∷∷∷※※※※※※∷∷∷∷∷∷∷※※※※※∷∷∷\n" +
    "∷∷∷∷※∷∷∷※※∷∷∷∷∷∷※※※※※∷∷∷∷∷∷※※※※※※∷∷∷∷∷∷∷※∷∷※※∷∷∷\n" +
    "∷∷∷∷※∷∷∷※※∷∷∷∷∷※※∷∷※※∷∷∷∷∷∷※∷※※∷※∷∷∷∷∷∷∷※∷∷※※∷∷∷\n" +
    "∷∷∷※※※※※※∷∷∷∷∷※※※∷∷※※※∷∷∷∷※※※※※※※※※∷∷∷∷※※※※※※∷∷∷\n" +
    "∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷\n" +
    "∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷∷";

console.log(NAME);
//引入依赖模块
var http = require('http');
var express = require("express");
var bodyParser = require('body-parser');
var restfulService = require('./RestfulService.js');//引入自定义的接口定义类
//实例化express
var app = express();
//设置静态目录
app.use('/', express.static("./web"));
app.use(bodyParser.urlencoded({extended: true}));
//处理所有过程中未处理的异常，临时写法
process.on('uncaughtException', function (err) {
    console.log(err);
    console.log(err.stack);
});
//加载服务接口定义
restfulService.defineService(app , http);


//启动服务端口
app.listen("1108", function () {
    console.log("Lamb 已启动！端口：1108！");
});


