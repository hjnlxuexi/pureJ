/**
 * <p>Title : 服务测试页面主js</p>
 * <p>Description : 服务调试控制代码</p>
 * <p>Date : 2017/4/17 17:18</p>
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
$(document).ready(function () {
    var app = {
        /**
         * 初始化方法
         */
        init: function () {
            //0、获取服务路径
            var serice = app.getQueryString("service");
            //1、绑定事件
            app.loadServiceConf(serice);
            app.bindDirectChangeEvent();
            app.bindSaveServiceConfEvent();
            //2、设计服务流程
            app.bindDesignFlow();
            //3、添加字段
            $(".add-field").click(app.bindAddFieldEvent);
        },
        /**
         * 加载服务配置内容
         * @param service 服务路径
         */
        loadServiceConf: function (service) {
            $.post("/loadServiceConf", {service: service}, function (data) {
                var result = !!data ? JSON.parse(data) : {};
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
                $("#service_id").val(result['id']||'');
                $("#service_desc").val(result['desc'] || '');
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
                $tr.find('.regexp').val(field['regexp']);
                $tr.find(".in_name").val(field['name']);
                $tr.find('.in_required').val(field['required'] || 'false');
                $tr.find('.in_desc').val(field['desc']);
                if (type == 'E') {
                    $tr.find("td:first").addClass("e");
                    var $subTable = $(app.table_tpl.join(''));
                    $tr.after($subTable);
                    var $tr_body = $subTable.find("table tbody");
                    $tr_body.prev().find("tr .add-field").click(app.bindAddFieldEvent)
                    && $tr_body.find("tr .del-field").click(app.bindDelFieldEvent) && $tr_body.find("tr .in_type").change(app.bindTypeChangeEvent);
                    var list = field['list'];
                    for (var idx in list) {
                        var item = list[idx];
                        var $tr_sub_tr = $tr_body.find("tr:eq(" + idx + ")");
                        var $tr_subtr = $tr_sub_tr.length == 0 ? $(app.tr_tpl.join('')) : $tr_sub_tr;
                        $tr_sub_tr.length == 0 && $tr_body.append($tr_subtr)
                        && $tr_subtr.find(".del-field").click(app.bindDelFieldEvent) && $tr_subtr.find(".in_type").change(app.bindTypeChangeEvent);
                        $tr_subtr.find('.in_type').val(item['type']);
                        $tr_subtr.find('.regexp').val(item['regexp']);
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
            var $tr = $(app.tr_tpl.join(""));
            $tbody.append($tr);
            //删除字段、类型选择
            $tr.find(".del-field").click(app.bindDelFieldEvent) && $tr.find(".in_type").change(app.bindTypeChangeEvent);
        },
        /**
         * 绑定删除字段事件
         */
        bindDelFieldEvent: function () {
            //如果为列表型字段，则删除子列表
            $(this).hasClass("e") && $(this).parent().next().remove();
            var num = $(this).parent().siblings().length;
            num > 0 ? $(this).parent().remove()
                : $(this).parent().find("input").val("") && $(this).parent().find(".in_type").val("S") && $(this).parent().find(".in_required").val("true");
        },
        /**
         * 绑定类型选择事件
         */
        bindTypeChangeEvent: function () {
            var $this = $(this);
            var $tr = $this.parent().parent();
            if ($this.val() == "E") {//增加子列表
                if ($tr.parents("tr.sub").length > 0) {
                    alert("不支持多级列表！");
                    $this.val("S");
                    return;
                }
                $tr.find("td:first").addClass("e");
                var $subTable = $(app.table_tpl.join(''));
                $tr.after($subTable);
                var $tr_body = $subTable.find("table tbody");
                $tr_body.prev().find("tr .add-field").click(app.bindAddFieldEvent)
                && $tr_body.find("tr .del-field").click(app.bindDelFieldEvent) && $tr_body.find("tr .in_type").change(app.bindTypeChangeEvent);
            } else {//删除子列表
                $tr.find("td:first").hasClass("e") && $tr.next().remove() && $tr.removeClass("e");
            }

        },
        /**
         * 绑定过路属性选择事件
         */
        bindDirectChangeEvent: function () {
            $('input:radio[name="isDirect"]').click(function () {
                $('input:radio').removeProp("checked");
                $(this).prop("checked", 'checked');
                if ($(this).val() == "true") {
                    $("#direct_type").removeClass("hide");
                    $("#normal_service").addClass("hide");
                    $('input:radio[name="directType"][value="database"]').prop("checked", 'checked');
                } else {
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
                app.saveServiceConf();
            });
        },
        /**
         * 保存服务配置
         */
        saveServiceConf: function () {
            var serviceConf = {};
            var input, output;
            //1、组装基本信息
            serviceConf['servicePath'] = $("#service_path").val();
            serviceConf['name'] = decodeURI(app.getQueryString("name"));//服务名称
            serviceConf['id'] = $("#service_id").val();//服务ID
            serviceConf['desc'] = $("#service_desc").val();//服务描述
            var direct = $("input:radio[name='isDirect']:checked").val();
            serviceConf['direct'] = direct;//是否过路服务
            direct == "true" && (serviceConf['directtype'] = $("input:radio[name='directType']:checked").val());//过路服务类型
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
            });
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
                if (type == "E") {
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
            if (!name) return null;
            var field = {};
            field['name'] = $el.find(".in_name").val();
            field['type'] = $el.find(".in_type").val();
            field['required'] = $el.find(".in_required").val();
            field['desc'] = $el.find(".in_desc").val();
            field['regexp'] = $el.find(".regexp").val();
            return field;
        },
        /**
         * 绑定设计服务流程
         */
        bindDesignFlow: function () {
            $("#design_flow").click(function () {
                var flowConfPath = $("#service_id").val();
                var title = $("#service_desc").val();
                var url = "designFlow.html?path=" + flowConfPath + "&title=" + title;
                var winName = "newWin";
                var awidth = screen.availWidth / 10 * 9;       //窗口宽度,需要设置
                var aheight = screen.availHeight / 5 * 4;         //窗口高度,需要设置
                var atop = (screen.availHeight - aheight) / 2;  //窗口顶部位置,一般不需要改
                var aleft = (screen.availWidth - awidth) / 2;   //窗口放中央,一般不需要改
                var param0 = "scrollbars=0,status=0,menubar=0,resizable=no,location=0"; //新窗口的参数
                var params = "top=" + atop + ",left=" + aleft + ",width=" + awidth + ",height=" + aheight + "," + param0;
                var win = window.open(url, winName, params); //打开新窗口
                win.focus(); //新窗口获得焦点
            });
        },

        getQueryString: function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if (r != null)return r[2];
            return null;
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
            '<option value="T">日期型</option>',
            '<option value="P">密文型</option>',
            '<option value="E">列表型</option>',
            '</select>',
            '</td>',
            '<td><input type="text" class="regexp" placeholder="正则表达式"></td>',
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
        table_tpl: ['<tr class="sub"><td></td><td colspan="5" style="border: 1px solid #ddd;"><table>',
            '<thead>',
            '<tr>',
            '<td class="add-field second-level">✚</td>',
            '<td>字段名称</td>',
            '<td>字段类型</td>',
            '<td>正则表达式</td>',
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
            '<option value="T">日期型</option>',
            '<option value="P">密文型</option>',
            '<option value="E">列表型</option>',
            '</select>',
            '</td>',
            '<td><input type="text" class="regexp" placeholder="正则表达式"></td>',
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