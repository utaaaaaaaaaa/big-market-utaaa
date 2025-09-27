package com.uta.infrastructure.persistent.dao;

import com.uta.infrastructure.persistent.po.RaffleActivitySku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【raffle_activity_sku】的数据库操作Mapper
* @createDate 2025-09-27 10:05:24
* @Entity persistent.pojo.RaffleActivitySku
*/
@Mapper
public interface RaffleActivitySkuMapper extends BaseMapper<RaffleActivitySku> {

    RaffleActivitySku queryActivitySku(Long sku);

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);
}




