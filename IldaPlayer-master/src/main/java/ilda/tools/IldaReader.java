package ilda.tools;

import ilda.ColorData;
import ilda.CoordinateHeader;
import ilda.IldaFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IldaReader {

    public static IldaFormat read(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        byte[] bytes = Files.readAllBytes(path);
        return deserializeIldaFormat(bytes);
    }

    private static IldaFormat deserializeIldaFormat(byte[] bytes) {
        List<CoordinateHeader> headers = new ArrayList<>();
        int index = 0;

        while (index < bytes.length) {
            if (index + 32 > bytes.length) { // Check for just the header data
                throw new IllegalArgumentException("Insufficient data for header information");
            }

            String protocol = new String(bytes, index, 4).trim(); //not used -> always ilda
            index += 7; // Advance past protocol (4 bytes) and 3 unused bytes
            int formatCode = bytes[index++];//not used -> always 5
            String frameName = new String(bytes, index, 8).trim();
            index += 8;
            String companyName = new String(bytes, index, 8).trim();
            index += 8;

            int numberOfRecords = getInteger(bytes, index);
            index += 2;
            int frameNumber = getInteger(bytes, index);
            index += 2;
            int totalFrames = getInteger(bytes, index);
            index += 2;
            int projectorNumber = bytes[index++];
            index++; // Skip reserved byte

            if (numberOfRecords < 0) {
                throw new IllegalArgumentException("Number of records cannot be negative");
            }

            List<ColorData> colorDataList = new ArrayList<>();
            for (int i = 0; i < numberOfRecords; i++) {
                if (index + 8 > bytes.length) { // Check for each color data record
                    throw new IllegalArgumentException("Insufficient data for color data records");
                }

                int x = getInteger(bytes, index);
                index += 2;
                int y = getInteger(bytes, index);
                index += 2;
                byte statusCode = bytes[index++];
                int blue = bytes[index++] & 0xFF;
                int green = bytes[index++] & 0xFF;
                int red = bytes[index++] & 0xFF;

                ColorData data = new ColorData.Builder()
                        .setXY(x, y)
                        .setStatusCode(statusCode)
                        .setRGB(red, green, blue)
                        .build();
                colorDataList.add(data);
            }

            CoordinateHeader header = new CoordinateHeader.Builder()
                    .setFrameName(frameName)
                    .setCompanyName(companyName)
                    .setNumberOfRecords(numberOfRecords)
                    .setFrameNumber(frameNumber)
                    .setTotalFrames(totalFrames)
                    .setProjectorNumber(projectorNumber)
                    .setColorData(colorDataList)
                    .build();

            headers.add(header);
        }

        IldaFormat ildaFormat = new IldaFormat();
        ildaFormat.setCoordinateHeaders(headers);
        return ildaFormat;
    }

    public static int getInteger(byte[] bytes, int index) {
        if (index < 0 || index + 1 >= bytes.length) {
            throw new IllegalArgumentException("Index is out of valid range.");
        }
        // Cast bytes to int and perform sign extension manually if necessary
        int high = bytes[index] << 8;   // Left shift 8 bits. Still a byte shifted in int form.
        int low = bytes[index + 1] & 0xFF;  // Mask the low byte to prevent sign extension.

        // Combine the bytes. No need for further masking since high was shifted from a byte.
        return high | low;
    }
}