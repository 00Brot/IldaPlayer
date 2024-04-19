package ilda;


import java.util.ArrayList;
import java.util.List;

public class CoordinateHeader {
    private final String protocol = "ILDA";
    private final int formatCode = 5;
    private final String companyName;
    private final String frameName;
    private final int numberOfRecord;
    private final int frameNumber;
    private final int totalFramesOrZero;
    private final int projectorNumber;
    private List<ColorData> colorData;

    // Private constructor to enforce use of the builder
    private CoordinateHeader(String frameName, int numberOfRecord, int frameNumber, int totalFramesOrZero,
                             String companyName, int projectorNumber, List<ColorData> colorData) {
        this.frameName = frameName;
        this.numberOfRecord = numberOfRecord;
        this.frameNumber = frameNumber;
        this.totalFramesOrZero = totalFramesOrZero;
        this.companyName = companyName;
        this.projectorNumber = projectorNumber;
        this.colorData = colorData;
    }

    public void addColorData(List<ColorData> colorData) {
        if (this.colorData == null) {
            this.colorData = new ArrayList<>();
        }
        this.colorData.addAll(colorData);
    }

    public void addSingleColorData(ColorData colorData) {
        if (this.colorData == null) {
            this.colorData = new ArrayList<>();
        }
        this.colorData.add(colorData);
    }

    public String getProtocol() {
        return protocol;
    }

    public int getFormatCode() {
        return formatCode;
    }

    public String getFrameName() {
        return frameName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getNumberOfRecords() {
        return numberOfRecord;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public int getTotalFramesOrZero() {
        return totalFramesOrZero;
    }

    public int getProjectorNumber() {
        return projectorNumber;
    }

    public List<ColorData> getColorData() {
        return colorData;
    }

    @Override
    public String toString() {
        return "CoordinateHeader{" +
                "protocol='" + protocol + '\'' +
                ", formatCode=" + formatCode +
                ", companyName='" + companyName + '\'' +
                ", projectorNumber=" + projectorNumber +
                ", frameName='" + frameName + '\'' +
                ", numberOfRecord=" + numberOfRecord +
                ", frameNumber=" + frameNumber +
                ", totalFramesOrZero=" + totalFramesOrZero +
                ", colorData=" + colorData +
                '}';
    }

    public static class Builder {
        private String frameName;
        private int numberOfRecord;
        private int frameNumber;
        private int totalFramesOrZero;
        private String companyName;
        private int projectorNumber;
        private List<ColorData> colorData;

        public Builder setFrameName(String frameName) {
            if (frameName.length() > 9) {
                throw new IllegalArgumentException("FrameName has to have less then 9 chars");
            }
            this.frameName = frameName;
            return this;
        }

        public Builder setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder setNumberOfRecords(int numberOfRecord) {
            this.numberOfRecord = numberOfRecord;
            return this;
        }

        public Builder setFrameNumber(int frameNumber) {
            this.frameNumber = frameNumber;
            return this;
        }

        public Builder setTotalFrames(int totalFramesOrZero) {
            this.totalFramesOrZero = totalFramesOrZero;
            return this;
        }

        public Builder setProjectorNumber(int projectorNumber) {
            this.projectorNumber = projectorNumber;
            return this;
        }

        public Builder setColorData(List<ColorData> colorData) {
            this.colorData = colorData;
            return this;
        }

        public CoordinateHeader build() {
            return new CoordinateHeader(frameName, numberOfRecord, frameNumber, totalFramesOrZero,
                    companyName, projectorNumber, colorData);
        }
    }
}