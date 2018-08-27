package com.lamb;

import com.lamb.discover.ServiceAutoDiscover;
import com.lamb.framework.base.Framework;
import com.lamb.framework.util.MybatisMapperHotLoading;
import com.lamb.framework.util.PropertySourceHotLoading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * <p>Title : 应用启动入口</p>
 * <p>Description : 通过springBoot管理启动的应用入口</p>
 * <p>Date : 2017/2/28 21:03</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@SpringBootApplication(scanBasePackages = "com.lamb")
public class Application  {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * 以java引用形式启动
     * @param args 参数
     */
    public static void main(String[] args) {
        logger.debug("启动App...");
        ApplicationContext applicationContext = SpringApplication.run(Application.class , args);
        Framework.setSpringCtx(applicationContext);
        //启动热加载
        hotLoading();
        //服务发现
        discovery();
    }

    /**
     * 热加载
     * 1、mapper文件热加载
     * 2、系统配置热加载
     */
    private static void hotLoading(){
        //启动 mapper文件热加载
        MybatisMapperHotLoading.init(2 , 60 , 30);
        //启动 系统配置文件热加载
        PropertySourceHotLoading.init(2 , 60 , 30);
    }

    /**
     * 启动服务发现
     * 通过特定的数据结构注册到zookeeper
     */
    private static void discovery(){
        ServiceAutoDiscover.init();
    }

}
