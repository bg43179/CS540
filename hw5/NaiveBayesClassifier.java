/**
 * Interface class for the naive bayes classifier. For an explanation of methods, see
 * NaiveBayesClassifierImpl.
 * 
 * DO NOT MODIFY
 */
public interface NaiveBayesClassifier {
  void train(Instance[] trainingData);

  double p_l(Label label);

  double p_w_given_l(String word, Label label);

  public Label classify(Instance ins);
  

  public void words_per_label_count();

  public void documents_per_label_count();
}
