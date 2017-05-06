package mgpires.solutionType;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.ArrayReal;
import jmetal.encodings.variable.Int;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

 /**  
 * Class representing the solution type of solutions composed of an array of 
 * integer values, and other array of real values
 * The integer and real array have their own bounds.
 * Created by Matheus G. Pires - 2015/06/17 - mgpires@ecomp.uefs.br
 */
public class ArrayIntAndRealSolutionType extends SolutionType {

    private final int realLength_ ;
    private final int maxLengthOfArrayInt_;
    private final int minNumberOfRules_;
    private final int maxNumberOfRules_;
    private final int numberOfVariables_;
    private final double[] arrayIntLowerBounds_;
    private final double[] arrayIntUpperBounds_;
    
    /**
     * Constructor
     * @param problem Problem being solved
     * @param realLength Length of the real array
     * @param minNumberOfRules Minimum number of rules
     * @param maxNumberOfRules Maximum number of rules
     * @param numberOfVariables Number of variables
     * @param maxLength The highest length value of the integer array
     * @param lowerBounds Vector with the lower bounds values of the integer array
     * @param upperBounds Vector with the upper bound values of the integer array
     */
    public ArrayIntAndRealSolutionType(Problem problem, int realLength,
        int maxLength, int minNumberOfRules, int maxNumberOfRules, 
        int numberOfVariables, double[] lowerBounds, double[] upperBounds) {
        
        super(problem) ;                
        realLength_         = realLength;
        maxLengthOfArrayInt_= maxLength;
        minNumberOfRules_   = minNumberOfRules;
        maxNumberOfRules_   = maxNumberOfRules;
        numberOfVariables_  = numberOfVariables;
        arrayIntLowerBounds_= lowerBounds;
        arrayIntUpperBounds_= upperBounds;        
    } // Constructor

    /**
     * Creates the variables of the solution
     * @return One object Variable[] with two arrays: one integer array and one
     * real array
     * @throws ClassNotFoundException
     */
    @Override
    public Variable[] createVariables() throws ClassNotFoundException {
        Variable[] variables = new Variable[3];      
        
        // variables[0] encodes the rules
        variables[0] = new ArrayInt(maxLengthOfArrayInt_, arrayIntLowerBounds_, arrayIntUpperBounds_);
        
        // variables[1] encodes the displacement parameters of membership functions
        variables[1] = new ArrayReal(realLength_, problem_);
                
        /* I did this because each chromosome can have one different number of
        rules. So, when the variables are created for each chromosome, one new
        intLength_ value will be generated  
        intLength_ contain the number of genes necessary to codify the rules
        intLength_ is the number of variables * the random number rules      */        
        int numberOfRules = PseudoRandom.randInt(minNumberOfRules_, maxNumberOfRules_);
        
        // variables[2] stores the number of rules of the chromosome
        variables[2] = new Int(numberOfRules, minNumberOfRules_, maxNumberOfRules_);      
                
        int intLength = numberOfVariables_ * numberOfRules;        
        
        //System.out.println("ArrayIntAndRealSolutionType > createVariables. Number of rules: " + numberOfRules + " Int part length: " + intLength);
        try {
            clearRules(variables, maxLengthOfArrayInt_, intLength);
        } catch (JMException ex) {            
            System.err.println(ex + ArrayIntAndRealSolutionType.class.getName() + " Exception in createVaribles()\n" );
            System.exit(-1);
        }
        return variables ;
    } // createVariables

    /* This method fill with -1 the ArrayInt from var (Variable[])
       -1 means that has not variables codified   
    */
    private void clearRules(Variable[] var, int maxLengthOfArrayInt, int intLength) throws JMException {
        ArrayInt aux;
        aux = (ArrayInt)var[0];
        for (int i = intLength; i < maxLengthOfArrayInt; i++)
            aux.setValue(i,-1);        
    }     

    public int getRealLength_() {
        return realLength_;
    }

    public int getMaxLengthOfArrayInt_() {
        return maxLengthOfArrayInt_;
    }

    public int getMinNumberOfRules_() {
        return minNumberOfRules_;
    }

    public int getMaxNumberOfRules_() {
        return maxNumberOfRules_;
    }

    public int getNumberOfVariables_() {
        return numberOfVariables_;
    }  

    public double[] getArrayIntLowerBounds_() {
        return arrayIntLowerBounds_;
    }
    
    /** 
     * @param numberOfVariables Number of variables of the dataset (input + output)
     * @return One double array with the lower bounds values of ArrayInt of 
     * ArrayIntAndRealSolutionType type
     */
    public double[] getArrayIntLowerBounds_(int numberOfVariables) {
        double[] aux = new double[numberOfVariables];
        
        System.arraycopy(arrayIntLowerBounds_, 0, aux, 0, numberOfVariables);
        return aux;
    }

    public double[] getArrayIntUpperBounds_() {
        return arrayIntUpperBounds_;
    }
    
    /** 
     * @param numberOfVariables Number of variables of the dataset (input + output)
     * @return One double array with the upper bounds values of ArrayInt of 
     * ArrayIntAndRealSolutionType type
     */
    public double[] getArrayIntUpperBounds_(int numberOfVariables) {
        double[] aux = new double[numberOfVariables];
        
        System.arraycopy(arrayIntUpperBounds_, 0, aux, 0, numberOfVariables);
        return aux;
    }
    
    
} // end of ArrayIntAndRealSolutionType class