package com.zhaoyi.yijiaoyou.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhaoyi.yijiaoyou.model.dto.user.SafetyUser;
import com.zhaoyi.yijiaoyou.model.dto.user.UserUpdateDTO;
import com.zhaoyi.yijiaoyou.model.dto.user.UserRegisterDTO;
import com.zhaoyi.yijiaoyou.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author PC
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-01-05 22:00:34
*/

public interface UserService extends IService<User> {

    void userRegister(UserRegisterDTO userRegisterDTO);

    String userLogin(String phone, String password);

    List<User> userSelect(String username);

    void userDelete(Long userId);

    void userUpdate(UserUpdateDTO userUpdateDTO);

    List<SafetyUser> searchUserByTags(List<String> tagNameList);

    SafetyUser getSafetyUser(User user);

    boolean isAdmin();

    User getLoginUser();


    List<User> matchUsersByTags(long num ,User loginUser);

    List<User> recommend();

    String upload(MultipartFile file);
}
