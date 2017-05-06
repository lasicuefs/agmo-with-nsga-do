package mgpires.operators.mutation;

import jmetal.operators.mutation.*;
import jmetal.core.Solution;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XInt;
import mgpires.solutionType.ArrayIntAndRealSolutionType;

/**
 * This class implements a mutation operator based on Antonelli's paper.
 * Antonelli et al (2012) Genetic training instance selection in MOEFS A coevolutionary approach
 * NOTE: the operator is applied to integer array from solutions
 * The solutions are ArrayIntAndRealSolutionType type.
 * Created by Matheus G. Pires - 2015/07/07 - mgpires@ecomp.uefs.br
 */
public class MutationAddRules extends Mutation {
    /**
     * Valid solution types to apply this operator 
     */
    private static final List VALID_TYPES = Arrays.asList(ArrayIntAndRealSolutionType.class);    

    private Double mutationProbability_ = null ;
    
    // flag_ indicates if the crossover operator was applied
    private boolean flag_;
  
    /**
     * Constructor
     * Creates a new instance of the MutationAddRules mutation operator
     * @param parameters
     */
    public MutationAddRules(HashMap<String, Object> parameters) {
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
            New rules can be added. To each rule, one random number of antecedents
            is generated. To each antecedent, one random number is generated,
            which represents the index of the fuzzy set.
            ------------------------------------------------------------------*/
            if (PseudoRandom.randDouble() <= probability) {
                /* o valor de maxRules deve ser definido pelo usuario e nao pegar o numero
                atual de regras do cromossomo!!! Preciso consertar isso!!!                
                */                
                int maxRules = ((ArrayIntAndRealSolutionType)solution.getType()).getMaxNumberOfRules_();
                int rulesToAdd = PseudoRandom.randInt(1, maxRules); 
                int nowRules = (int)solution.getDecisionVariables()[2].getValue();            
                int numberOfVariables = solution.numberOfVariables();            

                // verifying if rulesToAdd over the maxRules limit
                if (rulesToAdd + nowRules > maxRules)
                    rulesToAdd = maxRules - nowRules;

                if (rulesToAdd > 0) {      
                    //System.out.println("Rules to add = " + rulesToAdd);
                    XInt x = new XInt(solution);                
                    int idx, rest;
                    int[] numberOfAntecedent = new int[rulesToAdd];                
                    double[] upperBounds;

                    /* numberOfAntecedent stores the number of antecedents of each
                     rule that will be added to chromosome
                     -1 because must exclude the output variable
                    */
                    //System.out.print("Antecedents to add: ");
                    for (int i = 0; i < rulesToAdd; i++) {                                         
                        numberOfAntecedent[i] = PseudoRandom.randInt(1, numberOfVariables - 1);                              
                        //System.out.print(numberOfAntecedent[i] + " ");
                    }
                    //System.out.print("\n");                    

                    // upperBounds is the array with the upper bounds values of variables
                    upperBounds = ((ArrayIntAndRealSolutionType)solution.getType()).getArrayIntUpperBounds_(numberOfVariables);

                    /*
                    for (int i = 0; i < upperBounds.length; i++)
                        System.out.print(upperBounds[i] + " ");
                    System.out.print("\n");                
                    */
                    
                    // adding rule i
                    // idx is the index of chromosome that indicates the first bit to add the new rules
                    idx = nowRules * numberOfVariables;  
                    for (int i = 0; i < rulesToAdd; i++) {
                        // adding the jth antecedent of the rule i
                        rest = (numberOfVariables - 1) - numberOfAntecedent[i];

                        for (int j = 0; j < numberOfAntecedent[i]; j++) {                            
                            /* PseudoRandom.randInt(1, (int)upperBounds[j]) is the index of fuzzy set. 
                            In this case, the don't care condition is not allowed because the numberOfAntecedent
                            can be smaller than (numberOfVariables - 1) and the rest variable
                            will be the new don't care conditions                        
                            */                        
                            x.setValue(idx, PseudoRandom.randInt(1, (int)upperBounds[j]));
                            idx++;
                        }         
                        // filling the rest of antecedents of the rule i with don't care conditions
                        if (rest > 0) {
                            for (int k = 0; k < rest; k++) {
                                x.setValue(idx, 0);
                                idx++;
                            }
                        }
                        // adding the output
                        x.setValue(idx, PseudoRandom.randInt(1, (int)upperBounds[numberOfVariables - 1]));
                        idx++;                   
                    }         
                    // updating the chromosomes' number of rules
                    solution.getDecisionVariables()[2].setValue(nowRules + rulesToAdd);                    
                    
                    // flag_ == true means that mutation was applied
                    flag_ = true;
                } // end if               
            } // end if
        } 
        catch (ClassCastException e1) {           
            Configuration.logger_.log(Level.SEVERE,"MutationAddRules.doMutation: " + 
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
            
            Configuration.logger_.log(Level.SEVERE,"MutationAddRules.execute: the solution " + 
                    "is not of the right type. The type should be ''ArrayIntAndRealSolutionType'', " + 
                    "but {0} is obtained", solution.getType());

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
	}        
        
	doMutation(mutationProbability_, solution);        
        return solution;
    } // execute
    
} // MutationAddRules
