package entrants.pacman.BreakingPac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants.DM;
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
	private int [] m_visitedNodes;
	private int m_maxLookahead;
	private int m_maxNegPoints;
	private int m_minNegPoints;


	// Constructor: 100ms to compute: build up initial map
	public MyPacMan()
	{
		m_policy = new Policy();
		m_map = new Mapping();
		m_ghosts = new POCommGhosts(50);
		m_maxLookahead = 7;
		m_maxNegPoints = (m_maxLookahead-1)*m_policy.pill + m_policy.ghost;
		m_minNegPoints = m_maxLookahead * m_policy.step;
	}

	// ------------------------------------------------------------------------------------------------------------------------------
	
	public MOVE getMove(Game game, long timeDue) {
		//Place your game logic here to play the game as Ms Pac-Man
		if(game.getCurrentLevelTime() < 1)
			m_visitedNodes = new int [game.getNumberOfNodes()];



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
			rewardedMoves.add(i, new Pair<Integer, MOVE>(computeMove(0, m_maxLookahead, moves.get(i), nodeIdx, simGame), moves.get(i)));
		}

		Pair<Integer, MOVE> localExplore = new Pair<Integer, MOVE>(Integer.MAX_VALUE,rewardedMoves.get(0).second);
		// sort moves to get best moves in ascending order
		sortRewardedMoves(rewardedMoves);
		// loop descending 
		
		// Flee or chase ghosts
		if(game.getGhostCurrentNodeIndex(GHOST.BLINKY) != -1)
		{
			if(game.isGhostEdible(GHOST.BLINKY))
			{	
				MOVE m = game.getNextMoveTowardsTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.MANHATTAN);
				m_visitedNodes[game.getNeighbour(nodeIdx, m)]++;
				return game.getNextMoveTowardsTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.MANHATTAN);
			}				
			else
			{
				MOVE m = game.getNextMoveAwayFromTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.MANHATTAN);;
				m_visitedNodes[game.getNeighbour(nodeIdx, m)]++;
				return game.getNextMoveAwayFromTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.MANHATTAN);
			}
		}
		if(game.getGhostCurrentNodeIndex(GHOST.INKY) != -1)
		{
			if(game.isGhostEdible(GHOST.INKY))
			{	
				MOVE m = game.getNextMoveTowardsTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.INKY), DM.MANHATTAN);
				m_visitedNodes[game.getNeighbour(nodeIdx, m)]++;
				return game.getNextMoveTowardsTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.INKY), DM.MANHATTAN);
			}				
			else
			{
				MOVE m = game.getNextMoveAwayFromTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.INKY), DM.MANHATTAN);;
				m_visitedNodes[game.getNeighbour(nodeIdx, m)]++;
				return game.getNextMoveAwayFromTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.INKY), DM.MANHATTAN);
			}
		}
		if(game.getGhostCurrentNodeIndex(GHOST.PINKY) != -1)
		{
			if(game.isGhostEdible(GHOST.PINKY))
			{	
				MOVE m = game.getNextMoveTowardsTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.PINKY), DM.MANHATTAN);
				m_visitedNodes[game.getNeighbour(nodeIdx, m)]++;
				return game.getNextMoveTowardsTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.PINKY), DM.MANHATTAN);
			}				
			else
			{
				MOVE m = game.getNextMoveAwayFromTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.PINKY), DM.MANHATTAN);;
				m_visitedNodes[game.getNeighbour(nodeIdx, m)]++;
				return game.getNextMoveAwayFromTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.PINKY), DM.MANHATTAN);
			}
		}
		if(game.getGhostCurrentNodeIndex(GHOST.SUE) != -1)
		{
			if(game.isGhostEdible(GHOST.SUE))
			{	
				MOVE m = game.getNextMoveTowardsTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.SUE), DM.MANHATTAN);
				m_visitedNodes[game.getNeighbour(nodeIdx, m)]++;
				return game.getNextMoveTowardsTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.SUE), DM.MANHATTAN);
			}				
			else
			{
				MOVE m = game.getNextMoveAwayFromTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.SUE), DM.MANHATTAN);;
				m_visitedNodes[game.getNeighbour(nodeIdx, m)]++;
				return game.getNextMoveAwayFromTarget(nodeIdx, game.getGhostCurrentNodeIndex(GHOST.SUE), DM.MANHATTAN);
			}
		}


		
		for(int i = rewardedMoves.size()-1; i >= 0; i--)
		{
			// check if next node was visited before
			int neighbor = game.getNeighbour(nodeIdx, rewardedMoves.get(i).second);

			// add next node to visited nodes
			if(m_visitedNodes[neighbor] == 0 && rewardedMoves.get(i).first > m_maxNegPoints)
			{
				m_visitedNodes[neighbor]++;
				return rewardedMoves.get(i).second;
			}
			// look for node that was visited least
			else if(m_visitedNodes[neighbor] < localExplore.first && rewardedMoves.get(i).first > m_maxNegPoints)
			{
				localExplore.second = rewardedMoves.get(i).second;
				localExplore.first = m_visitedNodes[neighbor];
			}
		}

		if(localExplore.first < m_minNegPoints)
		{
			m_visitedNodes[game.getNeighbour(nodeIdx, lastMove)]++;
			return lastMove;
		}			
		m_visitedNodes[game.getNeighbour(nodeIdx, localExplore.second)]++;
		return localExplore.second;
	}

	// ------------------------------------------------------------------------------------------------------------------------------
	
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
		 MASController ghosts = new POCommGhosts(50);
		
		// advance game for each possible move
		for (int i = 0; i < moves.length; i++) {			
			gameCopies[i] = game.copy();
			gameCopies[i].advanceGame(moves[i], ghosts.getMove(gameCopies[i].copy(), 40));

			int idx = gameCopies[i].getPacmanCurrentNodeIndex();
			if(idx == -1)
				continue;

			//rewards[i] = game.getScore();
			// TODO: use better prediction (e.g. powerpill only when ghosts available, check if you can see ghosts)
			if(gameCopies[i].wasPillEaten())
				rewards[i] += m_policy.pill;
			if(gameCopies[i].wasPowerPillEaten())
				rewards[i] += m_policy.powerPill;
			if(gameCopies[i].wasPacManEaten())
				return m_policy.ghost;				
			if(gameCopies[i].wasGhostEaten(GHOST.BLINKY))
				rewards[i] += m_policy.ghost * m_policy.ppGhostMulti;
			if(gameCopies[i].wasGhostEaten(GHOST.SUE))
				rewards[i] += m_policy.ghost * m_policy.ppGhostMulti;
			if(gameCopies[i].wasGhostEaten(GHOST.INKY))
				rewards[i] += m_policy.ghost * m_policy.ppGhostMulti;
			if(gameCopies[i].wasGhostEaten(GHOST.PINKY))
				rewards[i] += m_policy.ghost * m_policy.ppGhostMulti;
			
				rewards[i] += m_policy.step;


			rewards[i] += computeMove(currentStep+1, maxSteps, moves[i], idx, gameCopies[i]);
			if(rewards[i] > max)
				max = rewards[i];
		}

		return max;
	}

	// ------------------------------------------------------------------------------------------------------------------------------
	
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

	// ------------------------------------------------------------------------------------------------------------------------------
	
	// Internal policy
	private class Policy
	{
		int ghost = -500;
		int pill = 10;
		int step = -2;
		int powerPill = -50;
		int powerPillGhost = 100;
		int ppGhostMulti = -2;	// use ghost * ppGhostMulti while PP is active
	}

	// ------------------------------------------------------------------------------------------------------------------------------
	
	private class Pair<F, S> 
	{
		F first;
		S second;

		Pair(F _first,S _second)
		{
			first = _first;
			second = _second;
		}
		Pair(){}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------

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
				else if(list.get(j).first == list.get(j+1).first)
				{
					if(list.get(j).second == lastMove)
					{
						Pair<Integer, MOVE> tmp = list.get(j+1);
						list.set(j+1, list.get(j));
						list.set(j, tmp);
					}
				}
			}
		}
	}
}