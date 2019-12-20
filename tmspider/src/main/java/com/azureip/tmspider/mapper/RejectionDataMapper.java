package com.azureip.tmspider.mapper;

import com.azureip.tmspider.model.RejectionData;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 驳回信息Mapper
 */
public interface RejectionDataMapper {

    int deleteByPrimaryKey(@Param("regNum") String regNum, @Param("type") Byte type);

    int insert(RejectionData record);

    RejectionData selectByPrimaryKey(@Param("regNum") String regNum, @Param("type") Byte type);

    List<RejectionData> selectAll();

    int updateByPrimaryKey(RejectionData record);

    /**
     * 获取待查询驳回状态的数据
     * @param deadline 期限
     * @return 数据列表
     */
    List<RejectionData> getPendingRejectionDatas(Date deadline, int limit);
}
