package mgpires.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jmetal.core.Solution;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.ArrayReal;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;
import mgpires.solutionType.ArrayIntAndRealSolutionType;

/**
 * @author Matheus Giovanni Pires
 * @email  mgpires@ecomp.uefs.br
 * @data   2014/10/06
 */
public class FuzzyReasoning {    
    
/**
 * This method calculates the number of patterns classified as correct using
 * the Fuzzy Classic Reasoning. This method works only to ArrayIntAndRealSolutionType type  
 * @param solution Is the chromosome that encodes the rules and the core values
 * of membership functions
 * @param samples Samples of the dataset
 * @param partition Partition fuzzy of the variables
 * @param type It indicates if is "training" or "test" phase
 * @return The number of patterns classified as correct
 * @throws JMException 
 */
public static double classicFuzzyReasoning (
    Solution solution, Samples samples, PartitionFuzzy partition,
    String type) throws JMException {

    if (solution.getType().getClass() == ArrayIntAndRealSolutionType.class) {             
    
        int numberOfSamples, numberOfRules, numberOfInputVariables, idx, set, count;
        double prod, value;
        double[] points;
        String[][] auxSamples;        
        List<Double> listCompatRules = new ArrayList<>(); 
        numberOfRules = (int)solution.getDecisionVariables()[2].getValue();         
        numberOfInputVariables = samples.getNumberOfVariables() - 1;           

        if (type.equalsIgnoreCase("training")) {
            auxSamples = getSelectedSamples(samples);
            numberOfSamples = samples.getNumberOfSelectedSamples();
            
        }
        // in this case, only the learning KB will be executed
        else if (type.equalsIgnoreCase("trainingKB")) {
            numberOfSamples = samples.getNumberOfTraSamples();
            auxSamples = samples.getTraSamples();
            
        }
        else {
            numberOfSamples = samples.getNumberOfTestSamples();
            auxSamples = samples.getTestSamples();
        }     

        int[] resultFuzzyReasoning = new int[numberOfSamples]; 

        for (int idxSample = 0; idxSample < numberOfSamples; idxSample++) {            
            /* indice to "run" in ArrayInt (rules)
            idx can not exceed the (number of rules * number of variables) value
            of the chromosome (solution)
            */
            idx = 0;
            for (int idxRule = 0; idxRule < numberOfRules; idxRule++) {                
                prod = 1.0;  
                //System.out.println("\nSample " + (idxSample+1) + " Rule " + (idxRule+1));                
                
                for (int idxVar = 0; idxVar <= numberOfInputVariables; idxVar++) {                    
                    /* if idxVar == numberOfInputVariables means that idxVar is the 
                    ouput variable indice. So, in this case, I only increment idx,
                    to continue reading the input variables of the next rule.
                    */
                    if (idxVar == numberOfInputVariables)
                        idx++;
                    else {
                        // set is the value encoded in the rule idx                        
                        set = ((ArrayInt)solution.getDecisionVariables()[0]).getValue(idx);
                        idx++;
                        // set == 0 means that is a don't care condition. In this case, I don't take into account                        
                        if ((set != 0) && (set != -1)) {
                            // points gets the partition fuzzy values of the set of the idxVar                            
                            points = getPointsOfMembershipFunction(solution, partition, idxVar, set);
                            // values gets the membership degree
                            value = MembershipDegree.calcMembershipTriangularFunction(auxSamples[idxSample][idxVar], points);                              

                            //System.out.println("Var " + (idxVar+1) + " Set " + set + " Point 1 " + points[0] + " Point 2 " + points[1] +
                                //" Point 3 " + points[2] + " Samples " + auxSamples[idxSample][idxVar] + " Degree " + value);

                            // it's applying the product operator
                            prod *= value;                         
                        }
                        else if (set == -1)
                            prod = 0;
                    }                    
                }
                //System.out.println("Degree compatibility: " + prod);
                /* listCompatRules stores the compatitibility degree of all 
                rules "idxRule" with the sample "idxSample" */
                listCompatRules.add(prod);                
            }
            // resultFuzzyReasoning stores the rule index which the sample idxSample has higher compatibility                       
            resultFuzzyReasoning[idxSample] = getRuleIdxWithMaxCompatibility(listCompatRules); 
            //System.out.println("Compatible rule: " + resultFuzzyReasoning[idxSample]);            
            listCompatRules.clear();
        }
                
        //for (int i = 0; i < resultFuzzyReasoning.length; i++)
            //System.out.print(resultFuzzyReasoning[i] + " ");        
        //System.out.println(" ");

        count = 0;
        for (int idxSample = 0; idxSample < numberOfSamples; idxSample++) {
            //System.out.println("Sample " + (i+1) + " is compatible with rule " + (resultFuzzyReasoning[i] + 1));

            // resultFuzzyReasoning[idxSample] == -1 means that has not compatibility between rule and pattern 
            if (resultFuzzyReasoning[idxSample] != -1) {                                                
                // idx is the index on ArrayInt of the output variable of the compatible rule with the sample "idxSample"                
                idx = (resultFuzzyReasoning[idxSample] * samples.getNumberOfVariables()) - 1;                                     
                
                // set is the output variable of the compatible rule with the sample "idxSample"                
                set = ((ArrayInt)solution.getDecisionVariables()[0]).getValue(idx);
                
                //System.out.println("Idx sample = " + (idxSample + 1) + " Output sample = " + auxSamples[idxSample][numberOfInputVariables] +
                    //" Idx compatible rule = " + resultFuzzyReasoning[idxSample] + " Output rule = " + set);
                
                // If the output of the sample is equal to output of the rule, the sample is classified as correct.
                if (auxSamples[idxSample][numberOfInputVariables].equalsIgnoreCase(Integer.toString(set))) {
                    count++;
                    //System.out.println("Is compatible!!!");
                }                    
            }
        }
        /* count is the number of samples classified as correct
           coutn / numberOfSamples is the percentage of correct classification
        */
        //System.out.println("Count = " + count + " Number of samples = " + numberOfSamples + " Accuracy = " + (double)count/numberOfSamples);
        return (double)count / numberOfSamples;
        //return (double)count;
    }
    else {
        System.out.println("FuzzyReasoning class > classicFuzzyReasoning method: Solution type " + solution.getType().getClass() + " invalid");
        System.exit(-1);
        return -1;
    }    
} // end classicFuzzyReasoning method

/**
 * This method calculates the number of patterns classified as correct using
 * the Fuzzy Classic Reasoning
 * @param partition is the partition fuzzy of the variables
 * @param samples are the samples (patterns)
 * @param rulebase are the fuzzy rule
 * @param displacements is the real vector which contains the values of 
 * displacements
 * These real values are the displacements of each fuzzy set 
 * @param ruleSelected is the binary vector which "says" if one rule is
 * selected or not
 * @return The number of patterns classified as correct
 * @throws jmetal.util.JMException
 */
public static int classicFuzzyReasoning(PartitionFuzzy partition, Samples samples, RuleBase rulebase, XReal displacements, Binary ruleSelected) throws JMException {
    // listCompatSample stores the compatitibility degree of each rule with one sample
    List<Double> listCompatSample = new ArrayList<>();       
    double value, prod;
    int count, numberOfSamples, numberOfInputVariables, numberOfRules, index, aux;

    //numberOfSamples = samples.getNumberOfSamples();
    numberOfSamples = samples.getNumberOfTestSamples();
    numberOfRules = rulebase.getNumberOfRules_();        
    numberOfInputVariables = partition.getNumberOfInputVariables_();        
    int[] resultFuzzyReasoning = new int[numberOfSamples];        

    for (int i = 0; i < numberOfSamples; i++) {

        for (int j = 0; j < numberOfRules; j++) {

            if (ruleSelected.getIth(j) == true) {
                aux = 0;
                // prod stores the product of the membership degrees of the
                // jth rule's antecedent
                prod = 1.0;                
                for (int k = 0; k < numberOfInputVariables; k++) {                   
                    /* I did rulease.getFuzzySetfromRule(j,k))-1 because the 
                    fuzzy sets codification starts in 1. In other words, the 
                    first fuzzy set is one, the second is 2, and so on. But, 
                    in the partitionFuzzyInput_ matrix, the first index starts in zero.
                    The vector "displacements" stores the values of displacements
                    for each fuzzy set. So, it's necessary to get the correct
                    displacement value. The code below do this. 
                    */
                    if (k == 0) {
                        index = Integer.parseInt(rulebase.getFuzzySetfromRule(j,k)) - 1;
                        //System.out.println(index);
                    }    
                    else {
                        aux = aux + partition.getNumberOfFuzzySetsOfIthVariable(k - 1);
                        index = (aux + Integer.parseInt(rulebase.getFuzzySetfromRule(j,k))) - 1;
                        //System.out.println("partition = "+ partition.getNumberOfFuzzySetsOfIthVariable(k) + " aux = " + aux);
                    }                        

                    value = MembershipDegree.calcMembershipWithLateralDisplacement(samples.getValueOfTestSamples(i, k), k, 
                            Integer.parseInt(rulebase.getFuzzySetfromRule(j,k))-1,
                            partition.getPartitionFuzzyInput_(), "input", displacements.getValue(index));

                    //System.out.println("rule = " + j + " variable = " + k + " index = " + index + " displacement value = " + displacements.getValue(index));

                    //System.out.println("Value: " + samples.getAttribute(i, k) + " Attribute: " + k + " Fuzzy set: " + 
                            //Integer.parseInt(rulebase.getFuzzySetfromRule(j,k)) + " Membership: " + value);                  

                    // it's applying the product operator
                    prod *= value;                   
                }
                listCompatSample.add(prod);
                //System.out.println("compatibility sample " + (i + 1) + " with rule " + (j + 1) + " = " + compatSample[j]); 
            } // end if                
            else
            /* If the rule is not selected, then the compatibiliy degree
            is zero. I need add zero to listCompatSample because the
            getRuleIdxWithMaxCompatibility method uses the position on the
            list as rule index                
            */
                listCompatSample.add(0.0);
        }

        // resultFuzzyReasoning stores the rule index which the ith sample has higher compatibility            
        resultFuzzyReasoning[i] = getRuleIdxWithMaxCompatibility(listCompatSample);        
        listCompatSample.clear();
    }        
    count = 0;
    for (int i = 0; i < numberOfSamples; i++) {
        //System.out.println("Sample " + (i+1) + " is compatible with rule " + (resultFuzzyReasoning[i] + 1));

        // resultFuzzyReasoning[i] == -1 means that has not compatibility between rule and pattern 
        if (resultFuzzyReasoning[i] != -1) {
            /* If the output from samples_ is equal to output from ruleBase_, the sample is classified as correct            

            samples.getAttribute(i, numberOfInputVariables) is the class
            (output) of the sample

            wangMendel.getFuzzySetfromRule(resultFuzzyReasoning[i], numberOfInputVariables) is
            the class defined by the winner rule

            if these are equals, so, the pattern is classified as correct!                
            */          
            if (samples.getValueOfTestSamples(i, numberOfInputVariables).compareTo(rulebase.getFuzzySetfromRule(resultFuzzyReasoning[i], numberOfInputVariables)) == 0)
                count++;
        }
    }

    return count;       
} // end classicFuzzyReasoning method    

/* this method calculates the number of samples classified correctly using
classic fuzzy reasoning, but without the "displacements" array

public void classicFuzzyReasoning() {
    // listCompatSample stores the compatitibility degree of each rule with one sample
    List<Double> listCompatSample = new ArrayList<>();
    //double[] compatSample = new double[numberOfRules_];
    int[] resultFuzzyReasoning = new int[numberOfSamples_];
    double value, prod;
    int count;

    for (int i = 0; i < numberOfSamples_; i++) {

        for (int j = 0; j < numberOfRules_; j++) {
            // prod stores the product of the membership degrees of the
            // jth rule's antecedent
            prod = 1.0;                
            for (int k = 0; k < numberOfInputVariables_; k++) {                   
                // I did ruleBase_ - 1 because the fuzzy sets codification starts
                // in 1. In other words, the first fuzzy set is one, the second
                // is 2, and so on. But, in the partitionFuzzyInput_ the first
                // index starts in zero.
                value = MembershipDegree_.calcMembership(samples_[i][k], k, Integer.parseInt(ruleBase_[j][k])-1, partitionFuzzyInput_, "input");
                //System.out.println("Value: " + samples_[i][k] + " Attribute: " + k + " Fuzzy set: " + ruleBase_[j][k] + " Membership: " + value);

                // it's applying the product operator
                prod *= value;                   
            }
            //compatSample[j] = prod;       
            listCompatSample.add(prod);
            //System.out.println("compatibility sample " + (i + 1) + " with rule " + (j + 1) + " = " + compatSample[j]);                
        }
        // resultFuzzyReasoning stores the rule index which the sample has higher compatibility
        //resultFuzzyReasoning[i] = getMaxCompatibility(compatSample, numberOfRules_);  
        resultFuzzyReasoning[i] = getRuleIdxWithMaxCompatibility(listCompatSample);
        listCompatSample.clear();
    }

    count = 0;
    for (int i = 0; i < numberOfSamples_; i++) {
        //System.out.println("Sample " + (i+1) + " is compatible with rule " + (resultFuzzyReasoning[i] + 1));

        // resultFuzzyReasoning[i] == -1 means that has not compatibility between rule and pattern 
        if (resultFuzzyReasoning[i] != -1)
            // If the output from samples_ is equal to output from ruleBase_, the sample is classified as correct
            if (samples_[i][numberOfInputVariables_].equals(ruleBase_[resultFuzzyReasoning[i]][numberOfInputVariables_]))
                count++;
    }
    System.out.println("Number of samples classified correctly.: " + count);
    value = (double)count*100/numberOfSamples_;
    System.out.println("Percentage of samples classified correctly.: " + value);


} // end classicFuzzyReasoning method
*/

/**
 * Returns the index of the rule with higher compatibility degree
 * @param list is the list with the compatibility degrees 
 * @return index of the rule with higher compatibility degree. The index starts in one,
 * i.e, the first rule has index one.
 */
private static int getRuleIdxWithMaxCompatibility(List<Double> list) {
    double max, firstMax, secondMax, degree;
    int idx = -1;        
    
    max = 0;
    for (int i = 0; i < list.size(); i++) {        
        degree = list.get(i);
        // max == 0 means that has not compatibility between rule and pattern
        if (degree > max) {
            max = degree;                
            /* idx + 1 because I want that the index of the first rule be 1, the 
            indice of the second rule be 2, and so on. */
            idx = i + 1;
        }            
    }        
    Collections.sort(list);               
    /* 
     firstMax == secondMax means that have 2 or more rules with the same
     compatibility degree, so, in this case, the pattern is not classified
     as correct         
    */
    if (list.size() > 1) {
        firstMax = list.get(list.size()-1);
        secondMax = list.get(list.size()-2);
        if (firstMax == secondMax)
            idx = -1;
    }
       
    return idx;
}

/**
 * Returns the points of a triangular membership function
 * @param solution Is the chromosome
 * @param partition Partition fuzzy of the variables
 * @param idxVar Index of variable
 * @param idxSet Index of fuzzy set
 * @return A double array with three values 
 * (points of triangular membership function)
 * @throws JMException 
 */
private static double[] getPointsOfMembershipFunction(Solution solution, PartitionFuzzy partition, int idxVar, int idxSet) throws JMException {             

    if (solution.getType().getClass() == ArrayIntAndRealSolutionType.class) {

        double[] result = new double[3];
        /* The parameter always will be "input" because this fuzzy reasoning works
        only to classification problems. So, the necessary variables are only the
        input variables
        */
        if ((idxVar < 0) || (idxSet - 1) < 0) {
            System.out.println("FuzzyReasoning class > getPointsOfMembershipFunction method error: index out of bounds!!!");
            System.out.println("idxVar = " + idxVar + " idxSet - 1 = " + (idxSet - 1));
            System.out.println("Number of rules = " + solution.getDecisionVariables()[2].getValue());
            
            ArrayInt inteiros = (ArrayInt)solution.getDecisionVariables()[0];
            int size = inteiros.getLength();        
            for (int k = 0; k < size; k++)
                System.out.print(inteiros.getValue(k) + " ");
            
            System.exit(-1);
        }
        // left extreme of fuzzy set
        result[0] = partition.getValuePartition(idxVar, idxSet - 1, 0, "input");

        // right extreme of fuzzy set
        result[2] = partition.getValuePartition(idxVar, idxSet - 1, 2, "input");                     

        int idx = -1;        
        if ((idxVar == 0) && (idxSet == 0))
            idx = 0;
        else {
            // vet contain the number of fuzzy sets of each variable
            int vet[] = partition.getNumberOfFuzzySets_();
            
            for (int i = 0; i < idxVar; i++)                    
                idx = idx + vet[i];

            idx = idx + idxSet;
        }
        // core of fuzzy set. This value is gotten from chromosome's part real (solution)
        //System.out.println("idxVar = " + idxVar + " idxSet = " + idxSet + " Idx = " + idx + " Size array real = " + cores.size());        
        result[1] = ((ArrayReal)solution.getDecisionVariables()[1]).getValue(idx);

        return result;
    }
    else
        return null;        
    
} // end getPointsOfMembershipFunction method

/**
 * This method returns the matrix with the selected samples
 * @param samples Object with the samples (training and test)
 * @return A matrix string[][] with the selected samples
 */
private static String[][] getSelectedSamples(Samples samples) {
    int size, line;
    boolean[] sel;
    size = samples.getNumberOfTraSamples();
    sel = samples.getSelectedSamples();
    String[][] auxSamples = new String[samples.getNumberOfSelectedSamples()][samples.getNumberOfVariables()];        

    if (sel.length != size) {
        System.err.println("FuzzyReasoning class > getSelectedSamples method error: length of arrays incompatibles");
        System.exit(-1);
    }
    else {
        line = 0;
        //System.out.println("Number of selected samples = " + samples.getNumberOfSelectedSamples());
        for (int i = 0; i < size; i++) {            
            if (sel[i] == true) {
                //System.out.print("Sample " + i + " = ");
                for (int j = 0; j < samples.getNumberOfVariables(); j++) {
                    auxSamples[line][j] = samples.getValueOfTraSamples(i, j);
                    //System.out.print(auxSamples[line][j] + " ");
                }
                //System.out.print("\n");
                line++;
            }            
        }        
    }                
    return auxSamples;
} // end getSelectedSamples method
    
} // end FuzzyReasoning class
