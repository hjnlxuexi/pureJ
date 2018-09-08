package com.lamb.framework.adapter.protocol.connector;

import com.alibaba.fastjson.JSON;
import com.lamb.framework.adapter.protocol.constant.AdapterConfConstants;
import com.lamb.framework.base.Context;
import com.lamb.framework.base.Framework;
import com.lamb.framework.exception.ServiceRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

/**
 * <p>Title : http连接器</p>
 * <p>Description : 连接http服务</p>
 * <p>Date : 2017/4/16 11:15</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Component
public class Connector4Http implements IConnector {
    private final static Logger logger = LoggerFactory.getLogger(Connector4Http.class);

    /**
     * 连接外部服务
     * @param context 数据总线
     * @param adapterConfig 外部服务配置对象
     */
    @Override
    public void connect(Context context, Map adapterConfig) {
        logger.debug("连接外部服务【" + adapterConfig.get(AdapterConfConstants.NAME_TAG) + "】，开始...");
        long start = System.currentTimeMillis();
        //连接url
        Object _url = adapterConfig.get(AdapterConfConstants.HOST_TAG);
        String url = _url == null ? Framework.getProperty("adapter.host") : _url.toString();

        //连接时间
        Object _connTimeout = adapterConfig.get(AdapterConfConstants.CONNECT_TIMEOUT_TAG);
        int connTimeout = _connTimeout == null ? Integer.parseInt(Framework.getProperty("adapter.connectTimeout")) : Integer.parseInt(_connTimeout.toString());
        //响应时间
        Object _respTimeout = adapterConfig.get(AdapterConfConstants.RESPONSE_TIMEOUT_TAG);
        int respTimeout = _respTimeout == null ? Integer.parseInt(Framework.getProperty("adapter.responseTimeout")) : Integer.parseInt(_respTimeout.toString());
        //字符集
        Object _charset = adapterConfig.get(AdapterConfConstants.CHARSET_TAG);
        String charset = _charset == null ? Framework.getProperty("adapter.charset") : _charset.toString();
        //数据报文格式
        String contentType = adapterConfig.get(AdapterConfConstants.CONTENT_TYPE_TAG).toString();
        //请求数据
        String reqStr = JSON.toJSONString(context.getRequestData());
        logger.debug("外部服务【" + adapterConfig.get(AdapterConfConstants.NAME_TAG) + "】，请求报文：" + reqStr);

        HttpURLConnection httpConn;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            //1、创建连接
            URL urlClient = new URL(url);
            httpConn = (HttpURLConnection) urlClient.openConnection();
            this.setHttpConnection(httpConn, connTimeout, respTimeout, contentType);
            //2、发送请求数据
            out = new PrintWriter(new OutputStreamWriter(httpConn.getOutputStream(), charset));
            out.print(reqStr);
            out.flush();

            //3、读取响应数据
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), charset));
            StringBuilder respSb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                respSb.append(line);
            }
            String respStr = respSb.toString();
            logger.debug("外部服务【" + adapterConfig.get(AdapterConfConstants.NAME_TAG) + "】，响应报文：" + respStr);
            Map respData = JSON.parseObject(respStr, Map.class);
            //4、将响应数据放入总线
            context.setResponseData(respData);
        } catch (IOException e) {
            throw new ServiceRuntimeException("5002", this.getClass(), e, adapterConfig.get(AdapterConfConstants.NAME_TAG));
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    logger.error("流关闭异常", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    logger.error("流关闭异常", e);
                }
            }
        }

        long end = System.currentTimeMillis();
        logger.debug("连接外部服务【" + adapterConfig.get(AdapterConfConstants.NAME_TAG) + "】，结束【" + (end - start) + "毫秒】");
    }

    /**
     * 设置http请求连接属性
     *
     * @param httpConn    连接对象
     * @param connTimeout 连接超时时间
     * @param respTimeout 响应超时时间
     * @throws ProtocolException 异常
     */
    private void setHttpConnection(HttpURLConnection httpConn, int connTimeout, int respTimeout, String contentType) throws ProtocolException {
        httpConn.setRequestMethod("POST");
        httpConn.setConnectTimeout(connTimeout);
        httpConn.setReadTimeout(respTimeout);
        httpConn.setRequestProperty("Connection", "keep-alive");
        httpConn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        httpConn.setRequestProperty("Content-Type", contentType);
        httpConn.setRequestProperty("Accept", contentType);
        httpConn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
    }
}
