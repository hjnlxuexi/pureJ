package com.lamb.framework.util;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
@Component
public class MybatisMapperHotLoading {
    private static Logger logger = LoggerFactory.getLogger(MybatisMapperHotLoading.class);
    /**
     * 是否启用mapper热加载
     */
    @Value("${jdbc.mapper.hotLoading}")
    private boolean isMapperHotLoading;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    /**
     * mapper文件位置
     */
    @Value("${jdbc.mapper.location}")
    private String sqlPath;
    private Resource[] mapperLocations;
    private HashMap<String, Long> fileMapping = new HashMap<>();// 记录文件是否变化

    @PostConstruct
    private void schedule() {
        if (!isMapperHotLoading) return;
        //1、创建计划实例
        final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(2);
        //2、创建任务实例
        final TimerTask getCurFilesTask = new TimerTask() {
            @Override
            public void run() {
                refreshMapper();
            }
        };
        //3、执行定时任务
        schedule.scheduleAtFixedRate(getCurFilesTask, 30, 30, TimeUnit.SECONDS);
    }

    private void refreshMapper() {
        try {
            Configuration configuration = this.sqlSessionFactory.getConfiguration();

            // step.1 扫描文件
            try {
                this.scanMapperXml();
            } catch (IOException e) {
                logger.error("sql文件路径配置错误");
                return;
            }

            // step.2 判断是否有文件发生了变化
            if (this.isChanged()) {
                logger.debug("==============刷新mapper开始......===============");
                // step.2.1 清理
                this.removeConfig(configuration);

                // step.2.2 重新加载
                for (Resource configLocation : mapperLocations) {
                    try {
                        /*
                        new XMLMapperBuilder中，第三个参数  resource名称 可以自定义
                        todo   尝试将配置中心单独拎出来，做为配置服务，包含：服务配置，mapper，系统运行参数配置
                         */
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configLocation.getInputStream(), configuration, configLocation.toString(), configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                        logger.info("mapper文件[" + configLocation.getFilename() + "]缓存加载成功");
                    } catch (IOException e) {
                        logger.error("mapper文件[" + configLocation.getFilename() + "]不存在或内容格式不对");
                    }
                }

                logger.debug("==============刷新后mapper中的内容===============");
                for (String name : configuration.getMappedStatementNames()) {
                    logger.debug(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描xml文件所在的路径
     *
     * @throws IOException IO异常
     */
    private void scanMapperXml() throws IOException {
        this.mapperLocations = new PathMatchingResourcePatternResolver().getResources(sqlPath);
    }

    /**
     * 清空Configuration中几个重要的缓存
     *
     * @param configuration mybatis配置对象
     * @throws Exception 异常
     */
    private void removeConfig(Configuration configuration) throws Exception {
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
    private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Map mapConfig = (Map) field.get(configuration);
        mapConfig.clear();
    }

    @SuppressWarnings("rawtypes")
    private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
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
    private boolean isChanged() throws IOException {
        boolean flag = false;
        for (Resource resource : mapperLocations) {
            String resourceName = resource.getFilename();

            boolean addFlag = !fileMapping.containsKey(resourceName);// 此为新增标识

            // 修改文件:判断文件内容是否有变化
            Long compareFrame = fileMapping.get(resourceName);
            long lastFrame = resource.contentLength() + resource.lastModified();
            boolean modifyFlag = null != compareFrame && compareFrame.longValue() != lastFrame;// 此为修改标识

            // 新增或是修改时,存储文件
            if (addFlag || modifyFlag) {
                fileMapping.put(resourceName, Long.valueOf(lastFrame));// 文件内容帧值
                flag = true;
            }
        }
        return flag;
    }

}
