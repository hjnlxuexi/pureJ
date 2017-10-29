package com.lamb;

import com.lamb.framework.base.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>Title : 应用启动入口</p>
 * <p>Description : 通过springBoot管理启动的应用入口</p>
 * <p>Date : 2017/2/28 21:03</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@SpringBootApplication(scanBasePackages = "com.lamb")
public class Application  extends SpringBootServletInitializer {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * 以java引用形式启动
     * @param args 参数
     */
    public static void main(String[] args) {
        logger.debug("启动App...");
        ApplicationContext applicationContext = SpringApplication.run(Application.class , args);
        Framework.setSpringCtx(applicationContext);
    }

    /**
     * 外部web容器支持
     * @param builder 应用构建器
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    /**
     * 外部web容器支持，获取容器上下文
     * @param application Spring应用
     * @return
     */
    @Override
    protected WebApplicationContext run(SpringApplication application){
        WebApplicationContext context = super.run(application);
        logger.debug("上下文："+context);
        Framework.setSpringCtx(context);
        return context;
    }
}
