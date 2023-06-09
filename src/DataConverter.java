import java.util.ArrayList;
import java.util.List;

public class DataConverter {

    public enum Endianness {
        LittleEndian,
        BigEndian
    }

    enum InsertionMode {
        Pre,
        Post
    }

    int floatingPointSize;
    Endianness endianness;
    List<String> numbers;


    public DataConverter(List<String> numbers, int floatingPointSize, Endianness endianness) {
        this.numbers = numbers;
        this.floatingPointSize = floatingPointSize;
        this.endianness = endianness;
    }

    /**
     * Converts given decimal numbers to hexadecimal in given endianness
     *
     * @return Returns converted numbers
     */
    List<String> convertNumbers() {
        List<String> convertedNumbers = new ArrayList<>();
        for (String number : numbers) {
            String tempNumber;
            if (checkForUnsignedNumber(number)) {
                //TODO handle unsigned numbers
                tempNumber = decToBinary(number.substring(0, number.length() - 1));
                tempNumber = completeByte(2, tempNumber, InsertionMode.Pre);
            } else if (checkForFloatingPointNumber(number)) {
                //TODO handle floating numbers
                tempNumber = floatingToBinary(number);
            } else {
                //TODO handle signed integers
                if (number.charAt(0) == '-') {
                    tempNumber = decToBinary(number.substring(1));
                    tempNumber = completeByte(2, tempNumber, InsertionMode.Pre);
                    tempNumber = twosCompliment(tempNumber);
                } else {
                    tempNumber = decToBinary(number);
                    tempNumber = completeByte(2, tempNumber, InsertionMode.Pre);
                }
            }
            tempNumber = binaryToHex(tempNumber);
            tempNumber = hexEndianFormatter(tempNumber);
            convertedNumbers.add(tempNumber);
        }
        return convertedNumbers;
    }

    /**
     * Checks the given number's last element for unsigned token ('u')
     *
     * @param number
     * @return
     */
    boolean checkForUnsignedNumber(String number) {
        return number.charAt(number.length() - 1) == 'u';
    }

    /**
     * Checks if the given number contains a floating number token ('.')
     *
     * @param number
     * @return
     */
    boolean checkForFloatingPointNumber(String number) {
        return number.contains(".");
    }

    /**
     * Converts the given number to the appropriate endianness format
     *
     * @param hexNumber
     * @return
     */
    String hexEndianFormatter(String hexNumber) {
        StringBuilder sBuilder = new StringBuilder();

        if (endianness == Endianness.LittleEndian) {
            for (int i = hexNumber.length() - 1; i >= 1; i = i - 2)
                sBuilder.append(hexNumber, i - 1, i + 1).append(" ");
        } else if (endianness == Endianness.BigEndian) {
            for (int i = 0; i < hexNumber.length() - 1; i = i + 2)
                sBuilder.append(hexNumber, i, i + 2).append(" ");
        }
        return sBuilder.toString().trim();
    }

    /**
     * Converts a floating number to binary
     *
     * @param decNumber Number to convert
     * @return Converted Binary Number
     */
    String floatingToBinary(String decNumber) {

        int E, bias, integralSize = 0, fractionalSize = 0;
        String exp;
        String integralPart, fractionalPart;
        boolean isNegative = false;


        switch (floatingPointSize) {

            case 1:
                integralSize = 3;
                fractionalSize = 4;
                break;
            case 2:
                integralSize = 8;
                fractionalSize = 7;
                break;
            case 3:
                integralSize = 10;
                fractionalSize = 13;
                break;
            case 4:
                integralSize = 12;
                fractionalSize = 13;
                break;
        }


        if (decNumber.charAt(0) == '-') {
            isNegative = true;
            integralPart = decNumber.substring(1, decNumber.indexOf('.'));
        } else {
            integralPart = decNumber.substring(0, decNumber.indexOf('.'));
        }


        fractionalPart = decNumber.substring(decNumber.indexOf('.'));
        String temp = fractionToBinary(fractionalPart);

        integralPart = decToBinary(integralPart);


        String mantissa = integralPart.substring(1) + temp;
        if (mantissa.length() > fractionalSize) {
            mantissa = roundToEven(mantissa, fractionalSize);
        }

        E = integralPart.length() - 1;
        bias = (int) (Math.pow(2, integralSize - 1) - 1);

        exp = Integer.toString(E + bias);
        exp = (decToBinary(exp));

        StringBuilder returnBinary = new StringBuilder();
        if (isNegative) {
            returnBinary.append('1');
        } else
            returnBinary.append('0');
        returnBinary.append(exp);
        returnBinary.append(mantissa);

        return completeByte(floatingPointSize, returnBinary.toString(), InsertionMode.Post);

    }

    /**
     * Converts fractional parts to binary
     *
     * @param fraction
     * @return
     */
    String fractionToBinary(String fraction) {
        float temp = Float.parseFloat(fraction);
        StringBuilder binaryNumber = new StringBuilder();
        int maxIteration = 23;
        do {
            temp *= 2;
            binaryNumber.append((int) temp);
            temp = temp % 1;
            maxIteration--;

        } while (temp != 0 && maxIteration != 0);

        return binaryNumber.toString();
    }

    /**
     * Rounds the given binary number to the desired size using "Round to even" method
     *
     * @param binaryNumber Number to Round
     * @param roundSize
     * @return Rounded binary number
     */
    String roundToEven(String binaryNumber, int roundSize) {

        String temp = (binaryNumber + "000").substring(roundSize, roundSize + 3);
        boolean roundUp = switch (temp) {
            case "100", "101", "110", "111" -> true;
            default -> false;
        };

        if (roundUp) {
            binaryNumber = addOneToNumber(binaryNumber);
        }

        return binaryNumber;
    }

    /**
     * Adds 1 to given binary number
     *
     * @param number
     * @return Result
     */
    String addOneToNumber(String number) {

        StringBuilder result = new StringBuilder("");

        int s = 0;
        int i = number.length() - 1, j = 0;
        while (i >= 0 || j >= 0 || s == 1) {
            s += ((i >= 0) ? number.charAt(i) - '0' : 0);
            s += ((j >= 0) ? "1".charAt(j) - '0' : 0);
            result.append((char) (s % 2 + '0'));
            s /= 2;
            i--;
            j--;
        }
        int start = result.length() - 1;

        while (start >= 0 && result.charAt(start) == '0') {
            start--;
        }

        if (start != result.length() - 1) {
            result.delete(start + 1, result.length());
        }

        return result.reverse().toString();
    }


    /**
     * Adds '0' up to the desired bit by looking at the insertion mode to the beginning or end of the given binary number
     *
     * @param numberOfByte Final length of the binary number
     * @param number
     * @param mode Mode of insertion
     * @return Completed binary number
     */
    String completeByte(int numberOfByte, String number, InsertionMode mode) {
        StringBuilder completedNumber = new StringBuilder();
        int numberOfZeros;
        numberOfZeros = (numberOfByte * 8) - number.length();
        if (mode == InsertionMode.Pre) {
            completedNumber.append("0".repeat(Math.max(0, numberOfZeros)));
            completedNumber.append(number);
        } else if (mode == InsertionMode.Post) {
            completedNumber.append(number);
            completedNumber.append("0".repeat(Math.max(0, numberOfZeros)));
        }
        return completedNumber.toString();
    }


    /**
     * Takes given number's two's complement
     * @param binaryNumber
     * @return
     */
    String twosCompliment(String binaryNumber) {
        StringBuilder binaryString = new StringBuilder(binaryNumber);
        boolean firstOneFound = false;
        for (int index = binaryString.length() - 1; index >= 0; index--) {
            if (!firstOneFound && binaryString.charAt(index) == '1') {
                firstOneFound = true;
            } else if (firstOneFound) {
                binaryString.setCharAt(index, flip(binaryString.charAt(index)));
            }
        }
        return binaryString.toString();
    }

    /**
     * Returns the inverse of the given bit
     * @param c
     * @return
     */
    char flip(char c) {
        return (c == '0') ? '1' : '0';
    }

    /**
     * Converts the given decimal number to binary
     * @param sNumber
     * @return
     */
    String decToBinary(String sNumber) {
        StringBuilder binary = new StringBuilder();
        int number = Integer.parseInt(sNumber);
        while (number > 0) {
            binary.insert(0, (number % 2));
            number /= 2;
        }
        return binary.toString();
    }

    /**
     * Converts the given binary number to hexadecimal
     * @param number
     * @return
     */
    String binaryToHex(String number) {
        StringBuilder hexNumber = new StringBuilder();
        int fourBitHolder;
        for (int i = 0; i < number.length() / 4; i++) {
            int sum = 0;
            fourBitHolder = Integer.parseInt(number.substring(i * 4, i * 4 + 4));

            int digitCounter = 0;
            while (fourBitHolder > 0) {
                if (fourBitHolder % 10 == 1) {
                    sum += ((int) (Math.pow(2, digitCounter)));
                }
                digitCounter++;
                fourBitHolder /= 10;

            }
            if (sum > 9) {
                char c = (char) (55 + sum); // ASCII : 65 => A  so if sum is over 9, we add it to 55
                hexNumber.append(c);
            } else {
                hexNumber.append(sum);
            }
        }

        return hexNumber.toString();

    }
}

