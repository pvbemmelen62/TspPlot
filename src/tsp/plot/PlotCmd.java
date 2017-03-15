package tsp.plot;

public class PlotCmd {

  public static void main(String[] args) {
    String algo = args[0];
    String file = args[1];
    
    String cmd = String.format(
        "-filePoints=./data/%s -fileSolution=./solution/%s/%s",
        file, algo, file);
    String[] args2 = cmd.split("\\s+");
    Plotter.main(args2);
  }
}
