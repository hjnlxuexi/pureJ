package com.lamb.gen.dao;

import com.lamb.gen.domain.UserInfo;
import com.lamb.framework.adapter.database.IBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper extends IBaseMapper<UserInfo> {
    public List getAll();
}