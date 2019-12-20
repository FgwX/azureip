package com.azureip.tmspider.mapper;

import com.azureip.tmspider.model.RejectionData;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RejectionDataMapper {
    int deleteByPrimaryKey(@Param("regNum") String regNum, @Param("type") Byte type);

    int insert(RejectionData record);

    RejectionData selectByPrimaryKey(@Param("regNum") String regNum, @Param("type") Byte type);

    List<RejectionData> selectAll();

    int updateByPrimaryKey(RejectionData record);
}