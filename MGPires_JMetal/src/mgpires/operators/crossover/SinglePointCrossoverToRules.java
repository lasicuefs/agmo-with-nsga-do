package mgpires.operators.crossover;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import jmetal.core.Solution;
import jmetal.encodings.variable.ArrayInt;
import jmetal.operators.crossover.Crossover;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import mgpires.solutionType.ArrayIntAndRealSolutionType;

/**
 * This class allows to apply a Single Point crossover operator using two parent
 * solutions. ATTENTION: 
 * This SinglePointCrossoverToRules operator was created for the ArrayIntAndRealSolutionType,
 * more specifically, the SinglePointCrossoverToRules operator will make changes in the
 * ArrayInt part.
 * Created by Matheus G. Pires - 2015/06/30 - mgpires@ecomp.uefs.br
 */
public class SinglePointCrossoverToRules extends Crossover {
    /**
    * Valid solution types to apply this operator 
    */
    private static final List VALID_TYPES = Arrays.asList(ArrayIntAndRealSolutionType.class);

    private Double crossoverProbability_ = null;
    
    // flag_ indicates if the crossover operator was applied
    private boolean flag_;

    /**
    * Constructor
    * Creates a new instance of the SinglePointCrossoverToRules crossover operator
     * @param parameters Stores the current operator parameters. 
    */
    public SinglePointCrossoverToRules(HashMap<String, Object> parameters) {
        super(parameters) ;
  	if (parameters.get("probability") != null)
            crossoverProbability_ = (Double) parameters.get("probability") ;  		
        
        flag_ = false;
    } // SinglePointCrossoverToRules
    
    public double getProbability(){
        return (double)this.crossoverProbability_;
    }

    public boolean isFlag_() {
        return flag_;
    }    

    public void setFlag_(boolean flag_) {
        this.flag_ = flag_;
    }   

    /**
    * Perform the crossover operation.
    * @param probability Crossover probability
    * @param parent1 The first parent
    * @param parent2 The second parent   
    * @return An array containig the two offsprings
    * @throws JMException
    */
    public Solution[] doCrossover(double probability, Solution parent1, Solution parent2) throws JMException {
        Solution[] offSpring = new Solution[2];
        offSpring[0] = new Solution(parent1);
        offSpring[1] = new Solution(parent2);   
        try {
            if (PseudoRandom.randDouble() <= probability) {                
                int length, rulesP1, rulesP2, minRules, pointCrossover, startBit;                         
                                            
                // Gets the number of rules of the parent1 and parent2                                        
                rulesP1 = (int)parent1.getDecisionVariables()[2].getValue();
                rulesP2 = (int)parent2.getDecisionVariables()[2].getValue();

                if (rulesP1 > rulesP2)
                    minRules = rulesP2;
                else
                    minRules = rulesP1;                        
                
                /* minRules == 1 means that an chromosome has only one rule, and 
                in this case, not worth apply the crossover
                */
                if (minRules > 1) {
                    pointCrossover = PseudoRandom.randInt(1, minRules - 1);                

                    startBit = pointCrossover * parent1.numberOfVariables();
                    //System.out.println("\n>> Rules P1: " + rulesP1 + " Rules P2: " + rulesP2 + " Min rules " + minRules + 
                        //" Point crossover: " + pointCrossover + " Start bit: " + startBit + "\n");

                    length = ((ArrayIntAndRealSolutionType)parent1.getType()).getMaxLengthOfArrayInt_();

                    // in this case, getNumberOfDecisionVariables will return the length of array int    
                    int valueX1, valueX2;
                    for (int i = startBit; i < length; i++) {                       
                        valueX1 = ((ArrayInt)parent1.getDecisionVariables()[0]).getValue(i);
                        valueX2 = ((ArrayInt)parent2.getDecisionVariables()[0]).getValue(i);

                        ((ArrayInt)offSpring[0].getDecisionVariables()[0]).setValue(i, valueX2);
                        ((ArrayInt)offSpring[1].getDecisionVariables()[0]).setValue(i, valueX1);
                    }     
                    // updating the chromosomes' number of rules                    
                    offSpring[0].getDecisionVariables()[2].setValue(parent2.getDecisionVariables()[2].getValue());
                    offSpring[1].getDecisionVariables()[2].setValue(parent1.getDecisionVariables()[2].getValue());               

                    flag_ = true;                    
                }
            } // if
        } catch (ClassCastException e1) {
            Configuration.logger_.severe("SinglePointCrossoverToRules.doCrossover: Cannot perfom " +
                "SinglePointCrossoverToRules");
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".doCrossover()");
        }    
        return offSpring;
    } // doCrossover

    /**
    * Executes the operation
    * @param object An object containing an array of two solutions
    * @return An object containing an array with the offSprings
    * @throws JMException
    */
    @Override
    public Object execute(Object object) throws JMException {
        Solution[] parents = (Solution[]) object;

        if (!(VALID_TYPES.contains(parents[0].getType().getClass())  &&
            VALID_TYPES.contains(parents[1].getType().getClass())) ) {

            Configuration.logger_.severe("SinglePointCrossoverToRules.execute: the solutions " +
                "are not of the right type. The type should be 'ArrayIntAndRealSolutionType', but " +
                parents[0].getType() + " and " + parents[1].getType() + " are obtained");

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        } // if

        if (parents.length < 2) {
            Configuration.logger_.severe("SinglePointCrossoverToRules.execute: operator " +
                "needs two parents");
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        } 

        Solution[] offSpring;
        offSpring = doCrossover(crossoverProbability_, parents[0], parents[1]);

        //-> Update the offSpring solutions
        if (offSpring != null)
            for (Solution offSpring1 : offSpring) {
            offSpring1.setCrowdingDistance(0.0);
            offSpring1.setRank(0);
        }
        
        return offSpring;
    } // execute
    
    
} // SinglePointCrossoverToRules
