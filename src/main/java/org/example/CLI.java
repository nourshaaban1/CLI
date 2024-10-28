package org.example;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CLI {

    private Path pwd;
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

    public void mkdir(String directory) {
        Path dirPath = pwd.resolve(directory);
        File dir = dirPath.toFile();
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.printf("Directory %s Successfully Created!\nPS ", dirPath);
            } else {
                System.out.printf("Failed to create directory %s.\nPS ", dirPath);
            }
        } else {
            System.out.printf("Directory %s already exists.\nPS ", dirPath);
        }
    }

    public void cd(String directory) {
        if (directory.equals("../")) {
            if (pwd.getParent() != null) {
                pwd = pwd.getParent();
            } else {
                System.out.println("Already at the root directory, cannot go up.");
            }
        } else {
            Path targetPath = pwd.resolve(directory);
            if (Files.exists(targetPath) && Files.isDirectory(targetPath)) {
                pwd = targetPath;
            } else {
                System.out.println("Directory does not exist: " + targetPath);
            }
        }
    }

    public void ls() {
        File currDir = pwd.toFile();
        File filesList[] = currDir.listFiles();

        if (filesList == null) {
            System.out.println("No files found.");
            return;
        }

        System.out.println("lastModified                     Length           Name");
        System.out.println("-------------------              ------           ---------------");

        for (File file : filesList) {
            if (!file.isHidden()) {
                System.out.printf("%tF %<tT              %d                %s\n",
                        file.lastModified(), file.length(), file.getName());
            }
        }
        System.out.print("\n");
    }

    public void lsr() {
        File currDir = pwd.toFile();
        File filesList[] = currDir.listFiles();

        if (filesList == null) {
            System.out.println("No files found.");
            return;
        }

        System.out.println("lastModified                     Length           Name");
        System.out.println("-------------------              ------           ---------------");

        for(int i = filesList.length - 1;i >= 0;i--) {
            if(!filesList[i].isHidden()) {
                System.out.printf("%tF %<tT              %d                %s\n",filesList[i].lastModified(),filesList[i].length(),filesList[i].getName());
            }
        }
        System.out.print("\n");
    }

    public void lsa() {
        File currDir = pwd.toFile();
        File filesList[] = currDir.listFiles();

        if (filesList == null) {
            System.out.println("No files found.");
            return;
        }

        System.out.println("lastModified                     Length           Name");
        System.out.println("-------------------              ------           ---------------");

        for (File file : filesList) {
            System.out.printf("%tF %<tT              %d                %s\n",file.lastModified(), file.length(), file.getName());
        }
        System.out.print("\n");
    }

    public void rmdir(String directory) {
        Path path = pwd.resolve(directory);
        File dir = path.toFile();

        if (!dir.exists()) {
            System.out.println("Directory Doesn't Exist");
            return;
        }

        if (dir.delete()) {
            System.out.println("Directory Successfully Deleted!");
        } else {
            System.out.println(" ");
            System.out.println("Failed to Delete Directory As It Does Not Empty");
            System.out.print("Enter(Y/N) To Delete All Files And Subdirectories Within The Directory: ");

            Scanner scanner = new Scanner(System.in);
            String c = scanner.next().trim();
            if (c.equalsIgnoreCase("Y")) {
                if (deleteDirectoryRecursively(dir)) {
                    System.out.println("Directory and all contents successfully deleted!\n");
                } else {
                    System.out.println("Failed to delete some contents.\n");
                }
            } else {
                System.out.println("Command Terminated.\n");
            }
        }
    }

    public void touch(String filePath) throws IOException {
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
        } else {
            System.out.println("File Already Exists.");
        }
    }
}