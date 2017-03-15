package tsp;

public class Point {

  public double x;
  public double y;
  
  /** Point.
   * @param x double
   * @param y double
   */
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }
  @Override
  public String toString() {
    return "Point [x=" + x + ", y=" + y + "]";
  }
  public double distance(Point p) {
    Point that = p;
    double rv = Math.sqrt(
        Util.sqr(this.x-that.x) + Util.sqr(this.y-that.y) );
    return rv;
  }
  public double distanceSqr(Point p) {
    Point that = p;
    double rv = Util.sqr(this.x-that.x) + Util.sqr(this.y-that.y);
    return rv;
  }
}
