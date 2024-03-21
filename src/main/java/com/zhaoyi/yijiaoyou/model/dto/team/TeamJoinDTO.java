package com.zhaoyi.yijiaoyou.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装的队伍加入请求
 */
@Data
public class TeamJoinDTO implements Serializable {
    private static final long serialVersionUID = -4959366248279677474L;

    private Long id;

    private String password;
}
