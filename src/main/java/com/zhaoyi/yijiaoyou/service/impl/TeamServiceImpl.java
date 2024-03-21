package com.zhaoyi.yijiaoyou.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyi.yijiaoyou.exception.BusinessException;
import com.zhaoyi.yijiaoyou.common.ErrorCode;
import com.zhaoyi.yijiaoyou.common.TeamStatus;
import com.zhaoyi.yijiaoyou.mapper.UserTeamMapper;
import com.zhaoyi.yijiaoyou.model.entity.Team;
import com.zhaoyi.yijiaoyou.model.entity.User;
import com.zhaoyi.yijiaoyou.model.entity.UserTeam;
import com.zhaoyi.yijiaoyou.model.vo.UserTeamListVo;
import com.zhaoyi.yijiaoyou.model.dto.team.TeamJoinDTO;
import com.zhaoyi.yijiaoyou.model.dto.team.TeamUpdateDTO;
import com.zhaoyi.yijiaoyou.service.TeamService;
import com.zhaoyi.yijiaoyou.mapper.TeamMapper;
import com.zhaoyi.yijiaoyou.service.UserService;
import com.zhaoyi.yijiaoyou.service.UserTeamService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author PC
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-01-18 17:11:33
 */
@Service
@Transactional
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    private final String YANZHI = "zhaoyi";

    private final String LOCK = "YiJiaoYou:team:";

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserTeamMapper userTeamMapper;
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;

    /**
     * 创建队伍
     *
     * @param team
     */
    @Override
    public void create(@RequestBody Team team) {
        //获取当前登录用户
        User loginUser = userService.getLoginUser();
        //对传入数据进行校验
        Integer maxNum = team.getMaxNum();
        if (maxNum == null || maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        String name = team.getName();
        if (name.length() > 20) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        String description = team.getDescription();
        if (description.length() > 512) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        Integer status = team.getStatus();
        if (status < 0 || status > 2) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        if (status == TeamStatus.ENCRYPT.getNum()) {
            String password = team.getPassword();
            if (password == null || status < 0 || status > 2) {
                throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
            }
        }
        //对队伍过期时间进行校验
        Date expireTime = team.getExpireTime();
        if (expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        Long userId = loginUser.getId();

        //统计该用户已经创建的队伍数量
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        long count = userTeamService.count(queryWrapper);
        //五个为可创建上限
        if (count >= 5) {
            throw new BusinessException(ErrorCode.THE_NUMBER_OF_TEAMS_HAS_REACHED_THE_UPPER_LIMIT);
        }
        //插入信息到队伍表
        team.setUserId(userId);
        teamMapper.insert(team);
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        //插入信息到用户-队伍关系表
        userTeamService.save(userTeam);
    }

    /**
     * 通过队伍名称进行搜索
     *
     * @param teamName
     * @return
     */
    @Override
    public List<UserTeamListVo> searchTeamsByName(String teamName) {
        /**
         * 判断是否为管理员  管理员可以看到私密队伍的密码 还有私密和加密
         * 普通用户可以看到公开的队伍
         */

        //查找队伍名称符合要求的队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", teamName);
        List<Team> teams = new ArrayList<>();
        List<Team> teamList = teamMapper.selectList(queryWrapper);
        for (Team team : teamList) {
            if (team.getExpireTime().before(new Date())) {
                team.setIsDelete(1);
            } else {
                teams.add(team);
            }
        }
        //没有符合要求的队伍  返回一个共列表
        if (teams.size() == 0) {
            return new ArrayList<>();
        }
        if (!userService.isAdmin()) {
            List<Team> teamPublic = new ArrayList<>();
            for (Team team : teams) {
                if (team.getStatus() != 0) {
                    teamPublic.add(team);
                }
            }
            teams = teamPublic;
        }
        //返回值是自己封装的类
        List<UserTeamListVo> userTeamListVos = new ArrayList<>();
        for (Team team : teams) {
            UserTeamListVo userTeamListVo = new UserTeamListVo();
            //进行属性复制
            BeanUtil.copyProperties(team, userTeamListVo);
            List<User> userList = new ArrayList<>();
            Long id = team.getId();
            QueryWrapper<UserTeam> teamQueryWrapper = new QueryWrapper<>();
            teamQueryWrapper.eq("team_id", id);
            List<UserTeam> userTeams = userTeamService.list(teamQueryWrapper);
            for (UserTeam userTeam : userTeams) {
                Long userId = userTeam.getUserId();
                User user = userService.getById(userId);
                userList.add(user);
            }
            userTeamListVo.setUserList(userList);
            userTeamListVos.add(userTeamListVo);
        }
        return userTeamListVos;
    }

    /**
     * 队伍信息更新
     *
     * @param teamUpdateDTO
     */
    @Override
    public void update(TeamUpdateDTO teamUpdateDTO) {
        User loginUser = userService.getLoginUser();
        //获取传入的消息
        Long id = teamUpdateDTO.getId();
        String name = teamUpdateDTO.getName();
        String description = teamUpdateDTO.getDescription();
        Integer maxNum = teamUpdateDTO.getMaxNum();
        Date expireTime = teamUpdateDTO.getExpireTime();
        Integer status = teamUpdateDTO.getStatus();
        String password = teamUpdateDTO.getPassword();

        //对传入数据进行校验
        if (maxNum == null || maxNum <= 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        if (name.length() > 20) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);

        }
        if (description.length() > 512) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        if (status < 0 || status > 2) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        if (status == TeamStatus.ENCRYPT.getNum()) {
            if (password == null || status > 0 || status <= 32) {
                throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
            }
        }
        //对队伍过期时间进行校验
        if (expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        if (id == null) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }

        //进行身份校验和队伍状态的判别
        Team team = this.getById(id);
        boolean admin = userService.isAdmin();
        boolean tempId = team.getUserId().equals(loginUser.getId());
        Long userId = team.getUserId();
        Long id1 = loginUser.getId();
        if (userService.isAdmin() || team.getUserId() == loginUser.getId()) {
            team.setName(name);
            team.setDescription(description);
            team.setMaxNum(maxNum);
            team.setExpireTime(expireTime);
            team.setStatus(status);
            if (status == 2) {
                team.setPassword(password);
            } else {
                team.setPassword("");
            }
            this.updateById(team);
        } else {
            throw new BusinessException(ErrorCode.NO_PERMISSIONS);
        }
    }

    /**
     * 加入队伍
     *
     * @param teamJoinDTO
     * @return
     */
    @Override
    public boolean joinTeam(TeamJoinDTO teamJoinDTO) {
        //获取到登录用户
        User loginUser = userService.getLoginUser();
        //在用户-队伍关系表中查找登录用户查当前用户已经加入了几个队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        //上锁 编写分布式锁(应该进行队伍锁和用户锁,但只进行了队伍锁)

        //给要加入队伍上锁
        RLock lock = redissonClient.getLock(LOCK + teamJoinDTO.getId());

        while (true) {
            try {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    //登录用户已经加入的队伍
                    List<UserTeam> teams = userTeamService.list(queryWrapper);
                    //已经加入队伍的数量
                    long count = userTeamService.count(queryWrapper);
                    queryWrapper.clear();
                    //如果加入的队伍大于等于5 就不能再加入了
                    if (count >= 5) {
                        throw new BusinessException(ErrorCode.THE_NUMBER_OF_TEAMS_HAS_REACHED_THE_UPPER_LIMIT);
                    }

                    //判断队伍是否存在
                    Team team = this.getById(teamJoinDTO.getId());
                    if (team == null) {
                        throw new BusinessException(ErrorCode.THE_TEAM_DOES_NOT_EXIST);
                    }
                    //判断队伍是否已经满员
                    queryWrapper.eq("team_id", teamJoinDTO.getId());
                    List<UserTeam> list = userTeamService.list(queryWrapper);
                    if (list.size() >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.THE_TEAM_IS_FULL);
                    }
                    //判断是否已经过期
                    if (team.getExpireTime().before(new Date())) {
                        throw new BusinessException(ErrorCode.THE_TEAM_DOES_NOT_EXIST);
                    }

                    //不能加入自己的队伍 不能重复加入已经加入的队伍(幂等性)
                    if (team.getUserId() == loginUser.getId()) {
                        throw new BusinessException(ErrorCode.WRONG_OPERATION);
                    }
                    //不能加入已经加入的队伍
                    for (UserTeam userTeam : teams) {
                        if (userTeam.getTeamId() == teamJoinDTO.getId()) {
                            throw new BusinessException(ErrorCode.WRONG_OPERATION);
                        }
                    }
                    //禁止加入私密队伍
                    if (team.getStatus() == 1) {
                        throw new BusinessException(ErrorCode.NO_PERMISSIONS);
                    }
                    //如果加密进行密码校验
                    if (team.getStatus() == 2) {
                        if (!team.getPassword().equals(teamJoinDTO.getPassword())) {
                            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
                        }
                    }
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(loginUser.getId());
                    userTeam.setTeamId(team.getId());
                    userTeam.setJoinTime(new Date());
                    userTeamService.save(userTeam);
                }
            } catch (InterruptedException e) {//必须对lock进行异常处理  出现异常直接返回
                return false;                 //方法有返回结果了   就不会执行finally语句了
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();               // 进行锁的释放
                }
            }
        }
    }


    /**
     * 根据id退出队伍
     *
     * @param id
     */
    @Override
    public void quitTeamById(Long id) {
        //判断登录了没
        User loginUser = userService.getLoginUser();

        //判断要删除的队伍是否存在
        Team team = this.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
        }
        //在用户-队伍关系表中查找已经加入该队伍的用户 排查登录用户是否在该队伍中
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", id);
        List<UserTeam> users = userTeamService.list(userTeamQueryWrapper);
        for (UserTeam userTeam : users) {
            if (userTeam.getUserId().equals(loginUser.getId())) {
                //如果队伍只剩一个人 自己再退出 那这个队伍就没了
                if (users.size() == 1) {
                    this.removeById(id);
                    userTeamService.remove(userTeamQueryWrapper);
                    return;
                }
                //不止一个人 判断是不是队长要退出
                if (team.getUserId().equals(loginUser.getId())) {
                    //取加入时间第二的用户 进行房主更替
                    //last 中可以写sql语句  起到拼接的作用
                    userTeamQueryWrapper.last("order by id asc limit 2");
                    List<UserTeam> list = userTeamService.list(userTeamQueryWrapper);
                    team.setUserId(list.get(1).getUserId());
                    this.updateById(team);
                } else {
                    userTeamQueryWrapper.clear();
                    userTeamQueryWrapper.eq("user_id", loginUser.getId());
                    userTeamQueryWrapper.eq("team_id", id);
                    userTeamService.remove(userTeamQueryWrapper);
                }
            } else {
                throw new BusinessException(ErrorCode.INFORMATION_IS_NOT_STANDARDIZED);
            }
        }
    }

    /**
     * 队长解散队伍
     *
     * @param id
     */
    @Override
    public void disbandTeamByCaptain(Long id) {
        User loginUser = userService.getLoginUser();

        //判断队伍是否存在
        Team team = this.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.THE_TEAM_DOES_NOT_EXIST);
        }
        //判断登录用户是否为队长
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_CAPTAIN);
        }
        //查询与这个队伍有关系的用户  全部删除
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("team_id", id);
        userTeamService.remove(userTeamQueryWrapper);
        //删除队伍
        this.removeById(id);
    }

    /**
     * 获取当前用户已经加入的队伍
     *
     * @return
     */
    @Override
    public List<Team> searchJoinedTeams() {
        User loginUser = userService.getLoginUser();
        //根据id获取该用户已经加入的队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("user_id", loginUser.getId());
        List<UserTeam> list = userTeamService.list(userTeamQueryWrapper);
        List<Team> teamList = new ArrayList<>();
        for (UserTeam userTeam : list) {
            Long teamId = userTeam.getTeamId();
            Team team = this.getById(teamId);
            if (team.getExpireTime().before(new Date())) {
                team.setIsDelete(1);
            }else {
                teamList.add(team);
            }
        }
        return teamList;
    }

    /**
     * 获取当前用户创建的队伍
     *
     * @return
     */
    @Override
    public List<Team> searchCreatedTeams() {
        User loginUser = userService.getLoginUser();

//        搜索当前由当前用户创建的队伍
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("user_id", loginUser.getId());
        List<Team> list = this.list(teamQueryWrapper);
        List<Team> teams = new ArrayList<>();
        for (Team team : list) {
            if (team.getExpireTime().before(new Date(System.currentTimeMillis()))){
                team.setIsDelete(1);
            }else {
                teams.add(team);
            }
        }
        return teams;
    }
}




