  
package mgpires.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Matheus Giovanni Pires
 * @email  mgpires@ecomp.uefs.br
 * @data   2014/10/03
 */
public class RuleBase {
    private String[][] ruleBase_;
    private int numberOfRules_;
    private int numberOfVar_; // input and output variables
    //private MembershipDegree MembershipDegree_;
    
    public RuleBase(int var) {
        ruleBase_ = null;
        numberOfRules_ = 0;
        numberOfVar_ = var;
        //this.MembershipDegree_ = new MembershipDegree();
    }   
    /**
     * Returns the fuzzy set from the one rule
     * @param line is the line of the rule base, which is one String matrix.
     * Each line of the matrix represents one rule.
     * @param column is the column of the rule base, which is one String matrix.
     * Each column is one part (fuzzy set) of one rule. 
     * @return  An especific variable from the ruleBase_ attribute 
     */
    public String getFuzzySetfromRule(int line, int column) {        
        return ruleBase_[line][column];
    }

    public String[][] getRuleBase() {
        return ruleBase_;
    }

    public void setRuleBase(String[][] ruleBase_) {
        this.ruleBase_ = ruleBase_;
    }

    public int getNumberOfRules_() {
        return numberOfRules_;
    }

    public void setNumberOfRules_(int numberOfRules_) {
        this.numberOfRules_ = numberOfRules_;
    }

    public int getNumberOfVar_() {
        return numberOfVar_;
    }

    public void setNumberOfVar_(int numberOfVar_) {
        this.numberOfVar_ = numberOfVar_;
    }
    
    /**
    * Returns the number of antecedents of one rule
    * @param idxRule index of the rule
    */
    public int getNumberOfAntecedents(int idxRule) {
        int count = 0;
        /*
        for (int j = 0; j < numberOfVar_; j++)
            System.out.print(ruleBase_[idxRule][j] + " ");
            
            System.out.println(" ");
        */
        // numberOfVar_ - 1 because I want only the input variables of the rule
        for (int i = 0; i < numberOfVar_ - 1; i++)
            // 0 means don't care condition!
            if (ruleBase_[idxRule][i].compareTo("0") != 0)
                count++;
        
        return count;
    }
        
    /**
     *  This method prints the rule base created by Wang-Mendel algorithm
     *  (original version).
     */
    public void printRuleBase() {
        
        System.out.println("");
        System.out.println("Printing the rule base...");
        
        for (int i = 0; i < numberOfRules_; i++) {
            for (int j = 0; j < numberOfVar_; j++) {
                System.out.print(ruleBase_[i][j] + " ");
            }
            System.out.println(" ");
        }
    }
        
    // creates one rule base from partitionFuzzyInput, partitionFuzzyOutput and samples
    // Wang and Mendel algorithm version 1
    public void createRulesWithWangMendel(double[][][] partitionFuzzyInput, double[][][] partitionFuzzyOutput, String[][] samples, 
        int numberOfSamples, int[] numberOfFuzzySets, String[] outputDiscrete, String typeOfOutputVariable) {        
        
        List<Double> listValuesOfMembership = new ArrayList<>(); 
        List<int[]> listOfRules = new ArrayList<>();
        List<Double> listOfDegreeRules = new ArrayList();
        double value, auxDegreeRule;
        int idxSet;
        int[] rule;
        
        for (int i = 0; i < numberOfSamples; i++) {
            auxDegreeRule = 1.0;
            rule = new int[numberOfVar_];
            
            for (int j = 0; j < numberOfVar_; j++) {
                
                for (int k = 0; k < numberOfFuzzySets[j]; k++) {
                    
                    if ((j == numberOfVar_-1) && (typeOfOutputVariable.equals("continuous"))) {
                        //value = calcMembership(samples[i][j], j, k, partitionFuzzyOutput);                        
                        value = MembershipDegree.calcMembership(samples[i][j], j, k, partitionFuzzyOutput, "output");
                        //System.out.println("Sample: " + samples[i][j] + " Set: " + k + " Membership value: " + value);
                    }
                    else if ((j == numberOfVar_-1) && (typeOfOutputVariable.equals("discrete"))) {
                        //value = calcMembership(samples[i][j], outputDiscrete[k]);
                        value = MembershipDegree.calcMembership(samples[i][j], outputDiscrete[k]);
                        //System.out.println("Sample: " + samples[i][j] + " Set: " + k + " Membership value: " + value);
                    }
                    else {                       
                        //value = calcMembership(samples[i][j], j, k, partitionFuzzyInput);
                        value = MembershipDegree.calcMembership(samples[i][j], j, k, partitionFuzzyInput, "input");
                        //System.out.println("Sample: " + samples[i][j] + " Set: " + k + " Membership value: " + value);
                    }                    
                    //System.out.print(value + " ");
                    listValuesOfMembership.add(value);                    
                } // end third for
                
                // returnIdxSet returns the fuzzy set index with highest membership degree in relation to the sample
                idxSet = returnIdxSet(listValuesOfMembership);
                rule[j] = idxSet;                
                Collections.sort(listValuesOfMembership);
                //System.out.println("Idx set " + idxSet + "  Greater degree " + 
                        //listValuesOfMembership.get(listValuesOfMembership.size()-1));
                
                // auxDegreeRule is the rule degree, which is the product among the membership degrees
                auxDegreeRule *= listValuesOfMembership.get(listValuesOfMembership.size()-1);
                listValuesOfMembership.clear();                
                
            } // end second for
            
            /*
            System.out.print("Rule.: ");
            for (int r = 0; r < numberOfVariables; r++) {
                System.out.print(rule[r] + " ");
            } */
            
            listOfRules.add(i, rule);
            listOfDegreeRules.add(i, auxDegreeRule);
            
            //System.out.println(" Rule degree.: " + auxDegreeRule);
            //System.out.println(" ");  
            
        } // end first for        
        
        /*
        System.out.println("Printing the rule base..." + listOfRules.size() + " rules " + listOfDegreeRules.size() + " degrees");
        for (int i = 0; i < listOfRules.size(); i++) {
            int[] get = listOfRules.get(i);
            for (int j = 0; j < get.length; j++) {
                System.out.print(get[j] + " ");               
            }
            System.out.print(listOfDegreeRules.get(i));
            System.out.println(" ");            
        }
        */
        
        // cleanRuleBase eliminates duplicated rules       
        listOfRules = cleanRuleBase(listOfRules, listOfDegreeRules, numberOfVar_);
        numberOfRules_ = listOfRules.size();
        ruleBase_ = new String[numberOfRules_][numberOfVar_];        
        
        //System.out.println(" ");
        //System.out.println("Rule base cleaned! " + numberOfRules_+ " rules ");
        for (int i = 0; i < numberOfRules_; i++) {
            int[] get = listOfRules.get(i);
            for (int j = 0; j < get.length; j++) {                
                ruleBase_[i][j] = String.valueOf(get[j]);
                //System.out.print(ruleBase_[i][j] + " ");
            }            
            //System.out.println(" ");
        }
    } //end createRuleBase method
    
    /**
     * Returns the fuzzy set index which the sample has greater membership degree
     * @param listValuesOfMembership is the list with the membership degrees 
     */
    private int returnIdxSet(List<Double> listValuesOfMembership) {
        double max;
        int idx=0;
        List<Double> list = new ArrayList<>();
        max = 0;
        for (int i = 0; i < listValuesOfMembership.size(); i++) {
            Double get = listValuesOfMembership.get(i);
            if (get >= max) {
                max = get;                
                // I am adding 1 because I want that the first fuzzy set to be one,
                // and not zero! I will let zero for dont' care                
                idx = i + 1;                 
            }            
        }
        return idx;
    }

    // cleanRuleBase elimiates duplicate rules
    private List<int[]> cleanRuleBase(List<int[]> listOfRules, List<Double> listOfDegree, int numberOfVariables) {
        // ant1 is the antecedent of the rule 1
        // ant2 is the antecedent of the rule 2
        String ant1, ant2, aux;        
        List<int[]> newList = new ArrayList<>();
        int count = 0, idx, idxRule;
        int[] auxRule;
        int[] verifiedRules = new int[listOfRules.size()]; 
        double max, degreeRule1, degreeRule2;        
        
        // Initializing the verifiedRules array with -1. This mean that the
        // rules were not evaluated
        for (int i = 0; i < listOfRules.size(); i ++)
            verifiedRules[i] = -1;
        
        idx = 1;
        for (int i = 0; i < listOfRules.size(); i++) {            
            if (verifiedRules[i] == -1) {
                int[] rule = listOfRules.get(i); 
                degreeRule1 = listOfDegree.get(i);
                ant1 = copyAntecedent(rule);
                idxRule = i;                
                verifiedRules[i] = 0; // if the rule was compared, verifiedRules stores zero
                max = 0;            
                for (int j = 0; j < listOfRules.size(); j++) {
                    int[] rule2 = listOfRules.get(j);                
                    degreeRule2 = listOfDegree.get(j);
                    ant2 = copyAntecedent(rule2);                    
                    //System.out.println(ant1 + " = " + ant2 + " ?");
                    if ((i != j) && (verifiedRules[j] == -1) && (ant1.equals(ant2))) {                        
                        //System.out.println("Yes");
                        count++;
                        //System.out.println("Degree 1 = " + degreeRule1 + " Degree 2 = " + degreeRule2);
                        if (degreeRule1 > degreeRule2) {
                            if (degreeRule1 >= max) {
                                idxRule = i;
                                max = degreeRule1;
                            }
                        }
                        else {
                            if (degreeRule2 >= max) {
                                idxRule = j;
                                max = degreeRule2;
                            } 
                        }
                        // set the rule j like compared!
                        verifiedRules[j] = 0;
                    }               
                }
                //System.out.println("Winner degree = " + max + " Winner rule = " + idx);
                verifiedRules[idxRule] = idx;
                idx++;
            }
        }
        
        idx = 0;        
        for (int i = 0; i < verifiedRules.length; i++) {
            // copying the rules to new list            
            if (verifiedRules[i] != 0) { 
                auxRule = new int[numberOfVar_];
                auxRule = listOfRules.get(i);
                newList.add(idx, auxRule);
                idx++;
            }
        }
        //System.out.println(" ");
        //System.out.println("Final number of rules: " + newList.size());
        
        //System.out.println(" ");       
        //System.out.println("Number of duplicate rules.: " + count);
        
        return newList;
        
    } // end cleanRuleBase method
        
    // Returns the antecedent of the one rule
    private String copyAntecedent(int[] rule) {
        String aux = "";
        // length - 1 because I need to get only antecedent of the rule
        for (int k = 0; k < rule.length - 1; k++)
            aux += String.valueOf(rule[k]); 
        
        return aux;
    }
    
} // end class WangMendell
