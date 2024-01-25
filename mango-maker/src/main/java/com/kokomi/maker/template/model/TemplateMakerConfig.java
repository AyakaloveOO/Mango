package com.kokomi.maker.template.model;

import com.kokomi.maker.meta.Meta;
import lombok.Data;

@Data
public class TemplateMakerConfig {
    private Meta meta=new Meta();
    private String originProjectPath;
    private TemplateMakerFileConfig fileConfig=new TemplateMakerFileConfig();
    private TemplateMakerModelConfig modelConfig=new TemplateMakerModelConfig();
    private TemplateMakerOutputConfig outputConfig=new TemplateMakerOutputConfig();
    private Long id;
}
