package com.zhaoyi.yijiaoyou.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;


/**
 *用户注册请求
 * @author zhaoyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO implements Serializable {
    private static final long serialVersionUID = -2638023139286098069L;

    private String userTags;

    private String username;
    /**
     * 用户头像
     */
    private MultipartFile avatarFile;

    private String password;

    private String phone;

    private String checkPassword;



}
