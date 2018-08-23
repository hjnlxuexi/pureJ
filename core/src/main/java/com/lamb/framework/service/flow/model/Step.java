package com.lamb.framework.service.flow.model;

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
     * 左偏移（绘图）
     */
    private String left;
    /**
     * 上偏移（绘图）
     */
    private String top;
    /**
     * 步骤跳转映射列表
     */
    private List<Forward> mapping = new ArrayList<Forward>();
    /**
     * 上一步骤
     */
    private Step previousStep;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Forward> getMapping() {
        return mapping;
    }

    public void setMapping(List<Forward> mapping) {
        this.mapping = mapping;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public Step getPreviousStep() {
        return previousStep;
    }

    public void setPreviousStep(Step previousStep) {
        this.previousStep = previousStep;
    }

}
