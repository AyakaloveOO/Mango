package com.kokomi.web.model.dto.generator;

import com.kokomi.web.meta.Meta;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 创建请求
 */
@Data
public class GeneratorUseRequest implements Serializable {
    /**
     * 生成器id
     */
    private Long id;
    /**
     * 数据模型
     */
    private Map<String,Object> dataModel;
    private static final long serialVersionUID = 1L;
}