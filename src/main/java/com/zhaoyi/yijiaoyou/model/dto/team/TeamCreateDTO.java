package com.zhaoyi.yijiaoyou.model.dto.team;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 封装的队伍创建请求
 */
@Data
public class TeamCreateDTO implements Serializable {
    private static final long serialVersionUID = 4687166911165894905L;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;




}
