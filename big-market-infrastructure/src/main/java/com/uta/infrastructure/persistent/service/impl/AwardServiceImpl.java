package com.uta.infrastructure.persistent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uta.infrastructure.persistent.pojo.Award;
import com.uta.infrastructure.persistent.service.AwardService;
import com.uta.infrastructure.persistent.mapper.AwardMapper;
import org.springframework.stereotype.Service;

/**
* @author 24333
* @description 针对表【award】的数据库操作Service实现
* @createDate 2025-09-17 20:40:14
*/
@Service
public class AwardServiceImpl extends ServiceImpl<AwardMapper, Award>
    implements AwardService{

}




