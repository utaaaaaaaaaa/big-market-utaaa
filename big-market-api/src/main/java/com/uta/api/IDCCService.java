package com.uta.api;

import com.uta.types.model.Response;

/**
 * 配置中心配置管理接口
 */
public interface IDCCService {

    Response<Boolean> updateConfig(String key, String value);

}
