package ilda.tools;

import static ilda.tools.IldaReader.getInteger;

public class IntegerByteConversion {

    public static void main(String[] args) {
        int originalNumber = -211;

        // Convert integer to bytes
        byte lowByte = getLowByte(originalNumber);
        byte highByte = getHighByte(originalNumber);

        // Display the bytes
        System.out.printf("Original Number: %d\n", originalNumber);
        System.out.printf("Low Byte (in hex): %02X\n", lowByte);
        System.out.printf("High Byte (in hex): %02X\n", highByte);

        // Create a byte array to mimic receiving these bytes in an array
        byte[] bytes = {highByte, lowByte};

        // Convert bytes back to integer
        int reconstructedNumber = getInteger(bytes, 0);

        // Display the reconstructed integer
        System.out.printf("Reconstructed Number: %d\n", reconstructedNumber);

        // Check if the original number equals the reconstructed number
        System.out.println("Conversion Valid: " + (originalNumber == reconstructedNumber));
    }

    private static byte getLowByte(final int number) {
        return (byte) (number & 0xFF);
    }

    private static byte getHighByte(final int number) {
        return (byte) ((number >> 8) & 0xFF);
    }
}