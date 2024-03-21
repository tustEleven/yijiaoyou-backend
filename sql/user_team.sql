create table user_team
(
    id           bigint auto_increment comment 'id'
        primary key,
    user_id            bigint comment '用户id',
    team_id            bigint comment '队伍id',
    join_time datetime  null comment '加入时间',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_delete     tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';