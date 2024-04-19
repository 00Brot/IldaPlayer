package org.example;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import ilda.CoordinateHeader;
import ilda.ColorData;
import ilda.IldaFormat;
import ilda.tools.IldaReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IldaPlayer {
    static {
        // Load the EasyLase DLL from an absolute path - adjust the path as necessary
        System.load("C:\\Users\\richt\\IdeaProjects\\untitled\\src\\main\\java\\org\\example\\Easylase.dll");
    }

    public static void main(String[] args) {
        try {
            // Load the DLL for the EasyLase API
            EasyLaseControl.EasyLaseLibrary easyLase = EasyLaseControl.EasyLaseLibrary.INSTANCE;

            // Initialize the EasyLase system
            int numCards = easyLase.EasyLaseGetCardNum();
            System.out.println("Number of EasyLase cards detected: " + numCards);
            if (numCards == 0) {
                throw new RuntimeException("No EasyLase cards detected.");
            }

            // Load ILDA file and convert to frames
            List<Frame> frames = IldaFileParser.parse();

            // Play each frame
            for (Frame frame : frames) {
                // Prepare data pointer from frame data
                Pointer dataPointer = frame.getDataPointer();

                // Check if the device is ready to receive a new frame
                while (easyLase.EasyLaseGetStatus(0) != 1) {
                    Thread.sleep(10); // wait before checking again
                }

                // Send the frame data
                boolean sent = easyLase.EasyLaseWriteFrame(0, dataPointer, frame.getByteCount(), frame.getPointSpeed());
                if (!sent) {
                    System.out.println("Failed to send frame.");
                }
            }

            // Close the EasyLase device
            easyLase.EasyLaseClose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Represents a frame of laser data
    static class Frame {
        private Pointer dataPointer;
        private int byteCount;
        private int pointSpeed;

        public Frame(Pointer dataPointer, int byteCount, int pointSpeed) {
            this.dataPointer = dataPointer;
            this.byteCount = byteCount;
            this.pointSpeed = pointSpeed;
        }

        public Pointer getDataPointer() {
            return dataPointer;
        }

        public int getByteCount() {
            return byteCount;
        }

        public int getPointSpeed() {
            return pointSpeed;
        }
    }
}

class IldaFileParser {
    static List<IldaPlayer.Frame> parse() throws IOException {
        // Use IldaReader to get the IldaFormat which contains the coordinate headers
        IldaFormat ildaFormat = IldaReader.read("C:\\Users\\richt\\IdeaProjects\\untitled\\src\\main\\resources\\ildaFilesFromGif\\011_test_gifFrame.ild");
        List<CoordinateHeader> headers = ildaFormat.getCoordinateHeaders();
        List<IldaPlayer.Frame> frames = new ArrayList<>();

        for (CoordinateHeader header : headers) {
            Memory buffer = new Memory((long) header.getNumberOfRecords() * IldaFormat.COORDINATE_DATA_SIZE);
            int offset = 0;

            for (ColorData data : header.getColorData()) {
                // Set X coordinate, ensure big endian (Java default)
                buffer.setShort(offset, (short) data.getX());  // cast to short to ensure it's two bytes
                offset += 2;

                // Set Y coordinate, ensure big endian
                buffer.setShort(offset, (short) data.getY());
                offset += 2;

                // Set Status Code
                buffer.setByte(offset, (byte) data.getStatusCode());
                offset += 1;

                // Set colors in order Blue, Green, Red
                buffer.setByte(offset, (byte) data.getBlue());
                offset += 1;
                buffer.setByte(offset, (byte) data.getGreen());
                offset += 1;
                buffer.setByte(offset, (byte) data.getRed());
                offset += 1;
            }

            frames.add(new IldaPlayer.Frame(buffer, offset, calculatePointSpeed(header)));
        }

        return frames;
    }

    // A method to calculate the point speed based on some header details or constants
    private static int calculatePointSpeed(CoordinateHeader header) {
        // Here you might decide the point speed based on frame details or keep it constant
        return 30000; // Example constant point speed
    }
}