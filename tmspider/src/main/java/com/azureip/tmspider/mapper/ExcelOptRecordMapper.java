package com.azureip.tmspider.mapper;

import com.azureip.tmspider.model.ExcelOptRecord;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelOptRecordMapper {
    int insert(ExcelOptRecord record);

    int insertSelective(ExcelOptRecord record);
}