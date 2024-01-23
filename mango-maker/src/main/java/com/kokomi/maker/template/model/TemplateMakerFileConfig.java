package com.kokomi.maker.template.model;

import lombok.Data;

import java.util.List;

@Data
public class TemplateMakerFileConfig {
    private List<FileInfoConfig> files;
    private FileGroupConfig fileGroupConfig;
    @Data
    public static class FileInfoConfig{
        private String path;
        private List<FileFilterConfig> filterConfigList;
    }
    @Data
    public static class FileGroupConfig{
        private String groupKey;
        private String groupName;
        private String condition;
    }
}
