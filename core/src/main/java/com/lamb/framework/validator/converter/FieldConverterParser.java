package com.lamb.framework.validator.converter;

import com.lamb.framework.base.Framework;
import com.lamb.framework.cache.ConfigCache;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.util.BizConfigHotLoading;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title : 字段转换器配置解析</p>
 * <p>Description : 解析字段转换配置</p>
 * <p>Date : 2018/9/17 </p>
 *
 * @author : hejie
 */
@Slf4j
@Component
@Data
public class FieldConverterParser {

    private final List<FieldConverter> converters = new ArrayList<>();

    /**
     * 解析字段转换配置
     *
     * @param converterName 数据总线
     * @return 流程实体对象
     */
    public FieldConverter parseConverterConfig(String converterName) {
        String converterPath = Framework.getProperty("biz.conf.converters");

        String converterKey = converterPath+"/"+converterName;
        //1、缓存中查找对象
        if (ConfigCache.hasConfig(converterKey)){
            log.debug("流程配置文件【"+converterKey+"】，【命中缓存】");
            return (FieldConverter) ConfigCache.getConfig(converterKey);
        }
        if (converterPath.startsWith(BizConfigHotLoading.HTTP_PROTOCOL))//配置不存在
            throw new ServiceRuntimeException("1014",this.getClass(),converterKey);

        //2、解析配置文档，并加入缓存
        this.parseNodes(converterPath + BizConfigHotLoading.LOCAL_CONF_POSTFIX);
        //3、再次获取
        if (!ConfigCache.hasConfig(converterKey)){
            throw new ServiceRuntimeException("1014",this.getClass(),converterKey);
        }
        return (FieldConverter) ConfigCache.getConfig(converterKey);
    }

    /**
     * 解析文档对象为流程对象
     *
     * @param path 文档路径
     */
    @SuppressWarnings("unchecked")
    public void parseNodes(String path) {
        List<FieldConverter> _converters = new ArrayList<>();
        //1、获取配置内容
        Element root = getFlowConfDoc(path);

        if (!root.getName().equals(ConverterConstants.CONVERTER_TAG))//流程文档结构不正确
            throw new ServiceRuntimeException("1012" , this.getClass());
        //2、解析转换器列表
        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            //转换器名称
            String converterName = e.getName();
            FieldConverter converter = new FieldConverter();
            converter.setName(converterName);

            //3、解析规则列表
            Iterator<Element> iter = e.elementIterator();
            while (iter.hasNext()){
                Element el = iter.next();
                if (!el.getName().equals(ConverterConstants.RULE_TAG))//流程文档结构不正确
                    throw new ServiceRuntimeException("1012" , this.getClass());
                List<Attribute> list = el.attributes();
                String from = null;
                String to = null;
                for (Attribute attribute : list){
                    String attrName = attribute.getName();
                    String attrValue = attribute.getValue();
                    //from
                    if (attrName.equals(ConverterConstants.FROM_PROP)){
                        from = (attrValue == null || attrValue.equals("")) ? ConverterConstants.NULL_SIGN : attrValue;
                    }
                    //to
                    if (attrName.equals(ConverterConstants.TO_PROP)){
                        to = (attrValue == null || attrValue.equals("")) ? ConverterConstants.NULL_SIGN : attrValue;
                    }
                }
                //转换前后不能为空
                if (from==null || to==null) throw new ServiceRuntimeException("1013" , this.getClass());
                //添加转换规则
                converter.putRule(from , to);
            }
            //4、将每个转换器加入到缓存
            ConfigCache.addConfig(Framework.getProperty("biz.conf.converters")+"/"+converterName, converter);
            _converters.add(converter);
        }
        this.converters.clear();
        this.converters.addAll(_converters);
    }


    /**
     * 获取配置文档对象
     *
     * @param path 文档路径
     * @return 文档对象
     */
    private Element getFlowConfDoc(String path) {
        SAXReader reader = new SAXReader();
        Document doc;
        try {
            doc = reader.read(new File(path));
        } catch (DocumentException e) {//文档解析失败
            throw new ServiceRuntimeException("1015" , this.getClass() , e , path);
        }
        return doc.getRootElement();
    }
}
