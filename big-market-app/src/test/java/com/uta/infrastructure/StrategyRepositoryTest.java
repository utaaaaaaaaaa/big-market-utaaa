package com.uta.infrastructure;

import com.uta.domain.strategy.model.vo.RuleTreeVO;
import com.uta.infrastructure.persistent.po.RuleTreeNodeLine;
import com.uta.infrastructure.persistent.repository.StrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest
public class StrategyRepositoryTest {

    @Resource
    private StrategyRepository repository;

    @Test
    public void test() {
        String treeId = "tree_lock";
        RuleTreeVO ruleTreeVO = repository.getRuleTreeVOByTreeId(treeId);
        log.info("测试结果：{}",ruleTreeVO.toString());
    }

}
