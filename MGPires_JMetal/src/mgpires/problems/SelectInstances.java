package mgpires.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.SolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;
import mgpires.algorithms.KNN;
import mgpires.core.ObjectivesAccuracyComplexity;
import mgpires.core.Samples;
import mgpires.solutionType.ArrayBinarySolutionType;

/**
 * @author Matheus Giovanni Pires
 * @email  mgpires@ecomp.uefs.br
 * @date   2015/05/22
 * @comments 
 * This problem has the aim to select a subset of training data from original 
 * training set
 * This problem has two objectives:
 *   1. accuracy
 *   2. reduction rate
 */
public class SelectInstances extends Problem {
    
    private Samples samples_;
    //private Map<String,Object> distance_;
    double[][] distance_;

    @Override
    public void evaluate(Solution solution) throws JMException {   
        
        double[] test;       
        
        test = KNN.calcAccuracyAndReduction(solution, samples_.getTraSamples(), distance_, samples_.getNumberOfTraSamples(), numberOfVariables_);
        
        // test[0] = accuracy (rate)   test[1] = reduction of samples (rate)
        solution.setObjective(0, -1 * test[0]);
        solution.setObjective(1, -1 * test[1]);
    }

    public SelectInstances(SolutionType solutionType) {
        super(solutionType);
    }

    //public SelectInstances(String solutionType, Samples samples, Map<String,Object> distance ) {
    public SelectInstances(String solutionType, Samples samples, double[][] distance ) {
        problemName_= "Select Instances";
        // In my cases, there will not have constraints
        numberOfConstraints_ = 0;
        // The first objective is accuracy (rate)
        // The second objective is the reduction (rate)
        numberOfObjectives_  = 2;
        
        samples_ = samples;
        /* In this case, the numberOfVariables_ are the variables of the database
        (like a sepal length and sepal Width from iris database)
        */        
        numberOfVariables_ = samples_.getNumberOfVariables();     
        // distance_ is a double matrix with distance values among training samples
        distance_ = distance;
        
        if (solutionType.compareTo("ArrayBinarySolutionType") == 0) {            
            // Samples_.getNumberOfTraSamples() will be the length of chromossome.
            // In this case, it is the total number of training samples            
            if (samples_.getTypeProcedure().equalsIgnoreCase("training"))
                solutionType_ = new ArrayBinarySolutionType(this,samples_.getNumberOfTraSamples());
            else if (samples_.getTypeProcedure().equalsIgnoreCase("test"))
                solutionType_ = new ArrayBinarySolutionType(this,samples_.getNumberOfTestSamples());
            else {
                System.err.print("SelectInstances class > constructor method error: samples_.getTypeProcedure: " + 
                samples_.getTypeProcedure() + " invalid.");
                System.exit(-1);
            }
        }
        else {
            System.out.println("Selecting Instances constructor error: solution type " + solutionType + " invalid");
            System.exit(-1);
        }     
    }   
     
    /**
     * This method gets one solution from Pareto front. The criterion used was
     * the best accuracy (the first objective of the SelecInstances problem)
     * @param population It contain the solutions from Pareto front
     * @return Reduction rate. It is not returned an object Solution, but the 
     * solution gotten from Pareto front is encoded in samples_ attribute
     */
    public double getSolutionFromPareto(SolutionSet population) {
        int numberOfSolutions, index, bits, count;        
        double max;
        Binary sol;        
        /* max will store the higher value of accuracy (objective zero)
         as the jMetal code minimizes the objectives, when is necessary to maximize, 
         must multiplicate the objective by -1. So, because of this, the code below does < max
        */
        max = population.get(0).getObjective(0); 
        numberOfSolutions = population.size();
        index = 0;
        for (int i = 0; i < numberOfSolutions; i++) {            
            if (population.get(i).getObjective(0) < max) {
                max = population.get(i).getObjective(0);
                index = i;
            }
        }
        
        sol = (Binary)population.get(index).getDecisionVariables()[0];
        bits = sol.getNumberOfBits();        
        count = 0;
        for (int i = 0; i < bits; i++) {
            if (sol.getIth(i) == true) {
                samples_.setSelectedSamples(i, true);                
                count++;
            }
        }    
        samples_.setNumberOfSelectedSamples(count);
        
        // returns the reduction rate
        return population.get(index).getObjective(1);
        
    } // end getSolutionFromPareto method
    
   /**
     * This method gets one solution from Pareto front. The criterion used was
     * the solution closest the mid point of Pareto front of the SelectInstances problem
     * @param population It contain the solutions from Pareto front     
     * @return Reduction rate. It is not returned an object Solution, but the 
     * solution gotten from Pareto front is encoded in samples_ attribute      
     * 
     * 2016/07/15. I changed the code to return one Solution object
     */     
    
    //public double getMidPointSolutionFromPareto(SolutionSet population) {
    public Solution getMidPointSolutionFromPareto(SolutionSet population) {
        
        int numberOfSolutions = population.size(), index = -1, bits, count;        
        double accuracy, reduction;        
        List<ObjectivesAccuracyComplexity> listSolution = new ArrayList<>();                

        if (numberOfSolutions == 0) {
            System.err.println("SelectInstances class > getMidPointSolutionFromPareto method error: no solution found");
            System.exit(-1);
        }
        else if (numberOfSolutions <= 2)
            index = 0;
        else {
            //System.out.print("\n");
            for (int i = 0; i < numberOfSolutions; i++) {             
                //System.out.println(population.get(i).getObjective(0) + " " + population.get(i).getObjective(1));            
                accuracy   = -1.0 * population.get(i).getObjective(0);
                reduction = -1.0 * population.get(i).getObjective(1);            
                ObjectivesAccuracyComplexity objectives = new ObjectivesAccuracyComplexity(accuracy, reduction, i);            
                listSolution.add(objectives);       
            }        
            //System.out.print("\n");
            //printList(listSolution);        
            index = getIndexOfMidpointSolution(listSolution);            
        }   
        
        Binary sol = (Binary)population.get(index).getDecisionVariables()[0];
        bits = sol.getNumberOfBits();        
        count = 0;
        for (int i = 0; i < bits; i++) {
            if (sol.getIth(i) == true) {
                samples_.setSelectedSamples(i, true);                
                count++;
            }
        }    
        samples_.setNumberOfSelectedSamples(count);
        
        // returns the reduction rate
        //return population.get(index).getObjective(1);   
        
        // return one Solution object
        // 2016/07/15. I changed the code to return one Solution object
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
    
} // end SelectInstances class
