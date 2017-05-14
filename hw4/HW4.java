import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Do not modify.
 * 
 * This is the class with the main function
 */

public class HW4{
	/**
	 * Runs the tests for HW4
	*/
	public static void main(String[] args)
	{
		//Checking for correct number of arguments
		if (args.length != 6) 
		{
			System.out.println("usage: java HW4 <noHiddenNode> " +
					"<learningRate> <maxEpoch> <trainFile> <testFile> <randomSeed>");
			System.exit(-1);
		}
		
		
		//Reading the training set 	
		ArrayList<Instance> trainingSet=getData(args[3]);
		
		
		//Create the weight structure from supplied topography
		Double[][] hiddenWeights=new Double[Integer.parseInt(args[0])][];
		for(int i=0;i<hiddenWeights.length;i++)
		{
			hiddenWeights[i]=new Double[trainingSet.get(0).attributes.size()+1];
		}
		
		Double[] outputWeights=new Double[hiddenWeights.length+1];
		
		
		initializeWeights(hiddenWeights,outputWeights,Integer.parseInt(args[5]));
		
		Double learningRate=Double.parseDouble(args[1]);
		
		if(learningRate>1 || learningRate<=0)
		{
			System.out.println("Incorrect value for learning rate\n");
			System.exit(-1);
		}
		
		NNImpl nn=new NNImpl(trainingSet,Integer.parseInt(args[0]),Double.parseDouble(args[1]),Integer.parseInt(args[2]), 
					hiddenWeights,outputWeights);
		nn.train();
			
		//Reading the test set 	
		ArrayList<Instance> testSet=getData(args[4]);
				
		System.out.println("Evaluating test set: ");		
		for(int i=0;i<testSet.size();i++)
		{
			//Getting output from network
			double predicted = nn.calculateOutputForInstance(testSet.get(i));
			double actual = testSet.get(i).output;
			System.out.printf("Instance " + (i) + " Actual: %.5f, Predicted: %.5f\n", actual, predicted);

		}		
		System.out.printf("Training set mean squared error: %.5f\n",nn.getMeanSquaredError(trainingSet));
		System.out.printf("Test set mean squared error: %.5f\n",nn.getMeanSquaredError(testSet));
			
	}
		
	// Reads a file and gets the list of instances
	private static ArrayList<Instance> getData(String file)
	{
		ArrayList<Instance> data=new ArrayList<Instance>();
		BufferedReader in;
		int attributeCount=0;
		int outputCount=0;
		
		try
		{
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) { 
				String line = in.readLine(); 	
				String prefix = line.substring(0, 2);
				if (prefix.equals("//")) {
				} 
				else if (prefix.equals("##")) {
					attributeCount=Integer.parseInt(line.substring(2));
				} else if (prefix.equals("**")) {
					outputCount=Integer.parseInt(line.substring(2));
				}else {
					String[] vals=line.split(",");
					Instance inst = new Instance();
					for (int i=0; i<attributeCount; i++)
						inst.attributes.add(Double.parseDouble(vals[i]));
					for (int i=attributeCount; i < vals.length; i++)
						inst.output=Double.parseDouble(vals[i]);	
					data.add(inst);	
				}
				
			}
			in.close();
			return data;
			
		}
		catch(Exception e)
		{
			System.out.println("Could not read instances: "+e);
		}
		
		return null;
	}

	// Gets weights randomly
	public static void initializeWeights(Double [][]hiddenWeights, Double[]outputWeights, int seed)
	{
		Random r = new Random(seed);
			
		for(int i=0;i<hiddenWeights.length;i++)
		{
			for(int j=0;j<hiddenWeights[i].length;j++)
			{
				hiddenWeights[i][j] = r.nextDouble()*0.01;
			}
		}
				

		for (int j=0; j<outputWeights.length; j++)
		{
			outputWeights[j]= r.nextDouble()*0.01;
		}
		
	}
}
