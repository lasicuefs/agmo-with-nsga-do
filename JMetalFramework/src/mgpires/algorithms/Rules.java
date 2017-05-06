package mgpires.algorithms;

import java.util.Arrays;
import jmetal.core.Solution;
import jmetal.encodings.variable.ArrayInt;
import jmetal.util.JMException;
import jmetal.util.wrapper.XInt;
import mgpires.solutionType.ArrayIntAndRealSolutionType;

/**
 * This class was created to provide methods with the goal of optimizing the Rule
 * Base
 * @author Matheus Giovanni Pires
 * @email mgpires@ecomp.uefs.br
 * @data 2015/07/21
 */
public class Rules {
    
/**
 * This method delete the duplicate rules from offSpring parameter 
 * @param offSpring Is the chromosome that encodes the rules. Its type must be 
 * ArrayIntAndRealSolutionType
 * @throws JMException 
 */
public static void deleteDuplicateRules(Solution offSpring) throws JMException {
    
    if (offSpring.getType().getClass() == ArrayIntAndRealSolutionType.class) {        
    
        int numberOfRules, numberOfVariables, idx;            
        int[] rule1, rule2, ant1, ant2, verifiedRules;
        boolean flag = false;

        numberOfRules = (int)offSpring.getDecisionVariables()[2].getValue();        
        numberOfVariables = offSpring.numberOfVariables();

        // Initializing the verifiedRules array with -1. This mean that the
        // rules were not evaluated
        verifiedRules = new int[numberOfRules]; 
        for (int i = 0; i < numberOfRules; i++)
            verifiedRules[i] = -1;   

        idx = 1;
        for (int i = 0; i < numberOfRules; i++) {            
            if (verifiedRules[i] == -1) {                
                rule1 = getRule(offSpring, i, numberOfVariables);                            
                verifiedRules[i] = idx;
                idx++;          
                for (int j = 0; j < numberOfRules; j++) {                
                    if (i !=j) {
                        if (verifiedRules[j] == -1) {
                            rule2 = getRule(offSpring, j, numberOfVariables);                            

                            if (Arrays.equals(rule1, rule2)) {
                                // if the rules are equals, one of them will be set with zero
                                verifiedRules[j] = 0;
                                flag = true;
                            }
                            /* if the rules are not equals, I need to compare the
                            antecedent part
                            */                            
                            else {
                                ant1 = getAntecedent(offSpring, i, numberOfVariables);
                                ant2 = getAntecedent(offSpring, j, numberOfVariables);

                                // If the antecedents are equals, both rules will be deleted
                                if (Arrays.equals(ant1, ant2)) {
                                    verifiedRules[i] = 0;
                                    verifiedRules[j] = 0;
                                    flag = true;
                                }
                            }                            
                        }       
                    }
                }          
            }        
        }    
        // if there are duplicate rules, it must delete them   
        if (flag == true) {
            //System.out.println("There are duplicate rules");
            int begin, end, count;
            int maxLength = ((ArrayIntAndRealSolutionType)offSpring.getType()).getMaxLengthOfArrayInt_();
            XInt offs = new XInt(offSpring);
            XInt auxOff = new XInt(offSpring);
            
            idx = 0; count = 0;
            for (int i = 0; i < numberOfRules; i ++) {
                // verifiedRules[i] != 0 means that the "rule i" is single and not conflict
                if (verifiedRules[i] != 0) {
                    // count stores the number of rules (no duplicates) 
                    count++;
                    if (i == 0) {
                        begin = 0;
                        end = numberOfVariables;
                    }
                    else {
                        begin = i * numberOfVariables;
                        end = (i + 1) * numberOfVariables;
                    }                
                    for (int j = begin; j < end; j++) {                    
                        offs.setValue(idx, auxOff.getValue(j));                    
                        idx++;
                    }                
                }
            }
            if (idx < maxLength) {
                // filling the rest of chromosome with -1 (no rules)
                for (int j = idx; j < maxLength; j++)
                    offs.setValue(j, -1);
            }
            // updating the chromosomes' number of rules
            //System.out.println("New number of rules = " + count);
            offSpring.getDecisionVariables()[2].setValue(count);
        }        
    }
    else {
        System.out.println("Rules class > deleteDuplicateRules method error: solution type " + 
            offSpring.getType().getClass() + " invalid");
        System.exit(-1);
    }
    
} // end deleteDuplicateRules method

/**
 * This method returns one rule from the one chromosome
 * @param offSpring Chromosome that encodes the rules (Rule Base)
 * @param idxRule Index of rule
 * @param numberOfVariables Number of variables of the database
 * @return One int array that encodes the rule
 * @throws JMException 
 */
private static int[] getRule(Solution offSpring, int idxRule, int numberOfVariables) throws JMException {
    int[] aux = new int[numberOfVariables];

    // begin is the first index of the rule (offSpring)
    // end is the last index of the rule (offSpring)
    int begin, end;

    if (idxRule == 0) {
        begin = 0;
        end = numberOfVariables;
    }
    else {
        begin = idxRule * numberOfVariables;
        end = (idxRule + 1) * numberOfVariables;
    }

    //System.out.println("getRule method. Idx rule = " + idxRule + " Variables = " + 
       //numberOfVariables + " Begin = " + begin + " End = " + end);
    int idx = 0;
    for (int i = begin; i < end; i++) {
        aux[idx] = ((ArrayInt)offSpring.getDecisionVariables()[0]).getValue(i);
        idx++;
    }
    return aux;        
} // end getRule method

/**
 * This method returns the antecedent of one rule
 * @param offSpring Chromosome that encodes the rules (Rule Base)
 * @param idxRule Index of rule
 * @param numberOfVariables Number of variables of the database
 * @return One int array that encodes the antecedent of the rule
 * @throws JMException 
 */
private static int[] getAntecedent(Solution offSpring, int idxRule, int numberOfVariables) throws JMException {
    int[] aux = new int[numberOfVariables - 1];

    // begin is the first index of the rule (offSpring)
    // end is the last index of the rule (offSpring)
    int begin, end;

    if (idxRule == 0) {
        begin = 0;
        // -1 because I don't want to get the output variable
        end = numberOfVariables - 1;
    }
    else {
        begin = idxRule * numberOfVariables;
        end = ((idxRule + 1) * numberOfVariables) - 1;
    }

    //System.out.println("getRule method. Idx rule = " + idxRule + " Variables = " + 
       //numberOfVariables + " Begin = " + begin + " End = " + end);
    int idx = 0;
    for (int i = begin; i < end; i++) {
        aux[idx] = ((ArrayInt)offSpring.getDecisionVariables()[0]).getValue(i);
        idx++;
    }
    return aux;        
} // end getRule method

/**
 * This method calculates the number of conditions of the rule base
 * @param solution Is the chromosome that encodes the rules
 * @return Number of conditions of the rule base
 * @throws JMException 
 */
public static int calcNumberOfConditions(Solution solution) throws JMException {

    ArrayInt rules = (ArrayInt)solution.getDecisionVariables()[0];
    int numberOfRules = (int)solution.getDecisionVariables()[2].getValue();
    int numberOfVariables = solution.numberOfVariables();
    int length = numberOfRules * numberOfVariables;         
    int var, numberOfConditions;

    // var will show when is an output variable
    var = 1;
    numberOfConditions = 0;
    for (int i = 0; i < length; i++)            
        // var == numberOfVariables_ means that is the ouput variable           
        if (var == numberOfVariables)
            // restart the count
            var = 1;
        else {
            // equal zero is a don't care condition
            if (rules.getValue(i) == 0) {                    
                var++;
            }
            else {
                numberOfConditions++;
                var++;
            }
        }

    return numberOfConditions;
} // end calcNumberOfConditions method
    
    
} // end Rules class
