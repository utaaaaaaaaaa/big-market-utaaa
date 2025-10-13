package com.uta.domain.award.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskStateVO {

    create("create","创建"),
    completed("completed","发奖完成"),
    fail("fail","发奖失败")
    ;

    private final String code;
    private final String info;

}
