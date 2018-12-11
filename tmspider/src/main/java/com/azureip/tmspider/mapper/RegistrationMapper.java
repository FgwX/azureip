package com.azureip.tmspider.mapper;

import com.azureip.tmspider.model.Registration;

public interface RegistrationMapper {
    int deleteByPrimaryKey(String regNum);

    int insert(Registration record);

    int insertSelective(Registration record);

    Registration selectByPrimaryKey(String regNum);

    int updateByPrimaryKeySelective(Registration record);

    int updateByPrimaryKey(Registration record);
}