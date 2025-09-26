package com.uta.api;

import com.uta.api.entity.dto.GetRaffleAwardListDTO;
import com.uta.api.entity.dto.RaffleDTO;
import com.uta.api.entity.vo.GetRaffleAwardListVO;
import com.uta.api.entity.vo.RaffleVO;
import com.uta.types.model.Response;

import java.util.List;

/**
 * 抽奖服务接口
 */
public interface IRaffleService {

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
     * 抽奖接口
     * @param raffleDTO 请求参数
     * @return 抽奖结果
     */
    Response<RaffleVO> raffle(RaffleDTO raffleDTO);

}
