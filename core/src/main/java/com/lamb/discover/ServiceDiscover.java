package com.lamb.discover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述: 服务发现
 * 作者: hejie
 * 日期: 2018/6/28
 */
@Component
public class ServiceDiscover {
    private static Logger logger = LoggerFactory.getLogger(ServiceDiscover.class);
    /**
     * 是否开启服务发现
     */
    @Value("${service.discover.open}")
    public boolean discoverOpen;
    //public boolean discoverOpen=true;
    /**
     * 服务配置目录
     */
    @Value("${service.conf.dir}")
    private String watchedDir;
    //private String watchedDir="/Users/home/Desktop/HJ/pureJ/biz/channel/service";
    /**
     * 允许的文件后缀
     */
    private String[] suffixs = {"xml"};
    //当前文件列表
    private final ArrayList<String> curFiles = new ArrayList<String>();

    //上一版本的文件列表
    private final Map<String, String> oldMap = new HashMap<String, String>();
    //当前文件列表，对应更新时间
    private final Map<String, String> curMap = new HashMap<String, String>();
    //当前新增文件
    private final Map<String, String> addMap = new HashMap<String, String>();
    //当前删除文件
    private final Map<String, String> delMap = new HashMap<String, String>();
    //当前更新
    private final Map<String, String> updMap = new HashMap<String, String>();

    //目录扫描计数器
    private static long loop = 0;

    @Resource
    private ServiceRegister serviceRegister;
    /**
     * 服务发现，更新
     */
    public void discover(){
        //0、判断是否开启服务发现
        if ( !discoverOpen ) {
            logger.info("====================**服务发现未开启！**====================");
            return;
        }
        //1、目录扫描
        dirScanner();
        //2、更新服务
        updateServiceRegistry();
        //3、清理数据
        clearScanResult();

    }

    /**
     * 扫描目录变化
     */
    private void dirScanner(){
        logger.debug("====================**开始扫描服务配置**====================");
        if (0 == ServiceDiscover.loop) {
            //首次获取文件列表
            getFiles(curFiles, watchedDir, suffixs);
            for (String ss : curFiles) {
                //记录当前所有文件更新时间
                curMap.put(ss, "" + (new File(ss)).lastModified());
                //记录为老版本所有文件更新时间
                oldMap.put(ss, "" + (new File(ss)).lastModified());
            }
        } else {
            getFiles(curFiles, watchedDir, suffixs);
            for (String fileName : curFiles) {
                //获取上次更新时间
                String lastValue = oldMap.get(fileName);

                if (null == lastValue) {
                    //新增
                    addMap.put(fileName, "" + (new File(fileName)).lastModified());
                    logger.debug("新增服务："+fileName);
                } else if (!lastValue.equals("" + (new File(fileName)).lastModified())) {
                    //更新
                    updMap.put(fileName, "" + (new File(fileName)).lastModified());
                    logger.debug("更新服务："+fileName);
                }
                //更新文件时间
                curMap.put(fileName, "" + (new File(fileName)).lastModified());
            }
            //记录删除的文件
            for (Map.Entry<String, String> entry : oldMap.entrySet()) {
                if (curMap.containsKey(entry.getKey())) continue;

                delMap.put(entry.getKey(), entry.getValue());
                logger.debug("删除服务："+entry.getKey());
            }
            //刷新老版本
            oldMap.clear();
            oldMap.putAll(curMap);
        }
        ServiceDiscover.loop++;

    }

    /**
     * 更新服务注册信息
     */
    private void updateServiceRegistry(){
        //1、首次扫描，注册所有服务
        if (loop==1){
            updateServiceBatch(curMap, ServiceRegister.REGISTRY_TYPE_ADD);

            logger.debug("====================**第【"+ServiceDiscover.loop+"】次扫描结果，新增服务总数【"+curMap.size()+"】**====================");
            return;
        }
        //2、非首次扫描
        //2.1、注册新增服务
        updateServiceBatch(addMap, ServiceRegister.REGISTRY_TYPE_ADD);
        //2.2、下线删除的服务
        updateServiceBatch(delMap, ServiceRegister.REGISTRY_TYPE_DEL);
        //2.3、更新服务
        updateServiceBatch(updMap, ServiceRegister.REGISTRY_TYPE_UPD);

        logger.debug("====================**第【"+ServiceDiscover.loop+"】次扫描结果，服务总数【"+curMap.size()+"】，新增【"
                +addMap.size()+"】，删除【"+delMap.size()+"】，修改【"+updMap.size()+"】**====================");
    }

    /**
     * 清理扫描数据
     */
    private void clearScanResult(){
        curFiles.clear();
        curMap.clear();
        addMap.clear();
        updMap.clear();
        delMap.clear();
    }

    /**
     * 获取目录下所有子目录和文件
     *
     * @param fileList 文件列表
     * @param filePath 目标目录
     * @param fileKeys 文件后缀
     */
    private static void getFiles(ArrayList<String> fileList, String filePath, String[] fileKeys) {
        File root = new File(filePath);
        File[] files = root.listFiles();
        //目录为空，忽略
        if (files == null) return;

        for (File file : files) {
            //为目录
            if (file.isDirectory() && file.canRead())
                getFiles(fileList, file.getAbsolutePath(), fileKeys);
            //为文件
            if (file.isFile()) {
                //获取文件全名
                String strFileName = file.getAbsolutePath();
                //获取文件后缀
                String suffix = strFileName.substring(strFileName.lastIndexOf(".") + 1);
                for (String s : fileKeys) {
                    //匹配文件类型
                    if (!suffix.toLowerCase().equals(s.toLowerCase())) continue;
                    fileList.add(strFileName);
                }
            }
        }

    }

    /**
     * 批量更新服务
     * @param serviceMap 服务集合
     * @param type C：新增，D：删除，U：修改
     */
    private void updateServiceBatch(Map<String, String> serviceMap, char type){

        for (String serviceFile : serviceMap.keySet()) {
            //服务编码
            String serviceCode = serviceFile.substring(watchedDir.length(),serviceFile.lastIndexOf("."));
            //服务版本，更新时间
            String version = serviceMap.get(serviceFile);

            serviceRegister.register(serviceCode,version,type);
        }

    }
}
