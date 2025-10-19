package com.uta.config.factory;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uta.types.annotations.DCCValue;
import com.uta.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class DCCValueBeanFactory implements BeanPostProcessor {

    private final CuratorFramework client;

    private final Map<String, Object> dccObjMap = new HashMap<>();

    public DCCValueBeanFactory(CuratorFramework client) throws Exception {
        this.client = client;

        if (null == client.checkExists().forPath(Constants.BASE_CONFIG_PATH_CONFIG)){
            client.create().creatingParentsIfNeeded().forPath(Constants.BASE_CONFIG_PATH_CONFIG);
        }

        CuratorCache cache = CuratorCache.build(client, Constants.BASE_CONFIG_PATH_CONFIG);
        cache.start();

        // 监听zk节点变化
        cache.listenable().addListener((type, oldData, data)->{
            switch (type){
                case NODE_CHANGED:
                    String path = data.getPath();
                    Object o = dccObjMap.get(path);
                    if (null == o){return ;}
                    try {
                        Field declaredField = o.getClass().getDeclaredField(path.substring(path.lastIndexOf("/") + 1));
                        declaredField.setAccessible(true);
                        declaredField.set(o, new String(data.getData(), StandardCharsets.UTF_8));
                        declaredField.setAccessible(false);
                    }catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                default:
                    break;
            }
        });
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DCCValue.class)) continue;
            DCCValue dccValue = field.getAnnotation(DCCValue.class);
            String value = dccValue.value();
            if (StringUtils.isBlank(value)){
                throw new RuntimeException("DCCValue is not config");
            }
            String[] split = value.split(Constants.COLON);
            String key = split[0];
            String defaultValue = split.length == 2 ? split[1] : null;

            String keyPath = Constants.BASE_CONFIG_PATH_CONFIG.concat("/").concat(key);
            try {
                if (client.checkExists().forPath(keyPath) == null){
                    client.create().creatingParentsIfNeeded().forPath(keyPath);
                    if (StringUtils.isNotBlank(defaultValue)){
                        field.setAccessible(true);
                        field.set(bean, defaultValue);
                        field.setAccessible(false);
                    }
                }else {
                    String configValue = new String(client.getData().forPath(keyPath), StandardCharsets.UTF_8);
                    if (StringUtils.isNotBlank(configValue)){
                        field.setAccessible(true);
                        field.set(bean, configValue);
                        field.setAccessible(false);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            dccObjMap.put(keyPath, bean);
        }


        return bean;
    }
}
