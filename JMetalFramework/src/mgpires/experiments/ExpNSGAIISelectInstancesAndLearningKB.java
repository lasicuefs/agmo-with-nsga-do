package mgpires.experiments;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.util.JMException;
import mgpires.metaheuristics.nsgaII.NSGAII_SelectInstances_and_LearningKB;
import mgpires.algorithms.Printer;

/**
 * This class was created to test all folds of a dataset. This class use threads
 * that implement Runnable interface
 * @author Matheus Giovanni Pires
 * @email mgpires@ecomp.uefs.br
 * @data 2015/07/31
 */
public class ExpNSGAIISelectInstancesAndLearningKB {
    
    public static void main(String[] args) throws 
                                  JMException, 
                                  SecurityException, 
                                  IOException, 
                                  ClassNotFoundException,
                                  InterruptedException {  
        
        //String[] datasetName = new String[]{"balance","banana", "iris"};
        String[] datasetName = new String[]{"balance"};
        
        String datasetLocation, configFile, pathResult, pathResultAll;
        // stratificationDataset can be 5 or 10
        String stratificationDataset = "10";
        int numberOfFolds            = 3; 
        
        // parameters AGMO -> SelectInstances
        int populationSizeSelectInstances          = 100;
        int maxEvaluationsSelectInstances          = 1000;
        double probabilityCrossoverSelectInstances = 0.9;
        double probabilityMutationSelectInstances  = 0.2;
        
        // parameters AGMO -> LearningKB
        int populationSizeLearningKB                 = 100;
        int maxEvaluationsLearningKB                 = 10000;
        int numberMaxToMutationChangeRulesLearningKB = 5;
        int minNumberRules                           = 5;
        int maxNumberRules                           = 30;
        
        double probabilityCrossoverRulesLearningKB     = 0.6;
        double probabilityCrossoverFunctionsLearningKB = 0.5;

        double probabilityMutationRulesLearningKB           = 0.1;
        double probabilityMutationAddRulesLearningKB        = 0.55;
        double probabilityMutationChangeRulesLearningKB     = 0.45;
        double probabilityMutationChangeFunctionsLearningKB = 0.2;        
        
        // test using Threads implementing Runnable
        NSGAII_SelectInstances_and_LearningKB experiment;
        
        Thread[] p = new Thread[numberOfFolds];
        
        for (int idxDataSet = 0; idxDataSet < datasetName.length; idxDataSet++) {
            
            datasetLocation = "./dataset/" + datasetName[idxDataSet] + "/";
            configFile      = "config-" + datasetName[idxDataSet] + ".txt";
            pathResult      = "./result/" + datasetName[idxDataSet] + "/";
            pathResultAll   = "./result/";
            
            System.out.println(datasetName[idxDataSet]);
        
            for (int idxFold = 1; idxFold <= numberOfFolds; idxFold++) {            

                experiment = new NSGAII_SelectInstances_and_LearningKB(     
                    datasetLocation,
                    datasetName[idxDataSet],
                    configFile,
                    stratificationDataset,
                    idxFold,
                    populationSizeSelectInstances,
                    maxEvaluationsSelectInstances,
                    probabilityCrossoverSelectInstances,
                    probabilityMutationSelectInstances,
                    minNumberRules,
                    maxNumberRules,
                    populationSizeLearningKB,
                    maxEvaluationsLearningKB,
                    numberMaxToMutationChangeRulesLearningKB,
                    probabilityCrossoverRulesLearningKB,
                    probabilityCrossoverFunctionsLearningKB,
                    probabilityMutationRulesLearningKB,
                    probabilityMutationAddRulesLearningKB,
                    probabilityMutationChangeRulesLearningKB,
                    probabilityMutationChangeFunctionsLearningKB,
                    pathResult); 

                p[idxFold - 1] = new Thread(experiment);
                p[idxFold - 1].start();           
            }

            // waiting the threads die
            try {
                for (int idxFold = 1; idxFold <= numberOfFolds; idxFold++) {
                    p[idxFold - 1].join();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ExpNSGAIISelectInstancesAndLearningKB.class.getName()).log(Level.SEVERE, null, ex);
            }       
            
            Printer.printFinalResult(datasetName[idxDataSet], pathResult, pathResultAll);
        } //end for       
    } //end main   
    
} // end ExpNSGAIISelectInstancesAndLearningKB class
