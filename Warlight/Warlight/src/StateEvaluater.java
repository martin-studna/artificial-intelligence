import game.Continent;
import game.Game;
import static java.lang.System.out;

public class StateEvaluater {

  private int getContinentPriority(Continent continent) {

    out.println(continent.getName());

    switch (continent.getName()) {
      case "Australia":
        return 1;
      case "South_America":
        return 2;
      case "North_America":
        return 3;
      case "Europe":
        return 4;
      case "Africa":
        return 5;
      case "Asia":
        return 6;
      default:
        return 7;
    }
  }

  public static double getValue(Game state) {

    return 0.0;
  }
}
