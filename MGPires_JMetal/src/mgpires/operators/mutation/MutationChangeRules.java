package mgpires.operators.mutation;

import jmetal.operators.mutation.*;
import jmetal.core.Solution;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import jmetal.encodings.variable.ArrayInt;
import jmetal.util.PseudoRandom;
import mgpires.solutionType.ArrayIntAndRealSolutionType;

/**
 * This class implements a mutation operator based on Antonelli's paper.
 * Antonelli et al (2012) Genetic training instance selection in MOEFS A coevolutionary approach
 * NOTE: the operator is applied to integer array from solutions
 * The solutions are ArrayIntAndRealSolutionType type.
 * Created by Matheus G. Pires - 2015/07/10 - mgpires@ecomp.uefs.br
 */
public class MutationChangeRules extends Mutation {
    /**
     * Valid solution types to apply this operator 
     */
    private static final List VALID_TYPES = Arrays.asList(ArrayIntAndRealSolutionType.class);    

    private Double mutationProbability_ = null ;
    
    // numberMaxToMutation_ is the max number of genes that can have mutation. 
    // This number is defined by user
    private Integer numberMaxToMutation_ = null ;
    
    // flag_ indicates if the crossover operator was applied
    private boolean flag_;
  
    /**
     * Constructor
     * Creates a new instance of the MutationChangeRules mutation operator
     * @param parameters
     */
    public MutationChangeRules(HashMap<String, Object> parameters) {
        super(parameters) ;
        if (parameters.get("probability") != null)
            mutationProbability_ = (Double) parameters.get("probability") ;  		
        
        if (parameters.get("numberMaxToMutation") != null)
            numberMaxToMutation_ = (Integer) parameters.get("numberMaxToMutation") ; 
        
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
            This mutation change randomly some genes of the chromosome (solution),
            in other words, change some rules.            
            ------------------------------------------------------------------*/
            if (PseudoRandom.randDouble() <= probability) {
                int numberOfRules = (int)solution.getDecisionVariables()[2].getValue();
                int numberOfVariables = solution.numberOfVariables();
                // length is the actual length of the chromosome's part integer
                int length = numberOfVariables * numberOfRules;
                int genesToMutation = PseudoRandom.randInt(1, numberMaxToMutation_); 
                int idxGene, value, j, aux;
                
                // upperBounds is the array with the upper bounds values of variables
                double[] upperBounds = ((ArrayIntAndRealSolutionType)solution.getType()).getArrayIntUpperBounds_(numberOfVariables);
                
                //System.out.println("Genes to mutation = " + genesToMutation + " Length = " + length);
                for (int i = 0; i < genesToMutation; i++) {                    
                    // gene is the index of chromosome that will be mutated
                    idxGene = PseudoRandom.randInt(0, length - 1);                    
                    /* in this while, I'm verifying if idxGene is an output variable or not.
                    The j value is being added "numberOfVariables" because j always 
                    will be an output variable's index in chromosome */
                    j = -1;
                    aux = -1;
                    while (j <= idxGene) {
                        if (j == idxGene) {
                            aux = j;
                            j = length; 
                        }
                        else {
                            // j always will be the index of output variable
                            j = j + numberOfVariables;
                            aux = j;
                        }
                    }                    
                    // idxGene == aux means that gene is an output variable                    
                    if (idxGene == aux) {
                        value = PseudoRandom.randInt(1, (int)upperBounds[numberOfVariables - 1]);
                        aux = ((ArrayInt)solution.getDecisionVariables()[0]).getValue(idxGene);
                        
                        if (value != aux)                        
                            // updating the "gene" of chromosome with "value"
                            ((ArrayInt)solution.getDecisionVariables()[0]).setValue(idxGene, value);
                        else {
                            /* if the random number "value" is equal gene in "idxGene" position,
                            must generate other random number until they are differents
                            */                            
                            while (value == aux) {
                                value = PseudoRandom.randInt(1, (int)upperBounds[numberOfVariables - 1]);
                            }
                            // updating the "gene" of chromosome with "value"
                            ((ArrayInt)solution.getDecisionVariables()[0]).setValue(idxGene, value);
                        }
                    }                    
                    else {
                        /* j is the index of upperBounds array and, in this
                        case, j is the index of an input variable. So, I can
                        generate 0 (don't care condition) in the rule.
                        */                                                          
                        j = (numberOfVariables - 1) - (aux - idxGene);                                                
                        value = PseudoRandom.randInt(0,(int)upperBounds[j]); 
                        aux = ((ArrayInt)solution.getDecisionVariables()[0]).getValue(idxGene);
                        
                        if (value != aux) {
                            // updating the "gene" of chromosome with "value"
                            ((ArrayInt)solution.getDecisionVariables()[0]).setValue(idxGene, value);    
                        }
                        else {
                            /* if the random number "value" is equal gene in "idxGene" position,
                            must generate other random number until they are differents
                            */                            
                            while (value == aux) {
                                value = PseudoRandom.randInt(1, (int)upperBounds[j]);
                            }
                            // updating the "gene" of chromosome with "value"
                            ((ArrayInt)solution.getDecisionVariables()[0]).setValue(idxGene, value);
                        }
                    }                    
                    //System.out.println("Index gene = " + idxGene + " aux = " + aux + " j = " + j + 
                        //" New value = " + value);
                } // end for
                // flag_ == true means that mutation was applied
                flag_ = true;                
            } // end if
        } 
        catch (ClassCastException e1) {           
            Configuration.logger_.log(Level.SEVERE,"MutationChangeRules.doMutation: " + 
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
            
            Configuration.logger_.log(Level.SEVERE,"MutationChangeRules.execute: the solution " + 
                    "is not of the right type. The type should be ''ArrayIntAndRealSolutionType'', " + 
                    "but {0} is obtained", solution.getType());

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
	}        
        
	doMutation(mutationProbability_, solution);        
        return solution;
    } // execute
    
} // MutationChangeRules
