import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import game.Game;
import game.Region;
import game.move.AttackTransfer;
import game.move.AttackTransferMove;
import game.move.Move;

public class AttackTransferTactics {

  private static Queue<Integer> queue;
  private static HashSet<Integer> opened;
  private static HashMap<Integer, Integer> predecessors;

  private static int canMiss(Region from, Region to, Game state) {
    if (state.getArmies(from) - 1 < state.getOwner(to))
      return state.getArmies(from) - 1;

    return (state.getArmies(from) - 1) - state.getArmies(to);
  }

  public static void safeAttack(Game state, List<AttackTransfer> attacks) {
    int me = state.currentPlayer();

    var enemyRegions = new ArrayList<Region>();
    var myRegions = state.regionsOwnedBy(me);
    var attackingCountries = new ArrayList<Region>();

    for (Region r : myRegions) {
      List<Region> neighbors = r.getNeighbors();
      int armyCount = state.getArmies(r) - 1;
      for (Region n : neighbors) {
        if (state.getOwner(n) != me)
          enemyRegions.add(n);
      }
    }
  }

  public static void transferArmies2(Game state, List<Move> moves) {
    int me = state.currentPlayer();
    var myRegions = state.regionsOwnedBy(me);

    for (Region r : myRegions) {
      boolean isBorderRegion = false;

      if (state.getArmies(r) == 1)
        continue;

      var availableArmy = state.getArmies(r) - 1;

      for (Region n : r.getNeighbors()) {
        if (state.getOwner(n) == me) {
          moves.add(new AttackTransferMove(Arrays.asList(new AttackTransfer(r, n, availableArmy))));
        }
      }
    }
  }

  public static void transferArmies(Game state, List<AttackTransfer> transfers) {
    int me = state.currentPlayer();
    var myRegions = state.regionsOwnedBy(me);

    for (Region r : myRegions) {
      boolean isBorderRegion = false;

      if (state.getArmies(r) == 1)
        continue;

      var availableArmy = state.getArmies(r) - 1;

      for (Region n : r.getNeighbors()) {
        if (state.getOwner(n) != me) {
          isBorderRegion = true;
          break;
        }
      }

      if (isBorderRegion)
        continue;

      queue = new LinkedList<Integer>();
      opened = new HashSet<Integer>();
      predecessors = new HashMap<Integer, Integer>();
      opened.add(r.id);
      queue.add(r.id);
      var firstId = r.id;
      var lastId = r.id;

      while (!queue.isEmpty()) {
        lastId = queue.remove();

        var region = state.getRegion(lastId);

        for (Region n : region.getNeighbors()) {
          if (state.getOwner(n) != me)
            continue;

          if (!opened.contains(n.id)) {
            opened.add(n.id);
            predecessors.put(n.id, region.id);
            queue.add(n.id);
          }
        }
      }

      while (firstId != predecessors.get(lastId)) {
        lastId = predecessors.get(lastId);
      }
      // System.out.println(r.getName() + " " + state.getOwner(r));
      // System.out.println(state.getRegion(lastId).getName() + " " +
      // state.getOwner(state.getRegion(lastId)));

      transfers.add(new AttackTransfer(r, state.getRegion(lastId), availableArmy));
    }
  }
}
