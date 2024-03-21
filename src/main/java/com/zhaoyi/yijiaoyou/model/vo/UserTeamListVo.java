package com.zhaoyi.yijiaoyou.model.vo;

import com.zhaoyi.yijiaoyou.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 自己封装的队伍详情相应
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTeamListVo implements Serializable {
    private static final long serialVersionUID = 428646662476011474L;
    /**
     * id
     */
    private Long id;

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
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 管理员可以看
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;

    /**
     * 加入队伍的用户
     */
    private List<User> userList;
}
