package com.lamb.app.listener;

import com.lamb.framework.base.Context;
import com.lamb.framework.listener.AbstractListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>Title : 测试监听器</p>
 * <p>Description : 测试监听器</p>
 * <p>Date : 2018/8/28 </p>
 *
 * @author : hejie
 */
@Slf4j
@Component
public class FirstListener extends AbstractListener {
    /**
     * 构造函数，设置顺序
     */
    public FirstListener() {
        super(1);
    }

    /**
     * 前置处理
     *
     * @param context 数据总线
     */
    @Override
    public void doBefore(Context context) {
        log.debug("第一个Listener，doBefore......");
    }

    /**
     * 后置处理
     *
     * @param context 数据总线
     */
    @Override
    public void doAfter(Context context) {
        log.debug("第一个Listener，doAfter......");
    }
}
