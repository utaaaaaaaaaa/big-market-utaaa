package com.uta.trigger.http;

import com.uta.api.IDCCService;
import com.uta.types.common.Constants;
import com.uta.types.enums.ResponseCode;
import com.uta.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/dcc")
public class IDCCController implements IDCCService {

    @Resource
    private CuratorFramework zookeeperClient;

    @PostMapping("/update_config")
    @Override
    public Response<Boolean> updateConfig(@RequestParam String key,@RequestParam String value) {
        try {
            log.info("DCC 动态配置开始 key:{} value:{}", key, value);
            String keyPath = Constants.BASE_CONFIG_PATH_CONFIG.concat("/").concat(key);
            if (zookeeperClient.checkExists().forPath(keyPath) == null) {
                log.info("DCC 动态配置节点不存在，自动创建 key:{} value:{}", key, value);
                zookeeperClient.create().creatingParentsIfNeeded().forPath(keyPath);
            }
            Stat stat = zookeeperClient.setData().forPath(keyPath, value.getBytes(StandardCharsets.UTF_8));
            log.info("DCC 动态配置写入成功 key:{} value:{} time:{}", key, value, stat.getCtime());
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .build();

        } catch (Exception e) {
            log.error("DCC 动态配置失败 key:{}, value:{}", key, value);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
