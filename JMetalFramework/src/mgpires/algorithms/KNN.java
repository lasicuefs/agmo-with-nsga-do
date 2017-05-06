package mgpires.algorithms;

import java.util.Map;
import jmetal.core.Solution;
import jmetal.encodings.variable.Binary;
import mgpires.core.Samples;

/**
 * This class implements the K-NN classifier
 *
 * @author Matheus Giovanni Pires
 * @email mgpires@ecomp.uefs.br
 * @date 2015/05/29
 * @modified 2016/10/28
 */
public class KNN {

    /**
     * This method calculates the accuracy and the reduction rate. The accuracy
     * is calculate by 1-NN classifier, i.e., the classification will be done by
     * closest neighbor
     *
     * @param solution Is the chromosome that codifies the samples that were
     * selected
     * @param samples Matrix with the samples
     * @param distance HashMap or matrix with the distance values
     * @param numberOfSamples Number of samples
     * @param nVar Number of variables of the dataset
     * @return one double vector with two values. 1) accuracy (rate) 2)
     * reduction of samples (rate)
     */
    public static double[] calcAccuracyAndReduction(Solution solution, String[][] samples, Map<String, Object> distance, int numberOfSamples, int nVar) {

        Binary sol = (Binary) solution.getDecisionVariables()[0];
        double dist, minDist;
        double[] result = new double[2];
        int idxMinDist, bits = sol.getNumberOfBits(), accuracy = 0, count = 0;
        String key;

        // this for run the chromosome, checking the selected samples
        for (int i = 0; i < bits; i++) {
            minDist = Double.MAX_VALUE;
            idxMinDist = -1;
            if (sol.getIth(i) == true) {
                count++; // count will be the number of selected samples
                // this for checks all samples
                for (int j = 0; j < numberOfSamples; j++) {
                    // samples equals are not compared    
                    if (i != j) {
                        /* the first symbol of key always is lower than the second symbol, in other words,
                         the first simbol starts in zero and goes to N samples
                         */
                        if (j < i) {
                            key = Integer.toBinaryString(j) + Integer.toBinaryString(i);
                        } else {
                            key = Integer.toBinaryString(i) + Integer.toBinaryString(j);
                        }

                        if (distance.containsKey(key) == false) {
                            System.err.println("KNN class > calcAccuracyAndReduction method error: key not found = " + key);
                            System.exit(-1);
                        } else {
                            dist = (double) distance.get(key);

                            if (dist < minDist) {
                                minDist = dist;
                                idxMinDist = j;
                            }

                        }
                    }
                }
                /* if the class of samples are equals, the classification is correct
                 idxMinDist is the index of the closest neighbor
                 */
                //System.out.println(i + " " + idxMinDist + " " + (nVar-1));
                if (samples[i][nVar - 1].equals(samples[idxMinDist][nVar - 1])) {
                    accuracy++;
                }
            } // end if        
        } // end for

        //System.out.println("Accuracy = " + accuracy + " Selected = " + count + " nSamples = " + numberOfSamples);
        result[0] = accuracy / (double) numberOfSamples;
        // the samples reduction rate means that lower number of selected samples better is the rate 
        result[1] = (numberOfSamples - count) / (double) numberOfSamples;

        return result;

    } // end of calcAccuracyAndReduction method

    // this version uses a double matrix distance
    public static double[] calcAccuracyAndReduction(Solution solution, String[][] samples, double[][] distance, int numberOfSamples, int nVar) {

        Binary sol = (Binary) solution.getDecisionVariables()[0];
        double dist, minDist;
        double[] result = new double[2];
        int idxMinDist, bits = sol.getNumberOfBits(), accuracy = 0, count = 0;

        // this for run the chromosome, checking the selected samples
        for (int i = 0; i < bits; i++) {
            minDist = Double.MAX_VALUE;
            idxMinDist = -1;
            if (sol.getIth(i) == true) {
                count++; // count will be the number of selected samples
                // this for checks all samples
                for (int j = 0; j < numberOfSamples; j++) {
                    // samples equals are not compared    
                    if (i != j) {
                        /* the first symbol of key always is lower than the second symbol, in other words,
                         the first simbol starts in zero and goes to N samples
                         */
                        if (j < i) //key = Integer.toBinaryString(j) + Integer.toBinaryString(i);
                        {
                            dist = distance[j][i];
                        } else //key = Integer.toBinaryString(i) + Integer.toBinaryString(j);
                        {
                            dist = distance[i][j];
                        }

                        if (dist < minDist) {
                            minDist = dist;
                            idxMinDist = j;
                        }
                    }
                }
                /* if the class of samples are equals, the classification is correct
                 idxMinDist is the index of the closest neighbor
                 */
                //System.out.println(i + " " + idxMinDist + " " + (nVar-1));
                if (samples[i][nVar - 1].equals(samples[idxMinDist][nVar - 1])) {
                    accuracy++;
                }
            } // end if        
        } // end for

        //System.out.println("Accuracy = " + accuracy + " Selected = " + count + " nSamples = " + numberOfSamples);
        result[0] = accuracy / (double) numberOfSamples;
        // the samples reduction rate means that lower number of selected samples better is the rate 
        result[1] = (numberOfSamples - count) / (double) numberOfSamples;

        return result;

    } // end of calcAccuracyAndReduction method

    // this version uses a double matrix distance
    public static double[] calcAccuracyAndReduction(Solution solution, double[][] distance, Samples samples, String flag) {

        Binary sol = (Binary) solution.getDecisionVariables()[0];
        double dist, minDist;
        double[] result = new double[2];
        int idxMinDist, bits = sol.getNumberOfBits(), accuracy = 0, count = 0,
           var = samples.getNumberOfVariables(), numberOfSamples;

        if (flag.equals("train")) {
            // get the training samples
            String[][] sampleMatrix = samples.getTraSamples();
            numberOfSamples = samples.getNumberOfTraSamples();

            // this for run the chromosome, checking the selected samples
            for (int i = 0; i < bits; i++) {
                minDist = Double.MAX_VALUE;
                idxMinDist = -1;
                if (sol.getIth(i) == true) {
                    count++; // count will be the number of selected samples
                    // this for checks all samples
                    for (int j = 0; j < numberOfSamples; j++) {
                        // samples equals are not compared    
                        if (i != j) {
                            /* the first symbol of key always is lower than the second symbol, in other words,
                             the first simbol starts in zero and goes to N samples
                             */
                            if (j < i) {
                                dist = distance[j][i];
                            } else {
                                dist = distance[i][j];
                            }

                            if (dist < minDist) {
                                minDist = dist;
                                idxMinDist = j;
                            }
                        }
                    }
                    /* if the class of samples are equals, the classification is correct
                     idxMinDist is the index of the closest neighbor
                     */
                    //System.out.println(i + " " + idxMinDist + " " + (nVar-1));
                    if (sampleMatrix[i][var - 1].equals(sampleMatrix[idxMinDist][var - 1])) {
                        accuracy++;
                    }
                } // end if        
            } // end for    
        } // end if
        else {
            String[][] sampleTra = samples.getTraSamples();
            String[][] sampleTest = samples.getTestSamples();
            numberOfSamples = samples.getNumberOfTestSamples();
            int column;
            // this for checks all test samples
            for (int i = 0; i < samples.getNumberOfTestSamples(); i++) {
                minDist = Double.MAX_VALUE;
                idxMinDist = -1;
                column = 0;

                // this for run the chromosome, checking the selected samples
                for (int j = 0; j < bits; j++) {

                    if (sol.getIth(j) == true) {
                        /* in case test, the distance matrix is complete, because it has the distances values
                         among the selected samples (by cromosomo) with the test samples. So, I need pay attention
                         with the indexes of matrix.                        
                         The distance matrix is composed by [test_smaples] [selected_tra_samples]                        
                         */
                        dist = distance[i][column];
                        column++;

                        if (dist < minDist) {
                            minDist = dist;
                            idxMinDist = j;
                        }
                    } // end if        
                } // end for 

                /* if the class of samples are equals, the classification is correct
                 idxMinDist is the index of the closest neighbor
                 */
                //System.out.println(i + " " + idxMinDist + " " + (nVar-1));
                if (sampleTest[i][var - 1].equals(sampleTra[idxMinDist][var - 1])) {
                    accuracy++;
                }

            } // end for
        } // end else

        for (int j = 0; j < bits; j++) {
            if (sol.getIth(j) == true) {
                count++;
            }
        }

        // calculates the accuracy rate
        result[0] = accuracy / (double) numberOfSamples;
        // calculates the reduction rate
        //System.out.println("olha aqui >>>> " + samples.getNumberOfTraSamples() + " " + count);
        result[1] = (samples.getNumberOfTraSamples() - count) / (double) samples.getNumberOfTraSamples();

        return result;

    } // end of calcAccuracyAndReduction method

    /**
     * This version of KNN calculates only the accuracy by 1-NN classifier
     * @param distance Matrix with distance values among the test and training samples
     * @param samples Matrix with test and training samples
     * @return accuracy, i.e., total of correct classification
     * @data 2016-10-28
     */
    public static double calcAccuracy(double[][] distance, Samples samples) {

        double dist, minDist, result;        
        int idxMinDist, accuracy = 0, var = samples.getNumberOfVariables(), numberOfTestSamples, numberOfTraSamples;

        String[][] sampleTra  = samples.getTraSamples();
        String[][] sampleTest = samples.getTestSamples();
        numberOfTraSamples    = samples.getNumberOfTraSamples();
        numberOfTestSamples   = samples.getNumberOfTestSamples();
        
        // this for checks all test samples
        for (int i = 0; i < numberOfTestSamples; i++) {
            minDist = Double.MAX_VALUE;
            idxMinDist = -1;

            // this for run the training data            
            for (int j = 0; j < numberOfTraSamples; j++) {

                // distance between i test sample and j training sample
                dist = distance[i][j];

                if (dist < minDist) {
                    minDist = dist;
                    idxMinDist = j;
                }
            }
            /* if the class of samples are equals, the classification is correct
               idxMinDist is the index of the closest neighbor */
            //System.out.println(i + " " + idxMinDist + " " + (nVar-1));
            if (sampleTest[i][var - 1].equals(sampleTra[idxMinDist][var - 1])) {
                accuracy++;
            }
        }        
        // calculates the accuracy rate
        result = accuracy / (double) numberOfTestSamples;

        return result;

    } // end of calcAccuracy method    
    
} // end KNN class