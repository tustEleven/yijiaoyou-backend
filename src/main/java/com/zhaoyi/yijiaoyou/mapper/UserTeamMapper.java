package com.zhaoyi.yijiaoyou.mapper;

import com.zhaoyi.yijiaoyou.model.entity.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author PC
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2024-01-18 17:11:44
* @Entity com.zhaoyi.yijiaoyou.model.domain.UserTeam
*/
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}




