package com.kokomi.maker.generator;

/**
 * 执行生成代码
 */
public class MainGenerator extends GenerateTemplate{
    @Override
    protected void buildDist(String outputPath, String sourceCopyPath, String shellOutputPath, String jarPath) {
        System.out.println("不输出精简代码");
    }
}
