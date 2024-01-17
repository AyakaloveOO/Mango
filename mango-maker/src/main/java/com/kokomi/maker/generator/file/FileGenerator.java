package com.kokomi.maker.generator.file;

import com.kokomi.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 动静结合生成文件
 */
public class FileGenerator {
    /**
     * 动静结合生成文件
     * @param model
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerator(Object model) throws TemplateException, IOException {
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        System.out.println(parentFile);
        //生成静态文件
        String inputPath=new File(projectPath,"samples/acm-template").getPath();
        String outputPath=new File(projectPath).getPath();
        StaticFileGenerator.copyFilesByHutool(inputPath,outputPath);
        //生成动态文件
        String projectFile = projectPath+File.separator+"mango-maker";
        String inputFile = new File(projectFile,"src/main/resources/templates/MainTemplate.java.ftl").getPath();
        String outputFile=projectPath+File.separator+"acm-template/src/com/kokomi/acm/MainTemplate.java";
        DynamicFileGenerator.doGenerator(inputFile,outputFile,model);
    }
}
