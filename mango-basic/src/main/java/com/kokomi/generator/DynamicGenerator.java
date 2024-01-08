package com.kokomi.generator;

import com.kokomi.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 动态文件生成
 */
public class DynamicGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir")+File.separator+"mango-basic";
        String inputPath = new File(projectPath,"src/main/resources/templates/MainTemplate.java.ftl").getPath();
        String outputPath = new File(projectPath,"MainTemplate.java").getPath();
        // 创建数据模型
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("kokomi");
        mainTemplateConfig.setOutputText("afafsafa");
        mainTemplateConfig.setLoop(true);
        doGenerator(inputPath,outputPath,mainTemplateConfig);
    }

    /**
     * 生成文件
     * @param inputPath
     * @param outputPath
     * @param model
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerator(String inputPath,String outputPath,Object model) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        File parentFile = new File(inputPath).getParentFile();

        // 指定模板文件所在的路径
        configuration.setDirectoryForTemplateLoading(parentFile);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        // 创建模板对象，加载指定模板
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName);

        // 生成
        //Writer out = new FileWriter(outputPath);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)), StandardCharsets.UTF_8));
        template.process(model, out);

        // 生成文件后别忘了关闭哦
        out.close();
    }
}
