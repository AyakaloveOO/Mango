package com.kokomi.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.kokomi.maker.generator.file.DynamicFileGenerator;
import com.kokomi.maker.meta.Meta;
import com.kokomi.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 执行生成代码
 */
public class MainGenerator extends GenerateTemplate{
    @Override
    protected void buildDist(String outputPath, String sourceCopyPath, String shellOutputPath, String jarPath) {
        System.out.println("没有精简");
    }
}
