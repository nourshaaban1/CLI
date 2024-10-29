package org.os;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Fcai CommandLineInterpreter\n" +
                "Copyright (C) OS Assignment. All rights reserved.");
        System.out.println(" ");
        System.out.println("Type 'help' for a list of commands.\n");

        while (true) {
            System.out.printf("PS %s> ", cli.pwd());
            String command = scanner.nextLine().trim();
            try {
                if (command.equals("exit")) {
                    System.out.println("Exiting the CLI. Goodbye!");
                    break;
                }
                if (command.contains("|")) {
                    handlePipeCommand(command, cli);
                } else {
                    handleSingleCommand(command, cli);
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void handleSingleCommand(String command, CLI cli) throws IOException {
        if (command.startsWith("mkdir")) {
            String dir = command.substring(6).trim();
            cli.mkdir(dir);
        }
        else if (command.startsWith("cd")) {
            String dir = command.substring(3).trim();
            cli.cd(dir);
        }
        else if (command.startsWith("echo")) {
            handleEcho(command, cli);
        }
        else if (command.startsWith("ls")) {
            handleLs(command, cli);
        }
        else if (command.startsWith("pwd")) {
            System.out.printf("\nPath: %s\n\n", cli.pwd());
        }
        else if (command.startsWith("touch")) {
            String fileName = command.substring(6).trim();
            cli.touch(fileName);
        }
        else if (command.startsWith("rmdir")) {
            String dir = command.substring(6).trim();
            cli.rmdir(dir);
        }
        else if (command.startsWith("rm")) {
            String fileName = command.substring(3).trim();
            cli.rm(fileName);
        }
        else if (command.startsWith("cat")) {
//          cat test.txt > file.txt
            if (command.contains(" > ")) {
                int separatorIndex = command.indexOf(" > ");
                String sourceFile = command.substring(4,separatorIndex);
                String destinationFile = command.substring(separatorIndex + 3).trim();
                cli.writeToFile(cli.cat(sourceFile), destinationFile + "\n");
            }
//          cat test.txt >> file.txt
            else if (command.contains(" >> ")) {
                int separatorIndex = command.indexOf(" >> ");
                String sourceFile = command.substring(4,separatorIndex);
                String destinationFile = command.substring(separatorIndex + 3).trim();
                cli.appendToFile(cli.cat(sourceFile), destinationFile + "\n");
            }
            else {
                String fileName = command.substring(4).trim();
                System.out.println(cli.cat(fileName));
            }
        }
        else if (command.equals("help")) {
            displayHelp();
        }
        else {
            System.out.println("Invalid command. Type 'help' for a list of commands.");
        }
    }
    private static void handlePipeCommand(String command, CLI cli) {
        String[] commands = command.split("\\|");
        String input = ""; // Initialize input for the first command

        for (String cmd : commands) {
            cmd = cmd.trim();
            String output;

            if (cmd.startsWith("ls")) {
                boolean showHidden = cmd.contains("-a");
                boolean reverseOrder = cmd.contains("-r");
                output = cli.ls(showHidden, reverseOrder);
            } else if (cmd.startsWith("cat")) {
                String fileName = cmd.substring(4).trim();
                output = cli.cat(fileName);
            } else if (cmd.startsWith("grep")) {
                output = handleGrep(input, cmd.substring(5).trim());
            } else {
                System.out.println("Invalid command in pipe: " + cmd);
                return;
            }
            input = output;
        }

        System.out.println(input);
    }

    private static String handleGrep(String input, String pattern) {
        StringBuilder result = new StringBuilder();
        String[] lines = input.split("\n");
        for (String line : lines) {
            if (line.contains(pattern.substring(1,pattern.length() - 1))) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }
    private static void handleEcho(String command, CLI cli) {
//      echo "test" > file.txt
        if (command.contains(" > ")) {
            int separatorIndex = command.indexOf(" > ");
            String text = command.substring(6, separatorIndex - 1).trim();
            String fileName = command.substring(separatorIndex + 3).trim();
            cli.writeToFile(fileName, cli.echo(text) + "\n");
        }
//      echo "test" >> file.txt
        else if (command.contains(" >> ")) {
            int separatorIndex = command.indexOf(" >> ");
            String text = command.substring(6, separatorIndex - 1).trim();
            String fileName = command.substring(separatorIndex + 4).trim();
            cli.appendToFile(fileName, cli.echo(text) + "\n");
        }
//      echo "Sally 3al naby"
        else {
            String text = command.substring(6, command.length() - 1).trim();
            System.out.println(cli.echo(text));
        }
    }
    private static void handleLs(String command, CLI cli) {
        boolean showHidden = command.contains("-a");
        boolean reverseOrder = command.contains("-r");

        if (command.contains(" > ")) {
            int separatorIndex = command.indexOf(" > ");
            String fileName = command.substring(separatorIndex + 3).trim();
            cli.writeToFile(fileName, cli.ls(showHidden, reverseOrder));
        } else if (command.contains(" >> ")) {
            int separatorIndex = command.indexOf(" >> ");
            String fileName = command.substring(separatorIndex + 4).trim();
            cli.appendToFile(fileName, cli.ls(showHidden, reverseOrder));
        } else {
            System.out.println(cli.ls(showHidden, reverseOrder));
        }
    }

    private static void displayHelp() {
        System.out.println("Available Commands:");
        System.out.println("mkdir <directory>   - Create a new directory.");
        System.out.println("cd <directory>      - Change the current directory.");
        System.out.println("ls [-a] [-r]        - List files in the current directory.");
        System.out.println("pwd                 - Show the current directory path.");
        System.out.println("touch <file>        - Create a new file.");
        System.out.println("rmdir <directory>   - Remove a directory.");
        System.out.println("rm <file>           - Remove a file.");
        System.out.println("cat <file>          - Display the contents of a file.");
        System.out.println("echo <text> > <file> - Overwrite a file with text.");
        System.out.println("echo <text> >> <file> - Append text to a file.");
        System.out.println("help                - Display this help message.");
        System.out.println("exit                - Exit the CLI.");
        System.out.println("Usage of pipes: <command1> | <command2> | ...");
    }
}
