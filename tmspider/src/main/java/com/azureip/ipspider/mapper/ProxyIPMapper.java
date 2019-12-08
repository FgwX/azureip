package com.azureip.ipspider.mapper;

import com.azureip.ipspider.model.ProxyIP;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProxyIPMapper {
    int deleteByPrimaryKey(@Param("ip") String ip, @Param("port") Integer port);

    int insert(ProxyIP record);

    ProxyIP selectByPrimaryKey(@Param("ip") String ip, @Param("port") Integer port);

    List<ProxyIP> selectAll();

    int updateByPrimaryKey(ProxyIP record);
}