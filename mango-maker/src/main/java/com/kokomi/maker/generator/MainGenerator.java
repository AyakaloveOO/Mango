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
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();
        System.out.println(meta);
        //输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath=projectPath+ File.separator+"generated"+File.separator+meta.getName();
        if(FileUtil.exist(outputPath)){
            FileUtil.mkdir(outputPath);
        }
        //复制原始文件
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyPath=outputPath+File.separator+".source";
        FileUtil.copy(sourceRootPath,sourceCopyPath,false);
        //读取resources目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();
        //Java包基础路径
        String outputBasePackage= StrUtil.join("/",StrUtil.split(meta.getBasePackage(),"."));
        String outputBasePackagePath=outputPath+File.separator+"src/main/java/"+outputBasePackage;
        String inputFilePath;
        String outputFilePath;
        //model.DataModel
        inputFilePath=inputResourcePath+File.separator+"templates/java/model/DataModel.java.ftl";
        outputFilePath=outputBasePackagePath+File.separator+"model/DataModel.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //cli.command.ConfigCommand
        inputFilePath=inputResourcePath+File.separator+"templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath=outputBasePackagePath+File.separator+"cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //cli.command.GenerateCommand
        inputFilePath=inputResourcePath+File.separator+"templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath=outputBasePackagePath+File.separator+"cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //cli.command.ListCommand
        inputFilePath=inputResourcePath+File.separator+"templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath=outputBasePackagePath+File.separator+"cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //cli.CommandExecutor
        inputFilePath=inputResourcePath+File.separator+"templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath=outputBasePackagePath+File.separator+"cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //Main
        inputFilePath=inputResourcePath+File.separator+"templates/java/Main.java.ftl";
        outputFilePath=outputBasePackagePath+File.separator+"Main.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //generator.DynamicGenerator.java
        inputFilePath=inputResourcePath+File.separator+"templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath=outputBasePackagePath+File.separator+"generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //generator.MainGenerator.java
        inputFilePath=inputResourcePath+File.separator+"templates/java/generator/MainGenerator.java.ftl";
        outputFilePath=outputBasePackagePath+File.separator+"generator/MainGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //generator.StaticGenerator.java
        inputFilePath=inputResourcePath+File.separator+"templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath=outputBasePackagePath+File.separator+"generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //pom.xml
        inputFilePath=inputResourcePath+File.separator+"templates/pom.xml.ftl";
        outputFilePath=outputPath+File.separator+"pom.xml";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //README.md
        inputFilePath=inputResourcePath+File.separator+"templates/README.md.ftl";
        outputFilePath=outputPath+File.separator+"README.md";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        //构建jar包
        JarGenerator.doGenerate(outputPath);
        //封装脚本
        String shellOutputPath=outputPath+File.separator+"generator";
        String jarName=String.format("%s-%s-jar-with-dependencies.jar",meta.getName(),meta.getVersion());
        String jarPath="target"+File.separator+jarName;
        ScriptGenerator.doGenerate(shellOutputPath,jarPath);
        //生成精简代码
        String distOutputPath=outputPath+"-dist";
        //-拷贝jar包
        String targetAbsolutePath=distOutputPath+File.separator+"target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath=outputPath+File.separator+jarPath;
        FileUtil.copy(jarAbsolutePath,targetAbsolutePath,true);
        //-拷贝脚本文件
        FileUtil.copy(shellOutputPath,distOutputPath,true);
        FileUtil.copy(shellOutputPath+".bat",distOutputPath,true);
        //-拷贝原模板文件
        FileUtil.copy(sourceCopyPath,distOutputPath,true);
    }
}
