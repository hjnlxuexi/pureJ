package com.lamb.framework.adapter.database;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * <p>Title : 通用Mapper</p>
 * <p>Description : 自动生成的mapper父接口</p>
 * <p>Date : 2018/9/8 </p>
 *
 * @author : hejie
 */
@tk.mybatis.mapper.annotation.RegisterMapper
public interface IBaseMapper<T> extends Mapper<T>, MySqlMapper<T> {

}
