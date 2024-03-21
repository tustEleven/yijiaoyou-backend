package com.zhaoyi.yijiaoyou.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.zhaoyi.yijiaoyou.common.BaseResponse;
import com.zhaoyi.yijiaoyou.common.ErrorCode;
import com.zhaoyi.yijiaoyou.exception.BusinessException;
import com.zhaoyi.yijiaoyou.model.dto.user.SafetyUser;
import com.zhaoyi.yijiaoyou.model.dto.user.UserLoginDTO;
import com.zhaoyi.yijiaoyou.model.dto.user.UserRegisterDTO;
import com.zhaoyi.yijiaoyou.model.dto.user.UserUpdateDTO;
import com.zhaoyi.yijiaoyou.model.entity.User;
import com.zhaoyi.yijiaoyou.model.vo.UserListTestVo;
import com.zhaoyi.yijiaoyou.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户Controller接口
 *
 * @author zhaoyi
 */
@RestController
@Slf4j
@RequestMapping(value = "/yijiaoyou/user")
//不加tags UI页面不显示
@Api(tags = "用户管理")
public class UserController {


    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据标签查找用户
     *
     * @param tagNameList
     * @return
     */
    @GetMapping("/search/tags")
    @ApiOperation("根据标签查找用户")
    public BaseResponse<List<SafetyUser>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtil.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.THE_INCOMING_IS_EMPTY);
        }
        List<SafetyUser> safetyUserList = userService.searchUserByTags(tagNameList);
        log.info("根据标签查找用户成功");
        return BaseResponse.success(safetyUserList);
    }


    /**
     * 用户注册
     *
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse userRegister(@RequestBody UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO == null) {
            throw new BusinessException(ErrorCode.THE_INCOMING_IS_EMPTY);
        }
        //提取消息
        String userTags = userRegisterDTO.getUserTags();
        String username = userRegisterDTO.getUsername();
        String password = userRegisterDTO.getPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        String phone = userRegisterDTO.getPhone();
        //判断传入消息是否有空
        if (!StrUtil.isAllNotBlank(username, password, checkPassword, phone, userTags)) {
            throw new BusinessException(ErrorCode.INCOMPLETE_INFORMATION);
        }
        userService.userRegister(userRegisterDTO);
        log.info("注册成功");
        return BaseResponse.success();
    }

    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        if (userLoginDTO == null) {
            throw new BusinessException(ErrorCode.THE_INCOMING_IS_EMPTY);
        }
        //获取到登录信息
        String phone = userLoginDTO.getPhone();
        String password = userLoginDTO.getPassword();
        String jwt = userService.userLogin(phone, password);
        log.info("登录成功");
        return BaseResponse.success(jwt);
    }

    /**
     * 用户查询
     *
     * @param username
     * @return
     */
    @GetMapping("/select/{username}")
    @ApiOperation("用户查询")
    public BaseResponse<List> userSelect(@PathVariable String username) {
        if (username == null) {
            throw new BusinessException(ErrorCode.THE_INCOMING_IS_EMPTY);
        }
        if (!userService.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_PERMISSIONS);
        }
        List<User> list = userService.userSelect(username);
        log.info("用户查询成功");
        return BaseResponse.success(list);
    }

    /**
     * 用户删除
     *
     * @param userId
     * @return
     */
    @DeleteMapping("/delete/{userId}")
    @ApiOperation("用户删除")
    public BaseResponse userDelete(@PathVariable Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.THE_INCOMING_IS_EMPTY);
        }
//        if (!userService.isAdmin()) {
//            throw new BusinessException(ErrorCode.NO_PERMISSIONS);
//        }
        userService.userDelete(userId);
        log.info("删除成功");
        return BaseResponse.success();
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateDTO
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("更新用户信息")

    public BaseResponse userUpdate(@RequestBody UserUpdateDTO userUpdateDTO) {
        if (userService.isAdmin()) {
            if (userUpdateDTO == null) {
                throw new BusinessException(ErrorCode.THE_INCOMING_IS_EMPTY);
            }
            userService.userUpdate(userUpdateDTO);
            log.info("用户信息修改成功");
            return BaseResponse.success();
        } else {
            throw new BusinessException(ErrorCode.NO_PERMISSIONS);
        }
    }

    /**
     * 推荐用户
     *
     * @return
     */
    @GetMapping("/recommend")
    @ApiOperation("推荐用户")
    public BaseResponse<List<User>> recommendUsers() {

        List<User> usersRecommend = userService.recommend();
        return BaseResponse.success(usersRecommend);
    }

    @GetMapping("/test/list")
    public BaseResponse<List<UserListTestVo>> getUserListTest() {
        List<User> list = userService.list();
        List<UserListTestVo> userListTestVos = new ArrayList<>();
        for (User user : list) {
            UserListTestVo userListTestVo = new UserListTestVo();
            BeanUtil.copyProperties(user,userListTestVo);
            userListTestVos.add(userListTestVo);
        }
        return BaseResponse.success(userListTestVos);
    }

    @PostMapping("/match")
    @ApiOperation("根据标签推荐用户")
    public BaseResponse<List<User>> matchUsersByTags(long num) {
        User loginUser = userService.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_PERMISSIONS);
        }
        List<User> userList = userService.matchUsersByTags(num, loginUser);
        return BaseResponse.success(userList);
    }


}
