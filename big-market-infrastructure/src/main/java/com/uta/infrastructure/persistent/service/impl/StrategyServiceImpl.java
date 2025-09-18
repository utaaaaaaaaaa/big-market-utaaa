package com.uta.infrastructure.persistent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uta.infrastructure.persistent.po.Strategy;
import com.uta.infrastructure.persistent.service.StrategyService;
import com.uta.infrastructure.persistent.dao.StrategyMapper;
import org.springframework.stereotype.Service;

/**
* @author 24333
* @description 针对表【strategy】的数据库操作Service实现
* @createDate 2025-09-17 20:40:14
*/
@Service
public class StrategyServiceImpl extends ServiceImpl<StrategyMapper, Strategy>
    implements StrategyService{

}




