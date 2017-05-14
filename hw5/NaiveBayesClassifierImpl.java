import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.lang.Math;
/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {

	//THESE VARIABLES ARE OPTIONAL TO USE, but HashMaps will make your life much, much easier on this assignment.

	//dictionaries of form word:frequency that store the number of times word w has been seen in documents of type label
	//for example, comedyCounts["mirth"] should store the total number of "mirth" tokens that appear in comedy documents
   private HashMap<String, Integer> tragedyCounts = new HashMap<String, Integer>();
   private HashMap<String, Integer> comedyCounts  = new HashMap<String, Integer>();
   private HashMap<String, Integer> historyCounts = new HashMap<String, Integer>();
   
   //prior probabilities, ie. P(T), P(C), and P(H)
   //use the training set for the numerator and denominator
   private double tragedyPrior;
   private double comedyPrior;
   private double historyPrior;
   
   //total number of word TOKENS for each type of document in the training set, ie. the sum of the length of all documents with a given label
   private int tTokenSum;
   private int cTokenSum;
   private int hTokenSum;
   
   //full vocabulary, update in training, cardinality is necessary for smoothing
   private HashSet<String> vocabulary = new HashSet<String>();
   
   private int numComedy;
   private int numTragedy;
   private int numHistory;

  /**
   * Trains the classifier with the provided training data
   * Should iterate through the training instances, and, for each word in the documents, update the variables above appropriately.
   * The dictionary of frequencies and prior probabilites can then be used at classification time.
   */
  @Override
  public void train(Instance[] trainingData) {

    for (Instance inst : trainingData) {

      if (inst.label == Label.COMEDY) {
        numComedy += 1;  
        
        for (String token : inst.words) {   
          cTokenSum += 1;
          
          if(!vocabulary.contains(token)){
            vocabulary.add(token);
          }

          if (!comedyCounts.containsKey(token)) {
            comedyCounts.put(token, 1);
          } else {
            comedyCounts.put(token, comedyCounts.get(token)+1);
          }
        }
      } else if (inst.label == Label.TRAGEDY) {
        numTragedy += 1;
        
        for (String token : inst.words) {
          tTokenSum  += 1;
          
          if(!vocabulary.contains(token)){
            vocabulary.add(token);
          }

          if (!tragedyCounts.containsKey(token)) {
            tragedyCounts.put(token, 1);
          } else {
            tragedyCounts.put(token, tragedyCounts.get(token)+1);            
          }
        }
      } else if (inst.label == Label.HISTORY) {
        numHistory += 1;
       
        for (String token : inst.words) {
          hTokenSum  += 1;
          
          if(!vocabulary.contains(token)){
            vocabulary.add(token);
          }

          if (!historyCounts.containsKey(token)) {
            historyCounts.put(token, 1);
          } else {
            historyCounts.put(token, historyCounts.get(token)+1);

          }
        }
      }
    }

    comedyPrior  = numComedy  *1.0 / (double) (trainingData.length);
    tragedyPrior = numTragedy *1.0 / (double) (trainingData.length);
    historyPrior = numHistory *1.0 / (double) (trainingData.length);
  }

  /*
   * Prints out the number of documents for each label
   * A sanity check method
   */
  public void documents_per_label_count(){
    System.out.println("Tragedy: " + numTragedy);
    System.out.println("Comedy:  " + numComedy);
    System.out.println("History: " + numHistory);
  }

  /*
   * Prints out the number of words for each label
	Another sanity check method
   */
  public void words_per_label_count(){
    System.out.println("Tragedy: " + tTokenSum);
    System.out.println("Comedy:  " + cTokenSum);
    System.out.println("History: " + hTokenSum);
  }

  /**
   * Returns the prior probability of the label parameter, i.e. P(COMEDY) or P(TRAGEDY)
   */
  @Override
  public double p_l(Label label) {
    if (label == Label.COMEDY) {
      return comedyPrior;
    } else if (label == Label.TRAGEDY) {
      return tragedyPrior;
    } else if (label == Label.HISTORY) {
      return historyPrior;
    }

    System.out.println("It shold never come here");
    return 0;
  }

  /**
   * Returns the smoothed conditional probability of the word given the label, i.e. P(word|COMEDY) or
   * P(word|HISTORY)
   */
  @Override
  public double p_w_given_l(String word, Label label) {
    double value       = 0.0; 
    double denominator = 0.0;
    double result      = 0.0;

    if (label == Label.COMEDY) {
      if (!comedyCounts.containsKey(word)){
        value = 0.0;
      } else {
        value = comedyCounts.get(word);
      }
      
      denominator = ((double) (vocabulary.size())*0.00001) + cTokenSum;
      result = (((double) (value)) + 0.00001) / denominator;

    } else if (label == Label.TRAGEDY) {
      if (!tragedyCounts.containsKey(word)){
        value = 0.0;
      } else {
        value = tragedyCounts.get(word);
      }
      
      denominator = ((double) (vocabulary.size())*0.00001) + tTokenSum;
      result = (((double) (value)) + 0.00001) / denominator;

    } else if (label == Label.HISTORY) {
      if (!historyCounts.containsKey(word)){
        value = 0.0;
      } else {
        value = historyCounts.get(word);
      }
      
      denominator = ((double) (vocabulary.size())*0.00001) + hTokenSum;
      result = (((double) (value)) + 0.00001) / denominator;

    }
    return result;
  }

  /**
   * Classifies a document as either a Comedy, History, or Tragedy.
   * Break ties in favor of labels with higher prior probabilities.
   */
  @Override
  public Label classify(Instance ins) {
	
	//Initialize sum probabilities for each label
    double probComedy  = 0.0;
    double probTragedy = 0.0;
    double probHistory = 0.0;

	//For each word w in document ins
  //compute the log (base e or default java log) probability of w|label for all labels (COMEDY, TRAGEDY, HISTORY)
    for (int i = 0; i < ins.words.length; i++) {    
      String word = ins.words[i];
      probComedy  += Math.log(p_w_given_l(word, Label.COMEDY));
      probTragedy += Math.log(p_w_given_l(word, Label.TRAGEDY)); 
      probHistory += Math.log(p_w_given_l(word, Label.HISTORY));
    }

	//add to appropriate sum	
		probComedy  += Math.log(p_l(Label.COMEDY));
    probTragedy += Math.log(p_l(Label.TRAGEDY));
    probHistory += Math.log(p_l(Label.HISTORY));
    
	//Return the Label of the maximal sum probability
    if (probComedy == probTragedy) {
      return (comedyPrior > tragedyPrior) ? Label.COMEDY : Label.TRAGEDY;
    } else if (probComedy == probHistory) {
      return (comedyPrior > historyPrior) ? Label.COMEDY : Label.HISTORY;
    } else if (probHistory == probTragedy) {
      return (historyPrior > tragedyPrior) ? Label.HISTORY : Label.TRAGEDY;
    } else if (probComedy > probTragedy && probComedy > probHistory) {
      return Label.COMEDY;
    } else if (probTragedy > probComedy && probTragedy > probHistory) {
      return Label.TRAGEDY;
    } else {
      return Label.HISTORY;
    }
  }  
}
