package com.lamb.framework.adapter.protocol.helper;

import com.lamb.framework.adapter.protocol.constant.AdapterConfConstants;
import com.lamb.framework.base.Context;
import com.lamb.framework.cache.ConfigCache;
import com.lamb.framework.exception.ServiceRuntimeException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * <p>Title : 外部服务配置解析器</p>
 * <p>Description : 解析外部服务配置</p>
 * <p>Date : 2017/3/1 11:02</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Component
public class AdapterConfigParser {
    private static Logger logger = LoggerFactory.getLogger(AdapterConfigParser.class);
    /**
     * 服务配置根目录
     */
    @Value("${adapter.conf.dir}")
    private String serviceConfPath;
    /**
     * 服务配置对象缓存
     */
    @Resource
    private ConfigCache configCache;

    /**
     * 解析服务配置
     *
     * @param context 数据总线
     * @return 服务配置对象
     */
    public Map parseAdapterConf(Context context) {
        long start = System.currentTimeMillis();
        String serviceId = serviceConfPath + context.getServiceId();
        logger.debug("解析外部服务配置文件【"+serviceId+"】，开始...");
        //0、缓存中获取配置对象
        if (configCache.hasConfig(serviceId)){
            logger.debug("解析外部服务配置文件【"+serviceId+"】，结束【命中缓存】");
            return (Map) configCache.getConfig(serviceId);
        }
        //1、获得服务配置文档对象
        Element root = this.getAdapterConfDoc(serviceId + ".xml");
        //2、解析配置文档
        Map config = this.parseNodes(root);
        //3、添加配置对象至缓存，并返回
        configCache.addConfig(serviceId, config);
        long end = System.currentTimeMillis();
        logger.debug("解析外部服务配置文件【"+serviceId+"】，结束【"+(end-start)+"毫秒】");
        return config;
    }

    /**
     * 读取文档，并返回根节点
     *
     * @param path 外部服务配置路径
     * @return 文档根节点
     * @throws DocumentException 文档读取异常
     */
    private Element getAdapterConfDoc(String path) {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new File(path));
            return doc.getRootElement();
        } catch (DocumentException e) {//服务配置解析失败
            throw new ServiceRuntimeException("5000" , this.getClass() , e , path);
        }
    }

    /**
     * 遍历解析节点树
     *
     * @param node 根节点
     */
    private Map parseNodes(Element node) {
        if (!node.getName().equals(AdapterConfConstants.ROOT_TAG))//服务配置结构不正确
            throw new ServiceRuntimeException("5001" , this.getClass());
        Map map = new HashMap();
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            //1、获取当前节点值
            String key = e.getName();//节点的名称
            String value = e.getText();//节点值
            if (!(e.getTextTrim().equals(""))) {//存放当前节点键值对
                map.put(key, value);
                continue;
            }
            //2、解析输入/输出字段列表
            if (key.equals(AdapterConfConstants.INPUT_TAG)
                    || key.equals(AdapterConfConstants.OUTPUT_TAG)) {
                List fields = this.parseFields(e);
                map.put(key, fields);
            }
        }
        return map;
    }

    /**
     * 解析字段列表
     *
     * @param node 节点元素
     * @return 字段列表
     */
    private List parseFields(Element node) {
        List list = new ArrayList();
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            if (!e.getName().equals(AdapterConfConstants.FIELD_TAG))//服务配置结构不正确
                throw new ServiceRuntimeException("5001" , this.getClass());
            Map field = new HashMap();
            //1、获取当前节点的所有属性
            List<Attribute> attributes = e.attributes();
            for (Attribute attribute : attributes) {
                String attrName = attribute.getName();
                String attrValue = attribute.getValue();
                field.put(attrName, attrValue);
                //2、如为列表域，则递归加载
                if (attrName.equals(AdapterConfConstants.TYPE_PROP)
                        && attrValue.equals(AdapterConfConstants.FIELD_TYPE_E)) {
                    field.put(AdapterConfConstants.FIELD_KEY_LIST, parseFields(e));
                }
            }
            list.add(field);
        }
        return list;
    }

}
