package com.kokomi.maker.generator.file;

import cn.hutool.core.io.FileUtil;

/**
 * 静态文件生成
 */
public class StaticFileGenerator {

    /**
     * 利用Hutool拷贝文件
     * @param inputPath
     * @param outputPath
     */
    public static void copyFilesByHutool(String inputPath,String outputPath){
        FileUtil.copy(inputPath,outputPath,false);
    }
}
