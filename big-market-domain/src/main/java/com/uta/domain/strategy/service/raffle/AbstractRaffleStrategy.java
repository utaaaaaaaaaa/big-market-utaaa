package com.uta.domain.strategy.service.raffle;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uta.domain.strategy.model.entity.RaffleAwardEntity;
import com.uta.domain.strategy.model.entity.RaffleFactorEntity;
import com.uta.domain.strategy.model.entity.RuleActionEntity;
import com.uta.domain.strategy.model.vo.AwardRuleModelVO;
import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.IRaffleStrategy;
import com.uta.domain.strategy.service.armory.IStrategyDispatch;
import com.uta.domain.strategy.service.rule.chain.ILogicChain;
import com.uta.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.uta.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.uta.types.enums.ResponseCode;
import com.uta.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽奖策略抽象类
 * 抽奖类实现接口，从而实现对接口方法的流程规范定义（也就是对接口方法定义一个通用模板）
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    protected IStrategyRepository repository;

    protected IStrategyDispatch strategyDispatch;

    protected final DefaultChainFactory chainFactory;

    protected final DefaultTreeFactory treeFactory;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory factory, DefaultChainFactory chainFactory, DefaultTreeFactory treeFactory) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
        this.chainFactory = chainFactory;
        this.treeFactory = treeFactory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactor) {
        // 1、参数校验
        Long strategyId = raffleFactor.getStrategyId();
        String userId = raffleFactor.getUserId();
        if (strategyId == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2、责任链抽奖计算【这步拿到的是初步的抽奖ID，之后需要根据ID处理抽奖】注意；黑名单、权重等非默认抽奖的直接返回抽奖结果
        DefaultChainFactory.StrategyAwardVO chainStrategyAwardVO = raffleLogicChain(userId, strategyId);
        log.info("抽奖策略计算-责任链 {} {} {} {}", userId, strategyId, chainStrategyAwardVO.getAwardId(), chainStrategyAwardVO.getLogicModel());
        if (!chainStrategyAwardVO.getLogicModel().equals(DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode())){
            return RaffleAwardEntity.builder()
                    .strategyId(strategyId)
                    .awardId(chainStrategyAwardVO.getAwardId())
                    .build();
        }

        // 3、规则树抽奖过滤【奖品ID，会根据抽奖次数判断、库存判断、兜底兜里返回最终的可获得奖品信息】
        DefaultTreeFactory.StrategyAwardVO treeStrategyAwardVO = raffleLogicTree(userId, strategyId, chainStrategyAwardVO.getAwardId());
        log.info("抽奖策略计算-规则树 {} {} {} {}", userId, strategyId, treeStrategyAwardVO.getAwardId(), treeStrategyAwardVO.getAwardRuleValue());

        return RaffleAwardEntity.builder()
                .awardId(treeStrategyAwardVO.getAwardId())
                .awardConfig(treeStrategyAwardVO.getAwardRuleValue())
                .build();
    }

    /**
     * 抽奖计算，责任链抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    public abstract DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId);

    /**
     * 抽奖结果过滤，决策树抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     * @return 过滤结果【奖品ID，会根据抽奖次数判断、库存判断、兜底兜里返回最终的可获得奖品信息】
     */
    public abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId);


    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckBeforeRaffle(RaffleFactorEntity build, String... logics);

    protected abstract RuleActionEntity<RuleActionEntity.RaffleMidEntity> doCheckMidRaffle(RaffleFactorEntity build, String... logics);
}
