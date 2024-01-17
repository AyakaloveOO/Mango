package com.kokomi.maker.meta;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.kokomi.maker.meta.enums.FileGenerateTypeEnum;
import com.kokomi.maker.meta.enums.FileTypeEnum;
import com.kokomi.maker.meta.enums.ModelTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校验和处理默认值
 */
public class MetaValidator {
    public static void validAndFill(Meta meta){
        //基础信息
        validAndFillMetaRoot(meta);
        //文件信息
        validAndFillFileConfig(meta);
        //模型信息
        validAndFillModelConfig(meta);
    }

    private static void validAndFillModelConfig(Meta meta) {
        Meta.ModelConfigDTO modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            return;
        }
        List<Meta.ModelConfigDTO.ModelsDTO> models = modelConfig.getModels();
        if (CollectionUtil.isEmpty(models)) {
            return;
        }
        for (Meta.ModelConfigDTO.ModelsDTO model : models) {
            String groupKey = model.getGroupKey();
            if(StrUtil.isNotEmpty(groupKey)){
                //生成中间参数
                List<Meta.ModelConfigDTO.ModelsDTO> subModels=model.getModels();
                String allArgsStr=model.getModels().stream()
                        .map(subModel->String.format("\"--%s\"",subModel.getFieldName()))
                        .collect(Collectors.joining(","));
                model.setAllArgsStr(allArgsStr);
                continue;
            }
            String fieldName = model.getFieldName();
            if (StrUtil.isBlank(fieldName)) {
                throw new MetaException("未填写 fieldName");
            }
            String type = model.getType();
            if (StrUtil.isEmpty(type)) {
                model.setType(ModelTypeEnum.STRING.getValue());
            }
        }
    }

    private static void validAndFillFileConfig(Meta meta) {
        Meta.FileConfigDTO fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            return;
        }
        String sourceRootPath = fileConfig.getSourceRootPath();
        if(StrUtil.isBlank(sourceRootPath)){
            throw new MetaException("未填写 sourceRootPath");
        }
        String inputRootPath = fileConfig.getInputRootPath();
        if(StrUtil.isBlank(inputRootPath)){
            inputRootPath=".source"+ File.separator+ FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
            fileConfig.setInputRootPath(inputRootPath);
        }
        String outputRootPath = fileConfig.getOutputRootPath();
        if (StrUtil.isBlank(outputRootPath)) {
            outputRootPath="generated";
            fileConfig.setOutputRootPath(outputRootPath);
        }
        String type = fileConfig.getType();
        if(StrUtil.isEmpty(type)){
            type= FileTypeEnum.DIR.getValue();
            fileConfig.setType(type);
        }
        List<Meta.FileConfigDTO.FilesDTO> files = fileConfig.getFiles();
        if (CollectionUtil.isEmpty(files)) {
            return;
        }
        for (Meta.FileConfigDTO.FilesDTO file : files) {
            String fileType = file.getType();
            if(FileTypeEnum.GROUP.getValue().equals(fileType)){
                continue;
            }
            String inputPath = file.getInputPath();
            if (StrUtil.isBlank(inputPath)) {
                throw new MetaException("未填写 inputPath");
            }
            String outputPath = file.getOutputPath();
            if (StrUtil.isBlank(outputPath)) {
                file.setOutputPath(inputPath);
            }
            if (StrUtil.isBlank(fileType)) {
                if (StrUtil.isBlank(FileUtil.getSuffix(inputPath))) {
                    file.setType(FileTypeEnum.DIR.getValue());
                }else {
                    file.setType(FileTypeEnum.FILE.getValue());
                }
            }
            String generateType = file.getGenerateType();
            if (StrUtil.isBlank(generateType)) {
                if (inputPath.endsWith(".ftl")) {
                    file.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                }else {
                    file.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                }
            }
        }
    }

    private static void validAndFillMetaRoot(Meta meta) {
        String name =StrUtil.blankToDefault(meta.getName(),"my-generator");
        String description =StrUtil.emptyToDefault(meta.getDescription(),"代码生成器");
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(),"com.kokomi");
        String version = StrUtil.emptyToDefault(meta.getVersion(),"1.0");
        String author = StrUtil.emptyToDefault(meta.getAuthor(),"kokomi");
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(),DateUtil.now());
        meta.setName(name);
        meta.setDescription(description);
        meta.setBasePackage(basePackage);
        meta.setVersion(version);
        meta.setAuthor(author);
        meta.setCreateTime(createTime);
    }
}
