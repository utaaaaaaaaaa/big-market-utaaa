package com.uta.infrastructure;

import com.uta.infrastructure.persistent.dao.AwardMapper;
import com.uta.infrastructure.persistent.po.Award;
import com.uta.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @description 奖品持久化单元测试
 */
@SpringBootTest
@Slf4j
public class AwardTest {

    @Autowired
    private IRedisService redisService;

    @Test
    public void test() {
        RMap<Object, Object> map = redisService.getMap("strategy_id_100001");
        map.put(1,101);
        map.put(2,101);
        map.put(3,101);
        map.put(4,102);
        map.put(5,102);
        map.put(6,103);
        map.put(7,103);
        map.put(8,103);
        log.info(redisService.getFromMap("strategy_id_100001",1).toString());
    }

}
