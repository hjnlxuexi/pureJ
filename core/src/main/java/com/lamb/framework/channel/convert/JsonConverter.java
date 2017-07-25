package com.lamb.framework.channel.convert;

import com.alibaba.fastjson.JSON;
import com.lamb.framework.base.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * <p>Title : 数据转换器</p>
 * <p>Description : </p>
 * <p>Date : 2017/2/28 22:55</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Component
public class JsonConverter  extends AbstractHttpMessageConverter<Object> {
    private static Logger logger = LoggerFactory.getLogger(JsonConverter.class);
    //字符集
    private final static Charset UTF8 = Charset.forName("UTF-8");

    public JsonConverter(){
        super(UTF8,MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    /**
     * 数据类型判断，是否支持
     * @param clazz 类型
     * @return 支持
     */
    @Override
    protected boolean supports(Class clazz) {
        return true;
    }

    /**
     * 转换输入参数
     * @param clazz  类类型
     * @param inputMessage 输入消息
     * @return 总线
     * @throws IOException IO异常
     * @throws HttpMessageNotReadableException 信息读取异常
     */
    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        //1、获取请求报文
        long contentLength = inputMessage.getHeaders().getContentLength();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(contentLength >= 0L?(int)contentLength:4096);
        StreamUtils.copy(inputMessage.getBody(), bos);
        byte[] bytes = bos.toByteArray();
        String reqStr = new String(bytes, UTF8);
        logger.debug("服务请求报文："+reqStr);

        Map packet =JSON.parseObject(reqStr, Map.class);

        //2、将请求数据存入数据总线
        Context context = new Context();
        //3、报文数据存入输入区
        context.setServiceInput(packet);

        //4、返回数据总线，以传递到Controller中
        return context;
    }

    /**
     * 转换输出数据
     * @param obj 服务处理之后的数据
     * @param httpOutputMessage http输出对象
     * @throws IOException IO异常
     * @throws HttpMessageNotWritableException 信息输出异常
     */
    @Override
    protected void writeInternal(Object obj, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        String text = JSON.toJSONString(obj);
        logger.debug("服务响应报文："+text);

        byte[] bytes = text.getBytes(UTF8);
        StreamUtils.copy(bytes, httpOutputMessage.getBody());
    }
}
