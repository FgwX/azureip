package com.azureip.ipspider.mapper;

import com.azureip.ipspider.model.ProxyIP;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ProxyIPMapper {
    int deleteByPrimaryKey(@Param("ip") String ip, @Param("port") Integer port);

    int insert(ProxyIP record);

    ProxyIP selectByPrimaryKey(@Param("ip") String ip, @Param("port") Integer port);

    List<ProxyIP> selectAll();

    int updateByPrimaryKey(ProxyIP record);

    void saveAll(List<ProxyIP> proxyList);

    /**
     * 提取待验证的代理IP
     * @param deadline 时限
     * @param limit    数量
     * @return 列表
     */
    List<ProxyIP> selectUnverifiedProxies(Date deadline, int limit);

    /**
     * 更新代理IP信息
     * @param proxy 代理
     */
    void updateProxyIP(ProxyIP proxy);
}