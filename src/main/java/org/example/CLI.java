package org.example;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Scanner;

public class CLI {

    private Path pwd;
    private Scanner scanner;
    private boolean deleteDirectoryRecursively(File dir) {
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryRecursively(file);
            }
        }
        return dir.delete();
    }

    public CLI() {pwd = Paths.get("C:\\Users\\Brothers");}

    public Path pwd() {return pwd;}

    public boolean mkdir(String directory) {
        Path dirPath = pwd.resolve(directory);
        File dir = dirPath.toFile();
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.printf("Directory %s Successfully Created!\n", dirPath);
                return true;
            } else {
                System.out.printf("Failed to create directory %s.\n", dirPath);
                return false;
            }
        } else {
            System.out.printf("Directory %s already exists.\n", dirPath);
            return false;
        }
    }

    public boolean cd(String directory) {
        if (directory.equals("../")) {
            if (pwd.getParent() != null) {
                pwd = pwd.getParent();
                return true;
            } else {
                System.out.println("Already at the root directory, cannot go up.");
                return false;
            }
        } else {
            Path targetPath = pwd.resolve(directory);
            if (Files.exists(targetPath) && Files.isDirectory(targetPath)) {
                pwd = targetPath;
                return true;
            } else {
                System.out.println("Directory does not exist: " + targetPath);
                return false;
            }
        }
    }

    public String ls(boolean showHidden, boolean reverseOrder) {
        File currDir = pwd.toFile();
        File[] filesList = currDir.listFiles();
        StringBuilder output = new StringBuilder();

        if (filesList == null || filesList.length == 0) {
            output.append("No files found.\n");
            return output.toString();
        }

        output.append("lastModified                     Length           Name\n");
        output.append("-------------------              ------           ---------------\n");

        int start = reverseOrder ? filesList.length - 1 : 0;
        int end = reverseOrder ? -1 : filesList.length;
        int step = reverseOrder ? -1 : 1;

        for (int i = start; i != end; i += step) {
            File file = filesList[i];
            if (showHidden || !file.isHidden()) {
                output.append(String.format("%tF %<tT              %d                %s\n",
                        file.lastModified(), file.length(), file.getName()));
            }
        }
        output.append("\n");
        return output.toString();
    }

    public boolean rmdir(String directory) {
        Path path = pwd.resolve(directory);
        File dir = path.toFile();

        if (!dir.exists()) {
            System.out.println("Directory Doesn't Exist");
            return false;
        }

        if (dir.delete()) {
            System.out.println("Directory Successfully Deleted!");
            return true;
        } else {
            System.out.println("Failed to Delete Directory As It Is Not Empty");
            System.out.print("Enter (Y/N) To Delete All Files And Subdirectories Within The Directory: ");

            String c = scanner.next().trim();
            if (c.equalsIgnoreCase("Y")) {
                if (deleteDirectoryRecursively(dir)) {
                    System.out.println("Directory and all contents successfully deleted!\n");
                    return true;
                } else {
                    System.out.println("Failed to delete some contents.\n");
                    return false;
                }
            } else {
                System.out.println("Command Terminated.\n");
                return false;
            }
        }
    }

    public boolean touch(String filePath) throws IOException {
        Path path = pwd.resolve(filePath);
        File file = path.toFile();

        // Check if the parent directory exists; if not, create it
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Create the new file if it doesn't exist
        if (file.createNewFile()) {
            System.out.println("File Successfully Created!");
            return true;
        } else {
            System.out.println("File Already Exists.");
            return false;
        }
    }

    public boolean rm(String fileName) {
        Path filePath = pwd.resolve(fileName);
        File file = filePath.toFile();

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully.");
                return true;
            } else {
                System.out.println("Failed to delete the file.");
                return false;
            }
        } else {
            System.out.println("File does not exist.");
            return false;
        }
    }

    public String cat(String fileName) {
        Path filePath = pwd.resolve(fileName);
        StringBuilder content = new StringBuilder();

        if (!filePath.toFile().exists()) {
//            System.out.println("File Doesn't Exist");
            return "File Doesn't Exist";
        } else {
            try {
                Files.lines(filePath).forEach(line -> {
//                    System.out.println(line);
                    content.append(line).append("\n");
                });
            } catch (IOException e) {
                return "Failed to read file or file does not exist.";
            }
        }

        return content.toString();
    }

    public boolean appendToFile(String fileName, String content) {
        Path filePath = pwd.resolve(fileName);

        try {
            Files.write(filePath, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Content appended to file successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("Failed to append to file.");
            return false;
        }
    }

    public boolean writeToFile(String fileName, String content) {
        Path filePath = pwd.resolve(fileName);

        try {
            Files.write(filePath, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("File written successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("Failed to write to file.");
            return false;
        }
    }

}