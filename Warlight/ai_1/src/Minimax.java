
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

  private Result<A> evaluate(S state, int depth, double alpha, double beta, boolean maximizingPlayer) {
    debugLevel++;
    System.out.println("Debug level: " + debugLevel);
    if (game.isDone(state)) {
      System.out.println(game.outcome(state));
      return new Result<A>(game.outcome(state), null);
    }

    if (depth == 0 && limit != 0) {
      System.out.println(game.evaluate(state));
      return new Result<A>(game.evaluate(state), null);
    }

    A resultAction = null;

    if (maximizingPlayer) {
      var maxEval = -10000000.0;
      for (var action : game.actions(state)) {
        var newState = game.clone(state);
        game.apply(newState, action);
        var result = evaluate(newState, depth - 1, alpha, beta, false);
        debugLevel--;
        System.out.println("Debug level: " + debugLevel);
        if (maxEval < result.value) {
          resultAction = action;
          maxEval = result.value;
        }

        if (maxEval >= beta)
          return new Result<A>(maxEval, resultAction);

        if (maxEval > alpha) {
          alpha = maxEval;
        }
      }
      System.out.println(maxEval);
      return new Result<A>(maxEval, resultAction);
    } else {
      var minEval = 10000000.0;
      for (var action : game.actions(state)) {
        var newState = game.clone(state);
        game.apply(newState, action);
        var result = evaluate(newState, depth - 1, alpha, beta, true);
        debugLevel--;
        System.out.println("Debug level: " + debugLevel);
        if (minEval > result.value) {
          resultAction = action;
          minEval = result.value;
        }

        if (minEval <= alpha)
          return new Result<A>(minEval, resultAction);

        if (minEval < beta) {
          beta = minEval;
        }
      }
      System.out.println(minEval);
      return new Result<A>(minEval, resultAction);
    }
  }

  // method in Strategy interface
  public A action(S state) {
    this.player = game.player(state);
    var result = evaluate(state, limit, Double.MIN_VALUE, Double.MAX_VALUE, game.player(state) == 1 ? true : false);
    return result.action;
  }

}
