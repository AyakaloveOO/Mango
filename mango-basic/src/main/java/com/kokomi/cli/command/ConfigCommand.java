package com.kokomi.cli.command;

import com.kokomi.model.MainTemplateConfig;
import picocli.CommandLine.Command;

import java.lang.reflect.Field;

/**
 * 查看参数信息命令
 */
@Command(name = "config",description = "查看参数信息",mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("查看参数信息");
        Class<MainTemplateConfig> mainTemplateConfigClass = MainTemplateConfig.class;
        Field[] fields = mainTemplateConfigClass.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("字段名称:"+field.getName());
            System.out.println("字段类型:"+field.getType());
            System.out.println("-------");
        }
    }
}
