package com.kokomi.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;

/**
 * 缓存请求
 */
@Data
public class GeneratorCacheRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}