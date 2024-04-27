package ilda.debugging;

import ilda.ColorData;
import ilda.CoordinateHeader;
import ilda.IldaFormat;
import ilda.tools.IldaReader;

import java.io.IOException;
import java.util.List;

public class IldaDataViewer {
    public static void main(String[] args) {
        String filePath = ".\\src\\main\\resources\\ildaFilesFromGif\\009_test_gifFrame.ild";
        try {
            IldaFormat ildaFormat = IldaReader.read(filePath);
            displayIldaFormat(ildaFormat);
        } catch (IOException e) {
            System.out.println("Error reading the ILD file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error processing the ILD file: " + e.getMessage());
        }
    }

    private static void displayIldaFormat(IldaFormat ildaFormat) {
        List<CoordinateHeader> headers = ildaFormat.getCoordinateHeaders();
        if (headers.isEmpty()) {
            System.out.println("No headers found in the file.");
            return;
        }

        for (CoordinateHeader header : headers) {
            System.out.println("=======================================");
            System.out.println("Frame Name: " + header.getFrameName());
            System.out.println("Company Name: " + header.getCompanyName());
            System.out.println("Number of Records: " + header.getNumberOfRecords());
            System.out.println("Frame Number: " + header.getFrameNumber());
            System.out.println("Total Frames: " + header.getTotalFramesOrZero());
            System.out.println("Projector Number: " + header.getProjectorNumber());
            System.out.println("---------------------------------------");

            List<ColorData> colorDataList = header.getColorData();
            for (ColorData colorData : colorDataList) {
                System.out.printf("Point (%d, %d): R=%d, G=%d, B=%d, Status=%d%n",
                        colorData.getX(), colorData.getY(),
                        colorData.getRed(), colorData.getGreen(), colorData.getBlue(),
                        colorData.getStatusCode());
            }
            System.out.println("=======================================");
        }
    }
}