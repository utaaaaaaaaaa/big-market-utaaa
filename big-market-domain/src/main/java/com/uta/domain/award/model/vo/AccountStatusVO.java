package com.uta.domain.award.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatusVO {

    OPEN("open","开启"),
    CLOSE("close","冻结"),
    ;

    private final String code;
    private final String info;

}
