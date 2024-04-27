package ilda.debugging;

import ilda.ColorData;
import ilda.CoordinateHeader;
import ilda.IldaFormat;
import ilda.tools.IldaReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IldaFileValidator {
    private static final String fileToValidate = ".\\src\\main\\resources\\ildaFilesFromGif\\011_test_gifFrame.ild";

    public static void main(String[] args) {
        try {
            List<String> errors = validateIldaFile(fileToValidate);
            if (errors.isEmpty()) {
                System.out.println("Validation complete. No errors found.");
            } else {
                System.out.println("Validation complete. Errors found:");
                errors.forEach(System.out::println);
            }
            outputPoints(fileToValidate);
        } catch (IOException e) {
            System.out.println("Failed to read the file: " + e.getMessage());
        }
    }

    public static List<String> validateIldaFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }

        IldaFormat ildaFormat = IldaReader.read(filePath);
        List<String> errors = new ArrayList<>();

        ildaFormat.getCoordinateHeaders().forEach(header -> {
            try {
                validateCoordinateHeader(header);
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        });

        return errors;
    }

    private static void validateCoordinateHeader(CoordinateHeader header) {
        if (header.getNumberOfRecords() < 0) {
            throw new IllegalArgumentException("Number of records cannot be negative in header: " + header.getFrameName());
        }

        header.getColorData().forEach(data -> {
            try {
                validateColorData(data);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error in frame '" + header.getFrameName() + "': " + e.getMessage());
            }
        });
    }

    private static void validateColorData(ColorData data) {
        if (data.getX() < ColorData.minValue || data.getX() > ColorData.maxValue ||
                data.getY() < ColorData.minValue || data.getY() > ColorData.maxValue) {
            throw new IllegalArgumentException("Coordinate values (" + data.getX() + ", " + data.getY() + ") are out of the permitted range.");
        }

        if (data.getRed() < 0 || data.getRed() > 255 ||
                data.getGreen() < 0 || data.getGreen() > 255 ||
                data.getBlue() < 0 || data.getBlue() > 255) {
            throw new IllegalArgumentException("RGB color values (" + data.getRed() + ", " + data.getGreen() + ", " + data.getBlue() + ") must be between 0 and 255.");
        }
    }

    private static void outputPoints(String filePath) throws IOException {
        IldaFormat ildaFormat = IldaReader.read(filePath);
        int countLastPoint = 0;
        int countBlanking = 0;

        System.out.println("Outputting points from the file:");
        for (CoordinateHeader header : ildaFormat.getCoordinateHeaders()) {
            for (ColorData data : header.getColorData()) {
                System.out.println("x:" + data.getX() + ", y:" + data.getY());
                if ((data.getStatusCode() & 0x80) != 0) { // Check last point bit
                    countLastPoint++;
                }
                if ((data.getStatusCode() & 0x40) != 0) { // Check blanking bit
                    countBlanking++;
                }
            }
        }

        System.out.println("Total points with Last Point bit set: " + countLastPoint);
        System.out.println("Total points with Blanking bit set: " + countBlanking);
    }
}