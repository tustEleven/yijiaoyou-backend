package com.zhaoyi.yijiaoyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhaoyi.yijiaoyou.model.entity.Tag;
import com.zhaoyi.yijiaoyou.service.TagService;
import com.zhaoyi.yijiaoyou.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author PC
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2024-01-17 09:54:20
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




