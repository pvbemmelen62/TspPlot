package tsp.plot;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import tsp.*;
import tsp.Point;

public class Plotter {
  private static final String nl = System.getProperty("line.separator");
  
  private JFrame frame;
  private Canvas canvas;
  String fileName;
  Point[] points;
  BoundingBox bbox;
  Solution solution;
  private Font fontPlain;
//  private Font fontBold;
  AffineTransform Tzoom;
  AffineTransform Ttrans;
  
  public static void usage() {
    String msg =
      "java -cp bin tsp.plot.Plotter -filePoints=<fileNameP> " +
        "[-fileSolution=<fileNameS>]" + nl +
      "where" + nl +
      "  fileNameP : the points file that was input to the solver;" + nl +
      "  fileNameS : file with output from the solver" + nl +
      "where \"solver\" refers to the solver for the traveling salesman" + nl +
      "assignment, for week 4 of the coursera course \"Discrete Optimization\"."
      + nl;
    System.out.println(msg);
  }
  public static void main(String[] args) {
    // TODO: make sure that changes to points and solution are visible to
    //  event handling thread.
    Plotter plotter = new Plotter();
    for(String arg : args) {
      if (arg.startsWith("-filePoints=")) {
        String fileName = arg.substring("-filePoints=".length());
        plotter.readFilePoints(fileName);
        plotter.setFileName(fileName);
      }
      if(arg.startsWith("-fileSolution=")) {
        String fileName = arg.substring("-fileSolution=".length());
        plotter.readFileSolution(fileName);
      }
    }
    if(plotter.points==null) {
      usage();
      System.exit(1);
    }
    SwingUtilities.invokeLater(plotter.new GUIRunnable());
  }
  void setFileName(String fileName) {
    this.fileName = fileName;
  }
  private class GUIRunnable implements Runnable {
    public void run() {
      frame = new JFrame("Plotter " + fileName);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      Tzoom = new AffineTransform();
      
      Container cp = frame.getContentPane();
      canvas = new Canvas();
      cp.add(canvas, BorderLayout.CENTER);

      frame.pack();
      frame.setSize(800, 600);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    }
  }
  private class MyMouseWheelListener extends MouseAdapter {
    java.awt.Point pointDown;
    AffineTransform TzoomDown;
    
    public MyMouseWheelListener() {
      pointDown = null;
    }
    public void mouseWheelMoved(MouseWheelEvent e) {
      if(pointDown!=null) {
        return;  // don't try to handle simultaneous zoom and move.
      }
      int notches = e.getWheelRotation();
      double s = Math.pow(1.2, -notches);
      int x = e.getX();
      int y = e.getY();
      Tzoom.preConcatenate(AffineTransform.getTranslateInstance(-x, -y));
      Tzoom.preConcatenate(AffineTransform.getScaleInstance(s, s));
      Tzoom.preConcatenate(AffineTransform.getTranslateInstance(x, y));
      canvas.repaint();
    }
    @Override
    public void mousePressed(MouseEvent e) {
      pointDown = new java.awt.Point(e.getX(), e.getY());
      TzoomDown = new AffineTransform(Tzoom);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
      pointDown = null;
      TzoomDown = null;
    }
    public void mouseDragged(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      int tx = x-pointDown.x;
      int ty = y-pointDown.y;
      AffineTransform Tt = AffineTransform.getTranslateInstance(tx, ty);
      Tzoom = new AffineTransform(TzoomDown);
      Tzoom.preConcatenate(Tt);
      canvas.repaint();
    }
    public void mouseMoved(MouseEvent e) {
    }
    
  }
  @SuppressWarnings("serial")
  private class Canvas extends JPanel {
    
    Canvas() {
      MouseAdapter ma = new MyMouseWheelListener();
      addMouseWheelListener(ma);
      addMouseMotionListener(ma);
      addMouseListener(ma);
    }

    @Override
    protected void paintComponent(Graphics g) {
      int h = getHeight();
      int w = getWidth();
      Graphics2D g2 = (Graphics2D)g;
      g2.setColor(Color.WHITE);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
          RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      g2.fillRect(0,0,w,h);
      g2.setColor(Color.BLACK);
      int fontSize = 12;
      fontPlain = new Font("SansSerif", Font.PLAIN, fontSize);
//      fontBold = new Font("SansSerif", Font.BOLD, (int)(fontSize*1.2));
      g2.setFont(fontPlain);
      if(points==null) {
        return;
      }
      BoundingBox bb = BoundingBox.fromPoints(points);
      Rectangle2D.Double rectIn = new Rectangle2D.Double(
          bb.minX, bb.minY, bb.width(), bb.height());
      double m = 10; // margin
      Rectangle2D.Double rectOut = new Rectangle2D.Double(
          0+m, 0+m, w-2*m, h-2*m);
      AffineTransform T0 =
          AffineUtil.getScaleTranslateInstance(rectIn, rectOut, true);
      AffineTransform T = new AffineTransform(T0);
      T.preConcatenate(Tzoom);
      Point2D.Double ptDst = new Point2D.Double();
      g2.setColor(Color.RED);
      for(Point p : points) {
        Point2D ptSrc = new Point2D.Double(p.x, p.y);
        T.transform(ptSrc, ptDst);
        drawPoint(g2, new Point(ptDst.x, ptDst.y));
      }
      if(solution==null) {
        return;
      }
      g2.setColor(Color.BLACK);
      Stroke stroke = new BasicStroke(1.0f);
      g2.setStroke(stroke);
      int[] order = solution.order;
      Util.myAssert(order.length == points.length);
      int N = points.length;
      Point2D prev = new Point2D.Double();
      Point2D cur = new Point2D.Double();
      for(int i=0; i<order.length; ++i) {
        if(i==0) {
          Point p = points[order[i]];
          transform(T, p, prev);
        }
        Point q = points[order[(i+1)%N]];
        transform(T, q, cur);
        g2.drawLine((int)prev.getX(), (int)prev.getY(),
            (int)cur.getX(), (int)cur.getY() );
        prev.setLocation(cur);
      }
    }
  }
  private void transform(AffineTransform at, Point p, Point2D p2) {
    Point2D ptSrc = new Point2D.Double(p.x, p.y);
    at.transform(ptSrc, p2);
  }
  private void drawPoint(Graphics2D g2, Point p ) {
    fillCircle(g2, p, 2);
  }
  private void fillCircle(Graphics2D g2, Point p, double radius) {
    int x = (int)(p.x - radius);
    int y = (int)(p.y - radius);
    int width = (int)(2*radius);
    int height = (int)(2*radius);
    int startAngle = 0;
    int arcAngle = 360;
    g2.fillArc(x, y, width, height, startAngle, arcAngle);
  }
  private void readFilePoints(String fileName) {
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      ArrayList<Point> pts = Points.fromBufferedReader(br);
      points = pts.toArray(new Point[pts.size()]);
      bbox = BoundingBox.fromPoints(points);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  private void readFileSolution(String fileName) {
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      solution = Solution.fromBufferedReader(br);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
