package com.zhaoyi.yijiaoyou.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SafetyUser implements Serializable{


    private static final long serialVersionUID = 3792000357453456390L;
    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

  
    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户标签
     */
    private String userTags;

}
