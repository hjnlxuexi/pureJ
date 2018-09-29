package com.lamb.framework.adapter.protocol.constant;

/**
 * <p>Title : 外部服务配置的常量定义</p>
 * <p>Description : 定义外部服务配置中的标签/属性常量</p>
 * <p>Date : 2017/3/1 9:53</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class AdapterConfConstants {
    //==========适配器标签定义==========
    //根节点标签
    public static final String ROOT_TAG = "adapter";
    //外部服务名称标签
    public static final String NAME_TAG = "name";
    //外部服务地址标签
    public static final String HOST_TAG = "host";
    //连接超时标签
    public static final String CONNECT_TIMEOUT_TAG = "connectTimeout";
    //响应超时标签
    public static final String RESPONSE_TIMEOUT_TAG = "responseTimeout";
    //通讯工具标签
    public static final String NET_TOOL_TAG = "netTool";
    //服务路径
    public static final String SERVICE_TAG = "service";
    //字符集
    public static final String CHARSET_TAG = "charset";
    //数据格式
    public static final String CONTENT_TYPE_TAG = "contentType";

    //服务输入标签
    public static final String INPUT_TAG = "input";
    //服务输出标签
    public static final String OUTPUT_TAG = "output";
    //数据域标签
    public static final String FIELD_TAG = "field";

    //==========数据域属性==========
    //字段名
    public static final String NAME_PROP = "name";
    //目标字段名
    public static final String TARGET_NAME_PROP = "targetName";
    //字段类型
    public static final String TYPE_PROP = "type";
    //字段校验，正则表达式
    public static final String REGEXP_PROP = "regexp";
    //字段必输
    public static final String REQUIRED_PROP = "required";
    //字段描述
    public static final String DESC_PROP = "desc";

    //==========字段类型==========
    //字符串类型
    public static final String FIELD_TYPE_S = "S";
    //整型
    public static final String FIELD_TYPE_I = "I";
    //浮点型
    public static final String FIELD_TYPE_F = "F";
    //布尔值
    public static final String FIELD_TYPE_B = "B";
    //时间类型
    public static final String FIELD_TYPE_T = "T";
    //密文
    public static final String FIELD_TYPE_P = "P";
    //列表
    public static final String FIELD_TYPE_E = "E";

    //=========列表型字段key============
    public static final String FIELD_KEY_LIST = "children";
}
