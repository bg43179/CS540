import java.util.ArrayList;

/**
 * A state in the search represented by the (x,y) coordinates of the square and
 * the parent. In other words a (square,parent) pair where square is a Square,
 * parent is a State.
 * 
 * You should fill the getSuccessors(...) method of this class.
 * 
 */
public class State {

	private Square square;
	private State parent;

	// Maintain the gValue (the distance from start)
	// You may not need it for DFS but you will
	// definitely need it for AStar
	private int gValue;

	// States are nodes in the search tree, therefore each has a depth.
	private int depth;

	/**
	 * @param square
	 *            current square
	 * @param parent
	 *            parent state
	 * @param gValue
	 *            total distance from start
	 */
	public State(Square square, State parent, int gValue, int depth) {
		this.square = square;
		this.parent = parent;
		this.gValue = gValue;
		this.depth = depth;
	}

	/**
	 * @param visited
	 *            explored[i][j] is true if (i,j) is already explored
	 * @param maze
	 *            initial maze to get find the neighbors
	 * @return all the successors of the current state
	 */
	public ArrayList<State> getSuccessors(boolean[][] explored, Maze maze, String search) {
		ArrayList<State> successorlist = new ArrayList<State>();
		// Check all four neighbors (up, right, down, left)

		int i = square.X;
		int j = square.Y;
		if (search == "dfs") {
		// Each successor's depth and gValue are +1 of this object.
		if (j-1 >= 0 && maze.getSquareValue(i,j-1) != '%' && explored[i][j-1] == false) {
			State left = new State(new Square(i,j-1), this, gValue+1, depth+1);
			successorlist.add(left);
		}
		if (i+1 <= maze.getNoOfRows()-1 && maze.getSquareValue(i+1,j) != '%' && explored[i+1][j] == false) {
			State down = new State(new Square(i+1,j), this, gValue+1, depth+1);
			successorlist.add(down);
		}
		if (j+1 <= maze.getNoOfCols()-1 && maze.getSquareValue(i,j+1) != '%' && explored[i][j+1] == false) { 
			State right = new State(new Square(i,j+1), this, gValue+1, depth+1);
			successorlist.add(right);
		}

		if (i-1 >= 0 && maze.getSquareValue(i-1,j) != '%' && explored[i-1][j] == false) {
			State up = new State(new Square(i-1,j), this, gValue+1, depth+1);
			successorlist.add(up);
		}
		} else {
			if (i-1 >= 0 && maze.getSquareValue(i-1,j) != '%' && explored[i-1][j] == false) {
				State up = new State(new Square(i-1,j), this, gValue+1, depth+1);
				successorlist.add(up);
			}
			if (j+1 <= maze.getNoOfCols()-1 && maze.getSquareValue(i,j+1) != '%' && explored[i][j+1] == false) { 
				State right = new State(new Square(i,j+1), this, gValue+1, depth+1);
				successorlist.add(right);
			}
			if (i+1 <= maze.getNoOfRows()-1 && maze.getSquareValue(i+1,j) != '%' && explored[i+1][j] == false) {
				State down = new State(new Square(i+1,j), this, gValue+1, depth+1);
				successorlist.add(down);
			}
			if (j-1 >= 0 && maze.getSquareValue(i,j-1) != '%' && explored[i][j-1] == false) {
			State left = new State(new Square(i,j-1), this, gValue+1, depth+1);
			successorlist.add(left);
			}	
		}
		return successorlist;		
	}
	/**
	 * @return x coordinate of the current state
	 */
	public int getX() {
		return square.X;
	}

	/**
	 * @return y coordinate of the current state
	 */
	public int getY() {
		return square.Y;
	}

	/**
	 * @param maze initial maze
	 * @return true is the current state is a goal state
	 */
	public boolean isGoal(Maze maze) {
		if (square.X == maze.getGoalSquare().X
				&& square.Y == maze.getGoalSquare().Y)
			return true;

		return false;
	}

	/**
	 * @return the current state's square representation
	 */
	public Square getSquare() {
		return square;
	}

	/**
	 * @return parent of the current state
	 */
	public State getParent() {
		return parent;
	}

	/**
	 * You may not need g() value in the BFS but you will need it in A-star
	 * search.
	 * 
	 * @return g() value of the current state
	 */
	public int getGValue() {
		return gValue;
	}

	/**
	 * @return depth of the state (node)
	 */
	public int getDepth() {
		return depth;
	}
}
