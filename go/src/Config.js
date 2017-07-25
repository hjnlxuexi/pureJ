/**
 * <p>Title : 常量配置</p>
 * <p>Description : 配置运行环境参数</p>
 * <p>Date : 2017/5/4 11:02</p>
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
var Config = {
    host : 'localhost',
    port : '9100',
    method : 'post',
    contentType : 'application/json',
    encode : 'utf8',

    //服务输入数据路径
    inputDataPath : '/Users/home/Desktop/HJ/pureJ/biz/data/channel/input/',
    //服务输出数据路径
    outputDataPath : '/Users/home/Desktop/HJ/pureJ/biz/data/channel/output/',
    //目录数据文件路径
    menuPath : "/Users/home/Desktop/HJ/pureJ/biz/data/menu/menus.json",
    //服务配置路径
    servicePath : "/Users/home/Desktop/HJ/pureJ/biz/service/",
    //服务流程配置
    serviceFlowPath : "/Users/home/Desktop/HJ/pureJ/biz/flow/"

};

module.exports = Config;