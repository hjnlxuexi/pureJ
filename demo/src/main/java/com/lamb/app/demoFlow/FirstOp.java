package com.lamb.app.demoFlow;

import com.lamb.gen.dao.UserInfoMapper;
import com.lamb.framework.base.Context;
import com.lamb.framework.service.OP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
public class FirstOp implements OP{
    @Resource
    private UserInfoMapper userInfoMapper;
    @Override
    @SuppressWarnings("unchecked")
    public void execute(Context context) {
        List list = userInfoMapper.selectAll();
        context.getParams().put("param1" , "1234");
        context.getParams().put("param2" , "23");
        context.getParams().put("list" , list);
        log.debug("步骤一【FirstOp】，执行结束");
    }
}
