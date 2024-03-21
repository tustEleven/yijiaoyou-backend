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
public class UserListTestVo implements Serializable {

    private static final long serialVersionUID = -687356274973003166L;
    private Long id;
    private String username;
    private String phone;
    /**
     * 队伍名称
     */

    private Date createTime;
    /**
     * 用户id
     */




}
