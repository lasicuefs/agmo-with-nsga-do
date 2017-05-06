package mgpires.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Matheus Giovanni Pires
 * @email  mgpires@ecomp.uefs.br
 * @data   2014/09/23
 * @comments This class creates the uniform fuzzy partition of the variables.
 * The fuzzy sets considered are only triangular!!!
 */
public class PartitionFuzzy {
    private int maxNumberOfFuzzySetsInputVariables_, numberOfInputVariables_, numberOfClasses_;    
    
    // stores the number of fuzzy set of each variable
    private int[] numberOfFuzzySets_; 
    
    /* The dimensions of the partitionFuzzyInput_ and partitionFuzzyOutput_ are:
       number of variables, number of Fuzzy Sets, triangular membership function's points
    */
    private double[][][] partitionFuzzyInput_;
    private double[][][] partitionFuzzyOutput_;
    
    private String[] output_;
    private String typeOfOutputVariable_;

    public PartitionFuzzy() {
        maxNumberOfFuzzySetsInputVariables_ = 0;
        numberOfInputVariables_             = 0;
        numberOfClasses_                    = 0;
        numberOfFuzzySets_                  = null;
        partitionFuzzyInput_                = null;
        partitionFuzzyOutput_               = null;
        output_                             = null;
        typeOfOutputVariable_               = null;
    }    
    /**
     * This method get a value from partitionFuzzyInput_ or partitionFuzzyOutput_.      
     * @param variable Is the variable that wants to get the partition value 
     * @param set Is the set of the "variable"
     * @param point Is the point of the "set"
     * @param type Indicates if is an "input" variable or "output" variable
     * @return The partition value from a variable
     */
    public double getValuePartition(int variable, int set, int point, String type) {
        
        if (type.equalsIgnoreCase("input"))
            return partitionFuzzyInput_[variable][set][point];
        else
            return partitionFuzzyOutput_[0][set][point];                
    }    
    /**
     * This method return a double array with core values of membership functions.
     * The core values are of idxFuzzySet, idxFuzzySet - 1 (prior) and 
     * idxFuzzySet + 1 (posterior)
     * @param idxVariable Index of variable
     * @param idxFuzzySet Index of fuzzy set of the variable
     * @param type Indicates if is an "input" variable or "output" variable
     * @return Double array with three core values of membership functions
     */
    public double[] getCoreFunctions (int idxVariable, int idxFuzzySet, String type) {
        double[] cores = new double[3];
        
        if (type.equalsIgnoreCase("input")) {
            cores[0] = partitionFuzzyInput_[idxVariable][idxFuzzySet][1];
            cores[1] = partitionFuzzyInput_[idxVariable][idxFuzzySet - 1][1];
            cores[2] = partitionFuzzyInput_[idxVariable][idxFuzzySet + 1][1];    
        }
        else {
            cores[0] = partitionFuzzyOutput_[0][idxFuzzySet][1];
            cores[1] = partitionFuzzyOutput_[0][idxFuzzySet - 1][1];
            cores[2] = partitionFuzzyOutput_[0][idxFuzzySet + 1][1];
        }                
        return cores;
    }

    public int getMaxNumberOfFuzzySetsInputVariables_() {
        return maxNumberOfFuzzySetsInputVariables_;
    }

    public void setMaxNumberOfFuzzySetsInputVariables_(int maxNumberOfFuzzySetsInputVariables_) {
        this.maxNumberOfFuzzySetsInputVariables_ = maxNumberOfFuzzySetsInputVariables_;
    }

    public int getNumberOfInputVariables_() {
        return numberOfInputVariables_;
    }

    public int getNumberOfClasses_() {
        return numberOfClasses_;
    }
    
    /**
     * Returns the number of fuzzy sets of one variable 
     * @param index is the index of the variable
     * @return The number of fuzzy sets of one variable
     */
    public int getNumberOfFuzzySetsOfIthVariable(int index) { 
        return numberOfFuzzySets_[index];
    }

    /**
     * Return an int array which contain the number of fuzzy sets for each 
     * variable, including of the output variable.
     * @return Integer array numberOfFuzzySets_
     */
    public int[] getNumberOfFuzzySets_() {
        return numberOfFuzzySets_;
    }

    public double[][][] getPartitionFuzzyInput_() {
        return partitionFuzzyInput_;
    }


    public double[][][] getPartitionFuzzyOutput_() {
        return partitionFuzzyOutput_;
    }

    public String[] getOutput_() {
        return output_;
    }

    public void setOutput_(String[] output_) {
        this.output_ = output_;
    }

    /**
     * This method returns the type of output variable of the dataset
     * @return Returns string "discrete" or "continuous"
     */
    public String getTypeOfOutputVariable_() {
        return typeOfOutputVariable_;
    }

    public void setTypeOfOutputVariable_(String typeOfOutputVariable_) {
        this.typeOfOutputVariable_ = typeOfOutputVariable_;
    }    
        
    /** readConfigFile
     * @param configFile contain the information about problem's variables. Limits 
     * inferior and superior, number of fuzzy sets for each variable and the type
     * of the output variable (discrete or continuous).
     * @return List<String[]>
    */
    public List<String[]> readConfigFile(String configFile) {     
        
        try {      
            FileReader reader = new FileReader(configFile);            
            BufferedReader leitor = new BufferedReader(reader);  
            StringTokenizer st;
            int count, idx, maior = 0, numberOfOutput;
            List<String[]> lista = new ArrayList<>();
            String []oneLine;
            String dados, linha;   
            boolean add = true;
            
            idx = 0;
            linha = leitor.readLine();            
            while (linha != null) {
                st = new StringTokenizer(linha, " ");                
                // oneLine will have length 3, because the parameters always will be
                // inferior and superior limits and number of partitions
                oneLine = new String[3];                
                count = 0;
                while (st.hasMoreTokens()) {
                    dados = st.nextToken();                    
                    //System.out.print(dados + " ");   
                    
                    if (dados.equals("@output")) {
                        dados = st.nextToken();
                        if (dados.equals("discrete")) {
                            partitionFuzzyOutput_ = null;
                            typeOfOutputVariable_ = "discrete";
                            add = false;
                            // get the number of class of the output variable
                            dados = st.nextToken();
                            numberOfOutput = Integer.parseInt(dados); 
                            numberOfClasses_ = numberOfOutput;
                            output_ = new String[numberOfOutput];
                            linha = leitor.readLine();
                            st = new StringTokenizer(linha, " ");                           
                            
                            for (int i = 0; i < numberOfOutput; i++) {
                                dados = st.nextToken();
                                output_[i] = dados;
                            }  
                            // after the output variable, the file hasn't nothing more
                            linha = null;                                                               
                        }                        
                        // if is not discrete, is continuous
                        // after "continuous" has only one line of the file
                        else if (dados.equals("continuous")){ 
                            typeOfOutputVariable_ = "continuous";
                            add = false;
                            // 3 because has only: limits inferior, superior and the 
                            // number of fuzzy sets
                            output_ = new String[3];                            
                            linha = leitor.readLine();
                            st = new StringTokenizer(linha, " ");                            
                            
                            //System.out.println(count);
                            while (count <= 2) {
                                dados = st.nextToken();
                                //System.out.print(dados + " ");
                                //oneLine[count] = dados;
                                output_[count] = dados;                                
                                count++;
                            }
                            // after the output variable, the file hasn't nothing more
                            linha = null;
                        }
                    }   
                    /* int this else is read the limits inferior and superior, and
                    the number of fuzzy sets of each variable. Here, only continuos
                    variables were considered!!!
                    */
                    else {                        
                        oneLine[count] = dados;                    

                        // checking which variable has the higher partition
                        if (count == 2) 
                            if (Integer.parseInt(dados) > maior)
                                maior = Integer.parseInt(dados);

                        count++;
                    }
                } //end while
                
                //System.out.println("");
                if (add == true) {                    
                    lista.add(idx, oneLine);
                    idx++;
                    linha = leitor.readLine();
                }
            } //end while
            
            maxNumberOfFuzzySetsInputVariables_ = maior;
            numberOfInputVariables_ = lista.size();
            return lista;
        } catch (IOException | NumberFormatException e) {             
           System.out.println("Erro encontrado: "+ e);
           return null;
        }    
    }

    /** createPartition
    * @param configFile contain the information about problem's variables. Limits 
    * inferior and superior, number of fuzzy sets for each variable and the type
    * of the output variable (discrete or continuous).
    * @comments This method creates one uniform partition of the triangular 
    * membership functions for each variable   
    */
    public void createPartition(String configFile) {
        //List<String[]> lista = new ArrayList<>();
        List<String[]> lista;
        lista = this.readConfigFile(configFile);
        
        /*    
        for (String[] next : lista) {
            for (String next1 : next) {
                System.out.print(next1 + " ");                  
            }
            System.out.println("");
        }
        System.out.println("Maior particao.: " +  maxNumberOfFuzzySetsInputVariables_ + " Numero de variaveis de entrada.: " + numberOfInputVariables_);
        System.out.println("");
        */    
        
        if (lista == null) { 
            System.out.println("Erro ao abrir o arquivo.: " + configFile);       
        }
        else {            
            double limitInferior, limitSuperior, interval, range, aux;
            int numberOfSets;
            
            // The dimensions of the partitionFuzzyInput_ are:
            // number of variables, number of Fuzzy Sets, triangular membership function's points
            partitionFuzzyInput_ = new double[lista.size()][maxNumberOfFuzzySetsInputVariables_][3];
            // 1 is the output variable
            numberOfFuzzySets_ = new int[numberOfInputVariables_ + 1];
            
            /*
            for (String[] next : lista) {
                for (String next1 : next) {
                    System.out.print(next1 + " ");                  
                }
                System.out.println("");
            }
            System.out.println("Maior particao.: " +  maxNumberOfFuzzySetsInputVariables_ + " Numero de variaveis.: " + numberOfInputVariables_);
            System.out.println("");
            */
            
            for (int i = 0; i < numberOfInputVariables_; i++) {
                String[] get = lista.get(i);
                limitInferior = Double.parseDouble(get[0]);
                limitSuperior = Double.parseDouble(get[1]);
                interval = limitSuperior - limitInferior;
                numberOfSets = Integer.parseInt(get[2]);
                numberOfFuzzySets_[i] = numberOfSets;
                
                if ((numberOfSets - 1.0) == 0.0)
                    range = 0.0;
                else
                    // range represents the one triangle's half
                    range = interval / (numberOfSets - 1.0);
                
                //System.out.println(limitInferior + " " + limitSuperior + " " + numberOfSets + " " + interval + " " + range);
                //System.out.println(" ");
                // 3 because triangular membership function has 3 points
                for (int j = 0; j < 3; j++) {
                    // j == 0 is the first point of the triangular membership function
                    if (j == 0) { 
                        aux = limitInferior;                       
                        for (int k = 0; k < numberOfSets; k++) {                            
                            // For this case (first fuzzy set) I can't update
                            // the aux, because the first point of the second
                            // fuzzy set starts at limitInferior too.
                            if (k == 0)
                                partitionFuzzyInput_[i][k][j] = aux;                            
                            // Since the second fuzzy set I can update the aux
                            else {
                                partitionFuzzyInput_[i][k][j] = aux;
                                aux += range;
                            }                                                      
                        }                   
                    }
                    // j == 1 is the second point of the triangular membership function
                    else if (j == 1) { 
                        aux = limitInferior + range;                        
                        for (int k = 0; k < numberOfSets; k++) {
                            // The second point of the first fuzzy set
                            if (k == 0)
                                partitionFuzzyInput_[i][k][j] = limitInferior;
                            // I did this if because of the diference in decimal values,
                            // for example, if limitSuperior is equal 230.0, the
                            // result of aux += range is 229.99999999999997
                            else if (k == numberOfSets - 1)
                                partitionFuzzyInput_[i][k][j] = limitSuperior;
                            else {
                                partitionFuzzyInput_[i][k][j] = aux;
                                aux += range;
                            }                          
                        }                   
                    }
                    // j == 2 is the third point of the triangular membership function
                    else if (j == 2) { 
                        aux = limitInferior + (2 * range);                        
                        for (int k = 0; k < numberOfSets; k++) {
                            if (k == 0)
                                partitionFuzzyInput_[i][k][j] = limitInferior + range;
                            // I did this if because of the diference in decimal values,
                            // for example, if limitSuperior is equal 230.0, the
                            // result of aux += range is 229.99999999999997
                            else if ((k == numberOfSets - 1) || (k == numberOfSets - 2))
                                partitionFuzzyInput_[i][k][j] = limitSuperior;
                            else {
                                partitionFuzzyInput_[i][k][j] = aux;
                                aux += range;
                            }
                        }
                    }
                }
            }
            
            // calculate the uniform partition fuzzy only to output variable
            if (typeOfOutputVariable_.equals("continuous")) {
                limitInferior = Double.parseDouble(output_[0]);
                limitSuperior = Double.parseDouble(output_[1]);
                interval = limitSuperior - limitInferior;
                numberOfSets = Integer.parseInt(output_[2]);
                numberOfFuzzySets_[numberOfInputVariables_] = numberOfSets;
                // range represents the one triangle's half
                range = interval / (numberOfSets - 1.0);
                //System.out.println(limitInferior + " " + limitSuperior + " " + numberOfSets + " " + interval + " " + range);
                //System.out.println(" ");
                partitionFuzzyOutput_ = new double[1][numberOfSets][3];
                // 3 because triangular membership function has 3 points
                for (int j = 0; j < 3; j++) {
                    // j == 0 is the first point of the triangular membership function
                    if (j == 0) { 
                        aux = limitInferior;                       
                        for (int k = 0; k < numberOfSets; k++) {                            
                            // For this case (first fuzzy set) I can't update
                            // the aux, because the first point of the second
                            // fuzzy set starts at limitInferior too.
                            if (k == 0)
                                partitionFuzzyOutput_[0][k][j] = aux;                            
                            // Since the second fuzzy set I can update the aux
                            else {
                                partitionFuzzyOutput_[0][k][j] = aux;
                                aux += range;
                            }                                                      
                        }                   
                    }
                    // j == 1 is the second point of the triangular membership function
                    else if (j == 1) { 
                        aux = limitInferior + range;                        
                        for (int k = 0; k < numberOfSets; k++) {
                            // The second point of the first fuzzy set
                            if (k == 0)
                                partitionFuzzyOutput_[0][k][j] = limitInferior;
                            // I did this if because of the diference in decimal values,
                            // for example, if limitSuperior is equal 230.0, the
                            // result of aux += range is 229.99999999999997
                            else if (k == numberOfSets - 1)
                                partitionFuzzyOutput_[0][k][j] = limitSuperior;
                            else {
                                partitionFuzzyOutput_[0][k][j] = aux;
                                aux += range;
                            }                          
                        }                   
                    }
                    // j == 2 is the third point of the triangular membership function
                    else if (j == 2) { 
                        aux = limitInferior + (2 * range);                        
                        for (int k = 0; k < numberOfSets; k++) {
                            if (k == 0)
                                partitionFuzzyOutput_[0][k][j] = limitInferior + range;
                            // I did this if because of the diference in decimal values,
                            // for example, if limitSuperior is equal 230.0, the
                            // result of aux += range is 229.99999999999997
                            else if (k == numberOfSets - 1)
                                partitionFuzzyOutput_[0][k][j] = limitSuperior;
                            else {
                                partitionFuzzyOutput_[0][k][j] = aux;
                                aux += range;
                            }
                        }
                    }
                }            
            }
            // if output variable is discrete, is not necessary to calculate
            // the partition fuzzy
            else {
                numberOfFuzzySets_[numberOfInputVariables_] = numberOfClasses_;                
            }
        }            
    }
    
    public void printPartitionFuzzyInput() {
        
        System.out.println("");
        System.out.println("Printing the partition fuzzy of the input variables...");
        
        for (int i = 0; i < numberOfInputVariables_; i++) {
            for (int j = 0; j < maxNumberOfFuzzySetsInputVariables_; j++) {
                for (int k = 0; k < 3; k++) {
                    System.out.print(partitionFuzzyInput_[i][j][k] + " ");                        
                }
                System.out.println("");
            }
            System.out.println(" ");
        }
    }
    
    public void printPartitionFuzzyOutput() {
        
        System.out.println("");
        System.out.println("Printing the partition fuzzy of the output variable...");
        
        // if partitionFuzzyOutput_ == null then output variable is discrete
        if (partitionFuzzyOutput_ == null) {           
            for (int i = 0; i < numberOfClasses_; i++) {
                System.out.print(output_[i] + " ");                
            }
            System.out.println("");           
        }
        // otherwise, the output variable is continuous
        else {           
            for (int i = 0; i < 1; i++) {
                for (int j = 0; j < numberOfFuzzySets_[numberOfInputVariables_]; j++) {
                    for (int k = 0; k < 3; k++) {
                        System.out.print(partitionFuzzyOutput_[i][j][k] + " ");                        
                    }
                    System.out.println("");
                }
                System.out.println("");
            }  
        } 
        System.out.println("");
    }
    
} // end class
 