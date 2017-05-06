
package mgpires.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Matheus Giovanni Pires
 * @email  mgpires@ecomp.uefs.br
 * @date   2014/09/19
 * @modified 2015/05/21
 */
public class Samples {    
    // traSamples_ stores the training data
    // testSamples_ stores the test data    
    private String[][] traSamples_, testSamples_;    
    private int numberOfVariables_, numberOfTestSamples_, numberOfTraSamples_;
    // numberOfSamples_ not should be used, because it is only one auxiliar
    // variable used in loadSamples method
    private int numberOfSamples_;
    
    // this vector indicates which samples are or not selected. true is equal selected,
    // and false is equal not selected
    private boolean[] selectedSamples_;
    
    private int numberOfSelectedSamples_;
    
    // typeDataSet_ is "classification" or "regression"
    private String typeDataSet_;
    
    // typeProcedure_ is "training" or "test"
    private String typeProcedure_;
    
    
    public Samples() {
        traSamples_              = null;
        testSamples_             = null;        
        selectedSamples_         = null;        
        numberOfTestSamples_     = 0;
        numberOfTraSamples_      = 0; 
        numberOfVariables_       = 0;       
        numberOfSamples_         = 0;       
        typeDataSet_             = null;
        typeProcedure_           = null;
        numberOfSelectedSamples_ = 0;
    }        
    /**
     * Returns one attribute from the database 
     * @param line is the line of the database
     * @param column is the column of the database
     * @return one attribute from the database   
     */
    public String getValueOfTestSamples (int line, int column) {
        return testSamples_[line][column];
    }
    
    public String getValueOfTraSamples (int line, int column) {
        return traSamples_[line][column];
    }
    
    public String[][] getTestSamples() {
        return testSamples_;
    }

    public String[][] getTraSamples() {
        return traSamples_;
    } 

    public int getNumberOfVariables() {
        return numberOfVariables_;
    }

    /**
     * @return The number of test samples
     */
    public int getNumberOfTestSamples() {
        return numberOfTestSamples_;
    }    

    /**
     * @return The number of training samples
     */
    public int getNumberOfTraSamples() {
        return numberOfTraSamples_;
    } 

    /**
     * @return The type of dataset. It can be "classification" problem or 
     * "regression" problem
     */
    public String getTypeDataSet() {
        return typeDataSet_;
    }

    /**
     * Set the type of dataset. It is "classification" problem or "regression"
     * problem
     * @param typeDataSet "classification" or "regression"
     */
    public void setTypeDataSet(String typeDataSet) {
        if (typeDataSet.equalsIgnoreCase("classification") || typeDataSet.equalsIgnoreCase("regression"))
            this.typeDataSet_ = typeDataSet;
        else {
            System.err.print("Samples class > setTypeDataSet_ method error: typeDataSet parameter: " + 
                typeDataSet + " invalid.");
            System.exit(-1);
        }
    }
    
    /**
     * @return The type of procedure. It can be "training" or "test"
     */
    public String getTypeProcedure() {
        return typeProcedure_;
    }
    
    /**
     * Set the type of current procedure. It is "training", "trainingKB" or "test"
     * "trainingKB" is used when only learningKB is executed
     * @param typeProcedure 
     */
    public void setTypeProcedure(String typeProcedure) {
        if (typeProcedure.equalsIgnoreCase("training") || typeProcedure.equalsIgnoreCase("test") ||
            typeProcedure.equalsIgnoreCase("trainingKB") || typeProcedure.equalsIgnoreCase("none"))
            this.typeProcedure_ = typeProcedure;
        else {
            System.err.print("Samples class > setTypeProcedure method error: typeProcedure parameter: " + 
                typeProcedure + " invalid.");
            System.exit(-1);            
        }
    }
       
    /**
     * This method set the index "idx" of boolean vector selectedSamples_ with
     * boolean value
     * @param idx Index of boolean vector selectedSamples_
     * @param value Boolean value
     */
    public void setSelectedSamples(int idx, boolean value) {
        selectedSamples_[idx] = value;
    }

    /**
     * This method returns a boolean vector that indicates which samples were or not selected. 
     * True is equal selected and false is equal not selected
     * @return The selectedSamples_ vector
     */
    public boolean[] getSelectedSamples() {
        return selectedSamples_;
    }    

    /**
     * @return The number of selected samples
     */
    public int getNumberOfSelectedSamples() {
        return numberOfSelectedSamples_;
    }

    /**
     * This method set the number of selected samples
     * @param numberOfSelectedSamples_ Stores the number of selected samples
     */    
    public void setNumberOfSelectedSamples(int numberOfSelectedSamples_) {
        this.numberOfSelectedSamples_ = numberOfSelectedSamples_;
    }
    
    public void printTestSamples () {                
        System.out.println("\nPrinting the test samples...");
        System.out.println("Number fo test samples = " + numberOfTestSamples_ + " Number of variables = " + numberOfVariables_);
        
        for (int i = 0; i < numberOfTestSamples_; i++) {
            for (int j = 0; j < numberOfVariables_; j++)
                System.out.print(testSamples_[i][j] + " ");                
            
            System.out.println(" ");
        }
    }
    
    public void printTraSamples () {               
        System.out.println("\nPrinting the training samples...");
        System.out.println("Number fo training samples = " + numberOfTraSamples_ + " Number of variables = " + numberOfVariables_);
        
        for (int i = 0; i < numberOfTraSamples_; i++) {
            for (int j = 0; j < numberOfVariables_; j++)
                System.out.print(traSamples_[i][j] + " ");                
            
            System.out.println(" ");
        }
    }
    
    public void printSelectedSamples () {               
        System.out.println("\nPrinting the selectedSamples vector. Number of selected samples = " + numberOfSelectedSamples_);
        
        for (int i = 0; i < numberOfTraSamples_; i++) {            
            if (selectedSamples_[i] == true)
                System.out.print("1");                
            else
                System.out.print("0");           
        }
        System.out.println("\n");
    }    
    /**
     * This method loads the test and training matrix
     * @param datasetName Name of dataset
     * @param stratificationDataset Number of folds. This number can be 5 or 10
     * @param index Index of the fold that will be used    
     */   
    public void loadSamples (String datasetName, String stratificationDataset, int index) { 
        List<String[]> listOfSamples;        
        
        // Name of test fold. Example name: adult-5-1tst.dat            
        String name = datasetName + "-" + stratificationDataset + "-" + Integer.toString(index) + "tst.dat";        
        listOfSamples = readDataSet(name);
        numberOfTestSamples_ = numberOfSamples_;
        testSamples_ = new String[numberOfTestSamples_][numberOfVariables_];
        testSamples_ = copySamplesFromList(listOfSamples, numberOfTestSamples_);        
        
        // Name of training fold. Example name: adult-5-1tra.dat            
        name = datasetName + "-" + stratificationDataset + "-" + Integer.toString(index) + "tra.dat";         
        listOfSamples = readDataSet(name);
        numberOfTraSamples_ = numberOfSamples_;
        traSamples_ = new String[numberOfTraSamples_][numberOfVariables_];
        traSamples_ = copySamplesFromList(listOfSamples, numberOfTraSamples_); 
        
        /* this vector indicates which samples are or not selected. true is 
        equal selected, and false is equal not selected
        */
        selectedSamples_ = new boolean[numberOfTraSamples_];
        // initializing the vector with false
        for (int i = 0; i < numberOfTraSamples_; i++)
            selectedSamples_[i] = false;
        
    } //end loadSamples method

    /**
     * This method reads the dataset
     * @param fileName Name of dataset
     * @return List which contain the data of dataset
     */
    /*
    private List<String[]> readDataSet (String fileName) {       
        try {
            FileReader reader = new FileReader(fileName);            
            BufferedReader leitor = new BufferedReader(reader);  
            StringTokenizer st;   
            String line, data;
            String[] oneSample;
            int idx, count, inputs = 0; // inputs is the counter of the input variables            
            boolean flagInputs = false;        
            List<String[]> listOfSamples;
            listOfSamples = new ArrayList<>();
            line = leitor.readLine();
            
            // this while reads the file's header
            while (line != null) {
                st = new StringTokenizer(line, ",| ");
                
                while (st.hasMoreTokens()) {
                    data = st.nextToken();                                     
                    switch (data) {
                        case "@inputs":
                            flagInputs = true;
                            break;
                        case "@outputs":
                            flagInputs = false;
                            break;
                        case "@output":
                            flagInputs = false;
                            break;
                    }                  
                    // while flagInputs == true, I will count the number of 
                    // input variables
                    if ((flagInputs == true) && (!data.equals("@inputs")))
                        inputs++;                    
                    // when data is equal @data, will start the samples' data
                    if (!st.hasMoreTokens() && data.equals("@data")) 
                        line = null; 
                    else if (!st.hasMoreTokens() && !data.equals("@data")) {                        
                        line = leitor.readLine();
                    }
                }// end while
            }// end while               
            // 1 is the output variable
            // numberOfVariables_ is the number of variables of the database (input + output variables)
            numberOfVariables_ = inputs + 1;
            System.out.println(numberOfVariables_);
            
            // this while reads the all samples and stores in one arrayList "listOfSamples"
            idx = 0; // index of the list
            while ((line = leitor.readLine()) != null) {
                st = new StringTokenizer(line, ",| ");  
                oneSample = new String[numberOfVariables_];
                count = 0;
                while (st.hasMoreTokens()) {
                    data = st.nextToken();
                    oneSample[count] = data;                    
                    count++;
                }                   
                listOfSamples.add(idx, oneSample);                
                idx++;            
            } // end while
            numberOfSamples_ = listOfSamples.size();
            
            leitor.close();  
            reader.close();  
            return listOfSamples;
        } 
        catch (Exception e) {             
           System.out.println("Erro encontrado: "+ e);
           return null;
        }      
    } // end readDataSet method 
    */
    
    private List<String[]> readDataSet (String fileName) {       
        try (FileReader reader = new FileReader(fileName);            
             BufferedReader leitor = new BufferedReader(reader); )  
        {    
            StringTokenizer st;   
            String line, data;
            String[] oneSample;
            int idx, count, vars = 0; // vars is the counter of the input variables            
            List<String[]> listOfSamples;
            listOfSamples = new ArrayList<>();
            line = leitor.readLine();
            
            // this while reads the file's header
            while (line != null) {
                st = new StringTokenizer(line, ",| ");
                
                while (st.hasMoreTokens()) {
                    data = st.nextToken();       
                    //System.out.println(data);
                    if (data.equalsIgnoreCase("@attribute")) {
                        vars++;                    
                    }                    
                    else if (data.equalsIgnoreCase("@data")) {
                        line = null;
                    }

                    if ((st.hasMoreTokens() == false) && (!data.equalsIgnoreCase("@data"))) {
                        line = leitor.readLine();
                    }
                }// end while
            }// end while            
            // numberOfVariables_ is the number of variables of the database (input + output variables)
            numberOfVariables_ = vars;            
            
            // this while reads the all samples and stores in one arrayList "listOfSamples"
            idx = 0; // index of the list
            while ((line = leitor.readLine()) != null) {               
                st = new StringTokenizer(line, ",| ");  
                oneSample = new String[numberOfVariables_];
                count = 0;
                while (st.hasMoreTokens()) {
                    data = st.nextToken();
                    oneSample[count] = data;                    
                    count++;
                }               
                listOfSamples.add(idx, oneSample);                
                idx++;            
            } // end while
            numberOfSamples_ = listOfSamples.size();            
            
            leitor.close();  
            reader.close();  
            return listOfSamples;
        } 
        catch (Exception e) {             
           System.out.println("Erro encontrado: "+ e);
           return null;
        }      
    } // end readDataSet method 
    
    
    /**
     * This method copy samples from list to one matrix      
     * @param list It contain the samples
     * @param nSamples Number of samples of the list
     * @return Matrix String[][] with the samples
     */
    private String[][] copySamplesFromList(List<String[]> list, int nSamples) {
        String[][] samples = new String[nSamples][numberOfVariables_];
        int i = 0, j;       
        
        for (String[] next : list) {
            j = 0;
            for (String next1 : next) {                
                samples[i][j] = next1;                
                j++;
            }
            i++;
        } 
        return samples;
    }//end of copySamplesFromList method    
    
    public void printEuclideanDistance(Map<String,Object> dist) {
        String key;
        double aux;
        System.out.println("\nSize HashMap = " + dist.size());
        for (int i = 0; i < numberOfTraSamples_; i++) {            
            for (int j = i + 1; j < numberOfTraSamples_; j++) {                
                key = Integer.toBinaryString(i) + Integer.toBinaryString(j);                
                aux = (double)dist.get(key);
                //System.out.print(i + " " + j + " " + aux + " ");
                //System.out.print(aux + " ");
                System.out.print(i + " " + j + "     ");
            }
            System.out.print("\n");
        }        
    }    
    /**
     * This method prints the distance matrix
     * @param dist is the matrix which contain the distances among the samples
     * @param flag indicates if the matrix is from training samples or test samples. 
     * Flag can be "test" or "train".
     */
    public void printEuclideanDistanceMatrix(double[][] dist, String flag) {

        System.out.println("\nSize Matrix = " + dist.length);
        
        if (flag.equals("test")) {
            for (int i = 0; i < numberOfTestSamples_; i++) {                
                for (int j = 0; j < numberOfSelectedSamples_; j++) {
                    System.out.print(dist[i][j] + " ");
                }
                System.out.print("\n");
            }            
        }               
        else if (flag.equals("train")) {
            for (int i = 0; i < numberOfTraSamples_; i++) {
                for (int j = 0; j < numberOfTraSamples_; j++) {
                    System.out.print(dist[i][j] + " ");
                }
                System.out.print("\n");
            }            
        } 
        else {
            System.err.print("Samples class > printEuclideanDistanceMatrix method error: flag parameter: " + 
                flag + " invalid.");
            System.exit(-1);            
        }        
    } // end printEuclideanDistanceMatrix method
       
    /**
     * This method calculates the Euclidean distance among training samples
     * @return A HashMap with the distances values. The key is the string index of samples.
     * For example: distance between samples 3 and 7, the key is "37"
     */
    
    /* THIS METHOD IS DEPRECIATED!!!
    
    public Map<String,Object> getEuclideanDistance() {       
        
        Map<String,Object> distHashMap;        
        distHashMap = new HashMap<>();        
        String key;
        double dist;
        
        for (int i = 0; i < numberOfTraSamples_; i++) {            
            for (int j = i + 1; j < numberOfTraSamples_; j++) {                
                key = Integer.toBinaryString(i) + Integer.toBinaryString(j);                
                dist = calculateEuclideanDistance(i,j);                
                distHashMap.put(key, dist);
            }
        }
        return distHashMap;
        
    } // end getEuclideanDistance method
    */   
    
    private double[][] cleanMatrix(int line, int column) {
        
        double[][] distance = new double[line][column];
        
        for (int i = 0; i < line; i++) {
            for (int j = 0; j < column; j++) {
                distance[i][j] = 0;
            }
        }
        return distance;
    }    
    /**
     * This method calculates de Euclidean distance matrix.
     * @param flag if flag is equal "test", the data are loaded from test data.
     * If flag is equal "train", the data are loaded from training data.
     * @return one double[][] Euclidean distance matrix
     * @date 2016/07/29
     */
    public double[][] getEuclideanDistanceMatrix(String flag) {
       
        //double[][] distance = null;        
        
        switch (flag) {
            case "test":
            {
                int column;              
                /* This matrix will contain the distances among test samples with selected training samples.
                So, this matrix is full, complete.
                */               
                //System.out.println(numberOfTestSamples_ + " " + numberOfSelectedSamples_ + " " + numberOfTraSamples_);                
                double[][] distance = cleanMatrix(numberOfTestSamples_, numberOfSelectedSamples_);                
                
                double dist;                
                for (int i = 0; i < numberOfTestSamples_; i++) {
                    column = 0;
                    for (int j = 0; j < numberOfTraSamples_; j++) {
                        
                        if (selectedSamples_[j] == true) {
                            dist = calculateEuclideanDistance(i, j, flag);                            
                            //System.out.println("i = " + i + " column = " + column);                            
                            distance[i][column] = dist;                    
                            column++;
                        }
                    }
                }
                return distance;
            }
            
            /* This case is for when the KNN algorithm is tested        */
            case "knn":
            {                
                /* This matrix will contain the distances among test samples with training samples.
                So, this matrix is full, complete. */               
                //System.out.println(numberOfTestSamples_ + " " + numberOfSelectedSamples_ + " " + numberOfTraSamples_);                
                double[][] distance = cleanMatrix(numberOfTestSamples_, numberOfTraSamples_);                
                
                double dist;                
                for (int i = 0; i < numberOfTestSamples_; i++) {
                    
                    for (int j = 0; j < numberOfTraSamples_; j++) {
                        
                        dist = calculateEuclideanDistance(i, j, "test");                            
                        distance[i][j] = dist;                            
                        
                    }
                }
                return distance;
            }
            
            case "train":
            {
                double dist;                
                double[][] distance = new double[numberOfTraSamples_][numberOfTraSamples_];
                
                for (int i = 0; i < numberOfTraSamples_; i++) {
                    for (int j = i + 1; j < numberOfTraSamples_; j++) {
                        dist = calculateEuclideanDistance(i, j, flag);
                        distance[i][j] = dist;
                    }
                }
                return distance;
            }
            default:
                System.err.print("Samples class > getEuclideanDistanceMatrix method error: "
                   + this.getTypeProcedure() + " invalid.");
                System.exit(-1);
        }
        return null;
    } // end getEuclideanDistance method

    /**
     * This method calculates the Euclidean distance between samples i and j. 
     * The samples are from training samples matrix or test samples matrix
     * @param i Sample in the i-th location in matrix
     * @param j Sample in the j-th location in matrix
     * @return The Euclidean distance
     */
    private double calculateEuclideanDistance(int i, int j, String flag) {
        double aux = 0.0, x, y;
        //System.out.print(i + " " + j + " ");
        
        /* In the "test" case, the distance will be between the test sample 
           with the selected training sample.
        */
        if (flag.equals("test")) {
            for (int var = 0; var < (numberOfVariables_ - 1); var++) {
                x = Double.parseDouble(testSamples_[i][var]);
                y = Double.parseDouble(traSamples_[j][var]);
            
                aux += (x - y) * (x - y);                   
            }
            aux = Math.sqrt(aux);             
        }
        else {
            for (int var = 0; var < (numberOfVariables_ - 1); var++) {
                //System.out.print(traSamples_[i][var] + " " + traSamples_[j][var] + " ");            
                x = Double.parseDouble(traSamples_[i][var]);
                y = Double.parseDouble(traSamples_[j][var]);
            
                aux += (x - y) * (x - y);                   
            }
            aux = Math.sqrt(aux);            
        }			
        //System.out.println(aux);
        return aux;
    }    
} // end class Sample