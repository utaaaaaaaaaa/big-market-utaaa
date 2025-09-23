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

    private final DefaultChainFactory factory;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory factory) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
        this.factory = factory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactor) {
        // 1、参数校验
        Long strategyId = raffleFactor.getStrategyId();
        String userId = raffleFactor.getUserId();
        if (strategyId == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2、责任链处理抽奖
        ILogicChain logicChain = factory.openLogicChain(strategyId);
        Integer awardId = logicChain.logic(userId, strategyId);

        // 5、查询奖品规则【抽奖中（拿到奖品id时，过滤规则）】、抽奖后（扣减奖品库存后过滤，抽奖中拦截或者库存不足则走兜底）
        AwardRuleModelVO awardRuleModels = repository.getAwardRuleModels(strategyId,awardId);
        String[] raffleMidRuleList = awardRuleModels.getRaffleMidRuleList();

        // 6、抽奖中 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleMidEntity> ruleActionMidEntity = doCheckMidRaffle(RaffleFactorEntity.builder()
                .awardId(awardId)
                .strategyId(strategyId)
                .userId(userId).build(), raffleMidRuleList);

        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionMidEntity.getCode())) {
            log.info("【临时日志】中奖规则拦截，通过抽奖后规则rule_lock_award走兜底奖励");
            return RaffleAwardEntity.builder()
                    .awardDesc("中奖规则拦截，通过抽奖后规则rule_lock_award走兜底奖励")
                    .build();
        }

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();
    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckBeforeRaffle(RaffleFactorEntity build, String... logics);

    protected abstract RuleActionEntity<RuleActionEntity.RaffleMidEntity> doCheckMidRaffle(RaffleFactorEntity build, String... logics);
}
