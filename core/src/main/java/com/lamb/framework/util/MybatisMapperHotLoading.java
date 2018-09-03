package com.lamb.framework.util;

import com.lamb.framework.base.Framework;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title : mapper文件热加载</p>
 * <p>Description : 修改mapper文件后，无需重启</p>
 * <p>Date : 2018/8/14 </p>
 *
 * @author : hejie
 */
public class MybatisMapperHotLoading {
    private static Logger logger = LoggerFactory.getLogger(MybatisMapperHotLoading.class);
    /**
     * 扫描结果
     */
    private static Resource[] mapperLocations;
    private static HashMap<String, Long> fileMapping = new HashMap<>();// 记录文件是否变化

    /**
     * 启动热加载
     * @param threadSize 启动线程数
     * @param delay 延迟时间（秒）
     * @param period 间隔时间（秒）
     */
    public static void init(int threadSize, int delay, int period) {
        //1、创建计划实例
        final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(threadSize);
        //2、创建任务实例
        final TimerTask refreshMapperTask = new TimerTask() {
            @Override
            public void run() {
                refreshMapper();
            }
        };
        //3、执行定时任务
        schedule.scheduleAtFixedRate(refreshMapperTask, delay, period, TimeUnit.SECONDS);
    }

    /**
     * 刷新Mapper
     */
    private static void refreshMapper() {
        try {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) Framework.getBean("sqlSessionFactory");
            Configuration configuration = sqlSessionFactory.getConfiguration();

            // 1、 扫描文件
            try {
                scanMapperXml();
            } catch (IOException e) {
                logger.error("sql文件路径配置错误");
                return;
            }

            // 2、 判断是否有文件发生了变化
            if ( isChanged() ) {
                // step.2.1 清理
                removeConfig(configuration);

                // 2.2、 重新加载
                for (Resource configLocation : mapperLocations) {
                    try {
                        /*
                        new XMLMapperBuilder中，第三个参数  resource名称 可以自定义
                        todo   尝试将配置中心单独拎出来，做为配置服务，包含：服务配置，mapper，系统运行参数配置
                         */
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configLocation.getInputStream(), configuration, configLocation.toString(), configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                    } catch (IOException e) {
                        logger.error("mapper文件[" + configLocation.getFilename() + "]不存在或内容格式不对");
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("【mapper文件】热加载失败！");
            e.printStackTrace();
        }
    }

    /**
     * 扫描xml文件所在的路径
     *
     * @throws IOException IO异常
     */
    private static void scanMapperXml() throws IOException {
        mapperLocations = new PathMatchingResourcePatternResolver().getResources(  Framework.getProperty("jdbc.mapper.location") );
    }

    /**
     * 清空Configuration中几个重要的缓存
     *
     * @param configuration mybatis配置对象
     * @throws Exception 异常
     */
    private static void removeConfig(Configuration configuration) throws Exception {
        Class<?> classConfig = configuration.getClass();
        clearMap(classConfig, configuration, "mappedStatements");
        clearMap(classConfig, configuration, "caches");
        clearMap(classConfig, configuration, "resultMaps");
        clearMap(classConfig, configuration, "parameterMaps");
        clearMap(classConfig, configuration, "keyGenerators");
        clearMap(classConfig, configuration, "sqlFragments");

        clearSet(classConfig, configuration, "loadedResources");

    }

    @SuppressWarnings("rawtypes")
    private static void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Map mapConfig = (Map) field.get(configuration);
        mapConfig.clear();
    }

    @SuppressWarnings("rawtypes")
    private static void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Set setConfig = (Set) field.get(configuration);
        setConfig.clear();
    }

    /**
     * 判断文件是否发生了变化
     *
     * @return 是否变化
     * @throws IOException 异常
     */
    private static boolean isChanged() throws IOException {
        boolean flag = false;
        for (Resource resource : mapperLocations) {
            String resourceName = resource.getFilename();

            boolean addFlag = !fileMapping.containsKey(resourceName);// 此为新增标识

            // 修改文件:判断文件内容是否有变化
            Long compareFrame = fileMapping.get(resourceName);
            long lastFrame = resource.contentLength() + resource.lastModified();
            boolean modifyFlag = null != compareFrame && compareFrame != lastFrame;// 此为修改标识

            // 新增或是修改时,存储文件
            if (addFlag || modifyFlag) {
                fileMapping.put(resourceName, lastFrame);// 文件内容帧值
                flag = true;
            }
        }
        return flag;
    }

}
