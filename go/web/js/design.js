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
        /**
         * 初始化方法
         */
        init: function () {
            //1、加载目录信息
            $.post("/getMenuData", {}, function (menus) {
                if (!menus) {
                    alert("未找到目录数据！");
                    return;
                }
                app.menus = menus = JSON.parse(menus);
                for (var i = 0; i < menus.length; i++) {
                    var menu = menus[i];
                    $(".menus").append("<li class='menu' data-index='" + i + "'>" + menu['name'] + "</li>");
                }
                //2、绑定事件
                app.bindMenuEvent();
                app.bindAddModuleEvent();
                app.bindAddServiceEvent();
                app.bindDelModuleEvent();
                app.bindDirectChangeEvent();
                app.bindSaveServiceConfEvent();
                //3、模拟点击第一个菜单
                $(".menu:eq(0)").trigger("click");
                //4、设计服务流程
                app.bindDesignFlow();
            });
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
                var index = $(this).data("index");
                var menu = app.menus[index];
                var items = menu['items'];
                if (!items)return;
                //5、显示子菜单
                for (var j = 0; j < items.length; j++) {
                    var item = items[j];
                    $(".left-menu")
                        .append("<button class='item' data-index='" + j + "' data-service='" + item['service'] + "'>" + item['name'] + "</button> <span>▬</span>");
                }
                //6、绑定二级菜单事件
                app.bindItemEvent();
                //7、模拟点击第一个二级菜单
                $(".item:eq(0)").trigger("click");
                //8、删除二级菜单
                app.bindDelServiceEvent();
                //9、添加字段
                $(".add-field").click(app.bindAddFieldEvent);
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
                //3、加载服务配置
                app.loadServiceConf($(this).data("service"));
            });
        },
        /**
         * 绑定增加模块事件
         */
        bindAddModuleEvent: function () {
            $("#add_module").click(function () {
                var name = $("#module_name").val();
                if (!name)return;
                var menu = {};
                menu['name'] = name;
                menu['items'] = [];
                app.menus.push(menu);
                var data = {};
                data['menus'] = app.menus;
                //保存完整目录
                $.post("/saveMenuData", data, function (d) {
                    console.log(d);
                    window.location.reload();
                });
            });
        },
        /**
         * 绑定删除模块
         */
        bindDelModuleEvent: function () {
            $(".menu").dblclick(function () {
                if (window.confirm("确定删除整个模块？")) {
                    var index = $(this).data("index");
                    app.menus.splice(Number(index), 1);
                    var data = {};
                    data['menus'] = app.menus;
                    //保存完整目录
                    $.post("/saveMenuData", data, function (d) {
                        console.log(d);
                        window.location.reload();
                    });
                }
            });
        },
        /**
         * 绑定增加服务事件
         */
        bindAddServiceEvent: function () {
            $("#add_service").click(function () {
                var name = $("#service_name").val();
                var code = $("#service_code").val();
                if (!name || !code) {
                    alert("服务名称/服务路径必输！");
                    return;
                }
                var menu_index = $(".menu.active").data("index");
                var menu = app.menus[menu_index];
                var item = {};
                item['name'] = name;
                item['service'] = code;
                var items = menu["items"] || [];
                items.unshift(item);
                !menu["items"] && (menu["items"] = items);
                var data = {};
                data['menus'] = app.menus;
                //保存完整目录
                $.post("/saveMenuData", data, function (d) {
                    console.log(d);
                    window.location.reload();
                });
            });
        },
        /**
         * 绑定删除服务事件
         */
        bindDelServiceEvent: function () {
            $(".main .left-menu span").click(function () {
                if (window.confirm("确定删除服务？")) {
                    var service_index = $(this).prev().data("index");
                    var menu_index = $(".menu.active").data("index");
                    var items = app.menus[menu_index].items;
                    items.splice(Number(service_index), 1);
                    var data = {};
                    data['menus'] = app.menus;
                    //保存完整目录
                    $.post("/saveMenuData", data, function (d) {
                        console.log(d);
                        window.location.reload();
                    });
                }
            });
        },
        /**
         * 加载服务配置内容
         * @param service 服务路径
         */
        loadServiceConf: function (service) {
            $.post("/loadServiceConf", {service: service}, function (data) {
                var result = JSON.parse(data);
                //0、重置内容
                var $tbody = $(".main .content tbody");
                var $tr = $(app.tr_tpl.join(''));
                $tbody.empty().append($tr);
                //删除字段、类型选择
                $tbody.find("tr .del-field").click(app.bindDelFieldEvent) && $tbody.find("tr .in_type").change(app.bindTypeChangeEvent);
                $("#direct_type").removeClass("hide");
                $("#normal_service").addClass("hide");
                $('input:radio').removeProp("checked");
                //1、显示基本信息
                $("#service_path").val(service);
                $("#service_id").val(result['id']);
                $("#service_desc").val(result['desc']);
                var isDirect = result["direct"] || 'false';
                $('input:radio[name="isDirect"][value=' + isDirect + ']').prop("checked", 'checked');
                if (isDirect == "true") {
                    var directType = result['directtype'];
                    $('input:radio[name="directType"][value=' + directType + ']').prop("checked", 'checked');
                } else {
                    $("#direct_type").addClass("hide");
                    $("#normal_service").removeClass("hide");
                }
                //2、显示请求字段信息
                app.loadServiceFields(result['input'], $("#input_body"));
                //3、显示响应字段信息
                app.loadServiceFields(result['output'], $("#output_body"));
            });
        },
        /**
         * 加载服务字段信息
         * @param data
         * @param $tBody
         */
        loadServiceFields: function (data, $tBody) {
            for (var i in data) {
                var field = data[i];
                var type = field['type'];
                var $_tr = $tBody.find("tr:not('.sub,.sub *'):eq(" + i + ")");
                var $tr = $_tr.length == 0 ? $(app.tr_tpl.join('')) : $_tr;
                $_tr.length == 0 && $tBody.append($tr)
                && $tr.find(".del-field").click(app.bindDelFieldEvent) && $tr.find(".in_type").change(app.bindTypeChangeEvent);
                $tr.find('.in_type').val(type);
                $tr.find(".in_name").val(field['name']);
                $tr.find('.in_required').val(field['required'] || 'false');
                $tr.find('.in_desc').val(field['desc']);
                if (type == 'E') {
                    $tr.find("td:first").addClass("e");
                    var $subTable = $(app.table_tpl.join(''));
                    $tr.after($subTable);
                    var $tr_body = $subTable.find("table tbody");
                    $tr_body.prev().find("tr .add-field").click(app.bindAddFieldEvent)
                    && $tr_body.find("tr .del-field").click(app.bindDelFieldEvent)  && $tr_body.find("tr .in_type").change(app.bindTypeChangeEvent);
                    var list = field['list'];
                    for (var idx in list) {
                        var item = list[idx];
                        var $tr_sub_tr = $tr_body.find("tr:eq(" + idx + ")");
                        var $tr_subtr = $tr_sub_tr.length == 0 ? $(app.tr_tpl.join('')) : $tr_sub_tr;
                        $tr_sub_tr.length == 0 && $tr_body.append($tr_subtr)
                        && $tr_subtr.find(".del-field").click(app.bindDelFieldEvent)&& $tr_subtr.find(".in_type").change(app.bindTypeChangeEvent);
                        $tr_subtr.find('.in_type').val(item['type']);
                        $tr_subtr.find(".in_name").val(item['name']);
                        $tr_subtr.find('.in_required').val(item['required'] || 'false');
                        $tr_subtr.find('.in_desc').val(item['desc']);
                    }
                }
            }
        },
        /**
         * 绑定增加字段事件
         */
        bindAddFieldEvent: function () {
            var $tbody = $(this).parent().parent().next("tbody");
            var $tr  = $(app.tr_tpl.join(""));
            $tbody.append($tr);
            //删除字段、类型选择
            $tr.find(".del-field").click(app.bindDelFieldEvent)&& $tr.find(".in_type").change(app.bindTypeChangeEvent);
        },
        /**
         * 绑定删除字段事件
         */
        bindDelFieldEvent: function () {
            //如果为列表型字段，则删除子列表
            $(this).hasClass("e") && $(this).parent().next().remove();
            var num = $(this).parent().siblings().length;
            num > 0 ? $(this).parent().remove()
                : $(this).parent().find("input").val("") && $(this).parent().find(".in_type").val("S")&&$(this).parent().find(".in_required").val("true");
        },
        /**
         * 绑定类型选择事件
         */
        bindTypeChangeEvent: function () {
            var $this = $(this);
            var $tr = $this.parent().parent();
            if($this.val()=="E"){//增加子列表
                if($tr.parents("tr.sub").length>0){
                    alert("不支持多级列表！");
                    $this.val("S");
                    return;
                }
                $tr.find("td:first").addClass("e");
                var $subTable = $(app.table_tpl.join(''));
                $tr.after($subTable);
                var $tr_body = $subTable.find("table tbody");
                $tr_body.prev().find("tr .add-field").click(app.bindAddFieldEvent)
                && $tr_body.find("tr .del-field").click(app.bindDelFieldEvent)  && $tr_body.find("tr .in_type").change(app.bindTypeChangeEvent);
            }else{//删除子列表
                $tr.find("td:first").hasClass("e")&&$tr.next().remove()&&$tr.removeClass("e");
            }

        },
        /**
         * 绑定过路属性选择事件
         */
        bindDirectChangeEvent: function () {
            $('input:radio[name="isDirect"]').click(function () {
                $('input:radio').removeProp("checked");
                $(this).prop("checked", 'checked');
                if ($(this).val()=="true"){
                    $("#direct_type").removeClass("hide");
                    $("#normal_service").addClass("hide");
                    $('input:radio[name="directType"][value="database"]').prop("checked", 'checked');
                }else {
                    $("#direct_type").addClass("hide");
                    $("#normal_service").removeClass("hide");
                }
            });
        },
        /**
         * 绑定保存服务配置事件
         */
        bindSaveServiceConfEvent: function () {
            $("#save_service").click(function () {
                if($(".item.choose").length==0){
                    alert("请选择一个服务！");
                    return;
                }
                app.saveServiceConf();
            });
        },
        /**
         * 保存服务配置
         */
        saveServiceConf: function () {
            var serviceConf = {};
            var input,output;
            //1、组装基本信息
            serviceConf['servicePath'] = $("#service_path").val();
            serviceConf['name'] = $(".item.choose").text();//服务名称
            serviceConf['id'] = $("#service_id").val();//服务ID
            serviceConf['desc'] = $("#service_desc").val();//服务描述
            var direct = $("input:radio[name='isDirect']:checked").val();
            serviceConf['direct'] = direct;//是否过路服务
            direct=="true"&& (serviceConf['directtype'] = $("input:radio[name='directType']:checked").val());//过路服务类型
            //2、组装请求信息
            input = app.buildServiceConf($("#input_body"));
            //3、组装响应信息
            output = app.buildServiceConf($("#output_body"));
            //4、装箱
            serviceConf['input'] = input;
            serviceConf['output'] = output;
            //5、保存服务配置
            $.post("/saveServiceConf", serviceConf, function (d) {
                alert(d);
                //6、判断服务路径
                app.checkServicePath();
            });
            // app.checkServicePath();
        },
        /**
         * 组装服务配置信息
         * @param $body
         */
        buildServiceConf: function ($body) {
            var array = [];
            $body.find("tr:not('.sub,.sub *')").each(function () {
                var field = app.buildField($(this));
                if (!field)return;
                var type = $(this).find(".in_type").val();
                if(type=="E"){
                    var list = [];
                    $body.find(".sub tbody tr").each(function () {
                        var sub_field = app.buildField($(this));
                        list.push(sub_field);
                    });
                    field['list'] = list;
                }
                array.push(field);
            });
            return array;
        },
        /**
         * 组装字段对象
         * @param $el
         * @returns
         */
        buildField: function ($el) {
            var name = $el.find(".in_name").val();
            if(!name) return null;
            var field = {};
            field['name'] = $el.find(".in_name").val();
            field['type'] = $el.find(".in_type").val();
            field['required'] = $el.find(".in_required").val();
            field['desc'] = $el.find(".in_desc").val();
            field['regexp'] = "";
            return field;
        },
        /**
         * 判断服务路径
         */
        checkServicePath: function () {
            //1、判断服务路径是否变化
            var new_path = $("#service_path").val();
            var old_path = $(".item.choose").data("service");
            //2、保存菜单
            if(new_path != old_path){
                var menu_index = $(".menu.active").data("index");
                var menu = app.menus[menu_index];
                var service_index = $(".item.choose").data("index");
                var items = menu["items"];
                var item = items[service_index];
                item['service'] = new_path;
                var data = {};
                data['menus'] = app.menus;
                //保存完整目录
                $.post("/saveMenuData", data, function (d) {
                    console.log(d);
                    $(".item.choose").data("service" , new_path);
                });
            }
        },
        /**
         * 绑定设计服务流程
         */
        bindDesignFlow:function () {
            $("#design_flow").click(function () {
                var flowConfPath = $("#service_id").val();
                var title = $("#service_desc").val();
                var url="./designFlow.html?path="+flowConfPath+"&title="+title;
                var winName="newWin";
                var awidth=screen.availWidth/10*9;       //窗口宽度,需要设置
                var aheight=screen.availHeight/5*4;         //窗口高度,需要设置
                var atop=(screen.availHeight - aheight)/2;  //窗口顶部位置,一般不需要改
                var aleft=(screen.availWidth - awidth)/2;   //窗口放中央,一般不需要改
                var param0="scrollbars=0,status=0,menubar=0,resizable=no,location=0"; //新窗口的参数
                var params="top=" + atop + ",left=" + aleft + ",width=" + awidth + ",height=" + aheight + "," + param0 ;
                var win=window.open(url,winName,params); //打开新窗口
                win.focus(); //新窗口获得焦点
            });
        },
        /**
         * 字段信息行，模板定义
         */
        tr_tpl: ['<tr><td class="del-field">▬</td>',
            '<td><input type="text" class="in_name" placeholder="字段名称"></td>',
            '<td>',
            '<select class="in_type">',
            '<option value="S">字符串型</option>',
            '<option value="I">整数型</option>',
            '<option value="F">浮点数型</option>',
            '<option value="B">布尔型</option>',
            '<option value="P">密文型</option>',
            '<option value="E">列表型</option>',
            '</select>',
            '</td>',
            '<td>',
            '<select class="in_required">',
            '<option value="true">是</option>',
            '<option value="false">否</option>',
            '</select>',
            '</td>',
            '<td><input type="text" class="in_desc" placeholder="字段描述"></td>',
            '</tr>'],
        /**
         * 列表字段信息，表格模板
         */
        table_tpl: ['<tr class="sub"><td></td><td colspan="4" style="border: 1px solid #ddd;"><table>',
            '<thead>',
            '<tr>',
            '<td class="add-field second-level">✚</td>',
            '<td>字段名称</td>',
            '<td>字段类型</td>',
            '<td>是否必需</td>',
            '<td>字段描述</td>',
            '</tr>',
            '</thead>',
            '<tbody>',
            '<tr>',
            '<td class="del-field">▬</td>',
            '<td><input type="text" class="in_name" placeholder="字段名称"></td>',
            '<td>',
            '<select class="in_type">',
            '<option value="S">字符串型</option>',
            '<option value="I">整数型</option>',
            '<option value="F">浮点数型</option>',
            '<option value="B">布尔型</option>',
            '<option value="P">密文型</option>',
            '<option value="E">列表型</option>',
            '</select>',
            '</td>',
            '<td>',
            '<select class="in_required">',
            '<option value="true">是</option>',
            '<option value="false">否</option>',
            '</select>',
            '</td>',
            '<td><input type="text" class="in_desc" placeholder="字段描述"></td>',
            '</tr>',
            '</tbody>',
            '</table></td></tr>']
    };
    app.init();
});