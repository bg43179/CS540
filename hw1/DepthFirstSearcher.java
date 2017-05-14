import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {
	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public DepthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main depth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// Explored list is a 2D Boolean array that indicates if a state associated with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];		
		LinkedList<State> stack = new LinkedList<State>();
		State init = new State(maze.getPlayerSquare(), null, 0, 0);
		Boolean repeat = false;
		// Add starting point into stack
		maxSizeOfFrontier =0;
		maxDepthSearched = 0;
		stack.push(init);

		while (!stack.isEmpty()) {

			// Pop the top point of stack, then move to it
			init = stack.pop();
			explored[init.getX()][init.getY()] = true;

			maxDepthSearched = Math.max(maxDepthSearched, init.getDepth());
			
			// Update the player square for generating differnet successor 
			maze = new Maze(maze.getMazeMatrix(), init.getSquare(), maze.getGoalSquare());

			for (State successor: init.getSuccessors(explored, maze, "dfs")) {
				// Goal State Found 
				
				for(State i: stack){
					if (i.getX() == successor.getX() && i.getY() == successor.getY()) {
						repeat = true;
					}
				}

				if (successor.isGoal(maze)) {
					
					// Take goal state as the last point serached
					cost = successor.getDepth();
					maxDepthSearched = Math.max(maxDepthSearched, successor.getDepth());
					noOfNodesExpanded += 1;
					// Use parent to set the path into '.'
					State goalstate = successor;
					while (goalstate.getParent() != null) {
						goalstate = goalstate.getParent();
						maze.setOneSquare(goalstate.getSquare(), '.');
					}
					maze.setOneSquare(goalstate.getSquare(), 'S');

					// explored counting
					for(int i=0 ; i < maze.getNoOfRows(); i++) {
						for (int j=0 ; j < maze.getNoOfCols(); j++) {
							if (explored[i][j] == true) {
								noOfNodesExpanded += 1;
							}
						}
					}				
					return true;
				}

				if (!repeat) {
					stack.push(successor);					
				}

				repeat = false;


				
			}

			// Track the max size of frontier
			maxSizeOfFrontier = Math.max(stack.size(),maxSizeOfFrontier);
		}
		// Return false if no solution
		return false;
	}
}
