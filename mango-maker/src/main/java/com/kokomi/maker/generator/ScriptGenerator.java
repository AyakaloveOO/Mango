package com.kokomi.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * 生成脚本文件
 */
public class ScriptGenerator {
    public static void doGenerate(String outputPath,String jarPath){
        //linux脚本
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash").append("\n");
        sb.append(String.format("java -jar %s \"$@\"",jarPath)).append("\n");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8),outputPath);
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(outputPath), permissions);
        } catch (Exception e) {
            System.out.println("aasgsagasg");
        }
        //windows脚本
        sb=new StringBuilder();
        sb.append("@echo off").append("\n");
        sb.append(String.format("java -jar %s %%*",jarPath)).append("\n");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8),outputPath+".bat");
    }
}
