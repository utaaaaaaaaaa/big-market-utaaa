package com.uta.api;

import com.uta.api.entity.dto.GetRaffleAwardListDTO;
import com.uta.api.entity.dto.RaffleStrategyDTO;
import com.uta.api.entity.dto.RaffleStrategyRuleWeightDTO;
import com.uta.api.entity.vo.GetRaffleAwardListVO;
import com.uta.api.entity.vo.RaffleStrategyRuleWeightVO;
import com.uta.api.entity.vo.RaffleStrategyVO;
import com.uta.types.model.Response;

import java.util.List;

/**
 * 抽奖服务接口
 */
public interface IRaffleStrategyService {

    /**
     * 装配策略
     * @param strategyId 策略id
     * @return 装配结果
     */
    Response<Boolean> strategyArmory(Long strategyId);

    /**
     * 查询奖品列表
     * @param getRaffleAwardListDTO 查询奖品列表参数
     * @return 奖品列表
     */
    Response<List<GetRaffleAwardListVO>> getRaffleAwardList(GetRaffleAwardListDTO getRaffleAwardListDTO);

    /**
     * 查询抽奖策略权重规则，给用户展示出抽奖N次后必中奖奖品范围
     *
     * @param request 请求对象
     * @return 权重奖品配置列表「这里会返回全部，前端可按需展示一条已达标的，或者一条要达标的」
     */
    Response<List<RaffleStrategyRuleWeightVO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightDTO request);

    /**
     * 抽奖接口
     * @param raffleDTO 请求参数
     * @return 抽奖结果
     */
    Response<RaffleStrategyVO> raffle(RaffleStrategyDTO raffleDTO);

}
