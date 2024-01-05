package com.kokomi.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 静态文件生成
 */
public class StaticGenerator {
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String inputPath=projectPath+File.separator+"samples/acm-template";
        String outputPath=projectPath;
        copyFilesByRecursive(inputPath,outputPath);
    }

    /**
     * 利用Hutool拷贝文件
     * @param inputPath
     * @param outputPath
     */
    public static void copyFilesByHutool(String inputPath,String outputPath){
        FileUtil.copy(inputPath,outputPath,false);
    }

    /**
     * 递归复制文件
     * @param inputPath
     * @param outputPath
     */
    public static void copyFilesByRecursive(String inputPath,String outputPath){
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        try {
            copyFilesByRecursive(inputFile,outputFile);
        }catch (Exception e){
            System.err.println("文件复制失败");
            e.printStackTrace();
        }
    }

    /**
     * 递归复制文件
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    public static void copyFilesByRecursive(File inputFile,File outputFile) throws IOException {
        if(inputFile.isDirectory()){
            File newFile = new File(outputFile, inputFile.getName());
            if(!newFile.exists()){
                newFile.mkdirs();
            }
            File[] files = inputFile.listFiles();
            if(ArrayUtil.isEmpty(files)){
                return;
            }
            for (File file : files) {
                copyFilesByRecursive(file,newFile);
            }
        }else {
            Path deskPath = outputFile.toPath().resolve(inputFile.getName());
            Files.copy(inputFile.toPath(),deskPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
