package com.lamb.discover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 描述: 服务自动发现检测器，定时扫描更新服务
 * 作者: hejie
 * 日期: 2018/6/28
 */
@Component
public class ServiceAutoDiscover {
    private static Logger logger = LoggerFactory.getLogger(ServiceAutoDiscover.class);
    /**
     * 是否开启自动发现
     */
    @Value("${service.auto-discover.open}")
    private boolean isAutoDiscover;
    /**
     * 活动线程数
     */
    @Value("${service.auto-discover.threads}")
    private int corePoolSize;
    /**
     * 初始延迟时间
     */
    @Value("${service.auto-discover.delay}")
    private int initialDelay;
    /**
     * 执行周期
     */
    @Value("${service.auto-discover.period}")
    private int period;
    /**
     * 服务发现
     */
    @Resource
    private ServiceDiscover serviceDiscover;

    @PostConstruct
    private void init(){
        //0、是否开启自动发现
        if (!serviceDiscover.discoverOpen) {
            logger.info("【服务发现】未开启！");
            return;
        }
        if (!isAutoDiscover) {
            logger.info("【服务发现】自动扫描未开启！");
            return;
        }
        //1、创建计划实例
        final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(corePoolSize);
        //2、创建任务实例
        final TimerTask getCurFilesTask = new TimerTask() {
            @Override
            public void run() {
                serviceDiscover.discover();
            }
        };
        //3、执行定时任务
        schedule.scheduleAtFixedRate(getCurFilesTask, initialDelay, period, TimeUnit.SECONDS);

    }

}
