package com.lamb.app.op;

import com.lamb.framework.base.Context;
import com.lamb.framework.service.op.BaseOP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>Title : 简单原子服务</p>
 * <p>Description : 一个简单的原子服务</p>
 * <p>Date : 2017/4/13 9:42</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Slf4j
@Service
public class SampleOp implements BaseOP {
    @Override
    @SuppressWarnings("unchecked")
    public void execute(Context context) {
        context.getParams().put("opName" , "一个简单的原子服务");
        log.debug("一个简单的原子服务");
    }
}
