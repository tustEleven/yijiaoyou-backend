package com.zhaoyi.yijiaoyou.controller;

import com.zhaoyi.yijiaoyou.common.BaseResponse;
import com.zhaoyi.yijiaoyou.common.ErrorCode;
import com.zhaoyi.yijiaoyou.exception.BusinessException;
import com.zhaoyi.yijiaoyou.model.dto.team.TeamJoinDTO;
import com.zhaoyi.yijiaoyou.model.dto.team.TeamUpdateDTO;
import com.zhaoyi.yijiaoyou.model.entity.Team;
import com.zhaoyi.yijiaoyou.model.vo.UserTeamListVo;
import com.zhaoyi.yijiaoyou.service.TeamService;
import com.zhaoyi.yijiaoyou.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 队伍Controller接口
 *
 * @author zhaoyi
 */
@RestController
@RequestMapping("/yijiaoyou/team")
@Slf4j
@Api(tags = "队伍管理")
public class TeamController {
    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    /**
     * 创建队伍
     *
     * @param team
     * @return
     */
    @PostMapping("/create")
    @ApiOperation("创建队伍")
    public BaseResponse createTeam(@RequestBody Team team) {
        if (team == null) {
            throw new BusinessException(ErrorCode.THE_INCOMING_IS_EMPTY);
        }
        teamService.create(team);
        log.info("创建队伍成功");
        return BaseResponse.success();
    }

    /**
     * 根据队伍名称查询队伍的信息
     *
     * @param teamName
     * @return
     */
    @GetMapping("/search")
    @ApiOperation("查询队伍信息")
    public BaseResponse<List<UserTeamListVo>> searchTeamsByName(String teamName) {
        if (teamName == null) {
            throw new BusinessException(ErrorCode.THE_INCOMING_IS_EMPTY);
        }
        List<UserTeamListVo> userTeamListVos = teamService.searchTeamsByName(teamName);
        log.info("查询队伍信息成功");
        return BaseResponse.success(userTeamListVos);
    }

    /**
     * 更新队伍信息
     *
     * @param teamUpdateDTO
     * @return
     */
    @PostMapping("/update")
    @ApiOperation("更改队伍信息")
    public BaseResponse updateTeam(@RequestBody TeamUpdateDTO teamUpdateDTO) {
        if (teamUpdateDTO == null) {
            throw new BusinessException(ErrorCode.THE_INCOMING_IS_EMPTY);
        }
        teamService.update(teamUpdateDTO);
        log.info("更改队伍信息成功");
        return BaseResponse.success();
    }

    /**
     * 用户加入队伍
     *
     * @param teamJoinDTO
     * @return
     */
    @PostMapping("/join")
    @ApiOperation("用户加入队伍")
    public BaseResponse joinTeam(@RequestBody TeamJoinDTO teamJoinDTO) {
        if (teamJoinDTO.getId() <= 0) {
            throw new BusinessException(ErrorCode.THE_TEAM_DOES_NOT_EXIST);
        }
        boolean result = teamService.joinTeam(teamJoinDTO);
        if (result==false){
            return BaseResponse.error("用户加入失败");
        }
        log.info("用户加入队伍成功");
        return BaseResponse.success();
    }

    /**
     * 用户退出队伍
     *
     * @param id
     * @return
     */
    @PostMapping("/quit")
    @ApiOperation("用户退出队伍")
    public BaseResponse quitTeamById(Long id) {
        if (id <= 0 || id == null) {
            throw new BusinessException(ErrorCode.THE_TEAM_DOES_NOT_EXIST);
        }
        teamService.quitTeamById(id);
        log.info("用户退出队伍成功");
        return BaseResponse.success();
    }

    /**
     * 队长解散队伍
     *
     * @param id
     * @return
     */
    @PostMapping("/disband")
    @ApiOperation("队长解散队伍")
    public BaseResponse disbandTeamByCaptain(Long id) {
        if (id <= 0 || id == null) {
            throw new BusinessException(ErrorCode.THE_TEAM_DOES_NOT_EXIST);
        }
        teamService.disbandTeamByCaptain(id);
        log.info("队长解散队伍成功");
        return BaseResponse.success();
    }

    /**
     * 获取当前用户已经加入的队伍
     *
     * @return
     */
    @GetMapping("/joined")
    @ApiOperation("获取当前用户已经加入的队伍")
    public BaseResponse<List<Team>> searchJoinedTeams() {
        List<Team> teams = teamService.searchJoinedTeams();
        log.info("获取当前用户已经加入的队伍成功");
        return BaseResponse.success(teams);
    }

    /**
     * 获取当前用户创建的队伍
     *
     * @return
     */
    @GetMapping("/created")
    @ApiOperation("获取当前用户创建的队伍")
    public BaseResponse<List<Team>> searchCreatedTeams( ) {
        List<Team> teams = teamService.searchCreatedTeams();
        log.info("获取当前用户创建的队伍成功");
        return BaseResponse.success(teams);
    }

}
