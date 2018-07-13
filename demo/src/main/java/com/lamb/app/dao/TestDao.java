package com.lamb.app.dao;

import com.lamb.framework.adapter.database.IBaseDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>Title : </p>
 * <p>Description : </p>
 * <p>Date : 2017/3/9 10:39</p>
 *
 * @author : hejie (hjnlxuexi@126.com)
 * @version : 1.0
 */
@Mapper
public interface TestDao extends IBaseDao {

    public Map findAllDeptCnt(Map prams);
    /**
     * 查询所有部门
     * @param param 参数
     * @return 部门列表
     */
    public List<Map> findAllDept(Map param);

    /**
     * 修改部门地区
     */
    public void updateDeptLocById();

    public void addBonus(Map param);
}
