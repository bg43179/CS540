import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.lang.Math;
import java.util.Arrays;
/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 5 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
  private DecTreeNode root;
  //ordered list of class labels
  private List<String> labels; 
  //ordered list of attributes
  private List<String> attributes; 
  //map to ordered discrete values taken by attributes
  private Map<String, List<String>> attributeValues; 
  
  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }

  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {

    this.labels          = train.labels;
    this.attributes      = train.attributes;
    this.attributeValues = train.attributeValues;
    List<String> copy    = new ArrayList<String>(attributes);
    
    root = buildTree(train.instances, copy, getMajorClass(train.instances), null);
  }
  
  /**
   * Helper function for building the decision tree
   *
   * @param examples: Data for building the decission tree
   * @param attrs: List of attribute for determinig which attribute is used to split at node
   * @param dLables: Default-label
   * @param parent: Parent's value of attribute
   * @return Decision Tree
   */
  private DecTreeNode buildTree(List<Instance> examples, List<String> attrs, String dLabel, String parent) {
    //No more example

    if (examples.isEmpty()) {
      return new DecTreeNode(dLabel, null, parent ,true);
    }

    // All same label condition
    Boolean sameLabel = true;
    String  temp      = examples.get(0).label;
    for (Instance example : examples) {
      if (!example.label.equals(temp)) {
        sameLabel = false;
        break;
      }
    }
    if (sameLabel) {
      return new DecTreeNode(temp, null, parent, true);
    }

    // No more attribute can be selected 
    if (attrs.isEmpty()) {
      return new DecTreeNode(getMajorClass(examples), null, parent, true);
    }

    String q      = maxInfoGain(examples, attrs);
    int    qIndex = this.getAttributeIndex(q);
    List<Instance> subset = new ArrayList<Instance>(); 
    
    // Create a new node with attribute q
    DecTreeNode  tree          = new DecTreeNode(getMajorClass(examples), q, parent, false);
    List<String> copyAttribute = new ArrayList<String>(attrs);
    copyAttribute.remove(q);

    // Partition example based on different value of specific q
    for (String attr : attributeValues.get(q)) {
    
      for (Instance example : examples) {
        if (attr.equals(example.attributes.get(qIndex))) {
          subset.add(example);
          // Remove example here will lead to the later cruch --> cannot do that here
        }    
      } 
      DecTreeNode subtree;
      // Deal with the empty subset
      if (subset.isEmpty()) {
        subtree = buildTree(subset, copyAttribute, dLabel, attr);
      } else {
        subtree = buildTree(subset, copyAttribute, getMajorClass(subset), attr);
      }

      subset = new ArrayList<Instance>();
      tree.addChild(subtree);     
    }
    return tree;
  }
  /**
   * Build a decision tree given a training set then prune it using a tuning set.
   * 
   * @param train: the training set
   * @param tune: the tuning set
   */
  DecisionTreeImpl(DataSet train, DataSet tune) {

    this.labels              = train.labels;
    this.attributes          = train.attributes;
    this.attributeValues     = train.attributeValues;
    List<String> copy        = new ArrayList<String>(attributes);
    
    root = buildTree(train.instances, copy, getMajorClass(train.instances), null);

    double fPruneAccu  = 0.0;
    double sPruneAccu  = 1.0;

    // Keep pruning until no more improvement
    while (sPruneAccu > fPruneAccu) {

      if (sPruneAccu != 1.0) {
        fPruneAccu = sPruneAccu; 
      }
      sPruneAccu = pruningTree(root, tune);
    }

    // Make sure this size of tree is the smallest
    double treeSize = nodeCount(root);
    pruningTree(root, tune);
    double modSize  = nodeCount(root);

    while (modSize < treeSize) {
      treeSize = modSize;
      pruningTree(root, tune);
      modSize  = nodeCount(root);
    }
  }
  
  /**
   * Helpr function for pruning the decision tree
   *
   *@param node: root node of the original tree
   *@param tune: tuning set 
   */
  private double pruningTree(DecTreeNode node, DataSet tune){
    double            tuneAccu  = 0.0; 
    List<DecTreeNode> nodes     = new ArrayList<DecTreeNode>();
    List<DecTreeNode> treeSet   = new ArrayList<DecTreeNode>();

    interNode(node, nodes);
    tuneAccu = getAccuracy(tune);
    
    for (DecTreeNode n : nodes) {
      treeSet.add(treeCopy(this.root, n));      
    }
    
    double      newAccu  = 0.0;
    DecTreeNode tempRoot = root;
    
    // Compare accuracy of new Tree and decide which one is going to be pruned 
    for (DecTreeNode newTree : treeSet) {
      
      this.root = newTree;
      newAccu =  getAccuracy(tune);

      // Select the tree with higher accuracy, if tie, select the tree with fewer nodes
      if (newAccu > tuneAccu) {
        tuneAccu = newAccu;
        tempRoot = newTree;
      } else if (newAccu == tuneAccu) {
        if (nodeCount(newTree) < nodeCount(tempRoot)) {
          tuneAccu = newAccu;
          tempRoot = newTree;
        }
      } else {
        this.root = tempRoot;
      }
    }
    return  tuneAccu;
  }
  
  /**
   * Helper function for traversing all the internal nodes of the tree 
   *
   * @param node: the node travering start
   * @param nodeList: list to store all the node
   */
  public void interNode(DecTreeNode node, List<DecTreeNode> nodeList) {
    if (node == null) {
      return ;
    }
    if (node.children == null) {
      return ;
    }
    if (node.terminal == true) {
      return ;
    }  
    
    nodeList.add(node);
    
    for (DecTreeNode child : node.children) {     
      interNode(child, nodeList);
    }
  }
  
  /**
   * Helper function for counting the number of inter node
   *
   * @param node: count the total number of child from this node
   */
  private int nodeCount(DecTreeNode node) {
    List<DecTreeNode> count = new ArrayList<DecTreeNode>();
    interNode(node, count);
    return count.size();
  }
  
  /**
   * Helper function for copying the tree
   *
   * @param node: root for the tree you wnat to copy
   * @param delete: The node to be delete
   */
  public DecTreeNode treeCopy(DecTreeNode node, DecTreeNode delete) {
    if (node.terminal == true) {
      return new DecTreeNode(node.label, null, node.parentAttributeValue, true);
    } else if (node == delete) {
      return new DecTreeNode(node.label, null, node.parentAttributeValue, true);   
    } else {
      DecTreeNode temp = new DecTreeNode(node.label, node.attribute, node.parentAttributeValue, false);
      for (DecTreeNode child : node.children) {
        temp.addChild(treeCopy(child, delete));
      }
      return temp;
    }
  }

  @Override
  public String classify(Instance instance) {
    return treeTraverse(instance, root);
  }
  /**
   * Helper function for classifying
   *
   *@param instance:  
   *@param node: 
   */
  public String treeTraverse(Instance instance, DecTreeNode node) {
    if (node.terminal == true) {
      return node.label;
    }

    int    indexOfAttr = this.getAttributeIndex(node.attribute);
    String output = "";

    for (DecTreeNode child : node.children) {
      if (instance.attributes.get(indexOfAttr).equals(child.parentAttributeValue)) {        
        output = treeTraverse(instance, child);
      }
    }
    return output;
  }

  @Override
  public void rootInfoGain(DataSet train) {
    this.labels          = train.labels;
    this.attributes      = train.attributes;
    this.attributeValues = train.attributeValues;

    Double infoGain = 0.0;

    for (String attr: this.attributes) {
      infoGain = getEntropy(train.instances) - getCond(train.instances, attr);
      System.out.printf(attr + ": %.5f\n",infoGain);
    }
  }
  
  @Override
  /**
   * Print the decision tree in the specified format
   */
  public void print() {
    printTreeNode(root, null, 0);
  }

  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }

  /**
   * Helper function to get the index of the label in labels list
   */
  private int getLabelIndex(String label) {
    for (int i = 0; i < this.labels.size(); i++) {
      if (label.equals(this.labels.get(i))) {
        return i;
      }
    }
    return -1;
  }
 
  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    for (int i = 0; i < this.attributes.size(); i++) {
      if (attr.equals(this.attributes.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }
  
  
  /**
   * Returns the accuracy of the decision tree on a given DataSet.
   */
  @Override
  public double getAccuracy(DataSet ds) {
    Double result = 0.0;
    Double count  = 0.0;

    for (Instance data : ds.instances) {
      count += 1;
      if (data.label.equals(classify(data))){
        result += 1;
      }
    }
    
    return result/count;
  }

  /**
   * Helper function for deciding the majority class of example in each nodes
   *
   * @param examples: testing examples passed to specific node
   */
  private String getMajorClass(List<Instance> examples) {
    if (examples.isEmpty()) {
      return null;
    }

    HashMap<String, Integer> hm = new HashMap<String, Integer>();
    // Add all label into hashMap
    for (Instance example : examples) {
      Integer i = hm.get(example.label);
      
      if (null == i) {
        hm.put(example.label, 1);
      } else {
        hm.put(example.label, i+1);
      }  
    } 
    // Select the max key value and return the key
    int    max      = Collections.max(hm.values());
    String maxClass = "";
    
    for (Map.Entry<String, Integer> entry : hm.entrySet()) {
      if (entry.getValue() == max) {
        maxClass = entry.getKey();
      }
    }
    return maxClass;
  }
  
  /**
   * Helper function for selecting the attribute with greatest information gain
   *
   * @param examples: testing examples
   * @param attrs: given a specific attribute for classifying the example
   */
  private String maxInfoGain(List<Instance> examples, List<String> attrs) {
    double   entro        = 0.0;
    double   condEntorpy  = 0.0;
    double   infoGain     = 0.0;


    // Calculate of number (normal)
    entro = getEntropy(examples);

    // Calculate conditional entropy
    // P(X1)P(Y|X1) + P(X2)P(Y|X2) + ....    
    double  minInfo = 1.0;
    double  temp    = 0.0;
    String  maxAttr = "";

    for (String attr : attrs) {
      temp     = getCond(examples, attr);
      infoGain = entro - temp;
      if (temp < minInfo) {
        minInfo = temp;
        maxAttr = attr;
      }
      if (maxAttr.equals("")) {
        maxAttr = attr;
      }
    }

    return maxAttr;
  }

  /**
   * Helper function for calculating entropy
   *
   * @param examples: testig examples
   *
   */
  private double getEntropy(List<Instance> examples) {
    double    entropy     = 0.0;
    double[]  proportion  = new double[this.labels.size()];    
    Arrays.fill(proportion, 0.0);

    for (int i = 0; i < this.labels.size(); i++) {
      for (Instance example : examples) {
        if (this.labels.get(i).equals(example.label)) {
          proportion[i] += 1;
        }
      }      
    }
    // Calculate entropy
    for (int i = 0; i < proportion.length; i++) {
        entropy += -1* (proportion[i]/(examples.size()*1.0)) * Math.log(proportion[i]/(examples.size()*1.0))/Math.log(2);
    }

    return entropy;
  }
  
  /**
   * Helper function for calculating conditional entropy with given attr
   *
   * @param examples: 
   * @param attr: given 
   */
  private double getCond(List<Instance> examples, String attr) {
    List<String> values      =  this.attributeValues.get(attr);
    int[]        countP      =  new int[values.size()]; 
    int[][]      countL      =  new int[values.size()][this.labels.size()];
    int          indexOfAttr =  this.getAttributeIndex(attr); //need to fix number
    int          counter     =  0;
    double[]     sum         =  new double[values.size()];
   
    Arrays.fill(countP, 0);
    Arrays.fill(sum, 0.0);
    for (int i=0; i<values.size(); i++) {
      Arrays.fill(countL[i], 0);
    }    

    // Get proportion of each value of an given attribute
    for (int i = 0; i < values.size(); i++) {
      for (Instance example : examples) {       
        if (example.getAttribute(indexOfAttr).equals(values.get(i))) {        
          for (int j = 0; j < this.labels.size(); j++) {
            if (this.labels.get(j).equals(example.label)) {
              countL[i][j] += 1;
              counter ++;
            }           
          } 
          countP[i] += 1;
        }
      }
    }
    
    // Count the total number of example while X is differnt value
    for (int i = 0; i < values.size(); i++) {
      for (int j =0; j < this.labels.size(); j++) {
        sum[i] += countL[i][j];
      }
    }

    double result    = 0.0; 
    double exception = 0.0; // Deal with log 0 or log 1 
    
    // Calculate the condtional entropy
    for (int i = 0; i < values.size(); i++) {
      for (int j =0; j < this.labels.size(); j++) {
        if (sum[i] == 0.0 || 1 == countL[i][j]/sum[i] || 0 == countL[i][j]/sum[i]) {
          result += exception;
        } else {
          result += ( (countP[i]/(examples.size()*1.0)) * -1 * (countL[i][j]/sum[i]) * (Math.log(countL[i][j]/sum[i])/ Math.log(2)) );
        }
        
      }
    }
    return result;
  } 

}