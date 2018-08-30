package com;

import com.lamb.discover.ServiceAutoDiscover;
import com.lamb.framework.base.Framework;
import com.lamb.framework.listener.AbstractListener;
import com.lamb.framework.util.BizConfigHotLoading;
import com.lamb.framework.util.MybatisMapperHotLoading;
import com.lamb.framework.util.PropertySourceHotLoading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Comparator;
import java.util.List;

/**
 * <p>Title : 应用启动入口</p>
 * <p>Description : 通过springBoot管理启动的应用入口</p>
 * <p>Date : 2017/2/28 21:03</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@SpringBootApplication
public class Application  {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * 以java引用形式启动
     * @param args 参数
     */
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class , args);
        Framework.setSpringCtx(applicationContext);
        //设置监听器
        setListeners(applicationContext);
        //启动热加载
        hotLoading();
        //服务发现
        discovery();
        logger.info("系统启动成功！！！");
    }

    /**
     * 热加载
     * 1、mapper文件热加载
     * 2、系统配置热加载
     */
    private static void hotLoading(){
        //启动 mapper文件热加载
        MybatisMapperHotLoading.init(2 , 0 , 30);
        //启动 系统配置文件热加载
        PropertySourceHotLoading.init(2 , 0 , 30);
        //启动 业务配置热加载
        BizConfigHotLoading.init(5 , 0 , 10);
    }

    /**
     * 启动服务发现
     * 通过特定的数据结构注册到zookeeper
     */
    private static void discovery(){
        ServiceAutoDiscover.init();
    }

    /**
     * 设置所有监听器
     */
    private static void setListeners(ApplicationContext springCtx){
        //1、获取监听器列表
        List<AbstractListener> list = (List<AbstractListener>)springCtx.getBean("listeners");
        //2、清空列表
        list.clear();
        //3、自动扫描，并添加所有监听器
        String[] listeners = springCtx.getBeanNamesForType(AbstractListener.class);
        for (String listenerName : listeners) {
            AbstractListener listener = (AbstractListener) Framework.getBean(listenerName);
            list.add(listener);
        }
        //4、排序
        list.sort(new Comparator<AbstractListener>() {
            @Override
            public int compare(AbstractListener o1, AbstractListener o2) {
                return o1.getSort().compareTo(o2.getSort());
            }
        });
    }

}
