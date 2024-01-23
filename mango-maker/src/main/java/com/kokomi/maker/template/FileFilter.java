package com.kokomi.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.kokomi.maker.template.enums.FileFilterRangeEnum;
import com.kokomi.maker.template.enums.FileFilterRuleEnum;
import com.kokomi.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件过滤
 */
public class FileFilter {
    /**
     * 对文件或者目录过滤
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> doFilter(String filePath,List<FileFilterConfig> fileFilterConfigList){
        return FileUtil.loopFiles(filePath).stream()
                .filter(file -> singleFileFilter(fileFilterConfigList,file))
                .collect(Collectors.toList());
    }
    /**
     * 单个文件过滤
     * @param fileFilterConfigList
     * @param file
     * @return
     */
    public static boolean singleFileFilter(List<FileFilterConfig> fileFilterConfigList, File file){
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        boolean result=true;

        if(CollUtil.isEmpty(fileFilterConfigList)){
            return true;
        }

        for (FileFilterConfig fileFilterConfig : fileFilterConfigList) {
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();

            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if(fileFilterRangeEnum==null){
                continue;
            }

            String content=fileName;
            switch (fileFilterRangeEnum) {
                case FILE_NAME:
                    content=fileName;
                    break;
                case FILE_CONTENT:
                    content=fileContent;
                    break;
                default:
            }

            FileFilterRuleEnum fileFilterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if(fileFilterRuleEnum==null){
                continue;
            }
            switch (fileFilterRuleEnum) {
                case CONTAINS:
                    result=content.contains(value);
                    break;
                case STARTS_WITH:
                    result=content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result=content.endsWith(value);
                    break;
                case REGEX:
                    result=content.matches(value);
                    break;
                case EQUALS:
                    result=content.equals(value);
                    break;
                default:
            }
            if(!result){
                return false;
            }
        }
        return true;
    }
}
