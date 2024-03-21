package com.zhaoyi.yijiaoyou.common;

/**
 * 枚举错误码
 * @author zhaoyi
 */
public enum ErrorCode {

    /**
     * 枚举要写在最上面
     */
    INCOMPLETE_INFORMATION("信息不完整"),
    USER_EXIST("用户已经存在"),
    THE_INCOMING_IS_EMPTY("传入消息为空"),
    THE_TEAM_DOES_NOT_EXIST("队伍不存在"),
    THE_NUMBER_OF_TEAMS_HAS_REACHED_THE_UPPER_LIMIT("队伍数量已经到达上线"),
    THERE_IS_NO_TEAM_THAT_MEETS_THE_REQUIREMENTS("没有符合要求的队伍"),
    THE_TEAM_IS_FULL("队伍已经满员"),
    WRONG_OPERATION("错误操作"),
    LOGIN_VERIFICATION_FAILED("登录认证失败"),
    FILE_UPLOAD_FAILED("文件上传失败"),
    NOT_CAPTAIN("不是队长,无法删除"),

    INFORMATION_IS_NOT_STANDARDIZED("信息不规范"),
    PASSWORD_INCONSISTENCY("密码不一致"),
    PASSWORD_ERROR("密码错误"),

    NO_UPDATE_INFORMATION("没有更新的信息"),
    NO_PERMISSIONS("无权限"),
    NOT_LOGGED_IN("未登录");
    private String description;


    ErrorCode(String description){
        this.description=description;
    }

    public String getDescription() {
        return description;
    }
}
