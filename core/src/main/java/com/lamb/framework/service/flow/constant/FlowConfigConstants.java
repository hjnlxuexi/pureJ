package com.lamb.framework.service.flow.constant;

/**
 * <p>Title : 流程配置常量类</p>
 * <p>Description : 标签定义，属性定义，表达式定义</p>
 * <p>Date : 2017/3/2 16:00</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class FlowConfigConstants {
    //==========标签定义==========
    //根节点
    public static final String FLOW_TAG = "flow";
    //步骤节点
    public static final String STEP_TAG = "step";
    //转向节点
    public static final String FORWARD_TAG = "forward";

    //==========属性定义==========
    //流程名称
    public static final String FLOW_TITLE = "title";
    //步骤索引
    public static final String STEP_INDEX = "index";
    //步骤映射处理类
    public static final String STEP_REF = "ref";
    //下一步
    public static final String STEP_NEXT = "next";
    //步骤描述
    public static final String STEP_DESC = "desc";
    //转向条件
    public static final String FORWARD_CONDITION = "condition";
    //转向步骤索引
    public static final String FORWARD_TO = "to";
    //转向描述
    public static final String FORWARD_DESC = "desc";

    //==========条件表达式操作符定义==========
    //与
    public static final String OPERATOR_AND = "and";
    //或
    public static final String OPERATOR_OR = "or";
    //非
    public static final char OPERATOR_NOT = '!';
    //相等
    public static final String OPERATOR_EQ = "eq";
    //不相等
    public static final String OPERATOR_UN_EQ = "uneq";
    //变量前缀
    public static final char OPERATOR_AT = '@';

    //数据库原子服务、外部原子服务，分隔符
    public static final String OP_SEPARATOR = ":";
}
