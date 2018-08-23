package com.lamb.framework.service.flow.helper;

import com.lamb.framework.base.Context;
import com.lamb.framework.cache.ConfigCache;
import com.lamb.framework.exception.ServiceRuntimeException;
import com.lamb.framework.service.flow.constant.FlowConfigConstants;
import com.lamb.framework.service.flow.model.Flow;
import com.lamb.framework.service.flow.model.Forward;
import com.lamb.framework.service.flow.model.Step;
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
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title : 流程配置解析器</p>
 * <p>Description : 解析流程配置</p>
 * <p>Date : 2017/3/2 16:58</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Component
public class FlowConfigParser {
    private static Logger logger = LoggerFactory.getLogger(FlowConfigParser.class);
    /**
     * 是否缓存服务流程配置
     */
    @Value("${cache.conf.flow}")
    private boolean isCacheFlowConf;
    /**
     * 流程配置根目录
     */
    @Value("${biz.conf.flow}")
    private String flowConfPath;
    /**
     * 流程配置对象缓存
     */
    @Resource
    private ConfigCache configCache;

    /**
     * 解析流程配置
     *
     * @param context 数据总线
     * @return 流程实体对象
     */
    public Flow parseFlowConfig(Context context) {
        long start = System.currentTimeMillis();
        String serviceId = flowConfPath + context.getServiceId();
        logger.debug("解析流程配置文件【"+serviceId+"】，开始...");
        //1、缓存中查找对象
        if (isCacheFlowConf && configCache.hasConfig(serviceId)){
            logger.debug("解析流程配置文件【"+serviceId+"】，结束【命中缓存】");
            return (Flow) configCache.getConfig(serviceId);
        }
        //2、获取配置文档对象
        Element root = this.getFlowConfDoc(serviceId + ".xml");
        //3、解析配置文档
        Flow config = this.parseNodes(root);
        //4、添加至缓存
        configCache.addConfig(serviceId, config);
        long end = System.currentTimeMillis();
        logger.debug("解析流程配置文件【"+serviceId+"】，结束【"+(end-start)+"毫秒】");
        return config;
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
        } catch (DocumentException e) {//流程文档解析失败
            throw new ServiceRuntimeException("2003" , this.getClass() , e , path);
        }
        return doc.getRootElement();
    }

    /**
     * 解析文档对象为流程对象
     *
     * @param root 流程节点节点
     * @return 流程对象
     */
    private Flow parseNodes(Element root) {
        if (!root.getName().equals(FlowConfigConstants.FLOW_TAG))//流程文档结构不正确
            throw new ServiceRuntimeException("2004" , this.getClass());
        Flow flow = new Flow();
        //1、获取流程属性
        List<Attribute> attributes = root.attributes();
        for (Attribute attribute : attributes) {
            String attrName = attribute.getName();
            String attrValue = attribute.getValue();
            //流程标题
            if (attrName.equals(FlowConfigConstants.FLOW_TITLE))
                flow.setTitle(attrValue);
        }
        //2、解析步骤实例
        Iterator<Element> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            if (!e.getName().equals(FlowConfigConstants.STEP_TAG))//流程文档结构不正确
                throw new ServiceRuntimeException("2004" , this.getClass());
            Step step = new Step();
            List<Attribute> attrs = e.attributes();
            for (Attribute attribute : attrs) {
                String attrName = attribute.getName();
                String attrValue = attribute.getValue();
                //步骤索引
                if (attrName.equals(FlowConfigConstants.STEP_INDEX)){
                    if (attrValue==null||attrValue.equals(""))//流程文档必须包含步骤索引
                        throw new ServiceRuntimeException("2004" , this.getClass());
                    step.setIndex(attrValue);
                }
                //下一步索引
                if (attrName.equals(FlowConfigConstants.STEP_NEXT)){
                    step.setNext(attrValue);
                }
                //步骤映射实体
                if (attrName.equals(FlowConfigConstants.STEP_REF)) {
                    if (attrValue == null || attrValue.equals(""))//流程文档必须包含步骤映射
                        throw new ServiceRuntimeException("2004",this.getClass());
                    step.setRef(attrValue);
                }
                //步骤描述
                if (attrName.equals(FlowConfigConstants.STEP_DESC))
                    step.setDesc(attrValue);
                //左偏移
                if (attrName.equals(FlowConfigConstants.STEP_LEFT))
                    step.setLeft(attrValue);
                //上偏移
                if (attrName.equals(FlowConfigConstants.STEP_TOP))
                    step.setTop(attrValue);
            }
            //3、解析转向实例
            Iterator<Element> iter = e.elementIterator();
            while (iter.hasNext()){
                Element el = iter.next();
                if (!el.getName().equals(FlowConfigConstants.FORWARD_TAG))//流程文档结构不正确
                    throw new ServiceRuntimeException("2004" , this.getClass());
                Forward forward = new Forward();
                List<Attribute> list = el.attributes();
                for (Attribute attribute : list){
                    String attrName = attribute.getName();
                    String attrValue = attribute.getValue();
                    //条件表达式
                    if (attrName.equals(FlowConfigConstants.FORWARD_CONDITION)){
                        if (attrValue == null || attrValue.equals(""))//流程文档必须包含条件表达式
                            throw new ServiceRuntimeException("2004" , this.getClass());
                        forward.setCondition(attrValue);
                    }
                    //转向步骤索引
                    if (attrName.equals(FlowConfigConstants.FORWARD_TO)){
                        if (attrValue == null || attrValue.equals(""))//流程文档必须包含转向索引
                            throw new ServiceRuntimeException("2004" , this.getClass());
                        forward.setTo(attrValue);
                    }
                    //转向描述
                    if (attrName.equals(FlowConfigConstants.FORWARD_DESC))
                        forward.setDesc(attrValue);
                }
                //将转向添加至步骤
                step.getMapping().add(forward);
            }
            //将步骤添加至流程
            flow.getSteps().put(step.getIndex() , step);
        }
        return flow;
    }
}
