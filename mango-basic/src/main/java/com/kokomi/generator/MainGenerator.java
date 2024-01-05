package com.kokomi.generator;

import com.kokomi.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 动静结合生成文件
 */
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("kokomi");
        mainTemplateConfig.setOutputText("求和结果是");
        mainTemplateConfig.setLoop(false);
        doGenerator(mainTemplateConfig);
    }

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
        String inputPath=new File("samples/acm-template").getPath();
        String outputPath=new File(projectPath).getPath();
        StaticGenerator.copyFilesByRecursive(inputPath,outputPath);
        //生成动态文件
        String projectFile = projectPath+File.separator+"mango-basic";
        String inputFile = new File(projectFile,"src/main/resources/templates/MainTemplate.java.ftl").getPath();
        String outputFile=projectPath+File.separator+"acm-template/src/com/kokomi/acm/MainTemplate.java";
        DynamicGenerator.doGenerator(inputFile,outputFile,model);
    }
}
