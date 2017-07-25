/**
 * <p>Title : restful的接口服务</p>
 * <p>Description : 封装服务</p>
 * <p>Date : 2017/5/3 23:49</p>
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
var RestfulService = {
    /**
     * 常量配置
     */
    config: require('./Config.js'),
    /**
     * 文件系统实例
     */
    fs: require('fs'),
    /**
     * path模块实例
     */
    path: require("path"),

    /**
     * 定义服务
     * @param express 模块实例
     * @param http 模块实例
     */
    defineService: function (express, http) {
        /**
         * 加载服务配置
         */
        express.post('/loadServiceConf', function (req, resp) {
            if (!req.body.service) {
                console.log("缺少【service】参数");
                resp.send("缺少【service】参数");
            }
            //1、调用java rest服务
            //请求参数
            var options = {
                host: RestfulService.config.host,
                port: RestfulService.config.port,
                path: '/loadServiceConf',
                method: RestfulService.config.method,
                headers: {
                    'Content-Type': RestfulService.config.contentType
                }
            };
            //请求数据
            var input = JSON.stringify(req.body);
            var ouput = '';
            //创建连接，并设置事件回调接受数据
            var connect = http.request(options, function (res) {
                res.setEncoding(RestfulService.config.encode);
                res.on('data', function (chunk) {
                    ouput += chunk;
                    console.log('Response: ' + chunk);
                    //2、返回数据
                    resp.end(ouput);
                });
            });
            //发送数据
            connect.write(input);
            //请求结束
            connect.end();
        });
        /**
         * 请求统一服务
         */
        express.post('/execute', function (req, resp) {
            //1、调用java rest服务
            //请求参数
            var options = {
                host: RestfulService.config.host,
                port: RestfulService.config.port,
                path: '/',
                method: RestfulService.config.method,
                headers: {
                    'Content-Type': RestfulService.config.contentType
                }
            };
            //请求数据
            var input = JSON.stringify(req.body);
            var ouput = '';
            //创建连接，并设置事件回调接受数据
            var connect = http.request(options, function (res) {
                res.setEncoding(RestfulService.config.encode);
                res.on('data', function (chunk) {
                    ouput += chunk;
                    console.log('Response: ' + chunk);
                    //2、返回数据
                    resp.end(ouput);
                });
            });
            //发送数据
            connect.write(input);
            //请求结束
            connect.end();
        });
        /**
         * 保存测试数据
         */
        express.post('/saveData', function (req, resp) {
            var fs = RestfulService.fs;
            //1、获取请求数据
            var data = req.body;
            var servicePath = data.servicePath + ".json";
            var inputFilePath = RestfulService.config.inputDataPath + servicePath;
            var outputFilePath = RestfulService.config.outputDataPath + servicePath;
            var input = data.input;
            var output = data.output;
            //2、保存输入数据
            fs.exists(inputFilePath, function (exist) {
                //文件不存在，则创建文件
                if (!exist) RestfulService.createFile(inputFilePath, fs);
                //写入内容
                fs.writeFile(inputFilePath, input, function (err) {
                    if (err) resp.end(err);
                    console.log("请求数据写入成功！");
                });
            });
            //3、保存输出数据
            fs.exists(outputFilePath, function (exist) {
                //文件不存在，则创建文件
                if (!exist) RestfulService.createFile(outputFilePath, fs);
                //写入内容
                fs.writeFile(outputFilePath, output, function (err) {
                    if (err) resp.end(err);
                    console.log("响应数据写入成功！");
                });
            });
            resp.end("测试数据保存成功！");
        });
        /**
         * 获取请求测试数据
         */
        express.post('/getReqTestData', function (req, resp) {
            var fs = RestfulService.fs;
            var data = req.body;
            var servicePath = data.servicePath + ".json";
            var inputFilePath = RestfulService.config.inputDataPath + servicePath;
                fs.readFile(inputFilePath,  {encoding: 'utf-8'},  function (err, bytes) {
                    if (err) {
                        resp.end(false);
                        return console.error(err);
                    }
                    console.log(bytes);
                    resp.end(bytes);
            });
        });
        /**
         * 获取目录数据
         */
        express.post('/getMenuData', function (req, resp) {
            var fs = RestfulService.fs;
            var menuPath = RestfulService.config.menuPath;
            fs.readFile(menuPath, {encoding: 'utf-8'}, function (err, bytes) {
                if (err) {
                    resp.end(false);
                    return console.error(err);
                }
                console.log(bytes);
                resp.end(bytes);
            });
        });
        /**
         * 保存目录数据
         */
        express.post('/saveMenuData', function (req, resp) {
            var fs = RestfulService.fs;
            //1、获取请求数据
            var data = req.body;
            var menus = data['menus'];
            var menuPath = RestfulService.config.menuPath;
            //2、保存目录数据
            fs.exists(menuPath, function (exist) {
                //文件不存在，则创建文件
                if (!exist) RestfulService.createFile(menuPath, fs);
                //写入内容
                fs.writeFile(menuPath, JSON.stringify(menus , null ,4), function (err) {
                    if (err) resp.end(err);
                    console.log("目录数据写入成功！");
                });
            });
            resp.end("目录数据保存成功！");
        });
        /**
         * 保存服务配置
         */
        express.post('/saveServiceConf', function (req, resp) {
            var fs = RestfulService.fs;
            //1、获取请求数据
            var data = req.body;
            var serviceCode = data['servicePath'];
            var servicePath = RestfulService.config.servicePath+serviceCode+".xml";
            //2、组装服务配置
            var serviceConf = RestfulService.buildServiceConf(data);
            //3、保存服务配置
            fs.exists(servicePath, function (exist) {
                //文件不存在，则创建文件
                if (!exist) RestfulService.createFile(servicePath, fs);
                //写入内容
                fs.writeFile(servicePath, serviceConf, function (err) {
                    if (err) resp.end(err);
                    console.log("服务配置写入成功！");
                });
            });
            resp.end("服务配置保存成功！");
        });

        /**
         * 获取服务流程配置
         */
        express.post('/getServiceFlowConf', function (req, resp) {
            //1、调用java rest服务
            //请求参数
            var options = {
                host: RestfulService.config.host,
                port: RestfulService.config.port,
                path: '/loadFlowConf',
                method: RestfulService.config.method,
                headers: {
                    'Content-Type': RestfulService.config.contentType
                }
            };
            //请求数据
            var input = JSON.stringify(req.body);
            var ouput = '';
            //创建连接，并设置事件回调接受数据
            var connect = http.request(options, function (res) {
                res.setEncoding(RestfulService.config.encode);
                res.on('data', function (chunk) {
                    ouput += chunk;
                    console.log('Response: ' + chunk);
                    //2、返回数据
                    resp.end(ouput);
                });
            });
            //发送数据
            connect.write(input);
            //请求结束
            connect.end();
        });
        /**
         * 保存服务流程配置
         */
        express.post('/saveServiceFlowConf', function (req, resp) {
            var fs = RestfulService.fs;
            //1、获取请求数据
            var data = req.body;
            var serviceId = data['serviceFlowPath'];
            var serviceFlowPath = RestfulService.config.serviceFlowPath+serviceId+".xml";
            //2、组装服务配置
            var serviceFlowConf = RestfulService.buildServiceFlowConf(data);
            //3、保存服务配置
            fs.exists(serviceFlowPath, function (exist) {
                //文件不存在，则创建文件
                if (!exist) RestfulService.createFile(serviceFlowPath, fs);
                //写入内容
                fs.writeFile(serviceFlowPath, serviceFlowConf, function (err) {
                    if (err) resp.end(err);
                    console.log("服务流程配置写入成功！");
                });
            });
            resp.end("服务流程配置保存成功！");
        });
        /**
         * 加载原子服务列表
         */
        express.post('/loadOPList', function (req, resp) {
            //1、调用java rest服务
            //请求参数
            var options = {
                host: RestfulService.config.host,
                port: RestfulService.config.port,
                path: '/loadOPList',
                method: RestfulService.config.method,
                headers: {
                    'Content-Type': RestfulService.config.contentType
                }
            };
            //请求数据
            var input = JSON.stringify({});
            var ouput = '';
            //创建连接，并设置事件回调接受数据
            var connect = http.request(options, function (res) {
                res.setEncoding(RestfulService.config.encode);
                res.on('data', function (chunk) {
                    ouput += chunk;
                    console.log('Response: ' + chunk);
                    //2、返回数据
                    resp.end(ouput);
                });
            });
            //发送数据
            connect.write(input);
            //请求结束
            connect.end();
        });
    },
    /**
     * 组装服务流程配置
     * @param data
     */
    buildServiceFlowConf: function (data) {
        var array = [];
        array.push('<?xml version="1.0" encoding="UTF-8"?>');
        array.push('<flow title="'+data.title+'">');
        var nodes = data.nodes;
        var lines = data.lines;
        for (var id in nodes) {
            var index = id.substr(-1);
            var node = nodes[id];
            array.push('    <step index="'+index+'" ref="'+node["ref"]+'" desc="'+node["name"]+'" left="'+node["left"]+'" top="'+node["top"]+'" ');
            var forwards = [];
            var hasForward = false;
            for (var key in lines) {
                var line = lines[key];
                if (id!=line["from"])continue;
                //next 映射
                if (!line['condition']){
                    array.push('next="'+line["to"].substr(-1)+'"')
                    continue;
                }
                hasForward = true;
                //普通条件
                forwards.push('     <forward condition="'+line["condition"]+'" desc="'+line["name"]+'" to="'+line["to"].substr(-1)+'"/>')
            }
            if(hasForward){
                array.push(' >');
                array = array.concat(forwards);
                array.push('    </step>');
            } else {
                array.push(' />');
            }
        }

        array.push('</flow>');
        return array.join('\n');
    },
    /**
     * 组装服务配置
     * @param data
     * @returns {string}
     * @private
     */
    buildServiceConf: function (data) {
        var array = [];
        array.push('<?xml version="1.0" encoding="UTF-8"?>');
        array.push('<service>');
        //1、组装基本信息
        data["name"]&&array.push('  <name>'+data["name"]+'</name>');
        data["desc"]&&array.push('  <desc>'+data["desc"]+'</desc>');
        data["direct"]&&array.push('    <direct>'+data["direct"]+'</direct>');
        data["directtype"]&&array.push('    <directtype>'+data["directtype"]+'</directtype>');
        data["id"]&&array.push('    <id>'+data["id"]+'</id>');
        //2、组装请求信息
        var input = data["input"];
        if(input&&input.length>0){
            array.push('    <input>');
            RestfulService.buildFields(input , array);
            array.push('    </input>');
        }else {
            array.push('    <input/>');
        }
        //3、组装响应信息
        var output = data["output"];
        if (output&&output.length>0){
            array.push('    <output>');
            RestfulService.buildFields(output , array);
            array.push('    </output>');
        }else {
            array.push('    <output/>');
        }
        array.push('</service>');
        return array.join('\n');
    },
    /**
     * 组装字段信息
     * @param fields
     * @param array
     * @private
     */
    buildFields: function (fields , array) {
        for (var i in fields) {
            var field = fields[i];
            var type = field["type"];
            if (type != "E"){
                array.push('        <field name="'+field["name"]+'" type="'+field["type"]+'" regexp="'+(field["regexp"]||"")+'" required="'+field["required"]+'" desc="'+(field["desc"]||"")+'"/>');
                continue;
            }
            array.push('        <field name="'+field["name"]+'" type="'+type+'" regexp="'+(field["regexp"]||"")+'" required="'+field["required"]+'" desc="'+(field["desc"]||"")+'">');
            var list = field['list'];
            for (var idx in list) {
                var sub = list[idx];
                array.push('            <field name="'+sub["name"]+'" type="'+sub["type"]+'" regexp="'+(sub["regexp"]||"")+'" required="'+sub["required"]+'" desc="'+(sub["desc"]||"")+'"/>');
            }
            array.push('        </field>');
        }
    },
    /**
     * 创建全路径文件
     * @param filePath 文件全路径
     * @param fs 文件系统实例
     * @private
     */
    createFile: function (filePath, fs) { //文件写入
        var sep = RestfulService.path.sep;
        var folders = RestfulService.path.dirname(filePath).split(sep);
        var p = '';
        //1、创建目录
        while (folders.length) {
            p += folders.shift() + sep;
            if (!fs.existsSync(p)) {
                fs.mkdirSync(p);
            }
        }
        //2、创建文件
        fs.createWriteStream(filePath);
    }
};
module.exports = RestfulService;
