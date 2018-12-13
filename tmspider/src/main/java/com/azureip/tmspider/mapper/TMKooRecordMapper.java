package com.azureip.tmspider.mapper;

import com.azureip.tmspider.model.TMKooRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TMKooRecordMapper {
    int deleteByPrimaryKey(String regNum);

    int insert(TMKooRecord record);

    int insertSelective(TMKooRecord record);

    TMKooRecord selectByPrimaryKey(String regNum);

    int updateByPrimaryKeySelective(TMKooRecord record);

    int updateByPrimaryKey(TMKooRecord record);

    void insertAll(List<TMKooRecord> list);
}