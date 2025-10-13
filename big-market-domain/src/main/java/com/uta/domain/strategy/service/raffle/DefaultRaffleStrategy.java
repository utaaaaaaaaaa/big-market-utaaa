package com.uta.domain.strategy.service.raffle;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uta.domain.strategy.model.entity.RaffleFactorEntity;
import com.uta.domain.strategy.model.entity.RuleActionEntity;
import com.uta.domain.strategy.model.entity.RuleMatterEntity;
import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import com.uta.domain.strategy.model.vo.AwardRuleModelVO;
import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.uta.domain.strategy.model.vo.RuleTreeVO;
import com.uta.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.IRaffleAward;
import com.uta.domain.strategy.service.IRaffleRule;
import com.uta.domain.strategy.service.IRaffleStock;
import com.uta.domain.strategy.service.IRaffleStrategy;
import com.uta.domain.strategy.service.armory.IStrategyDispatch;
import com.uta.domain.strategy.service.rule.chain.ILogicChain;
import com.uta.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.uta.domain.strategy.service.rule.filter.ILogicFilter;
import com.uta.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import com.uta.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.uta.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleStock, IRaffleAward, IRaffleRule {
    @Resource
    private DefaultLogicFactory factory;

    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory factory, DefaultChainFactory chainFactory, DefaultTreeFactory treeFactory) {
        super(repository, strategyDispatch, factory, chainFactory, treeFactory);
    }

    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain logicChain = chainFactory.openLogicChain(strategyId);
        return logicChain.logic(userId,strategyId);
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        return this.raffleLogicTree(userId, strategyId, awardId, null);
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId, Date endTime) {
        AwardRuleModelVO awardRuleModels = repository.getAwardRuleModels(strategyId, awardId);
        if (null == awardRuleModels) {
            return DefaultTreeFactory.StrategyAwardVO.builder().awardId(awardId).build();
        }
        RuleTreeVO ruleTreeVO = repository.getRuleTreeVOByTreeId(awardRuleModels.getRuleModels());
        if (null == ruleTreeVO) {
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 " + awardRuleModels.getRuleModels());
        }
        IDecisionTreeEngine treeEngine = treeFactory.openDecisionTree(ruleTreeVO);
        return treeEngine.process(userId, strategyId, awardId, endTime);
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
            log.info("抽奖前【规则过滤】 userId: {} ruleModel: {} code: {} info: {}", build.getUserId(), ruleActionEntity.getRuleModel(), ruleActionEntity.getCode(), ruleActionEntity.getInfo());
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
            log.info("抽奖中【规则过滤】 userId: {} ruleModel: {} code: {} info: {}", build.getUserId(), ruleActionEntity.getRuleModel(), ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())){
                return ruleActionEntity;
            }
        }

        return ruleActionEntity;
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        return repository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        repository.updateStrategyAwardStock(strategyId, awardId);
    }

    @Override
    public List<StrategyAwardEntity> getRaffleAwardList(Long strategyId) {
        return repository.getStrategyAwardList(strategyId);
    }

    @Override
    public List<StrategyAwardEntity> getRaffleAwardListByActivityId(Long activityId) {
        Long strategyId = repository.queryStrategyIdByActivityId(activityId);
        return getRaffleAwardList(strategyId);
    }

    @Override
    public Map<String, Integer> queryAwardRuleLockCount(String[] treeIds) {
        return repository.queryAwardRuleLockCount(treeIds);
    }
}
