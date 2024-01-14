package com.kokomi.maker.meta.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件生成类型枚举
 */
@AllArgsConstructor
@Getter
public enum FileGenerateTypeEnum {
    DYNAMIC("动态","dynamic"),
    STATIC("静态","static");
    private final String text;
    private final String value;
}
