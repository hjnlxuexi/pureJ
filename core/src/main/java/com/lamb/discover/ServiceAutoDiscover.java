package com.lamb.discover;

import com.lamb.framework.base.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    public static void init(){
        //0、是否开启自动发现
        if ( !Boolean.valueOf(Framework.getProperty("service.discover.open")) ) {
            logger.debug("【服务发现】未开启！");
            return;
        }
        if ( !Boolean.valueOf(Framework.getProperty("service.auto-discover.open")) ) {
            logger.debug("【服务发现】自动扫描未开启！");
            return;
        }
        //1、创建计划实例
        final ScheduledExecutorService schedule = Executors.newScheduledThreadPool( Integer.valueOf(Framework.getProperty("service.auto-discover.threads")) );
        //2、创建任务实例
        final TimerTask getCurFilesTask = new TimerTask() {
            @Override
            public void run() {
                ServiceDiscover.discover();
            }
        };
        //3、执行定时任务
        schedule.scheduleAtFixedRate(  getCurFilesTask,
                Integer.valueOf(Framework.getProperty("service.auto-discover.delay")),
                Integer.valueOf(Framework.getProperty("service.auto-discover.period")),
                TimeUnit.SECONDS);

    }

}
