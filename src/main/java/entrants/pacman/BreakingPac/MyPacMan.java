package entrants.pacman.BreakingPac;

import java.util.ArrayList;

import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
	private Mapping m_map;
	private Policy m_policy;    
	private MASController m_ghosts;


	// Constructor: 100ms to compute: build up initial map
	public MyPacMan()
	{
		m_policy = new Policy();
		m_map = new Mapping();
		m_ghosts = new POCommGhosts(50); 
	}

	public MOVE getMove(Game game, long timeDue) {
		//Place your game logic here to play the game as Ms Pac-Man

		MOVE [] moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		int [] rewards = new int [moves.length];
		int max = 0;
		int moveIdx = 0;

		// prepare simulated game
		Game simGame;
        GameInfo info = game.getPopulatedGameInfo();
        info.fixGhosts((ghost) -> new Ghost(
                ghost,
                game.getCurrentMaze().lairNodeIndex,
                -1,
                -1,
                MOVE.NEUTRAL
        ));
        simGame = game.getGameFromInfo(info);
        
        // simulate all moves
		for (int i = 0; i < moves.length; i++)
		{
			rewards[i] = computeMove(0, 5, moves[i], game.getPacmanCurrentNodeIndex(), simGame);
			if(rewards[i] > max && moves[i] != lastMove.opposite())
			{
				max = rewards[i];
				moveIdx = i;
			}
		}

		return moves[moveIdx];

	}


	private int computeMove(int currentStep, final int maxSteps, MOVE move, int nodeIdx, Game game)
	{
		// check if max depth is reached
		if(currentStep > maxSteps || nodeIdx == -1)
			return 0;

		// setup variables
		MOVE [] moves = game.getPossibleMoves(nodeIdx);
		int [] rewards = new int [moves.length];
		Game [] gameCopies = new Game[moves.length];
		int max = 0;

		// advance game for each possible move
		for (int i = 0; i < moves.length; i++) {			
			gameCopies[i] = game.copy();
			gameCopies[i].advanceGame(moves[i],m_ghosts.getMove(gameCopies[i], 40));
			
			int idx = gameCopies[i].getPacmanCurrentNodeIndex();
			if(idx == -1)
				continue;

			rewards[i] = game.getScore();
			
			
			/*if(gameCopies[i].wasPillEaten())
			//if(idx != -1 && game.getPillIndex(idx) != -1 && gameCopies[i].isPillStillAvailable(game.getPillIndex(idx)))	
				rewards[i] = m_policy.pill;
			else if(gameCopies[i].wasPowerPillEaten())
				rewards[i] = m_policy.powerPill;
			else if(gameCopies[i].wasPacManEaten())
				rewards[i] = m_policy.ghost;
			else if(gameCopies[i].wasGhostEaten(GHOST.BLINKY))
				rewards[i] = m_policy.ghost * m_policy.ppGhostMulti;
			else if(gameCopies[i].wasGhostEaten(GHOST.SUE))
				rewards[i] = m_policy.ghost * m_policy.ppGhostMulti;
			else if(gameCopies[i].wasGhostEaten(GHOST.INKY))
				rewards[i] = m_policy.ghost * m_policy.ppGhostMulti;
			else if(gameCopies[i].wasGhostEaten(GHOST.PINKY))
				rewards[i] = m_policy.ghost * m_policy.ppGhostMulti;
			else 
				rewards[i] = m_policy.step;*/


			rewards[i] += computeMove(currentStep+1, maxSteps, moves[i], idx, gameCopies[i]);
			if(rewards[i] > max)
				max = rewards[i];
		}

		return max;
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