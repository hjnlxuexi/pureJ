package com.lamb.framework.service.flow.model;

import lombok.Data;

/**
 * <p>Title : 转向实体</p>
 * <p>Description : 定义转向类</p>
 * <p>Date : 2017/3/2 16:43</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Data
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
}
