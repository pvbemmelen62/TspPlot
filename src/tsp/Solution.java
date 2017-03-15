package tsp;

import java.io.*;
import java.util.*;

public class Solution {

  public Point[] points;
  public int[] order;
  public Double distance;
  public boolean isOptimal;
  
  public static Solution fromBufferedReader(BufferedReader reader)
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
    String[] words;
    // first line
    words = lines.get(0).split("\\s+");
    double distance = Double.parseDouble(words[0]);
    boolean isOptimal = Integer.parseInt(words[1]) > 0 ? true : false;

    // second line
    words = lines.get(1).split("\\s+");
    int[] order = new int[words.length];
    for(int i=0; i<words.length; ++i) { 
      order[i] = Integer.parseInt(words[i]);
    }
    
    Solution sol = new Solution(null, order, isOptimal, distance);
    return sol;
  }

  public Solution(Point[] points, int[] order, boolean isOptimal,
      Double distance) {
    this.points = points;
    this.order = order;
    this.isOptimal = isOptimal;
    this.distance = distance;
  }
  public double calcDistance() {
    double dist = 0;
    int N = points.length;
    for(int i=0; i<N; ++i) {
      Point p0 = points[order[i]];
      Point p1 = points[order[(i+1)%N]];
      dist += p0.distance(p1);
    }
    return dist;
  }
  public String toString() {
    String rv = "{" +
        "points.length:" + points.length +
      ", order.length:" + order.length +
      ", isOptimal:" + isOptimal +
      ", distance:" + distance +
      "}";
    return rv;
  }
}
