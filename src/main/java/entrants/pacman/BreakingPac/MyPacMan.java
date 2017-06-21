package entrants.pacman.BreakingPac;

import java.util.ArrayList;

import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
    private Mapping m_map;
    private Policy m_policy;    

    
    
    // Constructor: 100ms to compute: build up initial map
    public MyPacMan()
    {
    }
    
    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man

    	MOVE[] moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
    	for (MOVE move : moves)
    	{
    		// move down if possible, else move left
    		if(move == MOVE.DOWN)
    		{
    			return move;
    		}
    	}
    	
        return MOVE.LEFT;
    }
    
    
    private int computeMove(int currentStep, int maxSteps, MOVE move, Game game)
    {
    	game.advanceGame(MOVE.LEFT, new POCommGhosts(40).getMove(game, 40));
    	return 0;
    }
    
    
    // Internal storage
    private class Mapping
    {
    	Cell[] cells = new Cell [9];
    	
    	private class Cell
    	{
    		int numberGhosts = 0;
    		int pillsAvailable = 0;
    		int ppAvailable = 0;
    	}
    }
    
    // Internal policy
    private class Policy
    {
    	int ghost = -200;
    	int pill = 10;
    	int step = -2;
    	int powerPill = 10;
    	int powerPillGhost = 100;
    	int ppGhostMulti = -2;	// use ghost * ppMulti while PP is active
    }
}