package com.lamb.framework.channel.constant;

/**
 * <p>Title : 服务配置的常量定义</p>
 * <p>Description : 定义服务配置中的标签/属性常量</p>
 * <p>Date : 2017/3/1 9:53</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class ServiceConfConstants {
    //==========服务标签定义==========
    //根节点标签
    public static final String ROOT_TAG = "service";
    //服务名称标签
    public static final String NAME_TAG = "name";
    //服务描述标签
    public static final String DESC_TAG = "desc";
    //过路交易标签
    public static final String DIRECT_TAG = "direct";
    //过路交易类型标签
    public static final String DIRECT_TYPE_TAG = "directtype";
    //服务id标签
    public static final String ID_TAG = "id";

    //服务输入标签
    public static final String INPUT_TAG = "input";
    //服务输出标签
    public static final String OUTPUT_TAG = "output";
    //数据域标签
    public static final String FIELD_TAG = "field";

    //==========数据域属性==========
    //字段名
    public static final String NAME_PROP = "name";
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

    //==========过路交易类型==========
    //数据库交易
    public static final String DIRECT_TYPE_DB = "database";
    //外部服务交易
    public static final String DIRECT_TYPE_PROTOCOL = "protocol";

    //=========列表型字段============
    public static final String FIELD_KEY_LIST = "list";
}
