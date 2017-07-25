package com.lamb;

import com.lamb.framework.base.Framework;
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
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.debug("启动App...");
        ApplicationContext applicationContext = SpringApplication.run(Application.class , args);
        Framework.setSpringCtx(applicationContext);
    }
}
