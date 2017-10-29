/**
 * <p>Title : 常量配置</p>
 * <p>Description : 配置运行环境参数</p>
 * <p>Date : 2017/5/4 11:02</p>
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
var bizConfDir = "/Users/home/Desktop/HJ/pureJ/biz";

var Config = {
    host : 'localhost',
    port : '8080',
    context:'/pureJ',
    method : 'post',
    contentType : 'application/json',
    encode : 'utf8',

    //服务输入数据路径
    inputDataPath : bizConfDir+'/data/channel/input/',
    //服务输出数据路径
    outputDataPath : bizConfDir+'/data/channel/output/',
    //目录数据文件路径
    menuPath : bizConfDir+"/data/menu/menus.json",
    //服务配置路径
    servicePath : bizConfDir+"/service/",
    //服务流程配置
    serviceFlowPath : bizConfDir+"/flow/"

};

module.exports = Config;