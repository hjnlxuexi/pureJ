package com.lamb.framework.service;

import com.lamb.framework.base.Context;

/**
 * <p>Title : 统一服务接口</p>
 * <p>Description : 定义统一的服务标准</p>
 * <p>Date : 2017/2/28 17:41</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public interface IService {
    /**
     * 服务功能入口
     * @param context 数据总线
     */
    public void execute(Context context);
}
