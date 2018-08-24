package com.lamb.framework.exception;

import com.lamb.framework.base.Framework;
import org.slf4j.LoggerFactory;

/**
 * <p>Title : 服务运行时异常</p>
 * <p>Description : 定义服务运行时内部异常</p>
 * <p>Date : 2017/3/1 11:24</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
public class ServiceRuntimeException extends RuntimeException {
    /**
     * 消息码
     */
    private String messageKey;

    /**
     * 消息参数
     */
    private Object args[];

    /**
     * 默认构造函数
     */
    public ServiceRuntimeException(){
        super();
    }

    /**
     * 无参消息处理，打印消息码
     * @param messageKey 消息码
     * @param clazz 异常目标类
     */
    public ServiceRuntimeException(String messageKey , Class clazz){
        super(messageKey);
        this.messageKey = messageKey;
        this.args = new Object[]{};
        LoggerFactory.getLogger(clazz).error(this.resolveMsg());
    }

    /**
     * 无参消息处理，打印原始异常
     * @param messageKey 消息码
     * @param clazz 异常目标类
     * @param cause 原始异常
     */
    public ServiceRuntimeException(String messageKey , Class clazz , Throwable cause){
        super(cause);
        this.messageKey = messageKey;
        this.args = new Object[]{};
        LoggerFactory.getLogger(clazz).error(this.resolveMsg());
        cause.printStackTrace();
    }

    /**
     * 有参消息处理，打印消息码
     * @param messageKey 消息码
     * @param clazz 异常目标类
     * @param args 参数组
     */
    public ServiceRuntimeException(String messageKey , Class clazz , Object... args){
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
        LoggerFactory.getLogger(clazz).error(this.resolveMsg());
    }

    /**
     * 有参消息处理，打印原始异常
     * @param messageKey 消息码
     * @param clazz 异常目标类
     * @param cause 原始异常
     * @param args 参数组
     */
    public ServiceRuntimeException(String messageKey , Class clazz , Throwable cause , Object... args){
        super(cause);
        this.messageKey = messageKey;
        this.args = args;
        LoggerFactory.getLogger(clazz).error(this.resolveMsg());
        cause.printStackTrace();
    }

    /**
     * 转换消息内容
     * @return 消息内容
     */
    public String resolveMsg(){
        //原始定义的消息内容
        String msg = Framework.getProperty(messageKey);
        //解析后的消息内容
        String resolveMsg = msg;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            String patten = "{"+i+"}";
            String dest = arg.toString();
            resolveMsg = msg.replace(patten , dest);
        }
        return resolveMsg;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
