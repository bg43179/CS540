import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * This is the main method that will load the application.
 * 
 * DO NOT MODIFY
 */

public class HW5 {
  /**
   * Creates a fresh instance of the classifier.
   * 
   * @return a classifier
   */
  private static NaiveBayesClassifier getNewClassifier() {
    NaiveBayesClassifier nbc = new NaiveBayesClassifierImpl();
    return nbc;
  }

  /**
   * Main method reads command-line flags and outputs either the classifications of the test file or
   * uses cross-validation to compute a mean accuracy of the classifier.
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("usage: java HW5 <mode> <trainingFilename> <testFilename>");
    }

    // Output classifications on test data
    int mode = Integer.parseInt(args[0]);
    File trainingFile = new File(args[1]);
    File testFile = new File(args[2]);

    Instance[] trainingData = createInstances(trainingFile);
    Instance[] testData = createInstances(testFile);

    NaiveBayesClassifier nbc = getNewClassifier();
    nbc.train(trainingData);
   
    if(mode==0) 
        nbc.documents_per_label_count();
    else if(mode==1)
        nbc.words_per_label_count();
       
    else if (mode==2){
		int correct = 0;
		for (Instance i: testData){
			Label guess = nbc.classify(i);
			System.out.println("Output: " + guess + " " + "Label: " + i.label);
			if (guess.equals(i.label)){
				correct++;
			}
		}
		System.out.printf("Test Set Accuracy: %.5f\n", (double)correct/testData.length);
    }
  }


 
  /**
   * Reads the lines of the input file, treats the first token as the label and cleanses the
   * remainder, returning an array of instances.
   * 
   * @param f
   * @return
   * @throws IOException
   */
  private static Instance[] createInstances(File f) throws IOException {
    String[] ls = lines(f);
    Instance[] is = new Instance[ls.length];
    for (int i = 0; i < ls.length; i++) {
      String[] ws = cleanse(ls[i]).split("\\s");
      is[i] = new Instance();
      is[i].words = drop(ws, 1);
      is[i].label = Label.valueOf(ws[0].toUpperCase());
    }
    return is;
  }

  /**
   * Some cleansing helps "thicken" the densities of the data model.
   * 
   * @param s
   * @return the string with punctuation removed and uniform case
   */
  private static String cleanse(String s) {
    s = s.replace("?", " ");
    s = s.replace(".", " ");
    s = s.replace(",", " ");
    s = s.replace("/", " ");
    s = s.replace("!", " ");
	s = s.replace(":", " ");
    s = s.replace("(", " ");
	s = s.replace(")", " ");
    s = s.replace(";", " ");
    return s.toLowerCase();
  }

  public static String[] lines(File f) throws IOException {
    FileReader fr = new FileReader(f);
    String[] l = lines(fr);
    fr.close();
    return l;
  }

  public static String[] lines(Reader r) throws IOException {
    BufferedReader br = new BufferedReader(r);
    String s;
    List<String> data = new ArrayList<String>();
    while ((s = br.readLine()) != null && !s.isEmpty()) {
      data.add(s);
    }
    br.close();
    return data.toArray(new String[data.size()]);
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] drop(T[] xs, int i) {
    T[] ys = (T[]) Array.newInstance(xs[0].getClass(), xs.length - i);
    System.arraycopy(xs, i, ys, 0, xs.length - 1);
    return ys;
  }

  public static double mean(double[] ds) {
    double sum = 0.0;
    for (int i = 0; i < ds.length; i++) {
      sum += ds[i];
    }
    return sum / ds.length;
  }
}
