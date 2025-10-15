package com.uta.api.entity.dto;

import lombok.Data;

@Data
public class GetUserActivityAccountValuesDTO {

    private String userId;
    private Long activityId;

}
