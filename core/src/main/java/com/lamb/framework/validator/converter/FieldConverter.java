package com.lamb.framework.validator.converter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Title : 字段转换器</p>
 * <p>Description : 对于业务服务中常见的数据字典转换</p>
 * <p>Date : 2018/9/17 </p>
 *
 * @author : hejie
 */
@Data
@Slf4j
public class FieldConverter implements Serializable{

    private String name;

    private Map<String , String> rules = new ConcurrentHashMap<>();

    /**
     * 设置转换规则
     * @param from 原值
     * @param to 转换值
     */
    public void putRule(String from , String to){
        if (rules.containsKey(from)){
            log.warn("字段转换器【"+name+"】,存在重复的值【"+from+"】");
            return;
        }
        rules.put(from , to);
    }

    /**
     * 获取
     * @param from 原值
     * @return 转换值
     */
    public String getRule(String from){
        String to;
        if (from == null || String.valueOf(from).equals("")) {
            to = rules.get(ConverterConstants.NULL_SIGN);
        }else {
            to = rules.get(from);
        }
        if (to != null && to.equals(ConverterConstants.NULL_SIGN)){
            to = null;
        }
        return to;
    }
}
