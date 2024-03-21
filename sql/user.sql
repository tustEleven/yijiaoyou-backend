create table user
(
    id            bigint auto_increment comment 'id'
        primary key,
    username      varchar(256)                       null comment '用户昵称',
    user_account  varchar(256)                       null comment '账号',
    avatar_url    varchar(1024)                      null comment '用户头像',
    gender        tinyint                            null comment '性别',
    user_password varchar(256)                       not null comment '密码',
    phone         varchar(128)                       null comment '电话',
    email         varchar(512)                       null comment '邮箱',
    user_status   int      default 0                 not null comment '状态 0 - 正常',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete     tinyint  default 0                 not null comment '是否删除   0  存在  1  删除',
    user_role     tinyint  default 0                 not null comment '普通用户 0   管理员 1 ',
    user_tags     varchar(1024)                      null comment '标签列表'
)
    comment '用户';

alter table user add column tags varchar(1024) null comment '标签列表';