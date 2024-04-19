package ilda.debugging;

import ilda.ColorData;
import ilda.CoordinateHeader;
import ilda.IldaFormat;
import ilda.tools.IldaReader;

import java.io.IOException;
import java.util.List;

public class IldaBlinkingComparator {

    public static void compareIldaFiles(String filePath1, String filePath2) throws IOException {
        IldaFormat ildaFile1 = IldaReader.read(filePath1);
        IldaFormat ildaFile2 = IldaReader.read(filePath2);

        List<CoordinateHeader> headers1 = ildaFile1.getCoordinateHeaders();
        List<CoordinateHeader> headers2 = ildaFile2.getCoordinateHeaders();

        for (int i = 0; i < headers1.size(); i++) {
            List<ColorData> data1 = headers1.get(i).getColorData();
            List<ColorData> data2 = headers2.get(i).getColorData();

            for (int j = 0; j < data1.size(); j++) {
                boolean blinking1 = (data1.get(j).getStatusCode() & 0x40) != 0;
                boolean blinking2 = (data2.get(j).getStatusCode() & 0x40) != 0;

                if (blinking1 != blinking2) {
                    System.out.println("Blinking bit mismatch at Frame " + i + " Point " + j);
                }
            }
        }
    }
}