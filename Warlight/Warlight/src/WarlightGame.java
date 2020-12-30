import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import game.Continent;
import game.Game;
import game.Phase;
import game.Region;
import game.move.AttackTransfer;
import game.move.AttackTransferMove;
import game.move.Move;
import game.move.PlaceArmies;
import game.move.PlaceArmiesMove;
import minimax.HeuristicGame;
import static java.lang.System.out;

public class WarlightGame implements HeuristicGame<Game, Move> {

  Random random = new Random(0);
  private Game game;

  public WarlightGame(int seed) {
    this.random = seed >= 0 ? new Random(seed) : new Random();
  }

  public WarlightGame() {
  }

  boolean isBorder(Game game, Region r) {
    int me = game.currentPlayer();
    for (Region s : r.getNeighbors())
      if (game.getOwner(s) != me)
        return true;

    return false;
  }

  boolean isEnemyBorder(Game game, Region r) {
    int me = game.currentPlayer();
    for (Region s : r.getNeighbors())
      if (game.getOwner(s) != me && game.getOwner(s) != 0)
        return true;

    return false;
  }

  public int getcontinentPriority(Continent continent) {

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

  @Override
  public Game initialState(int seed) {
    return null;
  }

  @Override
  public Game clone(Game state) {
    return state.clone();
  }

  @Override
  public int player(Game state) {
    return state.currentPlayer();
  }

  @Override
  public List<Move> actions(Game state) {
    var moves = new ArrayList<Move>();
    int me = state.currentPlayer();
    int opponent = me == 1 ? 2 : 1;

    if (state.getPhase() == Phase.PLACE_ARMIES) {

      int available = state.armiesPerTurn(me);

      List<Region> mine = state.regionsOwnedBy(me);

      Continent c = mine.get(0).getContinent();
      for (Region r : mine) {
        Continent c1 = r.getContinent();
        if (state.getOwner(c1) != me && (state.getOwner(c) == me || c1.getReward() < c.getReward()))
          c = c1;
      }

      ArrayList<Region> dest = new ArrayList<Region>();
      for (Region r : mine) {
        Continent c1 = r.getContinent();
        if (isEnemyBorder(state, r) && (c1 == c || state.getOwner(c1) == me))
          dest.add(r);
      }
      if (dest.isEmpty())
        for (Region r : mine)
          if (r.getContinent() == c && isBorder(state, r))
            dest.add(r);
      if (dest.isEmpty())
        dest = new ArrayList<Region>(mine);

      int[] count = new int[dest.size() + 1];
      count[0] = 0;
      count[1] = available;
      for (int i = 2; i < count.length; ++i)
        count[i] = random.nextInt(available + 1);
      Arrays.sort(count);

      List<PlaceArmies> ret = new ArrayList<PlaceArmies>();
      int i = 0;
      for (Region r : dest) {
        int n = count[i + 1] - count[i];
        if (n > 0)
          ret.add(new PlaceArmies(r, n));
        i += 1;
      }

      moves.add(new PlaceArmiesMove(ret));

      // int available = state.armiesPerTurn(me);

      // List<Region> mine = state.regionsOwnedBy(me);
      // int numRegions = mine.size();

      // int[] count = new int[numRegions];

      // Region region = null;
      // int max = 0;

      // List<PlaceArmies> retPlace = new ArrayList<PlaceArmies>();

      // // retPlace.add(new PlaceArmies(region, available));

      // for (int i = 0; i < numRegions; ++i) {
      // if (state.getArmies(mine.get(i)) >= max) {
      // region = mine.get(i);
      // max = state.getArmies(mine.get(i));
      // }
      // }

      // retPlace.add(new PlaceArmies(region, available));

      // moves.add(new PlaceArmiesMove(retPlace));

      // for (Region r : state.regionsOwnedBy(me)) {
      // max = 0;
      // for (Region n : r.getNeighbors()) {
      // if (state.getOwner(n) != state.getOwner(r) && max < state.getArmies(n)) {
      // max = state.getArmies(n);
      // }
      // }

      // if (max > 0) {
      // retPlace.clear();
      // retPlace.add(new PlaceArmies(r, available));
      // moves.add(new PlaceArmiesMove(retPlace));
      // }
      // }
    }

    if (state.getPhase() == Phase.ATTACK_TRANSFER) {

      // int me = game.currentPlayer();
      List<AttackTransfer> ret = new ArrayList<AttackTransfer>();

      for (Region from : state.regionsOwnedBy(me)) {
        ArrayList<Region> neighbors = new ArrayList<Region>(from.getNeighbors());
        Collections.shuffle(neighbors, random);
        Region to = null;
        for (Region n : neighbors) {
          if (to == null || priority(state, from, n) > priority(state, from, to))
            to = n;
        }

        int min = state.getOwner(to) == me ? 1 : (int) Math.ceil(state.getArmies(to) * 1.5);
        int max = state.getArmies(from) - 1;

        if (min <= max)
          ret.add(new AttackTransfer(from, to, max));
      }

      moves.add(new AttackTransferMove(ret));

      // List<AttackTransfer> transfers = new ArrayList<AttackTransfer>();

      // AttackTransferTactics.transferArmies(state, transfers);
      // // AttackTransferTactics.transferArmies2(state, moves);
      // moves.add(new AttackTransferMove(transfers));

      // for (Region r : state.regionsOwnedBy(opponent)) {
      // List<AttackTransfer> retAttack2 = new ArrayList<AttackTransfer>();
      // List<Region> neighbors = r.getNeighbors();

      // for (Region from : neighbors) {
      // if (state.getOwner(from) != me)
      // continue;

      // int army = state.getArmies(from);
      // if (army > 1) {
      // retAttack2.add(new AttackTransfer(from, r, state.getArmies(from) - 1));
      // }

      // }

      // if (retAttack2.size() > 0) {
      // moves.add(new AttackTransferMove(retAttack2));
      // var join = Stream.concat(retAttack2.stream(),
      // transfers.stream()).collect(Collectors.toList());
      // moves.add(new AttackTransferMove(join));
      // }
      // }

      // List<AttackTransfer> retAttack = new ArrayList<AttackTransfer>();
      // for (Region r : state.regionsOwnedBy(me)) {
      // List<Region> neighbors = r.getNeighbors();
      // int armyCount = state.getArmies(r) - 1;
      // for (Region to : neighbors) {
      // if (state.getOwner(to) == me)
      // continue;

      // if (armyCount > state.getArmies(to)) {
      // retAttack.add(new AttackTransfer(r, to, armyCount));

      // armyCount -= armyCount;
      // }
      // }
      // }
      // if (retAttack.size() > 0) {
      // moves.add(new AttackTransferMove(retAttack));
      // var join = Stream.concat(retAttack.stream(),
      // transfers.stream()).collect(Collectors.toList());
      // moves.add(new AttackTransferMove(join));

      // // retAttack.clear();
      // }

    }

    return moves;
  }

  int priority(Game game, Region from, Region to) {
    int me = game.currentPlayer();
    int who = game.getOwner(to);
    if (who == me)
      return 1;
    else if (who == 0)
      return to.getContinent() == from.getContinent() ? 3 : 2;
    else
      return 4;
  }

  @Override
  public void apply(Game state, Move action) {
    state.move(action);
  }

  @Override
  public boolean isDone(Game state) {
    return state.isDone();
  }

  @Override
  public double outcome(Game state) {

    // System.out.println("Winning player: " + state.winningPlayer());

    if (state.winningPlayer() == 1) {
      return 1000;
    } else if (state.winningPlayer() == 0)
      return 0;
    else
      return -1000;
  }

  @Override
  public double evaluate(Game state) {

    var regions1 = state.regionsOwnedBy(1);
    var regions2 = state.regionsOwnedBy(2);
    double regionCount1 = state.regionsOwnedBy(1).size();
    double regionCount2 = state.regionsOwnedBy(2).size();
    double armiesCount1 = 0;
    double armiesCount2 = 0;
    double armiesPerTurn1 = state.armiesPerTurn(1);
    double armiesPerTurn2 = state.armiesPerTurn(2);

    double biggestArmy1 = 0;
    double biggestArmy2 = 0;

    int totalScore = 0;
    for (Region region : regions1) {
      if (biggestArmy1 < state.getArmies(region))
        biggestArmy1 = state.getArmies(region);

      armiesCount1 += state.getArmies(region);
    }
    for (Region region : regions2) {
      if (biggestArmy2 < state.getArmies(region))
        biggestArmy2 = state.getArmies(region);
      armiesCount2 += state.getArmies(region);
    }

    // if (20 + biggestArmy1 < biggestArmy2) {
    // return -1000000000;
    // }

    totalScore += 1000 * (armiesCount1 - armiesCount2);
    totalScore += 1000 * (regionCount1 - regionCount2);
    totalScore += 100000 * (armiesPerTurn1 - armiesPerTurn2);

    return totalScore;
  }
}
