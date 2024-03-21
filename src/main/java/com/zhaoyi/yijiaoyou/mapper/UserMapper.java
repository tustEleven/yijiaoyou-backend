package com.zhaoyi.yijiaoyou.mapper;

import com.zhaoyi.yijiaoyou.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author PC
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-01-05 22:00:34
* @Entity com.zhaoyi.usermanagement.model.domain.User.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




