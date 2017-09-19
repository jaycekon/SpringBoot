package com.jaycekon.dubbo.service;


import com.jaycekon.dubbo.domain.City;

/**
 * 城市业务 Dubbo 服务层
 *
 * Created by Jaycekon on 20/09/2017.
 */
public interface CityDubboService {

    /**
     * 根据城市名称，查询城市信息
     * @param cityName
     */
    City findCityByName(String cityName);
}
