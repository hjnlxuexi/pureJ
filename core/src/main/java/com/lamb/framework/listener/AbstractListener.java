package com.lamb.framework.listener;

/**
 * <p>Title : 监听器抽象父类</p>
 * <p>Description : 添加排序字段</p>
 * <p>Date : 2018/8/28 </p>
 *
 * @author : hejie
 */
public abstract class AbstractListener implements IListener {
    /**
     * 排序
     */
    private Integer sort;

    public AbstractListener(Integer sort){
        this.setSort(sort);
    }

    public Integer getSort() {
        return this.sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
