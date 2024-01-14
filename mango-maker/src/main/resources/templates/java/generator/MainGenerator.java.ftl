package ${basePackage}.generator;

import ${basePackage}.generator.DynamicGenerator;
import ${basePackage}.generator.StaticGenerator;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 动静结合生成文件
 */
public class MainGenerator {
    /**
     * 动静结合生成文件
     * @param model
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerator(Object model) throws TemplateException, IOException {
        String inputRootPath="${fileConfig.inputRootPath}";
        String outputRootPath="${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
<#list fileConfig.files as fileInfo>
        inputPath=new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
        outputPath=new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
    <#if fileInfo.generateType=="static">
        StaticGenerator.copyFilesByHutool(inputPath,outputPath);
    <#else >
        DynamicGenerator.doGenerator(inputPath,outputPath,model);
    </#if>
</#list>
    }
}