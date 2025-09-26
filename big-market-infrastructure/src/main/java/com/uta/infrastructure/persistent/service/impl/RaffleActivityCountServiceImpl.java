package com.uta.infrastructure.persistent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uta.infrastructure.persistent.po.RaffleActivityCount;
import com.uta.infrastructure.persistent.service.RaffleActivityCountService;
import com.uta.infrastructure.persistent.dao.RaffleActivityCountMapper;
import org.springframework.stereotype.Service;

/**
* @author 24333
* @description 针对表【raffle_activity_count(抽奖活动次数配置表)】的数据库操作Service实现
* @createDate 2025-09-26 10:50:19
*/
@Service
public class RaffleActivityCountServiceImpl extends ServiceImpl<RaffleActivityCountMapper, RaffleActivityCount>
    implements RaffleActivityCountService{

}




