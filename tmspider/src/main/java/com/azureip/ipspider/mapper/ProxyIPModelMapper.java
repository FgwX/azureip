package com.azureip.ipspider.mapper;

import com.azureip.ipspider.model.ProxyIPModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProxyIPModelMapper {
    int deleteByPrimaryKey(@Param("ip") String ip, @Param("port") Integer port);

    int insert(ProxyIPModel record);

    ProxyIPModel selectByPrimaryKey(@Param("ip") String ip, @Param("port") Integer port);

    List<ProxyIPModel> selectAll();

    int updateByPrimaryKey(ProxyIPModel record);
}