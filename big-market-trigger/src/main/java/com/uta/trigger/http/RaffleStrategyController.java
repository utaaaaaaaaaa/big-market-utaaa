package com.uta.trigger.http;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uta.api.IRaffleStrategyService;
import com.uta.api.entity.dto.GetRaffleAwardListDTO;
import com.uta.api.entity.dto.RaffleStrategyDTO;
import com.uta.api.entity.dto.RaffleStrategyRuleWeightDTO;
import com.uta.api.entity.vo.GetRaffleAwardListVO;
import com.uta.api.entity.vo.RaffleStrategyRuleWeightVO;
import com.uta.api.entity.vo.RaffleStrategyVO;
import com.uta.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.uta.domain.activity.service.IRaffleActivityPartakeService;
import com.uta.domain.strategy.model.entity.RaffleAwardEntity;
import com.uta.domain.strategy.model.entity.RaffleFactorEntity;
import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import com.uta.domain.strategy.model.vo.RuleWeightVO;
import com.uta.domain.strategy.service.IRaffleAward;
import com.uta.domain.strategy.service.IRaffleRule;
import com.uta.domain.strategy.service.IRaffleStrategy;
import com.uta.domain.strategy.service.armory.IStrategyArmory;
import com.uta.types.enums.ResponseCode;
import com.uta.types.exception.AppException;
import com.uta.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy")
@DubboService(version = "1.0")
public class RaffleStrategyController implements IRaffleStrategyService {

    @Autowired
    private IStrategyArmory strategyArmory;

    @Autowired
    private IRaffleAward raffleAward;

    @Autowired
    private IRaffleRule raffleRule;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private IRaffleActivityAccountQuotaService activityAccountQuotaService;

    @Autowired
    private IRaffleActivityPartakeService raffleActivityPartakeService;

    @Override
    @PostMapping("strategy_armory")
    public Response<Boolean> strategyArmory(Long strategyId) {
        try {
            log.info("抽奖策略装配开始 strategyId:{}", strategyId);
            boolean armoryStatus = strategyArmory.assembleLotteryStrategy(strategyId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(armoryStatus)
                    .build();
        }catch (Exception e){
            log.error("抽奖策略装配失败 strategyId:{}", strategyId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @PostMapping("get_award_list")
    public Response<List<GetRaffleAwardListVO>> getRaffleAwardList(@RequestBody GetRaffleAwardListDTO getRaffleAwardListDTO) {
        Long strategyId = getRaffleAwardListDTO.getStrategyId();
        try {
            log.info("查询奖品列表 userId:{} activityId：{}", getRaffleAwardListDTO.getUserId(), getRaffleAwardListDTO.getActivityId());
            if (StringUtils.isBlank(getRaffleAwardListDTO.getUserId()) || getRaffleAwardListDTO.getActivityId() == null){
                return Response.<List<GetRaffleAwardListVO>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            List<StrategyAwardEntity> raffleAwardList = raffleAward.getRaffleAwardListByActivityId(getRaffleAwardListDTO.getActivityId());

            // 获取规则配置
            String[] ruleModels = raffleAwardList.stream()
                    .map(StrategyAwardEntity::getRuleModels)
                    .filter(rule -> rule != null && !rule.isEmpty())
                    .toArray(String[]::new);
            // 查询规则配置，获取奖品解锁次数
            Map<String, Integer> ruleLockCountMap = raffleRule.queryAwardRuleLockCount(ruleModels);
            // 查询抽奖次数 -用户已经参与抽奖的次数
            Integer dayPartakeCount = activityAccountQuotaService.getUserDayPartakeCount(getRaffleAwardListDTO.getUserId(), getRaffleAwardListDTO.getActivityId());

//            List<GetRaffleAwardListVO> awardVOList = raffleAwardList.stream()
//                    .map(this::obj2vo)
//                    .collect(Collectors.toList());
            List<GetRaffleAwardListVO> awardVOList = new ArrayList<>(raffleAwardList.size());
            for (StrategyAwardEntity strategyAward : raffleAwardList) {
                Integer awardRuleLockCount = ruleLockCountMap.get(strategyAward.getRuleModels());
                awardVOList.add(GetRaffleAwardListVO.builder()
                        .awardId(strategyAward.getAwardId())
                        .awardTitle(strategyAward.getAwardTitle())
                        .awardSubtitle(strategyAward.getAwardSubtitle())
                        .sort(strategyAward.getSort())
                        .awardRuleLockCount(awardRuleLockCount)
                        .isAwardUnlock(null == awardRuleLockCount || dayPartakeCount >= awardRuleLockCount)
                        .waitUnlockCount(null == awardRuleLockCount || awardRuleLockCount <= dayPartakeCount ? 0 : awardRuleLockCount - dayPartakeCount)
                        .build());
            }
            return Response.<List<GetRaffleAwardListVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(awardVOList)
                    .build();
        }catch (Exception e){
            log.error("抽奖策略装配失败 userId:{} activityId：{}", getRaffleAwardListDTO.getUserId(), getRaffleAwardListDTO.getActivityId());
            return Response.<List<GetRaffleAwardListVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @PostMapping("/query_raffle_strategy_rule_weight")
    @Override
    public Response<List<RaffleStrategyRuleWeightVO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightDTO request) {
        try {
            log.info("查询抽奖策略权重规则配置开始 userId:{} activityId：{}", request.getUserId(), request.getActivityId());
            // 1. 参数校验
            if (StringUtils.isBlank(request.getUserId()) || null == request.getActivityId()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2. 查询用户抽奖总次数
            Integer userActivityAccountTotalUseCount = activityAccountQuotaService.queryRaffleActivityAccountPartakeCount(request.getActivityId(), request.getUserId());
            // 3. 查询规则
            List<RaffleStrategyRuleWeightVO> raffleStrategyRuleWeightList = new ArrayList<>();
            List<RuleWeightVO> ruleWeightVOList = raffleRule.queryAwardRuleWeightByActivityId(request.getActivityId());
            for (RuleWeightVO ruleWeightVO : ruleWeightVOList) {
                // 转换对象
                List<RaffleStrategyRuleWeightVO.StrategyAward> strategyAwards = new ArrayList<>();
                List<RuleWeightVO.Award> awardList = ruleWeightVO.getAwardList();
                for (RuleWeightVO.Award award : awardList) {
                    RaffleStrategyRuleWeightVO.StrategyAward strategyAward = new RaffleStrategyRuleWeightVO.StrategyAward();
                    strategyAward.setAwardId(award.getAwardId());
                    strategyAward.setAwardTitle(award.getAwardTitle());
                    strategyAwards.add(strategyAward);
                }
                // 封装对象
                RaffleStrategyRuleWeightVO RaffleStrategyRuleWeightVO = new RaffleStrategyRuleWeightVO();
                RaffleStrategyRuleWeightVO.setRuleWeightCount(ruleWeightVO.getWeight());
                RaffleStrategyRuleWeightVO.setStrategyAwards(strategyAwards);
                RaffleStrategyRuleWeightVO.setUserActivityAccountTotalUseCount(userActivityAccountTotalUseCount);

                raffleStrategyRuleWeightList.add(RaffleStrategyRuleWeightVO);
            }
            Response<List<RaffleStrategyRuleWeightVO>> response = Response.<List<RaffleStrategyRuleWeightVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleStrategyRuleWeightList)
                    .build();
            log.info("查询抽奖策略权重规则配置完成 userId:{} activityId：{} response: {}", request.getUserId(), request.getActivityId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查询抽奖策略权重规则配置失败 userId:{} activityId：{}", request.getUserId(), request.getActivityId(), e);
            return Response.<List<RaffleStrategyRuleWeightVO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }

    }

    @Override
    @PostMapping("random_raffle")
    public Response<RaffleStrategyVO> raffle(@RequestBody RaffleStrategyDTO raffleDTO) {
        Long strategyId = raffleDTO.getStrategyId();
        try {
            log.info("抽奖开始 strategyId:{}", strategyId);
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId("system")
                    .strategyId(strategyId)
                    .build());
            return Response.<RaffleStrategyVO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleStrategyVO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
        }catch (AppException e){
            log.info("抽奖失败 strategyId:{}", strategyId);
            return Response.<RaffleStrategyVO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e){
            log.info("抽奖失败 strategyId:{}", strategyId);
            return Response.<RaffleStrategyVO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    private GetRaffleAwardListVO obj2vo(StrategyAwardEntity strategyAwardEntity){
        GetRaffleAwardListVO vo = new GetRaffleAwardListVO();
        BeanUtils.copyProperties(strategyAwardEntity, vo);

        return vo;
    }

}
