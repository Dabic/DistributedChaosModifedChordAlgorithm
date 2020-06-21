package app.fractals;

import app.AppConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class FractalCreator implements Runnable {
    private final AtomicBoolean working = new AtomicBoolean(false);
    private List<Point> currentWorkingPoints = new ArrayList<>();
    private List<Point> myTracePoints = new ArrayList<>();
    private List<Point> tempPoints = new ArrayList<>();
    private Point tracePoint = new Point(500, 0);
    private final Random rand = new Random();
    private final Object drawingLock = new Object();

    @Override
    public void run() {
        while (true) {
            while (working.get()) {
                int random = rand.nextInt(AppConfig.myFractalCreatorInfo.getPointCount());
                synchronized (drawingLock) {
                    drawPoint(tracePoint, currentWorkingPoints.get(random), AppConfig.myFractalCreatorInfo.getProportion());
                }
                try {
                    Thread.sleep(70);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Point findPoint(Point a, Point b, double proportion) {
        double distance = findDistance(a, b);
        double distanceAX = distance * proportion;
        double distanceXB = distance - distanceAX;
        double lambda = Math.abs(distanceXB / distanceAX);
        int x = (int) ((a.x + lambda * b.x) / (1 + lambda));
        int y = (int) ((a.y + lambda * b.y) / (1 + lambda));
        return new Point(x, y);
    }

    public void drawPoint(Point a, Point b, double proportion) {
        Point newPoint = findPoint(a, b, proportion);
        tracePoint = newPoint;
        myTracePoints.add(newPoint);
    }

    public void startWorking(List<Point> startingPoints, List<Point> tracePoints) {
        working.set(false);
        synchronized (drawingLock) {
            if (startingPoints != null) {
                currentWorkingPoints = startingPoints;
            } else {
                currentWorkingPoints = new ArrayList<>(AppConfig.myFractalCreatorInfo.getPoints());
            }
            if (tracePoints != null) {
                if (startingPoints != null) {
                    myTracePoints = findAllTracePoints(tracePoints, currentWorkingPoints);
                } else {
                    myTracePoints = tracePoints;
                }
            } else {
                myTracePoints = new ArrayList<>();
            }
            working.set(true);
        }
    }

    public void printResult(List<Point> points) {
        Thread resultPrinter = new Thread(() -> {
            BufferedImage resultImage = new BufferedImage(AppConfig.myFractalCreatorInfo.getWidth(), AppConfig.myFractalCreatorInfo.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = resultImage.createGraphics();
            graphics2D.setPaint(Color.WHITE);
            graphics2D.fillRect(0, 0, AppConfig.myFractalCreatorInfo.getWidth(), AppConfig.myFractalCreatorInfo.getHeight());
            graphics2D.setPaint(Color.BLACK);
            points.forEach(p -> graphics2D.fillOval(p.x, p.y, 5, 5));
            try {
                ImageIO.write(resultImage, "png", new File("D:\\resultImage.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AppConfig.timestampedStandardPrint("Exported result to D:\\resultImage.png ");
        });
        resultPrinter.start();
    }

    public double findDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    public List<Point> findAllTracePoints(List<Point> allPoints, List<Point> startingPoints) {
        List<Point> toReturn = new ArrayList<>();
        int[] xPoints = new int[AppConfig.myFractalCreatorInfo.getPointCount()];
        int[] yPoints = new int[AppConfig.myFractalCreatorInfo.getPointCount()];
        int i = 0;
        for (Point point : startingPoints) {
            xPoints[i] = point.x;
            yPoints[i++] = point.y;
        }
        Polygon polygon = new Polygon(xPoints, yPoints, xPoints.length);
        for (Point point : allPoints) {
            if (polygon.contains(point)) {
                toReturn.add(point);
            }
        }
        return toReturn;

        // Create a point for line segment from p to infinite
//        int counter = 0;
//        List<Point> points = new ArrayList<>();
//        Point[] polygon = new Point[startingPoints.size()];
//        for (Point p : startingPoints)
//            polygon[counter++] = p;
//        for (Point p : allPoints) {
//            Point extreme = new Point(100000, p.y);
//
//            // Count intersections of the above line
//            // with sides of polygon
//            int count = 0, i = 0;
//            do {
//                int next = (i + 1) % startingPoints.size();
//
//                // Check if the line segment from 'p' to
//                // 'extreme' intersects with the line
//                // segment from 'polygon[i]' to 'polygon[next]'
//                if (doIntersect(polygon[i], polygon[next], p, extreme)) {
//                    // If the point 'p' is colinear with line
//                    // segment 'i-next', then check if it lies
//                    // on segment. If it lies, return true, otherwise false
//                    if (orientation(polygon[i], p, polygon[next]) == 0) {
//                        if (onSegment(polygon[i], p,polygon[next])) {
//                            points.add(p);
//                        }
//                    }
//
//                    count++;
//                }
//                i = next;
//            } while (i != 0);
//
//            // Return true if count is odd, false otherwise
//            if ((count % 2 == 1)) {
//                points.add(p);
//            }
//        }
//        return points;
//    }
//
//    static boolean onSegment(Point p, Point q, Point r) {
//        if (q.x <= Math.max(p.x, r.x) &&
//                q.x >= Math.min(p.x, r.x) &&
//                q.y <= Math.max(p.y, r.y) &&
//                q.y >= Math.min(p.y, r.y)) {
//            return true;
//        }
//        return false;
//    }
//
//    // To find orientation of ordered triplet (p, q, r).
//    // The function returns following values
//    // 0 --> p, q and r are colinear
//    // 1 --> Clockwise
//    // 2 --> Counterclockwise
//    static int orientation(Point p, Point q, Point r) {
//        int val = (q.y - p.y) * (r.x - q.x)
//                - (q.x - p.x) * (r.y - q.y);
//
//        if (val == 0) {
//            return 0; // colinear
//        }
//        return (val > 0) ? 1 : 2; // clock or counterclock wise
//    }
//
//    // The function that returns true if
//    // line segment 'p1q1' and 'p2q2' intersect.
//    static boolean doIntersect(Point p1, Point q1,
//                               Point p2, Point q2) {
//        // Find the four orientations needed for
//        // general and special cases
//        int o1 = orientation(p1, q1, p2);
//        int o2 = orientation(p1, q1, q2);
//        int o3 = orientation(p2, q2, p1);
//        int o4 = orientation(p2, q2, q1);
//
//        // General case
//        if (o1 != o2 && o3 != o4) {
//            return true;
//        }
//
//        // Special Cases
//        // p1, q1 and p2 are colinear and
//        // p2 lies on segment p1q1
//        if (o1 == 0 && onSegment(p1, p2, q1)) {
//            return true;
//        }
//
//        // p1, q1 and p2 are colinear and
//        // q2 lies on segment p1q1
//        if (o2 == 0 && onSegment(p1, q2, q1)) {
//            return true;
//        }
//
//        // p2, q2 and p1 are colinear and
//        // p1 lies on segment p2q2
//        if (o3 == 0 && onSegment(p2, p1, q2)) {
//            return true;
//        }
//
//        // p2, q2 and q1 are colinear and
//        // q1 lies on segment p2q2
//        if (o4 == 0 && onSegment(p2, q1, q2)) {
//            return true;
//        }
//
//        // Doesn't fall in any of the above cases
//        return false;
    }

    public List<Point> findStartingPoints(int index, List<Point> points, double proportion) {
        List<Point> startingPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (i == index) {
                startingPoints.add(points.get(i));
            } else {
                startingPoints.add(findPoint(points.get(i), points.get(index), proportion));
            }
        }
        return startingPoints;
    }

    public List<Point> findStartingPointsForFractalId(String fractalId) {
        List<Point> myNewPoints = AppConfig.myFractalCreatorInfo.getPoints();
        for (int i = 0; i < fractalId.length() - 1; i++) {
            myNewPoints = findStartingPoints(
                    Integer.parseInt(String.valueOf(fractalId.charAt(i + 1))),
                    myNewPoints,
                    AppConfig.myFractalCreatorInfo.getProportion());
        }
        return myNewPoints;
    }

    public void deletePoints() {
        working.set(false);
        synchronized (drawingLock) {
            myTracePoints = findAllTracePoints(myTracePoints, currentWorkingPoints);
            working.set(true);
        }
    }

    public int getStatus() {
        synchronized (drawingLock) {
            return myTracePoints.size() + currentWorkingPoints.size();
        }
    }

    public void stop() {
        working.set(false);
    }
    public void start() {
        working.set(true);
    }
    public void stopWorking() {
        working.set(false);
        synchronized (drawingLock) {
            currentWorkingPoints = new ArrayList<>();
            myTracePoints = new ArrayList<>();
        }
    }

    public void setCurrentWorkingPoints(List<Point> currentWorkingPoints) {
        synchronized (drawingLock) {
            this.currentWorkingPoints = currentWorkingPoints;
        }
    }

    public List<Point> getCurrentWorkingPoints() {
        return new ArrayList<>(currentWorkingPoints);
    }

    public List<Point> getMyTracePoints() {
        synchronized (drawingLock) {
            return new ArrayList<>(myTracePoints);
        }
    }

    public List<Point> getTempPoints() {
        return tempPoints;
    }

    public void setTempPoints(List<Point> tempPoints) {
        this.tempPoints = tempPoints;
    }
}
