package com.kokomi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.Arrays;

@Command(name = "ASCIIArt", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true)
public class ASCIIArt implements Runnable { 

    @Option(names = { "-s", "--font-size" }, description = "Font size")
    int fontSize = 19;

    @Option(names = {"-v","--values"})
    int[] values;

    @Parameters(paramLabel = "<word>", defaultValue = "Hellofafa, picocli",
               description = "Words to be translated into ASCII art.")
    private String[] words = { "Hello,", "picocli" }; 

    @Override
    public void run() {
        System.out.println("fontSize = " + fontSize);
        System.out.println("words = " + String.join(",", words));
        System.out.println("values="+ Arrays.toString(values));
    }

    public static void main(String[] args) {
        args=new String[]{"-v","1","-v","2"};
        int exitCode = new CommandLine(new ASCIIArt()).execute(args);
        System.exit(exitCode); 
    }
}