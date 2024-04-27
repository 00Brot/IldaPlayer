import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.example.EasyLaseControl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EasyLaseLibraryTest {

    @Test
    public void testEasyLaseWriteFrame() {
        System.load("C:\\Users\\richt\\Desktop\\IldaPlayer-master\\IldaPlayer-master\\src\\main\\java\\org\\example\\Easylase.dll");
        EasyLaseControl.EasyLaseLibrary easyLase = EasyLaseControl.EasyLaseLibrary.INSTANCE;
        IntByReference cardNumber = new IntByReference(0);
        IntByReference numberOfPoints = new IntByReference(100);
        short byteCount = (short) (numberOfPoints.getValue() * 8);
        char pointSpeed = 30000;

        Memory dataPointer = new Memory(byteCount);
        for (int i = 0; i < byteCount; i++) {
            dataPointer.setByte(i, (byte) (i % 256));
        }

        boolean result = easyLase.EasyLaseWriteFrame(cardNumber, dataPointer, byteCount, pointSpeed);
        easyLase.EasyLaseGetDebugInfo(cardNumber, dataPointer, byteCount);
        byte[] debugData = new byte[byteCount];
        Memory debugInfo = new Memory(byteCount);
        debugInfo.read(0, debugData, 0, byteCount);
        // Here you can add code to interpret or display the debug data as needed
        for (byte b : debugData) {
            System.out.printf("%02X ", b);
        }
        easyLase.EasyLaseClose();
        System.out.println("EasyLaseWriteFrame result: " + result); // Logging the result for debugging
        assertTrue(result, "EasyLaseWriteFrame should return true indicating success");
    }

    @Test
    public void testEasyLaseWriteFrameWithInvalidData() {
        // Load the DLL interface
        System.load("C:\\Users\\richt\\Desktop\\IldaPlayer-master\\IldaPlayer-master\\src\\main\\java\\org\\example\\Easylase.dll");

        EasyLaseControl.EasyLaseLibrary easyLase = EasyLaseControl.EasyLaseLibrary.INSTANCE;

        // Example card number
        IntByReference cardNumber = new IntByReference(0);

        // Create dummy data with incorrect format or size
        short byteCount = 1024;  // Incorrect size not matching point count * 8
        char pointSpeed = 30000;  // Example point speed

        // Allocate memory and fill it with dummy data
        Memory dataPointer = new Memory(byteCount);
        for (int i = 0; i < byteCount; i++) {
            dataPointer.setByte(i, (byte) (i % 256));  // Fill with some pattern
        }

        // Call the function with incorrect data
        boolean result = easyLase.EasyLaseWriteFrame(cardNumber, dataPointer, byteCount, pointSpeed);
        easyLase.EasyLaseClose();

        // Assert that the result is false (assuming failure due to data size mismatch)
        assertFalse(result, "EasyLaseWriteFrame should return false indicating failure due to incorrect data size");
    }
    @Test
    public void testEasyLaseGetDebugInfo() {
        System.load("C:\\Users\\richt\\Desktop\\IldaPlayer-master\\IldaPlayer-master\\src\\main\\java\\org\\example\\Easylase.dll");
        EasyLaseControl.EasyLaseLibrary easyLase = EasyLaseControl.EasyLaseLibrary.INSTANCE;
        int cardNumber = 0;  // Example card number
        short byteCount = 512;  // Define the byte count based on expected debug data size

        // Allocate memory for the debug info
        Memory debugInfo = new Memory(byteCount);

        // Call the debug function
        boolean result = easyLase.EasyLaseGetDebugInfo(new IntByReference(cardNumber), debugInfo, byteCount);
        easyLase.EasyLaseClose();

        assertTrue(result, "EasyLaseGetDebugInfo should return true indicating success");
    }
}

