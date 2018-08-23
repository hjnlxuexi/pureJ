/**
 * 描述: 服务流程设计
 * 作者: hejie
 * 日期: 2017/7/9
 */
$(document).ready(function () {
    var getUrlParameter = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (!!r)return decodeURI(r[2]);
        return null;
    };
    var app = {
        property: {
            width: 1000,
            height: 600,
            toolBtns: ["node"],
            haveHead: true,
            headLabel: true,
            initLabelText: getUrlParameter("title"),
            headBtns: ["save", "undo", "redo", "reload"],
            haveTool: true,
            haveGroup: true,
            useOperStack: true
        },
        remark: {
            cursor: "选择指针",
            direct: "结点连线",
            node: "自动结点",
            group: "组织划分框编辑开关"
        },
        demo: null,
        /**
         * 页面初始化
         */
        init: function () {
            //1、初始化绘图组件
            this.demo = $.createGooFlow($("#flow"), this.property);
            this.demo.setNodeRemarks(this.remark);
            //2、初始化数据
            this.loadData();
            //3、绑定事件
            this.bindEvent();
        },
        /**
         * 加载流程数据
         */
        loadData: function () {
            var data = {};
            data['serviceFlowPath'] = getUrlParameter("path");
            $.post("/getServiceFlowConf", data, function (json_data) {
                if (!json_data) return;
                json_data = JSON.parse(json_data);
                var title = json_data['title'];
                if (!title) return;
                //1、重构流程数据对象
                var jsondata = {};
                jsondata['nodes'] = {};
                jsondata['lines'] = {};
                jsondata['areas'] = {};
                jsondata['title'] = json_data['title'];//标题
                jsondata['initNum'] = 1;
                var steps = json_data['steps'];
                var cnt = app.attributeCount(steps);
                for (var id in steps) {
                    var step = steps[id];
                    //1、组装节点
                    var node = {};
                    node['name'] = step['desc'];
                    node['type'] = 'node';
                    node['ref'] = step['ref'];
                    node['left'] = Number(step['left']);
                    node['top'] = Number(step['top']);
                    node['width'] = 150;
                    node['height'] = 50;
                    jsondata['nodes'][app.getGraphId(id, 'node')] = node;
                    //2、组装连线--默认连线
                    var next = step['next'];
                    if (next) {
                        var line = {};
                        line['name'] = '';
                        line['type'] = 'sl';
                        line['from'] = app.getGraphId(id, 'node');
                        line['to'] = app.getGraphId(next, 'node');
                        jsondata['lines'][app.getGraphId(++cnt, 'line')] = line;
                    }
                    //3、组装连线--条件连线
                    var mapping = step['mapping'];
                    if (!mapping) continue;
                    for (var i in mapping) {
                        ++cnt;
                        var forward = mapping[i];
                        app.buildForwardLine(id, forward, cnt, jsondata);
                    }
                }
                jsondata['initNum'] = ++cnt;

                app.demo.loadData(jsondata);
            });

        },
        /**
         * 绑定事件
         */
        bindEvent: function () {
            //1、保存
            this.demo.onBtnSaveClick = function () {
                var data = app.demo.exportData();
                // var s = JSON.stringify(data);
                data['serviceFlowPath'] = getUrlParameter("path");
                //保存为流程配置文件
                $.post("/saveServiceFlowConf", data, function (d) {
                    alert(d);
                    window.close();
                });

            };
            //2、获取焦点
            this.demo.onItemFocus = function (id, model) {
                var obj;
                $("#ele_model").val(model);
                $("#ele_id").val(id);
                if (model == "line") {
                    obj = this.$lineData[id];
                    $("#ele_type").val(obj.M).show();
                    $("#ele_left").val("").hide();
                    $("#ele_top").val("").hide();
                    $("#ele_width").val("").hide();
                    $("#ele_height").val("").hide();
                    $("#ele_from").val(obj.from).show();
                    $("#ele_to").val(obj.to).show();
                    $("#ele_condition").val(obj.condition).show();
                    $("#ele_ref").val("").hide();
                    $("#load_op").hide();
                } else if (model == "node") {
                    obj = this.$nodeData[id];
                    $("#ele_type").val(obj.type).show();
                    $("#ele_left").val(obj.left).show();
                    $("#ele_top").val(obj.top).show();
                    $("#ele_width").val(obj.width).show();
                    $("#ele_height").val(obj.height).show();
                    $("#ele_from").val("").hide();
                    $("#ele_to").val("").hide();
                    $("#ele_condition").val("").hide();
                    $("#ele_ref").val(obj.ref).show();
                    $("#load_op").show();
                }
                $("#ele_name").val(obj.name);
                return true;
            };
            //3、失去焦点
            this.demo.onItemBlur = function (id, model) {
                document.getElementById("propertyForm").reset();
                return true;
            };
            //4、刷新
            this.demo.onFreshClick = function () {
                app.demo.clearData();
                app.loadData();
            };
            //5、属性修改确定
            $("#pop_change").click(function () {
                var id = $("#ele_id").val();
                var type = $("#ele_model").val();
                if (!id)return;
                //1、获取当前节点
                var item;
                if ("node" === type) {
                    item = app.demo.$nodeData[id];
                    var ref = $("#ele_ref").val();
                    item['ref'] = ref;
                    item['left'] = $("#ele_left").val();
                    item['top'] = $("#ele_top").val();
                    item['width'] = $("#ele_width").val();
                    item['height'] = $("#ele_height").val();
                } else if ("line" === type) {
                    item = app.demo.$lineData[id];
                    item["condition"] = $("#ele_condition").val();
                }
                item['name'] = $("#ele_name").val();
            });
            //6、加载原子服务
            $("#load_op").click(function () {
                var $ref = $("#ele_ref");
                $.post("/loadOPList", {}, function (json_data) {
                    var jsonData = JSON.parse(json_data);
                    var opArray = jsonData['opList'];
                    var $opPanel = $("#choose_op");
                    $opPanel.empty();
                    var commonOP = "";
                    for (var i in opArray) {
                        var op = opArray[i];
                        if (op==="dataBaseOP"||op==="protocolOP" || op==="exceptionOP"){
                            commonOP += '<div class="op" id="' + op + '">' + op + '</div>';
                            continue;
                        }
                        $opPanel.append('<div class="op" id="' + op + '">' + op + '</div>');
                    }
                    $opPanel.append('<hr>');
                    $opPanel.append(commonOP);
                    $opPanel.append('<div style="margin-bottom: 50px;"></div>');
                    $opPanel.append('<div class="close">关闭</div>');
                    //回显选中
                    var ref = $ref.val();
                    if (!!ref) {
                        try {
                            if(ref.indexOf(':')>-1)
                                ref = ref.substring(0,ref.indexOf(':'));
                            $('#' + ref).addClass("choosed");
                        } catch (e) {
                        }
                    }
                    //绑定选中事件
                    $(".op").click(function () {
                        $(".op").removeClass("choosed");
                        $(this).addClass("choosed");
                        var op = $(this).text();
                        $ref.val(op);
                        if (op==="dataBaseOP"||op==="protocolOP" || op==="exceptionOP"){
                            $ref.focus();
                        }
                        $opPanel.hide();
                    });
                    $(".close").click(function () {
                        $opPanel.hide();
                    });
                    $opPanel.show();
                });
            });
        },
        /**
         * 根据后台配置ID生成流程图节点/连线ID
         * @private
         * @param id
         * @param type
         * @returns {string}
         */
        getGraphId: function (id, type) {
            return app.demo.$id + "_" + type + "_" + id;
        },
        /**
         * 获取对象属性数量
         * @private
         * @param obj
         * @returns {number}
         */
        attributeCount: function (obj) {
            var count = 0;
            for (var i in obj) {
                if (obj.hasOwnProperty(i)) {  // 建议加上判断,如果没有扩展对象属性可以不加
                    count++;
                }
            }
            return count;
        },
        /**
         * 组装条件连线
         * @param id
         * @param forward
         * @param cnt
         * @param jsondata
         */
        buildForwardLine: function (id, forward, cnt, jsondata) {
            var l = {};
            l['name'] = forward['desc'];
            l['type'] = 'sl';
            l['from'] = app.getGraphId(id, 'node');
            l['to'] = app.getGraphId(forward['to'], 'node');
            l['condition'] = forward['condition'];
            jsondata['lines'][app.getGraphId(cnt, 'line')] = l;
        }
    };

    app.init();
});