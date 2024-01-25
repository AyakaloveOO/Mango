package com.kokomi.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.kokomi.maker.meta.Meta;
import com.kokomi.maker.meta.enums.FileGenerateTypeEnum;
import com.kokomi.maker.meta.enums.FileTypeEnum;
import com.kokomi.maker.template.enums.FileFilterRangeEnum;
import com.kokomi.maker.template.enums.FileFilterRuleEnum;
import com.kokomi.maker.template.model.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateMaker {
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig){
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig templateMakerOutputConfig = templateMakerConfig.getOutputConfig();
        Long id = templateMakerConfig.getId();
        return makeTemplate(meta,originProjectPath,templateMakerFileConfig,templateMakerModelConfig,templateMakerOutputConfig,id);
    }
        /**
         * 制作模板
         * @param newMeta
         * @param originProjectPath
         * @param templateMakerFileConfig
         * @param templateMakerModelConfig
         * @param id
         * @return
         */
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, TemplateMakerOutputConfig templateMakerOutputConfig, Long id){
        if(id==null){
            id=IdUtil.getSnowflakeNextId();
        }

        //复制目录
        String projectPath=System.getProperty("user.dir");
        String tempDirPath=projectPath+File.separator+".temp";
        String templatePath=tempDirPath+File.separator+id;

        //是否为首次制作模板
        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath,templatePath,true);
        }
        //1、输入信息
        //- 文件信息
        String sourceRootPath=FileUtil.loopFiles(new File(templatePath),1,null).stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();
        //路径转义
        sourceRootPath=sourceRootPath.replaceAll("\\\\","/");
        //制作文件模板
        List<Meta.FileConfigDTO.FilesDTO> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);
        //处理模型信息
        List<Meta.ModelConfigDTO.ModelsDTO> newModelInfoList = getModelInfoList(templateMakerModelConfig);

        //3、生成配置文件
        String metaOutputPath=templatePath+File.separator+"meta.json";

        if(FileUtil.exist(metaOutputPath)){
            //在已有文件中追加信息
            Meta oldMeta=JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath),Meta.class);
            BeanUtil.copyProperties(newMeta,oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta=oldMeta;

            List<Meta.FileConfigDTO.FilesDTO> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfigDTO.ModelsDTO> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);
            //去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        }else {
            //- 构造配置参数

            Meta.FileConfigDTO fileConfig=new Meta.FileConfigDTO();
            newMeta.setFileConfig(fileConfig);

            fileConfig.setSourceRootPath(sourceRootPath);

            List<Meta.FileConfigDTO.FilesDTO> fileInfoList=new ArrayList<>();
            fileConfig.setFiles(fileInfoList);

            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfigDTO modelConfig=new Meta.ModelConfigDTO();
            newMeta.setModelConfig(modelConfig);

            List<Meta.ModelConfigDTO.ModelsDTO> modelsInfoList=new ArrayList<>();
            modelConfig.setModels(modelsInfoList);
            modelsInfoList.addAll(newModelInfoList);
        }
        //输出配置
        if(templateMakerOutputConfig!=null){
            if(templateMakerOutputConfig.isRemoveGroupFilesFromRoot()){
                List<Meta.FileConfigDTO.FilesDTO> fileInfoList = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(fileInfoList));
            }
        }
        //- 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta),metaOutputPath);
        return id;
    }

    private static List<Meta.ModelConfigDTO.ModelsDTO> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        List<Meta.ModelConfigDTO.ModelsDTO> newModelInfoList=new ArrayList<>();
        if(templateMakerModelConfig==null){
            return newModelInfoList;
        }
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if(CollUtil.isEmpty(models)){
            return newModelInfoList;
        }
        //处理模型信息
        List<Meta.ModelConfigDTO.ModelsDTO> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfigDTO.ModelsDTO modelInfo = new Meta.ModelConfigDTO.ModelsDTO();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());

        //模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if(modelGroupConfig!=null){
            Meta.ModelConfigDTO.ModelsDTO groupModelInfo = new Meta.ModelConfigDTO.ModelsDTO();
            BeanUtil.copyProperties(modelGroupConfig,groupModelInfo);

            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        }else {
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    private static List<Meta.FileConfigDTO.FilesDTO> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        //非空校验
        List<Meta.FileConfigDTO.FilesDTO> newFileInfoList=new ArrayList<>();
        if(templateMakerFileConfig==null){
            return newFileInfoList;
        }
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();
        if(CollUtil.isEmpty(fileConfigInfoList)){
            return newFileInfoList;
        }
        //生成文件模板
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();

            if(!inputFilePath.startsWith(sourceRootPath)){
                inputFilePath= sourceRootPath +File.separator+inputFilePath;
            }

            List<File> fileList = FileFilter.doFilter(inputFilePath, fileInfoConfig.getFilterConfigList());
            //不处理模板文件
            fileList=fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .collect(Collectors.toList());
            for (File file : fileList) {
                Meta.FileConfigDTO.FilesDTO fileInfo = makeFileTemplate(file, templateMakerModelConfig, sourceRootPath,fileInfoConfig);
                newFileInfoList.add(fileInfo);
            }
        }

        //文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if(fileGroupConfig!=null){
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            String condition = fileGroupConfig.getCondition();

            Meta.FileConfigDTO.FilesDTO groupFileInfo = new Meta.FileConfigDTO.FilesDTO();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList=new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }
        return newFileInfoList;
    }

    /**
     * 制作文件模板
     * @param inputFile
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @return
     */
    private static Meta.FileConfigDTO.FilesDTO makeFileTemplate(File inputFile, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        String fileInputAbsolutePath= inputFile.getAbsolutePath().replaceAll("\\\\","/");
        String fileOutputAbsolutePath= fileInputAbsolutePath+".ftl";
        //生成文件路径
        String fileInputPath=fileInputAbsolutePath.replace(sourceRootPath+"/","");
        String fileOutputPath=fileInputPath+".ftl";

        String fileContent;
        //是否已有模板文件
        boolean hasTemplateFile=FileUtil.exist(fileOutputAbsolutePath);

        if(hasTemplateFile){
            //在已有模板中再次制作
            fileContent=FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else {
            fileContent=FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();

        String replacement;
        String newFileContent=fileContent;

        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            if(modelGroupConfig==null){
                replacement= String.format("${%s}", modelInfoConfig.getFieldName());
            }else {
                String groupKey = modelGroupConfig.getGroupKey();
                replacement= String.format("${%s.%s}",groupKey, modelInfoConfig.getFieldName());
            }
            newFileContent= StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(),replacement);
        }

        //文件配置信息
        Meta.FileConfigDTO.FilesDTO fileInfo=new Meta.FileConfigDTO.FilesDTO();
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setCondition(fileInfoConfig.getCondition());
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        //是否更改文件内容
        boolean contentEquals=newFileContent.equals(fileContent);
        //静态文件不生成模板
        if(!hasTemplateFile){
            if(contentEquals){
                fileInfo.setInputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            }else {
                //- 输出模板文件
                FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);
            }
        }else if(!contentEquals) {
            //- 输出模板文件
            FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);
        }
        return fileInfo;
    }

    /**
     * 文件去重
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfigDTO.FilesDTO> distinctFiles(List<Meta.FileConfigDTO.FilesDTO> fileInfoList){
        Map<String,List<Meta.FileConfigDTO.FilesDTO>> groupKeyFileInfoListMap=fileInfoList.stream()
                .filter(fileInfo->StrUtil.isNotEmpty(fileInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.FileConfigDTO.FilesDTO::getGroupKey)
                );

        Map<String,Meta.FileConfigDTO.FilesDTO> groupKeyMergedFileInfoMap=new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfigDTO.FilesDTO>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfigDTO.FilesDTO> tempFileInfoList = entry.getValue();
            List<Meta.FileConfigDTO.FilesDTO> newFileInfoList=new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo->fileInfo.getFiles().stream())
                    .collect(
                            Collectors.toMap(Meta.FileConfigDTO.FilesDTO::getOutputPath,o->o,(e,r)->r)
                    ).values()
            );
            Meta.FileConfigDTO.FilesDTO newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey,newFileInfo);
        }

        List<Meta.FileConfigDTO.FilesDTO> resultList=new ArrayList<>(groupKeyMergedFileInfoMap.values());

        List<Meta.FileConfigDTO.FilesDTO> noGroupFileInfoList=fileInfoList.stream()
                .filter(fileInfo->StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfigDTO.FilesDTO::getOutputPath,o->o,(e,r)->r)
                ).values())
        );
        return resultList;
    }
    /**
     * 模型去重
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsDTO> distinctModels(List<Meta.ModelConfigDTO.ModelsDTO> modelInfoList){
        Map<String,List<Meta.ModelConfigDTO.ModelsDTO>> groupKeyModelInfoListMap=modelInfoList.stream()
                .filter(modelInfo->StrUtil.isNotEmpty(modelInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.ModelConfigDTO.ModelsDTO::getGroupKey)
                );

        Map<String,Meta.ModelConfigDTO.ModelsDTO> groupKeyMergedModelInfoMap=new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfigDTO.ModelsDTO>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfigDTO.ModelsDTO> tempModelInfoList = entry.getValue();
            List<Meta.ModelConfigDTO.ModelsDTO> newModelInfoList=new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo->modelInfo.getModels().stream())
                    .collect(
                            Collectors.toMap(Meta.ModelConfigDTO.ModelsDTO::getFieldName,o->o,(e,r)->r)
                    ).values()
            );
            Meta.ModelConfigDTO.ModelsDTO newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey,newModelInfo);
        }

        List<Meta.ModelConfigDTO.ModelsDTO> resultList=new ArrayList<>(groupKeyMergedModelInfoMap.values());

        List<Meta.ModelConfigDTO.ModelsDTO> noGroupModelInfoList=modelInfoList.stream()
                .filter(modelInfo->StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupModelInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfigDTO.ModelsDTO::getFieldName,o->o,(e,r)->r)
                ).values())
        );
        return resultList;
    }
}
