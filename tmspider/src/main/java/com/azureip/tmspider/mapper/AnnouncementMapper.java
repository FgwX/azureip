package com.azureip.tmspider.mapper;

import com.azureip.tmspider.model.Announcement;

import java.util.List;

public interface AnnouncementMapper {
    int deleteByPrimaryKey(String id);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);

    /**
     * 根据注册号查询公告
     * @param regNum
     * @return
     */
    List<Announcement> getByRegNum (String regNum);
}