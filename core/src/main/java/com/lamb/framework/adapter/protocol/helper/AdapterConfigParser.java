package com.lamb.framework.adapter.protocol.helper;

import com.lamb.framework.adapter.protocol.constant.AdapterConfConstants;
import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.cache.ConfigCache;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.util.BizConfigHotLoading;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
    private final static Logger logger = LoggerFactory.getLogger(AdapterConfigParser.class);

    /**
     * 解析服务配置
     *
     * @param context 数据总线
     * @return 服务配置对象
     */
    public Map parseAdapterConf(Context context) {
        long start = System.currentTimeMillis();
        String serviceId = Framework.getProperty("biz.conf.adapter") + context.getServiceId();
        //0、缓存中获取配置对象
        if (ConfigCache.hasConfig(serviceId)){
            logger.debug("外部服务配置文件【"+serviceId+"】，【命中缓存】");
            return (Map) ConfigCache.getConfig(serviceId);
        }
        if (serviceId.startsWith(BizConfigHotLoading.HTTP_PROTOCOL))//适配器配置不存在
            throw new ServiceRuntimeException("5004",this.getClass(),serviceId);

        logger.debug("解析外部服务配置文件【"+serviceId+"】，开始...");
        //1、解析配置文档
        Map config = this.parseNodes(serviceId + BizConfigHotLoading.LOCAL_CONF_POSTFIX);
        //2、添加配置对象至缓存，并返回
        ConfigCache.addConfig(serviceId, config);
        long end = System.currentTimeMillis();
        logger.debug("解析外部服务配置文件【"+serviceId+"】，结束【"+(end-start)+"毫秒】");
        return config;
    }

    /**
     * 遍历解析节点树
     *
     * @param path 外部服务配置路径
     */
    @SuppressWarnings("unchecked")
    public Map parseNodes(String path) {
        //0、读取配置文件
        Element node = getAdapterConfDoc(path);

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
     * 读取文档，并返回根节点
     *
     * @param path 外部服务配置路径
     * @return 文档根节点
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
     * 解析字段列表
     *
     * @param node 节点元素
     * @return 字段列表
     */
    @SuppressWarnings("unchecked")
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
