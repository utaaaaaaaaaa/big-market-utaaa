package com.uta.trigger.http;

import com.uta.api.IRaffleStrategyService;
import com.uta.api.entity.dto.GetRaffleAwardListDTO;
import com.uta.api.entity.dto.RaffleStrategyDTO;
import com.uta.api.entity.vo.GetRaffleAwardListVO;
import com.uta.api.entity.vo.RaffleStrategyVO;
import com.uta.domain.strategy.model.entity.RaffleAwardEntity;
import com.uta.domain.strategy.model.entity.RaffleFactorEntity;
import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import com.uta.domain.strategy.service.IRaffleAward;
import com.uta.domain.strategy.service.IRaffleStrategy;
import com.uta.domain.strategy.service.armory.IStrategyArmory;
import com.uta.types.enums.ResponseCode;
import com.uta.types.exception.AppException;
import com.uta.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy")
public class RaffleStrategyController implements IRaffleStrategyService {

    @Autowired
    private IStrategyArmory strategyArmory;

    @Autowired
    private IRaffleAward raffleAward;

    @Autowired
    private IRaffleStrategy raffleStrategy;

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
            log.info("查询奖品列表 strategyId:{}", strategyId);
            List<StrategyAwardEntity> raffleAwardList = raffleAward.getRaffleAwardList(strategyId);
            List<GetRaffleAwardListVO> awardVOList = raffleAwardList.stream()
                    .map(this::obj2vo)
                    .collect(Collectors.toList());
            return Response.<List<GetRaffleAwardListVO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(awardVOList)
                    .build();
        }catch (Exception e){
            log.error("抽奖策略装配失败 strategyId:{}", strategyId);
            return Response.<List<GetRaffleAwardListVO>>builder()
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
