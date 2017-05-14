/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check the type attribute of the node for details
 * 
 */


import java.util.*;
import java.lang.Math.*;

public class Node{
	private int type=0; //0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
	public ArrayList<NodeWeightPair> parents=null; //Array List that will contain the parents (including the bias node) with weights if applicable
		 
	private Double inputValue=0.0;
	private Double outputValue=0.0; // Output value of a node: same as input value for an input node, 1.0 for bias nodes
	private Double sum=0.0; // sum of wi*xi
	
	//Create a node with a specific type
	public Node(int type)
	{
		if(type>4 || type<0)
		{
			System.out.println("Incorrect value for node type");
			System.exit(1);
			
		}
		else
		{
			this.type=type;
		}
		
		if (type==2 || type==4)
		{
			parents=new ArrayList<NodeWeightPair>();
		}
	}
	
	//For an input node sets the input value which will be the value of a particular attribute
	public void setInput(Double inputValue)
	{
		if(type==0)//If input node
		{
			this.inputValue=inputValue;
		}
	}
	
	/**
	 * Calculate the output of a ReLU node.
	 * You can assume that outputs of the parent nodes have already been calculated
	 * You can get this value by using getOutput()
	 * @param train: the training set
	 */
	public void calculateOutput()
	{
		
		if(type==2 || type==4)//Not an input or bias node
		{	
			this.sum = 0.0;
			for(int i = 0; i < this.parents.size(); i++){
				double xi = this.parents.get(i).node.getOutput();
				double wi = this.parents.get(i).weight;
				this.sum += xi * wi;
			}
			this.outputValue = getReLU(this.sum);
		}
	}

	public double getReLU(double in) {
		return Math.max(in, 0.0);
	}

	public double getSum() {
		return sum;
	}
	
	//Gets the output value
	public double getOutput()
	{
		
		if(type==0)//Input node
		{
			return inputValue;
		}
		else if(type==1 || type==3)//Bias node
		{
			return 1.00;
		}
		else
		{
			//compute the ReLU of the sum of the weighted inputs to the current node
			return outputValue;
		}
		
	}
}


