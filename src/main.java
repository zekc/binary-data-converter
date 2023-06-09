import java.util.List;
import java.util.Scanner;

/**
 * Driver class
 */
public class ZekeriyaCedikci_LaleHuseyn_MohamadSaeedOzdamir_MaisSabbagh {

    public static void main(String[] args) {

        //TODO prompt user for input parameters

        FileHandler fileHandler = new FileHandler();
        Scanner scan = new Scanner(System.in);
        char endianness;
        int floatingPointSize;
        String fileName;
        final String OUTPUT_FILE = "output.txt";
        List<String> numbers;
        DataConverter.Endianness endiannes = null;

        System.out.println("Name of the Input File: ");
        fileName = scan.next();

        System.out.println("Byte ordering: ");
        endianness = scan.next().charAt(0);

        if (endianness == 'l') {
            endiannes = DataConverter.Endianness.LittleEndian;
        } else if (endianness == 'b') {
            endiannes = DataConverter.Endianness.BigEndian;
        }

        System.out.println("Floating point size: ");
        floatingPointSize = scan.nextInt();

        numbers = fileHandler.readNumbersFromFile(fileName);
        DataConverter dataConverter = new DataConverter(numbers, floatingPointSize, endiannes);
        List<String> parsedNumbers = dataConverter.convertNumbers();
        fileHandler.writeHexNumbersToFile(OUTPUT_FILE, parsedNumbers);

    }

}
