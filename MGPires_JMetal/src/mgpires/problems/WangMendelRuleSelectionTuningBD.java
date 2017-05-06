package mgpires.problems;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionType;
import mgpires.core.FuzzyReasoning;
import mgpires.core.PartitionFuzzy;
import mgpires.core.RuleBase;
import mgpires.core.Samples;
import jmetal.encodings.solutionType.ArrayRealAndBinarySolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;
import mgpires.solutionType.ArrayBinarySolutionType;


/**
 * @author Matheus Giovanni Pires
 * @email  mgpires@ecomp.uefs.br
 * @data   2014/11/06
 * @comments 
 * This problem consists of the 3 steps:
 * 1) Rules generating by Wang-Mendel algorithm
 * 2) Rules selection and tuning membership function by Multi-Objective Genetic
 * Algorithm
 * 
 * Objectives functions
 * 1) Number of correctly classified training patterns
 * 2) Number of selected fuzzy rules
 * 3) The total number of antecedent conditions
 */
public class WangMendelRuleSelectionTuningBD extends Problem {
    
    private PartitionFuzzy Partition_;
    private Samples Samples_;    
    //private FuzzyReasoning FuzzyReasoning_;
    private RuleBase RuleBase_;
    // realPartLength and binaryPartLength contain the length of each part of
    // the chromossome, real and binary, respectively.
    private int realPartLength_;
    private int binaryPartLength_;
    
    private int numberOfCorrectClassification_;
    private int numberOfSelectedRules_;
    private int numberOfAntecedents_;
    
    private String datasetLocation;

    public int getRealPartLength_() {
        return realPartLength_;
    }

    public void setRealPartLength_(int realPartLength_) {
        this.realPartLength_ = realPartLength_;
    }

    public int getBinaryPartLength_() {
        return binaryPartLength_;
    }

    public void setBinaryPartLength_(int binaryPartLength_) {
        this.binaryPartLength_ = binaryPartLength_;
    }    

    public WangMendelRuleSelectionTuningBD(SolutionType solutionType) {
        super(solutionType);
    }    
    
    /** Constructor
    * @param solutionType is the type of solution (Int, Real, ArrayInt, etc.)
    * @param configFile contain the information about problem's variables. Limits 
    * inferior and superior, number of fuzzy sets for each variable and the type
    * of the output variable (discrete or continuous).
    * @param nameDataSet is the data set name
    * @param nFold is the number of fold of data set
    * @param idxFold is the index of fold that will be used
    */
    //public WangMendelRuleSelectionTuningBD(String solutionType, String configFile, 
           // String trainFile, String testFile ) { 
    
    public WangMendelRuleSelectionTuningBD(String solutionType, String configFile, 
            String nameDataSet, int nFold, int idxFold) { 
        
        problemName_= "WangMendel RuleSelection TuningBD";
        datasetLocation = "./dataset/";
        
        // In my cases, there will not have constraints
        numberOfConstraints_ = 0;
        numberOfObjectives_  = 3;
        
        //FuzzyReasoning_ = new FuzzyReasoning();
        
        Partition_ = new PartitionFuzzy();
        Partition_.createPartition(datasetLocation+configFile);        
        //Partition_.printPartitionFuzzyInput();
        //Partition_.printPartitionFuzzyOutput();      
        
        Samples_ = new Samples();              
        Samples_.loadSamples(datasetLocation+nameDataSet, "5", 1);
        //System.out.println("Number of training samples: " + Samples_.getNumberOfTraSamples());
        //Samples_.printTraSamples();
        //System.out.println("Number of test samples: " + Samples_.getNumberOfTestSamples());
        //Samples_.printTestSamples();        
        
        /* In this case, the numberOfVariables_ are the variables of the database
        (like a sepal length, sepal Width from iris database)
        */
        numberOfVariables_ = Samples_.getNumberOfVariables();

        RuleBase_ = new RuleBase(numberOfVariables_);
        RuleBase_.createRulesWithWangMendel(Partition_.getPartitionFuzzyInput_(), Partition_.getPartitionFuzzyOutput_(), 
                Samples_.getTraSamples(), Samples_.getNumberOfTraSamples(), Partition_.getNumberOfFuzzySets_(), 
                Partition_.getOutput_(), Partition_.getTypeOfOutputVariable_());
        
        //System.out.println("Number of rules.: " + RuleBase_.getNumberOfRules_());
        //RuleBase_.printRuleBase();
        
        if (solutionType.compareTo("ArrayRealAndBinary") == 0) {
            binaryPartLength_ = RuleBase_.getNumberOfRules_();
            realPartLength_ = 0;
            int vet[] = Partition_.getNumberOfFuzzySets_();        
            // vet.length - 1 because I will count only the number of fuzzy set
            // of the input variables
            for (int j = 0; j < vet.length - 1; j++)
                realPartLength_ += vet[j];        

            // For the ArrayRealAndBinarySolutionType, the upperLimit_ and lowerLimit_
            // must be defined only for the real part
            upperLimit_ = new double[realPartLength_];
            lowerLimit_ = new double[realPartLength_];

            for (int var = 0; var < realPartLength_; var++){
                // This parameters must be tuned in according to the database
                lowerLimit_[var] = -0.5;
                upperLimit_[var] = 0.5;
            }            
                       
            //solutionType_ = new ArrayRealAndBinarySolutionTypeByMgpires(this,realPartLength_,binaryPartLength_);
            solutionType_ = new ArrayRealAndBinarySolutionType(this,realPartLength_,binaryPartLength_);
        }
        else if (solutionType.compareTo("BinarySolutionType") == 0) {
            solutionType_ = new BinarySolutionType(this);
        }        
        else if (solutionType.compareTo("ArrayBinarySolutionType") == 0) {            
            // Samples_.getNumberOfTraSamples() will be the length of chromossome.
            // In this case, it is the total number of training samples
            solutionType_ = new ArrayBinarySolutionType(this,Samples_.getNumberOfTestSamples());
        }
        else {
            System.out.println("WangMendelRuleSelectionTuningBD constructor error: solution type " + solutionType + " invalid");
            System.exit(-1);
        }  
        
    } // end of WangMendelRuleSelectionTuningBD constructor    
    
    @Override
    public void evaluate(Solution solution) throws JMException {      
        
        //if (this.solutionType_.getClass() == ArrayRealAndBinarySolutionTypeByMgpires.class) {
        if (this.solutionType_.getClass() == ArrayRealAndBinarySolutionType.class) {
            
            // This code works too, but the code below has only one line.
            //Variable[] variable = solution.getDecisionVariables();            
            //Binary bits = new Binary((Binary) variable[1]);            
            //System.out.println(variable[0].getClass() + " " + variable[1].getClass());
            
            Binary bits = (Binary)solution.getDecisionVariables()[1];           
            
            // Whe I use Xreal, displacement.size gives error!!!
            // One solution is to use the realPartLength_ to control the for
            XReal displacement = new XReal(solution);            
            //for (int i = 0; i < displacement.size(); i++)
            //for (int i = 0; i < realPartLength_; i++)
                //System.out.println(i + "  " + displacement.getValue(i) + " " + displacement.getLowerBound(i) + " " + displacement.getUpperBound(i));
                       
            // Other solution is to use ArrayReal
            // ArrayReal displacement = (ArrayReal)solution.getDecisionVariables()[0];                           
            // Printing the real part of the chromosome            
            //for (int i = 0; i < displacement.getLength(); i++)
                //System.out.println(i + "  " + displacement.getValue(i) + " " + displacement.getLowerBound(i) + " " + displacement.getUpperBound(i));
            
            // Calculating the number of selected fuzzy rules
            // Calculating the total number of antecedent conditions
            numberOfSelectedRules_ = 0;
            numberOfAntecedents_   = 0;           
            //for (int i = 0; i < binaryPartLength_; i++) {
            for (int i = 0; i < bits.getNumberOfBits(); i++) {
                // Printing the binary part of the chromosome
                //System.out.println(i + "  " + bits.getIth(i));            
                if (bits.getIth(i) == true) {                    
                    numberOfSelectedRules_ += 1;                    
                    numberOfAntecedents_ += RuleBase_.getNumberOfAntecedents(i);
                }
            }         
            
            numberOfCorrectClassification_ = FuzzyReasoning.classicFuzzyReasoning(Partition_, Samples_, RuleBase_, displacement, bits);                       
                       
            System.out.println(" ");
            System.out.println(":..Objectives..:");
            System.out.println("Number of selected fuzzy rule.............: " + numberOfSelectedRules_ +
                             "\nNumber of antecedents.....................: " + numberOfAntecedents_ + 
                             "\nNumber of correct classification..........: " + numberOfCorrectClassification_);  
            
            
            double value = (double)numberOfCorrectClassification_*100 / Samples_.getNumberOfTestSamples();
            System.out.println("Percentage of samples classified correctly: " + value);            
            
            solution.setObjective(0, numberOfSelectedRules_);
            solution.setObjective(1, numberOfAntecedents_);
            solution.setObjective(2, numberOfCorrectClassification_);
        }
        else if (this.solutionType_.getClass() == BinarySolutionType.class) {
            System.out.println("Evaluate method was not implemented!!!");
             
        }
        else {
            System.out.println("Evaluate error: solution type " + this.solutionType_.getClass() + " invalid");
            System.exit(-1);
        }
    } // end of evaluate method   

}
