package com.uta.domain.strategy.service.raffle;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uta.domain.strategy.model.entity.RaffleAwardEntity;
import com.uta.domain.strategy.model.entity.RaffleFactorEntity;
import com.uta.domain.strategy.model.entity.RuleActionEntity;
import com.uta.domain.strategy.model.entity.StrategyEntity;
import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.IRaffleStrategy;
import com.uta.domain.strategy.service.armory.IStrategyDispatch;
import com.uta.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.uta.types.enums.ResponseCode;
import com.uta.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 抽奖策略抽象类
 * 抽奖类实现接口，从而实现对接口方法的流程规范定义（也就是对接口方法定义一个通用模板）
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    protected IStrategyRepository repository;

    protected IStrategyDispatch strategyDispatch;

    public AbstractRaffleStrategy(IStrategyDispatch strategyDispatch, IStrategyRepository repository) {
        this.strategyDispatch = strategyDispatch;
        this.repository = repository;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactor) {
        // 1、参数校验
        Long strategyId = raffleFactor.getStrategyId();
        String userId = raffleFactor.getUserId();
        if (strategyId == null || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2、查询对应策略实体
        StrategyEntity strategy = repository.getStrategyEntityByStrategyId(strategyId);

        // 3、抽奖前 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = this.doCheckBeforeRaffle(raffleFactor, strategy.getRuleModel());

        if (Objects.equals(ruleActionEntity.getCode(), RuleLogicCheckTypeVO.TAKE_OVER.getCode())){
            if (ruleActionEntity.getRuleModel().equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())){
                // 黑名单返回固定奖品id
                return RaffleAwardEntity.builder()
                        .awardId(ruleActionEntity.getData().getAwardId())
                        .build();
            }else if(ruleActionEntity.getRuleModel().equals(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())){
                // 权重根据返回信息抽奖
                RuleActionEntity.RaffleBeforeEntity data = ruleActionEntity.getData();
                String ruleWeightValue = data.getRuleWeightValue();
                Integer randomAwardId = strategyDispatch.getRandomAwardId(strategyId, ruleWeightValue);
                return RaffleAwardEntity.builder()
                        .awardId(randomAwardId)
                        .build();
            }
        }

        // 4、执行抽奖
        Integer randomAwardId = strategyDispatch.getRandomAwardId(strategyId);

        // 5、抽奖中 -

        // 6、抽奖后 -

        return RaffleAwardEntity.builder()
                .awardId(randomAwardId)
                .build();
    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckBeforeRaffle(RaffleFactorEntity build, String... logics);

}
