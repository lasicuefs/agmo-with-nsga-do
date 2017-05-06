package mgpires.operators.mutation;

import jmetal.operators.mutation.*;
import jmetal.core.Solution;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import jmetal.encodings.variable.ArrayReal;
import jmetal.util.PseudoRandom;
import mgpires.problems.LearningKB;
import mgpires.solutionType.ArrayIntAndRealSolutionType;

/**
 * This class implements a mutation operator based on Antonelli's paper.
 * Antonelli et al (2012) Genetic training instance selection in MOEFS A coevolutionary approach
 * NOTE: the operator is applied to real array from solutions
 * The solutions are ArrayIntAndRealSolutionType type.
 * Created by Matheus G. Pires - 2015/07/13 - mgpires@ecomp.uefs.br
 */
public class MutationChangeFunctions extends Mutation {
    /**
     * Valid solution types to apply this operator 
     */
    private static final List VALID_TYPES = Arrays.asList(ArrayIntAndRealSolutionType.class);    

    private Double mutationProbability_ = null ;
        
    // flag_ indicates if the crossover operator was applied
    private boolean flag_;
  
    /**
     * Constructor
     * Creates a new instance of the Bit Flip mutation operator
     * @param parameters
     */
    public MutationChangeFunctions(HashMap<String, Object> parameters) {
        super(parameters) ;
        if (parameters.get("probability") != null)
            mutationProbability_ = (Double) parameters.get("probability") ;  		
                
        flag_ = false;
    }

    /**
     * Perform the mutation operation
     * @param probability Mutation probability
     * @param solution The solution to mutate 
     * @throws JMException
     */
    public void doMutation(double probability, Solution solution) throws JMException {            
        try {            
            /* -----------------------------------------------------------------            
            This mutation change the core of membership function of the variables.
            The value is changed to an small range.            
            ------------------------------------------------------------------*/
            if (PseudoRandom.randDouble() <= probability) {
                
                int var, numberOfVariables, fuzzySetToMutate, upper, idx;
                double[] cores; 
                double lowerBound, upperBound, value;
                String type;
                
                numberOfVariables = solution.numberOfVariables();                
                /* if the problem is a classification problem, the output variable
                can not mutated. If it is a regression problem, it can.
                var is the variable that will be mutated. Its value is not
                a index value, it indicates the sequence of variable, i.e, first,
                second, or third variable, and so on.
                */                
                if (((LearningKB)solution.getProblem()).getSamples_().getTypeDataSet().equalsIgnoreCase("classification")) {
                    var = PseudoRandom.randInt(1, numberOfVariables - 1);               
                    type = "input";
                }
                else {
                    var = PseudoRandom.randInt(1, numberOfVariables);
                    type = "output";
                }             
                                                           
                // upper gets the number of fuzzy sets of the variable var
                // as the index of array starts at zero, I need to use (var - 1)                
                upper = ((LearningKB)solution.getProblem()).getPartition_().getNumberOfFuzzySetsOfIthVariable(var - 1);
                
                /* fuzzySetToMutate is the fuzzy set of variable "var" that will be mutated.
                 In according to the limits, the first and the last fuzzy sets are not mutated
                */
                fuzzySetToMutate = PseudoRandom.randInt(2, upper - 1);                                
                /* cores gets the core values of membership functions of (var - 1) variable.
                As the index of array starts at zero, I need to use (var - 1) and (fuzzySetToMutate - 1)
                */                
                cores = ((LearningKB)solution.getProblem()).getPartition_().getCoreFunctions(var - 1, fuzzySetToMutate - 1, type);
                
                lowerBound = cores[0] - ((cores[0] - cores[1]) / 2);
                upperBound = cores[0] + ((cores[2] - cores[0]) / 2);
                
                value = PseudoRandom.randDouble(lowerBound, upperBound);
                
                // vet contain the number of fuzzy sets of each variable
                int vet[] = ((LearningKB)solution.getProblem()).getPartition_().getNumberOfFuzzySets_();
                       
                idx = -1;
                for (int i = 0; i < (var - 1); i++)                    
                    idx = idx + vet[i];
                        
                idx = idx + fuzzySetToMutate;
                
                // mutating the gene idx
                ((ArrayReal)solution.getDecisionVariables()[1]).setValue(idx, value);
                
                /*System.out.println("Var = " + var + " Set to mutate = " + fuzzySetToMutate + 
                    " Idx = " + idx + " Value = " + value + " Core i = " + cores[0] +
                    " Core i-1 = " + cores[1] + " Core i+1 = " + cores[2]);
                */
                
                // flag_ == true means that mutation was applied
                flag_ = true;                
            } // end if
        } 
        catch (ClassCastException e1) {           
            Configuration.logger_.log(Level.SEVERE,"MutationChangeFunctions.doMutation: " + 
                    "ClassCastException error{0}", e1.getMessage());
            
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".doMutation()");
        }    
    } // doMutation

    /**
     * Executes the operation
     * @param object An object containing a solution to mutate
     * @return An object containing the mutated solution
     * @throws JMException 
     */
    @Override
    public Object execute(Object object) throws JMException {
        Solution solution = (Solution) object;

	if (!VALID_TYPES.contains(solution.getType().getClass())) {
            
            Configuration.logger_.log(Level.SEVERE,"MutationChangeFunctions.execute: the solution " + 
                    "is not of the right type. The type should be ''ArrayIntAndRealSolutionType'', " + 
                    "but {0} is obtained", solution.getType());

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
	}        
        
	doMutation(mutationProbability_, solution);        
        return solution;
    } // execute
    
} // MutationChangeFunctions
