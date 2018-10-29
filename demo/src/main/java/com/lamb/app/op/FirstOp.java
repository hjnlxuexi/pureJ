package com.lamb.app.op;

import com.lamb.framework.base.Context;
import com.lamb.framework.service.op.BaseOP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>Title : 原子服务</p>
 * <p>Description : demo服务流程中的第一个步骤</p>
 * <p>Date : 2017/4/13 9:42</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Slf4j
@Service
public class FirstOp implements BaseOP {
    @Override
    @SuppressWarnings("unchecked")
    public void execute(Context context) {
        context.getParams().put("param" , "1234");
        context.getParams().put("branch" , "FirstOp");
        log.debug("步骤一【FirstOp】，执行结束");
    }
}
