package com.lamb.app.demoFlow;

import com.lamb.framework.base.Context;
import com.lamb.framework.service.op.BaseOP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>Title : 原子服务</p>
 * <p>Description : demo服务流程中的第三个步骤</p>
 * <p>Date : 2017/4/13 9:42</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Slf4j
@Service
public class ThirdOp implements BaseOP {
    @Override
    public void execute(Context context) {
        log.debug("步骤三【ThirdOp】，执行结束");
    }
}
