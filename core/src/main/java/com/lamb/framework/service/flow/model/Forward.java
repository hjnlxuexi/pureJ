package com.lamb.framework.service.flow.model;

/**
 * <p>Title : 转向实体</p>
 * <p>Description : 定义转向类</p>
 * <p>Date : 2017/3/2 16:43</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class Forward {
    /**
     * 条件表达式
     */
    private String condition;
    /**
     * 转向跳转的步骤索引
     */
    private String to;
    /**
     * 转向描述
     */
    private String desc;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
