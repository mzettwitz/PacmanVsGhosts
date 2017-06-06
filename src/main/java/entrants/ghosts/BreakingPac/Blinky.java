package entrants.ghosts.BreakingPac;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
 * Created by Piers on 11/11/2015.
 */
public class Blinky extends IndividualGhostController {


    public Blinky() {
        super(Constants.GHOST.BLINKY);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return MOVE.LEFT;
    }
}
