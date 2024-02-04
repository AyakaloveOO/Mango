package com.kokomi.maker.generator;

/**
 * 生成代码生成器压缩包
 */
public class ZipGenerator extends GenerateTemplate{
    @Override
    protected String buildDist(String outputPath, String sourceCopyPath, String shellOutputPath, String jarPath) {
        String distPath=super.buildDist(outputPath, sourceCopyPath, shellOutputPath, jarPath);
        return super.buildZip(distPath);
    }
}
