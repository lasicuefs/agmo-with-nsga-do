
package mgpires.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ReadResultsFolds {

    public static void printFinalResult(String datasetName, String path) throws FileNotFoundException, IOException {
        
        String fileName = path + datasetName + "-resultFolds.txt";  
        //System.out.println(fileName);     
        
        double timeSelectInstances = 0, reductionRate = 0, timeLearningKB = 0, 
           accuracyTra = 0, complexity = 0, accuracyTest = 0, timeTotal = 0;
        
        int countLine = 0;
        
        List<Double[]> listValues;
        listValues = new ArrayList<>();
        
        
        try (FileReader reader     = new FileReader(fileName);            
             BufferedReader leitor = new BufferedReader(reader);)
        {             
            StringTokenizer st;
            String line, data;
            int count, idx;          

            countLine = 0;
            while ((line = leitor.readLine()) != null) {
                countLine++;
                //System.out.println(countLine);
                st = new StringTokenizer(line, ",| ");                
                Double[] values = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
                timeTotal = 0;
                count = 0;
                idx = 0;
                while (st.hasMoreTokens()) {
                    data = st.nextToken();
                    //System.out.print(data + " ");
                    count++;                       
                    
                    if (count == 3) {
                        timeSelectInstances += Double.parseDouble(data);
                        timeTotal += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
                    else if (count == 4) {
                        reductionRate += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
                    else if (count == 5) {
                        timeLearningKB += Double.parseDouble(data);
                        timeTotal += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
                    else if (count == 6) {
                        accuracyTra += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
                    else if (count == 7) {
                        complexity += Double.parseDouble(data);
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
                    else if (count == 8) {
                        accuracyTest += Double.parseDouble(data);    
                        values[idx] = Double.parseDouble(data);
                        idx++;
                    }
                }
                values[idx] = timeTotal;
                listValues.add(values);                
                //System.out.println(" ");
            }            
            leitor.close();  
            reader.close(); 
            
            // calculating the median
            timeSelectInstances = timeSelectInstances / countLine;
            reductionRate       = reductionRate / countLine;
            timeLearningKB      = timeLearningKB / countLine;
            accuracyTra         = accuracyTra / countLine;
            complexity          = complexity / countLine;
            accuracyTest        = accuracyTest / countLine; 
            timeTotal           = timeTotal / countLine;
    
        }
        catch (Exception e) {             
           System.err.println("ExpNSGAIISelectInstancesAndLearningKB class >  printFinalResult method error: " + e);
           System.exit(-1);   
        }
        
        // standard deviations
        double stdDevTimeSelectInstances = 0, stdDevReductionRate = 0, stdDevTimeLearningKB = 0,
           stdDevAccuracyTra = 0,  stdDevComplexity = 0, stdDevAccuracyTest = 0,
           stdDevTimeTotal = 0;
        
        Double[] aux;
        Double dif;
        for (Double[] listValue : listValues) {
            aux = listValue;
            
            for (int j = 0; j < aux.length; j++) {                
                
                if (j == 0) {
                    //System.out.println(aux[j] + " " + timeSelectInstances);
                    dif = aux[j] - timeSelectInstances;
                    stdDevTimeSelectInstances += stdDevTimeSelectInstances + (dif * dif);                                      
                }
                else if (j == 1) {
                    //System.out.println(aux[j] + " " + reductionRate);
                    dif = aux[j] - reductionRate;
                    stdDevReductionRate += stdDevReductionRate + (dif * dif);
                }
                else if (j == 2) {
                    //System.out.println(aux[j] + " " + timeLearningKB);
                    dif = aux[j] - timeLearningKB;
                    stdDevTimeLearningKB += stdDevTimeLearningKB + (dif * dif);
                }
                else if (j == 3) {
                    //System.out.println(aux[j] + " " + accuracyTra);
                    dif = aux[j] - accuracyTra;
                    stdDevAccuracyTra += stdDevAccuracyTra + (dif * dif);
                }
                else if (j == 4) {
                    //System.out.println(aux[j] + " " + complexity);
                    dif = aux[j] - complexity;
                    stdDevComplexity += stdDevComplexity + (dif * dif);
                }
                else if (j == 5) {
                    //System.out.println(aux[j] + " " + accuracyTest);
                    dif = aux[j] - accuracyTest;
                    stdDevAccuracyTest += stdDevAccuracyTest + (dif * dif);
                }
                else if (j == 6) {
                    //System.out.println(aux[j] + " " + timeTotal);
                    dif = aux[j] - timeTotal;
                    stdDevTimeTotal += stdDevTimeTotal + (dif * dif);
                }
            }           
        }        
      
        stdDevTimeSelectInstances = Math.sqrt(stdDevTimeSelectInstances / (countLine - 1));
        stdDevReductionRate       = Math.sqrt(stdDevReductionRate / (countLine - 1));
        stdDevTimeLearningKB      = Math.sqrt(stdDevTimeLearningKB / (countLine - 1));
        stdDevAccuracyTra         = Math.sqrt(stdDevAccuracyTra / (countLine - 1));
        stdDevComplexity          = Math.sqrt(stdDevComplexity / (countLine - 1));
        stdDevAccuracyTest        = Math.sqrt(stdDevAccuracyTest / (countLine - 1));
        stdDevTimeTotal           = Math.sqrt(stdDevTimeTotal / (countLine - 1));
        
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
        
        fileName =  "./result/resultAllDataSets.txt";            
            
        try (FileWriter fw = new FileWriter(fileName,true)) //the true will append the new data
        {           
            fw.write(datasetName + " " + 
               Double.toString(timeSelectInstances) + " " + Double.toString(stdDevTimeSelectInstances) + " " +
               Double.toString(timeLearningKB) + " " + Double.toString(stdDevTimeLearningKB) + " " +
               Double.toString(timeTotal) + " " + Double.toString(stdDevTimeTotal) + " " +  
               Double.toString(reductionRate) + " " + Double.toString(stdDevReductionRate) + " " +              
               Double.toString(accuracyTra) + " " + Double.toString(stdDevAccuracyTra) + " " + 
               Double.toString(complexity) + " " + Double.toString(stdDevComplexity) + " " +               
               Double.toString(accuracyTest) + " " + Double.toString(stdDevAccuracyTest) + "\n");
               
            fw.close();
        }
        catch (IOException e) {
           System.err.println("ExpNSGAIISelectInstancesAndLearningKB class >  printFinalResult method error: " + e);
           System.exit(-1); 
        }       
    } //end printFinalResult method
    
}
