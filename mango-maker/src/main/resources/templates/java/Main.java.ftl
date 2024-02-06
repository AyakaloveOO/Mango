package ${basePackage};

import ${basePackage}.cli.CommandExecutor;

public class Main {
    /**
     * 全局调用入口，接收用户参数，执行命令
     * @param args
     */
    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}