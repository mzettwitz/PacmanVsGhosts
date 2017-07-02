package entrants.ghosts.BreakingPac;

import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Piers on 11/11/2015.
 */
public class Pinky extends IndividualGhostController {

    public Pinky() {
        super(Constants.GHOST.PINKY);
    }
    public static int powerPillIndex = 2;
    public static int pillIndex = -1;
    private int targetPill = -1;
    Random rnd = new Random();
    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
    	int currentPosition = game.getGhostCurrentNodeIndex(GHOST.PINKY);
		int currentPacPosition = game.getPacmanCurrentNodeIndex();
		if(Sue.pillIndex > 0 || Inky.powerPillIndex > 0 || Blinky.powerPillIndex > 0){
			pillIndex = Math.max(Math.max(Sue.pillIndex, Blinky.pillIndex), Inky.pillIndex);
		}
    	if(game.getNumberOfPowerPills() == 0 && pillIndex == -1 && !game.isPillStillAvailable(currentPosition)){
            Constants.MOVE[] possibleMoves = game.getPossibleMoves(currentPosition, game.getGhostLastMoveMade(GHOST.PINKY));
            return possibleMoves[rnd.nextInt(possibleMoves.length)];
    	}else{
			Constants.MOVE lastMove = game.getGhostLastMoveMade(GHOST.PINKY);
			if(game.getNumberOfActivePills() != 0){
				targetPill = game.getPowerPillIndices()[powerPillIndex];
				if(currentPosition == targetPill && !game.isPowerPillStillAvailable(game.getPowerPillIndex(targetPill)))
				{
					powerPillIndex = Sue.powerPillIndex;
				}
			}else if(pillIndex == -1){
				pillIndex = currentPosition;
				
			}else{
				targetPill = pillIndex;
			}
			int distanceToPowerPill = game.getShortestPathDistance(currentPosition,targetPill);
			int distancePacToPowerPill = game.getShortestPathDistance(currentPacPosition, targetPill);
			if(distanceToPowerPill > distancePacToPowerPill){
				Constants.MOVE move = game.getNextMoveTowardsTarget(currentPosition, targetPill, Constants.DM.PATH);
				return move;
			}else{
			    Constants.MOVE move = game.getApproximateNextMoveTowardsTarget(currentPosition,
			            currentPacPosition, lastMove, Constants.DM.PATH);
			    return move;
			}
    	}
    }
}
