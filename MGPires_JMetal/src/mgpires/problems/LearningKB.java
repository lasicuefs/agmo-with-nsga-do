package mgpires.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.SolutionType;
import jmetal.util.JMException;
import mgpires.algorithms.Rules;
import mgpires.core.FuzzyReasoning;
import mgpires.core.ObjectivesAccuracyComplexity;
import mgpires.core.PartitionFuzzy;
import mgpires.core.Samples;
import mgpires.solutionType.ArrayIntAndRealSolutionType;

/**
 * @author Matheus Giovanni Pires
 * @email  mgpires@ecomp.uefs.br
 * @data   2015/06/17
 * @comments 
 * This problem has the aim to learn the rules and to ajust the parameters (centers)
 * of the membership functions simultaneously.
 
 * This problem has two objectives:
 *   1. The number of patterns classified as correct
 *   2. Complexity (conditions number of the rule base)
 */
public class LearningKB extends Problem {
    
    private Samples samples_;
    private PartitionFuzzy partition_;
    private int minNumberRules_, maxNumberRules_;   

    @Override
    public void evaluate(Solution solution) throws JMException {                
        
        int numberOfConditions;          
        double accuracy;
        
        accuracy = FuzzyReasoning.classicFuzzyReasoning(solution, samples_, partition_, samples_.getTypeProcedure());
        
        numberOfConditions = Rules.calcNumberOfConditions(solution);
        
        //int numberOfRules = (int)solution.getDecisionVariables()[2].getValue();

        solution.setObjective(0, -1.0 * accuracy);      
        
        solution.setObjective(1, (double)numberOfConditions);
        //solution.setObjective(1, (double)numberOfRules);
        
        
    } // end evaluate method

    public int getMinNumberRules_() {
        return minNumberRules_;
    }

    public int getMaxNumberRules_() {
        return maxNumberRules_;
    }  

    public Samples getSamples_() {
        return samples_;
    }

    public PartitionFuzzy getPartition_() {
        return partition_;
    }    
    
    public LearningKB(SolutionType solutionType) {
        super(solutionType);
    }    

    public LearningKB(String solutionType, Samples samples, PartitionFuzzy partition,
        int minNumberRules, int maxNumberRules) {     
        
        if (solutionType.compareTo("ArrayIntAndRealSolutionType") == 0) {
            problemName_         = "Learning Knowledge Base";            
            numberOfConstraints_ = 0; // no constraints
            numberOfObjectives_  = 2;
            minNumberRules_      = minNumberRules;
            maxNumberRules_      = maxNumberRules;        
            samples_             = samples;
            partition_           = partition;        
            numberOfVariables_   = samples_.getNumberOfVariables();                                   
            
            /* If the dataset is a classification problem, the output variable
            is discrete. If is one regression problem, the output variable is
            continuous.            
            If is discrete, the real part of the chromosome not counts the output
            variable            
            lengthArrayReal is the length of the real array from ArrayIntAndRealSolutionType
            lengthArrayReal is the sum of the number of fuzzy sets of each variable
            */
            // vet contain the number of fuzzy sets of each variable
            int vet[] = partition_.getNumberOfFuzzySets_(); 
            int lengthArrayReal, idx; 
            double[] cores;
            
            lengthArrayReal = 0;
            if (partition.getTypeOfOutputVariable_().equalsIgnoreCase("discrete")) {                
                /* length is the length of vet, without the last variable if type
                of output variable is "discrete" (classification problem)
                */                
                for (int j = 0; j < vet.length - 1; j++)
                    lengthArrayReal += vet[j];
            }                
            else {
                /* length is the length of vet, WITH the last variable if type
                of output variable is NOT "discrete" (regression problem)
                */                
                for (int j = 0; j < vet.length; j++)
                    lengthArrayReal += vet[j];
            }
            
            upperLimit_ = new double[lengthArrayReal];
            lowerLimit_ = new double[lengthArrayReal]; 
            
            /* determining the limits of ArrayReal that encodes the cores values of the 
            membership functions of each variable            
            
            if the output variable is discrete, there will not have core values for it,
            because the problem is a classification problem
            */            
            idx = 0;
            for (int var = 0; var < vet.length - 1; var++) {
                for (int set = 0; set < vet[var]; set++) {                    
                    /* j == 0 is the first set of variable "vet[i]"
                       j == (vet[i] - 1) is the last set of variable "vet[i]"
                     In according to Antonelli et al (2012), Genetic training instance selection in MOEFS A coevolutionary approach,
                     the first and the last fuzzy set are not changed. So, the values of membership functions centers must be
                     equals the uniform partition                    
                    */
                    if ((set == 0) || (set == (vet[var] - 1))) {
                        lowerLimit_[idx] = this.partition_.getValuePartition(var, set, 1, "input");
                        upperLimit_[idx] = this.partition_.getValuePartition(var, set, 1, "input");
                    }   
                    else {
                        cores = this.partition_.getCoreFunctions(var, set, "input");                        
                        lowerLimit_[idx] = cores[0] - ((cores[0] - cores[1]) / 2);
                        upperLimit_[idx] = cores[0] + ((cores[2] - cores[0]) / 2);                        
                    }              
                    idx++;
                }
            } 
            if ((partition.getTypeOfOutputVariable_().equalsIgnoreCase("continuous"))) {
                /* vet[vet.length - 1] gets the number of fuzzy sets of the
                 last variable (output)
                */ 
                for (int set = 0; set < vet[vet.length - 1]; set++) {                           
                    if ((set == 0) || (set == (vet[vet.length - 1] - 1))) {
                        lowerLimit_[idx] = this.partition_.getValuePartition(vet.length - 1, set, 1, "output");
                        upperLimit_[idx] = this.partition_.getValuePartition(vet.length - 1, set, 1, "output");
                    }   
                    else {
                        cores = this.partition_.getCoreFunctions(vet.length - 1, set, "output");                        
                        lowerLimit_[idx] = cores[0] - ((cores[0] - cores[1]) / 2);
                        upperLimit_[idx] = cores[0] + ((cores[2] - cores[0]) / 2);                        
                    }              
                    idx++;
                } 
            }
            
            int maxLength = numberOfVariables_ * maxNumberRules_;
            double[] lowerBounds = new double[maxLength];
            double[] upperBounds = new double[maxLength];
            
            /* The vectors lowerBounds and upperBounds were created to define the
            limits of values of the integer array from ArrayIntAndRealSolutionType
            The attributes upperLimit_ and lowerLimit from Problem class,
            inherited by LearningKB class, were used to define the limits of 
            values of the real array.                     
            */                                   
            idx = 0;
            while (idx < maxLength) {
                for (int j = 0; j < numberOfVariables_; j++) {
                    if (j == numberOfVariables_ - 1) {
                        lowerBounds[idx] = 1;
                        upperBounds[idx] = vet[j];                        
                    }
                    else {
                        // 0 represents don't care condition and it cannot be in output variable                        
                        lowerBounds[idx] = 0;
                        upperBounds[idx] = vet[j];                          
                    } 
                    idx++;
                }
            }            
            
            solutionType_ = new ArrayIntAndRealSolutionType(this, lengthArrayReal, 
                maxLength, minNumberRules_, maxNumberRules_, numberOfVariables_,
                lowerBounds, upperBounds);           
        }
        else {
            System.err.println("Learning Knowledge Base constructor error: solution type " + solutionType + " invalid");
            System.exit(-1);
        }     
    }
    
    /**
     * This method gets one solution from Pareto front. The criterion used was
     * the best accuracy (the first objective of the LearningKB problem)
     * @param population It contain the solutions from Pareto front     
     * @return An object Solution from Pareto front      
     */
    public Solution getSolutionFromPareto(SolutionSet population) {

        int numberOfSolutions = population.size(), index = 0;        
        double max = population.get(0).getObjective(0);  

        System.out.print("\n");
        for (int i = 0; i < numberOfSolutions; i++) { 
            /* max will store the higher value of accuracy (objective zero)
            as the jMetal code minimizes the objectives, when is necessary to maximize, 
            must multiplicate the objective by -1. So, because of this, the code below does < max
            */            
            if (population.get(i).getObjective(0) < max) {
                max = population.get(i).getObjective(0);
                index = i;
            }
        }        
        return population.get(index);      
    } // end of getSolutionFromPareto method 
    
    /**
     * This method gets one solution from Pareto front. The criterion used was
     * the solution closest the mid point of Pareto front of the LearningKB problem
     * @param population It contain the solutions from Pareto front     
     * @return An object Solution from Pareto front      
     */
    public Solution getMidPointSolutionFromPareto(SolutionSet population) {
        
        int numberOfSolutions = population.size(), index = -1;               
        double accuracy, complexity;        
        List<ObjectivesAccuracyComplexity> listSolution = new ArrayList<>(); 
        
        if (numberOfSolutions == 0) {
            System.err.println("LearningKB class > getMidPointSolutionFromPareto method error: no solution found");
            System.exit(-1);
        }
        else if (numberOfSolutions <= 2)
            index = 0;
        else {
            //System.out.print("\n");
            for (int i = 0; i < numberOfSolutions; i++) {             
                //System.out.println(population.get(i).getObjective(0) + " " + population.get(i).getObjective(1));            
                accuracy   = -1.0 * population.get(i).getObjective(0);
                complexity = population.get(i).getObjective(1);            
                ObjectivesAccuracyComplexity objectives = new ObjectivesAccuracyComplexity(accuracy, complexity, i);            
                listSolution.add(objectives);       
            }        
            //System.out.print("\n");
            //printList(listSolution);        
            index = getIndexOfMidpointSolution(listSolution);        
        }
        
        return population.get(index);      
    } // end of getMidPointSolutionFromPareto method 
    
    private static void printList(List<ObjectivesAccuracyComplexity> list) {
        
        for (int i = 0; i < list.size(); i++) {
            ObjectivesAccuracyComplexity get = list.get(i);
            System.out.print(get.getAccuracy() + " " + get.getComplexity() + " " + get.getIndex() + "\n");            
        }
        
    }

    /**
     * This method return the index of solution closest the mid point of the Pareto front.
     * @param list This list has the solutions of the Pareto front
     * @return Index of solution
     */
    private int getIndexOfMidpointSolution(List<ObjectivesAccuracyComplexity> list) {
        
        double accuracyMax, complexityMax, accuracyMin, complexityMin, midPointX, midPointY, dist, distMin;
        int idx;
        
        Collections.sort(list);       
        
        //System.out.print("\nList ordered...\n");
        //printList(list);
        
        accuracyMin = list.get(0).getAccuracy();
        complexityMin = (double)list.get(0).getComplexity();
        
        accuracyMax = list.get(list.size()-1).getAccuracy();
        complexityMax = (double)list.get(list.size()-1).getComplexity();
        
        //System.out.println("\nMin accucary = " + accuracyMin + " Min complexity = " + 
           //complexityMin + " Index = " + list.get(0).getIndex());
        
        //System.out.println("Max accucary = " + accuracyMax + " Max complexity = " + 
           //complexityMax + " Index = " + list.get(list.size()-1).getIndex());
        
        midPointX = (accuracyMin + accuracyMax) / 2;
        midPointY = (complexityMin + complexityMax) / 2;
        
        //System.out.println("Mid point: X = " + midPointX + " Y = " + midPointY + "\n");
        
        /*I will calculate the Euclidean distance among the midPoint and the solutions, 
        however, I will not consider the first and the last solution. Because of
        this, the for starts in 1 and finish in list.size() - 1
        */       
        distMin = Math.sqrt(            
                (list.get(1).getAccuracy() - midPointX) * (list.get(1).getAccuracy() - midPointX) +
                (list.get(1).getComplexity() - midPointY) * (list.get(1).getComplexity() - midPointY)
            );           
           
        idx = -1;
        for (int i = 1; i < list.size() - 1; i++) {
            ObjectivesAccuracyComplexity get = list.get(i);           
            
            dist = Math.sqrt(            
                (get.getAccuracy() - midPointX) * (get.getAccuracy() - midPointX) +
                (get.getComplexity() - midPointY) * (get.getComplexity() - midPointY)
            ); 
            
            if (dist <= distMin) {
                distMin = dist;
                idx = get.getIndex();                
            }              
            //System.out.println(get.getAccuracy() + " " + get.getComplexity() + " " 
               //+ get.getIndex() + " " + dist);
        }
        //System.out.println("IDX = " + idx);
        return idx;       
    } //end getIndexOfMidpointSolution method
    
} // end LearningKB class
