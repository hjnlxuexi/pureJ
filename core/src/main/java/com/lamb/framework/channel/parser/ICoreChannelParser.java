package com.lamb.framework.channel.parser;

import com.lamb.framework.base.Context;

/**
 * <p>Title : 服务请求报文解析器</p>
 * <p>Description : 解析请求报文</p>
 * <p>Date : 2017/3/3 16:56</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public interface ICoreChannelParser {
    /**
     * 解析请求报文
     * @param context 数据总线
     */
    public void parse(Context context);
}
