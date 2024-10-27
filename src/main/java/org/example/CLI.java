package org.example;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CLI {

    private Path pwd;
    CLI() {pwd = Paths.get("C:\\Users\\Brothers");}

    public Path pwd() {return pwd;}

    public void mkdir(String directory) {
        Path dirPath = pwd.resolve(directory);
        File dir = dirPath.toFile();
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.printf("Directory %s Successfully Created!\n>>> ", dirPath);
            } else {
                System.out.printf("Failed to create directory %s.\n>>> ", dirPath);
            }
        } else {
            System.out.printf("Directory %s already exists.\n>>> ", dirPath);
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

}