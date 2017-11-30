/**
 * <p>Title : 服务测试页面主js</p>
 * <p>Description : 服务调试控制代码</p>
 * <p>Date : 2017/4/17 17:18</p>
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
$(document).ready(function () {
    var app = {
        menus: [],
        items: new Array(),
        req_packet: {
            header: {
                service: ''
            },
            body: {}
        },
        /**
         * 初始化方法
         */
        init: function () {
            //1、加载目录信息
            // $.post("/getMenuData" , {} , function (menus) {
            //     if (!menus) {
            //         alert("未找到目录数据！");
            //         return;
            //     }
            //     app.menus = menus = JSON.parse(menus);
            //     //一级菜单
            //     for (var i = 0; i < menus.length; i++) {
            //         var menu = menus[i];
            //         $(".menus").append("<li class='menu' data-index='" + i + "'>" + menu['name'] + "</li>");
            //         var items = menu['items'];
            //         if (!items)continue;
            //         //二级菜单
            //         for (var j = 0; j < items.length; j++) {
            //             var item = items[j];
            //             $(".left-menu")
            //                 .append("<button class='item' data-service='" + item['service'] + "'>" + item['name'] + "</button>");
            //             app.items.push(item);
            //         }
            //     }
            //     //2、绑定一级菜单事件
            //     app.bindMenuEvent();
            //     //3、绑定二级菜单事件
            //     app.bindItemEvent();
            //     //4、模拟点击第一个二级菜单
            //     var $item = $(".item:eq(0)");
            //     $item.trigger("click");
            //     //5、点击测试
            //     app.doTest();
            //     //6、清空
            //     app.clearData();
            //     //7、保存测试数据
            //     app.saveData();
            //     //8、填充测试数据
            //     app.fillData();
            // });


          //0、获取服务路径
          var serice = app.getQueryString("service");
          //4、加载服务配置
          app.loadServiceConf(serice);
          //5、点击测试
          app.doTest();
          //6、清空
          app.clearData();
          //7、保存测试数据
          app.saveData();
          //8、填充测试数据
          app.fillData();
        },
        /**
         * 绑定一级菜单事件
         */
        bindMenuEvent: function () {
            $(".menu").click(function () {
                //1、已选中的菜单忽略
                if ($(this).hasClass("active"))return;
                //2、菜单样式切换
                $(".active").removeClass("active");
                $(this).addClass("active");
                //3、清空二级菜单
                $(".left-menu").empty();
                //4、获得对应的子菜单
                if ($(this).hasClass("all")) {
                    var items = app.items;
                } else {
                    var index = $(this).data("index");
                    var menu = app.menus[index];
                    items = menu['items'];
                    if (!items)return;
                }
                //5、显示子菜单
                for (var j = 0; j < items.length; j++) {
                    var item = items[j];
                    $(".left-menu")
                        .append("<button class='item' data-service='" + item['service'] + "'>" + item['name'] + "</button>");
                }
                //6、绑定二级菜单事件
                app.bindItemEvent();
                //7、模拟点击第一个子菜单
                var $item = $(".item:eq(0)");
                $item.trigger("click");
            });
        },
        /**
         * 绑定二级菜单事件
         */
        bindItemEvent: function () {
            $(".item").click(function () {
                //1、已选中的菜单忽略
                if ($(this).hasClass("choose"))return;
                //2、菜单样式切换
                $(".choose").removeClass("choose");
                $(this).addClass("choose");
                //3、显示服务路径
                $("#service").val($(this).data("service"));
                //4、加载服务配置
                app.loadServiceConf();
                //5、调整布局
                app.reLayout();
            });
        },
        /**
         * 加载服务配置内容
         */
        loadServiceConf: function (service) {
            $("#service").val(service);
            $.post("/loadServiceConf", {service: service}, function (data) {
                var result = JSON.parse(data);
                //0、显示服务基本信息
                $("#serviceName").text(result.name);
                $("#serviceID").text(result.id);
                $("#serviceDesc").text(result.desc);
                if (result.direct && result.direct == "true") {
                    $(".direct").show();
                    $("#isDirect").text('是');
                    $("#directType").text(result.directtype);
                } else $(".direct").hide();
                //1、显示请求字段
                app.showInput(result.input);
                //2、显示响应字段
                app.showOutput(result.output);
                //3、绑定输入事件
                app.bindInputEvent();

                //4、调整布局
                app.reLayout();
            });
        },
        /**
         * 显示输入字段
         * @param input
         */
        showInput: function (input) {
            //1、显示输入项
            var $column = $(".main .content .request .column");
            $column.empty();
            app.req_packet.body = {};
            if (!input.length) $column.append("<div class='field'>无</div>");
            for (var i = 0; i < input.length; i++) {
                var f_in = input[i];
                var name = "【" + f_in.name + "】";
                var type = f_in.type || "";
                var required = f_in.required && (f_in.required == "true");
                var text = app.parseField(f_in);

                $column.append("<div class='list'></div>");
                var $list = $column.find(".list:last()");
                $list.append("<label>" + text + "</label>");
                if (type != "E") {
                    $list.append("<div class='value'><input type='text' " +
                        "data-name='"+f_in.name+"' data-required='" + required + "'></div>");
                    continue;
                }

                $list.append("<br><table data-name='"+f_in.name+"'>" +
                    "<thead>" +
                    "<tr><td id='add' style='color: cadetblue;font-size: 20px;cursor: pointer;'>✚</td>" +
                    "</thead>" +
                    "<tbody>" +
                    "<tr><td class='remove' style='color: firebrick;font-size: 20px;cursor: pointer;'>▬</td></tr>" +
                    "</tbody>" +
                    "</table>");
                var $head = $list.find("table thead tr");
                var $tr = $list.find("table tbody tr");
                var list = f_in.list;
                for (var j = 0; j < list.length; j++) {
                    var row = list[j];
                    var need = row.required && (row.required == "true");
                    $head.append("<td>" + row.name + "["+row.desc+"]</td>");
                    $tr.append("<td><input type='text' " +
                        "data-pname='"+f_in.name+"' data-name='"+row.name+"' data-required='" + need + "'></td>")
                }
            }
            //2、显示请求报文
            app.req_packet.header.service = $("#service").val();
            $("#req_data").val(JSON.stringify(app.req_packet, null, 4));
        },
        /**
         * 显示输出字段
         * @param output
         * @param isList
         */
        showOutput: function (output, isList) {
            var $column = $(".main .content .response .column");
            if (!isList){
                $column.empty();
                if (!output.length) $column.append("<div class='field'>无</div>");
            }
            $("#resp_data").val("");
            for (var j = 0; j < output.length; j++) {
                var f_out = output[j];
                var type = f_out.type || "";
                var text = app.parseField(f_out);
                if (isList) {
                    $column.append("<div class='field' style='margin-left:20%'>" + text + "</div>");
                } else {
                    $column.append("<div class='field'>" + text + "</div>");
                }
                if (type == "E") {
                    $column.find(".field:last").css({'color': "cornflowerblue"});
                    var list = f_out.list;
                    this.showOutput(list, true);
                }
            }
        },
        /**
         * 解析字段内容
         * @param field
         * @returns {string}
         */
        parseField: function (field) {
            var name = field.name;
            var desc = field.desc || "";
            var type = this.getFieldType(field);
            var required = field.required || "";
            required = required && (required == "true") ? ",必要项" : "";
            var regexp = field.regexp || "" ? ",[" + field.regexp + "]" : "";

            return "【" + name + "】{" + desc + type + required + regexp + "}"
        },
        /**
         * 获取字段类型
         * @param field
         * @returns {string}
         */
        getFieldType: function (field) {
            var type = field.type || "";
            switch (type) {
                case "S":
                    type = ",字符串型";
                    break;
                case "I":
                    type = ",整型";
                    break;
                case "F":
                    type = ",浮点型";
                    break;
                case "B":
                    type = ",布尔型";
                    break;
                case "P":
                    type = ",密文型";
                    break;
                case "E":
                    type = ",列表型";
                    break;
                default :
                    type = ",未知类型";
            }
            return type;
        },
        /**
         * 绑定输入事件
         */
        bindInputEvent: function () {
            //1、点击添加按钮
            $("#add").click(function () {
                var $body = $(this).parents("table").find("tbody");
                var tr = $body.find("tr:eq(0)");
                $body.append(tr.get(0).outerHTML);
                //输入事件
                $body.find("tr:last input").change(app.bindInputField);
                //删除行
                $body.find("tr:last .remove").click(app.bindRemoveInput);
                //调整布局
                app.reLayout();
            });
            //2、点击删除按钮
            $(".remove").click(app.bindRemoveInput);
            //3、输入事件
            $(".main .content .column input").change(app.bindInputField);
        },
        /**
         * 删除列表输入项
         */
        bindRemoveInput: function () {
            var index = $(".remove").index(this);
            var $table = $(this).parents("table");
             var num = $table.find("tbody tr").length;
             num > 1 ? $(this).parent().remove() : $(this).parent().find("input").val("");
             //删除请求数据中对应项
             var pname = $table.data("name");
             var list = app.req_packet.body[pname];
             list.splice(index , 1);
             $("#req_data").val(JSON.stringify(app.req_packet , null , 4));
            //调整布局
            app.reLayout();
        },
        /**
         * 输入事件
         */
        bindInputField: function () {
                var body = app.req_packet.body;
                var tbody = $(this).parents("table tbody");
                var line_num = tbody.find("tr").index($(this).parents("tr"));
                var pname = $(this).data("pname");
                var name = $(this).data("name");
                var value = $(this).val();
                if (pname){
                    if (body[pname]){
                        if (body[pname][line_num]){
                            body[pname][line_num][name] = value;
                        }else {
                            body[pname][line_num]={};
                            body[pname][line_num][name] = value;
                        }
                    }else {
                        body[pname] = [];
                        body[pname][line_num]={};
                        body[pname][line_num][name] = value;
                    }
                }else {
                    body[name] = value;
                }
                $("#req_data").val(JSON.stringify(app.req_packet, null, 4));
        },
        /**
         * 调整布局
         */
        reLayout: function () {
            $(".main .content  .request textarea").height($(".main .content .request .column").outerHeight() - 4);
            $(".main .content  .response textarea").height($(".main .content .response .column").outerHeight() - 4);
        },
        /**
         * 点击测试，回显响应数据
         */
        doTest: function () {
            $("#test").click(function () {
                var req = $("#req_data").val();
                if(!req){
                    alert("请求报文为空");
                    return;
                }
                var req_data = JSON.parse(req);
                $.post("/execute" , req_data , function (data) {
                    $("#resp_data").val(JSON.stringify(JSON.parse(data), null , 4));
                });
            });
        },
        /**
         * 保存测试数据
         */
        saveData:function () {
            $("#save").click(function () {
                var req = $("#req_data").val();
                var resp = $("#resp_data").val();
                if( !( req && resp) ) { alert("请先运行【测试】再执行【保存】"); return; }
                var servicePath = $("#service").val();
                var data = {};
                data['servicePath'] = servicePath;
                data['input'] = req;
                data['output'] = resp;
                $.post("/saveData" , data , function (d) {
                    alert(d);
                    console.log(d);
                });
            });
        },
        /**
         * 清空数据
         */
        clearData: function () {
            $("#clear").click(function () {
                $(".request input").val("");
                app.req_packet.body = {};
                $(".main .content  .packet textarea").val("");
                $("#req_data").val(JSON.stringify(app.req_packet , null , 4));
            });
        },
        /**
         * 填充测试数据
         */
        fillData:function () {
            $("#fill_data").click(function () {
                //1、获取请求测试数据
                var data = {};
                var param = {};
                param['servicePath'] = $("#service").val();
                $.post("/getReqTestData" , param , function (d) {
                    if (!d){
                        alert("未找到测试数据！");return;
                    }
                    app.req_packet = data = JSON.parse(d);
                    //2、回显测试数据
                    $("#req_data").val(JSON.stringify(data , null , 4));
                    var body = data["body"];
                    for (var key in body) {
                        var obj = body[key];
                        //列表参数
                        if (obj instanceof Array){
                            //列表table对象
                            var $list = $("table[data-name='"+key+"']");
                            for (var i in obj) {
                                var lineData = obj[i];
                                var $line = $list.find("tbody tr:eq("+i+")");
                                //追加行
                                if ($line.length==0){
                                    $list.find("tbody").append($list.find("tbody tr:eq(0)").get(0).outerHTML);
                                    $line = $list.find("tbody tr:eq("+i+")");
                                    //点击删除按钮
                                    $line.find(".remove").click(app.bindRemoveInput);
                                    //绑定输入事件
                                    $line.find("input").change(app.bindInputField);
                                }
                                //填充一行数据
                                for (var k in lineData) {
                                    var o = lineData[k];
                                    $line.find("input[data-name='"+k+"']").val(o);
                                }
                            }

                        }
                        //普通参数
                        $(".request .column .list .value input[data-name='"+key+"']").val(obj);
                    }
                    //调整布局
                    app.reLayout();
                });
            });
        },

      getQueryString: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null)return unescape(r[2]);
        return null;
      }

    };
    app.init();
});