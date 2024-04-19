package ilda;

import java.util.ArrayList;
import java.util.List;


public class IldaFormat {
    public static final int COORDINATE_HEADER_SIZE = 32;
    public static final int COORDINATE_DATA_SIZE = 8;
    public static final int MAX_WIDTH = 65535;
    public static final int MAX_HEIGHT = 65535;

    private List<CoordinateHeader> coordinateHeaders = new ArrayList<>();
    private boolean dirty;
    private int maxWidth;
    private int minWidth;
    private int maxHeight;
    private int minHeight;
    private int minDepth;
    private int maxDepth;

    public List<CoordinateHeader> getCoordinateHeaders() {
        return coordinateHeaders;
    }

    public void setCoordinateHeaders(List<CoordinateHeader> coordinateHeaders) {
        this.coordinateHeaders = coordinateHeaders;
    }

    public void addCoordinateHeader(final List<CoordinateHeader> coordinateHeader) {
        coordinateHeaders.addAll(coordinateHeader);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(final int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public double getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(final int minWidth) {
        this.minWidth = minWidth;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(final int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public double getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(final int minHeight) {
        this.minHeight = minHeight;
    }

    public double getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(final int minDepth) {
        this.minDepth = minDepth;
    }

    public double getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public String toString() {
        return "IldaFormat{" +
                "coordinateHeaders=" + coordinateHeaders +
                ", dirty=" + dirty +
                ", maxWidth=" + maxWidth +
                ", minWidth=" + minWidth +
                ", maxHeight=" + maxHeight +
                ", minHeight=" + minHeight +
                ", minDepth=" + minDepth +
                ", maxDepth=" + maxDepth +
                '}';
    }
}
