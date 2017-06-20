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
    	// simply obtain all possible moves(remember: ghost are only able to run forward and left/right, but stay at position
    	MOVE[] moves = game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost), game.getGhostLastMoveMade(ghost));
    	for (MOVE move : moves)
    	{
    		// move down if possible, else move left
    		if(move == MOVE.DOWN)
    			return move;
    	}
        return MOVE.LEFT;
    }
}
