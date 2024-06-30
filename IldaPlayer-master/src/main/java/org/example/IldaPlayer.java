    package org.example;

    import com.sun.jna.Memory;
    import com.sun.jna.Pointer;
    import com.sun.jna.ptr.IntByReference;
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
            System.load("C:\\Users\\richt\\Desktop\\IldaPlayer-master\\IldaPlayer-master\\src\\main\\java\\org\\example\\Easylase.dll");
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
                IntByReference cardNumber = new IntByReference(0); // Assuming card number 0

                // Load ILDA file and convert to frames
                List<Frame> frames = IldaFileParser.parse();
                boolean loop = true;
                while(loop) {
                    // Play each frame
                    for (Frame frame : frames) {
                        // Prepare data pointer from frame data
                        easyLase = EasyLaseControl.EasyLaseLibrary.INSTANCE;
                        Pointer dataPointer = frame.getDataPointer();
                        int status = easyLase.EasyLaseGetStatus(cardNumber);
                        // Check if the device is ready to receive a new frame
                        while (status != 1) {
                            Thread.sleep(10); // wait before checking again
                            status = easyLase.EasyLaseGetStatus(cardNumber);
                        }

                        // Send the frame data
                        if (easyLase.EasyLaseWriteFrame(cardNumber, dataPointer, frame.getByteCount(), frame.getPointSpeed())) {
                            System.out.println("Frame successfully sent.");
                        } else {
                            System.out.println("Failed to send frame.");
                        }
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
            private short byteCount;
            private char pointSpeed;

            public Frame(Pointer dataPointer, short byteCount, char pointSpeed) {
                this.dataPointer = dataPointer;
                this.byteCount = byteCount;
                this.pointSpeed = pointSpeed;
            }

            public Pointer getDataPointer() {
                return dataPointer;
            }

            public short getByteCount() {
                return byteCount;
            }

            public char getPointSpeed() {
                return pointSpeed;
            }
        }
    }

    class IldaFileParser {
        static List<IldaPlayer.Frame> parse() throws IOException {
            // Use IldaReader to get the IldaFormat which contains the coordinate headers
            IldaFormat ildaFormat = IldaReader.read("C:\\Users\\richt\\Desktop\\IldaPlayer-master\\IldaPlayer-master\\src\\main\\resources\\ildaFilesFromGif\\035_test_gifFrame.ild");
            List<CoordinateHeader> headers = ildaFormat.getCoordinateHeaders();
            List<IldaPlayer.Frame> frames = new ArrayList<>();

            for (CoordinateHeader header : headers) {
                Memory buffer = new Memory((long) header.getNumberOfRecords() * IldaFormat.COORDINATE_DATA_SIZE);
                short offset = 0;

                for (ColorData data : header.getColorData()) {
                    // Set X coordinate, ensure big endian (Java default)
                    short xData = (short) (data.getX()+1000); //add to move left
                    buffer.setShort(offset, xData);  // cast to short to ensure it's two bytes
                    offset += 2;
                    short yData = (short)(data.getY()+2500); //add to move up
                    // Set Y coordinate, ensure big endian
                    buffer.setShort(offset, yData);
                    offset += 2;

                    // Set colors in order Blue, Green, Red
                    buffer.setByte(offset, (byte) data.getRed());
                    offset += 1;
                    buffer.setByte(offset, (byte) data.getGreen());
                    offset += 1;
                    buffer.setByte(offset, (byte) data.getBlue());
                    offset += 1;
                    // laser off
                    if(data.getStatusCode()==64){
                        buffer.setByte(offset, (byte) 0);
                    }
                    else{
                        //laser on
                        buffer.setByte(offset, (byte) 255);
                    }
                    offset += 1;
                }

                frames.add(new IldaPlayer.Frame(buffer, offset, calculatePointSpeed()));
            }

            return frames;
        }

        private static char calculatePointSpeed() {
            return 20000; //constant point speed
        }
        //FIX COLOR AND COORDINATE SYSTEM
    }