package com.zhaoyi.yijiaoyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhaoyi.yijiaoyou.model.dto.team.TeamJoinDTO;
import com.zhaoyi.yijiaoyou.model.dto.team.TeamUpdateDTO;
import com.zhaoyi.yijiaoyou.model.entity.Team;
import com.zhaoyi.yijiaoyou.model.vo.UserTeamListVo;

import java.util.List;

/**
* @author PC
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-01-18 17:11:33
*/
public interface TeamService extends IService<Team> {

    void create(Team team);

    List<UserTeamListVo> searchTeamsByName(String teamName);


    void update(TeamUpdateDTO teamUpdateDTO );

    boolean joinTeam(TeamJoinDTO teamJoinDTO );

    void quitTeamById(Long id );

    void disbandTeamByCaptain(Long id );

    List<Team> searchJoinedTeams();

    List<Team> searchCreatedTeams();
}
