package com.lamb.framework.service.flow.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title : 流程实体</p>
 * <p>Description : 定义流程实体类</p>
 * <p>Date : 2017/3/2 16:51</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Data
public class Flow {
    /**
     * 流程标题
     */
    private String title;
    /**
     * 流程步骤列表
     */
    private Map<String , Step> steps = new HashMap<>();
}
