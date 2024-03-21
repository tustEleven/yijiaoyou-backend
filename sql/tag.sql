create table tag
(
    id bigint auto_increment comment 'id' primary key ,
    tag_name varchar(256) null comment '标签名称',
    user_id bigint null comment '用户 id',
    parent_id  bigint null comment '父标签 id',
    is_parent tinyint null comment '0-不是  1-父标签',
    create_time datetime default current_timestamp null comment '创建时间',
    update_time datetime default current_timestamp null on update current_timestamp,
    is_delete tinyint default 0 not null comment '是否删除'
) comment '标签';

# 现在表中可以为null的列有很多，后期如果无影响考虑把可以的子段设置为not null