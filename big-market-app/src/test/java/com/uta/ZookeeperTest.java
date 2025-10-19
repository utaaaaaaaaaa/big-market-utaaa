package com.uta;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@SpringBootTest
public class ZookeeperTest {

    @Resource
    private CuratorFramework zookeeperClient;

    @Test
    public void createNode() throws Exception {
        String path = "/big-market/config/downgradeSwitch/test/a";
        if (null == zookeeperClient.checkExists().forPath(path)){
            zookeeperClient.create().creatingParentsIfNeeded().forPath(path);
        }
    }

    @Test
    public void setData() throws Exception {
        String path = "/big-market/config/downgradeSwitch/test/a";
        String data = "utaaa";
        zookeeperClient.setData().forPath(path,data.getBytes());
    }

    @Test
    public void getData() throws Exception {
        String path = "/big-market/config/downgradeSwitch/test/a";
        byte[] data = zookeeperClient.getData().forPath(path);
        log.info(new String(data, StandardCharsets.UTF_8));
    }

}
