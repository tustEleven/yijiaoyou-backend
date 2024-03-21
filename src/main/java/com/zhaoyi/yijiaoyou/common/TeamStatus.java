package com.zhaoyi.yijiaoyou.common;

/**
 * 队伍状态枚举类
 * @author zhaoyi
 */
public enum TeamStatus {

    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    ENCRYPT(2,"加密");

    private int num;

    private String description;

    TeamStatus(){

    }
    TeamStatus(int num,String description){
        this.description=description;
        this.num=num;
    }

    /**
     * 获取
     * @return num
     */
    public int getNum() {
        return num;
    }

    /**
     * 设置
     * @param num
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * 获取
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "TeamStatus{PUBLIC = " + PUBLIC + ", PRIVATE = " + PRIVATE + ", ENCRYPT = " + ENCRYPT + ", num = " + num + ", description = " + description + "}";
    }
}

