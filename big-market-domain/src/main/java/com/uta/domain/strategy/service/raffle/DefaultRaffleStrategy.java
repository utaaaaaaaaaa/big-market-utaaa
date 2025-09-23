package com.uta.domain.strategy.service.raffle;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uta.domain.strategy.model.entity.RaffleFactorEntity;
import com.uta.domain.strategy.model.entity.RuleActionEntity;
import com.uta.domain.strategy.model.entity.RuleMatterEntity;
import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.armory.IStrategyDispatch;
import com.uta.domain.strategy.service.rule.ILogicFilter;
import com.uta.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {

    @Resource
    private DefaultLogicFactory factory;

    public DefaultRaffleStrategy(IStrategyDispatch strategyDispatch, IStrategyRepository repository) {
        super(strategyDispatch, repository);
    }

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckBeforeRaffle(RaffleFactorEntity build, String... logics) {
        if (logics == null || logics.length == 0){
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        Map<String, ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> map = factory.openLogicFilter();

        // 黑名单规则优先过滤
        String ruleBlackList = Arrays.stream(logics)
                .filter(str -> str.contains(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);

        if (StringUtils.isNotBlank(ruleBlackList)){
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> filter = map.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleMatterEntity ruleMatterEntity = RuleMatterEntity.builder()
                    .strategyId(build.getStrategyId())
                    .userId(build.getUserId())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                    .build();
            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleAction = filter.filter(ruleMatterEntity);
            if (!Objects.equals(ruleAction.getCode(), RuleLogicCheckTypeVO.ALLOW.getCode())){
                return ruleAction;
            }
        }

        // 顺序处理其他规则
        List<String> ruleList = Arrays.stream(logics)
                .filter(str -> !str.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());

        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = null;
        for (String ruleModel : ruleList) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = map.get(ruleModel);
            RuleMatterEntity ruleMatterEntity = RuleMatterEntity.builder()
                    .strategyId(build.getStrategyId())
                    .userId(build.getUserId())
                    .ruleModel(ruleModel)
                    .build();
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            // 非放行结果则顺序过滤
            log.info("抽奖前规则过滤 userId: {} ruleModel: {} code: {} info: {}", build.getUserId(), ruleActionEntity.getRuleModel(), ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())){
                return ruleActionEntity;
            }
        }

        return ruleActionEntity;
    }

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleMidEntity> doCheckMidRaffle(RaffleFactorEntity build, String... logics) {
        if (logics == null || logics.length == 0){
            return RuleActionEntity.<RuleActionEntity.RaffleMidEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        Map<String, ILogicFilter<RuleActionEntity.RaffleMidEntity>> map = factory.openLogicFilter();

        RuleActionEntity<RuleActionEntity.RaffleMidEntity> ruleActionEntity = null;
        for (String ruleModel : logics) {
            ILogicFilter<RuleActionEntity.RaffleMidEntity> logicFilter = map.get(ruleModel);
            RuleMatterEntity ruleMatterEntity = RuleMatterEntity.builder()
                    .strategyId(build.getStrategyId())
                    .userId(build.getUserId())
                    .awardId(build.getAwardId())
                    .ruleModel(ruleModel)
                    .build();
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            // 非放行结果则顺序过滤
            log.info("抽奖中规则过滤 userId: {} ruleModel: {} code: {} info: {}", build.getUserId(), ruleActionEntity.getRuleModel(), ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())){
                return ruleActionEntity;
            }
        }

        return ruleActionEntity;
    }
}
