package com.lamb.framework.util;

import com.lamb.framework.base.Framework;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
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
    private final static Logger logger = LoggerFactory.getLogger(MybatisMapperHotLoading.class);

    private static Resource[] mapperLocations;   //扫描结果
    private static HashMap<String, Long> fileMapping = new HashMap<>();// 记录文件是否变化

    private static boolean refresh;  // 刷新启用后，是否启动了刷新线程

    private static List<Resource> addModifyLocations = new ArrayList<>();  //新增、修改的location
    private static List<String> delLocations = new ArrayList<>();   //删除的location


    /**
     * 启动热加载
     * todo   尝试将配置中心单独拎出来，做为配置服务，包含：服务配置，mapper，系统运行参数配置
     *
     * @param threadSize 启动线程数
     * @param delay      延迟时间（秒）
     * @param period     间隔时间（秒）
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
            refresh = true;
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) Framework.getBean("sqlSessionFactory");
            Configuration configuration = sqlSessionFactory.getConfiguration();

            // 1、 扫描并整理资源文件
            try {
                scanMapperXml();
                collateLocations();
            } catch (IOException e) {
                refresh = false;
                logger.error("mapper文件路径配置错误");
                return;
            }

            if (delLocations.isEmpty() && addModifyLocations.isEmpty()) return;
            // 2、 清理内存中原有资源，只需执行一次
            resetConfig(configuration);

            //已加载的资源
            Field loadedResourcesField = configuration.getClass().getDeclaredField("loadedResources");
            loadedResourcesField.setAccessible(true);
            Set loadedResourcesSet = ((Set) loadedResourcesField.get(configuration));

            // 3、 删除
            for (String location : delLocations) {
                loadedResourcesSet.remove(location);
            }

            // 4、新增 && 修改
            for (Resource location : addModifyLocations) {
                try {
                    // 清理已加载的资源标识，方便让它重新加载。
                    String locationPath = ((FileSystemResource) location).getPath();
                    loadedResourcesSet.remove(locationPath);
                    // 重新加载 变化的mapper配置
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(location.getInputStream(), configuration, locationPath, configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                } catch (IOException e) {
                    logger.error("mapper文件[" + location.getFilename() + "]不存在或内容格式不对");
                }
            }

            //5、还原
            addModifyLocations.clear();
            delLocations.clear();
            refresh = false;
        } catch (Exception e) {
            refresh = false;
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
        mapperLocations = new PathMatchingResourcePatternResolver().getResources(Framework.getProperty("jdbc.mapper.location"));
    }

    /**
     * 整理mapper资源
     * 1、新增 && 修改
     * 2、删除
     *
     * @throws IOException 异常
     */
    private static void collateLocations() throws IOException {
        //0、删除
        for (String location : fileMapping.keySet()) {
            boolean exist = false;
            for (Resource resource : mapperLocations) {
                String resourceName = ((FileSystemResource) resource).getPath();
                if (location.equals(resourceName)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                delLocations.add(location);
            }
        }
        //1、新增 && 修改
        for (Resource resource : mapperLocations) {
            String resourceName = ((FileSystemResource) resource).getPath();

            //新增
            boolean addFlag = !fileMapping.containsKey(resourceName);// 此为新增标识

            // 修改文件:判断文件内容是否有变化
            Long compareFrame = fileMapping.get(resourceName);
            long lastFrame = resource.contentLength() + resource.lastModified();
            boolean modifyFlag = null != compareFrame && compareFrame != lastFrame;// 此为修改标识

            // 新增或是修改时,存储文件
            if (addFlag || modifyFlag) {
                fileMapping.put(resourceName, lastFrame);// 文件内容帧值
                addModifyLocations.add(resource);
            }
        }
        //3、刷新fileMapping
        for (String location : delLocations) {
            fileMapping.remove(location);
        }
    }

    /**
     * 清理原有资源
     *
     * @param configuration mybatis配置对象
     * @throws Exception 异常
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void resetConfig(Configuration configuration) throws Exception {
        // 清理原有资源，更新为自己的StrictMap方便，增量重新加载
        // TODO 暂时无法清理删除的key
        String[] mapFieldNames = new String[]{
                "mappedStatements", "caches",
                "resultMaps", "parameterMaps",
                "keyGenerators", "sqlFragments"
        };
        for (String fieldName : mapFieldNames) {
            Field field = configuration.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Map map = ((Map) field.get(configuration));
            if (!(map instanceof StrictMap)) {
                Map newMap = new StrictMap(StringUtils.capitalize(fieldName) + "collection");
                for (Object key : map.keySet()) {
                    try {
                        newMap.put(key, map.get(key));
                    } catch (IllegalArgumentException ex) {
                        newMap.put(key, ex.getMessage());
                    }
                }
                field.set(configuration, newMap);
            }
        }
    }


    /**
     * 重写 org.apache.ibatis.session.Configuration.StrictMap 类
     * 来自 MyBatis3.4.0版本，修改 put 方法，允许反复 put更新。
     */
    public static class StrictMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -4950446264854982944L;
        private String name;

        public StrictMap(String name, int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
            this.name = name;
        }

        public StrictMap(String name, int initialCapacity) {
            super(initialCapacity);
            this.name = name;
        }

        StrictMap(String name) {
            super();
            this.name = name;
        }

        public StrictMap(String name, Map<String, ? extends V> m) {
            super(m);
            this.name = name;
        }

        @SuppressWarnings("unchecked")
        public V put(String key, V value) {
            // T如果现在状态为刷新，则刷新(先删除后添加)
            if (refresh) {
                remove(key);
                logger.debug("mybatis mapper refresh key:" + key.substring(key.lastIndexOf(".") + 1));
            }
            // end
            if (containsKey(key)) {
                throw new IllegalArgumentException(name + " already contains value for " + key);
            }
            if (key.contains(".")) {
                final String shortKey = getShortName(key);
                if (super.get(shortKey) == null) {
                    super.put(shortKey, value);
                } else {
                    super.put(shortKey, (V) new Ambiguity(shortKey));
                }
            }
            return super.put(key, value);
        }

        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new IllegalArgumentException(name + " does not contain value for " + key);
            }
            if (value instanceof Ambiguity) {
                throw new IllegalArgumentException(((Ambiguity) value).getSubject() + " is ambiguous in " + name
                        + " (try using the full name including the namespace, or rename one of the entries)");
            }
            return value;
        }

        private String getShortName(String key) {
            final String[] keyparts = key.split("\\.");
            return keyparts[keyparts.length - 1];
        }

        static class Ambiguity {
            private String subject;

            Ambiguity(String subject) {
                this.subject = subject;
            }

            String getSubject() {
                return subject;
            }
        }
    }

}
