package com.uta.infrastructure.persistent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uta.infrastructure.persistent.po.RaffleActivity;
import com.uta.infrastructure.persistent.service.RaffleActivityService;
import com.uta.infrastructure.persistent.dao.RaffleActivityMapper;
import org.springframework.stereotype.Service;

/**
* @author 24333
* @description 针对表【raffle_activity(抽奖活动表)】的数据库操作Service实现
* @createDate 2025-09-26 10:50:19
*/
@Service
public class RaffleActivityServiceImpl extends ServiceImpl<RaffleActivityMapper, RaffleActivity>
    implements RaffleActivityService{

}




