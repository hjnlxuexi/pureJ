package com.lamb.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
@Component
public class PropertySourceHotLoading {
    private static Logger logger = LoggerFactory.getLogger(PropertySourceHotLoading.class);
    /**
     * PropertySource的key
     */
    private final static String DYNAMIC_CONFIG_NAME = "dynamic_config";
    /**
     * 是否启用热加载
     */
    @Value("${server.config.hotLoading}")
    private Boolean isHotLoading;
    /**
     * 热加载的配置文件路径
     */
    @Value("${server.config.path}")
    private String confPath;

    /**
     * 系统运行配置参数
     */
    @Resource
    private AbstractEnvironment environment;

    /**
     * 初始化定时任务
     */
    @PostConstruct
    private void init(){
        //是否开启热加载
        if ( !isHotLoading ) return;

        //1、创建计划实例
        final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
        //2、创建任务实例
        final TimerTask dynamicLoad = new TimerTask() {
            @Override
            public void run() {
                try {
                    //更新系统配置到环境参数中
                    environment.getPropertySources().addFirst(
                            new PropertySourcesLoader().load(
                                    new FileSystemResource(
                                            new File(confPath)),DYNAMIC_CONFIG_NAME,null)
                    );
                    logger.info("【系统配置】热加载成功！");
                } catch (IOException e) {
                    logger.error("【系统配置】热加载失败！");
                    e.printStackTrace();
                }
            }
        };
        //3、执行定时任务
        scheduled.scheduleAtFixedRate(dynamicLoad, 60, 30, TimeUnit.SECONDS);
    }
}
