package com.kokomi.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.kokomi.maker.generator.file.DynamicFileGenerator;
import com.kokomi.maker.meta.Meta;
import com.kokomi.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public abstract class GenerateTemplate {
    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();
        //输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath=projectPath+ "/generated"+File.separator+meta.getName();
        doGenerate(meta,outputPath);
    }

    public void doGenerate(Meta meta,String outputPath) throws TemplateException, IOException, InterruptedException {
        //复制原始文件
        String sourceCopyPath = copySource(meta, outputPath);
        //代码生成
        generateCode(meta, outputPath);
        //构建jar包
        String jarPath = buildJar(outputPath, meta);
        //封装脚本
        String shellOutputPath = buildScript(outputPath,jarPath);
        //生成精简代码
        buildDist(outputPath, sourceCopyPath, shellOutputPath, jarPath);
    }

    protected String buildScript(String outputPath,String jarPath) {
        String shellOutputPath= outputPath +"/generator";
        ScriptGenerator.doGenerate(shellOutputPath,jarPath);
        return shellOutputPath;
    }

    protected String buildDist(String outputPath, String sourceCopyPath, String shellOutputPath, String jarPath) {
        String distOutputPath= outputPath +"-dist";
        //-拷贝jar包
        String targetAbsolutePath=distOutputPath+"/target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath= outputPath +File.separator+ jarPath;
        FileUtil.copy(jarAbsolutePath,targetAbsolutePath,true);
        //-拷贝脚本文件
        FileUtil.copy(shellOutputPath,distOutputPath,true);
        FileUtil.copy(shellOutputPath +".bat",distOutputPath,true);
        //-拷贝原模板文件
        FileUtil.copy(sourceCopyPath,distOutputPath,true);
        return distOutputPath;
    }

    protected String buildJar(String outputPath,Meta meta) throws IOException, InterruptedException {
        JarGenerator.doGenerate(outputPath);
        String jarName=String.format("%s-%s-jar-with-dependencies.jar",meta.getName(),meta.getVersion());
        String jarPath="target"+File.separator+jarName;
        return jarPath;
    }

    protected String buildZip(String outputPath){
        String zipPath=outputPath+".zip";
        ZipUtil.zip(outputPath,zipPath);
        return zipPath;
    }

    protected void generateCode(Meta meta, String outputPath) throws IOException, TemplateException {
        //读取resources目录
        String inputResourcePath = "";
        //Java包基础路径
        String outputBasePackage= StrUtil.join("/",StrUtil.split(meta.getBasePackage(),"."));
        String outputBasePackagePath= outputPath +"/src/main/java/"+outputBasePackage;
        String inputFilePath;
        String outputFilePath;
        //model.DataModel
        inputFilePath=inputResourcePath+"/templates/java/model/DataModel.java.ftl";
        outputFilePath=outputBasePackagePath+"/model/DataModel.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //cli.command.ConfigCommand
        inputFilePath=inputResourcePath+"/templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath=outputBasePackagePath+"/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //cli.command.GenerateCommand
        inputFilePath=inputResourcePath+"/templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath=outputBasePackagePath+"/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //cli.command.JsonGenerateCommand
        inputFilePath=inputResourcePath+"/templates/java/cli/command/JsonGenerateCommand.java.ftl";
        outputFilePath=outputBasePackagePath+"/cli/command/JsonGenerateCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //cli.command.ListCommand
        inputFilePath=inputResourcePath+"/templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath=outputBasePackagePath+"/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //cli.CommandExecutor
        inputFilePath=inputResourcePath+"/templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath=outputBasePackagePath+"/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //Main
        inputFilePath=inputResourcePath+"/templates/java/Main.java.ftl";
        outputFilePath=outputBasePackagePath+"/Main.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //generator.DynamicGenerator.java
        inputFilePath=inputResourcePath+"/templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath=outputBasePackagePath+"/generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //generator.MainGenerator.java
        inputFilePath=inputResourcePath+"/templates/java/generator/MainGenerator.java.ftl";
        outputFilePath=outputBasePackagePath+"/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //generator.StaticGenerator.java
        inputFilePath=inputResourcePath+"/templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath=outputBasePackagePath+"/generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        //pom.xml
        inputFilePath=inputResourcePath+"/templates/pom.xml.ftl";
        outputFilePath= outputPath +"/pom.xml";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
        ////README.md
        //inputFilePath=inputResourcePath+"/templates/README.md.ftl";
        //outputFilePath= outputPath +"/README.md";
        //DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
    }

    private static String copySource(Meta meta, String outputPath) {
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyPath= outputPath +"/.source";
        FileUtil.copy(sourceRootPath,sourceCopyPath,false);
        return sourceCopyPath;
    }
}
