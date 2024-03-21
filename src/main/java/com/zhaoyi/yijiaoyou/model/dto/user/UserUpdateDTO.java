package com.zhaoyi.yijiaoyou.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户
 * @TableName user
 */
@Data
public class UserUpdateDTO implements Serializable {

    /**
     * 要更新数据的用户id
     */
    private Long id ;
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */

    private String userPassword;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;


    /**
     * 邮箱
     */
    private String email;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}