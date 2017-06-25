package entrants.ghosts.BreakingPac;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * Created by Piers on 11/11/2015.
 */
public class Inky extends IndividualGhostController {

    public Inky() {
        super(Constants.GHOST.INKY);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
    	// simply obtain all possible moves(remember: ghost are only able to run forward and left/right, but stay at position
    	Constants.MOVE lastMove = game.getGhostLastMoveMade(GHOST.INKY);
    	int currentPosition = game.getGhostCurrentNodeIndex(GHOST.INKY);
    	int currentPacPosition = game.getPacmanCurrentNodeIndex();
    	int targetPowerPill = game.getPowerPillIndices()[1];
    	int distanceToPowerPill = game.getShortestPathDistance(currentPosition,targetPowerPill);
    	int distancePacToPowerPill = game.getShortestPathDistance(currentPacPosition, targetPowerPill);
    	if(distanceToPowerPill > distancePacToPowerPill){
    		Constants.MOVE move = game.getNextMoveTowardsTarget(currentPosition, targetPowerPill, Constants.DM.PATH);
    		return move;
    	}else{
            Constants.MOVE move = game.getApproximateNextMoveTowardsTarget(currentPosition,
                    currentPacPosition, lastMove, Constants.DM.PATH);
            return move;
    	}
    	 
    	    
    }
}
