package ilda;

/**
 * Represents a single color data point for ILDA files.
 * This class encapsulates the coordinates, status code, and RGB color values
 * for a point in an ILDA image. It uses a builder pattern for construction
 * to ensure all values are valid upon creation.
 */
public class ColorData {
    public static final int minValue = -32768;
    public static final int maxValue = 32767;
    //Not used at the moment, but could become important later
    public static final byte LAST_POINT_BIT_MASK = (byte) 0x80;  //Bit 7 Explicitly casting, as 0x80 is 128 in decimal, becomes -128 in byte.
    public static final byte BLANKING_BIT_MASK = 0x40;           //Bit 6 No cast needed, 0x40 is 64 in decimal, within byte range.
    private int x;

    private int y;
    private int statusCode;
    private int red;
    private int green;
    private int blue;

    private ColorData(int x, int y, int statusCode, int red, int green, int blue) {
        this.x = x;
        this.y = y;
        this.statusCode = statusCode;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public static class Builder {
        private int x;
        private int y;
        private int red;
        private int green;
        private int blue;
        private byte statusCode = 0;  // Start with all bits cleared

        public Builder setXY(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder setStatusCode(byte statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder setLastPoint(boolean isLastPoint) {
            // Set or clear the last point bit
            if (isLastPoint) {
                this.statusCode |= LAST_POINT_BIT_MASK;
            } else {
                this.statusCode = 0;
            }
            return this;
        }

        public Builder setBlanking(boolean isBlanked) {
            // Set or clear the blanking bit
            if (isBlanked) {
                this.statusCode |= BLANKING_BIT_MASK;
                // Ensure RGB values are set to zero if blanking is true
                this.red = 0;
                this.green = 0;
                this.blue = 0;
            } else {
                this.statusCode = 0;
            }
            return this;
        }

        public Builder setRGB(int red, int green, int blue) {
            // Validate and set RGB values only if blanking bit is not set
            if ((this.statusCode & BLANKING_BIT_MASK) == 0) {
                validateColors(red, green, blue);
                this.red = red;
                this.green = green;
                this.blue = blue;
            }
            return this;
        }

        public ColorData build() {
            return new ColorData(x, y, statusCode, red, green, blue);
        }

        private void validateColors(int red, int green, int blue) {
            if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
                throw new IllegalArgumentException("Color values must be between 0 and 255.");
            }
        }
    }
}