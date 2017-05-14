/****************************************************************
 * studPlayer.java
 * Implements MiniMax search with A-B pruning and iterative deepening search (IDS). The static board
 * evaluator (SBE) function is simple: the # of stones in studPlayer's
 * mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for educational purposes provided that
 * (1) you do not distribute or publish solutions, (2) you retain the notice, and (3) you provide clear attribution to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu).
 * Some GUI componets are from Mancala Project in Google code.
 */




//################################################################
// studPlayer class
//################################################################


import java.lang.Math;

public class chen666Player extends Player {
    
    private int step = 1;
    private static final int MAX_DEPTH = 50;
    /* Use IDS search to find the best move. The step starts from 1 and keeps incrementing by step 1 until
	 * interrupted by the time limit. The best move found in each step should be stored in the
	 * protected variable move of class Player.
     */
    public void move(GameState state)
    {        
        GameState curr = new GameState(state);

        for (int step = 0 ; step < MAX_DEPTH; step++) {
            move = maxAction(state, step);
            state = new GameState(curr);
        }
    }

    // Return best move for max player. Note that this is a wrapper function created for ease to use.
	// In this function, you may do one step of search. Thus you can decide the best move by comparing the 
	// sbe values returned by maxSBE. This function should call minAction with 5 parameters.
    public int maxAction(GameState state, int maxDepth)
    {
        if (state.gameOver()) {
            return sbe(state);
        }

        int action     = -1;    
        int alpha      = Integer.MIN_VALUE;
        int beta       = Integer.MAX_VALUE; 
        int result     = alpha; 
        GameState curr = new GameState(state);
        
        // Select the action which maximaize the SBE valude
        for (int i = 0; i < 6; i++) {
            int temp;
            // Generate successor
            if (!state.illegalMove(i)) {
                if (state.applyMove(i)) {
                    temp = maxAction(state, 1, maxDepth, alpha, beta);
                } else {
                    temp = minAction(state, 1, maxDepth, alpha, beta);
                }

                if (result < temp){
                    result = temp;
                    action = i;    
                }

                if (temp >= beta) {
                    return temp;
                }
                alpha = Math.max(alpha, temp);
            }             
            state = new GameState(curr);
        }

        return action;
    }

	//return sbe value related to the best move for max player
    public int maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
        if (state.gameOver()) {
            return sbe(state);
        }
        if (maxDepth <= currentDepth) {
            return sbe(state);
        }

        int temp        = Integer.MIN_VALUE;
        GameState curr  = new GameState(state);

        for (int i = 0; i < 6; i++) {
            if (!state.illegalMove(i)) {
                
                // Generate successor
                if (state.applyMove(i)) {
                    temp = Math.max(temp, maxAction(state,currentDepth+1,maxDepth,alpha,beta));
                } else {
                    temp = Math.max(temp, minAction(state,currentDepth+1,maxDepth,alpha,beta));                
                }
                
                // pruning
                if (temp >= beta) {
                    return temp;
                }
                alpha = Math.max(alpha, temp);
            }
            state = new GameState(curr);
        }
        return temp;
    }
    //return sbe value related to the bset move for min player
    public int minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
        if (state.gameOver()) {
            return sbe(state);
        }      
        if (maxDepth <= currentDepth) {
            return sbe(state);
        }
        
        int temp        = Integer.MAX_VALUE;
        GameState curr  = new GameState(state);

        for (int i = 7; i < 13; i++) {
            if (!state.illegalMove(i)) {
                
                // Generate Successor
                if (state.applyMove(i)) {
                    temp = Math.min(temp, minAction(state, currentDepth+1, maxDepth, alpha, beta));
                } else {
                    temp = Math.min(temp, maxAction(state, currentDepth+1, maxDepth, alpha, beta));
                }
                
                //pruning
                if (temp <= alpha) {
                    return temp;
                }
                beta = Math.min(beta, temp);    
            }
            state = new GameState(curr);
        }
        return temp;
    }

    //the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
    private int sbe(GameState state){
        int myStones    = 0;
        int otherStones = 0;
        int score       = 0;
        
        for(int i = 0; i < 6; i++) {
            myStones    += state.stoneCount(0, i);
            otherStones += state.stoneCount(1, i);
        }
        
        myStones    += 20*state.stoneCount(6);
        otherStones += 20*state.stoneCount(13);
        
        score = myStones - otherStones + (3*getExtra(state));
        
        if(state.gameOver()) {
            int diff = state.stoneCount(6) - state.stoneCount(13);
            score += diff * 10000;
            //System.out.println("Over diff: " + diff + " " + diff*9999 + " " + score);
        }
    
        return score;
    }
    
    static public int getSteal(int row, GameState state, int start, int stones) {

        int steal  = 0;
        int endPos = start + stones;

        if(endPos == 6) {
            steal += 1;
        }
        
        if(endPos >= 12) {
            steal ++;
        }

        if(endPos >= 6) {
            steal ++;
            endPos %= 13;
        }

        // Move to an empty block and get all the stone
        if((endPos < 6) && (state.stoneCount(row, endPos) == 0)) {  
            if(row == 0) {
                steal += (1 + state.stoneCount(state.neighborOf(endPos)));
            }
            else {
                steal += (1 + state.stoneCount(state.neighborOf(endPos+7)));
            }
        }
        return steal;
    }
    
    static public int getExtra(GameState state) {
        int myExtra    = 0;
        int otherExtra = 0;
        
        for(int i = 0; i < 6; i++) {
            int stones0  = state.stoneCount(0, i);
            int stones1  = state.stoneCount(1, i);
            
            // Available move
            if(stones0 > 0) {
                myExtra += getSteal(0, state, i, stones0);
            }
            
            if(stones1 > 0) {
                otherExtra += getSteal(1, state, i, stones1);
            }
        }
            
        return myExtra - otherExtra;
    }
    
}

