package com.lamb.framework.util;

import com.lamb.framework.base.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title : 热加载系统配置文件</p>
 * <p>Description : 定时刷新配置内容</p>
 * <p>Date : 2018/8/24 </p>
 *
 * @author : hejie
 */
public class PropertySourceHotLoading {
    private final static Logger logger = LoggerFactory.getLogger(PropertySourceHotLoading.class);
    /**
     * PropertySource的key
     */
    private final static String DYNAMIC_CONFIG_NAME = "dynamic_config";

    /**
     * 启动热加载
     * @param threadSize 启动线程数
     * @param delay 延迟时间（秒）
     * @param period 间隔时间（秒）
     */
    public static void init(int threadSize, int delay, int period){
        //1、创建计划实例
        final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(threadSize);
        //2、创建任务实例
        final TimerTask dynamicLoad = new TimerTask() {
            @Override
            public void run() {
                try {
                    AbstractEnvironment environment = (AbstractEnvironment) Framework.getEnvironment();
                    //更新系统配置到环境参数中   todo 通过配置中心加载系统配置
                    environment.getPropertySources().addFirst(
                            new PropertySourcesLoader().load(
                                    new FileSystemResource(
                                            new File( Framework.getProperty("server.config.path") )),DYNAMIC_CONFIG_NAME,null)
                    );
                } catch (IOException e) {
                    logger.error("【系统配置】热加载失败！");
                    e.printStackTrace();
                }
            }
        };
        //3、执行定时任务
        scheduled.scheduleAtFixedRate(dynamicLoad, delay, period, TimeUnit.SECONDS);
    }
}
