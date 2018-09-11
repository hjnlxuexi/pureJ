package com.lamb.app.gen.dao;

import com.lamb.app.gen.domain.UserInfo;
import com.lamb.framework.adapter.database.IBaseMapper;

import java.util.List;

public interface UserInfoMapper extends IBaseMapper<UserInfo> {

    public List getAll();
}