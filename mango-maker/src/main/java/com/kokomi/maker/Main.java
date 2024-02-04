package com.kokomi.maker;

import com.kokomi.maker.generator.GenerateTemplate;
import com.kokomi.maker.generator.MainGenerator;
import com.kokomi.maker.generator.SimplifyGenerator;
import com.kokomi.maker.generator.ZipGenerator;
import freemarker.template.TemplateException;

import java.io.IOException;

public class Main {
    /**
     * 全局调用入口，接收用户参数，执行命令
     * @param args
     */
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        GenerateTemplate generateTemplate=new ZipGenerator();
        generateTemplate.doGenerate();
    }
}