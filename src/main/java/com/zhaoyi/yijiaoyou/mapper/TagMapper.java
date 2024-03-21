package com.zhaoyi.yijiaoyou.mapper;

import com.zhaoyi.yijiaoyou.model.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author PC
* @description 针对表【tag(标签)】的数据库操作Mapper
* @createDate 2024-01-17 09:54:20
* @Entity com.zhaoyi.yijiaoyou.model.domain.Tag
*/
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

}




