package com.kokomi.maker.template.model;

import com.kokomi.maker.meta.Meta;
import lombok.Data;

import java.util.List;
@Data
public class TemplateMakerModelConfig {
    private List<ModelInfoConfig> models;
    private ModelGroupConfig modelGroupConfig;
    @Data
    public static class ModelInfoConfig{
        private String fieldName;
        private String type;
        private String description;
        private Object defaultValue;
        private String abbr;
        private String replaceText;
    }
    @Data
    public static class ModelGroupConfig{
        private String groupKey;
        private String groupName;
        private String condition;
    }
}
