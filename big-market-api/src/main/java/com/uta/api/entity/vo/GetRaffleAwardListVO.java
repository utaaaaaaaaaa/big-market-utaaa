package com.uta.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRaffleAwardListVO {

    private Integer awardId;
    private String awardTitle;
    private String awardSubtitle;
    private Integer sort;

}
