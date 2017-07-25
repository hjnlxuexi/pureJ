package com.lamb.app.demoFlow;

import com.lamb.app.dao.TestDao;
import com.lamb.framework.base.Context;
import com.lamb.framework.service.OP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>Title : 原子服务</p>
 * <p>Description : demo服务流程中的第二个步骤</p>
 * <p>Date : 2017/4/13 9:42</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Service
public class SecondOp implements OP {
    private static Logger logger = LoggerFactory.getLogger(SecondOp.class);
    @Resource
    private TestDao testDao;
    @Override
    public void execute(Context context) {
        testDao.updateDeptLocById();
//        double d = 1/0;//测试事务是否回滚
        logger.debug("步骤二【SecondOp】，执行结束");
    }
}
