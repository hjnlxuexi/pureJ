package com.lamb.framework.channel;

import com.lamb.framework.base.Context;
import com.lamb.framework.channel.builder.ICoreChannelBuilder;
import com.lamb.framework.channel.parser.ICoreChannelParser;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.listener.IListener;
import com.lamb.framework.service.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>Title : 统一控制器</p>
 * <p>Description : 服务入口，控制路由到具体服务类</p>
 * <p>Date : 2017/2/28 17:17</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@RestController
public class ServiceRouter {
    private static Logger logger = LoggerFactory.getLogger(ServiceRouter.class);
    /**
     * 请求报文解析器
     */
    @Resource
    private ICoreChannelParser coreDataParser;
    /**
     * 响应报文组装器
     */
    @Resource
    private ICoreChannelBuilder coreDataBuilder;
    /**
     * 过路交易服务类
     */
    @Resource
    private IService directService;
    /**
     * 流程服务
     */
    @Resource
    private IService flowEngineService;
    /**
     * 监听器列表
     */
    @Resource
    private List<IListener> listeners;

    /**
     * 服务处理
     * 1、拦截所有请求
     * 2、服务分发
     * 3、服务处理
     *
     * @param context 数据总线
     * @return 输出数据
     */
    @RequestMapping("/")
    public Map execute(@RequestBody Context context) {
        try {
            long start = System.currentTimeMillis();
            //1、前置处理
            this.before(context);
            logger.debug("执行服务【" + context.getServiceCode() + "】，开始...");
            //2、服务路由判断
            IService service = context.isDirect() ? directService : flowEngineService;
            //3、执行服务
            service.execute(context);
            //4、后置处理
            this.after(context);
            long end = System.currentTimeMillis();
            logger.debug("执行服务【" + context.getServiceCode() + "】，结束【" + (end - start) + "毫秒】");
            //5、返回响应报文
            return context.getServiceOutput();
        }catch (Exception e) {
            if (e instanceof ServiceRuntimeException)
                return coreDataBuilder.buildError((ServiceRuntimeException)e);
            return coreDataBuilder.buildError(new ServiceRuntimeException("4000" , this.getClass(), e, context.getServiceName()));
        }
    }

    /**
     * 服务前置处理
     *
     * @param context 数据总线
     */
    private void before(Context context) {
        //1、报文解析
        this.coreDataParser.parse(context);

        //2、处理监听服务
        for (IListener listener : listeners) {
            listener.doBefore(context);
        }
    }

    /**
     * 服务后置处理
     *
     * @param context 数据总线
     */
    private void after(Context context) {
        //1、组装响应数据
        this.coreDataBuilder.build(context);

        //2、处理监听服务
        for (IListener listener : listeners) {
            listener.doAfter(context);
        }
    }
}
