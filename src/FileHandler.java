import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileHandler {

    /**
     * Reads numbers from the given file and populates a list with them
     * @param fileName Name of the input file
     * @return List of numbers
     */
    public List<String> readNumbersFromFile(String fileName) {
        Path file = Paths.get(fileName);
        List<String> numbers = null;
        try {
            numbers = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {

            System.out.println("Couldn't read from file");
        }
        return numbers;
    }

    /**
     * Writes the given numbers to the given file
     * @param targetFileName Name of the output file
     * @param numbers List of numbers to write to file
     */
    public void writeHexNumbersToFile(String targetFileName, List<String> numbers) {
        Path file = Paths.get(targetFileName);
        try {
            Files.write(file, numbers, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Couldn't write to file");
        }
    }
}
