package com.lamb.framework.service.op;

import com.lamb.framework.base.Context;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.service.OP;
import org.springframework.stereotype.Service;

/**
 * <p>Title : 异常处理原子服务</p>
 * <p>Description : 动态处理业务异常</p>
 * <p>Date : 2018/8/23 </p>
 *
 * @author : hejie
 */
@Service
public class ExceptionOP implements OP {
    /**
     * 服务功能入口
     *
     * @param context 数据总线
     */
    @Override
    public void execute(Context context) {
        String messageKey = context.getServiceId();
        throw new ServiceRuntimeException(messageKey,this.getClass());
    }
}
