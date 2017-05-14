/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;
import java.lang.Math.*;


public class NNImpl{
	public ArrayList<Node> inputNodes=null;//list of the output layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public Node outputNode=null;// single output node that represents the result of the regression
	
	public ArrayList<Instance> trainingSet=null;//the training set
	
	Double learningRate=1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs
	
	
	/**
 	* This constructor creates the nodes necessary for the neural network
 	* Also connects the nodes of different layers
 	* After calling the constructor the last node of both inputNodes and  
 	* hiddenNodes will be bias nodes. 
 	*/
	
	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Double [][]hiddenWeights, Double[] outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;
		
		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		int outputNodeCount=1;
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}
		
		//bias node from input layer to hidden
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);
		
		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}
		
		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);
			


		Node node=new Node(4);
		//Connecting output node with hidden layer nodes
		for(int j=0;j<hiddenNodes.size();j++)
		{
			NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(j), outputWeights[j]);
			node.parents.add(nwp);
		}	
		outputNode = node;
			
	}
	
	/**
	 * Get the output from the neural network for a single instance. That is, set the values of the training instance to
	 * the appropriate input nodes, percolate them through the network, then return the activation value at the single output
	 * node. This is your estimate of y. 
	 */
	
	public double calculateOutputForInstance(Instance inst)
	{
		propogateInst(inst);
		return outputNode.getOutput();
	}
	
	
	/**
	 * Trains a neural network with the parameters initialized in the constructor for the number of epochs specified in the instance variable maxEpoch.
	 * The parameters are stored as attributes of this class, namely learningRate (alpha) and trainingSet.
	 * Implement stochastic graident descent: update the network weights using the deltas computed after each the error of each training instance is computed.
	 * An single epoch looks at each instance training set once, so you should update weights n times per epoch if you have n instances in the training set.
	 */	
	public void train()
	{			
		for (int i = 0; i < maxEpoch; i++) {
			for(int j = 0; j < this.trainingSet.size(); j++){
				Instance inst = this.trainingSet.get(j);
				propogateInst(inst);
				backward(inst);
			}
		}
	}

	public void propogateInst(Instance inst)
	{

		// Set values for input nodes
		for(int i = 0; i < this.inputNodes.size()-1; i++){
			Node node = this.inputNodes.get(i);
			node.setInput(inst.attributes.get(i));
		}

		// Calculate output for hidden nodes
		for(int i = 0; i < this.hiddenNodes.size(); i++){
			this.hiddenNodes.get(i).calculateOutput();
		}
		this.outputNode.calculateOutput();
		
	}
	/**
	 * Returns the mean squared error of a dataset. That is, the sum of the squared error (T-O) for each instance
	 * in the dataset divided by the number of instances in the dataset.
	 */
	public void backward(Instance inst) {
		double   deltaOutput = 0.0;
		double[] deltaHidden = new double[this.hiddenNodes.size()-1];
				
		deltaOutput = getDeltaOutput(inst, outputNode);	
		getDeltaHidden(deltaHidden, deltaOutput);

		updateWeight(outputNode.parents, deltaOutput);
		
		for (int i = 0; i < hiddenNodes.size()-1; i++) {
			updateWeight(this.hiddenNodes.get(i).parents, deltaHidden[i]);
		}
		
	}

	public double getDeltaOutput(Instance inst, Node on) {
		double ink = on.getOutput();
		double Tk  = (double) inst.output;
		double ak  =  outputNode.getOutput();
		return reLu(ink) * (Tk-ak) ;
	}

	public void getDeltaHidden(double[] deltaHidden, double deltaOutput) {	
		for (int i = 0; i < hiddenNodes.size() -1; i++) {
			double inj = hiddenNodes.get(i).getOutput();
			deltaHidden[i] = reLu(inj) * getWeightSum(deltaOutput, i);
		}
	}

	public double getWeightSum(double deltaOutput, int j) {
		double wi = outputNode.parents.get(j).weight;
		double weightSum = wi * deltaOutput; 
		return weightSum;
	}

	public void updateWeight(ArrayList<NodeWeightPair> parents ,double delta) {
		for (int k = 0; k < parents.size(); k++) {
			double ai = parents.get(k).node.getOutput();
			parents.get(k).weight += this.learningRate * ai * delta;
		}
	}

	public double reLu(double in) {
		return (in <= 0.0000000000001) ? 0.0 : 1.0;
	}
	public double getMeanSquaredError(List<Instance> dataset){
		
		double temp   = 0;
		double actual = 0;
		double error  = 0;

		for (Instance example : dataset) {
			temp   = calculateOutputForInstance(example);
			actual = example.output;
			error  += Math.pow((actual - temp),2);
		}
		return error/dataset.size();
	}
}
