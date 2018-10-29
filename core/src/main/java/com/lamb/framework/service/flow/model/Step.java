package com.lamb.framework.service.flow.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title : 步骤实体</p>
 * <p>Description : 定义步骤类</p>
 * <p>Date : 2017/3/2 16:46</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Data
public class Step {
    /**
     * 步骤索引
     */
    private String index;
    /**
     * 下一步索引
     */
    private String next;
    /**
     * 步骤映射处理类
     */
    private String ref;
    /**
     * 步骤描述
     */
    private String desc;
    /**
     * 步骤跳转映射列表
     */
    private List<Forward> mapping = new ArrayList<>();
    /**
     * 上一步骤
     */
    private Step previousStep;

}
