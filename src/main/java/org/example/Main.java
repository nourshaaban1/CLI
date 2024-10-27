package org.example;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();
        while(true) {
            System.out.printf("PS %s> ",cli.pwd());
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            int comLength = command.length();

            if(command.equals("exit")) System.exit(0);
            if(command.startsWith("mkdir")) {
                String dir = command.substring(6,comLength);
                    cli.mkdir(dir);
            }
            if(command.startsWith("cd")) {
                String dir = command.substring(3,comLength);
                cli.cd(dir);
            }
            if(command.startsWith("ls")) {
                cli.ls();
            }
            if(command.startsWith("ls -r")) {
                cli.lsr();
            }
            if(command.startsWith("ls -a")) {
                cli.lsa();
            }
            if(command.startsWith("pwd")) {
                System.out.println("\nPath");
                System.out.println("----");
                System.out.printf("%s\n",cli.pwd());
                System.out.println(" ");
            }
        }
    }
}