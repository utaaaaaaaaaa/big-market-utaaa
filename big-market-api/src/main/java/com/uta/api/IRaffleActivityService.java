package com.uta.api;

import com.uta.api.entity.dto.ActivityDrawDTO;
import com.uta.api.entity.dto.GetUserActivityAccountValuesDTO;
import com.uta.api.entity.vo.ActivityDrawVO;
import com.uta.api.entity.vo.GetUserActivityAccountValuesVO;
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

    /**
     * 日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    Response<Boolean> calendarSignRebate(String userId);

    /**
     * 判断是否完成日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    Response<Boolean> isCalendarSignRebate(String userId);

    /**
     * 查询用户活动账户剩余次数
     * @param getUserActivityAccountValuesDTO 请求对象
     * @return 用户活动抽奖剩余次数
     */
    Response<GetUserActivityAccountValuesVO> GetUserActivityAccountValues(GetUserActivityAccountValuesDTO getUserActivityAccountValuesDTO);

}
