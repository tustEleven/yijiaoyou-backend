package com.zhaoyi.yijiaoyou.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhaoyi.yijiaoyou.common.BaseContext;
import com.zhaoyi.yijiaoyou.common.Constant;
import com.zhaoyi.yijiaoyou.common.ErrorCode;
import com.zhaoyi.yijiaoyou.exception.BusinessException;
import com.zhaoyi.yijiaoyou.mapper.UserMapper;
import com.zhaoyi.yijiaoyou.model.dto.user.SafetyUser;
import com.zhaoyi.yijiaoyou.model.dto.user.UserRegisterDTO;
import com.zhaoyi.yijiaoyou.model.dto.user.UserUpdateDTO;
import com.zhaoyi.yijiaoyou.model.entity.User;
import com.zhaoyi.yijiaoyou.service.UserService;
import com.zhaoyi.yijiaoyou.utils.AlgorithmUtils;
import com.zhaoyi.yijiaoyou.utils.AliFileUploadUtils;
import com.zhaoyi.yijiaoyou.utils.JwtUtils;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author PC
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-01-05 22:00:34
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * 加了个盐值
     * 后期做了解
     */
    private static final String YANZHI = "zhaoyi";
    private static final String signKey = "zhaoyi";


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AliFileUploadUtils aliFileUploadUtils;


    /**
     * 用户注册
     *
     * @param userRegisterDTO
     */
    @Override
    public void userRegister(UserRegisterDTO userRegisterDTO) {

        String username = userRegisterDTO.getUsername();
        String password = userRegisterDTO.getPassword();
        String phone = userRegisterDTO.getPhone();
        String checkPassword = userRegisterDTO.getCheckPassword();


        /**
         * 用户名不得超过六位
         * 密码大于4 小于8
         * phone 11位
         * 检查两次的密码是否一样
         */
        if (username.length() > 6 || username.length() < 0) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        if (password.length() < 4 || password.length() > 8) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        if (phone.length() != 11) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        if (!checkPassword.equals(password)) {
            throw new BusinessException(ErrorCode.PASSWORD_INCONSISTENCY);
        }

        //判断用户是否已经注册
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        /**
         * Exception必须被处理
         * RuntimeException可以不被处理
         * 回头巩固
         * 后查表
         */
        User userExist = getOne(queryWrapper);
        if (userExist != null) {
            throw new BusinessException(ErrorCode.USER_EXIST);
        }

        //密码加密
        String encryptedPassword = SecureUtil.md5(password + YANZHI);
        //直接进行数据的复制
        User user = new User();
        BeanUtil.copyProperties(userRegisterDTO, user);
        user.setUserPassword(encryptedPassword);
        MultipartFile file = userRegisterDTO.getAvatarFile();
        try {
            String url = aliFileUploadUtils.upload(file);
            user.setAvatarUrl(url);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
//        user.setUsername(username);
//        user.setUserPassword(encryptedPassword);
//        user.setPhone(phone);
//        user.setUserTags(userTags);


        userMapper.insert(user);

    }

    /**
     * 用户登录
     *
     * @param phone
     * @param password
     * @return
     */
    @Override
    public String userLogin(String phone, String password) {
        //密码规范性
        if (StrUtil.isAllBlank(phone, password)) {
            throw new BusinessException(ErrorCode.INCOMPLETE_INFORMATION);
        }
        if (phone.length() > 11 || phone.length() < 0) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        if (password.length() < 4 || password.length() > 8) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        //密码验证
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = userMapper.selectOne(queryWrapper);
        if (!SecureUtil.md5(password + YANZHI).equals(user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        /**
         * 后面了解session
         * 好像还要脱敏
         */
        Long id = user.getId();
        Map<String, Object> claim = new HashMap<>();
        claim.put("id", id);
        String jwt = JwtUtils.generateJwt(claim, signKey);
        return jwt;
    }

    /**
     * 用户查找
     *
     * @param username
     * @return
     */
    public List<User> userSelect(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        for (int i = 0; i < users.size(); i++) {
            users.get(i).setUserPassword("******");
        }
        return users;
    }

    /**
     * 用户删除
     *
     * @param userId
     */
    @Override
    public void userDelete(Long userId) {
        userMapper.deleteById(userId);
    }

    /**
     * 更新用户信息 分为管理员和普通用户
     *
     * @param userUpdateDTO
     */
    @Override
    public void userUpdate(UserUpdateDTO userUpdateDTO) {
        User loginUser = this.getById(BaseContext.getCurrentId());
        //管理员(管理员可以更新任何人的消息)
        if (isAdmin()) {
            //获取需要更改的信息
            String username = userUpdateDTO.getUsername();
            Long id = userUpdateDTO.getId();
            String avatarUrl = userUpdateDTO.getAvatarUrl();
            Integer gender = userUpdateDTO.getGender();
            String email = userUpdateDTO.getEmail();
            //找到要更新信息的用户
            //这里登录的不是要更改的人 所以需要去查询
            User updateUser = getById(id);
            //更新用户信息
            updateUser.setUsername(username);
            updateUser.setAvatarUrl(avatarUrl);
            updateUser.setGender(gender);
            updateUser.setEmail(email);
            try {
                userMapper.updateById(updateUser);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.NO_UPDATE_INFORMATION);
            }
        } else if (loginUser.getUsername().equals(userUpdateDTO.getUsername())) {
            //更新用户信息
            loginUser.setAvatarUrl(userUpdateDTO.getAvatarUrl());
            loginUser.setGender(userUpdateDTO.getGender());
            loginUser.setUsername(userUpdateDTO.getUsername());
            loginUser.setEmail(userUpdateDTO.getEmail());
            //登录就是要更新的对象 直接更新
            try {
                userMapper.updateById(loginUser);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.NO_UPDATE_INFORMATION);
            }
        } else {
            throw new BusinessException(ErrorCode.NO_PERMISSIONS);
        }


    }

    /**
     * 根据标签搜索哟用户
     * 分别写了基于内存查找和数据库查找的方式
     *
     * @param tagNameList
     * @return
     */
    @Override
    public List<SafetyUser> searchUserByTags(List<String> tagNameList) {

        /**
         *
         * 根据内存查询
         List<User> userList = userMapper.selectList(userQueryWrapper);
         List<SafetyUser> safetyUserList = new ArrayList<>();
         Gson gson = new Gson();
         for (User user : userList) {
         boolean temp = true;
         String userTags = user.getUserTags();
         List<String> tagsList = gson.fromJson(userTags,new TypeToken<List<String>>() {}.getType());
         for (String tagName : tagNameList) {
         if (!tagsList.contains(tagName)){
         temp=false;
         }
         }
         if (temp){
         safetyUserList.add(getSafetyUser(user));
         }
         }
         return safetyUserList;
         */

        /**
         * * 根据数据库查询
         */
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            userQueryWrapper.like("user_tags", tagName);
        }
        List<User> userList = userMapper.selectList(userQueryWrapper);
        List<SafetyUser> safetyUserList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            SafetyUser safetyUser = getSafetyUser(userList.get(i));
            safetyUserList.add(safetyUser);
        }
        return safetyUserList;
    }

    /**
     * 私密消息脱敏获得安全用户 不把重要的信息透露出去
     *
     * @param user
     * @return
     */
    @Override
    public SafetyUser getSafetyUser(User user) {
        SafetyUser safetyUser = new SafetyUser();
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserTags(user.getUserTags());
        return safetyUser;
    }

    /**
     * 判断是否为管理员  判断是否有权限
     */
    public boolean isAdmin() {
        try {
            User loginUser = this.getById(BaseContext.getCurrentId());

            if (loginUser.getUserRole() != Constant.ADMIN || loginUser.getUserRole() == null) {
                return false;
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NO_PERMISSIONS);
        }
        return true;
    }

    /**
     * 获取登录用户
     *
     * @return
     */
    @Override
    public User getLoginUser() {
        Long currentId = BaseContext.getCurrentId();
        User loginUser = this.getById(currentId);
        return loginUser;
    }

    /**
     * 根据标签匹配用户
     *
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsersByTags(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "user_tags");
        queryWrapper.isNotNull("user_tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getUserTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getUserTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

    @Override
    public List<User> recommend() {
        User loginUSer = this.getById(BaseContext.getCurrentId());
        String redisKey = "yijiaoyou:user:recommend";
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存
        List<User> users = (List<User>) valueOperations.get(redisKey);
        if (users != null) {
            return users;
        }
        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //last 起到了拼接的作用
        queryWrapper.last("LIMIT 10");
        List<User> list = this.list(queryWrapper);
        // 写缓存
        try {
            valueOperations.set(redisKey, list, 30000, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return list;
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) {
        try {
            String url = aliFileUploadUtils.upload(file);
            return url;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

}




