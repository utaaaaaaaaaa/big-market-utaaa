package com.uta.domain.award.service.distribute.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uta.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.uta.domain.award.model.entity.DistributeAwardEntity;
import com.uta.domain.award.model.entity.UserAwardRecordEntity;
import com.uta.domain.award.model.entity.UserCreditAwardEntity;
import com.uta.domain.award.model.vo.AwardStateVO;
import com.uta.domain.award.repository.IAwardRepository;
import com.uta.domain.award.service.distribute.IDistributeAward;
import com.uta.types.common.Constants;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * 用户积分随机奖励，支持award_config透传
 */
@Component("user_credit_random")
public class UserCreditRandomAward implements IDistributeAward {

    @Resource
    private IAwardRepository repository;

    @Override
    public void giveOutPrizes(DistributeAwardEntity distributeAwardEntity) {
        String userId = distributeAwardEntity.getUserId();
        String orderId = distributeAwardEntity.getOrderId();
        Integer awardId = distributeAwardEntity.getAwardId();
        String awardConfig = distributeAwardEntity.getAwardConfig();

        // 没有透传award_config则主动查询数据库
        if (StringUtils.isBlank(awardConfig)) {
            awardConfig = repository.queryAwardConfig(awardId);
        }

        // 根据award_config生成随机积分
        String[] split = awardConfig.split(Constants.SPLIT);
        if (split.length != 2) {
            throw new RuntimeException("奖品配置award_config格式错误，award_config："+awardConfig);
        }

        BigDecimal credit = generateRandom(new BigDecimal(split[0]), new BigDecimal(split[1]));

        // 构建聚合对象
        UserAwardRecordEntity userAwardRecordEntity = GiveOutPrizesAggregate.buildDistributeUserAwardRecordEntity(
                distributeAwardEntity.getUserId(),
                distributeAwardEntity.getOrderId(),
                distributeAwardEntity.getAwardId(),
                AwardStateVO.completed
        );

        UserCreditAwardEntity userCreditAwardEntity = GiveOutPrizesAggregate.buildUserCreditAwardEntity(distributeAwardEntity.getUserId(), credit);

        GiveOutPrizesAggregate giveOutPrizesAggregate = new GiveOutPrizesAggregate();
        giveOutPrizesAggregate.setUserId(distributeAwardEntity.getUserId());
        giveOutPrizesAggregate.setUserAwardRecordEntity(userAwardRecordEntity);
        giveOutPrizesAggregate.setUserCreditAwardEntity(userCreditAwardEntity);

        // 存储发奖对象
        repository.saveGiveOutPrizesAggregate(giveOutPrizesAggregate);

    }

    private BigDecimal generateRandom(BigDecimal min, BigDecimal max) {
        if (min.equals(max)) {return min;}
        BigDecimal random = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return random.round(new MathContext(3));
    }

}
