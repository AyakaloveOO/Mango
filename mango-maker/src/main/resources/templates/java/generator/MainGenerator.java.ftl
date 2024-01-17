package ${basePackage}.generator;

import ${basePackage}.generator.DynamicGenerator;
import ${basePackage}.generator.StaticGenerator;
import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile intent fileInfo>
${intent}inputPath=new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
${intent}outputPath=new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType=="static">
${intent}StaticGenerator.copyFilesByHutool(inputPath,outputPath);
<#else >
${intent}DynamicGenerator.doGenerator(inputPath,outputPath,model);
</#if>
</#macro>

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
    public static void doGenerator(DataModel model) throws TemplateException, IOException {
        String inputRootPath="${fileConfig.inputRootPath}";
        String outputRootPath="${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

<#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
        <#list modelInfo.models as subModelInfo>
        ${subModelInfo.type} ${subModelInfo.fieldName}=model.${modelInfo.groupKey}.${subModelInfo.fieldName};
        </#list>
    <#else >
        ${modelInfo.type} ${modelInfo.fieldName}=model.${modelInfo.fieldName};
    </#if>
</#list>

<#list fileConfig.files as fileInfo>
    <#if fileInfo.groupKey??>
        <#if fileInfo.condition??>
        if(${fileInfo.condition}){
            <#list fileInfo.files as subFileInfo>
            <@generateFile fileInfo=subFileInfo intent="            "/>

            </#list>
        }
        <#else >
        <#list fileInfo.files as subFileInfo>
        <@generateFile fileInfo=subFileInfo intent="        "/>

        </#list>
        </#if>
    <#else >
        <#if fileInfo.condition??>
        if(${fileInfo.condition}){
            <@generateFile fileInfo=fileInfo intent="        "/>
        }
        <#else >
            <@generateFile fileInfo=fileInfo intent="        "/>
        </#if>
    </#if>

</#list>
    }
}