package com.lamb.framework.service.direct;

import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.channel.constant.ServiceConfConstants;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.service.IService;
import com.lamb.framework.service.OP;
import com.lamb.framework.service.op.DataBaseOP;
import com.lamb.framework.service.op.ProtocolOP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>Title : 公共过路服务</p>
 * <p>Description : 无需任何业务处理，直接请求外部数据并返回</p>
 * <p>Date : 2017/2/28 17:44</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Slf4j
@Service
public class SimpledService implements IService {
    /**
     * 外部原子服务
     */
    @Resource
    private ProtocolOP protocolOP;
    /**
     * 数据库原子服务
     */
    @Resource
    private DataBaseOP dataBaseOP;

    /**
     * 过路服务统一入口
     *
     * @param context 数据总线
     */
    @Override
    @Transactional
    public void execute(Context context) {
        try {
            String type = context.getType();
            switch (type) {
                //1、外部服务
                case ServiceConfConstants.TYPE_PROTOCOL:
                    protocolOP.execute(context);
                    break;
                //2、数据库服务
                case ServiceConfConstants.TYPE_DB:
                    dataBaseOP.execute(context);
                    break;
                //3、原子服务
                case ServiceConfConstants.TYPE_SINGLE:
                    String serviceId = context.getServiceId();
                    OP op = (OP) Framework.getBean(serviceId);
                    op.execute(context);
                    break;
                //4、过路类型不合法
                default:
                    throw new ServiceRuntimeException("1011", this.getClass(), context.getServiceName(), type);
            }
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                throw (ServiceRuntimeException)e;
            throw new ServiceRuntimeException("4000" , this.getClass(), e, context.getServiceName());
        }
    }
}
