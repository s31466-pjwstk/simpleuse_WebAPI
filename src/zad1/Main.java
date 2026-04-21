/**
 *
 *  @author Prokopczuk Tymoteusz s31466
 *
 */

package zad1;


public class Main {
  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();
    // ...
    // część uruchamiająca GUI
    GUI gui = new GUI(s,weatherJson,rate1,rate2);
    gui.setVisible(true);
  }
}
