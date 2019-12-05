package com.azureip.ipspider.mapper;

import com.azureip.ipspider.model.ProxyIPModel;
import com.azureip.ipspider.model.ProxyIPModelKey;

public interface ProxyIPModelMapper {
    int deleteByPrimaryKey(ProxyIPModelKey key);

    int insert(ProxyIPModel record);

    int insertSelective(ProxyIPModel record);

    ProxyIPModel selectByPrimaryKey(ProxyIPModelKey key);

    int updateByPrimaryKeySelective(ProxyIPModel record);

    int updateByPrimaryKey(ProxyIPModel record);
}