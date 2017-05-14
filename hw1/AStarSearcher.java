import java.util.ArrayList;
import java.util.PriorityQueue;
import java.lang.Math;
/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// Explored list is a Boolean array that indicates if a state associated with a given position in the maze has already been explored. 
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];		
		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
		
		// Parameter for explored point counting
		//int test = 0;
		

		Square  goalsquare = maze.getGoalSquare();
		Boolean repeat = false;

		// Initialize the root state and add to frontier list
		State init = new State(maze.getPlayerSquare(), null, 0, 0); 
		frontier.add(new StateFValuePair(init, fValue(init, goalsquare)));
		maxSizeOfFrontier = frontier.size(); 

		while (!frontier.isEmpty()) {

			// Pop the head of the priority queue
			init = frontier.poll().getState();
			noOfNodesExpanded += 1;
			explored[init.getX()][init.getY()] = true;
			
			if (init.getDepth() > maxDepthSearched) {
				maxDepthSearched = init.getDepth();
			}

			// Goal State Found 
			if (init.isGoal(maze)) {
					
				// Take goal state as the last point serached					
				cost = init.getDepth();
				maxDepthSearched = Math.max(maxDepthSearched, init.getDepth());
				maxSizeOfFrontier = Math.max(maxSizeOfFrontier, frontier.size()); 					

				// Set the final state and print '.'
				State goalstate = init;
				while (goalstate.getParent() != null) {
					goalstate = goalstate.getParent();
					maze.setOneSquare(goalstate.getSquare(), '.');
				}
				maze.setOneSquare(goalstate.getSquare(), 'S');
					
					/*
					// explored counting
					for(int i=0 ; i < maze.getNoOfRows(); i++) {
						for (int j=0 ; j < maze.getNoOfCols(); j++) {
							if (explored[i][j] == true) {
								System.out.println(i +", " +j);
								test += 1;
							}
						}
					}
					System.out.println("Tese: "+ test);
					*/

				return true;					
			} 


			// Update the player square for generating differnet successor 
			maze = new Maze(maze.getMazeMatrix(), init.getSquare(), goalsquare);

			for (State successor : init.getSuccessors(explored, maze, "astar")) {
				
				// Evaluate g(x) for same square 
				for (StateFValuePair element : frontier) {
					State temp = element.getState();
					if (temp.getX() == successor.getX() && temp.getY() == successor.getY()) {
						repeat = true;
						if (temp.getGValue() > successor.getGValue()) {
							frontier.add(new StateFValuePair(successor, fValue(successor, goalsquare)));
							frontier.remove(element);	
						}				
					}
				}
				
				// Add non-repeated node into frontier
				if (!repeat) {
					frontier.add(new StateFValuePair(successor, fValue(successor, goalsquare)));
				} else {
					repeat = false;
				}

				maxSizeOfFrontier = Math.max(maxSizeOfFrontier, frontier.size()); 					

			}

		}
		// Return false if no solution
		return false;
	}
	/**
	 * Apply the heuristic function and calculate the fValue
	 * 
	 * @return fValue 
	 */	
	private double fValue(State state, Square goalsquare) {
		int x = state.getX();
		int y = state.getY();
		int gx = goalsquare.X;
		int gy = goalsquare.Y;

		return Math.sqrt( Math.pow((x-gx), 2) + Math.pow((y-gy),2)) + state.getGValue();
	}
}
