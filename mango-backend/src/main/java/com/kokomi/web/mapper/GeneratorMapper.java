package com.kokomi.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kokomi.web.model.entity.Generator;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 代码生成器数据库操作
 */
public interface GeneratorMapper extends BaseMapper<Generator> {
    @Select("select id,distPath from generator where isDelete=1")
    List<Generator> listDeletedGenerator();
}




