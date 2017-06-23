package entrants.pacman.BreakingPac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
	private ArrayList<Integer> m_visitedNodes; 


	// Constructor: 100ms to compute: build up initial map
	public MyPacMan()
	{
		m_policy = new Policy();
		m_map = new Mapping();
		m_ghosts = new POCommGhosts(50);
		m_visitedNodes = new ArrayList<Integer>();	//TODO: store the times a node has been visited
	}

	public MOVE getMove(Game game, long timeDue) {
		//Place your game logic here to play the game as Ms Pac-Man

		MOVE [] _moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
		ArrayList<MOVE> moves = new ArrayList<MOVE>();
		for(int i = 0; i < _moves.length; i++)
			moves.add(_moves[i]);

		//ArrayList<Integer> rewards = new ArrayList<Integer>(moves.length);
		ArrayList<Pair<Integer, MOVE> > rewardedMoves = new ArrayList<Pair<Integer, MOVE> >(moves.size());
		int nodeIdx = game.getPacmanCurrentNodeIndex();

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
		for (int i = 0; i < moves.size(); i++)
		{
			rewardedMoves.add(i, new Pair<Integer, MOVE>());
			rewardedMoves.get(i).first = computeMove(0, 3, moves.get(i), nodeIdx, simGame);
			rewardedMoves.get(i).second = moves.get(i);
		}

		// sort moves to get best moves in ascending order
		sortRewardedMoves(rewardedMoves);
		// loop descending 
		for(int i = rewardedMoves.size()-1; i >= 0; i--)
		{
			// check if next node was visited before
			int neighbor = game.getNeighbour(nodeIdx, rewardedMoves.get(i).second);
			// add next node to visited nodes
			if(!m_visitedNodes.contains(neighbor))
			{
				m_visitedNodes.add(neighbor);
				return rewardedMoves.get(i).second;
			}
		}

		// if all neighbor nodes were visited: do random step
		// TODO: use heuristics
		Random rnd = new Random();
		int rndMove = 0;
		if(game.isJunction(nodeIdx))
		{		
			rndMove = rnd.nextInt(moves.size());
			while(game.getPacmanLastMoveMade() == moves.get(rndMove).opposite())
				rndMove = rnd.nextInt(moves.size());
		}		
		else if(moves.contains(lastMove))
			return lastMove;		
		else
		{
			rndMove = rnd.nextInt(moves.size());
			while(game.getPacmanLastMoveMade() == moves.get(rndMove).opposite())
				rndMove = rnd.nextInt(moves.size());
		}
		return	moves.get(rndMove);
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

			//rewards[i] = game.getScore();

			
			// TODO: use better prediction (e.g. powerpill only when ghosts available, check if you can see ghosts)
			if(gameCopies[i].wasPillEaten())
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
				rewards[i] = m_policy.step;


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
		int powerPill = -50;
		int powerPillGhost = 100;
		int ppGhostMulti = -2;	// use ghost * ppMulti while PP is active
	}


	private class Pair<F, S> 
	{
		F first;
		S second;
	}

	private void sortRewardedMoves(ArrayList<Pair<Integer, MOVE> > list)
	{
		for(int i = 0; i < list.size(); i++)
		{
			for(int j = 0; j < list.size()-i-1; j++)
			{
				if(list.get(j).first > list.get(j+1).first)
				{
					Pair<Integer, MOVE> tmp = list.get(j+1);
					list.set(j+1, list.get(j));
					list.set(j, tmp);
				}					
			}
		}
	}
}