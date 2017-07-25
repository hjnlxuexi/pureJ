package com.lamb.framework.listener;

import com.lamb.framework.base.Context;

/**
 * <p>Title : 监听器</p>
 * <p>Description : 处理MainController切面事物</p>
 * <p>Date : 2017/3/3 16:25</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public interface IListener {
    /**
     * 前置处理
     * @param context 数据总线
     */
    public void doBefore(Context context);

    /**
     * 后置处理
     * @param context 数据总线
     */
    public void doAfter(Context context);
}
