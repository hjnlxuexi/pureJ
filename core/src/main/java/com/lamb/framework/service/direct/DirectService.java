package com.lamb.framework.service.direct;

import com.lamb.framework.base.Context;
import com.lamb.framework.channel.constant.ServiceConfConstants;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.service.IService;
import com.lamb.framework.service.op.DataBaseOP;
import com.lamb.framework.service.op.ProtocolOP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Service
public class DirectService implements IService {
    private static Logger logger = LoggerFactory.getLogger(DirectService.class);
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
            logger.debug("执行过路服务【" + context.getServiceName() + "】，开始...");
            long start = System.currentTimeMillis();
            String directType = context.getDirectType();
            //1、外部服务
            if (directType.equals(ServiceConfConstants.DIRECT_TYPE_PROTOCOL)) {
                protocolOP.execute(context);
            }else {
                //2、数据库服务
                dataBaseOP.execute(context);
            }
            long end = System.currentTimeMillis();
            logger.debug("执行过路服务【" + context.getServiceName() + "】，结束【" + (end - start) + "毫秒】");
        } catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                throw e;
            throw new ServiceRuntimeException("4000" , this.getClass(), e, context.getServiceName());
        }
    }
}
