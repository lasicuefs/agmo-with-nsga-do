package mgpires.algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import jmetal.core.Solution;

/**
 * This class implements the methods to print the results in text files
 *
 * @author Matheus Giovanni Pires
 * @email mgpires@ecomp.uefs.br
 * @date 2015/08/07
 */
public class Printer {

    public static void printObjectivesToFile(Solution solution, String path) {

        try (FileOutputStream fos = new FileOutputStream(path);
           OutputStreamWriter osw = new OutputStreamWriter(fos);
           BufferedWriter bw = new BufferedWriter(osw)) {

            bw.write(solution.toString());
            bw.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printObjectivesToFile method error: " + e);
            System.exit(-1);
        }
    }

    public static void printVariablesToFile(Solution solution, String path) {

        try (FileOutputStream fos = new FileOutputStream(path);
           OutputStreamWriter osw = new OutputStreamWriter(fos);
           BufferedWriter bw = new BufferedWriter(osw)) {
            int numberOfVariables = solution.getDecisionVariables().length;

            for (int j = 0; j < numberOfVariables; j++) {
                bw.write(solution.getDecisionVariables()[j].toString() + " ");
            }

            bw.close();

        } catch (IOException e) {
            System.err.println("Printer class >  printVariablesToFile method error: " + e);
            System.exit(-1);
        }
    }

    // Here, the parameters toward select instances only
    public static void printResultToFile(String datasetName, String path, int indexFold, double timeSelectInstances, double reductionRate,
       double accuracyTra, double accuracyTest) throws IOException {

        String filename = path + datasetName + "-resultFolds.txt";

        //System.out.println(filename);

        try (FileWriter fw = new FileWriter(filename, true)) //the true will append the new data
        {
            fw.write(datasetName + " " + Integer.toString(indexFold) + " " + Double.toString(timeSelectInstances) + " " + Double.toString(reductionRate) + " "
               + Double.toString(accuracyTra) + " " + Double.toString(accuracyTest));

            fw.write("\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printResultToFile method error: " + e);
            System.exit(-1);
        }
    }
    
    /**
     * 
        This method is used to save the results of the KNN algorithm
    */
    
    /**
     * This method is used to save the results of the KNN algorithm
     * @param datasetName Dataset name
     * @param path Path of the dataset
     * @param indexFold Index fold of the dataset
     * @param accuracy Accuracy value of the KNN algorithm
     * @param time Execution time value of the KNN algorithm
     * @throws IOException 
     */
    public static void printResultToFile(String datasetName, String path, int indexFold, double accuracy, double time) throws IOException {

        String filename = path + datasetName + "-resultFolds.txt";

        //System.out.println(filename);

        try (FileWriter fw = new FileWriter(filename, true)) //the true will append the new data
        {
            fw.write(datasetName + " " + Integer.toString(indexFold) + " " + Double.toString(accuracy) + " " + Double.toString(time));
            fw.write("\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printResultToFile method error: " + e);
            System.exit(-1);
        }
    }    

    public static void printResultToFile(String datasetName, String path, int indexFold, double timeSelectInstances, double reductionRate,
       double accuracyTra, int numberOfConditions, double accuracyTest, int numberOfRules) throws IOException {

        String filename = path + datasetName + "-resultFolds.txt";

        try (FileWriter fw = new FileWriter(filename, true)) //the true will append the new data
        {
            fw.write(datasetName + " " + Integer.toString(indexFold) + " " + Double.toString(timeSelectInstances) + " " + Double.toString(reductionRate) + " "
               + Double.toString(accuracyTra) + " " + Integer.toString(numberOfConditions) + " "
               + Integer.toString(numberOfRules) + " " + Double.toString(accuracyTest));

            fw.write("\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printResultToFile method error: " + e);
            System.exit(-1);
        }
    }

    public static void printResultToFile(String datasetName, String path, int indexFold, double timeSelectInstances, double reductionRate, double timeLearningKB,
       double accuracyTra, int numberOfConditions, double accuracyTest, int numberOfRules) throws IOException {

        String filename = path + datasetName + "-resultFolds.txt";

        try (FileWriter fw = new FileWriter(filename, true)) //the true will append the new data
        {
            fw.write(datasetName + " " + Integer.toString(indexFold) + " " + Double.toString(timeSelectInstances) + " " + Double.toString(reductionRate) + " "
               + Double.toString(timeLearningKB) + " " + Double.toString(accuracyTra) + " " + Integer.toString(numberOfConditions) + " "
               + Integer.toString(numberOfRules) + " " + Double.toString(accuracyTest));

            fw.write("\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printResultToFile method error: " + e);
            System.exit(-1);
        }
    }

    public static void printResultToFile(String datasetName, String path, int indexFold, double timeLearningKB,
       double accuracyTra, int numberOfConditions, double accuracyTest, int numberOfRules) throws IOException {

        String filename = path + datasetName + "-resultFolds.txt";

        try (FileWriter fw = new FileWriter(filename, true)) //the true will append the new data
        {
            fw.write(datasetName + " " + Integer.toString(indexFold) + " "
               + Double.toString(timeLearningKB) + " " + Double.toString(accuracyTra) + " " + Integer.toString(numberOfConditions) + " "
               + Integer.toString(numberOfRules) + " " + Double.toString(accuracyTest));

            fw.write("\n");
            fw.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printResultToFile method error: " + e);
            System.exit(-1);
        }
    }

    public static void printFinalResult(String datasetName, String path, String pathAll) throws FileNotFoundException, IOException {

        String fileName = path + datasetName + "-resultFolds.txt";
        //System.out.println(fileName);     

        double timeSelectInstances = 0, reductionRate = 0, timeLearningKB = 0,
           accuracyTra = 0, complexity = 0, accuracyTest = 0, timeTotal = 0,
           numberOfRules = 0, timeTotalActual;

        int countLine = 0;

        List<Double[]> listValues;
        listValues = new ArrayList<>();

        try (FileReader reader = new FileReader(fileName);
           BufferedReader leitor = new BufferedReader(reader);) {
            StringTokenizer st;
            String line, data;
            int count, idx;

            countLine = 0;
            while ((line = leitor.readLine()) != null) {
                countLine++;
                //System.out.println(countLine);
                st = new StringTokenizer(line, ",| ");
                Double[] values = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                timeTotalActual = 0.0;
                count = 0;
                idx = 0;
                while (st.hasMoreTokens()) {
                    data = st.nextToken();
                    //System.out.print(data + " ");
                    count++;

                    if (count == 3) {
                        timeSelectInstances += Double.parseDouble(data);
                        timeTotal += Double.parseDouble(data);
                        timeTotalActual += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 4) {
                        reductionRate += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 5) {
                        timeLearningKB += Double.parseDouble(data);
                        timeTotal += Double.parseDouble(data);
                        timeTotalActual += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 6) {
                        accuracyTra += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 7) {
                        complexity += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 8) {
                        numberOfRules += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 9) {
                        accuracyTest += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
                }
                values[idx] = timeTotalActual;
                listValues.add(values);
                //System.out.println(" ");
            }
            leitor.close();
            reader.close();

            // calculating the median
            timeSelectInstances = timeSelectInstances / countLine;
            reductionRate = reductionRate / countLine;
            timeLearningKB = timeLearningKB / countLine;
            accuracyTra = accuracyTra / countLine;
            complexity = complexity / countLine;
            accuracyTest = accuracyTest / countLine;
            timeTotal = timeTotal / countLine;
            numberOfRules = numberOfRules / countLine;

        } catch (Exception e) {
            System.err.println("Printer class >  printFinalResult method error: " + e);
            System.exit(-1);
        }

        // standard deviations
        double stdDevTimeSelectInstances = 0, stdDevReductionRate = 0, stdDevTimeLearningKB = 0,
           stdDevAccuracyTra = 0, stdDevComplexity = 0, stdDevAccuracyTest = 0,
           stdDevTimeTotal = 0, stdDevNumberOfRules = 0;

        Double[] aux;
        Double dif;
        for (Double[] listValue : listValues) {
            aux = listValue;

            for (int j = 0; j < aux.length; j++) {

                if (j == 0) {
                    //System.out.println(aux[j] + " " + timeSelectInstances);
                    dif = aux[j] - timeSelectInstances;
                    stdDevTimeSelectInstances += (dif * dif);
                } else if (j == 1) {
                    //System.out.println(aux[j] + " " + reductionRate);
                    dif = aux[j] - reductionRate;
                    stdDevReductionRate += (dif * dif);
                } else if (j == 2) {
                    //System.out.println(aux[j] + " " + timeLearningKB);
                    dif = aux[j] - timeLearningKB;
                    stdDevTimeLearningKB += (dif * dif);
                } else if (j == 3) {
                    //System.out.println(aux[j] + " " + accuracyTra);
                    dif = aux[j] - accuracyTra;
                    stdDevAccuracyTra += (dif * dif);
                } else if (j == 4) {
                    //System.out.println(aux[j] + " " + complexity);
                    dif = aux[j] - complexity;
                    stdDevComplexity += (dif * dif);
                } else if (j == 5) {
                    //System.out.println(aux[j] + " " + accuracyTest);
                    dif = aux[j] - accuracyTest;
                    stdDevAccuracyTest += (dif * dif);
                } else if (j == 6) {
                    dif = aux[j] - numberOfRules;
                    stdDevNumberOfRules += (dif * dif);
                } else if (j == 7) {
                    //System.out.println(aux[j] + " " + timeTotal);
                    dif = aux[j] - timeTotal;
                    stdDevTimeTotal += (dif * dif);
                }
            }
        }

        stdDevTimeSelectInstances = Math.sqrt(stdDevTimeSelectInstances / (countLine - 1));
        stdDevReductionRate = Math.sqrt(stdDevReductionRate / (countLine - 1));
        stdDevTimeLearningKB = Math.sqrt(stdDevTimeLearningKB / (countLine - 1));
        stdDevAccuracyTra = Math.sqrt(stdDevAccuracyTra / (countLine - 1));
        stdDevComplexity = Math.sqrt(stdDevComplexity / (countLine - 1));
        stdDevAccuracyTest = Math.sqrt(stdDevAccuracyTest / (countLine - 1));
        stdDevTimeTotal = Math.sqrt(stdDevTimeTotal / (countLine - 1));
        stdDevNumberOfRules = Math.sqrt(stdDevNumberOfRules / (countLine - 1));

        /*
         System.out.print("\n");
         System.out.println(stdDevTimeSelectInstances);
         System.out.println(stdDevReductionRate);
         System.out.println(stdDevTimeLearningKB);
         System.out.println(stdDevAccuracyTra);
         System.out.println(stdDevComplexity);
         System.out.println(stdDevAccuracyTest);
         System.out.println(stdDevTimeTotal);
         */
        fileName = pathAll + "resultAllDataSets.txt";
        String fileName2 = path + datasetName + "-resultFinal.txt";

        try (FileWriter fw = new FileWriter(fileName, true);
           FileWriter fw2 = new FileWriter(fileName2, true);) //the true will append the new data
        {
            fw.write(datasetName + " "
               + Double.toString(timeSelectInstances) + " " + Double.toString(stdDevTimeSelectInstances) + " "
               + Double.toString(timeLearningKB) + " " + Double.toString(stdDevTimeLearningKB) + " "
               + Double.toString(timeTotal) + " " + Double.toString(stdDevTimeTotal) + " "
               + Double.toString(reductionRate) + " " + Double.toString(stdDevReductionRate) + " "
               + Double.toString(accuracyTra) + " " + Double.toString(stdDevAccuracyTra) + " "
               + Double.toString(complexity) + " " + Double.toString(stdDevComplexity) + " "
               + Double.toString(numberOfRules) + " " + Double.toString(stdDevNumberOfRules) + " "
               + Double.toString(accuracyTest) + " " + Double.toString(stdDevAccuracyTest) + "\n");

            fw2.write(datasetName + " "
               + Double.toString(timeSelectInstances) + " " + Double.toString(stdDevTimeSelectInstances) + " "
               + Double.toString(timeLearningKB) + " " + Double.toString(stdDevTimeLearningKB) + " "
               + Double.toString(timeTotal) + " " + Double.toString(stdDevTimeTotal) + " "
               + Double.toString(reductionRate) + " " + Double.toString(stdDevReductionRate) + " "
               + Double.toString(accuracyTra) + " " + Double.toString(stdDevAccuracyTra) + " "
               + Double.toString(complexity) + " " + Double.toString(stdDevComplexity) + " "
               + Double.toString(numberOfRules) + " " + Double.toString(stdDevNumberOfRules) + " "
               + Double.toString(accuracyTest) + " " + Double.toString(stdDevAccuracyTest) + "\n");

            fw.close();
            fw2.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printFinalResult method error: " + e);
            System.exit(-1);
        }
    } //end printFinalResult method

    public static void printFinalResultLearningKB(String datasetName, String path, String pathAll) throws FileNotFoundException, IOException {

        String fileName = path + datasetName + "-resultFolds.txt";
        //System.out.println(fileName);     

        double timeLearningKB = 0, accuracyTra = 0, complexity = 0, accuracyTest = 0,
           numberOfRules = 0;

        int countLine = 0;

        List<Double[]> listValues;
        listValues = new ArrayList<>();

        try (FileReader reader = new FileReader(fileName);
           BufferedReader leitor = new BufferedReader(reader);) {
            StringTokenizer st;
            String line, data;
            int count, idx;

            countLine = 0;
            while ((line = leitor.readLine()) != null) {
                countLine++;
                //System.out.println(countLine);
                st = new StringTokenizer(line, ",| ");
                Double[] values = {0.0, 0.0, 0.0, 0.0, 0.0};
                count = 0;
                idx = 0;
                while (st.hasMoreTokens()) {
                    data = st.nextToken();
                    //System.out.print(data + " ");
                    count++;

                    if (count == 3) {
                        timeLearningKB += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 4) {
                        accuracyTra += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 5) {
                        complexity += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 6) {
                        numberOfRules += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 7) {
                        accuracyTest += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
                }
                listValues.add(values);
                //System.out.println(" ");
            }
            leitor.close();
            reader.close();

            // calculating the median
            timeLearningKB = timeLearningKB / countLine;
            accuracyTra = accuracyTra / countLine;
            complexity = complexity / countLine;
            accuracyTest = accuracyTest / countLine;
            numberOfRules = numberOfRules / countLine;

        } catch (Exception e) {
            System.err.println("Printer class >  printFinalResultLearningKB method error: " + e);
            System.exit(-1);
        }

        // standard deviations
        double stdDevTimeLearningKB = 0, stdDevAccuracyTra = 0, stdDevComplexity = 0,
           stdDevAccuracyTest = 0, stdDevNumberOfRules = 0;
        Double[] aux;
        Double dif;
        for (Double[] listValue : listValues) {
            aux = listValue;

            for (int j = 0; j < aux.length; j++) {

                if (j == 0) {
                    //System.out.println(aux[j] + " " + timeLearningKB);
                    dif = aux[j] - timeLearningKB;
                    stdDevTimeLearningKB += (dif * dif);
                } else if (j == 1) {
                    //System.out.println(aux[j] + " " + accuracyTra);
                    dif = aux[j] - accuracyTra;
                    stdDevAccuracyTra += (dif * dif);
                } else if (j == 2) {
                    //System.out.println(aux[j] + " " + complexity);
                    dif = aux[j] - complexity;
                    stdDevComplexity += (dif * dif);
                } else if (j == 3) {
                    dif = aux[j] - numberOfRules;
                    stdDevNumberOfRules += (dif * dif);
                } else if (j == 4) {
                    //System.out.println(aux[j] + " " + accuracyTest);
                    dif = aux[j] - accuracyTest;
                    stdDevAccuracyTest += (dif * dif);
                }
            }
        }

        stdDevTimeLearningKB = Math.sqrt(stdDevTimeLearningKB / (countLine - 1));
        stdDevAccuracyTra = Math.sqrt(stdDevAccuracyTra / (countLine - 1));
        stdDevComplexity = Math.sqrt(stdDevComplexity / (countLine - 1));
        stdDevAccuracyTest = Math.sqrt(stdDevAccuracyTest / (countLine - 1));
        stdDevNumberOfRules = Math.sqrt(stdDevNumberOfRules / (countLine - 1));

        fileName = pathAll + "resultAllDataSets.txt";
        String fileName2 = path + datasetName + "-resultFinal.txt";

        try (FileWriter fw = new FileWriter(fileName, true);
           FileWriter fw2 = new FileWriter(fileName2, true);) //the true will append the new data
        {
            fw.write(datasetName + " "
               + Double.toString(timeLearningKB) + " " + Double.toString(stdDevTimeLearningKB) + " "
               + Double.toString(accuracyTra) + " " + Double.toString(stdDevAccuracyTra) + " "
               + Double.toString(complexity) + " " + Double.toString(stdDevComplexity) + " "
               + Double.toString(numberOfRules) + " " + Double.toString(stdDevNumberOfRules) + " "
               + Double.toString(accuracyTest) + " " + Double.toString(stdDevAccuracyTest) + "\n");

            fw2.write(datasetName + " "
               + Double.toString(timeLearningKB) + " " + Double.toString(stdDevTimeLearningKB) + " "
               + Double.toString(accuracyTra) + " " + Double.toString(stdDevAccuracyTra) + " "
               + Double.toString(complexity) + " " + Double.toString(stdDevComplexity) + " "
               + Double.toString(numberOfRules) + " " + Double.toString(stdDevNumberOfRules) + " "
               + Double.toString(accuracyTest) + " " + Double.toString(stdDevAccuracyTest) + "\n");

            fw.close();
            fw2.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printFinalResultLearningKB method error: " + e);
            System.exit(-1);
        }
    } //end printFinalResult method

    
    /**
     * This method calculates and saves the results in text files of the selection instances process.
     * @param datasetName name of dataset
     * @param path path where will be saved the files
     * @param pathAll path where will be save the general results, such as, median and standard deviation of measures
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void printFinalResultSelectInstances(String datasetName, String path, String pathAll) throws FileNotFoundException, IOException {

        String fileName = path + datasetName + "-resultFolds.txt";
        //System.out.println(fileName);     

        double timeSelectInstances = 0, reductionRate = 0, accuracyTra = 0, accuracyTest = 0;

        int countLine = 0;

        List<Double[]> listValues;
        listValues = new ArrayList<>();

        try (FileReader reader = new FileReader(fileName);
           BufferedReader leitor = new BufferedReader(reader);) {
            StringTokenizer st;
            String line, data;
            int count, idx;

            countLine = 0;
            while ((line = leitor.readLine()) != null) {
                countLine++;
                //System.out.println(countLine);
                st = new StringTokenizer(line, ",| ");
                
                /*  values[0] -> timeSelectInstances
                    values[1] -> reductionRate
                    values[2] -> accuracyTra
                    values[3] -> accuracyTest
                */
                Double[] values = {0.0, 0.0, 0.0, 0.0};                
                count = 0;
                idx = 0;
                while (st.hasMoreTokens()) {
                    data = st.nextToken();
                    //System.out.print(data + " ");
                    count++;                    
                    /* the data are read from, for example, iris-resultFolds.txt
                    So, I need pay attention in the sequence of data that were saved in this file to
                    calculate the measures (median and standar deviation)      
                    
                    Printer.printResultToFile(datasetName, pathResult, indexFold, timeSelectInstances,
                        (-1.0 * reductionRate), (-1.0 * accuracyTra), test[0]);
                    */
                    if (count == 3) {
                        timeSelectInstances += Double.parseDouble(data);                   
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 4) {
                        reductionRate += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 5) {
                        accuracyTra += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    } else if (count == 6) {
                        accuracyTest += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
                }                
                listValues.add(values);
                //System.out.println(" ");
            }
            leitor.close();
            reader.close();

            // calculating the median
            timeSelectInstances = timeSelectInstances / countLine;
            reductionRate       = reductionRate       / countLine;            
            accuracyTra         = accuracyTra         / countLine;            
            accuracyTest        = accuracyTest        / countLine;            

        } catch (Exception e) {
            System.err.println("Printer class >  printFinalResultSelectInstances method error: " + e);
            System.exit(-1);
        }

        // standard deviations
        double stdDevTimeSelectInstances = 0, stdDevReductionRate = 0, stdDevAccuracyTra = 0, stdDevAccuracyTest = 0;           

        Double[] aux;
        Double dif;
        for (Double[] listValue : listValues) {
            aux = listValue;

            for (int j = 0; j < aux.length; j++) {

                if (j == 0) {
                    //System.out.println(aux[j] + " " + timeSelectInstances);
                    dif = aux[j] - timeSelectInstances;
                    stdDevTimeSelectInstances += (dif * dif);
                } else if (j == 1) {
                    //System.out.println(aux[j] + " " + reductionRate);
                    dif = aux[j] - reductionRate;
                    stdDevReductionRate += (dif * dif);
                } else if (j == 2) {
                    //System.out.println(aux[j] + " " + accuracyTra);
                    dif = aux[j] - accuracyTra;
                    stdDevAccuracyTra += (dif * dif);
                } else if (j == 3) {
                    //System.out.println(aux[j] + " " + accuracyTest);
                    dif = aux[j] - accuracyTest;
                    stdDevAccuracyTest += (dif * dif);
                }
            }
        }
        // calculatin the standard deviations    
        stdDevTimeSelectInstances = Math.sqrt(stdDevTimeSelectInstances / (countLine - 1));
        stdDevReductionRate       = Math.sqrt(stdDevReductionRate       / (countLine - 1));        
        stdDevAccuracyTra         = Math.sqrt(stdDevAccuracyTra         / (countLine - 1));        
        stdDevAccuracyTest        = Math.sqrt(stdDevAccuracyTest        / (countLine - 1));
        /*
         System.out.print("\n");
         System.out.println(stdDevTimeSelectInstances);
         System.out.println(stdDevReductionRate);         
         System.out.println(stdDevAccuracyTra);         
         System.out.println(stdDevAccuracyTest);         
         */
        fileName = pathAll + "resultAllDataSets.txt";
        String fileName2 = path + datasetName + "-resultFinal.txt";

        try (FileWriter fw = new FileWriter(fileName, true);
           FileWriter fw2 = new FileWriter(fileName2, true);) //the true will append the new data
        {
            fw.write(datasetName + " "
               + Double.toString(timeSelectInstances) + " " + Double.toString(stdDevTimeSelectInstances) + " "
               + Double.toString(reductionRate) + " " + Double.toString(stdDevReductionRate) + " "
               + Double.toString(accuracyTra) + " " + Double.toString(stdDevAccuracyTra) + " "               
               + Double.toString(accuracyTest) + " " + Double.toString(stdDevAccuracyTest) + "\n");

            fw2.write(datasetName + " "
               + Double.toString(timeSelectInstances) + " " + Double.toString(stdDevTimeSelectInstances) + " "               
               + Double.toString(reductionRate) + " " + Double.toString(stdDevReductionRate) + " "
               + Double.toString(accuracyTra) + " " + Double.toString(stdDevAccuracyTra) + " "               
               + Double.toString(accuracyTest) + " " + Double.toString(stdDevAccuracyTest) + "\n");

            fw.close();
            fw2.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printFinalResultSelectInstances method error: " + e);
            System.exit(-1);
        }
    } //end printFinalResultSelectInstances method 
    
    /**
     * This method calculates and saves the results in text files of the KNN algorithm
     * @param datasetName name of dataset
     * @param path path where will be saved the files
     * @param pathAll path where will be save the general results, such as, median and standard deviation of measures
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void printFinalResultKNN(String datasetName, String path, String pathAll) throws FileNotFoundException, IOException {

        String fileName = path + datasetName + "-resultFolds.txt";
        //System.out.println(fileName);     

        double accuracy = 0;
        double time = 0;
        
        int countLine = 0;
        List<Double[]> listValues;
        listValues = new ArrayList<>();

        try (FileReader reader = new FileReader(fileName);
            BufferedReader leitor = new BufferedReader(reader);) {
            StringTokenizer st;
            String line, data;
            int count, idx;

            countLine = 0;
            while ((line = leitor.readLine()) != null) {
                countLine++;
                //System.out.println(countLine);
                st = new StringTokenizer(line, ",| ");
                
                Double[] values = {0.0, 0.0};                 
                count = 0;
                idx = 0;
                
                while (st.hasMoreTokens()) {
                    data = st.nextToken();
                    //System.out.print(data + " ");
                    count++;                    
                    
                    if (count == 3) {                        
                        accuracy += Double.parseDouble(data); 
                        values[idx] = Double.parseDouble(data);
                        idx++;
                        //System.out.println(value);
                    }
                    else if (count == 4) {
                        time += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
               
                }                
                listValues.add(values);
                //System.out.println(" ");
            }
            leitor.close();
            reader.close();

            // calculating the median
            accuracy = accuracy / countLine;    
            time     = time / countLine;

        } catch (Exception e) {
            System.err.println("Printer class >  printFinalResultKNN method error: " + e);
            System.exit(-1);
        }

        // standard deviations
        double stdDevAccuracy = 0, stdDevTime = 0;
        Double[] aux;
        double dif;
        
        for (Double[] listValue : listValues) {
            aux = listValue;
            
            for (int j = 0; j < aux.length; j++) {
                
                if (j == 0) {                    
                    dif = aux[j] - accuracy;
                    stdDevAccuracy += (dif * dif);
                } else if (j == 1) {                    
                    dif = aux[j] - time;
                    stdDevTime += (dif * dif);
                }
            }  
        }
        
        // calculating the standard deviations    
        stdDevAccuracy = Math.sqrt(stdDevAccuracy / (countLine - 1));   
        stdDevTime     = Math.sqrt(stdDevTime / (countLine - 1));
        
        fileName = pathAll + "resultAllDataSets.txt";
        String fileName2 = path + datasetName + "-resultFinal.txt";

        try (FileWriter fw = new FileWriter(fileName, true);
           FileWriter fw2 = new FileWriter(fileName2, true);) //the true will append the new data
        {
            fw.write(datasetName + " "
               + Double.toString(accuracy) + " " + Double.toString(stdDevAccuracy) + " " 
               + Double.toString(time)     + " " + Double.toString(stdDevTime) + "\n");

            fw2.write(datasetName + " "
               + Double.toString(accuracy) + " " + Double.toString(stdDevAccuracy) + " "
               + Double.toString(time)     + " " + Double.toString(stdDevTime) + "\n");

            fw.close();
            fw2.close();
        } catch (IOException e) {
            System.err.println("Printer class >  printFinalResultKNN method error: " + e);
            System.exit(-1);
        }
    } //end printFinalResultKNN method     

} //end Printer class
