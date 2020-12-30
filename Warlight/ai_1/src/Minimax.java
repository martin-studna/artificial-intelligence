
import java.util.ArrayList;
import java.util.Random;

import minimax.*;

public class Minimax<S, A> implements Strategy<S, A> {

  private HeuristicGame<S, A> game;
  private int limit;
  private int player;
  private long debugLevel;

  public Minimax(HeuristicGame<S, A> game, int limit) {
    this.game = game;
    this.limit = limit;
    this.debugLevel = 0;
  }

  private Result<A> evaluate(S state, int depth, int level, double alpha, double beta, boolean maximizingPlayer) {

    /**
     * If we have visited all states.
     */
    if (game.isDone(state)) {
      // System.out.println("Game is done!");
      // System.out.println("Level: " + level);
      var outcome = game.outcome(state);

      /**
       * Prioritize win or lose by level.
       */
      if (this.player == 1 && outcome != 0) {
        if (outcome == 1000)
          outcome -= level;
        else
          outcome += level;
      } else if (this.player == 2 && outcome != 0) {
        if (outcome == -1000)
          outcome += level;
        else
          outcome -= level;
      }
      return new Result<A>(outcome, null, level);
    } else if (depth == 0 && limit != 0) {
      var outcome = game.evaluate(state);
      if (this.player == 1 && outcome != 0) {
        if (outcome == 1000)
          outcome -= level;
        else
          outcome += level;
      } else if (this.player == 2 && outcome != 0) {
        if (outcome == -1000)
          outcome += level;
        else
          outcome -= level;
      }
      // System.out.println(outcome);
      return new Result<A>(outcome, null, level);
    }

    A resultAction = null;
    long resultLevel = 1000000000;
    int wonTimes = 0;

    if (maximizingPlayer) {
      var maxEval = -1000000.0;
      for (var action : game.actions(state)) {
        var newState = game.clone(state);
        game.apply(newState, action);
        var result = evaluate(newState, depth - 1, level + 1, alpha, beta, false);

        if ((this.player == 1 && result.value == (HeuristicGame.PLAYER_1_WIN - result.level))
            || (this.player == 2 && result.value == (HeuristicGame.PLAYER_2_WIN + result.level))) {
          wonTimes++;
        }

        if (result.value > maxEval) {
          maxEval = result.value;
          resultAction = action;
          resultLevel = result.level;
        }

        if (maxEval >= beta) {
          if (maxEval == 0) {
            return new Result<A>(this.player == 1 ? wonTimes : -wonTimes, resultAction, resultLevel);
          } else
            return new Result<A>(maxEval, resultAction, resultLevel);
        }

        if (maxEval > alpha)
          alpha = maxEval;
      }
      if (maxEval == 0)
        return new Result<A>(this.player == 1 ? wonTimes : -wonTimes, resultAction, resultLevel);
      else
        return new Result<A>(maxEval, resultAction, resultLevel);
    } else {
      var minEval = 1000000.0;
      for (var action : game.actions(state)) {
        var newState = game.clone(state);
        game.apply(newState, action);
        var result = evaluate(newState, depth - 1, level + 1, alpha, beta, true);

        if ((this.player == 1 && result.value == (HeuristicGame.PLAYER_1_WIN - result.level))
            || (this.player == 2 && result.value == (HeuristicGame.PLAYER_2_WIN + result.level))) {
          wonTimes++;
        }

        if (result.value < minEval) {
          minEval = result.value;
          resultAction = action;
          resultLevel = result.level;
        }

        if (minEval <= alpha) {
          if (minEval == 0)
            return new Result<A>(this.player == 1 ? wonTimes : -wonTimes, resultAction, resultLevel);
          else
            return new Result<A>(minEval, resultAction, resultLevel);
        }

        if (minEval < beta)
          beta = minEval;

      }
      if (minEval == 0)
        return new Result<A>(this.player == 1 ? wonTimes : -wonTimes, resultAction, resultLevel);
      else
        return new Result<A>(minEval, resultAction, resultLevel);
    }
  }

  // method in Strategy interface
  public A action(S state) {
    this.player = game.player(state);
    int level = 0;
    var result = evaluate(state, limit, level, Double.MIN_VALUE, Double.MAX_VALUE,
        game.player(state) == 1 ? true : false);
    return result.action;
  }
}
