package com.uta.infrastructure.persistent.repository;

import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.infrastructure.persistent.dao.StrategyAwardMapper;
import com.uta.infrastructure.persistent.redis.IRedisService;
import com.uta.types.common.Constants;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description 策略仓储实现
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private StrategyAwardMapper strategyAwardMapper;

    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> getStrategyAwardList(Long strategyId) {
        //redis缓存中查找策略奖品
        String cachedKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cachedKey);
        if (strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) {
            return strategyAwardEntities;
        }
        //没找到则直接查询数据库
        List<StrategyAwardEntity> strategyAwards = strategyAwardMapper.queryStrategyAwardListByStrategyId(strategyId);
        //写入redis
        redisService.setValue(cachedKey, strategyAwards);
        return strategyAwards;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(Long strategyId, Integer size, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        // 1.存储策略对应的概率范围
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+strategyId, size);
        // 2.存储策略对应的概率查找表
        RMap<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, Integer i) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY+strategyId, i);
    }
}
