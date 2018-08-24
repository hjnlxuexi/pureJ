package com.lamb.configuration;

import com.lamb.framework.channel.convert.JsonConverter;
import com.lamb.framework.listener.IListener;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title : 上下文配置类</p>
 * <p>Description : 用于替换Spring的xml配置</p>
 * <p>Date : 2017/2/28 23:08</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Configuration
public class ChannelConfig {
    /**
     * 注册消息转换器
     * @return HttpMessageConverters实例
     */
    @Bean
    public HttpMessageConverters jsonConverter() {
        HttpMessageConverter<Object> jsonHttpMsgConvert = new JsonConverter();
        return new HttpMessageConverters(jsonHttpMsgConvert);
    }

    /**
     * 注册监听器列表
     * @return 监听器列表
     */
    @Bean
    public List<IListener> listeners(){
        //TODO  监听器待维护
        return new ArrayList<>();
    }
}
