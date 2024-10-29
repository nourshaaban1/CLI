import org.os.CLI;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CLITest {
    private final CLI cli = new CLI();

    @BeforeEach
    public void setUp() {
        cli.mkdir("testDir");
        cli.cd("testDir");
    }
    @AfterEach
    public void tearDown() {
        cli.cd("../");
        cli.rmdir("testDir",true);
    }

    @Test
    public void testPwd() {
        assertEquals("C:\\Users\\Brothers\\testDir", cli.pwd().toString());
    }

    @Test
    public void testCd() {
        cli.mkdir("nestedDir");
        // Test cd into nestedDir
        assertTrue(cli.cd("nestedDir"));
        assertEquals("C:\\Users\\Brothers\\testDir\\nestedDir", cli.pwd().toString());

        // Test cd out to testDir
        assertTrue(cli.cd("../"));
        assertEquals("C:\\Users\\Brothers\\testDir", cli.pwd().toString());

        // Test cd into a non-existent directory
        cli.rmdir("nestedDir",true);
        assertFalse(cli.cd("nestedDir"));
    }

    @Test
    public void testMkdir() {
        // Test creating a valid directory
        assertTrue(cli.mkdir("newDir"));
        assertTrue(cli.cd("newDir"));
        assertEquals("C:\\Users\\Brothers\\testDir\\newDir", cli.pwd().toString());

        // Return to testDir and cleanup
        cli.cd("../");
        cli.rmdir("newDir",true);

        // Test trying to create a directory with invalid characters
        assertFalse(cli.mkdir("Z:\\Users\\Brothers"));
        assertFalse(cli.cd("Z:\\Users\\Brothers"));
    }

    @Test
    public void testLs() throws IOException {
        cli.touch("file1.txt");
        cli.touch("file2.txt");

        // Generate expected output by calling cli.ls
        String output = cli.ls(false, false);

        // Check that output contains specific file names and expected structure
        assertTrue(output.contains("file1.txt"), "Output should contain file1.txt");
        assertTrue(output.contains("file2.txt"), "Output should contain file2.txt");
        assertTrue(output.contains("lastModified"), "Output should contain header 'lastModified'");
        assertTrue(output.contains("Length"), "Output should contain header 'Length'");
        assertTrue(output.contains("Name"), "Output should contain header 'Name'");

        // Cleanup files
        cli.rm("file1.txt");
        cli.rm("file2.txt");
    }

    @Test
    public void testRmdir() {
        cli.mkdir("nestedDir");
        assertTrue(cli.rmdir("nestedDir", true));
        assertFalse(cli.cd("nestedDir"));
    }

    @Test
    public void testRmdirRecursive() throws IOException {
        cli.mkdir("dirWithFiles");
        cli.touch("dirWithFiles/file1.txt");
        assertTrue(cli.rmdir("dirWithFiles", true));
        assertFalse(cli.cd("dirWithFiles"));
    }

    @Test
    public void testTouch() throws IOException {
        assertTrue(cli.touch("newFile.txt"));
        assertFalse(cli.touch("newFile.txt")); // File already exists
        cli.rm("newFile.txt");
    }

    @Test
    public void testRm() throws IOException {
        cli.touch("fileToDelete.txt");
        assertTrue(cli.rm("fileToDelete.txt"));
        assertFalse(cli.rm("fileToDelete.txt")); // File no longer exists
    }

    @Test
    public void testCat() {
        cli.writeToFile("catTest.txt", "Sample content");
        assertEquals("Sample content\n", cli.cat("catTest.txt"));
        cli.rm("catTest.txt");
    }

    @Test
    public void testEcho() {
        String text = "Hello, World!";
        assertEquals(text, cli.echo(text));
    }

    @Test
    public void testAppendToFile() {
        cli.writeToFile("appendTest.txt", "Line 1\n");
        assertTrue(cli.appendToFile("appendTest.txt", "Line 2"));
        assertEquals("Line 1\nLine 2\n", cli.cat("appendTest.txt"));
        cli.rm("appendTest.txt");
    }

    @Test
    public void testWriteToFile() throws IOException {
        cli.touch("writeTest.txt");

        assertTrue(cli.writeToFile("writeTest.txt", "Initial content"));
        assertTrue(cli.writeToFile("writeTest.txt", "Overwrite content"));

        assertEquals("Overwrite content\n", cli.cat("writeTest.txt"));
        cli.rm("writeTest.txt");
    }

    @Test
    public void testMv() throws IOException {
        cli.touch("sourceFile.txt");

        // Test renaming a file (destination doesn't exist)
        assertTrue(cli.mv("sourceFile.txt", "renamedFile.txt"));
        assertFalse(cli.mv("sourceFile.txt", "renamedFile.txt")); // Source no longer exists
        assertTrue(cli.rm("renamedFile.txt")); // Cleanup

        // Test moving a file into an existing directory
        cli.touch("sourceFile.txt");
        cli.mkdir("targetDir");

        assertTrue(cli.mv("sourceFile.txt", "targetDir"));
        assertTrue(cli.cd("targetDir"));
        assertTrue(new File(cli.pwd().resolve("sourceFile.txt").toString()).exists());

        cli.cd("../");
        cli.rmdir("targetDir", true); // Cleanup

        // Test moving a non-existent file
        assertFalse(cli.mv("nonExistent.txt", "renamed.txt"));

        // Test renaming a directory
        cli.mkdir("sourceDir");
        assertTrue(cli.mv("sourceDir", "renamedDir"));
        assertTrue(new File(cli.pwd().resolve("renamedDir").toString()).exists());
        cli.rmdir("renamedDir",true); // Cleanup
    }

    @Test
    public void testPipedLsGrep() throws IOException {
        cli.touch("file1.txt");
        cli.touch("files2.log");
        cli.touch("anotherfile.txt");

        String command = "ls | grep \"file\"";
        String result = cli.executePipedCommands(command);

        assertTrue(result.contains("file1.txt"), "Result should contain file1.txt");
        assertTrue(result.contains("anotherfile.txt"), "Result should contain anotherfile.txt");
        assertFalse(result.contains("file2.log"), "Result should not contain file2.log");

        // Cleanup
        cli.rm("file1.txt");
        cli.rm("files2.log");
        cli.rm("anotherfile.txt");
    }

    @Test
    public void testPipedEchoGrep() {
        String command = "echo \"hello world\" | grep \"hello\"";
        String result = cli.executePipedCommands(command);

        assertEquals("\"hello world\"\n", result, "The echo | grep command should produce 'hello world'.");
    }

    @Test
    public void testPipedCatGrep() {
        cli.writeToFile("testfile.txt", "hello world\nanother line\nhello again\n");

        String command = "cat testfile.txt | grep \"hello\"";
        String result = cli.executePipedCommands(command);

        // Expected result should only contain lines with "hello"
        String expected = "hello world\nhello again\n";
        assertEquals(expected, result, "The cat | grep command did not filter correctly.");

        // Cleanup
        cli.rm("testfile.txt");
    }

    @Test
    public void testPipedEchoGrepNoMatch() {
        String command = "echo \"goodbye world\" | grep \"hello\"";
        String result = cli.executePipedCommands(command);

        // Since there's no "hello" in the echo, expect an empty result
        assertEquals("", result, "The echo | grep command should produce no output if there's no match.");
    }

    @Test
    public void testMultiplePipes() {
        String command = "echo \"line one\nline two\nline three\" | grep \"line\" | grep \"two\"";
        String result = cli.executePipedCommands(command);

        // Only "line two" should match both grep commands
        assertEquals("line two\n", result, "The command with multiple pipes did not produce the expected result.");
    }
}
