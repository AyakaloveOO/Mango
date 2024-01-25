package com.kokomi.maker.template.model;

import lombok.Data;

@Data
public class TemplateMakerOutputConfig {
    //从未分组文件中移除组内相同文件
    private boolean removeGroupFilesFromRoot=true;
}
