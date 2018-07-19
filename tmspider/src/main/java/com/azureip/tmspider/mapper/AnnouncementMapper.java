package com.azureip.tmspider.mapper;

import com.azureip.tmspider.model.Announcement;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementMapper {

    int deleteByPrimaryKey(String id);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);

    /**
     * 插入公告集合
     */
    int insertList(List<Announcement> list);

    /**
     * 根据注册号查询公告
     */
    List<Announcement> getByRegNum(String reg_num);

    /**
     * 根据注册号查询公告数量
     */
    Integer getCountByRegNum(String reg_num);
}