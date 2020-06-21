package app.fractals;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FractalCreatorInfo implements Serializable {
    private static final long serialVersionUID = -7985880321247695942L;
    private String jobName;
    private int pointCount;
    private double proportion;
    private int width;
    private int height;
    private List<Point> points;
    private boolean working;
    private final AtomicInteger mergePoints = new AtomicInteger(0);
    private final AtomicInteger regularPoints = new AtomicInteger(0);

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public String getJobName() {
        return jobName;
    }

    public int getPointCount() {
        return pointCount;
    }

    public double getProportion() {
        return proportion;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public void setProportion(double proportion) {
        this.proportion = proportion;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public boolean isWorking() {
        return working;
    }

    public int getMergeCount() {
        return mergePoints.get();
    }

    public void incrementMergeCount() {
        mergePoints.getAndIncrement();
    }
    public int getRegularCount() {
        return regularPoints.get();
    }
    public void incrementRegularCount() {
        regularPoints.getAndIncrement();
    }
    @Override
    public String toString() {
        return "FractalCreatorInfo{" +
                "jobName='" + jobName + '\'' +
                ", pointCount=" + pointCount +
                ", proportion=" + proportion +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
