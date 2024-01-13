package com.kokomi.maker.generator;

import java.io.*;

/**
 * 构建jar包
 */
public class JarGenerator {
    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        //构建jar包命令
        String mvnCommand="mvn.cmd clean package -DskipTests=true";
        //拆分命令
        ProcessBuilder processBuilder = new ProcessBuilder(mvnCommand.split(" "));
        processBuilder.directory(new File(projectDir));
        Process process = processBuilder.start();
        //读取命令输出
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line=reader.readLine())!=null){
            System.out.println(line);
        }
        //等待命令执行完成
        int exitCode = process.waitFor();
        System.out.println("命令执行结束，退出码："+exitCode);
    }
}
