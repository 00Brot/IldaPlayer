package ilda.tools;

import ilda.ColorData;
import ilda.CoordinateHeader;
import ilda.IldaFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class IldaWriter {

    public void write(final IldaFormat ildaFormat, final String fileName) throws IOException {
        final Path path = Paths.get(fileName);
        final int totalByteSize = getTotalByteSize(ildaFormat);
        final byte[] bytes = new byte[totalByteSize];
        fillBytesWithCoordinate(bytes, ildaFormat.getCoordinateHeaders());
        Files.write(path, bytes);
    }

    private void fillBytesWithCoordinate(byte[] bytes, List<CoordinateHeader> coordinateHeaders) {
        int index = 0;
        try {
            for (CoordinateHeader coordinateHeader : coordinateHeaders) {
                String protocol = String.format("%-4s", coordinateHeader.getProtocol()).substring(0, 4);
                String frameName = String.format("%-8s", coordinateHeader.getFrameName()).substring(0, 8);
                String companyName = String.format("%-8s", coordinateHeader.getCompanyName()).substring(0, 8);

                for (int i = 0; i < 4; i++) bytes[index++] = (byte) protocol.charAt(i);
                for (int i = 0; i < 3; i++) bytes[index++] = 0; //fill in 3 empty byte
                bytes[index++] = (byte) coordinateHeader.getFormatCode();
                for (int i = 0; i < 8; i++) bytes[index++] = (byte) frameName.charAt(i);
                for (int i = 0; i < 8; i++) bytes[index++] = (byte) companyName.charAt(i);

                bytes[index++] = getHighByte(coordinateHeader.getNumberOfRecords());
                bytes[index++] = getLowByte(coordinateHeader.getNumberOfRecords());
                bytes[index++] = getHighByte(coordinateHeader.getFrameNumber());
                bytes[index++] = getLowByte(coordinateHeader.getFrameNumber());
                bytes[index++] = getHighByte(coordinateHeader.getTotalFramesOrZero());
                bytes[index++] = getLowByte(coordinateHeader.getTotalFramesOrZero());
                bytes[index++] = getHighByte(coordinateHeader.getProjectorNumber());
                bytes[index++] = 0;  // Reserved future use. Must be 0

                for (ColorData colorData : coordinateHeader.getColorData()) {
                    bytes[index++] = getHighByte(colorData.getX());
                    bytes[index++] = getLowByte(colorData.getX());
                    bytes[index++] = getHighByte(colorData.getY());
                    bytes[index++] = getLowByte(colorData.getY());
                    bytes[index++] = (byte) (colorData.getStatusCode());
                    bytes[index++] = (byte) colorData.getBlue();
                    bytes[index++] = (byte) colorData.getGreen();
                    bytes[index++] = (byte) colorData.getRed();
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("Attempting to write beyond array bounds at index: " + index + " with array length: " + bytes.length);
        }
    }

    private byte getLowByte(final int number) {
        return (byte) (number & 0xFF);
    }

    private byte getHighByte(final int number) {
        return (byte) ((number >> 8) & 0xFF);
    }

    private int getTotalByteSize(final IldaFormat ildaFormat) {
        int size = 0;
        for (CoordinateHeader coordinateHeader : ildaFormat.getCoordinateHeaders()) {
            size += IldaFormat.COORDINATE_HEADER_SIZE + (coordinateHeader.getNumberOfRecords() * IldaFormat
                    .COORDINATE_DATA_SIZE);
        }
        return size;
    }
}
