package com.lamb.app.demoFlow;

import com.lamb.framework.base.Context;
import com.lamb.framework.service.OP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>Title : 原子服务</p>
 * <p>Description : demo服务流程中的第三个步骤</p>
 * <p>Date : 2017/4/13 9:42</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Service
public class ThirdOp implements OP{
    private static Logger logger = LoggerFactory.getLogger(ThirdOp.class);
    @Override
    public void execute(Context context) {
        logger.debug("步骤三【ThirdOp】，执行结束");
    }
}
