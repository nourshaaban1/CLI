import org.os.CLI;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CLITest {
    private CLI cli;
    @BeforeEach
    public void setUp() {
        cli = new CLI();
        cli.mkdir("testDir");
        cli.cd("testDir");
    }

    @AfterEach
    public void tearDown() {
        cli.cd("../");
        cli.rmdir("testDir");
    }

    @Test
    public void testMkdir() {
        assertTrue(cli.mkdir("newDir"));
        assertFalse(cli.mkdir("newDir"));  // Directory already exists
        cli.rmdir("newDir");  // Cleanup
    }

    @Test
    public void testCd() {
        cli.mkdir("nestedDir");
        assertTrue(cli.cd("nestedDir"));
        assertEquals("C:\\Users\\Brothers\\testDir\\nestedDir", cli.pwd().toString());

        assertTrue(cli.cd("../"));
        assertEquals("C:\\Users\\Brothers\\testDir", cli.pwd().toString());
    }

    @Test
    public void testTouch() throws IOException {
        assertTrue(cli.touch("file.txt"));
        assertFalse(cli.touch("file.txt"));  // File already exists
        cli.rm("file.txt");  // Cleanup
    }

    @Test
    public void testRm() throws IOException {
        cli.touch("fileToDelete.txt");
        assertTrue(cli.rm("fileToDelete.txt"));
        assertFalse(cli.rm("fileToDelete.txt"));  // File does not exist anymore
    }

    @Test
    public void testEchoOverwrite() {
        cli.writeToFile("file.txt", "Initial content");
        cli.writeToFile("file.txt", "Overwritten content");
        assertEquals("Overwritten content\n", cli.cat("file.txt"));
        cli.rm("file.txt");  // Cleanup
    }

    @Test
    public void testEchoAppend() {
        cli.writeToFile("file.txt", "Line 1");
        cli.appendToFile("file.txt", "Line 2");
        assertEquals("Line 1\nLine 2\n", cli.cat("file.txt"));
        cli.rm("file.txt");  // Cleanup
    }

    @Test
    public void testLs() throws IOException {
        cli.touch("file1.txt");
        cli.touch("file2.txt");
        String output = cli.ls(false, false);
        assertTrue(output.contains("file1.txt"));
        assertTrue(output.contains("file2.txt"));
        cli.rm("file1.txt");
        cli.rm("file2.txt");
    }

    @Test
    public void testPwd() {
        assertEquals("C:\\Users\\Brothers\\testDir", cli.pwd().toString());
    }

    @Test
    public void testCat() {
        cli.writeToFile("catTest.txt", "Sample content");
        assertEquals("Sample content\n", cli.cat("catTest.txt"));
        cli.rm("catTest.txt");  // Cleanup
    }
}
