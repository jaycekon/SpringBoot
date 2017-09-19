package com.jaycekon.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.jaycekon.dubbo.domain.City;
import com.jaycekon.dubbo.service.CityDubboService;

/**
 * 城市业务 Dubbo 服务层实现层
 *
 * Created by Jaycekon on 20/09/2017.
 */
// 注册为 Dubbo 服务
@Service
public class CityDubboServiceImpl implements CityDubboService {

    public City findCityByName(String cityName) {
        return new City(1L,2L,"广州","是我的故乡");
    }
}
