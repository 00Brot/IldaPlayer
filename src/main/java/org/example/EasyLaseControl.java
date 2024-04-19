package org.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class EasyLaseControl {
    static {
        // Load the EasyLase DLL from an absolute path - adjust the path as necessary
        System.load("C:\\Users\\richt\\IdeaProjects\\untitled\\src\\main\\java\\org\\example\\Easylase.dll");
    }

    public interface EasyLaseLibrary extends Library {
        // Load the library directly using the DLL name if it's in the system path
        EasyLaseLibrary INSTANCE = Native.load("EasyLase", EasyLaseLibrary.class);

        int EasyLaseGetCardNum();
        boolean EasyLaseClose();
        int EasyLaseGetStatus(int cardNumber);
        boolean EasyLaseWriteFrame(int cardNumber, Pointer dataPointer, int byteCount, int pointSpeed);
        boolean EasyLaseWriteFrameNR(int cardNumber, Pointer dataPointer, int byteCount, int pointSpeed, int repNum);
        boolean EasyLaseStop(int cardNumber);
        boolean EasyLaseWriteDMX(int cardNumber, Pointer dmxBuffer);
        boolean EasyLaseDMXOut(int cardNumber, Pointer dataPointer, int baseAddress, int channelCount);
    }

    public static void main(String[] args) {
        // Using the INSTANCE as it should be loaded
        EasyLaseLibrary easyLase = EasyLaseLibrary.INSTANCE;

        // Example usage
        int numCards = easyLase.EasyLaseGetCardNum();
        System.out.println("Number of cards detected: " + numCards);
    }
}