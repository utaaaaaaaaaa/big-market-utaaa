package com.uta.infrastructure.persistent.repository;

import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import com.uta.domain.strategy.model.entity.StrategyEntity;
import com.uta.domain.strategy.model.entity.StrategyRuleEntity;
import com.uta.domain.strategy.model.vo.*;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.uta.infrastructure.persistent.dao.*;
import com.uta.infrastructure.persistent.po.*;
import com.uta.infrastructure.persistent.redis.IRedisService;
import com.uta.types.common.Constants;
import com.uta.types.enums.ResponseCode;
import com.uta.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RMap;
import org.redisson.spring.session.RedissonSessionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description 策略仓储实现
 */
@Repository
@Slf4j
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private StrategyMapper strategyMapper;

    @Resource
    private StrategyAwardMapper strategyAwardMapper;

    @Resource
    private StrategyRuleMapper strategyRuleMapper;

    @Resource
    private RuleTreeMapper ruleTreeMapper;

    @Resource
    private RuleTreeNodeMapper ruleTreeNodeMapper;

    @Resource
    private RuleTreeNodeLineMapper ruleTreeNodeLineMapper;

    @Resource
    private RaffleActivityMapper raffleActivityMapper;

    @Resource
    private RaffleActivityAccountDayMapper raffleActivityAccountDayMapper;

    @Resource
    private RaffleActivityAccountMapper raffleActivityAccountMapper;

    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> getStrategyAwardList(Long strategyId) {
        //redis缓存中查找策略奖品
        String cachedKey = Constants.RedisKey.STRATEGY_AWARD_LIST_KEY + strategyId;
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
    public void storeStrategyAwardSearchRateTable(String key, Integer size, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        // 1.存储策略对应的概率范围
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY+key, size);
        // 2.存储策略对应的概率查找表
        RMap<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return getRateRange(String.valueOf(strategyId));
    }

    @Override
    public int getRateRange(String key) {
        String cacheKey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key;
        if (!redisService.isExists(cacheKey)) {
           throw new AppException(ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY.getCode(), cacheKey + Constants.COLON +ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY.getInfo());
        }
        return redisService.getValue(cacheKey);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, Integer i) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY+strategyId, i);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, Integer i) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY+key, i);
    }

    @Override
    public StrategyEntity getStrategyEntityByStrategyId(Long strategyId) {
        String cachedKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(cachedKey);
        if (strategyEntity != null) {
            return strategyEntity;
        }
        StrategyEntity strategy = strategyMapper.queryStrategyEntityByStrategyId(strategyId);
        redisService.setValue(cachedKey, strategy);
        return strategy;
    }

    @Override
    public StrategyRuleEntity getStrategyRuleEntityByStrategyId(Long strategyId) {
        return strategyRuleMapper.queryStrategyRuleEntityByStrategyId(strategyId);
    }

    @Override
    public String getStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        return strategyRuleMapper.queryStrategyRuleValue(strategyId,awardId,ruleModel);
    }

    @Override
    public String getStrategyRuleValue(Long strategyId, String ruleModel) {
        return this.getStrategyRuleValue(strategyId,null, ruleModel);
    }

    @Override
    public AwardRuleModelVO getAwardRuleModels(Long strategyId, Integer randomAwardId) {
        return strategyAwardMapper.queryAwardRuleModel(strategyId,randomAwardId);
    }

    @Override
    public RuleTreeVO getRuleTreeVOByTreeId(String treeId) {
        //优先从缓存中获取
        String key = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = redisService.getValue(key);
        if (ruleTreeVOCache != null) {
            return ruleTreeVOCache;
        }

        RuleTree ruleTree = ruleTreeMapper.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeMapper.queryRuleTreeNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineMapper.queryRuleTreeNodeLineByTreeId(treeId);

        //treeNodeLine 转换为 map结构
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeNodeLineVOMap = new HashMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(ruleTreeNodeLine.getTreeId())
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                    .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                    .build();


            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOS = ruleTreeNodeLineVOMap.computeIfAbsent(
                    ruleTreeNodeLine.getRuleNodeFrom(),
                    k -> new ArrayList<>());
            ruleTreeNodeLineVOS.add(ruleTreeNodeLineVO);
        }

        //treeNode 转换为 map结构
        Map<String, RuleTreeNodeVO> treeNodeMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(ruleTreeNode.getTreeId())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .treeNodeLineVOList(ruleTreeNodeLineVOMap.get(ruleTreeNode.getRuleKey()))
                    .build();
            treeNodeMap.put(ruleTreeNode.getRuleKey(), ruleTreeNodeVO);
        }

        //构建ruleTree
        RuleTreeVO ruleTreeVODB = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc())
                .treeRootRuleNode(ruleTree.getTreeRootRuleKey())
                .treeNodeMap(treeNodeMap)
                .build();

        redisService.setValue(key, ruleTreeVODB);
        return ruleTreeVODB;
    }

    @Override
    public void cacheStrategyAwardCount(String key, Integer awardCount) {
        Long cacheAwardCount = redisService.getAtomicLong(key);
        if (0L != cacheAwardCount) {
            return;
        }
        redisService.setAtomicLong(key, awardCount);
    }

    @Override
    public Boolean subtractionAwardStock(String key) {
        return this.subtractionAwardStock(key, null);
    }

    @Override
    public Boolean subtractionAwardStock(String key, Date endDateTime) {
        //返回剩余库存值
        long surplus = redisService.decr(key);
        if (surplus < 0) {
            redisService.setValue(key, 0);
            return false;
        }
        //setNx 设置库存剩余数量锁，防止运营意外操作导致超卖
        String lockKey = key + Constants.UNDERLINE + surplus;
        Boolean lock;
        if (endDateTime != null){
            long expireMillis = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
            lock = redisService.setNx(lockKey, expireMillis, TimeUnit.MILLISECONDS);
        }else {
            lock = redisService.setNx(lockKey);
        }
        if (!lock){
            log.info("策略奖品库存加锁失败 lockKey：{}", lockKey);
        }
        return lock;
    }

    @Override
    public void awardStockConsumeSendMsgQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<StrategyAwardStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(strategyAwardStockKeyVO,3, TimeUnit.SECONDS);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        return blockingQueue.poll();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        strategyAwardMapper.updateStrategyAwardStock(strategyId, awardId);
    }

    @Override
    public StrategyAwardEntity getStrategyAwardEntity(Long strategyId, Integer awardId) {

        //redis缓存中查找策略奖品
        String cachedKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        StrategyAwardEntity strategyAwardEntities = redisService.getValue(cachedKey);
        if (strategyAwardEntities != null) {
            return strategyAwardEntities;
        }

        StrategyAwardEntity strategyAwardEntity = strategyAwardMapper.queryStrategyAwardEntity(strategyId, awardId);
        redisService.setValue(cachedKey, strategyAwardEntity);

        return strategyAwardEntity;
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        return raffleActivityMapper.queryStrategyIdByActivityId(activityId);
    }

    @Override
    public Integer queryTodayUserRaffleCount(String userId, Long strategyId) {
        Long activityId = raffleActivityMapper.queryActivityIdByStrategyId(strategyId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayMapper.queryActivityAccountDayByUserId(
                RaffleActivityAccountDay.builder()
                        .userId(userId)
                        .activityId(activityId)
                        .day(today)
                        .build()
        );
        if (raffleActivityAccountDay == null) {return 0;}
        return raffleActivityAccountDay.getDayCount() - raffleActivityAccountDay.getDayCountSurplus();
    }

    @Override
    public Map<String, Integer> queryAwardRuleLockCount(String[] treeIds) {
        if (null == treeIds || treeIds.length == 0) return new HashMap<>();
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeMapper.queryRuleLocks(treeIds);
        Map<String, Integer> resultMap = new HashMap<>();
        for (RuleTreeNode node : ruleTreeNodes) {
            String treeId = node.getTreeId();
            Integer ruleValue = Integer.valueOf(node.getRuleValue());
            resultMap.put(treeId, ruleValue);
        }
        return resultMap;
    }

    @Override
    public Integer queryActivityAccountTotalUseCount(String userId, Long strategyId) {
        Long activityId = raffleActivityMapper.queryActivityIdByStrategyId(strategyId);
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountMapper.queryActivityAccountByUserId(RaffleActivityAccount.builder()
                .activityId(activityId)
                .userId(userId)
                .build());
        if (raffleActivityAccount == null) {return 0;}
        return raffleActivityAccount.getTotalCount() - raffleActivityAccount.getTotalCountSurplus();
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeight(Long strategyId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.STRATEGY_RULE_WEIGHT_KEY + strategyId;
        List<RuleWeightVO> ruleWeightVOS = redisService.getValue(cacheKey);
        if (null != ruleWeightVOS) return ruleWeightVOS;

        ruleWeightVOS = new ArrayList<>();
        // 1. 查询权重规则配置
        StrategyRule strategyRuleReq = new StrategyRule();
        strategyRuleReq.setStrategyId(strategyId.intValue());
        strategyRuleReq.setRuleModel(DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode());
        String ruleValue = strategyRuleMapper.queryStrategyRuleValue(strategyId, null, strategyRuleReq.getRuleModel());
        // 2. 借助实体对象转换规则
        StrategyRuleEntity strategyRuleEntity = new StrategyRuleEntity();
        strategyRuleEntity.setRuleModel(DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode());
        strategyRuleEntity.setRuleValue(ruleValue);
        Map<String, List<Integer>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        // 3. 遍历规则组装奖品配置
        Set<String> ruleWeightKeys = ruleWeightValues.keySet();
        for (String ruleWeightKey : ruleWeightKeys) {
            List<Integer> awardIds = ruleWeightValues.get(ruleWeightKey);
            List<RuleWeightVO.Award> awardList = new ArrayList<>();
            // 也可以修改为一次从数据库查询
            for (Integer awardId : awardIds) {
                StrategyAwardEntity strategyAward = strategyAwardMapper.queryStrategyAwardEntity(strategyId, awardId);
                awardList.add(RuleWeightVO.Award.builder()
                        .awardId(strategyAward.getAwardId())
                        .awardTitle(strategyAward.getAwardTitle())
                        .build());
            }

            ruleWeightVOS.add(RuleWeightVO.builder()
                    .ruleValue(ruleValue)
                    .weight(Integer.valueOf(ruleWeightKey.split(Constants.COLON)[0]))
                    .awardIds(awardIds)
                    .awardList(awardList)
                    .build());
        }

        // 设置缓存 - 实际场景中，这类数据，可以在活动下架的时候统一清空缓存。
        redisService.setValue(cacheKey, ruleWeightVOS);

        return ruleWeightVOS;

    }


}
