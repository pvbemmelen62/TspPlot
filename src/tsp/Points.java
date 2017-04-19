package tsp;

import java.io.*;
import java.util.*;

public class Points {

  public static ArrayList<Point> fromBufferedReader(BufferedReader reader)
      throws IOException {
    ArrayList<String> lines = new ArrayList<>();
    try {
      String line = null;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }
    finally {
      reader.close();
    }

    // parse the data in the file
    String word = lines.get(0).trim();
    int numPoints = Integer.parseInt(word);

    ArrayList<Point> points = new ArrayList<>(numPoints);
    
    String[] words = null;
    for(int i=0; i<numPoints; ++i) {
      words = lines.get(i+1).split("\\s+");
      double x = Double.parseDouble(words[0]);
      double y = Double.parseDouble(words[1]);
      points.add(new Point(x,y));
    }
    return points;
  }
  /** Translates each point over (tx,ty). */
  public static void translate(double tx, double ty, Point[] points) {
    for(Point p : points) {
      p.x += tx;
      p.y += ty;
    }
  }
}
