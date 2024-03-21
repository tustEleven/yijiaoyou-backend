package com.zhaoyi.yijiaoyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhaoyi.yijiaoyou.model.entity.Team;
import org.apache.ibatis.annotations.Mapper;

/**
* @author PC
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2024-01-18 17:11:33
* @Entity com.zhaoyi.yijiaoyou.model.domain.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {


}




