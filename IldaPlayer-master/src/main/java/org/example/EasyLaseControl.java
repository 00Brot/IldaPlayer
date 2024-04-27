package org.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

public class EasyLaseControl {
    static {
        // Load the EasyLase DLL from an absolute path - adjust the path as necessary
        System.load("C:\\Users\\richt\\Desktop\\IldaPlayer-master\\IldaPlayer-master\\src\\main\\java\\org\\example\\Easylase.dll");
    }

    public interface EasyLaseLibrary extends Library {
        // Load the library directly using the DLL name if it's in the system path
        EasyLaseLibrary INSTANCE = Native.load("EasyLase", EasyLaseLibrary.class);

        int EasyLaseGetCardNum();
        boolean EasyLaseClose();
        int EasyLaseGetStatus(IntByReference cardNumber);
        boolean     EasyLaseWriteFrame(IntByReference cardNumber, Pointer dataPointer, short byteCount, char pointSpeed);
        boolean EasyLaseWriteFrameNR(int cardNumber, Pointer dataPointer, int byteCount, int pointSpeed, int repNum);
        boolean EasyLaseStop(int cardNumber);
        boolean EasyLaseGetDebugInfo(IntByReference cardNumber, Pointer dataPointer, short count);

    }

    public static void main(String[] args) {
        // Using the INSTANCE as it should be loaded
        EasyLaseLibrary easyLase = EasyLaseLibrary.INSTANCE;

        // Example usage
        int numCards = easyLase.EasyLaseGetCardNum();
        System.out.println("Number of cards detected: " + numCards);
    }
}