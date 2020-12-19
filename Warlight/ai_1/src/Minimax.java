
import java.util.ArrayList;
import java.util.Random;

import minimax.*;

public class Minimax<S, A> implements Strategy<S, A> {

  private HeuristicGame<S, A> game;
  private int limit;
  private int player;

  public Minimax(HeuristicGame<S, A> game, int limit) {
    this.game = game;
    this.limit = limit;
  }

  private double evaluate(S state, A lastAction, int depth, double alpha, double beta, boolean maximizingPlayer) {

    if (game.isDone(state))
      return game.outcome(state);

    if (depth == 0 && limit != 0) {
      return game.evaluate(state);
    }

    if (maximizingPlayer) {
      var maxEval = Double.MAX_VALUE;
      for (var action : game.actions(state)) {
        var newState = game.clone(state);
        game.apply(newState, action);
        var eval = evaluate(newState, lastAction, depth - 1, alpha, beta, false);
        if (eval > maxEval && player == 1)
          lastAction = action;
        maxEval = Math.max(eval, maxEval);
        alpha = Math.max(eval, maxEval);
        lastAction = action;
        if (beta <= alpha)
          break;
      }
      return maxEval;
    } else {
      var minEval = Double.MIN_VALUE;
      for (var action : game.actions(state)) {
        var newState = game.clone(state);
        game.apply(newState, action);
        var eval = evaluate(newState, lastAction, depth - 1, alpha, beta, false);
        if (eval < minEval && player == 2)
          lastAction = action;
        minEval = Math.min(eval, minEval);
        alpha = Math.min(eval, minEval);
        if (beta <= alpha)
          break;
      }

      return minEval;
    }
  }

  // method in Strategy interface
  public A action(S state) {
    A lastAction = null;
    this.player = game.player(state);
    evaluate(state, lastAction, limit, Double.MIN_VALUE, Double.MAX_VALUE,
        game.player(state) == 1 ? true : false);

    return lastAction;
  }

}
