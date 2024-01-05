package com.kokomi.model;

import lombok.Data;

/**
 * 动态模板配置
 */
@Data
public class MainTemplateConfig {
    /**
     * 作者
     */
    private String author="kokomi";
    /**
     * 输出信息
     */
    private String outputText="求和结果";
    /**
     * 是否循环
     */
    private boolean loop;
}
