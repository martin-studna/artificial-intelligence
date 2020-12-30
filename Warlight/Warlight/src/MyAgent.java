
import java.util.*;

import engine.AgentBase;
import game.*;
import game.move.*;

public class MyAgent extends AgentBase {
    Random random = new Random(0);

    private Minimax<Game, Move> minimax;

    public MyAgent() {
        var warlightGame = new WarlightGame();
        minimax = new Minimax<Game, Move>(warlightGame, 10);
    }

    @Override
    public void init(long timeoutMillis) {
    }

    @Override
    public Region chooseRegion(Game game) {
        ArrayList<Region> choosable = game.getPickableRegions();
        return choosable.get(random.nextInt(choosable.size()));
    }

    @Override
    public List<PlaceArmies> placeArmies(Game game) {

        PlaceArmiesMove move = (PlaceArmiesMove) minimax.action(game);

        return move.commands;
    }

    @Override
    public List<AttackTransfer> attackTransfer(Game game) {

        AttackTransferMove move = (AttackTransferMove) minimax.action(game.clone());

        return move.commands;
    }
}
