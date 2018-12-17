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
     * 查询本地公告中最新一期的期号
     */
    String queryLocalLatestAnnNum();

    /**
     * 根据期号查询公告数量
     */
    int queryAnnCountByAnnNum(int annNum);

    /**
     * 插入公告集合
     */
    int insertList(List<Announcement> list);

    /**
     * 根据注册号查询公告
     */
    List<Announcement> getByRegNum(String regNum);

    /**
     * 根据注册号查询公告数量
     */
    Integer getCountByRegNum(String regNum);

    /**
     * 根据期号删除公告
     */
    void deleteAnnByAnnNum(int annNum);
}