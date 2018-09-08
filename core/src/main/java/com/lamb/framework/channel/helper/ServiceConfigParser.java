package com.lamb.framework.channel.helper;

import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.cache.ConfigCache;
import com.lamb.framework.channel.constant.ServiceConfConstants;
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
 * <p>Title : 服务配置解析器</p>
 * <p>Description : 解析服务配置</p>
 * <p>Date : 2017/3/1 11:02</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Component
public class ServiceConfigParser {
    private final static Logger logger = LoggerFactory.getLogger(ServiceConfigParser.class);

    /**
     * 解析服务配置
     *
     * @param context 数据总线
     * @return 服务配置对象
     */
    public Map parseServiceConf(Context context) {
        long start = System.currentTimeMillis();
        String serviceCode = Framework.getProperty("biz.conf.service") + context.getServiceCode();
        //0、缓存中获取配置对象
        if (ConfigCache.hasConfig(serviceCode)){
            logger.debug("服务配置文件【"+serviceCode+"】，【命中缓存】");
            return (Map) ConfigCache.getConfig(serviceCode);
        }
        if (serviceCode.startsWith(BizConfigHotLoading.HTTP_PROTOCOL))//服务配置不存在
            throw new ServiceRuntimeException("1010",this.getClass(),serviceCode);

        logger.debug("解析服务配置文件【"+serviceCode+"】，开始...");
        //1、解析配置文档
        Map config = this.parseNodes(serviceCode + BizConfigHotLoading.LOCAL_CONF_POSTFIX);
        //2、添加配置对象至缓存，并返回
        ConfigCache.addConfig(serviceCode, config);
        long end = System.currentTimeMillis();
        logger.debug("解析服务配置文件【"+serviceCode+"】，结束【"+(end-start)+"毫秒】");
        return config;
    }

    /**
     * 遍历解析节点树
     *
     * @param path 服务配置路径
     */
    @SuppressWarnings("unchecked")
    public Map parseNodes(String path) {
        //0、读取文档
        Element node = getServiceConfDoc(path);

        if (!node.getName().equals(ServiceConfConstants.ROOT_TAG))//服务配置结构不正确
            throw new ServiceRuntimeException("1005" , this.getClass());
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
            if (key.equals(ServiceConfConstants.INPUT_TAG)
                    || key.equals(ServiceConfConstants.OUTPUT_TAG)) {
                List fields = this.parseFields(e);
                map.put(key, fields);
            }
        }
        return map;
    }

    /**
     * 读取文档，并返回根节点
     *
     * @param path 服务配置路径
     * @return 文档根节点
     */
    private Element getServiceConfDoc(String path) {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new File(path));
            return doc.getRootElement();
        } catch (DocumentException e) {//服务配置解析失败
            throw new ServiceRuntimeException("1004" , this.getClass() , e , path);
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
            if (!e.getName().equals(ServiceConfConstants.FIELD_TAG))//服务配置结构不正确
                throw new ServiceRuntimeException("1005" , this.getClass());
            Map field = new HashMap();
            //1、获取当前节点的所有属性
            List<Attribute> attributes = e.attributes();
            for (Attribute attribute : attributes) {
                String attrName = attribute.getName();
                String attrValue = attribute.getValue();
                field.put(attrName, attrValue);
                //2、如为列表域，则递归加载
                if (attrName.equals(ServiceConfConstants.TYPE_PROP)
                        && attrValue.equals(ServiceConfConstants.FIELD_TYPE_E)) {
                    field.put(ServiceConfConstants.FIELD_KEY_LIST, parseFields(e));
                }
            }
            list.add(field);
        }
        return list;
    }

}
