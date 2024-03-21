package com.zhaoyi.yijiaoyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyi.yijiaoyou.model.entity.UserTeam;
import com.zhaoyi.yijiaoyou.service.UserTeamService;
import com.zhaoyi.yijiaoyou.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author PC
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-01-18 17:11:44
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




