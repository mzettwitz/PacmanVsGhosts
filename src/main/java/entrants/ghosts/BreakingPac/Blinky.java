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
public class Blinky extends IndividualGhostController {


    public Blinky() {
        super(Constants.GHOST.BLINKY);
    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
    	// simply obtain all possible moves(remember: ghost are only able to run forward and left/right, but stay at position
Constants.MOVE lastMove = game.getGhostLastMoveMade(GHOST.BLINKY);
int currentPosition = game.getGhostCurrentNodeIndex(GHOST.BLINKY);
int currentPacPosition = game.getPacmanCurrentNodeIndex();
int targetPowerPill = game.getPowerPillIndices()[0];
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
