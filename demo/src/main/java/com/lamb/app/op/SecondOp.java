package com.lamb.app.op;

import com.lamb.framework.base.Context;
import com.lamb.framework.service.op.BaseOP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>Title : 原子服务</p>
 * <p>Description : demo服务流程中的第二个步骤</p>
 * <p>Date : 2017/4/13 9:42</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Slf4j
@Service
public class SecondOp implements BaseOP {
    @Override
    public void execute(Context context) {
        context.getParams().put("branch" , "SecondOp");
        log.debug("步骤二【SecondOp】，执行结束");
    }
}
