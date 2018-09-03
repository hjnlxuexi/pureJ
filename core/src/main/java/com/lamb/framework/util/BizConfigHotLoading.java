package com.lamb.framework.util;

import com.lamb.framework.adapter.protocol.helper.AdapterConfigParser;
import com.lamb.framework.base.Framework;
import com.lamb.framework.cache.ConfigCache;
import com.lamb.framework.channel.helper.ServiceConfigParser;
import com.lamb.framework.service.flow.helper.FlowConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title : 热加载业务配置文件</p>
 * <p>Description : 定时刷新业务配置内容，到内存缓存</p>
 * <p>Date : 2018/8/24 </p>
 *
 * @author : hejie
 */
public class BizConfigHotLoading {
    private static Logger logger = LoggerFactory.getLogger(BizConfigHotLoading.class);
    /**
     * http协议名
     */
    public final static String HTTP_PROTOCOL = "http";
    public final static String LOCAL_CONF_POSTFIX = ".xml";

    /**
     * 启动热加载
     *
     * @param threadSize 启动线程数
     * @param delay      延迟时间（秒）
     * @param period     间隔时间（秒）
     */
    public static void init(int threadSize, int delay, int period) {
        //1、创建计划实例
        final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(threadSize);
        //2、创建任务实例
        final TimerTask dynamicLoad = new TimerTask() {
            @Override
            public void run() {
                refreshBizConf();
            }
        };
        //3、执行定时任务
        scheduled.scheduleAtFixedRate(dynamicLoad, delay, period, TimeUnit.SECONDS);
    }

    /**
     * 加载配置
     */
    private static void refreshBizConf() {
        //1、加载service配置
        String servicePath = Framework.getProperty("biz.conf.service");
        refresh(servicePath, 1);

        //2、加载Flow配置
        String flowPath = Framework.getProperty("biz.conf.flow");
        refresh(flowPath, 2);

        //3、加载adapter配置
        String adapterPath = Framework.getProperty("biz.conf.adapter");
        refresh(adapterPath, 3);

    }

    /**
     * 刷新配置内容
     * 1、本地配置文件
     * 2、远端配置中心
     *
     * @param path 配置路径
     * @param type 配置类型
     */
    private static void refresh(String path, int type) {
        if (!path.startsWith(HTTP_PROTOCOL)) {
            //加载本地的业务配置
            getBizConfByLocal(path, type);
        } else {
            //加载远端配置中心
            getBizConfByRemote(path, type);
        }
    }

    /**
     * 加载本地的业务配置
     *
     * @param filePath 文件路径
     * @param type     配置类型：1-服务配置、2-流程配置、3-适配器配置
     */
    private static void getBizConfByLocal(String filePath, int type) {
        //遍历文件路径
        File root = new File(filePath);
        File[] files = root.listFiles();
        //目录为空，忽略
        if (files == null) return;

        for (File file : files) {
            //为目录
            if (file.isDirectory() && file.canRead()) {
                getBizConfByLocal(file.getAbsolutePath(), type);
                continue;
            }
            //为文件
            if (file.isFile()) {
                //1、拼接服务编码
                String strFileName = file.getAbsolutePath();
                if (!strFileName.endsWith(LOCAL_CONF_POSTFIX)) continue;
                Object config = null;
                //2、解析文件
                if (type == 1) {//服务配置
                    ServiceConfigParser serviceConfigParser = (ServiceConfigParser) Framework.getBean("serviceConfigParser");
                    config = serviceConfigParser.parseNodes(file.getAbsolutePath());
                } else if (type == 2) {//流程配置
                    FlowConfigParser flowConfigParser = (FlowConfigParser) Framework.getBean("flowConfigParser");
                    config = flowConfigParser.parseNodes(file.getAbsolutePath());
                } else if (type == 3) {//适配器配置
                    AdapterConfigParser adapterConfigParser = (AdapterConfigParser) Framework.getBean("adapterConfigParser");
                    config = adapterConfigParser.parseNodes(file.getAbsolutePath());
                }
                //3、加入缓存
                String serviceCode = strFileName.substring(0 , strFileName.indexOf(LOCAL_CONF_POSTFIX));
                if (config != null) ConfigCache.addConfig(serviceCode, config);
            }
        }
    }

    /**
     * 加载本地的业务配置
     * todo 通过配置中心获取
     *
     * @param filePath 文件路径
     * @param type     配置类型：1-服务配置、2-流程配置、3-适配器配置
     */
    private static void getBizConfByRemote(String filePath, int type) {

    }
}
