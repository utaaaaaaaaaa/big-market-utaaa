package com.uta.infrastructure;

import com.uta.infrastructure.persistent.mapper.AwardMapper;
import com.uta.infrastructure.persistent.pojo.Award;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @description 奖品持久化单元测试
 */
@SpringBootTest
public class AwardTest {

    @Autowired
    private AwardMapper awardMapper;

    @Test
    public void test() {
        List<Award> awardList = awardMapper.getAwardList();
        System.out.println(awardList);
    }

}
