package com.zhaoyi.yijiaoyou.model.dto.user;

import java.io.Serializable;


/**
 *用户注册请求
 * @author zhaoyi
 */

public class UserLoginDTO implements Serializable {


    private static final long serialVersionUID = -7766912188023841312L;
    private String phone;

    private String password;


    public UserLoginDTO() {
    }

    public UserLoginDTO(String username, String password) {
        this.phone = username;
        this.password = password;
    }

    /**
     * 获取
     * @return username
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }


    public String toString() {
        return "UserRegisterRequest{username = " + phone + ", password = " + password + ", phone = "  + "}";
    }
}
