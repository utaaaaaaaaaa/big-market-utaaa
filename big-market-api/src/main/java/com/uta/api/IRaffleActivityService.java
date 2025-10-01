package com.uta.api;

import com.uta.api.entity.dto.ActivityDrawDTO;
import com.uta.api.entity.vo.ActivityDrawVO;
import com.uta.types.model.Response;

/**
 * 抽奖活动服务
 */
public interface IRaffleActivityService {

    /**
     * 活动装配，数据预热缓存
     * @param activityId 活动id
     * @return 装配结果
     */
    Response<Boolean> armory(Long activityId);

    /**
     * 活动抽奖接口
     * @param activityDrawDTO 请求对象
     * @return 奖品包装
     */
    Response<ActivityDrawVO> draw(ActivityDrawDTO activityDrawDTO);

}
